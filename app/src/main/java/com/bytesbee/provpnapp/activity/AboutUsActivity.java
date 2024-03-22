package com.bytesbee.provpnapp.activity;


import static com.bytesbee.provpnapp.constants.IConstants.FALSE;
import static com.bytesbee.provpnapp.constants.IConstants.HANDLER_SUCCESS;
import static com.bytesbee.provpnapp.constants.IConstants.TRUE;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.AdsManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.models.Setting;
import com.bytesbee.provpnapp.utils.Utils;


/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link https://bytesbee.com
 */
public class AboutUsActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity mActivity;

    private TextView txtAppName, txtVersion, txtCompany, txtEmail, txtWebsite, txtContact;
    private ImageView imgAppLogo;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mActivity = this;

        txtAppName = findViewById(R.id.txtAppName);
        txtVersion = findViewById(R.id.txtVersion);
        txtCompany = findViewById(R.id.txtCompany);
        txtEmail = findViewById(R.id.txtEmail);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtContact = findViewById(R.id.txtContact);
        imgAppLogo = findViewById(R.id.imgAppLogo);
        webView = findViewById(R.id.webView);
        webView.setBackgroundColor(0);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultFontSize(14);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                if (Utils.isOnline(getApplicationContext())) {
                    swipeProgress(TRUE);
                    Utils.requestSettingApi(getApplicationContext(), dataHandler);
                }
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        });

        setupToolbar();
        setupData();
    }

    private final Handler dataHandler = new Handler(message -> {
        if (message.what == HANDLER_SUCCESS) {
            setupData();
        }
        return true;
    });

    private void setupData() {
        try {
            swipeProgress(FALSE);
            Setting setting = SessionManager.get().getSettingModel();
            if (setting != null) {

                AdsManager.showBannerAd(mActivity);

                txtAppName.setText(setting.getApp_name());
                txtVersion.setText(setting.getApp_version());
                txtCompany.setText(setting.getApp_author());
                txtEmail.setText(setting.getApp_email());
                txtWebsite.setText(setting.getApp_website());
                txtContact.setText(setting.getApp_contact());

                Utils.setSettingImageView(mActivity, setting, imgAppLogo);

                webView.loadUrl("");
                String mimeType = "text/html;charset=UTF-8";
                String encoding = "utf-8";
                String htmlText = setting.getApp_description();
                String rtl = SessionManager.get().isRTLOn() ? "dir=\"rtl\"" : "";
                String text = "<html><head>"
                        + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_res/font/poppins_regular.otf\")}body{font-family: MyFont;color: #8D8D8D;}"
                        + "</style></head>"
                        + "<body " + rtl + ">"
                        + htmlText
                        + "</body></html>";

                webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void setupToolbar() {
        try {
            final Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(TRUE);
                getSupportActionBar().setHomeButtonEnabled(TRUE);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void swipeProgress(final boolean show) {
        try {
            if (!show) {
                swipeRefreshLayout.setRefreshing(FALSE);
                return;
            }
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(TRUE));
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(FALSE);
    }
}
