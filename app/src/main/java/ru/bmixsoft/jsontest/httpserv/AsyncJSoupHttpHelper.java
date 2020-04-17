package ru.bmixsoft.jsontest.httpserv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;

import java.util.HashMap;

import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Mike on 19.02.2018.
 */

public class AsyncJSoupHttpHelper extends AsyncTask<Void, String, HashMap<String, Object>> {

    private static final String DbgTAG = "AsyncJSoupHttpHelper";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private static AsyncJSoupHttpHelper instance;
    private Callback mCallback;
    private ExecInBackground mExecInBackground;
    private String mMsgDialog;
    private boolean mShowProcDialog = false;

    public static final String hmk_success = "success";
    public static final String hmk_result = "result";
    public static final String hmk_result_head_txt = "result_head_txt";
    public static final String hmk_result_body_txt = "result_doby_txt";


    public interface Callback {
        public void onRefreshUI(HashMap<String, Object> obj);
    }

    public interface ExecInBackground {
        public HashMap<String, Object> exec(AsyncJSoupHttpHelper longTask);
    }

    public void setProcDialog(boolean isShow, String msgDialog) {
        mShowProcDialog = isShow;
        mMsgDialog = msgDialog;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setExecInBackground(ExecInBackground execInBackground) {
        mExecInBackground = execInBackground;
    }

    public AsyncJSoupHttpHelper(Context context) {
        mContext = context;
    }

    public void publishProgress(String msg) {
        publishProgress(msg);
    }

    //onPreExecute() – выполняется перед doInBackground(). Имеет доступ к UI
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mShowProcDialog) {
            try {
                mProgressDialog.setMessage(mMsgDialog);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            } catch (Exception e) {
               // Utils.safePrintError(e);
               // Utils.printCrashToFireBase("AsyncJSoupHttpHelper.onPreExecute", "Ошибка отображения диалога", e);
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setMessage(mMsgDialog);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }
        if (mContext instanceof Activity)
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    //doInBackground() – основной метод, который выполняется в новом потоке. Не имеет доступа к UI.
    @Override
    protected HashMap<String, Object> doInBackground(Void... params) {

        if (!Utils.isOnline((mContext).getApplicationContext())) {
            ///throw new Exception("Интернет-соединение отсутствует!");
            //Utils.msgError("Интернет-соединение отсутствует!");
            HashMap<String, Object> map = new HashMap<>();
            map.put(hmk_success, 0);
            map.put(hmk_result, "Сетевое соединение отсутствует!");
            return map;
        }

        try {
            if (mExecInBackground != null)
                return mExecInBackground.exec(this);
        } catch (Exception e) {
            Utils.printCrashToFireBase("AsyncJSoupHttpHelper.doInBackground", "Ошибка выполнения mExecInBackground.exec", e);
        }
        return null;
    }

    //onPostExecute() – выполняется после doInBackground() (может не вызываться, если AsyncTask был отменен). Имеет доступ к UI. Используйте его для обновления пользовательского интерфейса, как только ваша фоновая задача завершена. Данный обработчик при вызове синхронизируется с потоком GUI, поэтому внутри него вы можете безопасно изменять элементы пользовательского интерфейса.
    @Override
    protected void onPostExecute(HashMap<String, Object> obj) {
        super.onPostExecute(obj);
        try {
            if (mCallback != null)
                mCallback.onRefreshUI(obj);
        } catch (Exception e) {
            Utils.safePrintError(e);
            Utils.printCrashToFireBase("AsyncJSoupHttpHelper.onPostExecute", "Ошибка выполнения mCallback.onRefreshUI(obj)", e);
        } finally {
            try {
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            } catch (Exception e) {
                Utils.safePrintError(e);
                Utils.printCrashToFireBase("AsyncJSoupHttpHelper.onPostExecute", "Ошибка освобождения mProgressDialog", e);
            }
            if (mContext instanceof Activity)
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }

    }

    //onProgressUpdate(). Имеет доступ к UI. Переопределите этот обработчик для публикации промежуточных обновлений в пользовательский интерфейс. При вызове он синхронизируется с потоком GUI, поэтому в нём вы можете безопасно изменять элементы пользовательского интерфейса
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (mProgressDialog != null)
            mProgressDialog.setMessage(values[0]);
    }


}