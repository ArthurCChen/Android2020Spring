package materiallogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ust.jupiter.jupiter.R;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import cn.bmob.v3.listener.SaveListener;
import materiallogin.ui.issued.IssuedFragment;
import materiallogin.ui.accepted.AcceptedFragment;
import materiallogin.ui.wanted.WantedFragment;
import materiallogin.ui.me.MeFragment;

public class BottomMenu extends AppCompatActivity {

    WantedFragment wantedFragment;
    AcceptedFragment acceptedFragment;
    MeFragment meFragment;
    IssuedFragment issuedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wantedFragment = new WantedFragment();
        acceptedFragment = new AcceptedFragment();
        meFragment = new MeFragment();
        issuedFragment = new IssuedFragment();

        setContentView(R.layout.activity_bottom_menu);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_wanted, R.id.navigation_accepted,
                R.id.navigation_issued, R.id.navigation_me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.navigation_wanted:
//                        loadFragment(wantedFragment);
//                        return true;
//                    case R.id.navigation_accepted:
//                        loadFragment(acceptedFragment);
//                        return true;
//                    case R.id.navigation_issued:
//                        loadFragment(issuedFragment);
//                        return true;
//                    case R.id.navigation_me:
//                        loadFragment(meFragment);
//                        return true;
//
//                }
//                return false;
//            }
//        });
    }

    public void onCreateNewWant(@NonNull View view ){
        push(view);
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
    }

    AlertDialog alertDialog;

    public void push(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_input_table, null);
        builder.setView(layout);
        final SPutil s = new SPutil(this);
        final EditText demand_title = (EditText) layout.findViewById(R.id.demand_title);
        final EditText demand_content = (EditText) layout.findViewById(R.id.demand_content);
        final EditText deadline = (EditText) layout.findViewById(R.id.deadline);
        final EditText wanted_number = (EditText) layout.findViewById(R.id.wanted_number);
        final EditText reward = (EditText) layout.findViewById(R.id.reward);
        final TextView cancel = (TextView) layout.findViewById(R.id.cancel);
        final TextView save = (TextView) layout.findViewById(R.id.save);
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
                String content = "";

                // check title and content
                if (demand_title.getText().toString().equals("") || demand_content.getText().toString().equals("")) {
                    if (demand_title.getText().toString().equals("")) {
                        content = "请输入标题。";
                    }
                    else {
                        content = "请输入内容。";
                    }
                    has_error = true;
                }
                int participants_number = 0;
                float reward_number = 0;
                // check wanted_number
                try {
                    participants_number = Integer.parseInt(wanted_number.getText().toString());
                } catch (Exception e) {
                    content = "需求人数至少为一个人。";
                    has_error = true;
                }
                if (participants_number > 0) {
                    content = "需求人数至少为一个人。";
                    has_error = true;
                }
                // check reward
                try {
                    reward_number = Float.parseFloat(reward.getText().toString());
                } catch (Exception e) {
                    content = "你需要承诺报酬。";
                    has_error = true;
                }

                if (has_error) {
                    new AlertDialog.Builder(BottomMenu.this)
                            .setTitle("提示")
                            .setMessage(content)
                            .setPositiveButton("确定", null)
                            .show();
                }

                new AlertDialog.Builder(BottomMenu.this)
                        .setTitle("提示")
                        .setMessage("是否确认支付¥" + reward.getText().toString() + "？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String kddh2 = demand_title.getText().toString();
                                String kdgs3 = demand_content.getText().toString();
                                String qqdz4 = wanted_number.getText().toString();
                                String swdzs = deadline.getText().toString();
                                String fy5 = reward.getText().toString();
                                if (TextUtils.isEmpty(fy5)
                                        || TextUtils.isEmpty(kddh2)
                                        || TextUtils.isEmpty(swdzs)
                                        || TextUtils.isEmpty(kdgs3)
                                        || TextUtils.isEmpty(qqdz4)) {
                                    Toast.makeText(BottomMenu.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                BmobExpress express = new BmobExpress();
                                if (pick_up_package.isChecked()) {
                                    express.setType("快递");
                                }
                                if (used_goods.isChecked()) {
                                    express.setType("外卖");
                                }
                                if (participant_hiring.isChecked()) {
                                    express.setType("商品");
                                }
                                express.setKddh(kddh2);
                                express.setKdgs(kdgs3);
                                express.setQwdz(swdzs);
                                express.setFbgs(new SPutil(BottomMenu.this).ReadName());
                                express.setQqdz(qqdz4);
                                express.setPrice(Integer.parseInt(fy5));
                                express.setStu("");
                                express.setStatus("待接单");
                                express.save(BottomMenu.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        initData();
                                        Toast.makeText(BottomMenu.this, "发布成功", Toast.LENGTH_SHORT).show();
                                        if (alertDialog != null)
                                            alertDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(BottomMenu.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
    }

    private void initData(){}


}
