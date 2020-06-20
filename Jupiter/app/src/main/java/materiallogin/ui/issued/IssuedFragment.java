package materiallogin.ui.issued;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thu.qinghuaquan.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import materiallogin.ui.wanted.WantPagerAdapter;

public class IssuedFragment extends Fragment {

    ViewPager viewPager;
    WantPagerAdapter pagerAdapter;
    private Context mContext;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_issued, container, false);
        viewPager = root.findViewById(R.id.issue_pager);
        pagerAdapter = new WantPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(1);
//        viewPager.addOnPageChangeListener();
        return root;
    }


}
