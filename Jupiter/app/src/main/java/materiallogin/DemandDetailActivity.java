package materiallogin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.icu.util.ULocale;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thu.qinghuaquan.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.leancloud.AVInstallation;
import cn.leancloud.AVObject;
import cn.leancloud.AVPush;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.push.PushService;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import materiallogin.ui.accepted.AcceptedFragment;

public class DemandDetailActivity extends AppCompatActivity {

    private TextView demand_detail;
    private TextView demand_user;
    private TextView demand_hint;
    private Button left_button;
    private Button right_button;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private String demandId;
    private  String objectId;

    @SuppressLint("Assert")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand_state);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.demand_toolbar);
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

        String objectId;
        String message = intent.getStringExtra("com.avoscloud.Data");
        if(message != null){
            DetailObject object = JSON.parseObject(message, DetailObject.class);
            objectId = object.objectId;
        }else{
            objectId = intent.getStringExtra("objectId");
        }

//        String role = intent.getStringExtra("role");
//        assert role != null : "you forget to pass 'role' to intent";

        demand_detail = (TextView) findViewById(R.id.demand_description);
        demand_user = (TextView) findViewById(R.id.user_info);
        demand_hint = (TextView) findViewById(R.id.demand_hint);
        left_button = (Button) findViewById(R.id.demand_button_left);
        right_button = (Button) findViewById(R.id.demand_button_right);

        AVObject demand = AVObject.createWithoutData("demand", objectId);
        demand.fetchInBackground().subscribe(new Observer<AVObject>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(AVObject avObject) {
                get_user_and_then_set_content(avObject);
            }

            @Override
            public void onError(Throwable e) {
                fail();
            }

            @Override
            public void onComplete() { }
        });

    }

    private void get_user_and_then_set_content(AVObject demand) {
        AVUser currentUser = AVUser.getCurrentUser();
        String email = (String) currentUser.getServerData().get("email");
        // to accelerate debug process
//        String email = "wuxs16@mails.tsinghua.edu.cn";
//        String email = "lisiyu201695@gmail.com";
        objectId = demand.getString("objectId");
        demandId = String.valueOf(demand.getNumber("demand_id"));
        AVQuery<AVObject> query = new AVQuery<>("demand_relationship");
        query.whereEqualTo("demand", demand);
        query.whereEqualTo("enroller_id", email);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            public void onSubscribe(Disposable disposable) {}
            @Override
            public void onNext(List<AVObject> avObjects) {
                AVObject relationship;
                if (avObjects.size() != 0) {
                    relationship = avObjects.get(0);
                }
                else {
                    relationship = null;
                }
                set_content(demand, relationship);
            }
            public void onError(Throwable throwable) {
                fail();
            }
            public void onComplete() {}
        });
    }

    private void set_content(AVObject demand, AVObject relationship) {
        String hint = "";

        AVUser currentUser = AVUser.getCurrentUser();
        String email = (String) currentUser.getServerData().get("email");
        // to accelerate debug process
//        String email = "wuxs16@mails.tsinghua.edu.cn";
//        String email = "lisiyu201695@gmail.com";
        String role = "viewer";
        if (demand.getString("username").equals(email)) {
            role = "issuer";
        }
        else if (relationship != null) {
            role = "taker";
        }
        String demand_state = demand.getString("demand_state");

        if (role.equals("issuer")) {
            switch (demand_state) {
                case "inactive":
                    hint = "可以修改/删除提醒：\n暂未有人向您发起接单请求，可以修改或删除！";
                    left_button.setText("修改请求");
                    right_button.setText("删除请求");
                    left_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            push(v, demand);
                        }
                    });
                    right_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            demand.delete();
                        }
                    });
                    break;
                case "active":
                    hint = "不能修改/删除提醒：\n已有人向您发起了接单请求，为保障用户权益，不允许修改或删除，谢谢合作！";
                    left_button.setVisibility(View.GONE);
                    right_button.setVisibility(View.GONE);


                    recyclerView = (RecyclerView) findViewById(R.id.application_list);
                    layoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(layoutManager);
                    // query the data
                    AVQuery<AVObject> query = new AVQuery<>("demand_relationship");
                    query.whereEqualTo("demand", demand);
                    query.findInBackground().subscribe(new Observer<List<AVObject>>() {
                        @Override
                        public void onSubscribe(Disposable d) { }
                        @Override
                        public void onNext(List<AVObject> avObjects) {
                            mAdapter = new ApplicationAdapter(avObjects);
                            recyclerView.setAdapter(mAdapter);
                        }
                        @Override
                        public void onError(Throwable e) {

                        }
                        @Override
                        public void onComplete() { }
                    });


                    break;
                case "done":
                    hint = "当前任务已经完成！";
                    left_button.setVisibility(View.GONE);
                    right_button.setVisibility(View.GONE);
                    break;
                default:
                    assert false : "wrong state";
            }
        }
        else if (role.equals("taker")) {
            // only this possibility... see the text set below
            right_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    relationship.deleteInBackground().subscribe();
                    Toast.makeText(DemandDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            switch (demand_state) {
                case "active":
                    switch (relationship.getString("enroller_state")) {
                        case "pending_review_for_enroll_demand":
                            hint = "您的申请正在审核中，请稍加等候。";
                            left_button.setVisibility(View.GONE);
                            right_button.setText("删除请求");
                            break;
                        case "accepted":
                            hint = "您的申请已经通过，请按时完成~";
                            left_button.setVisibility(View.GONE);
                            right_button.setVisibility(View.GONE);
                            break;
                        case "rejected":
                            hint = "很遗憾，您的申请没有通过！";
                            left_button.setVisibility(View.GONE);
                            right_button.setText("删除申请");
                            break;
                        case "pending_review_for_quit_demand":
                            hint = "您的推出申请正在审核中，请稍加等候。";
                            left_button.setVisibility(View.GONE);
                            right_button.setText("撤销申请");
                            break;
                        case "quitted":
                            hint = "您已退出该任务";
                            left_button.setVisibility(View.GONE);
                            right_button.setText("删除任务");
                            break;
                        default:
                            assert false : "wrong state";
                    }
                    break;
                case "done":
                    hint = "当前任务已经完成！";
                    left_button.setVisibility(View.GONE);
                    right_button.setText("删除任务");
                    break;
                default:
                    assert false : "wrong state";
            }
        }
        else {
            // viewer
            hint = "您尚未向对方提交接单申请，请点击下方按钮发出申请。";
            left_button.setVisibility(View.GONE);
            right_button.setText("提出申请");
            right_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AVObject relationship = new AVObject("demand_relationship");
                    relationship.put("demand", demand);
                    relationship.put("demander_id", demand.getString("username"));
                    relationship.put("enroller_id", email);
                    relationship.put("enroller_state", "pending_review_for_enroll_demand");
                    relationship.saveInBackground().subscribe(new Observer<AVObject>() {
                        @Override
                        public void onSubscribe(Disposable d) { }
                        @Override
                        public void onNext(AVObject avObject) {
                            demand.put("demand_state", "active");
                            demand.saveInBackground().subscribe(new Observer<AVObject>() {
                                @Override
                                public void onSubscribe(Disposable d) { }
                                @Override
                                public void onNext(AVObject avObject) {
                                    PushService.subscribe(DemandDetailActivity.this, demandId, DemandDetailActivity.class);
                                    DetailObject detailObject = new DetailObject("新提交", objectId, "0");
                                    String pushStr = JSON.toJSONString(detailObject);
                                    AVQuery pushQuery = AVInstallation.getQuery();
                                    pushQuery.whereEqualTo("channels", pushStr);
                                    AVPush push = new AVPush();
                                    push.setQuery(pushQuery);
                                    push.setMessage(pushStr);
                                    push.setPushToAndroid(true);
                                    push.sendInBackground().subscribe(new Observer<JSONObject>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                        }
                                        @Override
                                        public void onNext(JSONObject jsonObject) {
                                            System.out.println("推送成功" + jsonObject);
                                        }
                                        @Override
                                        public void onError(Throwable e) {
                                            System.out.println("推送失败，错误信息：" + e.getMessage());
                                        }
                                        @Override
                                        public void onComplete() {
                                        }
                                    });
                                    Toast.makeText(DemandDetailActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    set_hint("您的推出申请正在审核中，请稍加等候。");
                                    right_button.setVisibility(View.GONE);
                                }
                                @Override
                                public void onError(Throwable e) { }
                                @Override
                                public void onComplete() { }
                            });
                        }
                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(DemandDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onComplete() { }
                    });
                }
            });
        }

        getSupportActionBar().setTitle(demand.getString("title"));
        String discription = String.format("<br><p>请求内容：%s</p><br><p>奖励：%s元</p><br><p>需求人数：%s</p><br><p>接单人数：%s</p><br><p>截止时间：%s</p>",
                demand.getString("content"), demand.getDouble("reward"),
                demand.getInt("wanted_number"),
                demand.getInt("confirmed_number"), demand.get("end_time").toString());
        String user = String.format("<br><p>来自%s的请求</p>", demand.getString("username"));
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

    private void set_hint(String hint) {
        hint = String.format("<br><p>%s<p>", hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            demand_hint.setText(Html.fromHtml(hint, Html.FROM_HTML_MODE_COMPACT));
        } else {
            demand_hint.setText(Html.fromHtml(hint));
        }
    }

    private void fail() {
        Toast.makeText(this, "网络错误。", Toast.LENGTH_SHORT).show();
    }

    AlertDialog alertDialog;

    public void push(View view, AVObject demand) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DemandDetailActivity.this);
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_input_table, null);
        builder.setView(layout);
        final SPutil s = new SPutil(this);
        final EditText demand_title = (EditText) layout.findViewById(R.id.demand_title);
        final EditText demand_content = (EditText) layout.findViewById(R.id.demand_content);
        final EditText deadline = (EditText) layout.findViewById(R.id.deadline);
        final EditText wanted_number = (EditText) layout.findViewById(R.id.wanted_number);
        final EditText reward = (EditText) layout.findViewById(R.id.reward);
        final Button cancel = (Button) layout.findViewById(R.id.cancel);
        final Button save = (Button) layout.findViewById(R.id.save);
        final Spinner demand_type = (Spinner) layout.findViewById(R.id.service_type);

        Intent intent = getIntent();
        assert intent != null : "intent is null in demand detail activity";
        demand_title.setText(demand.getString("title"));
        demand_content.setText(demand.getString("content"));
        wanted_number.setText(String.format("%s", demand.getInt("wanted_number")));
        reward.setText(String.format("%s", demand.getDouble("reward")));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean has_error = false;
                String error_content = "";

                final String title = demand_title.getText().toString();
                final String content = demand_content.getText().toString();
                final String end_time = deadline.getText().toString();
                final String type = demand_type.getSelectedItem().toString();

                // check title and content
                if (title.equals("") || content.equals("")) {
                    if (demand_title.getText().toString().equals("")) {
                        error_content = "请输入标题。";
                    }
                    else {
                        error_content = "请输入内容。";
                    }
                    has_error = true;
                }
                int participants_number = 0;
                float reward_number = 0;
                int days = 0;
                // check wanted_number
                try {
                    participants_number = Integer.parseInt(wanted_number.getText().toString());
                } catch (Exception e) {
                    error_content = "需求人数至少为一个人。";
                    has_error = true;
                }
                if (participants_number <= 0) {
                    error_content = "需求人数至少为一个人。";
                    has_error = true;
                }
                // check reward
                try {
                    reward_number = Float.parseFloat(reward.getText().toString());
                } catch (Exception e) {
                    error_content = "你需要承诺报酬。";
                    has_error = true;
                }
                // check time
                try {
                    days = Integer.parseInt(end_time);
                } catch (Exception e) {
                    error_content = "请输入一个合理的天数。";
                    has_error = true;
                }
                if (days <= 0) {
                    error_content = "任务至少持续一天。";
                    has_error = true;
                }

                if (has_error) {
                    new AlertDialog.Builder(DemandDetailActivity.this)
                            .setTitle("提示")
                            .setMessage(error_content)
                            .setPositiveButton("确定", null)
                            .show();
                }

                demand.put("content", content);
                demand.put("title", title);
                demand.put("wanted_number", participants_number);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, days);
                demand.put("end_time", c.getTime());
                demand.put("reward", reward_number);
                demand.put("type", type);
                demand.saveInBackground().subscribe();

                Toast.makeText(DemandDetailActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


}
