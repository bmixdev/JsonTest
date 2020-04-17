package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.UUID;

/**
 * Created by Михаил on 21.04.2016.
 */
public class DeviceFragment extends Fragment{
    /*
    public static final String EXTRA_DEVICE_ID = "adddevicelintent.DEVICE_ID";
    private Device mDevice;
    private EditText mEdtDeviceName;
    private EditText mEdtUrl;
    private CheckBox mIsActive;
    private CheckBox mUseService;
    private RadioGroup mRGTypeResp;

    Callbacks mCallbacks;

    private DeviceServicesFragment mServicFragment;


    public interface Callbacks {
        void onDeviceUpdated(Device device);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static DeviceFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DEVICE_ID, crimeId);

        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID deviceId = (UUID)getArguments().getSerializable(EXTRA_DEVICE_ID);
        mDevice = DeviceLib.get4LpuCode(getActivity()).getDevice(deviceId);

        setHasOptionsMenu(true);
    }

    @Override
    @TargetApi(11)
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_device, parent, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            try {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        mEdtDeviceName = (EditText) v.findViewById(R.id.device_edt_name);
        mEdtDeviceName.setText(mDevice.getTitle());
        mEdtDeviceName.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mDevice.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
                mCallbacks.onDeviceUpdated(mDevice);
            }
        });

        mEdtUrl = (EditText) v.findViewById(R.id.device_edt_url);
        mEdtUrl.setText(mDevice.getUrl());
        mEdtUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDevice.setUrl(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCallbacks.onDeviceUpdated(mDevice);
            }
        });

        mIsActive = (CheckBox) v.findViewById(R.id.device_chk_isactive);
        mIsActive.setChecked(mDevice.isActive());
        mIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDevice.setIsActive(isChecked);
            }
        });

        mUseService = (CheckBox)v.findViewById(R.id.device_chk_use_service) ;
        mUseService.setChecked(mDevice.isUseServices());
        mUseService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDevice.setUseServices(isChecked);
            }
        });

        mRGTypeResp = (RadioGroup) v.findViewById(R.id.act_device_rg_type_resp);
        try {
            switch (mDevice.getTypeResponse().toString()) {
                case "JSON":
                    mRGTypeResp.check(R.id.act_device_rg_type_resp_i_json);
                    break;
                case "XML":
                    mRGTypeResp.check(R.id.act_device_rg_type_resp_i_xml);
                    break;
                case "TXT":
                    mRGTypeResp.check(R.id.act_device_rg_type_resp_i_txt);
                    break;
            }
        } catch (NullPointerException e)
        { e.printStackTrace(); }
        mRGTypeResp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton chkBtn = (RadioButton)group.findViewById(checkedId);
                    boolean isChecked = chkBtn.isChecked();
                    if (isChecked)
                    {
                        switch (checkedId){
                            case R.id.act_device_rg_type_resp_i_json:
                                mDevice.setTypeResponse(MyHelper.typeResponse.JSON);
                                break;
                            case R.id.act_device_rg_type_resp_i_xml:
                                mDevice.setTypeResponse(MyHelper.typeResponse.XML);
                                break;
                            case R.id.act_device_rg_type_resp_i_txt:
                                mDevice.setTypeResponse(MyHelper.typeResponse.TXT);
                                break;
                        }

                    }
            }
        });


        FragmentManager manager = getActivity().getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentDeviceServiceContainer);

        if (fragment == null) {
            mServicFragment = DeviceServicesFragment.newInstance(mDevice.getId());
            manager.beginTransaction()
                    .add(R.id.fragmentDeviceServiceContainer, mServicFragment)
                    .commit();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mServicFragment != null){
            mServicFragment.updateUI();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DeviceLib.get4LpuCode(getActivity()).saveDevice();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
}