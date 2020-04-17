package ru.bmixsoft.jsontest.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.loopj.android.http.RequestParams;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import ru.bmixsoft.jsontest.BuildConfig;
import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.fragment.options.Option;
import ru.bmixsoft.jsontest.httpserv.Actions;
import ru.bmixsoft.jsontest.httpserv.HttpServ;

/**
 * Created by Михаил on 06.12.2017.
 */

public class UpdateHelper {

    private Context mActivityContext;
    private DBHelper mDBHelper;
    private int mBuildVersionCode;
    private int mCurVersionCode = getVersionBuilder();
    private boolean mIsNeedUpdate = false;
    private String mBuildVersionName;
    private HashMap<String, String> mResultSoap;
    private LibOption mLibOption;
    private HttpServ mHttpServ;

    private MyAsyncHttp.CallbackNextRequest mCallbackNextRequest;
    private MyAsyncHttp.Callback mCallbackPostExecuteUi;
    private MyAsyncHttp.CallbackParserRequest mCallbackParserRequest;
    private CallbackPostExec mCallbackPostExec;

    public interface CallbackPostExec{
        public void OnPostExecute();
    }

    public UpdateHelper(Context contextActivity, CallbackPostExec callbackPostExec)
    {
        mActivityContext = contextActivity;
        mDBHelper = DBFactory.getInstance(mActivityContext.getApplicationContext()).getDBHelper(Option.class);
        mBuildVersionCode = BuildConfig.VERSION_CODE;
        mBuildVersionName =  BuildConfig.VERSION_NAME;
        mResultSoap = new HashMap<String, String>();
        mCallbackPostExec = callbackPostExec;
        Utils.mContext = mActivityContext;
        //LibOption.setOption(contextActivity, "cntRunApp", LibOption.getOptionValueInt(contextActivity,"cntRunApp") + 1);
    }

    public void downloadNewApk(final String apkFileName)
    {
        String soapUploadConfig = mActivityContext.getString(R.string.update_app_url_path) + apkFileName;
        RequestParams requestParams = new RequestParams();
        mResultSoap.clear();
        // выполнить запрос
        mHttpServ.process(Actions.HTTP_REQ_GET
                , soapUploadConfig
                , requestParams
                , "Загрузка новой версии с сервера"
                , new MyAsyncHttp.Callback() {
                    @Override
                    public void onPostExecuteUI(String response, boolean success) {
                        ((Activity) mActivityContext).finish();
                    }
                }
                , null
                , null
                , new MyAsyncHttp.CallbackParserRequestByte() {
                    @Override
                    public void onParse(MyAsyncHttp longTask, byte[] responce, ProgressDialog progressDialog) {
                        try {

                            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";

                            File file = new File(destination);
                            file.mkdirs();
                            File outputFile = new File(file, apkFileName.toString());
                            if (outputFile.exists()) {
                                outputFile.delete();
                            }
                            FileOutputStream fos = new FileOutputStream(outputFile);
                            fos.write(responce);
                            fos.close();

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(destination + apkFileName.toString())), "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                            mActivityContext.startActivity(intent);


                        } catch (Exception e) {
                            Log.e("downloadNewApk", "Ошибка обновлени: " + e.getMessage());
                        }
                    }
                }
        );
    }


    public void checkUpdateApkMarket(int currentVersionCode)
    {
        mBuildVersionCode = getVersionBuilder();
        if ( currentVersionCode != mBuildVersionCode )
        {
            UpdateText.updateAppVersion(mActivityContext, currentVersionCode, mBuildVersionCode, mDBHelper);
            mLibOption.setOption("curVersionAppIsUpdate", "True");
            mLibOption.setOption("curVersionApp", BuildConfig.VERSION_NAME);
            mLibOption.setOption("curVersionAppCode", String.valueOf(mBuildVersionCode));
        }
    }


    public void checkUpdateApkHttp()
    {
        mHttpServ = HttpServ.getInstance(mActivityContext);
        String soapUploadConfig = mActivityContext.getString(R.string.update_app_url_path) + mActivityContext.getString(R.string.update_app_url_config);
        RequestParams requestParams = new RequestParams();
        mResultSoap.clear();

        //разбор результата
        mCallbackParserRequest= new MyAsyncHttp.CallbackParserRequest() {
            @Override
            public void onParse(MyAsyncHttp longTask, String responce, ProgressDialog progressDialog) {

                longTask.onPublishProcess("Обработка данных...");
                try {
                    JSONArray jsonArray = new JSONArray(responce);

                    JSONObject json = (JSONObject) jsonArray.get(0);
                    JSONObject apkInfo = json.getJSONObject("apkInfo");
                    String versionCode = apkInfo.getString("versionCode");
                    String path = json.getString("path");
                    mResultSoap.put("versionCode", versionCode);
                    mResultSoap.put("pathApk", path);

                } catch (Exception e){
                    Utils.safePrintError(e); // e.printStackTrace();
                    Utils.d("onParse: "+e.getMessage());
                }
            }
        };

        // обновление ui
        mCallbackPostExecuteUi = new MyAsyncHttp.Callback() {
            @Override
            public void onPostExecuteUI(String response, boolean success)
            {
                if ( mIsNeedUpdate || mBuildVersionCode != mCurVersionCode )
                {
                    UpdateText.updateAppVersion(mActivityContext, mCurVersionCode, mBuildVersionCode, mDBHelper);
                    mLibOption.setOption("curVersionAppIsUpdate", "True");
                    mLibOption.setOption("curVersionApp", mBuildVersionName);
                    mLibOption.setOption("curVersionAppCode", String.valueOf(mBuildVersionCode));
                }

               int soapVersionCode = Integer.valueOf(mResultSoap.get("versionCode"));
               if (soapVersionCode > mBuildVersionCode)
               {
                   new AlertDialog.Builder(mActivityContext)
                           .setTitle("Доступна новая версия")
                           .setMessage("Обновление до версии "+soapVersionCode+"\n*Исправление ошибок и другие оптимизации")
                           .setPositiveButton("Установить", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                    downloadNewApk(mResultSoap.get("pathApk"));
                               }
                           })
                           .setNegativeButton("Позже", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   // do nothing
                                   if (mCallbackPostExec != null) mCallbackPostExec.OnPostExecute();
                               }
                           })
                           .setIcon(android.R.drawable.ic_dialog_info)
                           .create()
                           .show();
               } else {
                   //Utils.d("У Вас самая актуальная версия");
                   if (mCallbackPostExec != null) mCallbackPostExec.OnPostExecute();
               }
            }
        };

        //следующий запрос
        mCallbackNextRequest = new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {

            }
        };

        // выполнить запрос
        mHttpServ.process( Actions.HTTP_REQ_GET
                        , soapUploadConfig
                        , requestParams
                        , "Получение файла конфигурации с сервера"
                        , mCallbackPostExecuteUi
                        , mCallbackNextRequest
                        , mCallbackParserRequest
                        , null
                        );
    }

    public void updateVersion() {
        try {

            //db.execSQL("DELETE FROM Option");
            mLibOption = LibOption.getInstance(mActivityContext.getApplicationContext());
            Option optionIsUpdate = mLibOption.getOption("curVersionAppIsUpdate");
            if (optionIsUpdate != null) {
                mIsNeedUpdate = ! optionIsUpdate.getValue().equals("True") ? true : false;
            } else
            {
                mIsNeedUpdate = true;
            }
            Option optionLastVersionCode = mLibOption.getOption("curVersionAppCode");
            if (optionLastVersionCode != null)
            {
                mCurVersionCode = Integer.valueOf(optionLastVersionCode.getValue());
            } else
            {
                mCurVersionCode = getVersionBuilder();
            }

            if (LibOption.getOptionValueBool(mActivityContext.getApplicationContext(), "checkNewVersionOnHttp")) {
                checkUpdateApkHttp();
            }

            checkUpdateApkMarket(Integer.valueOf(optionLastVersionCode.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
            //Utils.showErrorDlg(mActivityContext, "Обновление версии", Utils.errStack(e));
            //Utils.d(e.getMessage());
        }
        finally {
            if (mCallbackPostExec != null) mCallbackPostExec.OnPostExecute();
        }
    }

    public static int getVersionBuilder(int versionMajor, int versionMinor, int versionPatch, int versionBuild)
    {
        return versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild;
    }

    public static int getVersionBuilder()
    {
        return BuildConfig.VERSION_CODE;
    }

    public static String getVersionBuilderName() {
       return BuildConfig.VERSION_NAME.replace("_", ".");
    }

    public static void shareApp(Context context)
    {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Запись к врачу по МО");
            String sAux = "\nРекомендую установить приложение \"Запись к врачу по МО\"\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id="+context.getPackageName()+" \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            context.startActivity(Intent.createChooser(i, "Как отправить ссылку"));
        } catch(Exception e) {
            //e.toString();
        }
    }

}
