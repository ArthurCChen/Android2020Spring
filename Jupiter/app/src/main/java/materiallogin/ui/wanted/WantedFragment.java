package materiallogin.ui.wanted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.jupiter.jupiter.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class WantedFragment extends Fragment {

    private WantedViewModel wantedViewModel;
    private ViewPager viewPager;
    private WantPagerAdapter pagerAdapter;
    int currentPosition;
    int currentSize ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wantedViewModel =
                ViewModelProviders.of(this).get(WantedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wanted, container, false);


        viewPager  = (ViewPager) root.findViewById(R.id.wants_pager);
//        viewPager = (ViewPager) inflater.inflate(R.layout.fragment_wants_list, container, false);
        final ArrayList<String> mTypes = new ArrayList<String>(Arrays.asList(
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other),
                getResources().getString(R.string.type_other)

                ));

        final ArrayList<String> mTitles = new ArrayList<String>(Arrays.asList(
                "1","2","3","4","5", "6", "1","2","3","4","5", "6"
        ));

        final ArrayList<String> mContents = new ArrayList<String>(Arrays.asList(
                "1","2","3","4", "5", "6", "1","2","3","4","5", "6"
        ));

        final ArrayList<String> mMoneys = new ArrayList<String>(Arrays.asList(
                "1","2","3","4", "5", "6", "1","2","3","4","5", "6"
        ));

        currentSize = 1;
        currentPosition = 0;
        pagerAdapter = new WantPagerAdapter(getChildFragmentManager(),
                3,
                mTypes, mTitles, mContents, mMoneys
                );
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == currentSize){

                    pagerAdapter.types.addAll(mTypes);
                    pagerAdapter.titles.addAll(mTitles);
                    pagerAdapter.moneys.addAll(mMoneys);
                    pagerAdapter.contents.addAll(mContents);
                    currentSize += 1;
                }
                currentPosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return root;
    }
}
