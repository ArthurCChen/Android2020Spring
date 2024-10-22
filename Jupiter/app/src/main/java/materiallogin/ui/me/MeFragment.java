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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import materiallogin.BottomMenu;
import materiallogin.MainActivity;
import materiallogin.RegisterActivity;

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

        final TextView textViewCompleteNum = root.findViewById(R.id.text_received_num);

        final TextView textViewCredit = root.findViewById(R.id.text_credit);

        AVQuery query = new AVQuery("demand_relationship");
        query.whereEqualTo("enroller_id", (String)AVUser.getCurrentUser().getServerData().get("username"));
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) { }
            @Override
            public void onNext(List<AVObject> objects) {
                textViewCompleteNum.setText(" 已完成 " + objects.size() + " 单");
                textViewCredit.setText("当前积分：" + 10 * objects.size());
            }
            @Override
            public void onError(Throwable e) { }
            @Override
            public void onComplete() { }
        });

        AVQuery query_ = new AVQuery("demand");
        query_.whereEqualTo("username", (String)AVUser.getCurrentUser().getServerData().get("username"));
        query_.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) { }
            @Override
            public void onNext(List<AVObject> objects) {
                textViewCreateNum.setText(" 已发布 " + objects.size() + " 单");
            }
            @Override
            public void onError(Throwable e) { }
            @Override
            public void onComplete() { }
        });

//        final ImageView imageViewAvatar = root.findViewById(R.id.img_avatar);
//        //?
//        imageViewAvatar.setImageURI(avatarUrl);

        btEditMe.setOnClickListener(new View.OnClickListener() {
            AlertDialog alertDialog = null;

            View layout;
            EditText textBrief = null;
            EditText textTelephone = null;
            EditText textRealname = null;
            EditText textNickname = null;
            EditText textAddress = null;
            Button cancelChange = null;
            Button saveChange = null;
            Spinner demand_type = null;

            private void renew(AVUser user) {
                textBrief.setText((String)user.getServerData().get("brief"));
                textTelephone.setText((String)user.getServerData().get("telephone"));
                textNickname.setText((String)user.getServerData().get("nickname"));
                textRealname.setText((String)user.getServerData().get("realname"));
                textAddress.setText((String)user.getServerData().get("address"));
            }

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final LayoutInflater inflater = getLayoutInflater();
                layout = inflater.inflate(R.layout.me_change_table, null);
                builder.setView(layout);
                textNickname = (EditText) layout.findViewById(R.id.text_nickname);
                textBrief = (EditText) layout.findViewById(R.id.text_brief);
                textTelephone = (EditText) layout.findViewById(R.id.text_telephone_change);
                textRealname = (EditText) layout.findViewById(R.id.text_realname);
                textAddress = (EditText) layout.findViewById(R.id.text_address);
                cancelChange = (Button) layout.findViewById(R.id.cancel_change);
                saveChange = (Button) layout.findViewById(R.id.save_change);
                demand_type = (Spinner) layout.findViewById(R.id.service_type);

                final AVUser user = AVUser.getCurrentUser();
                renew(user);

                cancelChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //关闭小弹窗
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        alertDialog = null;
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
                        final String nickname = textNickname.getText().toString();
                        final String address = textAddress.getText().toString();
                        user.put("brief",brief);
                        user.put("nickname",nickname);
                        user.put("telephone",telephone);
                        user.put("realname",realname);
                        user.put("address",address);
                        user.saveInBackground().subscribe(new Observer<AVObject>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(AVObject avObject) {
                                Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                                if (alertDialog != null)
                                    alertDialog.dismiss();
                                alertDialog = null;
                                renew(user);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                                if (alertDialog != null)
                                    alertDialog.dismiss();
                                alertDialog = null;
                                System.out.println(e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }
                });




//        final TextView textView = root.findViewById(R.id.text_nickname);
//        meViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
                alertDialog = builder.create();
                alertDialog.show();

            }

            private void init(Bundle savedInstanceState) {
            }

        });

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            AlertDialog alertDialog = null;
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
                        if (textEmail.equals(AVUser.getCurrentUser().getServerData().get("email"))) {
                            AVUser.requestPasswordResetInBackground(textEmail);
                            Toast.makeText(getActivity(), "邮件已发送", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "邮箱输入错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                backMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //关闭小弹窗
                        if (alertDialog != null)
                            alertDialog.dismiss();
                        alertDialog = null;
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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return root;
    }
}
