package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v4.app.ListFragment;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Михаил on 27.04.2016.
 */
public class DeviceServicesFragment extends ListFragment {

    /*
    public static final String EXTRA_DEVICE_ID = "DeviceServicesFragment.DEVICE_ID";

    private Device mDevice;
    private DeviceService mDeviceService;
    private ArrayList<DeviceService> mDeviceServices;
    private TextView mBtnDelete;

    private ListView mListView;

    Callbacks mCallbacks;


    private static final String DLGSERV_NAME = "dlg_serv_title";
    private static final String DLGSERV_URL = "dlg_serv_url";
    private static final String DLGSERV_CODE = "dlg_serv_code";
    private static final int REQUEST_DLGSERV = 0;



    public interface Callbacks {
        void onDevieServiceUpdate(Device device);
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

    public static DeviceServicesFragment newInstance(UUID deviceId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DEVICE_ID, deviceId);

        DeviceServicesFragment fragment = new DeviceServicesFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID deviceId = (UUID)getArguments().getSerializable(EXTRA_DEVICE_ID);
        mDevice = DeviceLib.get4LpuCode(getActivity()).getDevice(deviceId);
        mDeviceServices = mDevice.getDeviceServices();

        DeviceServiceAdapter viewAdapter = new DeviceServiceAdapter(mDeviceServices);


        setListAdapter(viewAdapter);
        setRetainInstance(true);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListView = (ListView) inflater.inflate(R.layout.slidemenu, null);
        View footerView = inflater.inflate(R.layout.footer_list, null);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addService();
            }
        });
                //((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_list, null, false);

        mListView.addFooterView(footerView,null, false);
        return mListView;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (id == R.id.footer_layout)
        {}
        else {
            mDeviceService = (DeviceService) (getListAdapter()).getItem(position);
            Toast.makeText(getContext(), mDeviceService.getId().toString(), Toast.LENGTH_LONG).show();
        }
    }

    //Адаптер для обновления ListView списка девайсов
    private class DeviceServiceAdapter extends ArrayAdapter<DeviceService> {
        public DeviceServiceAdapter(ArrayList<DeviceService> services)
        {
            super(getActivity(), android.R.layout.simple_list_item_1, services);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //если представление пустое, то присваиваем макет
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_lv_devic_service, null);
            }
            //получаем объект
            mDeviceService = getItem(position);

            //заполняем элементы макета
            LinearLayout tv_fmt_device_src_id = (LinearLayout) convertView.findViewById(R.id.fmt_device_src_id);
            tv_fmt_device_src_id.setContentDescription(mDeviceService.getId().toString());

            TextView tv_fmt_device_src_title = (TextView)convertView.findViewById(R.id.fmt_device_src_title);
            tv_fmt_device_src_title.setText(mDeviceService.getTitle()+" ["+mDeviceService.getUrl()+"]");


            mBtnDelete = (TextView)convertView.findViewById(R.id.frag_ds_btn_delete);
            mBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeviceService = getItem(position);
                    deleteService(mDeviceService);
                }
            });

            return convertView;
        }

    }

    private void deleteService(DeviceService ds)
    {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.alrt_del_serv_title)
                .setMessage(R.string.alrt_del_serv_msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        mDevice.deleteService(mDeviceService);
                        mCallbacks.onDevieServiceUpdate(mDevice);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()
                .show();
    }

    public void updateUI()
    {
        ((DeviceServiceAdapter)getListAdapter()).notifyDataSetChanged();
        MyHelper.getListViewSize(mListView);
    }

    void addService()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogEditPolisFragment dialog = DialogEditPolisFragment.newInstance(null, null, null);
        dialog.setTargetFragment(DeviceServicesFragment.this, REQUEST_DLGSERV);
        dialog.show(fm, DLGSERV_NAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DLGSERV){
            String dlg_serv_title = (String)data.getSerializableExtra(DialogEditPolisFragment.EXTRA_SERV_TITLE);
            String dlg_serv_url = (String)data.getSerializableExtra(DialogEditPolisFragment.EXTRA_SERV_URL);
            String dlg_serv_code = (String)data.getSerializableExtra(DialogEditPolisFragment.EXTRA_SERV_CODE);
            DeviceService tmp_device_service = new DeviceService();
            tmp_device_service.setTitle(dlg_serv_title);
            tmp_device_service.setUrl(dlg_serv_url);
            tmp_device_service.setCode(dlg_serv_code);
            mDevice.addService(tmp_device_service);
            updateUI();
        }
    }
*/

}
