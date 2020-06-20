package materiallogin.ui.accepted;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.thu.qinghuaquan.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Date;

import materiallogin.AVDemand;
import materiallogin.DemandDetailActivity;
import materiallogin.ui.issued.IssuedFragment;

public class AcceptedFragment extends Fragment {

    private AcceptedViewModel acceptedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        acceptedViewModel =
                ViewModelProviders.of(this).get(AcceptedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_accepted, container, false);

        // test code, to provide an entrance for demand detail activity, delete when it's the time
        Button test_button = (Button)root.findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent demand_page = new Intent(getActivity(), DemandDetailActivity.class);
                AVDemand demand = new AVDemand();
                demand_page.putExtra("title", "桃子滞销，帮帮老农");
                demand_page.putExtra("content", "免费请你吃桃子");
                demand_page.putExtra("demand_state", "inactive");
                demand_page.putExtra("end_time", new Date().toString());
                demand_page.putExtra("reward", 2.5f);
                demand_page.putExtra("wanted_number", 15);
                demand_page.putExtra("confirmed_number", 0);
                demand_page.putExtra("nickname", "张三");
                demand_page.putExtra("role", "issuer"); // issuer or taker(already applied) or viewer(not applied)
                // 5 cases: pending_review_for_enroll_demand, accepted, rejected, done, pending_review_for_quit_demand, quitted
                // valid only when role is taker
                demand_page.putExtra("enroller_state", "accepted");
                startActivity(demand_page);
            }
        });

        final TextView textView = root.findViewById(R.id.text_accepted);
        acceptedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
