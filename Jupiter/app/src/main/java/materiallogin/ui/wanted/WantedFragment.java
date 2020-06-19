package materiallogin.ui.wanted;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ust.jupiter.jupiter.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.LogRecord;

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

    private void clearLists(){
        pages = 0;
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
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wanted, container, false);


        viewPager  = (ViewPager) root.findViewById(R.id.wants_pager);

//        final ArrayList<String> mTypes = new ArrayList<String>(Arrays.asList(
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other),
//                getResources().getString(R.string.type_other)
//
//                ));
//
//        final ArrayList<String> mTitles = new ArrayList<String>(Arrays.asList(
//                "1","2","3","4","5", "6", "1","2","3","4","5", "6"
//        ));
//
//        final ArrayList<String> mContents = new ArrayList<String>(Arrays.asList(
//                "1","2","3","4", "5", "6", "1","2","3","4","5", "6"
//        ));
//
//        final ArrayList<String> mMoneys = new ArrayList<String>(Arrays.asList(
//                "1","2","3","4", "5", "6", "1","2","3","4","5", "6"
//        ));

        clearLists();

//        pagerAdapter = new WantPagerAdapter(getChildFragmentManager(),
//                3,
//                mTypes, mTitles, mContents, mMoneys
//                );
        pagerAdapter = new WantPagerAdapter(getChildFragmentManager());
        boolean succes = newQuery();
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
                    boolean success = newQuery();
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

    private int pages;
    private ArrayList<String> newTitles;
    private ArrayList<String> newTypes;
    private ArrayList<String> newMoneys;
    private ArrayList<String> newContents;

    private void refreshAdapter(){
        pagerAdapter.types.addAll(newTypes);
        pagerAdapter.titles.addAll(newTitles);
        pagerAdapter.moneys.addAll(newMoneys);
        pagerAdapter.contents.addAll(newContents);
        pagerAdapter.pages = pages;
        curLoadPages += 1;
    }

    private boolean newQuery(){
        final CountDownLatch latch = new CountDownLatch(2);

        queryTotalNum(latch);

        queryNewPages(latch);

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

    private void queryNewPages(final CountDownLatch latch){
        AVQuery<AVObject> query = new AVQuery<>("demand");
        query.limit(GridWantAdapter.pageMaxCnt);
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
                    newMoneys.add((String)avObject.getString("reward"));
                    newTitles.add((String)avObject.getString("title"));
                }
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(),"网络错误", Toast.LENGTH_SHORT);
                latch.countDown();
                Thread.currentThread().interrupt();

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
                        pages =  (integer==0)? 0 : (integer + 5) / GridWantAdapter.pageMaxCnt;
                        latch.countDown();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(),"网络错误", Toast.LENGTH_SHORT);
                        latch.countDown();
                        Thread.currentThread().interrupt();
                    }

                    @Override
                    public void onComplete() {
                    }
                }
        );
    }
}
