package ru.bmixsoft.jsontest.trash;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.List;

import ru.bmixsoft.jsontest.utils.Utils;

public class ServiceHandler {
	
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler(Context contex) throws Exception {
		if (! isOnline(contex)){
			throw new Exception("Интернет-соединение отсутствует!");
			//Toast.makeText(contex, "Интернет-соединение не доступно!", Toast.LENGTH_LONG).show();
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
	
	
    public String makeServiceCall(String url, int method){
		
        return  this.makeServiceCall(url, method, null);
    }




    public String makeServiceCall(String url, int method, List<NameValuePair> params){
        try{
            HttpParams my_httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(my_httpParams, 10000); // 10 сек
            HttpConnectionParams.setSoTimeout(my_httpParams, 10000);

            DefaultHttpClient httpClient = new DefaultHttpClient(my_httpParams);
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            if(method == POST){
                HttpPost httpPost = new HttpPost(url);
                StringEntity se = new StringEntity("{'string':'string'}", HTTP.UTF_8);
                se.setContentType("application/json; charset=UTF-8");
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                if(params!=null){
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);
            } else if (method == GET){
                if (params!=null){
                    String paramsString = URLEncodedUtils.format(params, "");
                    url += "?" + paramsString;
                }

                HttpGet httpGet = new HttpGet(url);
                httpResponse = httpClient.execute(httpGet);

            }

            httpEntity = httpResponse.getEntity();
            //response = EntityUtils.toString(httpEntity,"UTF-8");//"iso-8859-1");
			is = httpEntity.getContent();
        }catch (UnsupportedEncodingException e){
            Utils.safePrintError(e); // e.printStackTrace();
        }
        catch (SocketTimeoutException e)
        {
            // handle timeouts
            Utils.safePrintError(e); // e.printStackTrace();
        }
        catch (ClientProtocolException e){
            Utils.safePrintError(e); // e.printStackTrace();
        } catch (IOException e){
            Utils.safePrintError(e); // e.printStackTrace();
        }
		

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is
																			 , "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

        return json;

    }
}

