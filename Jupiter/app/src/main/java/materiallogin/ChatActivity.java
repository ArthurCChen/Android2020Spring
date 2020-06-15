package materiallogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ust.jupiter.jupiter.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ChatActivity extends AppCompatActivity {

    public static final int INTERVAL = 2500;

    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private String uid;
    private String umMy;
    private String umOth;

    private String nMy;
    private String nOth;

    private List<ChatMsg> msgList = new ArrayList<>();

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initMsgs();
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            postDelayed(mRunnable, INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main3);
        getSupportActionBar().setTitle("聊天");

        nMy = SPUtils.getString("sName");
        nOth = getIntent().getStringExtra("mOth");
        umMy = SPUtils.getString("username");
        umOth = getIntent().getStringExtra("umOth");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    BmobQuery<YCommunicate> communicateBmobQuery = new BmobQuery<>();
                    judge(communicateBmobQuery);
                    communicateBmobQuery.findObjects(ChatActivity.this, new FindListener<YCommunicate>() {
                        @Override
                        public void onSuccess(List<YCommunicate> us) {
                            if (us == null || us.isEmpty()) {
                                YCommunicate news = new YCommunicate();
                                set(news);
                                news.setMsg("");
                                news.setId(System.currentTimeMillis());
                                List<ChatMsg> msg = new ArrayList<>();
                                final ChatMsg newc = new ChatMsg(System.currentTimeMillis() + "", SPUtils.getString("sName"), SPUtils.getString("username"), content);
                                msg.add(newc);
                                news.setMsg(JSON.toJSONString(msg));
                                news.save(ChatActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        msgList.add(newc);
                                        adapter.notifyDataSetChanged();
                                        msgListView.setSelection(msgList.size());
                                        inputText.setText("");
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(ChatActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                if (us != null && us.size() > 0) {
                                    final YCommunicate old = us.get(0);
                                    List<ChatMsg> mmm = old.convertToMsg();
                                    final ChatMsg newc = new ChatMsg(System.currentTimeMillis() + "", SPUtils.getString("sName"), SPUtils.getString("username"), content);
                                    mmm.add(newc);
                                    old.setMsg(JSONArray.toJSONString(mmm));
                                    old.update(ChatActivity.this, new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            msgList.add(newc);
                                            adapter.notifyDataSetChanged();
                                            msgListView.setSelection(msgList.size());
                                            inputText.setText("");
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            Toast.makeText(ChatActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(ChatActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        mHandler.postDelayed(mRunnable, 100L);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void set(YCommunicate communicate) {
        int compare = umMy.compareTo(umOth);
        if (compare <= 0) {
            communicate.setUn1(umMy);
            communicate.setUn2(umOth);
            communicate.setN1(nMy);
            communicate.setN2(nOth);
        } else {
            communicate.setUn1(umOth);
            communicate.setUn2(umMy);
            communicate.setN1(nOth);
            communicate.setN2(nMy);
        }
    }

    public void judge(BmobQuery<YCommunicate> query) {
        int compare = umMy.compareTo(umOth);
        if (compare <= 0) {
            query.addWhereEqualTo("un1", umMy);
            query.addWhereEqualTo("un2", umOth);
        } else {
            query.addWhereEqualTo("un2", umMy);
            query.addWhereEqualTo("un1", umOth);
        }
    }

    private void initMsgs() {
        BmobQuery<YCommunicate> communicateBmobQuery = new BmobQuery<>();
        judge(communicateBmobQuery);
        communicateBmobQuery.findObjects(this, new FindListener<YCommunicate>() {
            @Override
            public void onSuccess(List<YCommunicate> list) {
                if (list != null && !list.isEmpty()) {
                    msgList.clear();
                    msgList.addAll(list.get(0).convertToMsg());
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(ChatActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void launch(Context context, String un, String name) {
        context.startActivity(new Intent(context, ChatActivity.class)
                .putExtra("umOth", un)
                .putExtra("mOth", name));
    }

}