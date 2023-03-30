package projectSE114.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import com.google.android.material.navigation.NavigationView;

import projectSE114.moneymanager.fragment.AccountFragment;
import projectSE114.moneymanager.fragment.CategoryFragment;
import projectSE114.moneymanager.fragment.ChartFragment;
import projectSE114.moneymanager.fragment.CurrencyFragment;
import projectSE114.moneymanager.fragment.HomeFragment;
import projectSE114.moneymanager.fragment.ReminderFragment;
import projectSE114.moneymanager.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_ACCOUNT = 1;
    private static final int FRAGMENT_CHART = 2;
    private static final int FRAGMENT_CATEGORY = 3;
    private static final int FRAGMENT_REMINDER = 4;
    private static final int FRAGMENT_CURRENCY = 5;
    private static final int FRAGMENT_SETTING = 6;

    private int currentFragment = FRAGMENT_HOME;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tạo toolbar chứa title là tên project
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Tạo nút mở drawer trên toolbar
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Bắt sự kiện khi click vào các mục tròn drawer
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Khi vào app thì mở home fragment
        replaceFragment(new HomeFragment());

        // Hiện nút home đã được chọn
        // Nếu ko chọn thì khi mở drawer ra sẽ không thấy nút home chuyển màu
        navigationView.getMenu().findItem(R.id.nav_home).setCheckable(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Hiển thị các layout tương ứng khi click vào trong mục menu
        switch (id) {
            case R.id.nav_home:
                if(currentFragment != FRAGMENT_HOME) {
                    replaceFragment(new HomeFragment());
                    currentFragment = FRAGMENT_HOME;
                }
                break;
            case R.id.nav_account:
                if(currentFragment != FRAGMENT_ACCOUNT) {
                    replaceFragment(new AccountFragment());
                    currentFragment = FRAGMENT_ACCOUNT;
                }
                break;
            case R.id.nav_chart:
                if(currentFragment != FRAGMENT_CHART) {
                    replaceFragment(new ChartFragment());
                    currentFragment = FRAGMENT_CHART;
                }
                break;
            case R.id.nav_category:
                if(currentFragment != FRAGMENT_CATEGORY) {
                    replaceFragment(new CategoryFragment());
                    currentFragment = FRAGMENT_CATEGORY;
                }
                break;
            case R.id.nav_currency:
                if(currentFragment != FRAGMENT_CURRENCY) {
                    replaceFragment(new CurrencyFragment());
                    currentFragment = FRAGMENT_CURRENCY;
                }
                break;
            case R.id.nav_reminder:
                if(currentFragment != FRAGMENT_REMINDER) {
                    replaceFragment(new ReminderFragment());
                    currentFragment = FRAGMENT_REMINDER;
                }
                break;
            case R.id.nav_setting:
                if(currentFragment != FRAGMENT_SETTING) {
                    replaceFragment(new SettingFragment());
                    currentFragment = FRAGMENT_SETTING;
                }
                break;
        }

        // Sau khi hiện layout được chọn thì đóng drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Nếu bấm nút Back trên đt thì kiểm tra drawer có đang mở không
    @Override
    public void onBackPressed() {
        // Nếu drawer đang mở thì đóng drawer
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // Nếu drawer đã đóng thì đóng app
        else {
            super.onBackPressed();
        }
    }

    // Hàm đưa fragment được chọn vào FrameLayout đã được tạo trước bên UI
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
}