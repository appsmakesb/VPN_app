package com.bytesbee.provpnapp.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.managers.SessionManager;
import com.bytesbee.provpnapp.utils.Utils;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link http://bytesbee.com
 */
public class NotificationUtils {

    private static final String TAG = NotificationUtils.class.getSimpleName();

    private final Context context;

    public NotificationUtils(Context context) {
        this.context = context;
    }

    public void showNotificationMessage(String title, String message, Intent intent) {
        showNotificationMessage(title, message, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, Intent intent, String imageUrl) {
        try {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            int FLAG_PENDING_INTENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                FLAG_PENDING_INTENT = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
            } else {
                FLAG_PENDING_INTENT = PendingIntent.FLAG_UPDATE_CURRENT;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_PENDING_INTENT);

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

            final Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (!TextUtils.isEmpty(imageUrl) && (imageUrl.endsWith(".jpg") || imageUrl.endsWith(".png"))) {
                if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                    Bitmap bitmap = Utils.getBitmapFromURL(imageUrl);
                    if (bitmap != null) {
                        showBigNotification(bitmap, builder, title, message, pendingIntent, sound);
                    } else {
                        showSmallNotification(builder, title, message, pendingIntent, sound);
                    }
                }
            } else {
                showSmallNotification(builder, title, message, pendingIntent, sound);
            }
        } catch (Exception ignored) {
        }
    }


    private void showSmallNotification(NotificationCompat.Builder builder, String title, String message, PendingIntent resultPendingIntent, Uri sound) {
        final int icon = R.mipmap.ic_launcher;

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        Notification notification;
        notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(sound)
                .setStyle(inboxStyle)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message)
                .build();
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(title.hashCode(), notification);
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder builder, String title, String message, PendingIntent resultPendingIntent, Uri sound) {
        final int icon = R.mipmap.ic_launcher;

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(context.getString(R.string.app_name));
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        bigPictureStyle.bigLargeIcon(null);
        Notification notification;
        notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(sound)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(message.hashCode(), notification);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
    }


    private void sendRegistrationToServer(final String token) {
        String fcm_url = Utils.getTokenURL(context);
        fcm_url += "?token=" + token;
        try {
            URL url = new URL(fcm_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder stringBuffer = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(inputLine);
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void checkFCMTokenUpdated() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();
                if (!SessionManager.get().getFCMToken().equalsIgnoreCase(token)) {
                    //call API
                    new UploadFCMToken(token).execute();
                }
            });

        } catch (Exception ignored) {
        }
    }

    @SuppressLint("StaticFieldLeak")
    class UploadFCMToken extends AsyncTask<Void, Void, Void> {

        private final String newToken;

        UploadFCMToken(String newToken) {
            this.newToken = newToken;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sendRegistrationToServer(newToken);
            SessionManager.get().setFCMToken(newToken);
            return null;
        }
    }

}
