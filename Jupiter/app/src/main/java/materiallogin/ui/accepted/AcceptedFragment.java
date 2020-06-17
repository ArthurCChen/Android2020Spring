package materiallogin.ui.accepted;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ust.jupiter.jupiter.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class AcceptedFragment extends Fragment {

    private AcceptedViewModel acceptedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        acceptedViewModel =
                ViewModelProviders.of(this).get(AcceptedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_accepted, container, false);
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
