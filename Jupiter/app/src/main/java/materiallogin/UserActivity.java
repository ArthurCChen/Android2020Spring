package materiallogin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thu.qinghuaquan.R;

public class UserActivity extends AppCompatActivity {

    public static UserActivity ins;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ins = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.exit) {
            new SPutil(this).WriteName("");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        if (item.getItemId() == R.id.xx) {
            startActivity(new Intent(this, ChatsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cpgl(View v) {

    }

    public void ddgl(View v) {
        startActivity(new Intent(this, ShangjiaActivity.class));
    }

    public void zwgl(View v) {
    }

    public void grxx(View v) {
        startActivity(new Intent(this, XsActivity.class));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

}
