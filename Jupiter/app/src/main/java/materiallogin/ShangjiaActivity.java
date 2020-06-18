package materiallogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.jupiter.jupiter.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

// 发单页面
public class ShangjiaActivity extends AppCompatActivity {

    AlertDialog alertDialog;
    private TextView gsmc;
    private EditText kddh;
    private EditText kdgs;
    private EditText swdz;
    private EditText qsdz;
    private EditText fy;
    private TextView cancel;
    private TextView save;
    private ListView listView;
    private RadioGroup rg2;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;

    // 初始化
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shangjia_home);
        listView = (ListView) findViewById(R.id.list);
        rg2 = (RadioGroup) findViewById(R.id.service_type);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
        rb1.setChecked(true);
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                initData();
                ItemAdapter itemAdapter = (ItemAdapter) listView.getAdapter();
                if(itemAdapter == null) {
                    return;
                }
                if (rb1.isChecked()) {
                    itemAdapter.showBtn(true);
                }
                if (rb2.isChecked()) {
                    itemAdapter.showBtn(false);
                }
                if (rb3.isChecked()) {
                    itemAdapter.showBtn(false);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(ShangjiaActivity.this)
                        .setTitle("提示")
                        .setMessage("是否删除订单？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i2) {
                                final ItemAdapter adapter = (ItemAdapter) listView.getAdapter();
                                final BmobExpress express = (BmobExpress) adapter.getItem(i);
                                express.delete(ShangjiaActivity.this, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(ShangjiaActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
                                        adapter.remove(express);
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(ShangjiaActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });
        initData();
    }

    // 获取数据
    private void initData() {
        BmobQuery<BmobExpress> expressBmobQuery = new BmobQuery<>();
        expressBmobQuery.addWhereEqualTo("fbgs", new SPutil(this).ReadName())
                .findObjects(this, new FindListener<BmobExpress>() {
                    @Override
                    public void onSuccess(List<BmobExpress> list) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        Iterator<BmobExpress> es = list.iterator();
                        if (rb2.isChecked()) {
                            while (es.hasNext()) {
                                BmobExpress express = es.next();
                                if (!(TextUtils.isEmpty(express.getCmt()) && express.getStatus().equals("已完成"))) {
                                    es.remove();
                                }
                            }
                        }
                        if (rb3.isChecked()) {
                            while (es.hasNext()) {
                                BmobExpress express = es.next();
                                if (TextUtils.isEmpty(express.getCmt()) || !express.getStatus().equals("已完成")) {
                                    es.remove();
                                }
                            }
                        }
                        listView.setAdapter(new ItemAdapter(ShangjiaActivity.this, list));
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(ShangjiaActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    RadioGroup rg;
    RadioButton rbKd;
    RadioButton rbWm;
    RadioButton rbSp;
    TextView t1;
    TextView t2;

    // 发布按钮事件
    public void push(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View layout = inflater.inflate(R.layout.dialog_input_table, null);
        builder.setView(layout);
        SPutil s = new SPutil(this);
        kddh = (EditText) layout.findViewById(R.id.demand_title);
        swdz = (EditText) layout.findViewById(R.id.deadline);
        qsdz = (EditText) layout.findViewById(R.id.wanted_number);
        t1 = (TextView) layout.findViewById(R.id.tip1);
        t2 = (TextView) layout.findViewById(R.id.tip2);
        rg = (RadioGroup) layout.findViewById(R.id.service_type);
        fy = (EditText) layout.findViewById(R.id.reward);
        gsmc.setText(s.ReadGender());
        rbKd.setChecked(true);
        cancel = (TextView) layout.findViewById(R.id.cancel);
        save = (TextView) layout.findViewById(R.id.save);
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
//                if (id == R.id.used_goods) {
//                    t1.setText("外卖名称");
//                    t2.setText("配送公司");
//                    kddh.setHint("如：西红柿盖饭");
//                    kdgs.setHint("如：美团外卖");
//                } else if (id == R.id.pick_up_package) {
//                    t1.setText("快递单号");
//                    t2.setText("快递公司");
//                    kddh.setHint("如：1234567890");
//                    kdgs.setHint("如：顺丰快递");
//                } else {
//                    t1.setText("商品名称");
//                    t2.setText("商品备注");
//                    kddh.setHint("如：牙膏");
//                    kdgs.setHint("如：炫齿白牌子");
//                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ShangjiaActivity.this)
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
                                    Toast.makeText(ShangjiaActivity.this, "请填写完整信息", Toast.LENGTH_SHORT).show();
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
                                express.setFbgs(new SPutil(ShangjiaActivity.this).ReadName());
                                express.setQqdz(qqdz4);
                                express.setPrice(Integer.parseInt(fy5));
                                express.setStu("");
                                express.setStatus("待接单");
                                express.save(ShangjiaActivity.this, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        initData();
                                        Toast.makeText(ShangjiaActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                        if (alertDialog != null)
                                            alertDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Toast.makeText(ShangjiaActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
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

    // 退吹登陆
    public void exit(View view) {
        new SPutil(this).WriteName("");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}