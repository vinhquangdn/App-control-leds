package luandn.dt.ck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    FragmentRoom fragmentRoom;
    FragmentLogout fragmentLogout;
    FragmentSettings fragmentSettings;
    FragmentLogin fragmentLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();    // tao actionBar
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.background_toolbar)));

        // anh xa den cac bien
        FloatingActionButton fab = findViewById(R.id.fab);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        // tao actionBarDrawerToggle dung de chuyen doi menu khi mo va dong
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        // truyen actionBarDrawerToggle cho layout de su ly cac su kien khi dong mo menu
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();  // dong bo trang thai cua actionBarDrawerToggle

        // tao cac fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentLogout = new FragmentLogout();  // tao moi fragmentLogout
        fragmentLogin = new FragmentLogin(actionBar, actionBarDrawerToggle, fab, drawerLayout);

        fragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, fragmentLogin)    // thay the tren layout hien tai bang fragmentRoom
                .commit();   // bat dau thay the// tao moi fragmentHome

        // xu ly cac su kien tren navigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();   // tao fragmentTransaction

            if(item.getItemId() == R.id.menu_drawer_home) {
                fragmentRoom = fragmentLogin.getFragmentRoom();
                fragmentTransaction.replace(R.id.frameLayout, fragmentRoom);    // thay the tren layout hien tai bang fragmentRoom
                fragmentTransaction.commit();   // bat dau thay the
            }
            else if (item.getItemId() == R.id.menu_drawer_logout) {
                fragmentTransaction.replace(R.id.frameLayout, fragmentLogin);    // thay the tren layout hien tai bang fragmentRoom
                fragmentTransaction.commit();   // bat dau thay the
            }
            else if (item.getItemId() == R.id.menu_drawer_settings) {
                fragmentSettings = fragmentLogin.getFragmentRoom().getFragmentSetting();
                fragmentTransaction.replace(R.id.frameLayout, fragmentSettings);    // thay the tren layout hien tai bang fragmentRoom
                fragmentTransaction.commit();   // bat dau thay the
            }

            drawerLayout.close();   //dong menu

            return true;
        });
    }

    // xu ly su kien khi item trong menu duoc chon
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // neu nut dong mo duoc nhan thi dong mo drawer
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}