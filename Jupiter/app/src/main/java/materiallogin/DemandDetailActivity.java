package materiallogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.util.ULocale;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thu.qinghuaquan.R;

import java.util.Objects;

public class DemandDetailActivity extends AppCompatActivity {

    @SuppressLint("Assert")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand_state);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.demand_toolbar);
        TextView demand_detail = (TextView) findViewById(R.id.demand_description);
        TextView demand_user = (TextView) findViewById(R.id.user_info);
        TextView demand_hint = (TextView) findViewById(R.id.demand_hint);
        setSupportActionBar(myToolbar);
        assert getSupportActionBar() != null : "Null action bar";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        assert intent != null : "intent is null in demand detail activity";
        String hint = "hhh";
        String role = intent.getStringExtra("role");
        assert role != null : "you forget to pass 'role' to intent";
        if (role.equals("issuer")) {
            switch (Objects.requireNonNull(intent.getStringExtra("demand_state"))) {
                case "inactive":
                    hint = "可以修改/删除提醒：\n暂未有人向您发起接单请求，可以修改或删除！";
                    break;
                case "active":
                    hint = "不能修改/删除提醒：\n已有人向您发起了接单请求，为保障用户权益，不允许修改或删除，谢谢合作！";
                    break;
                case "done":
                    hint = "当前任务已经完成！";
                    break;
                default:
                    assert false : "wrong state";
            }
        }
        else if (role.equals("taker")) {
            switch (Objects.requireNonNull(intent.getStringExtra("demand_state"))) {
                case "active":
                    switch (Objects.requireNonNull(intent.getStringExtra("entoller_state"))) {
                        case "pending_review_for_enroll_demand":
                            hint = "您的申请正在审核中，请稍加等候。";
                            break;
                        case "accepted":
                            hint = "您的申请已经通过，请按时完成~";
                            break;
                        case "rejected":
                            hint = "很遗憾，您的申请没有通过！";
                            break;
                        case "pending_review_for_quit_demand":
                            hint = "您的推出申请正在审核中，请稍加等候。";
                            break;
                        case "quitted":
                            hint = "您已退出该任务";
                            break;
                        default:
                            assert false : "wrong state";
                    }
                    break;
                case "done":
                    hint = "当前任务已经完成！";
                    break;
                default:
                    assert false : "wrong state";
            }
        }
        else {
            // viewer
            hint = "您尚未向对方提交接单申请，请点击下方按钮发出申请或退出。";
        }

        getSupportActionBar().setTitle(intent.getStringExtra("title"));
        String discription = String.format("<br><p>请求内容：%s</p><br><p>奖励：%s元</p><br><p>需求人数：%s</p><br><p>接单人数：%s</p><br><p>截止时间：%s</p>",
                intent.getStringExtra("content"), intent.getFloatExtra("reward", 0),
                intent.getIntExtra("wanted_number", 1),
                intent.getIntExtra("confirmed_number", 0), intent.getStringExtra("end_time"));
        String user = String.format("<br><p>来自%s的请求</p>", intent.getStringExtra("nickname"));
        hint = String.format("<br><p>%s<p>", hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            demand_detail.setText(Html.fromHtml(discription, Html.FROM_HTML_MODE_COMPACT));
            demand_user.setText(Html.fromHtml(user, Html.FROM_HTML_MODE_COMPACT));
            demand_hint.setText(Html.fromHtml(hint, Html.FROM_HTML_MODE_COMPACT));
        } else {
            demand_detail.setText(Html.fromHtml(discription));
            demand_user.setText(Html.fromHtml(user));
            demand_hint.setText(Html.fromHtml(hint));
        }
    }


}
