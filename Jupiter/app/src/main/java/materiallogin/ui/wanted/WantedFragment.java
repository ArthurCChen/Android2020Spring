package materiallogin.ui.wanted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.jupiter.jupiter.R;

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
    private PagerAdapter pagerAdapter;
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
        final TextView textView = root.findViewById(R.id.text_wanted);
        wantedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        viewPager  = (ViewPager) root.findViewById(R.id.wants_pager);
//        viewPager = (ViewPager) inflater.inflate(R.layout.fragment_wants_list, container, false);
        List<Integer> mItems = Arrays.asList(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.black),
                getResources().getColor(R.color.colorPrimaryDark),
                getResources().getColor(R.color.colorPrimary));
        currentSize = mItems.size();
        currentPosition = 0;
        pagerAdapter = new WantPagerAdapter(getChildFragmentManager(), mItems);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(-1);

        return root;
    }
}
