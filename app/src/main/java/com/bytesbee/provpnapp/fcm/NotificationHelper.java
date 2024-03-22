package com.bytesbee.provpnapp.fcm;


import static com.bytesbee.provpnapp.constants.IConstants.FALSE;
import static com.bytesbee.provpnapp.constants.IConstants.TRUE;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.text.Html;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.bytesbee.provpnapp.R;
import com.bytesbee.provpnapp.activity.HomeActivity;

import java.util.Date;

/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link http://bytesbee.com
 */
@TargetApi(VERSION_CODES.O)
class NotificationHelper extends ContextWrapper {

    private NotificationManager notifManager;
    private static final String CHANNEL_ONE_ID = "com.bytesbee.my.notification.ONE";
    private static final String CHANNEL_ONE_NAME = "Channel One";
    private static final String CHANNEL_TWO_ID = "com.bytesbee.my.notification.TWO";
    private static final String CHANNEL_TWO_NAME = "Channel Two";

    private PendingIntent pendingIntent;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    private void createChannels() {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(TRUE);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(TRUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);

        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_TWO_ID, CHANNEL_TWO_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel2.enableLights(FALSE);
        notificationChannel2.enableVibration(TRUE);
        notificationChannel2.setLightColor(Color.RED);
        notificationChannel2.setShowBadge(FALSE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel2);

    }

    //Create the notification that’ll be posted to Channel One//
    public NotificationCompat.Builder getNotificationBackgroundWithImage(String title, String body, Bitmap bitmap, String webUrl) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(body).toString());
        bigPictureStyle.bigPicture(bitmap);
        bigPictureStyle.bigLargeIcon(null);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int FLAG_PENDING_INTENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FLAG_PENDING_INTENT = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            FLAG_PENDING_INTENT = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        if (TextUtils.isEmpty(webUrl))
            pendingIntent = PendingIntent.getActivity(this, 123, intent, FLAG_PENDING_INTENT);
        else {
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setData(Uri.parse(webUrl));
            pendingIntent = PendingIntent.getActivity(this, 123, notificationIntent, FLAG_PENDING_INTENT);
        }

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(new Date().getTime())
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(TRUE);
    }

    //Create the notification that’ll be posted to Channel One//
    public NotificationCompat.Builder getNotificationBackground(String title, String body, String webUrl) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int FLAG_PENDING_INTENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FLAG_PENDING_INTENT = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            FLAG_PENDING_INTENT = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        if (TextUtils.isEmpty(webUrl))
            pendingIntent = PendingIntent.getActivity(this, 123, intent, FLAG_PENDING_INTENT);
        else {
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setData(Uri.parse(webUrl));
            pendingIntent = PendingIntent.getActivity(this, 123, notificationIntent, FLAG_PENDING_INTENT);
        }

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(new Date().getTime())
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(TRUE);
    }

    public void notify(int id, NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }

    //Send your notifications to the NotificationManager system service//
    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}