package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.httpserv.Actions;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.model.CurTalon;
import ru.bmixsoft.jsontest.model.Doctor;
import ru.bmixsoft.jsontest.model.LPU;
import ru.bmixsoft.jsontest.model.LastSelectedLpu;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.model.Speciality;
import ru.bmixsoft.jsontest.model.Talon;
import ru.bmixsoft.jsontest.model.TalonInCalendar;
import ru.bmixsoft.jsontest.utils.CalendarHelper;
import ru.bmixsoft.jsontest.utils.PermissionsHelper;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 19.01.2017.
 */
public class ConfirmTalonFragment extends  MyFragment
{

    private static final String DbgTAG ="ConfirmTalonFragment";

    protected static ConfirmTalonFragment instance;

    public static final String INTENT_OLD_TALON_ID = "oldTalonId";

    private boolean recievedResult = false;


    private CurTalon mTalon;

    private Polis mCurPolis;
    private Doctor mDoctor;
    private LPU mClinic;
    private Speciality mSpec;

    private TextView mTextViewDbgTxt;
    private TextView mTextViewDbgTitle;

    private  TextView mTextViewDateTime;
    private  TextView mTextViewDoct;
    private  TextView mTextViewClinic;
    private  TextView mTextViewClinicAdr;
    private  TextView mTextViewSpec;
    private  TextView mTextViewTalonNumTxt;
    private  TextView mTextViewTalonNumVal;
    private  TextView mTextViewOldTalonNumTxt;
    private  TextView mTextViewOldTalonNumVal;
    private  Button btnConfirm;
    private  Button btnCancle;
    private CheckBox chkAddEvent;

    private String oldTalonId;
    private Talon oldTalon;

    private static final int REQUEST_FRAGMENT = 0;

    private boolean mIsSuccessReserve = false;

    public static MyFragment getInstance(Context context)
    {
        if (instance == null) {
            synchronized (MainFragment.class) {
                if (instance == null) {instance = new ConfirmTalonFragment();}
            }
        }else{}
        instance.mIsSuccessReserve = false;
        return instance;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        oldTalonId = getActivity().getIntent().getStringExtra(INTENT_OLD_TALON_ID);
        if (oldTalonId != null && !oldTalonId.isEmpty())
            oldTalon = Talon.get(oldTalonId);

        mTalon = CurTalon.get("1");
        mCurPolis = Polis.getPolis(mTalon.getPolisId());
        mDoctor = Doctor.get(mTalon.getDoctor());
        mClinic = LPU.get4LpuCode(mTalon.getLpu());
        mSpec = Speciality.get(mTalon.getSpec());
    }

    private void reservNewTalon()
    {
        HttpServ httpServ = HttpServ.getInstance(mActivity);
        httpServ.postCreateVisitNew(mCurPolis, mTalon, new HttpServ.Callback() {
            @Override
            public void onProc(HashMap<String, Object> result) {
                mTextViewDbgTitle.setText((String)result.get(Actions.RESULT_KEY_DESC));
                if ((result.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sFalse))))
                {
                    btnConfirm.setVisibility(View.INVISIBLE);
                    mTextViewDbgTitle.setTextColor(getResources().getColor(R.color.red));
                    if (Utils.isDebugMode())
                        mTextViewDbgTxt.setText((String)result.get(Actions.RESULT_KEY_RESPONSE));
                    mIsSuccessReserve = false;
                }
                else
                {
                    mTextViewDbgTitle.setTextColor(getResources().getColor(R.color.green));
                }
            }

            @Override
            public void onFinish(HashMap<String,Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                if (result != null) {

                    try {
                        if (! result.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue)))
                            throw new Exception("Ошибка резервирования талона:\n" + result.get(Actions.RESULT_KEY_DESC));
                        mTextViewDbgTitle.setText((String)result.get(Actions.RESULT_KEY_DESC));
                        mTextViewDbgTitle.setTextColor(getResources().getColor(R.color.green));
                        mTextViewTalonNumTxt.setVisibility(View.VISIBLE);
                        mTextViewTalonNumVal.setText(mTalon.getStubNum());
                        btnCancle.setVisibility(View.INVISIBLE);
                        btnConfirm.setText(getResources().getString(R.string.msg_close));
                        mTextViewTalonNumVal.setVisibility(mTextViewTalonNumTxt.getVisibility());
                        if (Utils.isDebugMode())
                            mTextViewDbgTxt.setText((String)result.get(Actions.RESULT_KEY_RESPONSE));
                        addTalon();
                        mIsSuccessReserve = true;
                    } catch (Exception e) {
                        btnConfirm.setVisibility(View.INVISIBLE);
                        mTextViewDbgTitle.setText(e.getMessage());
                        mTextViewDbgTitle.setTextColor(getResources().getColor(R.color.red));
                        if (Utils.isDebugMode())
                            mTextViewDbgTxt.setText((String)result.get(Actions.RESULT_KEY_RESPONSE));
                        mIsSuccessReserve = false;
                    }

                }
            }
        });
    }

    @TargetApi(14)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirm_new_talon, container, false);
        mTextViewDbgTxt = (TextView) v.findViewById(R.id.dlg_coftal_dbg_txt);
        mTextViewDbgTitle = (TextView) v.findViewById(R.id.dlg_coftal_dbg_title);

        mTextViewDbgTxt.setVisibility(Utils.isDebugMode() == true ? View.VISIBLE : View.INVISIBLE);

        mTextViewTalonNumTxt = (TextView) v.findViewById(R.id.dlg_conftal_talon_num_txt);
        mTextViewTalonNumVal = (TextView) v.findViewById(R.id.dlg_conftal_talon_num_val);
        mTextViewTalonNumTxt.setVisibility(View.INVISIBLE);
        mTextViewTalonNumVal.setVisibility(mTextViewTalonNumTxt.getVisibility());


        mTextViewOldTalonNumTxt = (TextView) v.findViewById(R.id.dlg_conftal_old_talon_num_txt);
        mTextViewOldTalonNumVal = (TextView) v.findViewById(R.id.dlg_conftal_old_talon_num_val);
        mTextViewOldTalonNumTxt.setVisibility(oldTalonId == null || oldTalonId.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        mTextViewOldTalonNumVal.setVisibility(mTextViewOldTalonNumTxt.getVisibility());
        if (oldTalonId != null && !oldTalonId.isEmpty() && oldTalon != null)
            mTextViewOldTalonNumVal.setText(oldTalon.getStubNum());


        mTextViewDateTime = (TextView) v.findViewById(R.id.dlg_conftal_dt);
        mTextViewDateTime.setText(Utils.strToDateWeekStr(mTalon.getDateStr(), "dd.MM.yyyy")+" "+ mTalon.getTimeStr().toString());


        mTextViewSpec = (TextView) v.findViewById(R.id.dlg_conftal_spec);
        mTextViewSpec.setText(mSpec.getName());


        mTextViewDoct = (TextView) v.findViewById(R.id.dlg_conftal_doct);
        mTextViewDoct.setText(mDoctor.getFamily()+" "+mDoctor.getName()+" "+mDoctor.getPatronymic());

        mTextViewClinic = (TextView) v.findViewById(R.id.dlg_conftal_clinic);
        mTextViewClinic.setText(mClinic.getNAME()+"\nтел.: "+mClinic.getPHONE()+"\nemail: "+mClinic.getEMAIL());


        mTextViewClinicAdr = (TextView) v.findViewById(R.id.dlg_conftal_clinic_adr);
        mTextViewClinicAdr.setText(mClinic.getADDRESS());

        btnConfirm = (Button) v.findViewById(R.id.dialog_confirm_talon_ok);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mIsSuccessReserve) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
                else {
                    if (oldTalonId != null && !oldTalonId.isEmpty())
                        deleteOldTalon();
                    else
                        reservNewTalon();
                }
            }
        });

        btnCancle = (Button) v.findViewById(R.id.dialog_confirm_talon_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  getActivity().finish();
            }
        });


        chkAddEvent = (CheckBox) v.findViewById(R.id.dlg_coftal_chk_add_event_calendar);
        chkAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionsHelper.verifyCalendarPermissions(getActivity());
                //addEventToCalendar();
            }
        });

        return v;
    }

    // текст описания для календаря
    private String getDescTalon()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<b><font color=green>").append(mCurPolis.getPolisText()).append("</font></b><br>");
        sb.append("<b>").append(getString(R.string.txt_conftal_dt)).append("</b><br><font color=green>").append(mTextViewDateTime.getText()).append("</font><br>");
        sb.append("<b>").append(getString(R.string.txt_conftal_spec)).append("</b><br><font color=green>").append(mTextViewSpec.getText()).append("</font><br>");
        sb.append("<b>").append(getString(R.string.txt_conftal_doct)).append("</b><br><font color=green>").append(mTextViewDoct.getText()).append("</font><br>");
        sb.append("<b>").append(getString(R.string.txt_conftal_clinic)).append("</b><br><font color=green>").append(mTextViewClinic.getText()).append("</font><br>");
        sb.append("<b>").append(getString(R.string.txt_conftal_clinic_adr)).append("</b><br><font color=green>").append(mTextViewClinicAdr.getText()).append("</font><br>");
        sb.append("<b>").append(getString(R.string.txt_conftal_talon_num)).append("</b><br><font color=green>").append(mTextViewTalonNumVal.getText()).append("</font><br>");
        return  sb.toString();
    }

    //добавление события в календарь
    private void addEventToCalendar()
    {

        if (chkAddEvent.isChecked()) {
            long eventId = -1;
            try {

                eventId = CalendarHelper.addEvent(getActivity().getApplicationContext(), getDescTalon(), Utils.strToDate(mTalon.getDateStr().trim() + " " + mTalon.getTimeStr(), "dd.MM.yyyy HH:mm").getTime());
            } catch (Exception e) {
                Utils.showErrorDlg(getActivity(),"Ошибка добавления события в календарь:",  e.getMessage() +"\n"+Utils.errStack(e));
            }
            if (eventId > 0) {
                TalonInCalendar tic = new TalonInCalendar(mTalon.getId(), mTalon.getStubNum(), (int) eventId);
                tic.saveToDB();
            }
        }

    }

    private void saveLastSelectedLpu(String lpuId)
    {
        LastSelectedLpu lastLpu = LastSelectedLpu.getForLpuId(lpuId);
        if (lastLpu != null ) {
            if (lastLpu.getCntSelected() == null) lastLpu.setCntSelected(0);
            lastLpu.incCntSelected();
            lastLpu.saveToDataBase();
        }
        else {
            lastLpu = new LastSelectedLpu();
            lastLpu.setLpuId(lpuId);
            lastLpu.setCntSelected(1);
            lastLpu.appendDB(true);
        }
    }

    private void deleteEventInCalendar(Talon talon)
    {
        DBHelper myDb = DBFactory.getInstance(getContext().getApplicationContext()).getDBHelper(Talon.class);

        ArrayList<TalonInCalendar> list = (ArrayList<TalonInCalendar>) myDb.getArrayList("talonNum = ?",new String[]{talon.getStubNum()}, TalonInCalendar.class, "");
        if (list != null && list.size() > 0)
        {
            try {
                for (TalonInCalendar tic : list) {
                    CalendarHelper.removeEventNew(mActivity.getApplicationContext(), tic.getEventId());
                    myDb.delete("eventId = ?", new Integer[]{tic.getEventId()},TalonInCalendar.class);
                }
            }
            catch (Exception e)
            {
                Utils.showErrorDlg((FragmentActivity) mActivity,"Ошибка удаления события из календаря:",  e.getMessage() +"\n"+Utils.errStack(e));
            }
        }
    }

    private void deleteOldTalon()
    {
        recievedResult = false;
        HttpServ httpServ = HttpServ.getInstance(mActivity);
        httpServ.postCancelVisit(Polis.getPolis(oldTalon.getPolisId()), oldTalon, new HttpServ.Callback()
        {
            @Override
            public void onProc(HashMap<String, Object> result) {

            }

            @Override
            public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList) {
                if (result != null){
                    if (result.containsKey(Actions.RESULT_KEY_SUCCESS))
                    {
                        recievedResult = true;
                        deleteEventInCalendar(oldTalon);
                        reservNewTalon();
                    }
                }
            }
        });

        /*
        while (!recievedResult)
        {
            deleteEventInCalendar(oldTalon);
            try {
                Thread.sleep(1000);
            } catch (Exception e)
            {

            }

        }
        */
    }

    // действия после добавления талона
    private void addTalon()
    {
        Talon curTalon = new Talon(mTalon.getId(),mTalon.getCity(),mTalon.getLpu(),mTalon.getSpec(),mTalon.getDoctor(),mTalon.getDaySchedule(),mTalon.getTimeSchedule(),mTalon.getTimeStr(),mTalon.getDateStr(),mTalon.getDocPost(),mTalon.getPolisId(),mTalon.getStubNum(),"0","null","null");
        //if (Utils.isDebugMode()) curTalon.setStubNum("НП-20ВВ");
        curTalon.saveToDataBase(true);

        addEventToCalendar();

        saveLastSelectedLpu(mClinic.getID());

        HttpServ httpServ = HttpServ.getInstance(mActivity);
        httpServ.getPatientOrder(true, mCurPolis, curTalon, null);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_FRAGMENT){

        }
    }
}
