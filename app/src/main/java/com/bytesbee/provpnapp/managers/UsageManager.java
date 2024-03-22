package com.bytesbee.provpnapp.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class UsageManager {

    private final SharedPreferences pref;
    private static final String PREF_NAME = "UsageHistory_";
    public static final String KEY_TOTAL_TIME = "totalTime";
    public static final String KEY_TOTAL_CONNECTIONS = "totalConnections";

    public static final String STR_CONNECTIONS = "_connections";
    public static final String STR_TIME = "_time";

    public UsageManager(final Context context) {
        pref = context.getSharedPreferences(PREF_NAME + context.getPackageName(), Context.MODE_PRIVATE);
    }

    public long getUsage(String key) {
        return pref.getLong(key, 0);
    }

    public void setUsage(String key, long value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void clearAll() {
        final SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
