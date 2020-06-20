package materiallogin.ui.accepted;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thu.qinghuaquan.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import cn.leancloud.AVObject;
import cn.leancloud.AVUser;
import cn.leancloud.search.AVSearchQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import materiallogin.AVDemand;
import materiallogin.DemandDetailActivity;
import materiallogin.ui.issued.IssuedFragment;
import materiallogin.ui.wanted.WantPagerAdapter;

public class AcceptedFragment extends Fragment {


    ViewPager viewPager;
    WantPagerAdapter pagerAdapter;
    PagerTabStrip tabStrip;
    private TextView suggestion;
    private Context mContext;
    AVSearchQuery query;


    private int items;
    private ArrayList<String> newTitles;
    private ArrayList<String> newTypes;
    private ArrayList<String> newMoneys;
    private ArrayList<String> newContents;
    private ArrayList<String> ids;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_accepted, container, false);
        viewPager = root.findViewById(R.id.accept_pager);
        suggestion = root.findViewById(R.id.suggestion);
        tabStrip = root.findViewById(R.id.want_pager_header);
        pagerAdapter = new WantPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);

        newQuery();
        return root;
    }

    // 在这里不分页了
    private void newQuery(){
        final CountDownLatch latch = new CountDownLatch(1);
        queryPages(latch);
        final Handler handler = new AcceptedFragment.mHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    items = query.getHits();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                handler.sendMessage(msg);
            }
        }).start();
    }

    private void refreshAdapter(){
        pagerAdapter.types.addAll(newTypes);
        pagerAdapter.titles.addAll(newTitles);
        pagerAdapter.moneys.addAll(newMoneys);
        pagerAdapter.contents.addAll(newContents);
        pagerAdapter.ids.addAll(ids);
        pagerAdapter.items = items;
    }

    class mHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(items == 0) {
                tabStrip.setVisibility(View.INVISIBLE);
                suggestion.setVisibility(View.VISIBLE);
                suggestion.setText("目前还没有,来接受一个?");
                return;
            }else{
                tabStrip.setVisibility(View.VISIBLE);
                suggestion.setVisibility(View.INVISIBLE);
            }
            refreshAdapter();
            pagerAdapter.notifyDataSetChanged();
        }
    }

    private void queryPages(final CountDownLatch latch){
        query = new AVSearchQuery();
        String userAccount = (String) AVUser.getCurrentUser().getServerData().get("email");
        query.setQueryString(String.format("enroller_id:\"%s\"", userAccount));
        query.setClassName("demand_relationship");
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AVObject> avObjects) {
                items = query.getHits();
                newTitles = new ArrayList<>();
                newTypes = new ArrayList<>();
                newMoneys = new ArrayList<>();
                newContents = new ArrayList<>();
                ids = new ArrayList<>();
                for (AVObject avObject : avObjects){
                    newContents.add((String)avObject.getString("content"));
                    newTypes.add((String)avObject.getString("type"));
                    newMoneys.add(String.valueOf(avObject.getNumber("reward")));
                    newTitles.add((String)avObject.getString("title"));
                    ids.add(avObject.getString("objectId"));
                }
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
