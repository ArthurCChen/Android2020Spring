package cn.leancloud.chatkit.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mappage.MapActivity;

import cn.leancloud.im.v2.AVIMMessage;
import cn.leancloud.im.v2.messages.AVIMLocationMessage;

import cn.leancloud.chatkit.R;
import cn.leancloud.chatkit.event.LCIMLocationItemClickEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/9/17.
 * 聊天页面中的地理位置 item 对应的 holder
 */
public class LCIMChatItemLocationHolder extends LCIMChatItemHolder {

  protected TextView contentView;

  public LCIMChatItemLocationHolder(Context context, ViewGroup root, boolean isLeft) {
    super(context, root, isLeft);
  }

  @Override
  public void initView() {
    super.initView();
    conventLayout.addView(View.inflate(getContext(), R.layout.lcim_chat_item_location, null));
    contentView = (TextView) itemView.findViewById(R.id.locationView);
    conventLayout.setBackgroundResource(isLeft ? R.drawable.lcim_chat_item_left_bg : R.drawable.lcim_chat_item_right_bg);
    contentView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //TODO play baiduMap
        final AVIMLocationMessage locMsg = (AVIMLocationMessage) message;
        double latitude = locMsg.getLocation().getLatitude();
        double longitude = locMsg.getLocation().getLongitude();
        Intent locateIntent = new Intent(getContext(), MapActivity.class);
        locateIntent.putExtra("latitude", latitude);
        locateIntent.putExtra("longitude", longitude);
        getContext().startActivity(locateIntent);

        System.out.println("clicked map");
        LCIMLocationItemClickEvent event = new LCIMLocationItemClickEvent();
        event.message = message;
        EventBus.getDefault().post(event);
      }
    });
  }

  @Override
  public void bindData(Object o) {
    super.bindData(o);
    AVIMMessage message = (AVIMMessage) o;
    if (message instanceof AVIMLocationMessage) {
      final AVIMLocationMessage locMsg = (AVIMLocationMessage) message;
      contentView.setText(locMsg.getText());
    }
  }
}
