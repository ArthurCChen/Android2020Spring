package materiallogin;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ust.jupiter.jupiter.R;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import materiallogin.ui.issued.IssuedFragment;
import materiallogin.ui.accepted.AcceptedFragment;
import materiallogin.ui.wanted.WantedFragment;
import materiallogin.ui.me.MeFragment;

public class BottomMenu extends AppCompatActivity {

    WantedFragment wantedFragment;
    AcceptedFragment acceptedFragment;
    MeFragment meFragment;
    IssuedFragment issuedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wantedFragment = new WantedFragment();
        acceptedFragment = new AcceptedFragment();
        meFragment = new MeFragment();
        issuedFragment = new IssuedFragment();

        setContentView(R.layout.activity_bottom_menu);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_wanted, R.id.navigation_accepted,
                R.id.navigation_issued, R.id.navigation_me)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

//        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.navigation_wanted:
//                        loadFragment(wantedFragment);
//                        return true;
//                    case R.id.navigation_accepted:
//                        loadFragment(acceptedFragment);
//                        return true;
//                    case R.id.navigation_issued:
//                        loadFragment(issuedFragment);
//                        return true;
//                    case R.id.navigation_me:
//                        loadFragment(meFragment);
//                        return true;
//
//                }
//                return false;
//            }
//        });
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
    }

}
