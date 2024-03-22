package com.bytesbee.provpnapp.activity;

import static com.bytesbee.provpnapp.managers.UsageManager.KEY_TOTAL_CONNECTIONS;
import static com.bytesbee.provpnapp.managers.UsageManager.KEY_TOTAL_TIME;
import static com.bytesbee.provpnapp.managers.UsageManager.STR_CONNECTIONS;
import static com.bytesbee.provpnapp.managers.UsageManager.STR_TIME;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.DialogManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.managers.UsageManager;
import com.bytesbee.provpnapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UsageActivity extends AppCompatActivity {
    private Activity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);
        activity = this;

        setupToolbar();

        final UsageManager usageManager = new UsageManager(this);

        final TextView txtAppInstalled = findViewById(R.id.txtAppInstalled);
        final TextView txtAppVersion = findViewById(R.id.txtAppVersion);
        txtAppVersion.setText(getString(R.string.lblAppVersion, Utils.getAppVersionName()));

        try {
            final String strDeviceCreated = SessionManager.get().getDeviceCreated();
            if (!strDeviceCreated.equals("null")) {
                final long deviceTime = Long.parseLong(strDeviceCreated);
                final long nowTime = System.currentTimeMillis();
                final long elapsedTime = nowTime - deviceTime;

                if (nowTime > deviceTime) {
                    if (elapsedTime >= 3_600_000 && elapsedTime < 7_200_000) {
                        txtAppInstalled.setText(getString(R.string.lblAppInstalled, TimeUnit.MILLISECONDS.toHours(elapsedTime) + " Hour Ago"));
                    } else if (elapsedTime >= 7_200_000 && elapsedTime < 86_400_000) {
                        txtAppInstalled.setText(getString(R.string.lblAppInstalled, TimeUnit.MILLISECONDS.toHours(elapsedTime) + " Hours Ago"));
                    } else if (elapsedTime >= 86_400_000 && elapsedTime < 172_800_000) {
                        txtAppInstalled.setText(getString(R.string.lblAppInstalled, TimeUnit.MILLISECONDS.toDays(elapsedTime) + " Day Ago"));
                    } else if (elapsedTime >= 172_800_000) {
                        txtAppInstalled.setText(getString(R.string.lblAppInstalled, TimeUnit.MILLISECONDS.toDays(elapsedTime) + " Days Ago"));
                    } else if (elapsedTime >= 120_000) {
                        txtAppInstalled.setText(getString(R.string.lblAppInstalled, TimeUnit.MILLISECONDS.toMinutes(elapsedTime) + " Minutes Ago"));
                    } else if (elapsedTime >= 60_000) {
                        txtAppInstalled.setText(getString(R.string.lblAppInstalled, TimeUnit.MILLISECONDS.toMinutes(elapsedTime) + " Minute ago"));
                    } else {//if (elapsedTime < 60_000) {
                        txtAppInstalled.setText(getString(R.string.lblAppInstalled, TimeUnit.MILLISECONDS.toSeconds(elapsedTime) + " Seconds Ago"));
                    }
                }
            }
        } catch (Exception e) {
            txtAppInstalled.setText(R.string.lblNA);
        }

        // today
        final Date Today = Calendar.getInstance().getTime();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        final String TODAY = df.format(Today);

        // yesterday
        final Calendar Cal1 = Calendar.getInstance();
        Cal1.add(Calendar.DATE, -1);
        final String YESTERDAY = df.format(new Date(Cal1.getTimeInMillis()));

        // three days
        final Calendar Cal2 = Calendar.getInstance();
        Cal2.add(Calendar.DATE, -2);
        final String THREEDAYS = df.format(new Date(Cal2.getTimeInMillis()));

        final String WEEK = String.valueOf(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
        final String MONTH = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
        final String YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        final long TODAY_USAGE = usageManager.getUsage(TODAY);
        final long YESTERDAY_USAGE = usageManager.getUsage(YESTERDAY);
        final long DAYTHREE_USAGE = usageManager.getUsage(THREEDAYS);
        final long WEEK_USAGE = usageManager.getUsage(WEEK + YEAR);
        final long MONTH_USAGE = usageManager.getUsage(MONTH + YEAR);

        final TextView txtUsageDataToday = findViewById(R.id.txtUsageDataToday);
        if (TODAY_USAGE == 0) {
            txtUsageDataToday.setText(R.string.lblNA);
        } else if (TODAY_USAGE <= 1000_000) {
            txtUsageDataToday.setText(getString(R.string.lblFormatKB, (TODAY_USAGE / 1000)));
        } else {
            txtUsageDataToday.setText(getString(R.string.lblFormatMB, (TODAY_USAGE / 1000_000)));
        }

        final TextView txtUsageDataYesterday = findViewById(R.id.txtUsageDataYesterday);
        if (YESTERDAY_USAGE == 0) {
            txtUsageDataYesterday.setText(R.string.lblNA);
        } else if (YESTERDAY_USAGE < 1000) {
            txtUsageDataYesterday.setText(R.string.lblOneKB);
        } else if (YESTERDAY_USAGE <= 1000_000) {
            txtUsageDataYesterday.setText(getString(R.string.lblFormatKB, (YESTERDAY_USAGE / 1000)));
        } else {
            txtUsageDataYesterday.setText(getString(R.string.lblFormatMB, (YESTERDAY_USAGE / 1000_000)));
        }

        final TextView txtUsageDataDayThreeDate = findViewById(R.id.txtUsageDataDayThreeDate);
        txtUsageDataDayThreeDate.setText(THREEDAYS);

        final TextView txtUsageDataDayThree = findViewById(R.id.txtUsageDataDayThree);
        if (DAYTHREE_USAGE == 0) {
            txtUsageDataDayThree.setText(R.string.lblNA);
        } else if (DAYTHREE_USAGE < 1000) {
            txtUsageDataDayThree.setText(R.string.lblOneKB);
        } else if (DAYTHREE_USAGE <= 1000_000) {
            txtUsageDataDayThree.setText(getString(R.string.lblFormatKB, (DAYTHREE_USAGE / 1000)));
        } else {
            txtUsageDataDayThree.setText(getString(R.string.lblFormatMB, (DAYTHREE_USAGE / 1000_000)));
        }

        final TextView txtUsageDataThisWeek = findViewById(R.id.txtUsageDataThisWeek);
        if (WEEK_USAGE == 0) {
            txtUsageDataThisWeek.setText(R.string.lblNA);
        } else if (WEEK_USAGE < 1000) {
            txtUsageDataThisWeek.setText(R.string.lblOneKB);
        } else if (WEEK_USAGE <= 1000_000) {
            txtUsageDataThisWeek.setText(getString(R.string.lblFormatKB, (WEEK_USAGE / 1000)));
        } else {
            txtUsageDataThisWeek.setText(getString(R.string.lblFormatMB, (WEEK_USAGE / 1000_000)));
        }

        final TextView txtUsageDataThisMonth = findViewById(R.id.txtUsageDataThisMonth);
        if (MONTH_USAGE == 0) {
            txtUsageDataThisMonth.setText(R.string.lblNA);
        } else if (MONTH_USAGE < 1000) {
            txtUsageDataThisMonth.setText(R.string.lblOneKB);
        } else if (MONTH_USAGE <= 1000_000) {
            txtUsageDataThisMonth.setText(getString(R.string.lblFormatKB, (MONTH_USAGE / 1000)));
        } else {
            txtUsageDataThisMonth.setText(getString(R.string.lblFormatMB, (MONTH_USAGE / 1000_000)));
        }

        final long timeToday = usageManager.getUsage(TODAY + STR_TIME);
        final long timeTesterday = usageManager.getUsage(YESTERDAY + STR_TIME);
        final long timeTotal = usageManager.getUsage(KEY_TOTAL_TIME);

        final String TodayTime = String.format(getString(R.string.string_of_two_number), (timeToday / (1000 * 60 * 60)) % 24) + ":" +
                String.format(getString(R.string.string_of_two_number), TimeUnit.MILLISECONDS.toMinutes(timeToday) % 60) + ":" +
                String.format(getString(R.string.string_of_two_number), (TimeUnit.MILLISECONDS.toSeconds(timeToday) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToday))));

        final String YesterdayTime = String.format(getString(R.string.string_of_two_number), (timeTesterday / (1000 * 60 * 60)) % 24) + ":" +
                String.format(getString(R.string.string_of_two_number), TimeUnit.MILLISECONDS.toMinutes(timeTesterday) % 60) + ":" +
                String.format(getString(R.string.string_of_two_number), (TimeUnit.MILLISECONDS.toSeconds(timeTesterday) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTesterday))));

        final String TotalTime = String.format(getString(R.string.string_of_two_number), (timeTotal / (1000 * 60 * 60)) % 24) + ":" +
                String.format(getString(R.string.string_of_two_number), TimeUnit.MILLISECONDS.toMinutes(timeTotal) % 60) + ":" +
                String.format(getString(R.string.string_of_two_number), (TimeUnit.MILLISECONDS.toSeconds(timeTotal) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTotal))));

        final TextView txtUsageTimeToday = findViewById(R.id.txtUsageTimeToday);
        final TextView txtUsageTimeYesterday = findViewById(R.id.txtUsageTimeYesterday);
        final TextView txtUsageTimeTotal = findViewById(R.id.txtUsageTimeTotal);

        txtUsageTimeToday.setText(TodayTime);
        txtUsageTimeYesterday.setText(YesterdayTime);
        txtUsageTimeTotal.setText(TotalTime);

        final long connectionsToday = usageManager.getUsage(TODAY + STR_CONNECTIONS);
        final long connectionsYesterday = usageManager.getUsage(YESTERDAY + STR_CONNECTIONS);
        final long connectionsTotal = usageManager.getUsage(KEY_TOTAL_CONNECTIONS);

        final TextView txtUsageConnectionToday = findViewById(R.id.txtUsageConnectionToday);
        final TextView txtUsageConnectionYesterday = findViewById(R.id.txtUsageConnectionYesterday);
        final TextView txtUsageConnectionTotal = findViewById(R.id.txtUsageConnectionTotal);

        txtUsageConnectionToday.setText(String.valueOf(connectionsToday));
        txtUsageConnectionYesterday.setText(String.valueOf(connectionsYesterday));
        txtUsageConnectionTotal.setText(String.valueOf(connectionsTotal));

        findViewById(R.id.imgDeleteUsage).setOnClickListener(v -> DialogManager.showYesNo(activity, R.string.lblDeleteTitle, R.string.msgDelete, () -> {
            usageManager.clearAll();
            recreate();
        }));
    }

    private void setupToolbar() {
        try {
            Toolbar mToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(view -> onBackPressed());
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.lblUsage);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
