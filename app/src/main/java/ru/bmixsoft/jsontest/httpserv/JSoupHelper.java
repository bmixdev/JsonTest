package ru.bmixsoft.jsontest.httpserv;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Mike on 21.02.2018.
 */

public class JSoupHelper {

    public interface Callback{
        public void onFinish(boolean success, HashMap<String, Object> result);
    }
    public static AsyncJSoupHttpHelper checkAvailableSite(final Context context, final Callback callback)
    {

        AsyncJSoupHttpHelper httpTask = new AsyncJSoupHttpHelper(context);
        httpTask.setProcDialog(true, "Проверка доступности web-портала");
        httpTask.setExecInBackground(new AsyncJSoupHttpHelper.ExecInBackground() {
            @Override
            public HashMap<String, Object> exec(AsyncJSoupHttpHelper longTask) {

                Utils.setSSL();

                String url = context.getString(R.string.urlChkAvailableServer);
                StringBuilder sb = new StringBuilder();
                HashMap<String, Object> map = new HashMap<String, Object>();
                Connection connect = Jsoup.connect(url);
                try {
                    connect.timeout(40 * 1000);// 60 сек таймаут
                    connect.userAgent(context.getString(R.string.defaultHttpUserAgent));
                    Connection.Response resp = connect.method(Connection.Method.GET).execute();
                    Map<String, String> cookies = resp.cookies();
                    Document doc = resp.parse();

                    try {

                        Elements p_err_title = doc.select("p.g-error-title-text");

                        if (p_err_title != null && p_err_title.size() > 0) {
                            Element first = p_err_title.first();
                            map.put(AsyncJSoupHttpHelper.hmk_result_head_txt, first.text());
                        }
                        else
                        {
                            Elements registryDiv = doc.select("div.b-registry-form__policy-data");
                            Elements registryDivOld = doc.select("form.b-e-reg-auth");


                            if ((registryDiv != null && registryDiv.size() > 0) ||
                                    (registryDivOld != null && registryDivOld.size() > 0))
                            {
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
                }
                catch (SocketTimeoutException et)
                {
                    sb.append("Время ожидания от сервера истекло! Попробуйте еще раз.");
                }
                catch (Exception e) {
                    Crashlytics.setString("url", url);
                    Utils.printCrashToFireBase("exec", "Ошибка получения данных с web-ресурса", e);
                    sb.append("Сервер временно не доступен! Попробуйте позже.");
                }
                map.put(AsyncJSoupHttpHelper.hmk_success, 0);
                map.put(AsyncJSoupHttpHelper.hmk_result, sb.toString());
                return map;
            }
        });

        httpTask.setCallback(new AsyncJSoupHttpHelper.Callback() {
            @Override
            public void onRefreshUI(HashMap<String, Object> obj) {
                boolean res = (Integer) obj.get(AsyncJSoupHttpHelper.hmk_success) == 1 ? true : false;
                callback.onFinish(res, (HashMap<String, Object>) obj);
            }
        });
        httpTask.execute();
        return httpTask;
    }
}
