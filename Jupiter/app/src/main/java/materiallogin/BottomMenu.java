package materiallogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.listener.SaveListener;
import cn.leancloud.AVObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import materiallogin.ui.issued.IssuedFragment;
import materiallogin.ui.accepted.AcceptedFragment;
import materiallogin.ui.wanted.WantedFragment;
import materiallogin.ui.me.MeFragment;

public class BottomMenu extends AppCompatActivity {

    WantedFragment wantedFragment;
    AcceptedFragment acceptedFragment;
    MeFragment meFragment;
    IssuedFragment issuedFragment;
    String username_from_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wantedFragment = new WantedFragment();
        acceptedFragment = new AcceptedFragment();
        meFragment = new MeFragment();
        issuedFragment = new IssuedFragment();
        username_from_intent = getIntent().getStringExtra("username");

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
        final Button cancel = (Button) layout.findViewById(R.id.cancel);
        final Button save = (Button) layout.findViewById(R.id.save);
        final Spinner demand_type = (Spinner) layout.findViewById(R.id.service_type);
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
                final String username = username_from_intent;
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
                    new AlertDialog.Builder(BottomMenu.this)
                            .setTitle("提示")
                            .setMessage(error_content)
                            .setPositiveButton("确定", null)
                            .show();
                }


                final AVODemand demand = new AVODemand();
                demand.setContent(content);
                demand.setTitle(title);
                demand.setUsername(username);
                demand.setDemand_state("inactive");
                demand.setWanted_number(participants_number);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE, days);
                demand.setEnd_time(c.getTime());
                demand.setReward(reward_number);
                demand.setType(type);

                demand.saveInBackground().subscribe(new Observer<AVObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(AVObject avObject) {
                        Toast.makeText(BottomMenu.this, "发布成功", Toast.LENGTH_SHORT).show();
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e.getMessage());
                        Toast.makeText(BottomMenu.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

}
