package materiallogin.ui.wanted;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class WantPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> items;

    public WantPagerAdapter(FragmentManager fm, List<Integer> items) {
        super(fm);
        this.items = items;

    }

    @Override
    public Fragment getItem(int position) {
        if(position == items.size() + 1){
            position = 0;
        }else if(position == 0){
            position = items.size() - 1;
        }else{
            position -= 1;
        }
        return new SimpleFragment(items.get(position));
    }

    @Override
    public int getCount() {
        return items.size() + 2;
    }
}
