package materiallogin.ui.me;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.jupiter.jupiter.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import materiallogin.AVODemand;
import materiallogin.BottomMenu;
import materiallogin.MainActivity;
import materiallogin.SPutil;
import android.widget.Toast;

public class MeFragment extends Fragment {

    private MeViewModel meViewModel;

    private View mRoot;
    private int color;

    String nickname;
    String email;
    Uri avatarUrl;
    String address;
    String brief;
    String realname;
    String telephone;
    int completeNumber;
    int createNumber;
    int credit;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meViewModel =
                ViewModelProviders.of(this).get(MeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_me, container, false);

//        AVQuery<AVUser> userQuery = AVUser.getQuery();
//        System.out.println( userQuery);
//        userQuery.selectKeys(Arrays.asList("title", "content"));
//        userQuery.getFirstInBackground().subscribe(new Observer<AVUser>() {
//
//            public void onSubscribe(Disposable disposable) {}
//            public void onNext(AVUser user) {
//                String title = todo.getString("title"); // √
//                String content = todo.getString("content"); // √
//                String notes = todo.getString("notes"); // 会报错
//            }
//            public void onError(Throwable throwable) {}
//            public void onComplete() {}
//        });
//        nickname = userQuery.
        AVUser currentUser = AVUser.getCurrentUser();
//        System.out.println( currentUser);
        nickname = (String) currentUser.getServerData().get("nickname");
        email = (String) currentUser.getServerData().get("email");
        avatarUrl = Uri.parse((String) currentUser.getServerData().get("avatarUrl"));
        brief = (String) currentUser.getServerData().get("brief");
        realname = (String) currentUser.getServerData().get("realname");
        address = (String) currentUser.getServerData().get("address");
        telephone = (String) currentUser.getServerData().get("telephone");
        createNumber = (int) currentUser.getServerData().get("createNumber");
        completeNumber = (int) currentUser.getServerData().get("completeNumber");
        credit = (int) currentUser.getServerData().get("credit");

        final TextView textViewNickname = root.findViewById(R.id.text_nickname);
        textViewNickname.setText(nickname);

        final TextView textViewEmail = root.findViewById(R.id.text_email);
        textViewEmail.setText(email);

        final TextView textViewBrief = root.findViewById(R.id.text_brief);
        textViewBrief.setText("个人简介："+brief);

        final TextView textViewRealname = root.findViewById(R.id.text_realname);
        textViewRealname.setText("真实姓名："+realname);

        final TextView textViewAddress = root.findViewById(R.id.text_address);
        textViewAddress.setText("宿舍地址："+address);

        final TextView textViewTelephone = root.findViewById(R.id.text_telephone);
        textViewTelephone.setText("联系电话："+telephone);

        final TextView textViewCreateNum = root.findViewById(R.id.text_issued_num);
        String issued = " 已发布 "+Integer.toString(createNumber)+" 单";
        textViewCreateNum.setText(issued);

        final TextView textViewCompleteNum = root.findViewById(R.id.text_received_num);
        String accepted = " 已完成 "+Integer.toString(completeNumber)+" 单";
        textViewCompleteNum.setText(accepted);

        final TextView textViewCredit = root.findViewById(R.id.text_credit);
        String creditText = "当前积分："+Integer.toString(credit);
        textViewCredit.setText(creditText);

        final ImageView imageViewAvatar = root.findViewById(R.id.img_avatar);
        imageViewAvatar.setImageURI(avatarUrl);
//        final TextView textView = root.findViewById(R.id.text_nickname);
//        meViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    private void init(Bundle savedInstanceState) {

    }

    public void changePassword(View view) {
        AVUser.requestPasswordResetInBackground("tom@leancloud.rocks").blockingSubscribe();
    }

    public void logout(View view) {

//        Toast.makeText(MeFragment.this, "该邮件已注册账号", Toast.LENGTH_SHORT).show();
    }

    AlertDialog alertDialog;
    public void editMe(View view) {
        //下方参考wxs的创建需求代码
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        final LayoutInflater inflater = getLayoutInflater();
//        final View layout = inflater.inflate(R.layout.dialog_input_table, null);
//        builder.setView(layout);
//        final SPutil s = new SPutil(this);
//        final EditText demand_title = (EditText) layout.findViewById(R.id.demand_title);
//        final EditText demand_content = (EditText) layout.findViewById(R.id.demand_content);
//        final EditText deadline = (EditText) layout.findViewById(R.id.deadline);
//        final EditText wanted_number = (EditText) layout.findViewById(R.id.wanted_number);
//        final EditText reward = (EditText) layout.findViewById(R.id.reward);
//        final Button cancel = (Button) layout.findViewById(R.id.cancel);
//        final Button save = (Button) layout.findViewById(R.id.save);
//        final Spinner demand_type = (Spinner) layout.findViewById(R.id.service_type);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (alertDialog != null)
//                    alertDialog.dismiss();
//            }
//        });
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                boolean has_error = false;
//                String error_content = "";
//
//                final String title = demand_title.getText().toString();
//                final String content = demand_content.getText().toString();
//                final String username = "";
//                final String end_time = deadline.getText().toString();
//                final String type = demand_type.getSelectedItem().toString();
//
//                // check title and content
//                if (title.equals("") || content.equals("")) {
//                    if (demand_title.getText().toString().equals("")) {
//                        error_content = "请输入标题。";
//                    }
//                    else {
//                        error_content = "请输入内容。";
//                    }
//                    has_error = true;
//                }
//                int participants_number = 0;
//                float reward_number = 0;
//                int days = 0;
//                // check wanted_number
//                try {
//                    participants_number = Integer.parseInt(wanted_number.getText().toString());
//                } catch (Exception e) {
//                    error_content = "需求人数至少为一个人。";
//                    has_error = true;
//                }
//                if (participants_number <= 0) {
//                    error_content = "需求人数至少为一个人。";
//                    has_error = true;
//                }
//                // check reward
//                try {
//                    reward_number = Float.parseFloat(reward.getText().toString());
//                } catch (Exception e) {
//                    error_content = "你需要承诺报酬。";
//                    has_error = true;
//                }
//                // check time
//                try {
//                    days = Integer.parseInt(end_time);
//                } catch (Exception e) {
//                    error_content = "请输入一个合理的天数。";
//                    has_error = true;
//                }
//                if (days <= 0) {
//                    error_content = "任务至少持续一天。";
//                    has_error = true;
//                }
//
//                if (has_error) {
//                    new AlertDialog.Builder(BottomMenu.this)
//                            .setTitle("提示")
//                            .setMessage(error_content)
//                            .setPositiveButton("确定", null)
//                            .show();
//                }
//
//
//                final AVODemand demand = new AVODemand();
//                demand.setContent(content);
//                demand.setTitle(title);
//                demand.setUsername(username);
//                demand.setDemand_state("inactive");
//                demand.setWanted_number(participants_number);
//                Calendar c = Calendar.getInstance();
//                c.add(Calendar.DATE, days);
//                demand.setEnd_time(c.getTime());
//                demand.setReward(reward_number);
//                demand.setType(type);
//
//                demand.saveInBackground().subscribe(new Observer<AVObject>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(AVObject avObject) {
//                        Toast.makeText(MeFragment.this, "发布成功", Toast.LENGTH_SHORT).show();
//                        if (alertDialog != null)
//                            alertDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        System.out.println(e.getMessage());
//                        Toast.makeText(MeFragment.this, "网络错误", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//            }
//        });
//        alertDialog = builder.create();
//        alertDialog.show();
    }
}
