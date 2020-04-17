package ru.bmixsoft.jsontest.utils;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Михаил on 08.12.2016.
 */
public class HttpRestClientUsage {

    private Context mContext;
    private static HttpRestClientUsage instance;
    private boolean mIsWork;

    private final static String DbgTAG = "HttpRestClientUsage";
    private int mIndicator = 0;
    private int mPublishIndicator = 0;
    public String soapResponce;
    public byte[] byteResponce;
    public boolean statusResponce;
    public boolean mBusy;

    public boolean mAvaibleSite = false;

    private Callback mCallback;

    public interface Callback
    {
       // public void onResponce(String soap_url, boolean success, String responce);
        public void onPublishProcess(String msg);

    }

    public HttpRestClientUsage(Context context, Callback callback) {
        this.mContext = context;
        this.soapResponce = new String();
        this.mBusy = false;
        this.statusResponce = false;
        this.mCallback = callback;
        Utils.setSSL();
    }

    public static HttpRestClientUsage getInstance(Context context, Callback callback)
    {
        if (instance == null )
        {
            synchronized (HttpRestClientUsage.class){
                if (instance == null)
                {
                    instance = new HttpRestClientUsage(context, callback);
                }
            }

        }
        instance.mContext = context;
        return instance;
    }

    public void getData(final String soap_url, final RequestParams params
    )
    {
                Log.d(DbgTAG, "getData -->");
                Log.d(DbgTAG, "soap_url: "+soap_url);
                Log.d(DbgTAG, "params: "+params.toString());
                HttpRestClient.get(soap_url, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                // Initiated the request
                                Log.d(DbgTAG, "onStart -->");
                                mBusy = true;
                                Log.d(DbgTAG, "onStart --<");
                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                // Successfully got a response
                                Log.d(DbgTAG, "onSuccess -->");
                                Log.d(DbgTAG, "  responseBody.length: " + String.valueOf(responseBody.length));
                                try {

                                    byteResponce = responseBody.clone();
                                    String responce = new String(responseBody, "UTF-8");
                                    Log.d(DbgTAG, "  responce: \n" + responce);
                                    soapResponce = responce;
                                    statusResponce = true;
                                    //callback.onResponce(soap_url, true, responce);
                                } catch (UnsupportedEncodingException e) {
                                    Log.d(DbgTAG, "Error: " + e.getMessage());
                                }
                                Log.d(DbgTAG, "onSuccess --<");
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                                    error) {
                                // Response failed :(
                                Log.d(DbgTAG, "onFailure -->");
                                soapResponce = "";
                                if (responseBody == null) {
                                    Log.d(DbgTAG, "  error: " + error.getMessage());
                                    soapResponce = "Ошибка взаимодействия с web-сервисом:" + error.getMessage();
                                    statusResponce = false;
                                } else {
                                    Log.d(DbgTAG, "  responseBody.length: " + String.valueOf(responseBody.length));
                                    try {
                                        byteResponce = responseBody.clone();
                                        String responce = new String(responseBody, "UTF-8");
                                        Log.d(DbgTAG, "  responce: \n" + responce);
                                        soapResponce = responce;
                                        statusResponce = false;
                                    } catch (UnsupportedEncodingException e) {
                                        Log.d(DbgTAG, "Error: " + e.getMessage());
                                    } catch (Exception e) {
                                        Log.d(DbgTAG, "Error: " + e.getMessage());
                                        soapResponce = "Ошибка взаимодействия с web-сервисом:" + e.getMessage();
                                        statusResponce = false;
                                    }
                                }
                                Log.d(DbgTAG, "onFailure --<");
                            }

                            @Override
                            public void onRetry(int retryNo) {
                                // Request was retried
                                Log.d(DbgTAG, "onRetry -->");
                                Log.d(DbgTAG, "  retryNo: " + String.valueOf(retryNo));
                                Log.d(DbgTAG, "onRetry --<");
                            }

                            @Override
                            public void onProgress(long bytesWritten, long totalSize) {
                                // Progress notification
                               // Log.d(DbgTAG, "onProgress -->");
                               // Log.d(DbgTAG, "  bytesWritten: " + bytesWritten);
                               // Log.d(DbgTAG, "  totalSize: " + totalSize);
                                mPublishIndicator = mPublishIndicator > 200 ? 0 : mPublishIndicator+1;
                                if (mPublishIndicator == 200) {
                                    mIndicator = mIndicator >= 3 ? 0 : mIndicator + 1;
                                    StringBuilder tmpStr = new StringBuilder("Получение даных");
                                    for (int i = 0; i < mIndicator; i++) {
                                        tmpStr.append(".");
                                    }
                                    mCallback.onPublishProcess(tmpStr.toString());
                                }
                               // Log.d(DbgTAG, "onProgress --<");
                            }

                            @Override
                            public void onFinish() {
                                Log.d(DbgTAG, "onFinishUI -->");
                                mBusy = false;
                                mIsWork = false;
                               // callback.onResponce(soap_url, statusResponce, soapResponce);
                                Log.d(DbgTAG, "onFinishUI --<");
                            }
                        });

                 Log.d(DbgTAG, "getData --<");
            }

    public void postData(final String soap_url, final RequestParams params)
    {

        Log.d(DbgTAG, "postData -->");
        Log.d(DbgTAG, "soap_url: "+soap_url);
        Log.d(DbgTAG, "params: "+params.toString());
        /*
        if (pDialog != null){
                  if (pDialog.isShowing())
                        pDialog.dismiss();
                }
        pDialog = ProgressDialog.show(mActivityContext, msgDialog,"Обработка post-запроса", false, false);
        */

        HttpRestClient.post(soap_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // Initiated the request
                Log.d(DbgTAG, "onStart -->");
                mBusy = true;
                Log.d(DbgTAG, "onStart --<");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                    error) {
                // Response failed :(
                Log.d(DbgTAG, "onFailure -->");
                soapResponce = "";
                if (responseBody == null) {
                    Log.d(DbgTAG, "  error: " + error.getMessage());
                    soapResponce = "Ошибка взаимодействия с web-сервисом:" + error.getMessage();
                    statusResponce = false;
                }
                else {
                    Log.d(DbgTAG, "  responseBody.length: " + String.valueOf(responseBody.length));

                    try {
                        byteResponce = responseBody.clone();
                        String responce = new String(responseBody, "UTF-8");
                        Log.d(DbgTAG, "  responce: \n" + responce);
                        soapResponce = responce;
                        statusResponce = false;
                        //   callback.onResponce(soap_url, false, responce);
                    } catch (UnsupportedEncodingException e) {
                        Log.d(DbgTAG, "Error: " + e.getMessage());

                    } catch (Exception e) {
                        soapResponce = "Ошибка взаимодействия с web-сервисом: "+e.getMessage();
                        statusResponce = false;
                        Log.d(DbgTAG, "Error: " + e.getMessage());
                    }
                }
                Log.d(DbgTAG, "onFailure --<");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Successfully got a response
                Log.d(DbgTAG, "onSuccess -->");
                Log.d(DbgTAG, "  responseBody.length: " + String.valueOf(responseBody.length));
                try {
                    byteResponce = responseBody.clone();
                    String responce = new String(responseBody, "UTF-8");
                    Log.d(DbgTAG, "  responce: \n" + responce);
                    soapResponce = responce;
                    statusResponce = true;
                 //   callback.onResponce(soap_url, true, responce);
                } catch (UnsupportedEncodingException e) {
                    Log.d(DbgTAG, "Error: " + e.getMessage());
                }
                Log.d(DbgTAG, "onSuccess --<");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                // Progress notification
                Log.d(DbgTAG, "onProgress -->");
                Log.d(DbgTAG, "  bytesWritten: " + bytesWritten);
                Log.d(DbgTAG, "  totalSize: " + totalSize);
                mPublishIndicator = mPublishIndicator > 200 ? 0 : mPublishIndicator+1;
                if (mPublishIndicator == 200) {
                    mIndicator = mIndicator >= 3 ? 0 : mIndicator + 1;
                    StringBuilder tmpStr = new StringBuilder("Получение даных");
                    for (int i = 0; i < mIndicator; i++) {
                        tmpStr.append(".");
                    }
                    mCallback.onPublishProcess(tmpStr.toString());
                }
                Log.d(DbgTAG, "onProgress --<");
            }

            @Override
            public void onFinish() {
                Log.d(DbgTAG, "onFinishUI -->");
                mBusy = false;
                mIsWork = false;
                Log.d(DbgTAG, "onFinishUI --<");
            }
        });
        Log.d(DbgTAG, "postData --<");
    }
}