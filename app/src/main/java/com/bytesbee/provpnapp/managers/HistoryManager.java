package com.bytesbee.provpnapp.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class HistoryManager {

    private final SharedPreferences pref;
    private static final String PREF_NAME = "historydata_v1";
    public static final String KEY_DATA = "DATA";

    //============== START
    private static HistoryManager mInstance;

    public static HistoryManager get() {
        return mInstance;
    }

    public static void init(Context ctx) {
        if (mInstance == null) mInstance = new HistoryManager(ctx);
    }
    //============== END

    public HistoryManager(final Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getHistory() {
        return pref.getString(KEY_DATA, "");
    }

    public void setHistory(String value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DATA, value);
        editor.apply();
    }

    public void clearAll() {
        final SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
