package materiallogin.ui.wanted;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
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

    private Context mContext;
    private String curCategory = "";
    private String curSort = "";

    private void clearLists(){
        pagerAdapter.contents = new ArrayList<>();
        pagerAdapter.titles = new ArrayList<>();
        pagerAdapter.moneys = new ArrayList<>();
        pagerAdapter.types = new ArrayList<>();
        pagerAdapter.items = 0;
        items = 0;
        curLoadPages = 0;
        currentPosition = 0;
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

        // Spinner
        final Spinner mSpinner = root.findViewById(R.id.want_type_spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curCategory = (String)mSpinner.getItemAtPosition(position);
                initNewQuery();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner spinner2 = root.findViewById(R.id.want_sort_spinner);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curSort = (String)spinner2.getItemAtPosition(position);
                initNewQuery();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        pagerAdapter = new WantPagerAdapter(getChildFragmentManager());
        initNewQuery();
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

    private boolean initNewQuery(){
        clearLists();
        return newQuery(1);
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
        limitedQuery(query);
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
        limitedQuery(query);
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

    void limitedQuery(AVQuery<AVObject> query){
        if (curCategory.equals(getResources().getString(R.string.type_ask)) ||
            curCategory.equals(getResources().getString(R.string.type_other)) ||
            curCategory.equals(getResources().getString(R.string.type_experiment)) ||
            curCategory.equals(getResources().getString(R.string.type_express)) ||
            curCategory.equals(getResources().getString(R.string.type_deal))){
            query.whereEqualTo("type", curCategory);
        }
        if(curSort.equals(getResources().getString(R.string.sort_begin_date))){
            query.orderByAscending("createdAt");
        }else if(curSort.equals(getResources().getString(R.string.sort_money))){
            query.orderByAscending("reward");
        }else if(curSort.equals(getResources().getString(R.string.sort_end_date))){
            query.orderByAscending("end_time");
        }
    }
}
