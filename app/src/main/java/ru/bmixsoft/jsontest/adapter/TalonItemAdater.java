package ru.bmixsoft.jsontest.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.activity.GetTalonActivity;
import ru.bmixsoft.jsontest.httpserv.Actions;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.model.Doctor;
import ru.bmixsoft.jsontest.model.LPU;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.model.Speciality;
import ru.bmixsoft.jsontest.model.Talon;
import ru.bmixsoft.jsontest.model.TalonInCalendar;
import ru.bmixsoft.jsontest.utils.CalendarHelper;
import ru.bmixsoft.jsontest.utils.PermissionsHelper;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 22.11.2016.
 */
public class TalonItemAdater extends ArrayAdapter<Talon> {

    private Activity mContext;
    private ArrayList<Talon> mItems;
    LinearLayout mLayoutHeader;
    private DBHelper myDb;

    private final static int REQUEST_GET_TALON = 0;

    private Callback mCallback;

    public static final int ACTION_DELETE = 0;

    public interface Callback {
        public void OnChangeTalon(Talon talon, int action);
    }

    public void registerCallBack(Callback callback) {
        mCallback = callback;
    }

    public TalonItemAdater(Context context, ArrayList<Talon> items, Callback callback) {
        super(context, 0, items);
        mContext = (Activity) context;
        mItems = items;
        myDb = DBFactory.getInstance(getContext().getApplicationContext()).getDBHelper(Talon.class);
        registerCallBack(callback);
    }

    /*

    private void editPolis(Polis polis){
        if (Utils.isDebugMode(getContext().getApplicationContext(), false))  Utils.d(getContext().getApplicationContext(), polis.toString());
        mCallback.OnChangePolis(polis);
//        notifyDataSetChanged();

    }


    private void deletePolis(Polis polis){
        if (Utils.isDebugMode(getContext().getApplicationContext(), false))  Utils.d(getContext().getApplicationContext(), polis.toString());
        remove(polis);
        myDb.refreshDataOnArrayList(mItems, Polis.class, true);

    }
*/

    private void deleteEventInCalendar(Talon talon)
    {
        ArrayList<TalonInCalendar> list = (ArrayList<TalonInCalendar>) myDb.getArrayList("talonNum = ?",new String[]{talon.getStubNum()}, TalonInCalendar.class, "");
        if (list != null && list.size() > 0)
        {
            try {
                for (TalonInCalendar tic : list) {
                    CalendarHelper.removeEventNew(mContext.getApplicationContext(), tic.getEventId());
                    myDb.delete("eventId = ?", new Integer[]{tic.getEventId()},TalonInCalendar.class);
                }
            }
            catch (Exception e)
            {
                Utils.showErrorDlg((FragmentActivity) mContext,"Ошибка удаления события из календаря:",  e.getMessage() +"\n"+Utils.errStack(e));
            }
        }
    }

    private void delete(final Talon talon){

        PermissionsHelper.verifyCalendarPermissions(mContext);

        new AlertDialog.Builder(getContext())
                .setTitle("Подтверждение действия")
                .setMessage("Вы уверены, что хотите отменить запись к "+talon.getV_Family()+" "+talon.getV_Name()+" "+talon.getV_Ot()+"\nКабинет №"+talon.getRoomNum()+" в "+talon.getTime_from()+" "+talon.getShortDate()+"?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        HttpServ httpServ = HttpServ.getInstance(mContext);
                        httpServ.postCancelVisit(Polis.getPolis(talon.getPolisId()), talon, new HttpServ.Callback()
                        {
                            @Override
                            public void onProc(HashMap<String, Object> result) {

                            }

                            @Override
                            public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                                if (result != null){
                                    if (result.containsKey(Actions.RESULT_KEY_SUCCESS))
                                    {
                                        deleteEventInCalendar(talon);
                                        remove(talon);
                                        myDb.refreshDataOnArrayList(mItems, Talon.class, true);
                                    }
                                }
                            }
                        });
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

    private void reWrite(final Talon curTalon)
    {
        Utils.msgInfo("Перезапись талона на другое время");
        /*
        Intent i = new Intent(mContext, GetLpuForPolis.class);
        i.putExtra(LpuForPolisFragment.INTENT_OPT_POLIS, curTalon.getPolisId());
        mContext.startActivity(i);
        */

        Intent i = new Intent(mContext, GetTalonActivity.class);
        i.putExtra(GetTalonActivity.TAG_ACTION, GetTalonActivity.actionReWrite);
        i.putExtra(GetTalonActivity.TAG_CITY_ID, LPU.get4LpuCode(curTalon.getLpu()).getCITY());
        i.putExtra(GetTalonActivity.TAG_POLIS_ID, curTalon.getPolisId());
        i.putExtra(GetTalonActivity.TAG_LPU_ID, LPU.get4LpuCode(curTalon.getLpu()).getID());
        i.putExtra(GetTalonActivity.TAG_SPEC_ID, curTalon.getSpec());
        i.putExtra(GetTalonActivity.TAG_OLD_TALON_ID, curTalon.getId());
        //i.putExtra(GetTalonActivity.TAG_DOC_ID, curTalon.getDoctor());
        mContext.startActivityForResult(i, REQUEST_GET_TALON);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mContext.getLayoutInflater().inflate(R.layout.fragment_talon_item, parent, false);

        }
        Talon talon = getItem(position);

        mLayoutHeader = (LinearLayout) convertView.findViewById(R.id.llHeader);
        mLayoutHeader.setBackgroundColor(Utils.getColorHeader());

        LinearLayout mLayoutHeader2 = (LinearLayout) convertView.findViewById(R.id.llHeaderTalon2);
        mLayoutHeader2.setBackgroundColor(Utils.getColorHeader());
        LinearLayout mLayoutHeader3 = (LinearLayout) convertView.findViewById(R.id.llHeaderTalon3);
        mLayoutHeader3.setBackgroundColor(Utils.getColorHeader());
        LinearLayout mLayoutHeader4 = (LinearLayout) convertView.findViewById(R.id.llHeaderTalon4);
        mLayoutHeader4.setBackgroundColor(Utils.getColorHeader());
        LinearLayout mLayoutHeader5 = (LinearLayout) convertView.findViewById(R.id.llHeaderTalon5);
        mLayoutHeader5.setBackgroundColor(Utils.getColorHeader());
        LinearLayout mLayoutHeader6 = (LinearLayout) convertView.findViewById(R.id.llHeaderTalon6);
        mLayoutHeader6.setBackgroundColor(Utils.getColorHeader());


        Polis mCurPolis = Polis.getPolis(talon.getPolisId());
        Doctor mDoctor = Doctor.get(talon.getDoctor());
        LPU mClinic = LPU.get4LpuCode(talon.getLpu());
        Speciality mSpec = Speciality.get(talon.getSpec());

        TextView tvPolisInfo = (TextView) convertView.findViewById(R.id.item_talon_polis_info);
        tvPolisInfo.setText(mCurPolis != null ? mCurPolis.getPolisText() : talon.getPolisId());

        TextView tvDt = (TextView) convertView.findViewById(R.id.item_talon_dt);
        tvDt.setText(talon.getDateStr().equals("null") ? Utils.strToDateWeekStr(talon.getShortDate(),"dd.MM.yyyy")+" " +talon.getTime_from() : talon.getDateStr().toString()+" "+ talon.getTimeStr().toString());

        TextView tvTalonNum = (TextView) convertView.findViewById(R.id.item_talon_talon_num_val);
        tvTalonNum.setText(talon.getStubNum());

        TextView tvSpec = (TextView) convertView.findViewById(R.id.item_talon_spec);
        tvSpec.setText((mSpec == null || mSpec.getId().equals("0") ? talon.getPRVSName() : mSpec.getName()) + "\nКабинет № "+ talon.getRoomNum().toString());

        TextView tvDoct = (TextView) convertView.findViewById(R.id.item_talon_doct);
        tvDoct.setText(mDoctor == null || mDoctor.getId().equals("0") ? talon.getV_Family()+" "+talon.getV_Name()+" "+talon.getV_Ot() :mDoctor.getFio());

        TextView tvClinic = (TextView) convertView.findViewById(R.id.item_talon_clinic);
        if (mClinic != null)
            tvClinic.setText(mClinic.getNAME()+"\nтел.: "+mClinic.getPHONE()+"\nemail: "+mClinic.getEMAIL());

        TextView tvClinicAdr = (TextView) convertView.findViewById(R.id.item_talon_clinic_adr);
        if (mClinic != null)
            tvClinicAdr.setText(mClinic.getADDRESS());

        TextView tvDbg = (TextView) convertView.findViewById(R.id.item_talon_dbg_txt);
        if (Utils.isDebugMode()) {
            tvDbg.setText(talon.toString());
            tvDbg.setVisibility(Utils.isDebugMode() == true ? View.VISIBLE : View.INVISIBLE);
        }
        ImageButton btnTalonInfo = (ImageButton) convertView.findViewById(R.id.btnTalonInfo);
        btnTalonInfo.setTag(talon);
        btnTalonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCallback.OnChangeTalon((Talon)v.getTag(), ACTION_DELETE);
               delete((Talon)v.getTag());
            }
        });

        ImageButton btnReWrite = (ImageButton) convertView.findViewById(R.id.btnTalonReWrite);
        btnReWrite.setTag(talon);
        btnReWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reWrite((Talon)v.getTag());
            }
        });


        return convertView;
    }
}
