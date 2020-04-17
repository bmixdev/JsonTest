package ru.bmixsoft.jsontest.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.widget.TextView;


import java.util.UUID;

import ru.bmixsoft.jsontest.activity.SingleFragmentActivity;
import ru.bmixsoft.jsontest.fragment.DeviceFragment;
import ru.bmixsoft.jsontest.fragment.DeviceServicesFragment;
import ru.bmixsoft.jsontest.fragment.MyFragment;

/**
 * Created by Михаил on 21.04.2016.
 */
public class DeviceActivity extends SingleFragmentActivity
//implements DeviceFragment.Callbacks, DeviceServicesFragment.Callbacks
{

    @Override
    protected MyFragment createFragment() {
        return null;
        /*
        UUID deviceId = (UUID)getIntent()
                .getSerializableExtra(DeviceFragment.EXTRA_DEVICE_ID);
        return DeviceFragment.newInstance(deviceId);
*/
    }
/*

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public void onDeviceUpdated(Device device) {
        // do nothing
        FragmentManager fm = getSupportFragmentManager();
        DeviceServicesFragment listFragment = (DeviceServicesFragment)
                fm.findFragmentById(R.id.fragmentDeviceServiceContainer);
        listFragment.updateUI();
    }

    public void onDevieServiceUpdate(Device device)
    {
        FragmentManager fm = getSupportFragmentManager();
        DeviceServicesFragment listFragment = (DeviceServicesFragment)
                fm.findFragmentById(R.id.fragmentDeviceServiceContainer);
        listFragment.updateUI();
    }
    */
}
