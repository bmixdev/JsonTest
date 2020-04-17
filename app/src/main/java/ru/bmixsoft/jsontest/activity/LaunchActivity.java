package ru.bmixsoft.jsontest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.utils.PermissionsHelper;
import ru.bmixsoft.jsontest.utils.UpdateHelper;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 12.05.2016.
 */
public class LaunchActivity extends Activity {

    public static final int REQUEST_MAIN = 0;
    private final static long SPLASH_DELAY = 10 * 1000;
    private TextView mTitle;
    private TextView mBottom;
    private UpdateHelper mUpdateHelper;
    private WebView mWebView;
    private boolean isAppStarted = false;
    private final Handler mHandler = new Handler();
    private final Runnable mSplashTask = new Runnable() {
        @Override
        public void run() {

            doStartMainActivity();

          //  finish();
        }
    };

    private MyThread mMyThread;

    public Handler myHandler;



    protected void doStartMainActivity()
    {
        //!!!! убрать
        //DBFactory.getInstance(this.getApplicationContext()).dropDataBase();
        //Intent serverIntent = new Intent(this, MainActivity.class);
        isAppStarted = true;
        Utils.initTune(getApplicationContext(), false);
        Intent serverIntent = new Intent(this, MainActivityNew.class);
        startActivityForResult(serverIntent, REQUEST_MAIN);
    };

    protected void startService()
    {
      //  if (PollService.isServiceAlarmOn(this))
      //   PollService.setServiceAlarm(this, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.launch_activity);

        startService();
        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.launch_tv_title);
        mTitle.setText(R.string.launch_title);
        mBottom = (TextView) findViewById(R.id.launch_tv_bottom);
        mBottom.setText(getResources().getString(R.string.launch_bottom)+"\nversion "+ UpdateHelper.getVersionBuilderName());

        mWebView = (WebView) findViewById(R.id.launch_webViewBaner);
        mWebView.loadUrl("https://covid.mz.mosreg.ru/");

        // обновление версии программы

/*
        Button mBtnRunThread = (Button)findViewById(R.id.btn_run_thread);
        mBtnRunThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRunThread();
            }
        });
        mBtnRunThread.setVisibility(View.INVISIBLE);

        Button mBtnStopThread = (Button)findViewById(R.id.btn_stop_thread);
        mBtnStopThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    doStopThread();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mBtnStopThread.setVisibility(View.INVISIBLE);
*/
        doRunThread();
    }

    public synchronized void doRunThread(){
        //if (mMyThread == null) {
            mMyThread = new MyThread();
        //}
       // if (!mMyThread.isAlive())
        mMyThread.start();
        if (mMyThread.myHandler != null) {
            Message msg;
            msg = mMyThread.myHandler.obtainMessage(MyThread.MSG_PROC);
            mMyThread.myHandler.sendMessage(msg);
        }

    }

    public synchronized void doStopThread() throws InterruptedException {
        if (mMyThread != null){
            mMyThread.interrupt();
            mMyThread.myHandler.obtainMessage();
          //  mMyThread.isWorked = false;
            Message msg = mMyThread.myHandler.obtainMessage(MyThread.MSG_OK);
            mMyThread.myHandler.sendMessage(msg);
          //  mMyThread.stop("mMyThread");
         //   mMyThread.interrupt();
         //   if (!mMyThread.isAlive())
           // {
                mMyThread = null;
           // }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        PermissionsHelper.verifyStoragePermissions(this);
        PermissionsHelper.verifyCalendarPermissions(this);
        if (mUpdateHelper == null) {
            mUpdateHelper = new UpdateHelper(this, new UpdateHelper.CallbackPostExec() {
                @Override
                public void OnPostExecute() {
                   startApp();
                }
            });
            mUpdateHelper.updateVersion();
        }
        //mHandler.removeCallbacks(mSplashTask);
        //mHandler.postDelayed(mSplashTask, SPLASH_DELAY);
    }

    private void startApp()
    {
        mHandler.removeCallbacks(mSplashTask);
        mHandler.postDelayed(mSplashTask, SPLASH_DELAY);
    }

    @Override
    protected void onPause() {
        mHandler.removeCallbacks(mSplashTask);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // mMyThread.myHandler.getLooper().quit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MAIN)
        {
            if (requestCode == getResources().getInteger(R.integer.RESULT_CLOSE_ALL) )
                finish();
        }
    }

    public class MyThread extends Thread {
        static final int MSG_OK = 1;
        static final int MSG_PROC = 2;
        public Handler myHandler;
        private final int sleep_time = 1 * 1000; // 1 сек
        private final int wait_sec = 5 ;
        public boolean isWorked = true;

        private int mIdx = 0;

        public MyThread(){
         //   setName(threadName);
            myHandler = new Handler(){
                @Override
                public synchronized void handleMessage(Message msg) {
                    int what = msg.what;
                    switch (what){
                        case MSG_OK:
                            isWorked = false;
                            if (!isAppStarted)
                                startApp();
                      //      mTitle.setText("Поток остановлен");
                            break;
                        case MSG_PROC:
                            //isWorked = true;
                          //  Bundle arg = msg.getData();
                          //  String txt_proc = arg.getString("IDX");
                          //  mTitle.setText(txt_proc);
                            break;
                    }
                }
            };

        }

        @Override
        public void run()
        {
          //  Looper.prepare();
            while (isWorked) {
                try {
                    mIdx = mIdx + 1;
                    Bundle bundle = new Bundle();
                    Message msg = new Message();
                    //если прождал больше положенных секунд - запустить приложение
                    if (mIdx >= wait_sec)
                    {
                        msg = myHandler.obtainMessage(MSG_OK);
                    }
                    else {
                        // bundle.putString("IDX", String.valueOf(mIdx));
                        msg = myHandler.obtainMessage(MSG_PROC);
                    }
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                    this.sleep(sleep_time);
                } catch (InterruptedException e) {
                    Utils.safePrintError(e); //  e.printStackTrace();
                }
            }
     //       Looper.loop();
        }

        private final AtomicBoolean started = new AtomicBoolean(false);
        /* (non-Javadoc)
         * @see java.lang.Thread#start()
         */
        @Override
        public synchronized void start() {
            if (!started.getAndSet(true)) {
                super.start();
                isWorked = true;
            }
        }

    }

}
