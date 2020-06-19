package materiallogin.ui.wanted;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class WantPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> items;

    public WantPagerAdapter(FragmentManager fm, List<Integer> colors) {
        super(fm);
        items = colors;
    }

    @Override
    public Fragment getItem(int position) {

        return SimpleFragment.newInstance(items.get(position));
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "第" + String.valueOf(position) + "页";
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
