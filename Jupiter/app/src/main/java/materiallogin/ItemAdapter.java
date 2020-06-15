package materiallogin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.jupiter.jupiter.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

// 列表数据展示
public class ItemAdapter extends BaseAdapter implements View.OnClickListener {

    private List<BmobExpress> objects = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflater;
    private boolean isStudent;
    private String name;

    public void remove(Object oj) {
        objects.remove(oj);
        notifyDataSetChanged();
    }

    // 构造器
    public ItemAdapter(Context context, List<BmobExpress> o) {
        this.objects = o;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        SPutil sp = new SPutil(context);
        name = sp.ReadName().replace("xs-", "");
    }

    boolean show = false;

    // 是否展示按钮
    public void showBtn(boolean b) {
        this.show = b;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public BmobExpress getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    AlertDialog alertDialog;

    // 绑定列表的数据展示
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    // 数据展示
    private void initializeViews(final BmobExpress object, final ViewHolder holder) {
        holder.all.setTag(R.id.action_settings, object);
        holder.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BmobExpress express = (BmobExpress) v.getTag(R.id.action_settings);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("需求信息");
                builder.setMessage(express.toString());
                builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertDialog != null)
                            alertDialog.dismiss();
                    }
                });
                if (new SPutil(context).ReadGender().equals(express.getFbgs())) {
                    builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new AlertDialog.Builder(context)
                                    .setTitle("提示")
                                    .setMessage("是否删除订单？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i2) {
                                            express.delete(context, new DeleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                                    remove(express);
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        }
                    });
                }
                alertDialog = builder.show();
            }
        });
        holder.dan.setText(object.getT() + object.getKddh() + object.gettp());
        holder.status.setText(object.getStatus());
        holder.status.setTag(object);
        holder.status.setOnClickListener(this);
        boolean fd = context instanceof ShangjiaActivity;
        holder.xx.setVisibility(View.VISIBLE);
        switch (object.getStatus()) {
            case "待接单":
                holder.status.setBackgroundColor(0xffff0000);
                if(fd) {
                    holder.xx.setVisibility(View.GONE);
                } else {
                    holder.xx.setVisibility(View.VISIBLE);
                }
                break;
            case "正在进行":
                holder.status.setBackgroundColor(0xff0000ff);
                break;
            case "已完成":
                holder.status.setBackgroundColor(0xff00ff00);
                break;
        }
        holder.xx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(object.getStatus().equals("待接单")) {
                    BmobQuery<BmobStudent> studentBmobQuery = new BmobQuery<>();
                    studentBmobQuery.addWhereEqualTo("username", object.getFbgs());
                    studentBmobQuery.findObjects(context, new FindListener<BmobStudent>() {
                        @Override
                        public void onSuccess(List<BmobStudent> list) {
                            if(list == null || list.isEmpty()) {
                                Toast.makeText(context, "用户不存在", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ChatActivity.launch(context, list.get(0).getUsername(), list.get(0).getName());
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    BmobQuery<BmobStudent> studentBmobQuery = new BmobQuery<>();
                    if(object.getFbgs().equals(SPUtils.getString("username"))) {
                        studentBmobQuery.addWhereEqualTo("username", object.getStu());
                    }else {
                        studentBmobQuery.addWhereEqualTo("username", object.getFbgs());
                    }
                    studentBmobQuery.findObjects(context, new FindListener<BmobStudent>() {
                        @Override
                        public void onSuccess(List<BmobStudent> list) {
                            if(list == null || list.isEmpty()) {
                                Toast.makeText(context, "用户不存在", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ChatActivity.launch(context, list.get(0).getUsername(), list.get(0).getName());
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    AlertDialog dialog1;

    // 按钮点击事件
    @Override
    public void onClick(final View v) {
        final BmobExpress express = (BmobExpress) v.getTag();
        if (express.getFbgs().equals(new SPutil(context).ReadName()) && !express.getStatus().equals("已完成")) {
            return;
        }
        switch (express.getStatus()) {
            case "待接单":
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("是否接单？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        express.setStu(name);
                        express.setStatus("正在进行");
                        express.update(context, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                objects.remove(express);
                                notifyDataSetChanged();
                                dialog1.dismiss();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });
                dialog1 = builder.create();
                dialog1.show();
                break;
            case "已完成":
                if (context instanceof ShangjiaActivity && TextUtils.isEmpty(express.getCmt())) {
                    View v1 = LayoutInflater.from(context).inflate(R.layout.cc1, null);
                    final EditText cmt = (EditText) v1.findViewById(R.id.c);
                    final RatingBar bar = (RatingBar) v1.findViewById(R.id.r);
                    new AlertDialog.Builder(context)
                            .setTitle("星级评价")
                            .setView(v1)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int r = (int) (bar.getRating());
                                    String cmts = cmt.getText().toString();
                                    if (TextUtils.isEmpty(cmts)) {
                                        Toast.makeText(context, "请输入评价信息", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    express.setXj(r + "星");
                                    express.setCmt(cmts);
                                    express.update(context, new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            objects.remove(express);
                                            Toast.makeText(context, "发布成功", Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            express.setXj(null);
                                            express.setCmt(null);
                                            Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
            case "正在进行":
                AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                builder2.setTitle("提示");
                builder2.setMessage("是否已完成并收取¥" + express.getPrice() + "费用？");
                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        express.setStu(name);
                        express.setStatus("已完成");
                        express.update(context, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                objects.remove(express);
                                notifyDataSetChanged();
                                dialog1.dismiss();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });
                dialog1 = builder2.create();
                dialog1.show();
                break;
        }
    }

    // 列表控件缓存
    protected class ViewHolder {
        private TextView dan;
        private TextView status;
        private View all;
        private ImageView xx;

        public ViewHolder(View view) {
            all = view;
            dan = (TextView) view.findViewById(R.id.dan);
            status = (TextView) view.findViewById(R.id.status);
            xx = (ImageView) view.findViewById(R.id.xx);
        }
    }
}
