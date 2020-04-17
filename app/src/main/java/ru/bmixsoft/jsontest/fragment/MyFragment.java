package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import ru.bmixsoft.jsontest.service.PollService;

/**
 * Created by Михаил on 18.11.2016.
 */
public class MyFragment extends Fragment {


    protected Activity mActivity;

    public static final String TAG = "MyFragment";
    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
            Toast.makeText(getActivity(),
                    "Got a broadcast:" + intent.getAction(),
                    Toast.LENGTH_LONG)
                    .show();
                    */
            setResultCode(Activity.RESULT_CANCELED);
        }
    };


    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new
                IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mOnShowNotification, filter, PollService.PERM_PRIVATE, null);
    }
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mOnShowNotification);
    }

}
