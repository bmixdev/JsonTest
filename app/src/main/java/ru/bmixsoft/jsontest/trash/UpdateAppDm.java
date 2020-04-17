package ru.bmixsoft.jsontest.trash;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

import ru.bmixsoft.jsontest.R;

/**
 * Created by Михаил on 08.12.2017.
 */

public class UpdateAppDm {
/*
    Context mActivityContext;

    public UpdateAppDm(Context context)

    {
        mActivityContext = context;

        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "AppName.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server
        String url = mActivityContext.getResources().getString(R.string.update_app_url);

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
       // request.setDescription(mActivityContext.getResources().getString(R.string.notification_description));
        request.setTitle(mActivityContext.getResources().getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) mActivityContext.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri,
                        manager.getMimeTypeForDownloadedFile(downloadId));
                mActivityContext.getApplicationContext().startActivity(install);

                mActivityContext.getApplicationContext().unregisterReceiver(this);
                //finish();
            }
        };
        //register receiver for when .apk download is compete
        mActivityContext.getApplicationContext().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
*/
}
