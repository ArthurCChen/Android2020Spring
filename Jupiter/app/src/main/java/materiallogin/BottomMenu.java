package materiallogin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thu.qinghuaquan.R;
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
        final TextView gsmc = (TextView) layout.findViewById(R.id.gsmc);
        final EditText kddh = (EditText) layout.findViewById(R.id.kddh);
        final EditText kdgs = (EditText) layout.findViewById(R.id.kdgs);
        final EditText swdz = (EditText) layout.findViewById(R.id.swdz);
        final EditText qsdz = (EditText) layout.findViewById(R.id.qsdz);
        final TextView t1 = (TextView) layout.findViewById(R.id.tip1);
        final TextView t2 = (TextView) layout.findViewById(R.id.tip2);
        final RadioGroup rg = (RadioGroup) layout.findViewById(R.id.rg);
        final RadioButton rbKd = (RadioButton) layout.findViewById(R.id.rb_kd);
        final RadioButton rbWm = (RadioButton) layout.findViewById(R.id.rb_wm);
        final RadioButton rbSp = (RadioButton) layout.findViewById(R.id.rb_sp);
        final EditText fy = (EditText) layout.findViewById(R.id.fy);
        gsmc.setText(s.ReadGender());
        rbKd.setChecked(true);
        final TextView cancel = (TextView) layout.findViewById(R.id.cancel);
        final TextView save = (TextView) layout.findViewById(R.id.save);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                if (id == R.id.rb_wm) {
                    t1.setText("外卖名称");
                    t2.setText("配送公司");
                    kddh.setHint("如：西红柿盖饭");
                    kdgs.setHint("如：美团外卖");
                } else if (id == R.id.rb_kd) {
                    t1.setText("快递单号");
                    t2.setText("快递公司");
                    kddh.setHint("如：1234567890");
                    kdgs.setHint("如：顺丰快递");
                } else {
                    t1.setText("商品名称");
                    t2.setText("商品备注");
                    kddh.setHint("如：牙膏");
                    kdgs.setHint("如：炫齿白牌子");
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BottomMenu.this)
                        .setTitle("提示")
                        .setMessage("是否确认支付¥" + fy.getText().toString() + "？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String kddh2 = kddh.getText().toString();
                                String kdgs3 = kdgs.getText().toString();
                                String qqdz4 = qsdz.getText().toString();
                                String swdzs = swdz.getText().toString();
                                String fy5 = fy.getText().toString();
                                if (TextUtils.isEmpty(fy5)
                                        || TextUtils.isEmpty(kddh2)
                                        || TextUtils.isEmpty(swdzs)
                                        || TextUtils.isEmpty(kdgs3)
                                        || TextUtils.isEmpty(qqdz4)) {
                                    Toast.makeText(BottomMenu.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                BmobExpress express = new BmobExpress();
                                if (rbKd.isChecked()) {
                                    express.setType("快递");
                                }
                                if (rbWm.isChecked()) {
                                    express.setType("外卖");
                                }
                                if (rbSp.isChecked()) {
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
        alertDialog = builder.create();
        alertDialog = builder.show();
    }

    private void initData(){}


}
