package materiallogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thu.qinghuaquan.R;

import java.util.List;

public class MsgAdapter extends ArrayAdapter<ChatMsg> implements View.OnClickListener {

    private int resourceId;
    private String myid;

    public MsgAdapter(Context context, int textViewResourceId, List<ChatMsg> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        myid = (String) SPUtils.getString("username");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMsg msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            viewHolder.name_l = (TextView) view.findViewById(R.id.name_le);
            viewHolder.name_r = (TextView) view.findViewById(R.id.name);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (!msg.getFrom().equals(myid)) {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.name_l.setVisibility(View.VISIBLE);
            viewHolder.name_r.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
            viewHolder.name_l.setText(msg.getName());
        } else {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.name_l.setVisibility(View.GONE);
            viewHolder.name_r.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getContent());
            viewHolder.name_r.setText("自己");
        }
        viewHolder.name_l.setTag(msg);
        viewHolder.name_r.setTag(msg);
        viewHolder.name_l.setOnClickListener(this);
        viewHolder.name_r.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(final View view) {

    }

    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView name_l;
        TextView name_r;
    }
}
