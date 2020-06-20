package materiallogin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.leancloud.AVObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

// 注册页面
public class RegisterActivity extends AppCompatActivity {

    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.cv_add)
    CardView cvAdd;

    @InjectView(R.id.et_username)
    EditText editText;

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

    // 确定按钮事件
    public void next(View view) {
        if (rbSj.isChecked()) {
            final String username = etUsername.getText().toString();
            final String password = etPassword.getText().toString();
            final String sjmx = etSjmx.getText().toString();
            final String sjdh = etSjdh.getText().toString();
            final String fr = etFr.getText().toString();
            final String qybh = etQybh.getText().toString();
            if (TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password)
                    || TextUtils.isEmpty(sjmx)
                    || TextUtils.isEmpty(sjdh)
                    || TextUtils.isEmpty(fr)
                    || TextUtils.isEmpty(qybh)) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            BmobQuery<BmobShangjia> query = new BmobQuery<>();
            query.addWhereEqualTo("username", username)
                    .findObjects(this, new FindListener<BmobShangjia>() {
                        @Override
                        public void onSuccess(List<BmobShangjia> list) {
                            if (list != null && list.size() > 0) {
                                Toast.makeText(RegisterActivity.this, "手机号已经存在，请修改手机号", Toast.LENGTH_SHORT).show();
                            } else {
                                BmobShangjia shangjia = new BmobShangjia();
                                shangjia.setUsername(username);
                                shangjia.setPassword(password);
                                shangjia.setQybh(qybh);
                                shangjia.setSjdh(sjdh);
                                shangjia.setSjfr(fr);
                                shangjia.setSjmc(sjmx);
                                shangjia.save(RegisterActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        SPutil s = new SPutil(RegisterActivity.this);
                                        s.WriteName(username);
                                        s.WriteGender(username);
                                        startActivity(new Intent(RegisterActivity.this, ShangjiaActivity.class));
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        if (MainActivity.mainActivity != null)
                                            MainActivity.mainActivity.finish();
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            final String username = xsEtUsername.getText().toString();
            final String password = xsEtPassword.getText().toString();
            final String xh = xsEtXh.getText().toString();
            final String xm = xsEtXm.getText().toString();
            final String bj = xsEtBj.getText().toString();
            final String sushe = xsEtSushe.getText().toString();
            final String email = etEmail.getText().toString();
            if (TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password)
                    || TextUtils.isEmpty(xh)
                    || TextUtils.isEmpty(xm)
                    || TextUtils.isEmpty(bj)
                    || TextUtils.isEmpty(email)
                    || TextUtils.isEmpty(sushe)) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                return;
            }
            final AVOUser user = new AVOUser(username, password, email, xh, xm, bj, sushe);
            user.saveInBackground().subscribe(new Observer<AVObject>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(AVObject avObject) {
//                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//                    if (MainActivity.mainActivity != null)
//                        MainActivity.mainActivity.finish();
//                    finish();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    private CardView xsCvAdd;
    private EditText xsEtUsername;
    private EditText xsEtPassword;
    private EditText xsEtXh;
    private EditText xsEtXm;
    private EditText xsEtBj;
    private EditText xsEtSushe;
    private Button btGo2;

    // onCreate生命周期，做初始化的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        rg = (RadioGroup) findViewById(R.id.rg);
        rbSx = (RadioButton) findViewById(R.id.rb_sx);
        rbSj = (RadioButton) findViewById(R.id.rb_sj);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etSjmx = (EditText) findViewById(R.id.et_sjmx);
        etSjdh = (EditText) findViewById(R.id.et_sjdh);
        etFr = (EditText) findViewById(R.id.et_fr);
        etQybh = (EditText) findViewById(R.id.et_qybh);
        etEmail = (EditText) findViewById(R.id.et_email);
        btGo = (Button) findViewById(R.id.bt_go);

        xsCvAdd = (CardView) findViewById(R.id.xs_cv_add);
        xsEtUsername = (EditText) findViewById(R.id.xs_et_username);
        xsEtPassword = (EditText) findViewById(R.id.xs_et_password);
        xsEtXh = (EditText) findViewById(R.id.xs_et_xh);
        xsEtXm = (EditText) findViewById(R.id.xs_et_xm);
        xsEtBj = (EditText) findViewById(R.id.xs_et_bj);
        xsEtSushe = (EditText) findViewById(R.id.xs_et_sushe);
        btGo2 = (Button) findViewById(R.id.bt_go_2);

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
