package cn.leancloud.chatkit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.EditText;

import cn.leancloud.AVException;
import cn.leancloud.im.v2.AVIMConversation;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.AVIMMessage;
import cn.leancloud.im.v2.AVIMMessageOption;
import cn.leancloud.im.v2.callback.AVIMConversationCallback;
import cn.leancloud.im.v2.callback.AVIMMessagesQueryCallback;
import cn.leancloud.im.v2.callback.AVIMMessageRecalledCallback;
import cn.leancloud.im.v2.callback.AVIMMessageUpdatedCallback;
import cn.leancloud.im.v2.messages.AVIMAudioMessage;
import cn.leancloud.im.v2.messages.AVIMImageMessage;
import cn.leancloud.im.v2.messages.AVIMLocationMessage;
import cn.leancloud.im.v2.messages.AVIMTextMessage;
import cn.leancloud.im.v2.messages.AVIMRecalledMessage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.leancloud.chatkit.R;
import cn.leancloud.chatkit.adapter.LCIMChatAdapter;
import cn.leancloud.chatkit.event.LCIMConversationReadStatusEvent;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMInputBottomBarEvent;
import cn.leancloud.chatkit.event.LCIMInputBottomBarRecordEvent;
import cn.leancloud.chatkit.event.LCIMInputBottomBarTextEvent;
import cn.leancloud.chatkit.event.LCIMMessageResendEvent;
import cn.leancloud.chatkit.event.LCIMMessageUpdateEvent;
import cn.leancloud.chatkit.event.LCIMMessageUpdatedEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import cn.leancloud.chatkit.utils.LCIMAudioHelper;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.chatkit.utils.LCIMLogUtils;
import cn.leancloud.chatkit.utils.LCIMNotificationUtils;
import cn.leancloud.chatkit.utils.LCIMPathUtils;
import cn.leancloud.chatkit.view.LCIMInputBottomBar;
import cn.leancloud.types.AVGeoPoint;
import de.greenrobot.event.EventBus;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by wli on 15/8/27.
 * 将聊天相关的封装到此 Fragment 里边，只需要通过 setConversation 传入 Conversation 即可
 */
public class LCIMConversationFragment extends Fragment {

  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_IMAGE_PICK = 2;

  protected AVIMConversation imConversation;

  /**
   * recyclerView 对应的 Adapter
   */
  protected LCIMChatAdapter itemAdapter;

  protected RecyclerView recyclerView;
  protected LinearLayoutManager layoutManager;

  /**
   * 负责下拉刷新的 SwipeRefreshLayout
   */
  protected SwipeRefreshLayout refreshLayout;

  /**
   * 底部的输入栏
   */
  protected LCIMInputBottomBar inputBottomBar;

  // 记录拍照等的文件路径
  protected String localCameraPath;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.lcim_conversation_fragment, container, false);

    recyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_rv_chat);
    refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_chat_srl_pullrefresh);
    refreshLayout.setEnabled(false);
    inputBottomBar = (LCIMInputBottomBar) view.findViewById(R.id.fragment_chat_inputbottombar);
    layoutManager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(layoutManager);

    itemAdapter = getAdpter();
    itemAdapter.resetRecycledViewPoolSize(recyclerView);
    recyclerView.setAdapter(itemAdapter);

    EventBus.getDefault().register(this);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        AVIMMessage message = itemAdapter.getFirstMessage();
        if (null == message) {
          refreshLayout.setRefreshing(false);
        } else {
          imConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
              refreshLayout.setRefreshing(false);
              if (filterException(e)) {
                if (null != list && list.size() > 0) {
                  itemAdapter.addMessageList(list);
                  itemAdapter.setDeliveredAndReadMark(imConversation.getLastDeliveredAt(),
                          imConversation.getLastReadAt());
                  itemAdapter.notifyDataSetChanged();
                  layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                }
              }
            }
          });
        }
      }
    });
  }

  protected LCIMChatAdapter getAdpter() {
    return new LCIMChatAdapter();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (null != imConversation) {
      LCIMNotificationUtils.addTag(imConversation.getConversationId());
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    LCIMAudioHelper.getInstance().stopPlayer();
    if (null != imConversation) {
      LCIMNotificationUtils.removeTag(imConversation.getConversationId());
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    EventBus.getDefault().unregister(this);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.conv_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_conv_setting) {
      Intent intent = new Intent(getActivity(), LCIMConversationDetailActivity.class);
      intent.putExtra(LCIMConstants.CONVERSATION_ID, imConversation.getConversationId());
      getActivity().startActivity(intent);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void setConversation(final AVIMConversation conversation) {
    imConversation = conversation;
    refreshLayout.setEnabled(true);
    inputBottomBar.setTag(imConversation.getConversationId());
    fetchMessages();
    imConversation.read();
    LCIMNotificationUtils.addTag(conversation.getConversationId());
    if (!conversation.isTransient()) {
      if (conversation.getMembers().size() == 0) {
        conversation.fetchInfoInBackground(new AVIMConversationCallback() {
          @Override
          public void done(AVIMException e) {
            if (null != e) {
              LCIMLogUtils.logException(e);
              Toast.makeText(getContext(), "encounter network error, please try later.", Toast.LENGTH_SHORT);
            }
            itemAdapter.showUserName(conversation.getMembers().size() > 2);
          }
        });
      } else {
        itemAdapter.showUserName(conversation.getMembers().size() > 2);
      }
    } else {
      itemAdapter.showUserName(true);
    }
  }

  /**
   * 拉取消息，必须加入 conversation 后才能拉取消息
   */
  private void fetchMessages() {
    imConversation.queryMessages(new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> messageList, AVIMException e) {
        if (filterException(e)) {
          itemAdapter.setMessageList(messageList);
          recyclerView.setAdapter(itemAdapter);
          itemAdapter.setDeliveredAndReadMark(imConversation.getLastDeliveredAt(),
                  imConversation.getLastReadAt());
          itemAdapter.notifyDataSetChanged();
          scrollToBottom();
          clearUnreadConut();
        }
      }
    });
  }

  /**
   * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
   * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
   */
  public void onEvent(LCIMInputBottomBarTextEvent textEvent) {
    if (null != imConversation && null != textEvent) {
      if (!TextUtils.isEmpty(textEvent.sendContent) && imConversation.getConversationId().equals(textEvent.tag)) {
        sendText(textEvent.sendContent);
      }
    }
  }

  /**
   * 处理推送过来的消息
   * 同理，避免无效消息，此处加了 conversation id 判断
   */
  public void onEvent(LCIMIMTypeMessageEvent messageEvent) {
    if (null != imConversation && null != messageEvent &&
            imConversation.getConversationId().equals(messageEvent.conversation.getConversationId())) {
      System.out.println("currentConv unreadCount=" + imConversation.getUnreadMessagesCount());
      if (imConversation.getUnreadMessagesCount() > 0) {
        paddingNewMessage(imConversation);
      } else {
        itemAdapter.addMessage(messageEvent.message);
        itemAdapter.notifyDataSetChanged();
        scrollToBottom();
      }
    }
  }

  /**
   * 重新发送已经发送失败的消息
   */
  public void onEvent(LCIMMessageResendEvent resendEvent) {
    if (null != imConversation && null != resendEvent &&
            null != resendEvent.message && imConversation.getConversationId().equals(resendEvent.message.getConversationId())) {
      if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == resendEvent.message.getMessageStatus()
              && imConversation.getConversationId().equals(resendEvent.message.getConversationId())) {
        sendMessage(resendEvent.message, false);
      }
    }
  }

  /**
   * 处理输入栏发送过来的事件
   *
   * @param event
   */
  public void onEvent(LCIMInputBottomBarEvent event) {
    if (null != imConversation && null != event && imConversation.getConversationId().equals(event.tag)) {
      switch (event.eventAction) {
        case LCIMInputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION:
          dispatchPickPictureIntent();
          break;
        case LCIMInputBottomBarEvent.INPUTBOTTOMBAR_CAMERA_ACTION:
          dispatchTakePictureIntent();
          break;
        case LCIMInputBottomBarEvent.INPUTBOTTOMBAR_LOCATION_ACTION:
          dispatchLocateIntent();
          break;
        default:
          break;
      }
    }
  }

  /**
   * 处理录音事件
   *
   * @param recordEvent
   */
  public void onEvent(LCIMInputBottomBarRecordEvent recordEvent) {
    if (null != imConversation && null != recordEvent &&
            !TextUtils.isEmpty(recordEvent.audioPath) &&
            imConversation.getConversationId().equals(recordEvent.tag)) {
      if (recordEvent.audioDuration > 0)
        sendAudio(recordEvent.audioPath);
    }
  }

  /**
   * 更新对方已读的位置事件
   * @param readEvent
   */
  public void onEvent(LCIMConversationReadStatusEvent readEvent) {
    if (null != imConversation && null != readEvent &&
            imConversation.getConversationId().equals(readEvent.conversationId)) {
      itemAdapter.setDeliveredAndReadMark(imConversation.getLastDeliveredAt(),
              imConversation.getLastReadAt());
      itemAdapter.notifyDataSetChanged();
    }
  }

  public void onEvent(final LCIMMessageUpdateEvent event) {
    if (null != imConversation && null != event &&
            null != event.message && imConversation.getConversationId().equals(event.message.getConversationId())) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle("操作").setItems(new String[]{"撤回", "修改消息内容"}, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          if (0 == which) {
            recallMessage(event.message);
          } else if (1 == which) {
            showUpdateMessageDialog(event.message);
          }
        }
      });
      builder.create().show();
    }
  }

  public void onEvent(final LCIMMessageUpdatedEvent event) {
    if (null != imConversation && null != event &&
            null != event.message && imConversation.getConversationId().equals(event.message.getConversationId())) {
      itemAdapter.updateMessage(event.message);
    }
  }

  public void onEvent(final LCIMOfflineMessageCountChangeEvent event) {
    if (null == event || null == event.conversation || null == event.conversation) {
      return;
    }
    if (!imConversation.getConversationId().equals(event.conversation.getConversationId())) {
      return;
    }
    if (event.conversation.getUnreadMessagesCount() < 1) {
      return;
    }
    paddingNewMessage(event.conversation);
  }

  private void paddingNewMessage(AVIMConversation currentConversation) {
    if (null == currentConversation || currentConversation.getUnreadMessagesCount() < 1) {
      return;
    }
    final int queryLimit = currentConversation.getUnreadMessagesCount() > 100 ? 100 : currentConversation.getUnreadMessagesCount();
    currentConversation.queryMessages(queryLimit, new AVIMMessagesQueryCallback() {
      @Override
      public void done(List<AVIMMessage> list, AVIMException e) {
        if (null != e) {
          return;
        }
        for (AVIMMessage m : list) {
          itemAdapter.addMessage(m);
        }
        itemAdapter.notifyDataSetChanged();
        clearUnreadConut();
      }
    });
  }

  private void showUpdateMessageDialog(final AVIMMessage message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    final EditText editText = new EditText(getActivity());
    builder.setView(editText);
    builder.setTitle("修改消息内容");
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        String content = editText.getText().toString();
        updateMessage(message, content);
      }
    });
    builder.show();
  }

  private void recallMessage(AVIMMessage message) {
    imConversation.recallMessage(message, new AVIMMessageRecalledCallback() {
      @Override
      public void done(AVIMRecalledMessage recalledMessage, AVException e) {
        if (null == e) {
          itemAdapter.updateMessage(recalledMessage);
        } else {
          Toast.makeText(getActivity(), "撤回失败", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  private void updateMessage(AVIMMessage message, String newContent) {
    AVIMTextMessage textMessage = new AVIMTextMessage();
    textMessage.setText(newContent);
    imConversation.updateMessage(message, textMessage, new AVIMMessageUpdatedCallback() {
      @Override
      public void done(AVIMMessage message, AVException e) {
        if (null == e) {
          itemAdapter.updateMessage(message);
        } else {
          Toast.makeText(getActivity(), "更新失败", Toast.LENGTH_SHORT).show();
        }
      }
    });
  }

  /**
   * 发送 Intent 跳转到系统拍照页面
   */
  private void dispatchTakePictureIntent() {

    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
    {
      Toast.makeText(getContext(), "请打开摄像机和读取权限", Toast.LENGTH_SHORT).show();
      judgePermission();
      return;
    }

    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      localCameraPath = LCIMPathUtils.getPicturePathByCurrentTime(getContext());
      Uri imageUri = Uri.fromFile(new File(localCameraPath));
      takePictureIntent.putExtra("return-data", false);
      takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
    } else {
      localCameraPath = Environment.getExternalStorageDirectory() + "/images/" + System.currentTimeMillis() + ".jpg";
      File photoFile = new File(localCameraPath);

      Uri photoURI = FileProvider.getUriForFile(this.getContext(),
              this.getContext().getPackageName() + ".provider", photoFile);
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
              photoURI);
    }
    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  /**
   * 发送 Intent 跳转到系统图库页面
   */
  private void dispatchPickPictureIntent() {
    if (
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
    {
      Toast.makeText(getContext(), "请打开读取权限", Toast.LENGTH_SHORT).show();
      judgePermission();
      return;
    }

    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, null);
    photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
    startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK);
  }

  private void dispatchLocateIntent() {
    //TODO 地理位置

    String serviceName = Context.LOCATION_SERVICE;
// 调用getSystemService()方法来获取LocationManager对象
    LocationManager locationManager = (LocationManager) getContext().getSystemService(serviceName);
    String provider = LocationManager.GPS_PROVIDER;
// 调用getLastKnownLocation()方法获取当前的位置信息
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
    {
      Toast.makeText(getContext(), "请打开定位权限", Toast.LENGTH_SHORT).show();
      judgePermission();
      return;
    }
    Location location = locationManager.getLastKnownLocation(provider);

    if(location == null){
      Toast.makeText(getContext(), "定位信号弱/请开启定位", Toast.LENGTH_SHORT).show();
      return;
    }
    double lat = location.getLatitude();
//获取经度
    double lng = location.getLongitude();

    AVIMLocationMessage message = new AVIMLocationMessage();
    message.setLocation(new AVGeoPoint(lat,lng));
    message.setText("我的位置");
    sendMessage(message);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    System.out.println("requestCode=" + requestCode + ", resultCode=" + resultCode);
    if (Activity.RESULT_OK == resultCode) {
      switch (requestCode) {
        case REQUEST_IMAGE_CAPTURE:
          System.out.println(localCameraPath);
          sendImage(localCameraPath);
          break;
        case REQUEST_IMAGE_PICK:
          sendImage(getRealPathFromURI(getActivity(), data.getData()));
          break;
        default:
          break;
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  /**
   * 滚动 recyclerView 到底部
   */
  private void scrollToBottom() {
    layoutManager.scrollToPositionWithOffset(itemAdapter.getItemCount() - 1, 0);
  }

  /**
   * 根据 Uri 获取文件所在的位置
   *
   * @param context
   * @param contentUri
   * @return
   */
  private String getRealPathFromURI(Context context, Uri contentUri) {
    if (contentUri.getScheme().equals("file")) {
      return contentUri.getEncodedPath();
    } else {
      Cursor cursor = null;
      try {
        String[] proj = {MediaStore.Images.Media.DATA};
        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor) {
          int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          cursor.moveToFirst();
          return cursor.getString(column_index);
        } else {
          return "";
        }
      } finally {
        if (cursor != null) {
          cursor.close();
        }
      }
    }
  }

  /**
   * 发送文本消息
   *
   * @param content
   */
  protected void sendText(String content) {
    AVIMTextMessage message = new AVIMTextMessage();
    message.setText(content);
    sendMessage(message);
  }

  /**
   * 发送图片消息
   * TODO 上传的图片最好要压缩一下
   *
   * @param imagePath
   */
  protected void sendImage(String imagePath) {
    Luban.with(getContext())
            .load(imagePath)
            .ignoreBy(100)
            .setTargetDir(getContext().getCacheDir().getAbsolutePath())
            .filter(new CompressionPredicate() {
              @Override
              public boolean apply(String path) {
                return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
              }
            })
            .setCompressListener(new OnCompressListener() {
              @Override
              public void onStart() {
                // TODO 压缩开始前调用，可以在方法内启动 loading UI
                Toast.makeText(getContext(), "正在上传图片,请稍候", Toast.LENGTH_SHORT).show();
              }

              @Override
              public void onSuccess(File file) {
                // TODO 压缩成功后调用，返回压缩后的图片文件

                try {
                  //检测是否有写的权限
                  int permission = ActivityCompat.checkSelfPermission(getActivity(),
                          "android.permission.READ_EXTERNAL_STORAGE");
                  String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE"};
                  if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(getActivity(), permissions, 1);
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }

                try {
                  sendMessage(new AVIMImageMessage(file.getPath()));
                } catch (IOException e) {
                  LCIMLogUtils.logException(e);
                }
              }

              @Override
              public void onError(Throwable e) {
                // TODO 当压缩过程出现问题时调用
                Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
              }
            }).launch();

  }

  /**
   * 发送语音消息
   *
   * @param audioPath
   */
  protected void sendAudio(String audioPath) {
    try {
      AVIMAudioMessage audioMessage = new AVIMAudioMessage(audioPath);
      sendMessage(audioMessage);
    } catch (IOException e) {
      LCIMLogUtils.logException(e);
    }
  }

  public void sendMessage(AVIMMessage message) {
    sendMessage(message, true);
  }

  /**
   * 发送消息
   *
   * @param message
   */
  public void sendMessage(AVIMMessage message, boolean addToList) {
    fetchMessages();
    if (addToList) {
      itemAdapter.addMessage(message);
    }
    itemAdapter.notifyDataSetChanged();
    scrollToBottom();

    AVIMMessageOption option = new AVIMMessageOption();
    if (message instanceof AVIMTextMessage) {
      AVIMTextMessage textMessage = (AVIMTextMessage) message;
      if (textMessage.getText().startsWith("tr:")) {
        option.setTransient(true);
      } else {
        option.setReceipt(true);
      }
    } else {
      option.setReceipt(true);
    }
    imConversation.sendMessage(message, option, new AVIMConversationCallback() {
      @Override
      public void done(AVIMException e) {
        fetchMessages();
        itemAdapter.notifyDataSetChanged();
        if (null != e) {
          LCIMLogUtils.logException(e);
        }
      }
    });
    fetchMessages();
  }

  private boolean filterException(Exception e) {
    if (null != e) {
      LCIMLogUtils.logException(e);
      Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    return (null == e);
  }

  private void clearUnreadConut() {
    if (imConversation.getUnreadMessagesCount() > 0) {
      imConversation.read();
    }
  }

  protected void judgePermission() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // 检查该权限是否已经获取
      // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

      String[] CAMERA = {Manifest.permission.CAMERA};
      if (ContextCompat.checkSelfPermission(getContext(), CAMERA[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), CAMERA, 800);
      }

      String[] READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
      if (ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), READ_EXTERNAL_STORAGE, 500);
      }

      String[] WRITE_EXTERNAL_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
      if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), WRITE_EXTERNAL_STORAGE, 600);
      }

      String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
      if (ContextCompat.checkSelfPermission(getContext(), SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), SdCardPermission, 100);
      }

      //手机状态权限
      String[] readPhoneStatePermission = {Manifest.permission.READ_PHONE_STATE};
      if (ContextCompat.checkSelfPermission(getContext(), readPhoneStatePermission[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), readPhoneStatePermission, 200);
      }

      // sd卡权限
      //定位权限
      String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
      if (ContextCompat.checkSelfPermission(getContext(), locationPermission[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), locationPermission, 300);
      }

      String[] ACCESS_COARSE_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION};
      if (ContextCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), ACCESS_COARSE_LOCATION, 400);
      }

      String[] ACCESS_FINE_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};
      if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION[0]) != PackageManager.PERMISSION_GRANTED) {
        // 如果没有授予该权限，就去提示用户请求
        ActivityCompat.requestPermissions(getActivity(), ACCESS_FINE_LOCATION, 700);
      }
    }
    //LocationClient.reStart();
  }
}

