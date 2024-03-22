package com.bytesbee.provpnapp.activity;


import static com.bytesbee.provpnapp.constants.IConstants.TRUE;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.AdsManager;
import com.bytesbee.provpnapp.managers.DialogManager;
import com.bytesbee.provpnapp.utils.Utils;

import de.blinkt.openvpn.core.LogItem;
import de.blinkt.openvpn.core.VpnStatus;

public class LogsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView txtLogs;
    private ImageView imgClear;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        init();
        setupToolbar();
        initListeners();
        initData();
        AdsManager.showBannerAd(this);
    }

    public void init() {
        imgClear = findViewById(R.id.imgClear);
        txtLogs = findViewById(R.id.txtLogs);
        animationView = findViewById(R.id.animationView);
    }

    private void setupToolbar() {
        try {
            final Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(TRUE);
                getSupportActionBar().setHomeButtonEnabled(TRUE);
            }
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    public void initListeners() {
        imgClear.setOnClickListener(this);
    }

    public void initData() {
        final StringBuilder stringBuffer = new StringBuilder();
        if (VpnStatus.getlogbuffer().length > 0) {
            animationView.setVisibility(View.GONE);
            txtLogs.setVisibility(View.VISIBLE);
            LogItem[] logItems = VpnStatus.getlogbuffer();
            for (int i = logItems.length; i >= 1; i--) {
                stringBuffer.append(logItems[i - 1]).append("\n");
            }
            txtLogs.setText(stringBuffer.toString());
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgClear) {
            /* clear all log data and set text empty */
            DialogManager.showYesNo(LogsActivity.this, R.string.lblDeleteTitle, R.string.msgClearLogs, () -> {
                VpnStatus.clearLog();
                txtLogs.setText("");
                animationView.setVisibility(View.VISIBLE);
                txtLogs.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

}