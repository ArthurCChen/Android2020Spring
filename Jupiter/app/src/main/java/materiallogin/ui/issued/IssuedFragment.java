package materiallogin.ui.issued;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thu.qinghuaquan.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class IssuedFragment extends Fragment {

    private IssuedViewModel issuedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        issuedViewModel =
                ViewModelProviders.of(this).get(IssuedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_issued, container, false);
        final TextView textView = root.findViewById(R.id.text_issued);
        issuedViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
