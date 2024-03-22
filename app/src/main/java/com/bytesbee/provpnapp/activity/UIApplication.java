package com.bytesbee.provpnapp.activity;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.fcm.ApplicationLifecycleManager;
import com.bytesbee.provpnapp.managers.ConnectionManager;
import com.bytesbee.provpnapp.managers.HistoryManager;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.utils.Utils;

import java.util.Calendar;

import de.blinkt.openvpn.core.PRNGFixes;
import de.blinkt.openvpn.core.StatusListener;

public class UIApplication extends Application {
    public static String deviceId;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());
        SessionManager.init(this);
        HistoryManager.init(this);
        ConnectionManager.init(this);
        try {
            Utils.setDarkMode(SessionManager.get().isDarkModeOn());
        } catch (Exception e) {
            Utils.getErrors(e);
        }

        deviceId = SessionManager.get().getDeviceId();

        if (deviceId.equalsIgnoreCase("NULL")) {
            deviceId = getUniqueKey();
            SessionManager.get().setDeviceId(deviceId);
            SessionManager.get().setDeviceCreated(String.valueOf(System.currentTimeMillis()));
        }

        PRNGFixes.apply();
        StatusListener mStatus = new StatusListener();
        mStatus.init(getApplicationContext());
    }

    private String getUniqueKey() {
        Calendar now = Calendar.getInstance();
        final int year = now.get(Calendar.YEAR);
        final int month = now.get(Calendar.MONTH);
        final int day = now.get(Calendar.DAY_OF_MONTH);
        final int hour = now.get(Calendar.HOUR_OF_DAY);
        final int minute = now.get(Calendar.MINUTE);
        final int second = now.get(Calendar.SECOND);
        final int millis = now.get(Calendar.MILLISECOND);
        final String Time = getResources().getString(R.string.get_time, year, month, day, hour, minute, second, millis);

        final String strApi = String.valueOf(Build.VERSION.SDK_INT); // API
        final String strModel = String.valueOf(Build.MODEL); // Model
        final String strManufacturer = String.valueOf(Build.MANUFACTURER); // Manufacturer
        String version;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "00";
        }

        return Time + strManufacturer + strApi + strModel + version;
    }
}
