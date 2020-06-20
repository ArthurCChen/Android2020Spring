package materiallogin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.thu.qinghuaquan.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

// 注册页面
public class RegisterActivity extends AppCompatActivity {

    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @InjectView(R.id.xs_cv_add)
    CardView cvAdd;

//    @InjectView(R.id.et_username)
//    EditText editText;

    private RadioGroup rg;
    private RadioButton rbSx;
    private RadioButton rbSj;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etSjmx;
    private EditText etSjdh;
    private EditText etFr;
    private EditText etQybh;
    private EditText etEmail;
    private Button btGo;

    // 发送验证邮件按钮事件
    public void validation(View view) {
        final String username = edit_email.getText().toString();
        final String nickname = edit_nickname.getText().toString();
        final String password = this.password.getText().toString();
        final String realname = edit_realname.getText().toString();
        final String telephone = edit_telephone.getText().toString();
        final String address = edit_address.getText().toString();
        final String email = edit_email.getText().toString();
        if (TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(nickname)
                || TextUtils.isEmpty(realname)
                || TextUtils.isEmpty(telephone)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        //为完成邮箱检验，我们使用AVUser类
        final AVUser user = new AVUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.put("nickname",nickname);
        user.put("realname",realname);
        user.put("telephone",telephone);
        user.put("address",address);
        //创建一个为验证的用户
        user.signUpInBackground().subscribe(new Observer<AVUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(AVUser user) {
                // 注册成功
                System.out.println("注册成功。objectId：" + user.getObjectId());
                AVUser.requestEmailVerifyInBackground(email).blockingSubscribe();
                Toast.makeText(RegisterActivity.this, "已发送验证邮件", Toast.LENGTH_SHORT).show();
            }
            public void onError(Throwable e) {
                Toast.makeText(RegisterActivity.this, "该邮件已注册账号", Toast.LENGTH_SHORT).show();
            }
            public void onComplete() {}
        });


    }

    // 确定按钮事件
    public void next(View view) {
//        if (rbSj.isChecked()) {
//            final String username = etUsername.getText().toString();
//            final String password = etPassword.getText().toString();
//            final String sjmx = etSjmx.getText().toString();
//            final String sjdh = etSjdh.getText().toString();
//            final String fr = etFr.getText().toString();
//            final String qybh = etQybh.getText().toString();
//            if (TextUtils.isEmpty(username)
//                    || TextUtils.isEmpty(password)
//                    || TextUtils.isEmpty(sjmx)
//                    || TextUtils.isEmpty(sjdh)
//                    || TextUtils.isEmpty(fr)
//                    || TextUtils.isEmpty(qybh)) {
//                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            BmobQuery<BmobShangjia> query = new BmobQuery<>();
//            query.addWhereEqualTo("username", username)
//                    .findObjects(this, new FindListener<BmobShangjia>() {
//                        @Override
//                        public void onSuccess(List<BmobShangjia> list) {
//                            if (list != null && list.size() > 0) {
//                                Toast.makeText(RegisterActivity.this, "手机号已经存在，请修改手机号", Toast.LENGTH_SHORT).show();
//                            } else {
//                                BmobShangjia shangjia = new BmobShangjia();
//                                shangjia.setUsername(username);
//                                shangjia.setPassword(password);
//                                shangjia.setQybh(qybh);
//                                shangjia.setSjdh(sjdh);
//                                shangjia.setSjfr(fr);
//                                shangjia.setSjmc(sjmx);
//                                shangjia.save(RegisterActivity.this, new SaveListener() {
//                                    @Override
//                                    public void onSuccess() {
//                                        SPutil s = new SPutil(RegisterActivity.this);
//                                        s.WriteName(username);
//                                        s.WriteGender(username);
//                                        startActivity(new Intent(RegisterActivity.this, ShangjiaActivity.class));
//                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//                                        if (MainActivity.mainActivity != null)
//                                            MainActivity.mainActivity.finish();
//                                        finish();
//                                    }
//
//                                    @Override
//                                    public void onFailure(int i, String s) {
//                                        Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onError(int i, String s) {
//                            Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } else { 下方行缩进-1
        final String username = edit_email.getText().toString();
        final String nickname = edit_nickname.getText().toString();
        final String password = this.password.getText().toString();
        final String realname = edit_realname.getText().toString();
        final String telephone = edit_telephone.getText().toString();
        final String address = edit_address.getText().toString();
        final String email = edit_email.getText().toString();
        if (TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(nickname)
                || TextUtils.isEmpty(realname)
                || TextUtils.isEmpty(telephone)
                || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final AVUser user = new AVUser();
        if (!user.getBoolean("emailVerified")) {
            Toast.makeText(this, "请先验证邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
        //这里跳转到内部界面

            //以下是原实现，AVObject类
//            final AVOUser user = new AVOUser(username, password, email, xh, xm, bj, sushe);
//            user.saveInBackground().subscribe(new Observer<AVObject>() {
//                @Override
//                public void onSubscribe(Disposable d) {
//
//                }
//
//                @Override
//                public void onNext(AVObject avObject) {
////                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
//                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
////                    if (MainActivity.mainActivity != null)
////                        MainActivity.mainActivity.finish();
////                    finish();
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onComplete() {
//
//                }
//            });
//        }这里是商家/学生选项else的后括号
    }

    private CardView xsCvAdd;
    private EditText edit_email;
    private EditText edit_nickname;
    private EditText password;
    private EditText edit_realname;
    private EditText edit_telephone;
    private EditText edit_address;
    private Button btGo2;

    private Button btFinish;

    // onCreate生命周期，做初始化的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        rbSx = (RadioButton) findViewById(R.id.rb_sx);
        rbSj = (RadioButton) findViewById(R.id.rb_sj);
//        etUsername = (EditText) findViewById(R.id.et_username);
//        etPassword = (EditText) findViewById(R.id.et_password);
//        etSjmx = (EditText) findViewById(R.id.et_sjmx);
//        etSjdh = (EditText) findViewById(R.id.et_sjdh);
//        etFr = (EditText) findViewById(R.id.et_fr);
//        etQybh = (EditText) findViewById(R.id.et_qybh);
        btGo = (Button) findViewById(R.id.bt_go);

        xsCvAdd = (CardView) findViewById(R.id.xs_cv_add);
        edit_email = (EditText) findViewById(R.id.logon_email);
        edit_nickname = (EditText) findViewById(R.id.logon_nickname);
        password = (EditText) findViewById(R.id.logon_password);
        edit_realname = (EditText) findViewById(R.id.logon_opt_name);
        edit_telephone = (EditText) findViewById(R.id.logon_opt_tel);
        edit_address = (EditText) findViewById(R.id.logon_opt_room);
        btGo2 = (Button) findViewById(R.id.bt_go_2);
        btFinish = (Button) findViewById(R.id.bt_finish);

        rbSj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbSj.isChecked()) {
                    xsCvAdd.setVisibility(View.GONE);
                    cvAdd.setVisibility(View.VISIBLE);
                } else {
                    cvAdd.setVisibility(View.GONE);
                    xsCvAdd.setVisibility(View.VISIBLE);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    // 动画
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

}
