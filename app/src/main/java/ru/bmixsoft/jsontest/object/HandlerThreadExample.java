package ru.bmixsoft.jsontest.object;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Михаил on 12.05.2016.
 */

public class HandlerThreadExample {

    private MyHandlerThread mMyHandlerThread;
    private Looper mLooper;
    private Handler mHandler;

    public HandlerThreadExample(){
        mMyHandlerThread = new MyHandlerThread();
        mMyHandlerThread.start();
        mLooper = mMyHandlerThread.getLooper();
    }
    public void callHello() {
        mHandler.sendEmptyMessage(1);
    }

    private class MyHandlerThread extends HandlerThread {
        private HelloLogger mHelloLogger;
        private Handler mHandler;
        public MyHandlerThread() {
            super("The MyHandlerThread thread", HandlerThread.NORM_PRIORITY);
        }
        public void run (){
            mHelloLogger = new HelloLogger();
            mHandler = new Handler(getLooper()){
                public void handleMessage(Message msg){
                    mHelloLogger.logHello();
                }
            };
            super.run();
        }
    }
    private class HelloLogger {
        public HelloLogger (){
        }
        public void logHello(){
            Log.d("HandlerThreadExample", "Hello World");
        }
    }
}