package materiallogin.ui.wanted;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class WantPagerAdapter extends FragmentStatePagerAdapter {


    public ArrayList<String> types;
    public ArrayList<String> titles;
    public ArrayList<String> contents;
    public ArrayList<String> moneys;
    int pages;

    public WantPagerAdapter(FragmentManager fm){
        super(fm);
        pages = 0;
        types = new ArrayList<>();
        titles = new ArrayList<>();
        contents = new ArrayList<>();
        moneys = new ArrayList<>();
    }

    /**
     * @deprecated
     * @param fm
     * @param pages
     * @param types
     * @param titles
     * @param contents
     * @param moneys
     */
    private WantPagerAdapter(FragmentManager fm,
                            int pages,
                            ArrayList<String> types,
            ArrayList<String> titles,
            ArrayList<String> contents,
            ArrayList<String> moneys) {
        super(fm);
        this.pages = pages;
        this.types = types;
        this.titles = titles;
        this.contents = contents;
        this.moneys = moneys;
    }

    @Override
    public Fragment getItem(int position) {

        int start = GridWantAdapter.pageMaxCnt * position;
        int end = Math.min(start + GridWantAdapter.pageMaxCnt, pages * GridWantAdapter.pageMaxCnt);

        return SimpleFragment.newInstance(
                end - start,
                new ArrayList<String>(types.subList(start, end)),
                new ArrayList<String>(titles.subList(start, end)),
                new ArrayList<String>(contents.subList(start, end)),
                new ArrayList<String>(moneys.subList(start, end)));
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "第" + String.valueOf(position) + "页";
    }

    @Override
    public int getCount() {
        return pages;
    }
}
