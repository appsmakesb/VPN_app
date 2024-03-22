package com.bytesbee.provpnapp.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.bytesbee.provpnapp.models.Setting;
import com.bytesbee.provpnapp.models.VPNServers;
import com.google.gson.Gson;

public class SessionManager {

    private final SharedPreferences pref;
    private static final String PREF_NAME = "BytesBeeProVPN_";
    private static final String KEY_DEVICE_CREATED = "deviceCreated";
    private static final String KEY_DEVICE_ID = "deviceId";
    private static final String KEY_ENTRY_DONE = "statusDone";
    private static final String KEY_FCM_TOKEN = "fcmToken";
    private static final String KEY_MODEL_SETTING_DATA = "setting_data";
    private static final String KEY_SERVER_OBJ = "ServerObj";
    private static final String KEY_ON_OFF_DARK_MODE = "onOffDarkMode";
    private static final String KEY_ON_OFF_NOTIFICATION = "onOffNotification";
    private static final String KEY_ON_OFF_RTL = "onOffRTL";
    private static final String KEY_PREMIUM_USER = "isPremiumUser";

    //============== START
    private static SessionManager mInstance;

    public static SessionManager get() {
        return mInstance;
    }

    public static void init(Context ctx) {
        if (mInstance == null) mInstance = new SessionManager(ctx);
    }
    //============== END


    public SessionManager(final Context context) {
        pref = context.getSharedPreferences(PREF_NAME + context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void setDeviceId(String device) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DEVICE_ID, device);
        editor.apply();
    }

    public String getDeviceId() {
        return pref.getString(KEY_DEVICE_ID, "null");
    }

    public void setDeviceCreated(String device) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_DEVICE_CREATED, device);
        editor.apply();
    }

    public String getDeviceCreated() {
        return pref.getString(KEY_DEVICE_CREATED, "null");
    }

    public void setOnBoardingDone() {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_ENTRY_DONE, true);
        editor.apply();
    }

    public boolean isOnBoardingDone() {
        return pref.getBoolean(KEY_ENTRY_DONE, false);
    }

    public String getFCMToken() {
        return pref.getString(KEY_FCM_TOKEN, "");
    }

    public void setFCMToken(String value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_FCM_TOKEN, value);
        editor.apply();
    }

    public Setting getSettingModel() {
        String data = pref.getString(KEY_MODEL_SETTING_DATA, "");
        return new Gson().fromJson(data, Setting.class);
    }

    public void setSettingModel(Setting user) {
        final SharedPreferences.Editor editor = pref.edit();
        String json = new Gson().toJson(user);
        editor.putString(KEY_MODEL_SETTING_DATA, json);
        editor.apply();
    }

    /**
     * save server object to session
     *
     * @param server server object to be saved to session
     */
    public void saveServer(VPNServers server) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_SERVER_OBJ, new Gson().toJson(server));
        editor.apply();
    }

    /**
     * returns the object of  server saved in session
     */
    public VPNServers getServer() {
        try {
            return new Gson().fromJson(pref.getString(KEY_SERVER_OBJ, null), VPNServers.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void setDarkMode(final boolean value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_ON_OFF_DARK_MODE, value);
        editor.apply();
    }

    public boolean isDarkModeOn() {
        return pref.getBoolean(KEY_ON_OFF_DARK_MODE, true);
    }

    public void setOnOffNotification(final boolean value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_ON_OFF_NOTIFICATION, value);
        editor.apply();
    }

    public boolean isNotificationOn() {
        return pref.getBoolean(KEY_ON_OFF_NOTIFICATION, true);
    }

    public void setOnOffRTL(final boolean value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_ON_OFF_RTL, value);
        editor.apply();
    }

    public boolean isRTLOn() {
        return pref.getBoolean(KEY_ON_OFF_RTL, false);
    }

    /**
     * sets premium user value to session
     */
    public void setPremiumUser(final boolean value) {
        final SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(KEY_PREMIUM_USER, value);
        editor.apply();
    }

    /**
     * returns boolean value if user is premium user or not
     * true ==> user is premium user
     * false ==> non premium user
     */
    public boolean isPremiumUser() {
        return pref.getBoolean(KEY_PREMIUM_USER, false);
    }

    public boolean isFreeUser() {
        return !pref.getBoolean(KEY_PREMIUM_USER, false);
    }
}
