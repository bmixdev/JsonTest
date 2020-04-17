package ru.bmixsoft.jsontest.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.activity.LaunchActivity;
import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.fragment.options.Option;
import ru.bmixsoft.jsontest.httpserv.AsyncCheckFaforites;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 19.12.2017.
 */

public class PollService extends IntentService {

    private static int POLL_INTERVAL = 1000 * 60; // каждую минуту
    public static final String PREF_IS_ALARM_ON = "isAlarmOn";
    public static final String ACTION_SHOW_NOTIFICATION =
            "ru.bmixsoft.jsontest.service.SHOW_NOTIFICATION";

    public static final String PERM_PRIVATE =
            "ru.bmixsoft.jsontest.PRIVATE";

    private static final String TAG = "PollService";

    public PollService() {
        super(TAG);
    }

    private boolean isTimeInAvaliblePeriod()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {

            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            //Date curTime = sdf.parse(hour + ":" + minute);
            Option option = LibOption.getInstance(getApplication()).getOption("avalibleIntervalService");
            boolean b = Utils.isTimeExistPeriod(hour + ":" + minute, option.getValue());
            return b;
        }
        catch (Exception e)
        {
            Utils.safePrintError(e); // e.printStackTrace();
            return false;
        }
    }


    public static void createChannel(Context c){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Description");
        notificationManager.createNotificationChannel(channel);
    }

    //вызываемое тело сервиса
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent->");
        /*
        // проверка сетевого подключения
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() &&
                cm.getActiveNetworkInfo() != null;
        if (!isNetworkAvailable) return;
*/

        if (!isTimeInAvaliblePeriod())
        {
            Log.i(TAG, "onHandleIntent enable=false-<");
            return;
        }

        synchronized (this) {
            AsyncCheckFaforites task = new AsyncCheckFaforites(this);
            task.setCallback(new AsyncCheckFaforites.Callback() {
                @Override
                public void onFinishUI(String result) {

                    if (result.isEmpty()) return;
                    Resources r = getResources();
                    PendingIntent pi = PendingIntent
                            .getActivity(PollService.this, 0, new Intent(PollService.this, LaunchActivity.class), 0);



                    RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_push);
                    contentView.setImageViewResource(R.id.image, R.drawable.hospital);
                    contentView.setTextViewText(R.id.title, "Найдены талоны к:");
                    contentView.setTextViewText(R.id.text, result);

                    createChannel(getApplicationContext());

                    Notification notification = new NotificationCompat.Builder(PollService.this, "channelID")
                            .setTicker("Найдены новые талоны к врачу")//r.getString(R.string.launch_title))
                            .setSmallIcon(R.drawable.hospital)
                            .setCustomBigContentView(contentView)
                            .setContentTitle("Список врачей:")
                            .setContentText(result)
                            .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                            .setLights(Color.CYAN, 3000, 3000)
                           // .setSubText(Utils.dateToStr(Utils.Sysdate(),"yyyy-MM-dd'T'HH:mm:ss'Z'")+" "+r.getString(R.string.app_name))
                          //  .setStyle(new NotificationCompat.BigTextStyle().bigText(result))
                            //.setCustomContentView(contentView)
                            // .setNumber()
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();
                    showBackgroundNotification(0, notification);

                }
            });
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        Log.i(TAG, "onHandleIntent-<");
    }

    /*
    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = 0x008000;
            notificationBuilder.setColor(color);
            return R.drawable.hospital_small;

        }
        return R.drawable.app_icon_lolipop_below;
    }
*/

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, 0);
        POLL_INTERVAL = 1000 * 60 * Utils.getIntervalService();
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PollService.PREF_IS_ALARM_ON, isOn)
                .commit();

    }

    //проверка существования пединг интента
    public static boolean isServiceAlarmOn(Context context) {
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }


    void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra("REQUEST_CODE", requestCode);
        i.putExtra("NOTIFICATION", notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }


}
