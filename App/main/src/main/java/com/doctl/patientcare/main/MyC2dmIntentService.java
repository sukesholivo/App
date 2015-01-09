package com.doctl.patientcare.main;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by mailtovishal.r on 6/21/2014.
 */
public class MyC2dmIntentService extends IntentService {
    private static final String TAG = MyC2dmIntentService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;

    public MyC2dmIntentService() {
        super("MyC2dmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(TAG, "Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(TAG, "Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                sendNotification(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        MyC2dmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle msg) {
        String type = msg.getString("type");
        String username = msg.getString("username");
        String data = msg.getString("data");
        if (type.toLowerCase().equals("card")) {
            Intent popupIntent = new Intent(this, PopupNotificationService.class);
            popupIntent.putExtra("card", data);
            startService(popupIntent);
        } else if (type.toLowerCase().equals("message")){
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Message from DOCTL")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(data))
                            .setContentText(data);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
//
    }
}