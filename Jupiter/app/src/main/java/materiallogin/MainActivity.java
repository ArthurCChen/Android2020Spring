package materiallogin;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
//import androidx.appcompat.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.thu.qinghuaquan.R;

import java.util.List;


import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.leancloud.AVObject;
import cn.leancloud.AVUser;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.AVIMMessageManager;
import cn.leancloud.im.v2.callback.AVIMClientCallback;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

// 登陆页面
public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.et_username)
    EditText etUsername;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.bt_go)
    Button btGo;
    @InjectView(R.id.cv)
    CardView cv;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

//    private RadioGroup rg;
//    private RadioButton rbSx;
//    private RadioButton rbSj;

    public static MainActivity mainActivity;

    // onCreate生命周期，做初始化的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.inject(this);
//        rg = (RadioGroup) findViewById(R.id.rg);
//        rbSx = (RadioButton) findViewById(R.id.rb_sx);
//        rbSj = (RadioButton) findViewById(R.id.rb_sj);
        mainActivity = this;
        SPutil sp = new SPutil(this);
        String name = sp.ReadName();
        SPUtils.init(this);
//        if(!TextUtils.isEmpty(name)) {
//            if (name.startsWith("xs-")){
//                startActivity(new Intent(this, UserActivity.class));
//            }else{
//                startActivity(new Intent(this,UserActivity.class));
//            }
//            finish();
//        }
    }

    // 加号和叉号点击事件
    @OnClick({R.id.bt_go, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
                final SPutil sp = new SPutil(MainActivity.this);
                final String un = etUsername.getText().toString();
                final String pw = etPassword.getText().toString();
                if(TextUtils.isEmpty(un)){
                    Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(pw)){
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                AVUser.logIn(un, pw).subscribe(new Observer<AVUser>() {
                    public void onSubscribe(Disposable disposable) {}
                    public void onNext(AVUser user) {
                        // 登录成功
                        Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();

                        AVIMClient me = AVIMClient.getInstance(user);
                        me.open(new AVIMClientCallback() {
                            @Override
                            public void done(AVIMClient client, AVIMException e) {
                                if(e==null){
                                    SPUtils.setMe(me);
                                    AVIMMessageManager.setConversationEventHandler(new MyPush.CustomConversationEventHandler());
                                    AVIMMessageManager.registerDefaultMessageHandler(new MyPush.CustomMessageHandler());
                                }else{
                                    Toast.makeText(MainActivity.this, "推送建立失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //要回到原安排，恢复下面4行，注释再下面12行
//                        Intent intent = new Intent(MainActivity.this, BottomMenu.class);
//                        intent.putExtra("username", un);
//                        startActivity(intent);
//                        finish();


                        //这里填写本人的clientId
                        LCChatKit.getInstance().open("Jerry", new AVIMClientCallback() {
                            @Override
                            public void done(AVIMClient avimClient, AVIMException e) {
                                if (null == e) {
                                    Intent intent = new Intent(MainActivity.this, LCIMConversationActivity.class);
                                    //这里填写对方clientId
                                    intent.putExtra(LCIMConstants.PEER_ID, "Tom");
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    public void onError(Throwable e) {
                        // 登录失败（可能是密码错误）
                        Toast.makeText(MainActivity.this, "账号密码/网络错误", Toast.LENGTH_SHORT).show();
                    }
                    public void onComplete() {}
                });
                //下面是对于AVObject类型的“条件查询方法登录”
//                AVQuery<AVObject> query = new AVQuery<>(AVOUser.tablename);
//                query.whereEqualTo("username", un);
//                query.whereEqualTo("password", pw);
//                query.findInBackground().subscribe(new Observer<List<AVObject>>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(List<AVObject> avObjects) {
//                        if(avObjects!=null && avObjects.size() > 0){
////                            sp.WriteName(un);
////                            SPUtils.init(MainActivity.this);
//                            Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
////                            SPUtils.putString("username", avObjects.get(0).getString("username"));
//                            startActivity(new Intent(MainActivity.this, UserActivity.class));
//                            finish();
//                        }else{
//                            Toast.makeText(MainActivity.this, "账号密码错误", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
        }
    }
}
