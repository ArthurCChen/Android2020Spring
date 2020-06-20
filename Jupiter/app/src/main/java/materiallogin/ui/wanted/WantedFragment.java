package materiallogin.ui.wanted;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ust.jupiter.jupiter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class WantedFragment extends Fragment {

    private ViewPager viewPager;
    private WantPagerAdapter pagerAdapter;
    int currentPosition;
    int curLoadPages;

    private ArrayList<String> mTitles;
    private ArrayList<String> mMoneys;
    private ArrayList<String> mTypes;
    private ArrayList<String> mContents;

    private Context mContext;

    private void clearLists(){
        items = 0;
        curLoadPages = 0;
        currentPosition = 0;
        mContents = new ArrayList<>();
        mMoneys = new ArrayList<>();
        mTitles = new ArrayList<>();
        mTypes = new ArrayList<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wanted, container, false);


        viewPager  = (ViewPager) root.findViewById(R.id.wants_pager);

        clearLists();


        pagerAdapter = new WantPagerAdapter(getChildFragmentManager());
        boolean succes = newQuery(2);//一开始加载两页,之后一页
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == curLoadPages){
                    //出bug
                    boolean success = newQuery(1);
                    if(!success) {
                        viewPager.setCurrentItem(position - 1);
                        return;
                    }else{
                        refreshAdapter();
                    }

                }
                currentPosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return root;
    }

    private int items;
    private ArrayList<String> newTitles;
    private ArrayList<String> newTypes;
    private ArrayList<String> newMoneys;
    private ArrayList<String> newContents;

    private void refreshAdapter(){
        pagerAdapter.types.addAll(newTypes);
        pagerAdapter.titles.addAll(newTitles);
        pagerAdapter.moneys.addAll(newMoneys);
        pagerAdapter.contents.addAll(newContents);
        pagerAdapter.items = items;
        curLoadPages += 1;
    }

    private boolean newQuery(int pags){
        final CountDownLatch latch = new CountDownLatch(2);

        queryTotalNum(latch);

        queryNewPages(items, latch);

        final Handler handler = new mHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                    refreshAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = "TRUE";
                handler.sendMessage(msg);
            }
        }).start();

        return true;
    }

    class mHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            pagerAdapter.notifyDataSetChanged();
        }
    }

    private void queryNewPages(int pages, final CountDownLatch latch){
        AVQuery<AVObject> query = new AVQuery<>("demand");
        query.limit(GridWantAdapter.pageMaxCnt * pages);
        query.skip(curLoadPages * GridWantAdapter.pageMaxCnt);
        query.findInBackground().subscribe(new Observer<List<AVObject>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<AVObject> avObjects) {
                int cnt = avObjects.size();
                curLoadPages += 1;
                newTitles = new ArrayList<>();
                newTypes = new ArrayList<>();
                newMoneys = new ArrayList<>();
                newContents = new ArrayList<>();
                for (AVObject avObject : avObjects){
                    newContents.add((String)avObject.getString("content"));
                    newTypes.add((String)avObject.getString("type"));
                    newMoneys.add(String.valueOf(avObject.getNumber("reward")));
                    newTitles.add((String)avObject.getString("title"));
                }
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(mContext,"网络错误", Toast.LENGTH_SHORT).show();
                latch.countDown();

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void queryTotalNum(final CountDownLatch latch){
        AVQuery<AVObject> query = new AVQuery<>("demand");
        query.countInBackground().subscribe(
                new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        items =  integer;
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mContext,"网络错误", Toast.LENGTH_SHORT).show();
                        latch.countDown();
                    }

                    @Override
                    public void onComplete() {
                    }
                }
        );
    }
}
