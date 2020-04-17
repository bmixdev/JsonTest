package ru.bmixsoft.jsontest.utils;

/*

Последовательность вызовов коллбэков

1. Разбор респонса - CallbackParserRequest
2. Обновление UI-интерфейса - Callback
3. Выполнение следующего soap-запроса - CallbackNextRequest

 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.dialog.InfoDialogFragment;
import ru.bmixsoft.jsontest.httpserv.AsyncJSoupHttpHelper;

/**
 * Created by Михаил on 24.01.2017.
 */
public class MyAsyncHttp extends AsyncTask<Void, String, Integer>
    implements HttpRestClientUsage.Callback
{

    private static MyAsyncHttp instance;

    private Context mContext;
    private ProgressDialog mProgressDialog;
    private String mTypeReq;
    private String mSoapUrl;
    private String mMsgDialog;
    private RequestParams mRequestParams;
    private HttpRestClientUsage mHttpClient;
    private Callback mCallback;
    private CallbackNextRequest mCallbackNextRequest;
    private CallbackParserRequest mCallbackParserRequest;
    private CallbackParserRequestByte mCallbackParserRequestByte;
    private boolean mIsCreateProcDlg;

    private static final String DbgTAG = "MyAsyncHttp";

    public interface Callback{
        public void onPostExecuteUI(String response, boolean success);
    }

    public interface CallbackNextRequest{
        public void onNextRequest();
    }

    public interface CallbackParserRequest{
        public void onParse(MyAsyncHttp longTask, String responce, ProgressDialog progressDialog);
    }
    public interface CallbackParserRequestByte{
        public void onParse(MyAsyncHttp longTask, byte[] responce, ProgressDialog progressDialog);
    }

    public void setCallbackParserRequest(CallbackParserRequest callbackParserRequest) {
        mCallbackParserRequest = callbackParserRequest;
    }
    public void setCallbackParserRequestByte(CallbackParserRequestByte callbackParserRequestByte) {
        mCallbackParserRequestByte = callbackParserRequestByte;
    }

    public MyAsyncHttp(Context context, String typeReq, String soapUrl, String msgDialog, RequestParams params, Callback callback, boolean isCreateProcDlg)
    {
        //-> mbelin 25/03/2020 Аутентификации по сертификату SSL
        Utils.setSSL();
        //-< mbelin 25/03/2020 Аутентификации по сертификату SSL

        mContext = context;
        mTypeReq = typeReq;
        mSoapUrl = soapUrl;
        mMsgDialog = msgDialog;
        mRequestParams = params;
        mHttpClient = HttpRestClientUsage.getInstance(mContext, this);
        mCallback = callback;
        mIsCreateProcDlg = isCreateProcDlg;
        if (mIsCreateProcDlg)
            mProgressDialog = new ProgressDialog(mContext);
        else mProgressDialog = null;
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


    public boolean checkAvaibleSite(Context context)
    {
        String url = context.getString(R.string.urlChkAvailableServer);

        StringBuilder sb = new StringBuilder();
        Connection connect = Jsoup.connect(url);
        try {
            connect.timeout(40 * 1000);// 40 сек таймаут
            connect.userAgent(context.getString(R.string.defaultHttpUserAgent));
            Connection.Response resp = connect.method(Connection.Method.GET).execute();
            Map<String, String> cookies = resp.cookies();
            Document doc = resp.parse();

            try {

                Elements p_err_title = doc.select("p.g-error-title-text");

                if (p_err_title != null && p_err_title.size() > 0) {
                    Element first = p_err_title.first();
                    sb.append(first.text());
                }
                else
                {
                    Elements registryDiv = doc.select("div.b-registry-form__policy-data");
                    Elements registryDivOld = doc.select("form.b-e-reg-auth");


                    if ((registryDiv != null && registryDiv.size() > 0) ||
                            (registryDivOld != null && registryDivOld.size() > 0))
                    {
                       // sb.append("Web-ресурс доступен!");
                        return true;
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
            Utils.printCrashToFireBase("exec", "Ошибка получения данных с web-ресурса", e);
            sb.append("Сервер временно не доступен! Попробуйте позже.");
        }
        mHttpClient.soapResponce = sb.toString();
        return false;
    }



    public void setCallbackNextRequest(CallbackNextRequest callbackNextRequest)
    {
        mCallbackNextRequest = callbackNextRequest;
    }


    @Override
    public void onPublishProcess(String msg) {
        publishProgress(msg);
    }

    //onPreExecute() – выполняется перед doInBackground(). Имеет доступ к UI
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressDialog != null) {
            try {
                mProgressDialog.setMessage(mMsgDialog);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } catch (Exception e) {
                Utils.safePrintError(e); // e.printStackTrace();
                //Utils.d(DbgTAG + ":onPreExecute: Возникли проблемы с mProgressDialog: \n" + e.getMessage());
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setMessage(mMsgDialog);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }
        if (mIsCreateProcDlg)
            ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    //doInBackground() – основной метод, который выполняется в новом потоке. Не имеет доступа к UI.
    @Override
    protected Integer doInBackground(Void... params) {

        if (!isOnline((mContext).getApplicationContext())){
            ///throw new Exception("Интернет-соединение отсутствует!");
            mHttpClient.soapResponce = "Интернет-соединение отсутствует!";
            mHttpClient.statusResponce = false;
            return 0;
        }

        /*
       // String checkAvaibleSiteErr = new String();
        if (mHttpClient.mAvaibleSite == false && !checkAvaibleSite(mContext))
        {
          //  mHttpClient.soapResponce = checkAvaibleSiteErr;
            mHttpClient.statusResponce = false;
            mHttpClient.mAvaibleSite = false;
            return 0;
        }
        */


        mHttpClient.mAvaibleSite = true;

        if (mTypeReq == null)
        {
            mHttpClient.soapResponce = "Не задан тип запроса!";
            mHttpClient.statusResponce = false;
            return 0;
        }
        if (mTypeReq.equals("GET"))
        {
            mHttpClient.getData(mSoapUrl, mRequestParams);
        }
        else
        {
            mHttpClient.postData(mSoapUrl, mRequestParams);
        }


        try {
            JSONObject json = new JSONObject(mHttpClient.soapResponce);
            mHttpClient.soapResponce = json.toString();
        }
        catch (JSONException e)
        {
          //  e.printStackTrace();
            Utils.safePrintError(e);
        }

        if (mCallbackParserRequestByte != null)
            mCallbackParserRequestByte.onParse(this, mHttpClient.byteResponce, mProgressDialog);

        if (mCallbackParserRequest != null)
            mCallbackParserRequest.onParse(this, mHttpClient.soapResponce, mProgressDialog);

        return 1;
    }

    //onPostExecute() – выполняется после doInBackground() (может не вызываться, если AsyncTask был отменен). Имеет доступ к UI. Используйте его для обновления пользовательского интерфейса, как только ваша фоновая задача завершена. Данный обработчик при вызове синхронизируется с потоком GUI, поэтому внутри него вы можете безопасно изменять элементы пользовательского интерфейса.
    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
        try {

            if (s.equals(0) && mContext != null && mContext instanceof Activity )
            {
                InfoDialogFragment.show((FragmentActivity) mContext, mContext.getString(R.string.chkAvailableServer), mContext.getString(R.string.errorWrkSite), mHttpClient.soapResponce, s.equals(1) ? InfoDialogFragment.TypeDialog.INFO : InfoDialogFragment.TypeDialog.ERROR);
            }

            mCallback.onPostExecuteUI(mHttpClient.soapResponce, mHttpClient.statusResponce);
        }
        catch (Exception e)
        {
            Utils.safePrintError(e); //e.printStackTrace();
            //Utils.d(DbgTAG + ":onPostExecute: Error:\n"+e.getMessage());
        }
        finally {
            try {
                if (mProgressDialog!= null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }
            catch (Exception e)
            {
                Utils.safePrintError(e);
            }
            if (mIsCreateProcDlg)
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            if (mCallbackNextRequest != null) mCallbackNextRequest.onNextRequest();
        }

    }

    //onProgressUpdate(). Имеет доступ к UI. Переопределите этот обработчик для публикации промежуточных обновлений в пользовательский интерфейс. При вызове он синхронизируется с потоком GUI, поэтому в нём вы можете безопасно изменять элементы пользовательского интерфейса
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(DbgTAG, "onProgressUpdate -->");
        Log.d(DbgTAG, " values[0]: "+values[0]);
        if (mProgressDialog != null)
            mProgressDialog.setMessage(values[0]);
        Log.d(DbgTAG, "onProgressUpdate --<");
    }


}

