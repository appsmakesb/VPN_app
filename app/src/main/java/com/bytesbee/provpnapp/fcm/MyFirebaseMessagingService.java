package com.bytesbee.provpnapp.fcm;

import static com.bytesbee.provpnapp.constants.IConstants.EXTRA_BACKGROUND;
import static com.bytesbee.provpnapp.constants.IConstants.EXTRA_DATA;
import static com.bytesbee.provpnapp.constants.IConstants.EXTRA_IMAGE;
import static com.bytesbee.provpnapp.constants.IConstants.EXTRA_MESSAGE;
import static com.bytesbee.provpnapp.constants.IConstants.EXTRA_TITLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.activity.HomeActivity;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link http://bytesbee.com
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context context;
    private NotificationUtils notificationUtils;
    private Intent resultIntent;
    private NotificationHelper notificationHelper;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
        SessionManager.get().setFCMToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        context = getApplicationContext();
        if (!SessionManager.get().isNotificationOn()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper = new NotificationHelper(this);
        }

        if (remoteMessage.getNotification() != null) {
            handlePushNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Utils.getErrors(e);
            }
        }
    }

    private void handlePushNotification(String title, String message) {
        resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.putExtra(EXTRA_MESSAGE, message);
        displayNotificationMessage(context, title, message, resultIntent);
    }

    private void handleDataMessage(JSONObject json) {
        try {
            final JSONObject data = json.getJSONObject(EXTRA_DATA);
            final boolean isBackgroundNotification = data.has(EXTRA_BACKGROUND) && data.getBoolean(EXTRA_BACKGROUND);

            if (isBackgroundNotification) {

                Utils.requestSettingApi(context);

            } else {

                final String title = data.has(EXTRA_TITLE) ? data.getString(EXTRA_TITLE) : getString(R.string.app_name);
                final String message = data.has(EXTRA_MESSAGE) ? data.getString(EXTRA_MESSAGE) : "Simple message!";
                String imageUrl = data.has(EXTRA_IMAGE) ? data.getString(EXTRA_IMAGE) : "";
                String webUrl = "";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationCompat.Builder notificationBuilder;
                    if (TextUtils.isEmpty(imageUrl)) {
                        notificationBuilder = notificationHelper.getNotificationBackground(title, message, webUrl);
                    } else {
                        imageUrl = Utils.getAPIUrl(context) + File.separator + imageUrl;
                        Bitmap bitmap = Utils.getBitmapFromURL(imageUrl);
                        if (bitmap != null) {
                            notificationBuilder = notificationHelper.getNotificationBackgroundWithImage(title, message, bitmap, webUrl);
                        } else {
                            notificationBuilder = notificationHelper.getNotificationBackground(title, message, webUrl);
                        }
                    }
                    if (notificationBuilder != null) {
                        notificationHelper.notify((int) new Date().getTime(), notificationBuilder);
                    }
                } else {
                    resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    resultIntent.putExtra(EXTRA_MESSAGE, message);
                    if (TextUtils.isEmpty(imageUrl) && (!imageUrl.endsWith(".jpg") || !imageUrl.endsWith(".png"))) {
                        displayNotificationMessage(getApplicationContext(), title, message, resultIntent);
                    } else {
                        imageUrl = Utils.getAPIUrl(context) + File.separator + imageUrl;
                        displayNotificationMessageWithBigImage(getApplicationContext(), title, message, resultIntent, imageUrl);
                    }
                }
            }

        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void displayNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    private void displayNotificationMessageWithBigImage(Context context, String title, String message, Intent intent, String imageUrl) {
        try {
            notificationUtils = new NotificationUtils(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    private void sendRegistrationToServer(final String token) {
        String fcm_url = Utils.getTokenURL(this);
        fcm_url += "?token=" + token;
        try {
            URL url = new URL(fcm_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            final int statusCode = httpURLConnection.getResponseCode();
            if (statusCode != 200) {
                Utils.sout("Error " + statusCode + " for URL " + url.toExternalForm());
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder stringBuffer = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(inputLine);
            }
            bufferedReader.close();
        } catch (IOException e) {
            Utils.getErrors(e);
        }
    }

}
