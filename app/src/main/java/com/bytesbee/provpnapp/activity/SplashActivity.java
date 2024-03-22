package com.bytesbee.provpnapp.activity;

import static com.bytesbee.provpnapp.constants.IConstants.BILL_DONE;
import static com.bytesbee.provpnapp.constants.IConstants.DELAY_MILLIS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.ProductDetails;
import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.BillingManager;
import com.bytesbee.provpnapp.managers.ScreenManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.utils.Utils;

import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements BillingManager.BillingResultHandler, BillingManager.ProductDetailsListener {

    private BillingManager billingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //make translucent statusBar on kitkat devices
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //make fully Android Transparent Status bar
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_splash);
        Utils.requestSettingApi(this, dataHandler);
    }

    private final Handler dataHandler = new Handler(message -> {
        initBilling();
        return true;
    });

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void openMainActivity() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            Class<?> cls;
            if (SessionManager.get().isOnBoardingDone()) {
                cls = HomeActivity.class;
            } else {
                cls = OnBoardingActivity.class;
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            ScreenManager.showClearTopScreen(getApplicationContext(), cls);
        }, DELAY_MILLIS);
    }

    private void initBilling() {
        billingManager = new BillingManager(this);
        billingManager.setCallback(this, this);
        billingManager.startConnection();
    }

    @Override
    public void showResultMsg(String message) {
        if (message.equalsIgnoreCase(BILL_DONE)) {
            billingManager.checkSubscription();
        } else {
            SessionManager.get().setPremiumUser(false);
        }
        openMainActivity();
    }

    @Override
    public void productDetailsList(List<ProductDetails> productDetailsList) {
        Utils.sout("SkuDetailList: " + productDetailsList.size() + " >> " + productDetailsList);
    }

    @Override
    protected void onDestroy() {
        if (billingManager != null)
            billingManager.closeConnection();
        super.onDestroy();
    }

}