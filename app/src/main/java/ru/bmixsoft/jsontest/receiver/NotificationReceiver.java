package ru.bmixsoft.jsontest.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Михаил on 20.12.2017.
 */

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context c, Intent i) {
        Log.i(TAG, "received result: " + getResultCode());
        if (getResultCode() != Activity.RESULT_OK)
            // Активность переднего плана отменила
            // широковещательную передачу
            return;
        int requestCode = i.getIntExtra("REQUEST_CODE", 0);
        Notification notification = (Notification)
                i.getParcelableExtra("NOTIFICATION");
        NotificationManager notificationManager = (NotificationManager)
                c.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(requestCode, notification);
    }
}