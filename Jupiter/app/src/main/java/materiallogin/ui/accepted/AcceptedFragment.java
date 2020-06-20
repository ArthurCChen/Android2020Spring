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
                demand_page.putExtra("objectId", "5eec5cd68ebe650008845ca3");
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
