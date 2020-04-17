package ru.bmixsoft.jsontest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import ru.bmixsoft.jsontest.service.PollService;

/**
 * Created by Михаил on 20.12.2017.
 */

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn = prefs.getBoolean(PollService.PREF_IS_ALARM_ON, false);
        PollService.setServiceAlarm(context, isOn);
    }
}
