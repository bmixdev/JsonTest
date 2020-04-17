package ru.bmixsoft.jsontest.httpserv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Mike on 19.02.2018.
 */

public class ThreadJSoupHttpHelper extends Thread{

    private static final String DbgTAG = "AsyncJSoupHttpHelper";
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private static ThreadJSoupHttpHelper instance;
    private Callback mCallback;
    private ExecInBackground mExecInBackground;
    private String mMsgDialog;
    private boolean mShowProcDialog = false;

    public Handler myHandler;
    private final int sleep_time = 1 * 1000; // 1 сек
    private final int wait_sec = 10 ;
    public boolean isWorked = true;
    private final AtomicBoolean started = new AtomicBoolean(false);


    static final int MSG_OK = 1;
    static final int MSG_PROC = 2;


    public static final String hmk_success = "success";
    public static final String hmk_result = "result";
    public static final String hmk_result_head_txt = "result_head_txt";
    public static final String hmk_result_body_txt = "result_doby_txt";


    public interface Callback {
        public void onRefreshUI(HashMap<String, Object> obj);
    }

    public interface ExecInBackground {
        public HashMap<String, Object> exec(ThreadJSoupHttpHelper longTask);
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

    public void publishProgress(String procMsg) {
        Bundle bundle = new Bundle();
        Message msg = new Message();
        bundle.putString("ProcMsg", procMsg);
        msg = myHandler.obtainMessage(MSG_PROC);
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }



    public ThreadJSoupHttpHelper(Context context) {
        mContext = context;
        //   setName(threadName);
        myHandler = new Handler(){
            @Override
            public synchronized void handleMessage(Message msg) {
                int what = msg.what;
                switch (what){
                    case MSG_OK:
                        //поток остановлен
                        isWorked = false;
                        HashMap<String, Object> obj = (HashMap<String, Object>) msg.getData().getSerializable("HashMap");
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

                        break;
                    case MSG_PROC:
                        // поток работает
                        String procMsg = msg.getData().getString("ProcMsg");
                        if (mProgressDialog != null)
                            mProgressDialog.setMessage(procMsg);
                        break;
                }
            }
        };

    }

    @Override
    public void run()
    {
        //  Looper.prepare();
//        while (isWorked) {
            try {
                Bundle bundle = new Bundle();
                Message msg = new Message();

                HashMap<String, Object> map = new HashMap<>();

                if (!Utils.isOnline((mContext).getApplicationContext())) {
                    map.put(hmk_success, 0);
                    map.put(hmk_result, "Сетевое соединение отсутствует!");
                }

                try {
                    if (mExecInBackground != null)
                        map = mExecInBackground.exec(this);
                } catch (Exception e) {
                    Utils.printCrashToFireBase("AsyncJSoupHttpHelper.doInBackground", "Ошибка выполнения mExecInBackground.exec", e);
                }

                bundle.putSerializable("HashMap", map);
                msg = myHandler.obtainMessage(MSG_OK);
                msg.setData(bundle);
                myHandler.sendMessage(msg);
    //            this.sleep(sleep_time);
            } catch (Exception e) {
                Utils.safePrintError(e); //  e.printStackTrace();
            }
  //      }
        //       Looper.loop();
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start() {
        if (!started.getAndSet(true)) {

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
            isWorked = true;
            super.start();
        }
    }

}