package materiallogin.ui.wanted;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thu.qinghuaquan.R;

import java.util.ArrayList;

import materiallogin.DemandDetailActivity;

public class GridWantAdapter extends BaseAdapter {
    public static int pageMaxCnt = 6;
    int numbers;
    Context context;
    ArrayList<String> types;
    ArrayList<String> titles;
    ArrayList<String> contents;
    ArrayList<String> moneys;
    ArrayList<String> ids;

    public GridWantAdapter(
            Context context,
            int numbers,
            ArrayList<String> types,
            ArrayList<String> titles,
            ArrayList<String> contents,
            ArrayList<String> moneys,
            ArrayList<String> ids
    ){
        this.numbers = numbers;
        this.context = context;
        this.types = types;
        this.titles = titles;
        this.contents = contents;
        this.moneys = moneys;
        this.ids = ids;
    }

    @Override
    public int getCount() {
        return numbers;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.fragment_wants, null);
        }


        String title = titles.get(position);
        String content = contents.get(position);
        String money = moneys.get(position);
        String type = types.get(position);
        String id = ids.get(position);
        View view = convertView;

        // TODO 打开 demand 的 onClick 入口
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DemandDetailActivity.class);
                intent.putExtra("objectId", id);
                System.out.println(id);
                context.startActivity(intent);
            }
        });

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvContent = (TextView) view.findViewById(R.id.content);
        TextView tvMoney = (TextView) view.findViewById(R.id.money);
        ImageView imageView = (ImageView) view.findViewById(R.id.want_image);

        tvTitle.setText(title);
        tvContent.setText(content);
        tvMoney.setText(money + "元");
        if(type.equals(context.getResources().getString(R.string.type_other))){
            imageView.setImageResource(R.drawable.other);
        }else if(type.equals(context.getResources().getString(R.string.type_express))){
            imageView.setImageResource(R.drawable.express);
        }else if(type.equals(context.getResources().getString(R.string.type_experiment))){
            imageView.setImageResource(R.drawable.experiment);
        }else if(type.equals(context.getResources().getString(R.string.type_deal))){
            imageView.setImageResource(R.drawable.deal);
        }else if(type.equals(context.getResources().getString(R.string.type_ask))){
            imageView.setImageResource(R.drawable.ask);
        }else{
            throw new RuntimeException("not valid type string");
        }
        return view;

    }
}
