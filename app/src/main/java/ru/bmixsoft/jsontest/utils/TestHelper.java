package ru.bmixsoft.jsontest.utils;

import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.conn.ssl.SSLContextBuilder;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.TrustStrategy;

/**
 * Created by Mike on 01.02.2018.
 */

public class TestHelper {

    static String url = "http://uslugi.mosreg.ru/zdrav/";
    //static final String  url = "http://mbelin.ru/mosregerror.html";



    public static void exec(final FragmentActivity activity) {


        String descrEvent = "У Вас оформлен талон №1 к специалисту Белин М.М.";

/*
        long calID = 0;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2018, 0, 16, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2018, 0, 16, 8, 45);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = activity.getApplicationContext().getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);

        values.put(CalendarContract.Events.TITLE, activity.getString(R.string.app_name));
        values.put(CalendarContract.Events.ALL_DAY,1);
        values.put(Events.EVENT_LOCATION, "Больница 1");

        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        values.put(CalendarContract.Events.DESCRIPTION,  descrEvent);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS,
                CalendarContract.Events.STATUS_CONFIRMED);
        values.put(CalendarContract.Events.ALL_DAY, 1);
        Uri uri = cr.insert(Events.CONTENT_URI, values);
        long eventID = Long.parseLong(uri.getLastPathSegment());
        Utils.msgInfo("eventID: "+eventID);
*/

        /*
        values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, 15);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        long remId = Long.parseLong(uri.getLastPathSegment());
        Utils.msgInfo("remId: "+remId);
*/
        /*

        uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(uri);
        activity.startActivity(intent);
        */
        //Calendar cal = Calendar.getInstance();

        //  CalendarHelper.addEvent(activity, "Запись к врачу 2", 2018,1,13,19,40);

        /*
        long eventId = -1;
        try {

            eventId = CalendarHelper.addEvent(activity.getApplicationContext(), "проверка", Utils.strToDate("2018-02-15 14:20", "yyyy-MM-dd HH:mm").getTime());
        }
        catch (Exception e)
        {
            Utils.msgError("Ошибка добавления события в календарь:\n" + e.toString());
        }
        if (eventId > 0){
            TalonInCalendar tic = new TalonInCalendar(1, "123", (int) eventId);
            tic.saveToDB();
        }
        */

//        Utils.showErrorDlg((FragmentActivity) activity,"Ошибка удаления события из календаря:",  "Проверка");

        // CalendarHelper.removeEventNew(activity, 121);

       /* try
        {
            throw new Exception("Вот же блять!");
        }
        catch (Exception e)
        {
            Utils.showErrorDlg(activity, "Ошибка", Utils.errStack(e));
        }
    */
        //WhatNewDialogFragment.show(activity);
        /*
        try {
            FileInputStream is = new FileInputStream("/non-existent/file");
            int c = is.read();
        } catch(IOException e) {
            FirebaseCrash.logcat(Log.ERROR, "TestNewFunc","FireBase Crash Test");
            FirebaseCrash.report(e); // Generate report
            Utils.msgInfo("FireBase CrashReport OK!");
        }
        */
        /*
        JSoupHelper.checkAvailableSite(activity, new JSoupHelper.Callback() {
            @Override
            public void onFinish(boolean success, HashMap<String, Object> result) {
                String title = "";
                if (result.containsKey(AsyncJSoupHttpHelper.hmk_result_head_txt))
                    title = (String) result.get(AsyncJSoupHttpHelper.hmk_result_head_txt);
                InfoDialogFragment.show((FragmentActivity) activity, "Проверка доступности сервера", title, (String) result.get(AsyncJSoupHttpHelper.hmk_result), (int) result.get(AsyncJSoupHttpHelper.hmk_success) == 1 ? InfoDialogFragment.TypeDialog.INFO : InfoDialogFragment.TypeDialog.ERROR);
            }
        });
        */
  /*
        final ThreadJSoupHttpHelper httpTask = new ThreadJSoupHttpHelper(activity);
        httpTask.setProcDialog(true, "Проверка доступности web-портала");
        httpTask.setExecInBackground(new ThreadJSoupHttpHelper.ExecInBackground() {
            @Override
            public HashMap<String, Object> exec(ThreadJSoupHttpHelper longTask) {

                StringBuilder sb = new StringBuilder();
                HashMap<String, Object> map = new HashMap<String, Object>();
                Connection connect = Jsoup.connect(url);
                try {
                    connect.timeout(30 * 1000);// 60 сек таймаут
                    connect.userAgent(activity.getString(R.string.defaultHttpUserAgent));
                    Connection.Response resp = connect.method(Connection.Method.GET).execute();
                    Map<String, String> cookies = resp.cookies();
                    Document doc = resp.parse();

                    try {

                        Elements p_err_title = doc.select("p.g-error-title-text");

                        if (p_err_title != null && p_err_title.size() > 0) {
                            Element first = p_err_title.first();
                            map.put(AsyncJSoupHttpHelper.hmk_result_head_txt, first.text());
                        } else {
                            Elements registryDiv = doc.select("div.b-registry-form__policy-data");
                            Elements registryDivOld = doc.select("form.b-e-reg-auth");


                            if ((registryDiv != null && registryDiv.size() > 0) ||
                                    (registryDivOld != null && registryDivOld.size() > 0)) {
                                sb.append("Web-ресурс доступен!");
                                map.put(AsyncJSoupHttpHelper.hmk_success, 1);
                                map.put(AsyncJSoupHttpHelper.hmk_result, sb.toString());
                                return map;
                            }


                        }
                        Elements p_err_body = doc.select("div.g-error-text");
                        if (p_err_body != null && p_err_body.size() > 0) {
                            Element first = p_err_body.first();
                            sb.append("\n").append(first.text());
                        }

                    } catch (Exception e) {
                        Utils.printCrashToFireBase("exec", "Ошибка парсинга страницы", e);
                        sb.append("Сервер временно не доступен! Попробуйте позже.");
                    }
                } catch (SocketTimeoutException et) {
                    sb.append("Время ожидания от сервера истекло! Попробуйте еще раз.");
                } catch (Exception e) {
                    Utils.printCrashToFireBase("exec", "Ошибка получения данных с web-ресурса", e);
                    sb.append("Сервер временно не доступен! Попробуйте позже.");
                }
                map.put(AsyncJSoupHttpHelper.hmk_success, 0);
                map.put(AsyncJSoupHttpHelper.hmk_result, sb.toString());
                return map;
            }
        });

        httpTask.setCallback(new ThreadJSoupHttpHelper.Callback() {
            @Override
            public void onRefreshUI(HashMap<String, Object> obj) {
                WebDialogFragment.show((FragmentActivity) activity,activity.getString(R.string.errorWrkSite), activity.getString(R.string.urlChkAvailableServer));
                boolean res = (Integer) obj.get(ThreadJSoupHttpHelper.hmk_success) == 1 ? true : false;
                String title = "";
                if (obj.containsKey(ThreadJSoupHttpHelper.hmk_result_head_txt))
                    title = (String) obj.get(ThreadJSoupHttpHelper.hmk_result_head_txt);
                InfoDialogFragment.show((FragmentActivity) activity, "Проверка доступности сервера", title, (String) obj.get(ThreadJSoupHttpHelper.hmk_result), (int) obj.get(ThreadJSoupHttpHelper.hmk_success) == 1 ? InfoDialogFragment.TypeDialog.INFO : InfoDialogFragment.TypeDialog.ERROR);

            }
        });
        httpTask.run();
*/

  /*
        Notification notification = new NotificationCompat.Builder(activity.getApplicationContext())
                .setTicker("Найдены новые талоны к врачу")//r.getString(R.string.launch_title))
                .setSmallIcon(R.drawable.hospital_small)
                .setContentTitle("Список врачей:")
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.CYAN, 3000, 3000)
                // .setSubText(Utils.dateToStr(Utils.Sysdate(),"yyyy-MM-dd'T'HH:mm:ss'Z'")+" "+r.getString(R.string.app_name))
                //  .setStyle(new NotificationCompat.BigTextStyle().bigText(result))
                //.setCustomContentView(contentView)
                // .setNumber()
                .setAutoCancel(true)
                .build();
        Intent i = new Intent("ru.bmixsoft.jsontest.service.SHOW_NOTIFICATION");

        Context c = activity.getApplicationContext();
        NotificationManager notificationManager = (NotificationManager)
                c.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

*/
        Utils.showNotification(activity,"Заголовок","Проверка");
        testHttpClient();
    }

    private static void testHttpClient(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new Thread(() -> {
            SyncHttpClient client = new SyncHttpClient();
            Utils.setSSL();

            try {
                SSLContext sslContexts = SSLContexts.custom().useSSL().loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                }).build();

            SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContexts);
            client.setSSLSocketFactory(sslSocketFactory);
            client.setTimeout(20);
            client.get(url, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                }
            });

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }).run();
    }


}
