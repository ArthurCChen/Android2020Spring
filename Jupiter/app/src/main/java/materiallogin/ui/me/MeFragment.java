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

import com.thu.qinghuaquan.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import materiallogin.AVDemand;
import materiallogin.BottomMenu;
import materiallogin.MainActivity;
import materiallogin.RegisterActivity;
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
    private Button btEditMe;
    private Button btChangePassword;
    private Button btLogout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        meViewModel =
                ViewModelProviders.of(this).get(MeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_me, container, false);

        btEditMe = (Button) root.findViewById(R.id.bt_edit_me);
        btChangePassword = (Button) root.findViewById(R.id.bt_change_password);
        btLogout = (Button) root.findViewById(R.id.bt_logout);

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
        textViewBrief.setText("个人简介：" + brief);

        final TextView textViewRealname = root.findViewById(R.id.text_realname);
        textViewRealname.setText("真实姓名：" + realname);

        final TextView textViewAddress = root.findViewById(R.id.text_address);
        textViewAddress.setText("宿舍地址：" + address);

        final TextView textViewTelephone = root.findViewById(R.id.text_telephone);
        textViewTelephone.setText("联系电话：" + telephone);

        final TextView textViewCreateNum = root.findViewById(R.id.text_issued_num);
        String issued = " 已发布 " + Integer.toString(createNumber) + " 单";
        textViewCreateNum.setText(issued);

        final TextView textViewCompleteNum = root.findViewById(R.id.text_received_num);
        String accepted = " 已完成 " + Integer.toString(completeNumber) + " 单";
        textViewCompleteNum.setText(accepted);

        final TextView textViewCredit = root.findViewById(R.id.text_credit);
        String creditText = "当前积分：" + Integer.toString(credit);
        textViewCredit.setText(creditText);

        final ImageView imageViewAvatar = root.findViewById(R.id.img_avatar);
        //?
        imageViewAvatar.setImageURI(avatarUrl);

        btEditMe.setOnClickListener(new View.OnClickListener() {
            AlertDialog alertDialog = null;
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final LayoutInflater inflater = getLayoutInflater();
                final View layout = inflater.inflate(R.layout.me_change_table, null);
                builder.setView(layout);
                final EditText textBrief = (EditText) layout.findViewById(R.id.text_brief);
                final EditText textTelephone = (EditText) layout.findViewById(R.id.text_telephone);
                final EditText textRealname = (EditText) layout.findViewById(R.id.text_realname);
                final EditText textAddress = (EditText) layout.findViewById(R.id.text_address);
                final Button cancelChange = (Button) layout.findViewById(R.id.cancel_change);
                final Button saveChange = (Button) layout.findViewById(R.id.save_change);
                final Spinner demand_type = (Spinner) layout.findViewById(R.id.service_type);
                cancelChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //关闭小弹窗
                        Intent intent = new Intent(getActivity(), BottomMenu.class);
                        intent.putExtra("username", email);
                        startActivity(intent);
                    }
                });

                saveChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean has_error = false;
                        String error_content = "";

                        final String brief = textBrief.getText().toString();
                        final String telephone = textTelephone.getText().toString();
                        final String realname = textRealname.getText().toString();
                        final String address = textAddress.getText().toString();
                        final AVUser user = new AVUser();
                        user.put("brief",brief);
                        user.put("telephone",telephone);
                        user.put("realname",realname);
                        user.put("address",address);
                        user.saveInBackground().subscribe(new Observer<AVObject>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(AVObject avObject) {
                                if (alertDialog != null)
                                    alertDialog.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                System.out.println(e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                });



                btChangePassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final LayoutInflater inflater = getLayoutInflater();
                        final View layout = inflater.inflate(R.layout.password_change, null);
                        builder.setView(layout);
                        final EditText emailCheck = (EditText) layout.findViewById(R.id.email_check);
                        final Button mailSend = (Button) layout.findViewById(R.id.mail_send);
                        final Button backMe = (Button) layout.findViewById(R.id.back_me);
                        mailSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String textEmail = emailCheck.getText().toString();
                                AVUser.requestPasswordResetInBackground(textEmail).blockingSubscribe();
                            }
                        });
                        backMe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //关闭小弹窗
                                Intent intent = new Intent(getActivity(), BottomMenu.class);
                                intent.putExtra("username", email);
                                startActivity(intent);
                            }
                        });
                        alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                btLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AVUser.logOut();
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        startActivity(intent);
                    }
                });

//        final TextView textView = root.findViewById(R.id.text_nickname);
//        meViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

            }

            private void init(Bundle savedInstanceState) {
            }

        });
        return root;
    }
}
