package materiallogin.ui.wanted;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;

import com.thu.qinghuaquan.R;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Created by arvin on 2016/8/30 16:16
 */
public class SimpleFragment extends Fragment {
    private View mRoot;
    private GridView gridView;
    private int color;

    int numbers;
    ArrayList<String> types;
    ArrayList<String> titles;
    ArrayList<String> contents;
    ArrayList<String> moneys;

    static String NUMBERS = "numbers";
    static String TYPES = "types";
    static String TITLES = "titles";
    static String CONTENTS = "contents";
    static String MONEYS = "moneys";

    public SimpleFragment() {
        System.out.println("fuck you android");
    }

    public static SimpleFragment newInstance(int numbers,
                                             ArrayList<String> types,
                                             ArrayList<String> titles,
                                             ArrayList<String> contents,
                                             ArrayList<String> moneys){
        SimpleFragment simpleFragment = new SimpleFragment();
        Bundle args = new Bundle();
        args.putInt(NUMBERS, numbers);
        args.putStringArrayList(TYPES, types);
        args.putStringArrayList(TITLES, titles);
        args.putStringArrayList(CONTENTS, contents);
        args.putStringArrayList(MONEYS, moneys);
        simpleFragment.setArguments(args);
        return simpleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        numbers = args.getInt(NUMBERS);
        titles = args.getStringArrayList(TITLES);
        contents = args.getStringArrayList(CONTENTS);
        types = args.getStringArrayList(TYPES);
        moneys = args.getStringArrayList(MONEYS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRoot = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_simple, null);
        gridView = mRoot.findViewById(R.id.layout_page);
        gridView.setAdapter(new GridWantAdapter(getActivity(),
                numbers,
                types,
                titles,
                contents,
                moneys));
        init(savedInstanceState);
        return mRoot;
    }

    private void init(Bundle savedInstanceState) {
        mRoot.findViewById(R.id.layout_page).setBackgroundColor(color);
    }

}
