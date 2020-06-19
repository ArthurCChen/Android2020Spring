package materiallogin.ui.wanted;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ust.jupiter.jupiter.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class GridWantFragment extends Fragment {
    private String type;
    private String title;
    private String content;
    private Double money;

    static String TYPE = "type";
    static String TITLE = "title";
    static String CONTENT = "content";
    static String MONEY = "money";


    public static GridWantFragment newInstance(String type, String title, String content, Double money){
        GridWantFragment simpleFragment = new GridWantFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putString(TITLE, title);
        args.putString(CONTENT, content);
        args.putDouble(MONEY, money);
        simpleFragment.setArguments(args);
        return simpleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = args.getString(TYPE);
        title = args.getString(TITLE);
        content = args.getString(CONTENT);
        money = args.getDouble(MONEY);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wants, container, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvContent = (TextView) view.findViewById(R.id.content);
        TextView tvMoney = (TextView) view.findViewById(R.id.money);
        ImageView imageView = (ImageView) view.findViewById(R.id.want_image);

        tvTitle.setText(title);
        tvContent.setText(content);
        tvMoney.setText(String.valueOf(money) + "元");
        if(type.equals(getResources().getString(R.string.type_other))){
            imageView.setImageResource(R.drawable.other);
        }else if(type.equals(getResources().getString(R.string.type_express))){
            imageView.setImageResource(R.drawable.express);
        }else if(type.equals(getResources().getString(R.string.type_experiment))){
            imageView.setImageResource(R.drawable.experiment);
        }else if(type.equals(getResources().getString(R.string.type_deal))){
            imageView.setImageResource(R.drawable.deal);
        }else if(type.equals(getResources().getString(R.string.type_ask))){
            imageView.setImageResource(R.drawable.ask);
        }else{
            throw new RuntimeException("not valid type string");
        }
        return view;

    }
}
