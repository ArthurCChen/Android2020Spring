package materiallogin;


import android.os.Bundle;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GridWantFragment extends Fragment {
    private GridView gridWants;
    private List<Map<String, Object>> dataLists;
    private SimpleAdapter simpleAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
