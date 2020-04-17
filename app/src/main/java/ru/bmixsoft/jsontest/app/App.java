package ru.bmixsoft.jsontest.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;
import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.activity.MainActivityNew;
import ru.bmixsoft.jsontest.fragment.options.Option;
import ru.bmixsoft.jsontest.utils.Utils;

public class App extends Application {

     @Override
    public void onCreate() {
         /*
         Thread.UncaughtExceptionHandler rootHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread thread, Throwable e)
            {
                /*
                Intent i = new Intent();
                i.setClassName("ru.bmixsoft.jsontest.app", "ErrorActivity");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                                System.exit(0);

            }
        });
*/
         /*
         Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this.getApplicationContext()));
*/

         /*
         Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(getApplicationContext(), Thread.getDefaultUncaughtExceptionHandler()));
*/


         CaocConfig.Builder.create()
                 .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //default: CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM
                 .enabled(true) //default: true
                 .showErrorDetails(true) //default: true
                 .showRestartButton(false) //default: true
                 .logErrorOnRestart(false) //default: true
                 .trackActivities(true) //default: false
                 .minTimeBetweenCrashesMs(2000) //default: 3000
                 .errorDrawable(R.drawable.customactivityoncrash_error_image) //default: bug image
                 .restartActivity(MainActivityNew.class) //default: null (your app's launch activity)
                 .errorActivity(DefaultErrorActivity.class) //default: null (default error activity)
                // .eventListener(new YourCustomEventListener()) //default: null
                 .apply();

         Fabric.with(this, new Crashlytics());

         Toasty.Config.getInstance()
                 .setTextSize(16)
                 .apply(); // required

         Utils.mContext = getApplicationContext();
         DBHelper dbHelper = DBFactory.getInstance(getApplicationContext()).getDBHelper(Option.class);
         super.onCreate();
     }
}