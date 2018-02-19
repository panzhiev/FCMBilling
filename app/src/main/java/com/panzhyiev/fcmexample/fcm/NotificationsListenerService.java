package com.panzhyiev.fcmexample.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.panzhyiev.fcmexample.R;
import com.panzhyiev.fcmexample.ui.activity.MainActivity;
import com.panzhyiev.fcmexample.ui.activity.ResultNotificationActivity;
import com.panzhyiev.fcmexample.db.SharedPreferencesHelper;
import com.panzhyiev.fcmexample.tools.Helper;

import java.util.Map;

public class NotificationsListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Helper.isAppForeground()) {
            Log.d("RECEIVE", "app foreground");
            Map<String, String> map = remoteMessage.getData();
            Log.d("MAP", map.toString());

            String[] strings = {map.get("title"), map.get("body")};
            Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
            intent.putExtra("notification", strings);
            sendBroadcast(intent);


//            showNotification(map.get("title"), map.get("body"), map.get("id"), true);

        } else {
            Log.d("RECEIVE", "app background");
            Map<String, String> map = remoteMessage.getData();
            Log.d("MAP", map.toString());
            showNotification(map.get("title"), map.get("body"), map.get("id"), false);
        }
    }

    private void showNotification(String title, String body, String id, boolean isInApp) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int idInt = Integer.parseInt(id);

        if (!SharedPreferencesHelper.getInstance().getStringValue("notifId").equals(id)) {

            SharedPreferencesHelper.getInstance().putStringValue("notifId", id);

            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(body)) {
                Intent intent = new Intent("OPEN_ACTIVITY_RESULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Notification.Builder notificationBuilder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_cloud_black_24dp)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                assert notificationManager != null;
                notificationManager.notify(idInt, notificationBuilder.build());
            }
        } else {
            assert notificationManager != null;
            notificationManager.cancel(idInt);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
