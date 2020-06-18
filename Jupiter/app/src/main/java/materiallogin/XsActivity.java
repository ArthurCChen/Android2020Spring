package materiallogin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ust.jupiter.jupiter.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

// 接单页面
public class XsActivity extends AppCompatActivity {

    private ListView listView;
    private RadioGroup rg;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;

    private List<BmobExpress> all = new ArrayList<>();
    private List<BmobExpress> ing = new ArrayList<>();
    private List<BmobExpress> ed = new ArrayList<>();
    private Runnable runAll;
    private Runnable runIng;
    private Runnable runEd;

    // 初始化
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xs);
        listView = (ListView) findViewById(R.id.list);
        rg = (RadioGroup) findViewById(R.id.service_type);
        rb1 = (RadioButton) findViewById(R.id.rb_1);
        rb2 = (RadioButton) findViewById(R.id.rb_2);
        rb3 = (RadioButton) findViewById(R.id.rb_3);

        runAll = new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new ItemAdapter(XsActivity.this, all));
            }
        };

        runIng = new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new ItemAdapter(XsActivity.this, ing));
            }
        };

        runEd = new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new ItemAdapter(XsActivity.this, ed));
            }
        };

        getData(runAll);

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getData(runAll);
                }
            }
        });

        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getData(runIng);
                }
            }
        });

        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getData(runEd);
                }
            }
        });

    }

    // 退出登陆
    public void exit(View v) {
        new SPutil(this).WriteName("");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // 获取数据
    private void getData(final Runnable runnable) {
        BmobQuery<BmobExpress> query = new BmobQuery<>();
        query.addWhereEqualTo("status", "待接单")
                .addWhereNotEqualTo("fbgs", new SPutil(this).ReadName().replace("xs-", ""))
                .findObjects(this, new FindListener<BmobExpress>() {
                    @Override
                    public void onSuccess(List<BmobExpress> list) {
                        all.clear();
                        all.addAll(list);
                        if (runnable != null)
                            runnable.run();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(XsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });

        BmobQuery<BmobExpress> query2 = new BmobQuery<>();
        query2.addWhereEqualTo("stu", new SPutil(this).ReadName().replace("xs-", ""))
                .addWhereEqualTo("status", "正在进行")
                .findObjects(this, new FindListener<BmobExpress>() {
                    @Override
                    public void onSuccess(List<BmobExpress> list) {
                        ing.clear();
                        ing.addAll(list);
                        if (runnable != null)
                            runnable.run();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(XsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });

        BmobQuery<BmobExpress> query3 = new BmobQuery<>();
        query3.addWhereEqualTo("stu", new SPutil(this).ReadName().replace("xs-", ""))
                .addWhereEqualTo("status", "已完成")
                .findObjects(this, new FindListener<BmobExpress>() {
                    @Override
                    public void onSuccess(List<BmobExpress> list) {
                        ed.clear();
                        ed.addAll(list);
                        if (runnable != null)
                            runnable.run();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(XsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
