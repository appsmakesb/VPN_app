package com.bytesbee.provpnapp.activity;

import static com.bytesbee.provpnapp.constants.IConstants.FALSE;
import static com.bytesbee.provpnapp.constants.IConstants.TRUE;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.AdsManager;
import com.bytesbee.provpnapp.managers.DialogManager;
import com.bytesbee.provpnapp.managers.ScreenManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.utils.Utils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private SwitchCompat notificationOnOff, rtlOnOff, darkOnOff;
    private AppCompatActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mActivity = this;

        try {
            Toolbar mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(view -> onBackPressed());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final LinearLayout layoutNotification = findViewById(R.id.layoutNotification);
        final LinearLayout layoutRTL = findViewById(R.id.layoutRTL);
        final LinearLayout layoutDarkMode = findViewById(R.id.layoutDarkMode);

        final LinearLayout layoutRateApp = findViewById(R.id.layoutRateApp);
        final LinearLayout layoutShare = findViewById(R.id.layoutShare);
        final LinearLayout layoutTerms = findViewById(R.id.layoutTerms);
        final LinearLayout layoutPrivacyPolicy = findViewById(R.id.layoutPrivacyPolicy);

        final TextView txtSettingVersion = findViewById(R.id.txtSettingVersion);
        txtSettingVersion.setText(String.format(getString(R.string.lblSettingVersion), Utils.getAppVersionName()));

        rtlOnOff = findViewById(R.id.rtlOnOff);
        rtlOnOff.setOnClickListener(v -> restartApp());
        rtlOnOff.setOnCheckedChangeListener((compoundButton, b) -> SessionManager.get().setOnOffRTL(b));

        darkOnOff = findViewById(R.id.darkOnOff);
        darkOnOff.setOnCheckedChangeListener((compoundButton, b) -> {
            SessionManager.get().setDarkMode(b);
            Utils.setDarkMode(b);
        });

        notificationOnOff = findViewById(R.id.notificationOnOff);
        notificationOnOff.setOnCheckedChangeListener((compoundButton, b) -> SessionManager.get().setOnOffNotification(b));

        notificationOnOff.setChecked(SessionManager.get().isNotificationOn());
        rtlOnOff.setChecked(SessionManager.get().isRTLOn());
        darkOnOff.setChecked(SessionManager.get().isDarkModeOn());


        layoutNotification.setOnClickListener(this);
        layoutRTL.setOnClickListener(this);
        layoutDarkMode.setOnClickListener(this);
        layoutRateApp.setOnClickListener(this);
        layoutShare.setOnClickListener(this);
        layoutTerms.setOnClickListener(this);
        layoutPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.layoutNotification) {
            if (notificationOnOff.isChecked()) {
                notificationOnOff.setChecked(FALSE);
            } else {
                notificationOnOff.setChecked(TRUE);
            }
        } else if (id == R.id.layoutRTL) {
            if (rtlOnOff.isChecked()) {
                rtlOnOff.setChecked(FALSE);
            } else {
                rtlOnOff.setChecked(TRUE);
            }
            restartApp();
        } else if (id == R.id.layoutDarkMode) {
            if (darkOnOff.isChecked()) {
                darkOnOff.setChecked(FALSE);
            } else {
                darkOnOff.setChecked(TRUE);
            }
        } else if (id == R.id.layoutRateApp) {
            Utils.rateApp(mActivity);
        } else if (id == R.id.layoutShare) {
            Utils.shareApp(mActivity);
        } else if (id == R.id.layoutPrivacyPolicy) {
//            Screens.showCustomScreen(mActivity, PrivacyPolicyActivity.class);
            AdsManager.showIntAds(mActivity, PrivacyPolicyActivity.class);
        } else if (id == R.id.layoutTerms) {
//            Screens.showCustomScreen(mActivity, AboutUsActivity.class);
            AdsManager.showIntAds(mActivity, AboutUsActivity.class);
        }
    }

    private void restartApp() {
        DialogManager.showOK(mActivity, R.string.lblRefresh, R.string.msgRefreshMessage, () -> ScreenManager.showClearTopScreen(mActivity, HomeActivity.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
