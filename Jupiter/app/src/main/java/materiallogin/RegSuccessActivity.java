package materiallogin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.transition.Explode;
import android.widget.TextView;

import com.ust.jupiter.jupiter.R;

public class RegSuccessActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_success);

        tv = (TextView) findViewById(R.id.tv);
        tv.setText("注册成功，手机号："+getIntent().getStringExtra("id"));

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
    }
}
