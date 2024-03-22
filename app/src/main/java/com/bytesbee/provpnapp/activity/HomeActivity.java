package com.bytesbee.provpnapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.fcm.NotificationUtils;
import com.bytesbee.provpnapp.fragments.HomeFragment;
import com.bytesbee.provpnapp.managers.AdsManager;
import com.bytesbee.provpnapp.managers.ScreenManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.shreyaspatil.material.navigationview.MaterialNavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppCompatActivity mActivity;
    private long exitTime = 0;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private MaterialNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mActivity = this;
        AdsManager.initializeMobileAds(mActivity);

        final ImageView imgPurchase = findViewById(R.id.imgPurchase);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupToolbar();
        setupNavDrawer();
        openHomeFragment();

        AdsManager.firstLoadAds(mActivity);

        final NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.checkFCMTokenUpdated();

        if (SessionManager.get().isPremiumUser()) {
            imgPurchase.setVisibility(View.GONE);
        } else {
            imgPurchase.setVisibility(View.VISIBLE);
            imgPurchase.setOnClickListener(view -> ScreenManager.showUpgradeActivity(mActivity));
        }
    }

    private void setupToolbar() {
        try {
            toolbar = findViewById(R.id.toolbar);
            if (toolbar == null) {
                return;
            }
            ActionBar actionBar = getSupportActionBar();
            if (actionBar == null) return;

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNavDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.strNavOpen, R.string.strNavClose);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final String mnuName = item.toString().trim();
        if (mnuName.equalsIgnoreCase(mActivity.getString(R.string.strSelectServer))) {
            ScreenManager.showCustomScreen(mActivity, SelectServersActivity.class);
        } else if (mnuName.equalsIgnoreCase(mActivity.getString(R.string.lblConnHistory))) {
            ScreenManager.showCustomScreen(mActivity, ConnectionHistoryActivity.class);
        } else if (mnuName.equalsIgnoreCase(mActivity.getString(R.string.lblUsageHistory))) {
            ScreenManager.showCustomScreen(mActivity, UsageActivity.class);
        } else if (mnuName.equalsIgnoreCase(mActivity.getString(R.string.lblSpeedTest))) {
            ScreenManager.showCustomScreen(mActivity, SpeedTestActivity.class);
        } else if (mnuName.equalsIgnoreCase(mActivity.getString(R.string.lblLogs))) {
            ScreenManager.showCustomScreen(mActivity, LogsActivity.class);
        } else if (mnuName.equalsIgnoreCase(getString(R.string.lblSettings))) {
            ScreenManager.showCustomScreen(mActivity, SettingsActivity.class);
        }

        return true;
    }

    private HomeFragment homeFragment;

    private void openHomeFragment() {
        try {
            homeFragment = new HomeFragment().newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, homeFragment, mActivity.getString(R.string.app_name))
                    .commit();
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isOpen()) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return;
            }
        }
        final int DEFAULT_DELAY = 2000;
        if ((System.currentTimeMillis() - exitTime) > DEFAULT_DELAY) {
            try {
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.mainRootLayout), getString(R.string.press_again_to_exit), Snackbar.LENGTH_LONG);
                final View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimaryDark));
                final TextView textView = sbView.findViewById(R.id.snackbar_text);
                textView.setTypeface(Utils.getRegularFont(mActivity));
                textView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorSnackBar));
                snackbar.show();
            } catch (Exception e) {
                Utils.getErrors(e);
                Utils.showToast(mActivity, getString(R.string.press_again_to_exit));
            }
            exitTime = System.currentTimeMillis();
        } else {
            if (homeFragment != null) {
                if (homeFragment.isVpnRunning()) {
                    this.moveTaskToBack(true);
                    return;
                }
            }
            finish();
            super.onBackPressed();
        }
    }
}