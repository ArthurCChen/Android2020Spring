package materiallogin.ui.wanted;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class WantPagerAdapter extends FragmentStatePagerAdapter {


    public ArrayList<String> types;
    public ArrayList<String> titles;
    public ArrayList<String> contents;
    public ArrayList<String> moneys;
    public ArrayList<String> ids;
    public int items;

    public WantPagerAdapter(FragmentManager fm){
        super(fm);
        items = 0;
        types = new ArrayList<>();
        titles = new ArrayList<>();
        contents = new ArrayList<>();
        moneys = new ArrayList<>();
        ids = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {

        int start = GridWantAdapter.pageMaxCnt * position;
        int end = Math.min(start + GridWantAdapter.pageMaxCnt, items);

        return SimpleFragment.newInstance(
                end - start,
                new ArrayList<String>(types.subList(start, end)),
                new ArrayList<String>(titles.subList(start, end)),
                new ArrayList<String>(contents.subList(start, end)),
                new ArrayList<String>(moneys.subList(start, end)),
                new ArrayList<String>(ids.subList(start, end)));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "第" + String.valueOf(position) + "页";
    }

    @Override
    public int getCount() {
        return items == 0? 0: (items + 5) / GridWantAdapter.pageMaxCnt;
    }

}
