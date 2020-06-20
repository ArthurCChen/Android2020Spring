package materiallogin.ui.me;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ust.jupiter.jupiter.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Arrays;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import io.reactivex.disposables.Disposable;

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
}
