package materiallogin;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.thu.qinghuaquan.R;

import org.w3c.dom.Text;

import java.util.List;

import cn.leancloud.AVObject;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.MyViewHolder> {

    private List<AVObject> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        Button accpet;
        Button reject;
        public MyViewHolder(ConstraintLayout v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.enroller_intro);
            accpet = (Button) v.findViewById(R.id.accept_button);
            reject = (Button) v.findViewById(R.id.reject_button);
        }

        public void set(AVObject relationship) {
            String hint = String.format("<br><p>%s向您发来申请</p>", relationship.getString("enroller_id"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(hint, Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView.setText(Html.fromHtml(hint));
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ApplicationAdapter(List<AVObject> mDataset) {
        this.mDataset = mDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ApplicationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.application_card, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.set(mDataset.get(position));
        holder.accpet.setOnClickListener(v -> {
            AVObject p = mDataset.get(position);
            p.put("enroller_state", "accepted");
            p.saveInBackground().subscribe();
            mDataset.remove(position);
            notifyItemRemoved(position);
        });
        holder.reject.setOnClickListener(v -> {
            AVObject p = mDataset.get(position);
            p.put("enroller_state", "rejected");
            p.saveInBackground().subscribe();
            AVObject demand = (AVObject) p.get("demand");
            demand.increment("confirmed_number");
            demand.saveInBackground().subscribe();
            mDataset.remove(position);
            notifyItemRemoved(position);
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}