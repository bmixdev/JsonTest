package ru.bmixsoft.jsontest.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import es.dmoral.toasty.Toasty;
import ru.bmixsoft.jsontest.BuildConfig;
import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.dialog.ErrorDialogFragment;
import ru.bmixsoft.jsontest.fragment.dialog.StrDialogFragment;
import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.fragment.options.Option;

//import es.dmoral.toasty.Toasty;
//import ru.bmixsoft.jsontest.fragment.dialog.ErrorDialogFragment;
//import ru.bmixsoft.jsontest.fragment.dialog.StrDialogFragment;
//import ru.bmixsoft.jsontest.fragment.options.LibOption;
//import ru.bmixsoft.jsontest.fragment.options.Option;

public class Utils {

    public static final String dateFrmt = "dd.MM.yyyy";

    public static boolean mDefaultValue = false;
    public static Context mContext;
    private static int timeShowToast = 1 * 1000 + 500; //секунд

    public static void initTune(Context context, boolean defaultValue) {
        mContext = context;
        mDefaultValue = defaultValue;
    }

    public static Object nvl(Object value, Object alternateValue) {
        if (value == null)
            return alternateValue;

        return value;
    }

    public static String errStack(Exception e) {
        String res = "";
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        res = result.toString();
        printWriter.close();
        return res;
    }

    public static void callStack() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            Log.d("Utils", ste.toString());
        }

    }

    public static void d(String msg) {
        try {
            if (mContext != null) Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void d(Context c, String msg) {
        try {

            Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
            {
                e.printStackTrace();
            }
    }

    public static boolean isDebugMode() {
        boolean res = mDefaultValue;
        Option option = LibOption.getInstance(mContext).getOption("onDebugMode");
        if (option != null) {
            res = option.getValue().equals("1") ? true : false;
            return res;
        }
        return res;
    }

    public static int getColorHeader() {
        int res = mContext.getResources().getColor(R.color.default_schema_color);
        Option option = LibOption.getInstance(mContext).getOption("colorHeader");
        if (option != null) {
            res = Integer.valueOf(option.getValue());
            return res;
        }
        return res;
    }

    public static int getIntervalService() {
        int res = Color.YELLOW;
        Option option = LibOption.getInstance(mContext).getOption("onIntervalRun");
        if (option != null) {
            res = Integer.valueOf(option.getValue());
            return res;
        }
        return res;
    }

    public static Date strToDate(String dtStart, String fmt) {
        //From String to Date
        //String dtStart = "2010-10-15T09:27:37Z";
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        try {
            Date date = format.parse(dtStart);
            //System.out.println(date);
            return date;
        } catch (ParseException e) {
            Utils.safePrintError(e); // e.printStackTrace();
            return null;
        }
    }

    public static String dateToStr(Date date, String fmt) {
        //From Date to String
        //SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat dateFormat = new SimpleDateFormat(fmt);
        try {
            String datetime = dateFormat.format(date);
            //System.out.println("Current Date Time : " + datetime);
            return datetime;
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
            return null;
        }
    }

    public static Date Sysdate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static String getjsonString(JSONObject json, String nameFld) {
        try {
            if (json.has(nameFld)) return json.getString(nameFld);
            else return "";
        } catch (JSONException je) {
            Utils.safePrintError(je); // je.printStackTrace();
            return "";
        }
    }

    public static int getjsonInt(JSONObject json, String nameFld) {
        try {
            if (json.has(nameFld)) return json.getInt(nameFld);
            else return 0;
        } catch (JSONException je) {
            Utils.safePrintError(je); // je.printStackTrace();
            return 0;
        }
    }

    public static boolean getjsonBool(JSONObject json, String nameFld) {
        try {
            if (json.has(nameFld)) return json.getBoolean(nameFld);
            else return false;
        } catch (JSONException je) {
            Utils.safePrintError(je); // je.printStackTrace();
            return false;
        }
    }


    public static String getResString(int res) {
        return mContext.getString(res);
    }

    public static String boolToString(boolean b) {
        return b ? getResString(R.string.sTrue) : getResString(R.string.sFalse);
    }


    public static String strToDateWeekStr(String dtStart, String fmtIn) {
        //From String to Date
        //String dtStart = "2010-10-15T09:27:37Z";
        SimpleDateFormat format = new SimpleDateFormat(fmtIn);
        SimpleDateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy");
        try {
            Date date = format.parse(dtStart);
            String dateWeek = dateFormat.format(date).toUpperCase();
            //System.out.println(date);
            return dateWeek;
        } catch (ParseException e) {
            Utils.safePrintError(e); // e.printStackTrace();
            return null;
        }
    }

    public <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static boolean isTimeExistPeriod(String time, String period) {
        boolean res = false;

        String timeBeg = period.substring(0, 5);
        String timeEnd = period.substring(6);


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date givenDate = sdf.parse(time);

			/*
            Calendar now = Calendar.getInstance();
			int hour = now.get(Calendar.HOUR_OF_DAY);
			int minute = now.get(Calendar.MINUTE);
			*/
            Date dateBeg = sdf.parse(timeBeg);

            Date dateEnd = sdf.parse(timeEnd);

            if (dateBeg.before(givenDate) || givenDate.equals(dateBeg)) {
                if (dateEnd.after(givenDate) || givenDate.equals(dateEnd)) {
                    res = true;
                } else {
                    res = false;
                }
            } else {
                res = false;
            }
        } catch (Exception e) {
            Log.e("Utils", e.toString());
        }
        return res;
    }


    public static void showNotification(Context context, String heading, String description){
        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        createChannel(context);
        //  PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0 /* Request code */, null/*intent*/,
        //        PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,"channelID")
                .setSmallIcon(R.drawable.hospital)
                .setContentTitle(heading)
                .setContentText(description)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        //      .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public static void createChannel(Context context){
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Description");
        notificationManager.createNotificationChannel(channel);
    }

    private void sendNotification(String message, String tick, String title, boolean sound, boolean vibrate, int iconID, Context context, Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);

        if (sound) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }

        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        notificationBuilder.setDefaults(notification.defaults);
        notificationBuilder.setSmallIcon(iconID)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setTicker(tick)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static int dpToPx(Context ctx, int dp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void safePrintError(Exception e) {
        if (LibOption.getOptionValueBool(mContext, "onDebugMode")) {
            e.printStackTrace();
        }

    }

    public static void printCrashToFireBase(String title, String header, Exception e){

        if (BuildConfig.DEBUG_MODE)
        {
            try {
                if (mContext != null) {
                    StrDialogFragment.show((FragmentActivity) mContext, title, header, Utils.errStack(e));
                } else {
                    msgError(Utils.errStack(e));
                }
            } catch (Exception el)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Crashlytics.log(title);
            Crashlytics.setString("header", header);
            Crashlytics.logException(e);
        }
    }

    public static void showDelayToast(final Toast t) {
        try {
            t.show();
            new CountDownTimer(timeShowToast, 1000) {
                public void onTick(long millisUntilFinished) {
                    t.show();
                }

                public void onFinish() {
                    t.show();
                }
            }.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgError(int resId) {
        try {
            if (mContext != null) {
                Toast t = Toasty.error(mContext, mContext.getString(resId), Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgError(String msg) {
        try {
            if (mContext != null) {
                Toast t = Toasty.error(mContext, msg, Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgInfo(int resId) {
        try {
            if (mContext != null) {
                Toast t = Toasty.info(mContext, mContext.getString(resId), Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgInfo(String msg) {
        try {
            if (mContext != null) {
                Toast t = Toasty.info(mContext, msg, Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgSuccess(int resId) {
        try {
            if (mContext != null) {
                Toast t = Toasty.success(mContext, mContext.getString(resId), Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgSuccess(String msg) {
        try {
            if (mContext != null) {
                Toast t = Toasty.success(mContext, msg, Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgWarning(int resId) {
        try {
            if (mContext != null) {
                Toast t = Toasty.warning(mContext, mContext.getString(resId), Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgWarning(String msg) {
        try {
            if (mContext != null) {
                Toast t = Toasty.warning(mContext, msg, Toast.LENGTH_SHORT, true);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void msgNormal(int resId) {
        try {
            if (mContext != null) {
                Toast t = Toasty.normal(mContext, mContext.getString(resId));
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void msgNormal(String msg) {
        try {
            if (mContext != null) {
                Toast t = Toasty.normal(mContext, msg);
                showDelayToast(t);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void openUrl(Context context, String url)
    {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }


    public static void sndEmail(Context context, String msgBody)
    {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.launch_title)).append(" ").append(UpdateHelper.getVersionBuilderName());
        String subject = sb.toString();

        sendIntent.setType("message/rfc822");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.developer_email)});
        sendIntent.putExtra(Intent.EXTRA_TEXT, msgBody);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        context.startActivity(sendIntent);
    }

    public static void showErrorDlg(Context context, String header, String msg)
    {
        try {
            ErrorDialogFragment.show((Activity) context, "Возникла ошибка", header, msg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }





    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void copyText(Context context, String copiedText) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copiedText);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("TAG",copiedText);
            clipboard.setPrimaryClip(clip);
        }
    }


    //Проверка активности соединения
    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {            return true;        }
        return false;
    }

    public static void viberate(Context context, int mls)
    {
        try {
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(mls);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void setSSL() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }});
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

}
