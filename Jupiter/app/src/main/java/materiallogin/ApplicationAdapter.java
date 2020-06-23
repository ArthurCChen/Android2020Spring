package materiallogin;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thu.qinghuaquan.R;

import org.w3c.dom.Text;

import java.util.List;

import cn.leancloud.AVInstallation;
import cn.leancloud.AVObject;
import cn.leancloud.AVPush;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.im.v2.AVIMClient;
import cn.leancloud.im.v2.AVIMException;
import cn.leancloud.im.v2.callback.AVIMClientCallback;
import cn.leancloud.push.PushService;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.MyViewHolder> {

    private List<AVObject> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        Button accpet;
        Button reject;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.enroller_intro);
            accpet = (Button) v.findViewById(R.id.accept_button);
            reject = (Button) v.findViewById(R.id.reject_button);
        }

        public void set(AVObject relationship) {
            String hint = "";
            if (relationship.getString("enroller_state").equals("accepted")) {
                hint = String.format("<br><p>您已接受%s的申请</p>", relationship.getString("enroller_id"));
                accpet.setText("聊天");
                reject.setText("完成");
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        relationship.put("enroller_state", "done");
                        relationship.saveInBackground().subscribe(new Observer<AVObject>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(AVObject avObject) {
                                set(relationship);
                                AVObject object = (AVObject)relationship.get("demand");
                                object.getServerData();
                                AVQuery query = new AVQuery("demand_relationship");
                                query.whereEqualTo("demand", object);
                                query.whereEqualTo("enroller_state", "done");
                                query.findInBackground().subscribe(new Observer<List<AVObject>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) { }
                                    @Override
                                    public void onNext(List<AVObject> objects) {
                                        if (objects.size() == object.getInt("wanted_number")) {
                                            object.put("type", "done");
                                            object.put("demand_state", "done");
                                            object.saveInBackground().subscribe();
                                        }
                                    }
                                    @Override
                                    public void onError(Throwable e) { }
                                    @Override
                                    public void onComplete() { }
                                });
                            }

                            @Override
                            public void onError(Throwable e) {
                                System.out.println(e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    }
                });
            }
            else if (!relationship.getString("enroller_state").equals("done")){
                hint = String.format("<br><p>%s向您发来申请</p>", relationship.getString("enroller_id"));
                reject.setVisibility(View.VISIBLE);
                accpet.setText("接受");
            }
            else {
                hint = String.format("<br><p>%s已完成任务</p>", relationship.getString("enroller_id"));
                reject.setVisibility(View.GONE);
                accpet.setVisibility(View.GONE);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(hint, Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView.setText(Html.fromHtml(hint));
            }
        }
    }

    // Provi`de a suitable constructor (depends on the kind of dataset)
    public ApplicationAdapter(List<AVObject> mDataset) {
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ApplicationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application_card, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AVObject p = mDataset.get(position);
        holder.set(p);
        if (p.getString("enroller_state").equals("accepted")) {
            holder.accpet.setOnClickListener(v -> {
                // TODO: 2020621chat
                String peer = p.getString("enroller_id");
                String un = p.getString("demander_id");
                String demand = p.getAVObject("demand").getObjectId();

                PushService.subscribe(App.getContext(), demand, DemandDetailActivity.class);
                DetailObject detailObject = new DetailObject("开始聊天", demand, "1");
                String pushStr = JSON.toJSONString(detailObject);
                AVQuery pushQuery = AVInstallation.getQuery();
                pushQuery.whereEqualTo("channels", demand);
                AVPush push = new AVPush();
                push.setData((JSONObject)JSON.toJSON(detailObject));
                push.setQuery(pushQuery);
                push.setPushToAndroid(true);
                push.sendInBackground().subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        System.out.println("推送成功" + jsonObject);
                    }
                    @Override
                    public void onError(Throwable e) {
                        System.out.println("推送失败，错误信息：" + e.getMessage());
                    }
                    @Override
                    public void onComplete() {
                    }
                });


                //下面是聊天窗口启动代码
                        LCChatKit.getInstance().open(un , new AVIMClientCallback() {
                            @Override
                            public void done(AVIMClient avimClient, AVIMException e) {
                                if (null == e) {
                                    Intent intent = new Intent(App.getContext(), LCIMConversationActivity.class);
                                    //这里填写对方clientId
                                    intent.putExtra(LCIMConstants.PEER_ID, peer);
                                    App.getContext().startActivity(intent);
                                } else {
                                    Toast.makeText( App.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            });
        }
        else {
            holder.accpet.setOnClickListener(v -> {
                p.put("enroller_state", "accepted");
                p.saveInBackground().subscribe();
                mDataset.remove(position);
                mDataset.add(p);
                notifyItemMoved(position, mDataset.size() - 1);
                notifyItemChanged(mDataset.size() - 1);
            });
            holder.reject.setOnClickListener(v -> {
                p.put("enroller_state", "rejected");
                p.saveInBackground().subscribe();
                AVObject demand = (AVObject) p.get("demand");
                demand.increment("confirmed_number");
                demand.saveInBackground().subscribe();
                mDataset.remove(position);
                notifyItemRemoved(position);
            });
        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
