package ru.bmixsoft.jsontest.utils;

import com.loopj.android.http.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.TrustStrategy;

/**
 * Created by Михаил on 08.12.2016.
 */
public class HttpRestClient {

    private static final String BASE_URL = "";//"https://uslugi.mosreg.ru/zdrav/";

    private static SyncHttpClient client = new SyncHttpClient();
    private static int defaultTimeout = 60 * 1000; //10*1000;

    private static SSLSocketFactory getTrustSSLSocketFactory() {
        try {
            SSLContext sslContexts = SSLContexts.custom().useSSL().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

           return new SSLSocketFactory(sslContexts);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setSSLSocketFactory(getTrustSSLSocketFactory());
        client.setTimeout(defaultTimeout);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setSSLSocketFactory(getTrustSSLSocketFactory());
        client.setTimeout(defaultTimeout);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}