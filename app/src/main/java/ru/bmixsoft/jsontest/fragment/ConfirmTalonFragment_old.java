package ru.bmixsoft.jsontest.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.httpserv.Actions;
import ru.bmixsoft.jsontest.httpserv.HttpServ;
import ru.bmixsoft.jsontest.model.CurTalon;
import ru.bmixsoft.jsontest.model.Doctor;
import ru.bmixsoft.jsontest.model.LPU;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.model.Speciality;
import ru.bmixsoft.jsontest.model.Talon;
import ru.bmixsoft.jsontest.utils.HttpRestClientUsage;
import ru.bmixsoft.jsontest.utils.MyAsyncHttp;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 19.01.2017.
 */
public class ConfirmTalonFragment_old extends  MyFragment
    implements MyAsyncHttp.Callback
{

    private static final String DbgTAG ="ConfirmTalonFragment";

    protected static ConfirmTalonFragment instance;

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
    private  Button btnConfirm;
    private  Button btnCancle;


    private static final String SAVE_KEY_CLASS = "CONFIRM_TALON_FRAGMENT";
    private static final String SAVE_KEY_CUR_ACTION = SAVE_KEY_CLASS+"_CUR_ACTION";
    private static final String SAVE_KEY_CUR_SEND_REQ = SAVE_KEY_CLASS+"_SEND_REQ";
    private static final String SAVE_KEY_CUR_SOAP = SAVE_KEY_CLASS+"_CUR_SOAP";

    private static final int REQUEST_FRAGMENT = 0;
    private DBHelper myDb;

    private String mCurAction; // текущее действие
    private String mPrevAction; // текущее действие
    private boolean mSendRequest; // нужно ли отправлять запрос
    private String mCurSoap;    // текущий запрос


    private HttpRestClientUsage mHttpClient;

    public static MyFragment getInstance(Context context)
    {
        if (instance == null) {
            synchronized (MainFragment.class) {
                if (instance == null) {instance = new ConfirmTalonFragment();}
            }
        }else{}
        return instance;
    }

    public void onAsyncParseRespResult(String action, HashMap<String, String> map) {
        Log.d(DbgTAG, "onAsyncParseRespResult-->");
        Log.d(DbgTAG, "action: "+ action);
        Log.d(DbgTAG, "map.size: "+ String.valueOf(map.size()));
        String strSucces = "";
        mPrevAction = action;
        String xErrDescr = "";
        String xStubNum = "";

        try {

            if (map.containsKey(Actions.RESULT_KEY_SUCCESS)) {
                strSucces = map.get(Actions.RESULT_KEY_SUCCESS);
            } else
            {
                mCurAction = Actions.ACTION_ERROR;
                throw new Exception(xErrDescr);
            }

                    //mTextViewDbgTxt.setText(strSucces);
            mSendRequest = true;


            switch (action) {
                case Actions.ACTION_SAVE_EMAIL:
                    if (strSucces.equals("true")) {
                        mCurAction = Actions.ACTION_SET_LAST_STEP;
                    } else {
                        mCurAction = Actions.ACTION_ERROR;
                    }
                    break;
                case Actions.ACTION_SET_LAST_STEP:
                    if (strSucces.equals("true")) {
                        mCurAction = Actions.ACTION_SUBMIT;
                    } else {
                        mCurAction = Actions.ACTION_ERROR;
                    }
                    break;
                case Actions.ACTION_SUBMIT:
                    if (strSucces.equals("true")) {
                        mCurAction = Actions.ACTION_CREATE_VISIT;
                    } else {
                        mCurAction = Actions.ACTION_ERROR;
                    }
                    break;
                case Actions.ACTION_CREATE_VISIT:
                    // if (Debug.isDebugMode) mSendRequest = false;
                    if (strSucces.equals("true")) {
                        String xResult = map.get(getResources().getString(R.string.xmlCVT_CreateVisitResult_aResult));
                        String xCode = map.get(getResources().getString(R.string.xmlCVT_CreateVisitResult_aCode));
                        xStubNum = map.get(getResources().getString(R.string.xmlCVT_CreateVisitResult_aStubNum));
                        xErrDescr = map.get(getResources().getString(R.string.xmlCVT_ErrorDescription));

                        if (xResult.equals("true")) {
                            mCurAction = Actions.ACTION_SEND_MAIL;
                            if (xStubNum != null) {
                                mTalon.setStubNum(xStubNum);
                                //mTalon.saveToDataBase();
                            }
                        } else mCurAction = Actions.ACTION_ERROR;
                    } else {
                        mCurAction = Actions.ACTION_ERROR;
                    }
                    break;
                case Actions.ACTION_SEND_MAIL:
                    //if (Utils.isDebugMode()) mSendRequest = false;
                    if (strSucces.equals("true")) {
                        mCurAction = Actions.ACTION_FINISH;
                    } else {
                        //mCurAction = Actions.ACTION_ERROR;
                        if (map.containsKey("message")) xErrDescr = map.get("message");
                        else xErrDescr = "Возникла проблема при отправке уведомелния на email.";
                        mCurAction = Actions.ACTION_FINISH;
                    }
                    break;
            }

        }
        catch (Exception e) { Utils.safePrintError(e); // e.printStackTrace();
            }
        finally {

            switch (mCurAction) {
                case Actions.ACTION_ERROR:
                    btnConfirm.setVisibility(View.INVISIBLE);
                    mTextViewDbgTitle.setText(getActionDesc(mCurAction) + "(" + getActionCode(mPrevAction) + ")" + "\n" + xErrDescr);
                    mTextViewDbgTitle.setTextColor(getResources().getColor(R.color.red));
                    break;
                case Actions.ACTION_FINISH:
                    mTextViewDbgTitle.setText(getActionDesc(mCurAction) + "\n" + Utils.nvl(xErrDescr, ""));
                    mTextViewTalonNumTxt.setVisibility(View.VISIBLE);
                    mTextViewTalonNumVal.setText(mTalon.getStubNum());
                    btnCancle.setVisibility(View.INVISIBLE);
                    addTalon();
                    break;
                default:
                    mTextViewDbgTitle.setTextColor(getResources().getColor(R.color.green));
                    mTextViewTalonNumTxt.setVisibility(View.INVISIBLE);
//                if(!Utils.isDebugMode())
                    procAction();
                    break;
            }
            mTextViewTalonNumVal.setVisibility(mTextViewTalonNumTxt.getVisibility());

            Log.d(DbgTAG, "onAsyncParseRespResult--<");
        }
    }

    @Override
    public void onPostExecuteUI(String response, boolean success) {
        Log.d(DbgTAG, "onPostExecuteUI-->");
        Log.d(DbgTAG, "responce:\n"+response);

        try {
            JSONObject json = new JSONObject(response);
            Log.d(DbgTAG, json.toString());
            new AsyncParseResp(getActivity(), mCurAction, json).execute();
            if (Utils.isDebugMode()) mTextViewDbgTxt.setText(json.toString());
        }
        catch (JSONException e)
        {
            mCurAction = Actions.ACTION_ERROR;
            btnConfirm.setVisibility(View.INVISIBLE);
            mTextViewDbgTitle.setText(getActionDesc(mCurAction) + "(" + getActionCode(mPrevAction) + ")" + "\n"+response);
            mTextViewDbgTitle.setTextColor(getResources().getColor(R.color.red));

            Utils.safePrintError(e); //  e.printStackTrace();
        }

        Log.d(DbgTAG, "onPostExecuteUI--<");
    }

    private String getActionCode(String action)
    {
        if (!Utils.isDebugMode()) return "";
        String res = "";
        switch (action){
            case Actions.ACTION_SAVE_EMAIL:
                res = "ACTION_SAVE_EMAIL";
                break;
            case Actions.ACTION_SET_LAST_STEP:
                res = "ACTION_SET_LAST_STEP";
                break;
            case Actions.ACTION_SUBMIT:
                res = "ACTION_SUBMIT";
                break;
            case Actions.ACTION_CREATE_VISIT:
                res = "ACTION_CREATE_VISIT";
                break;
            case Actions.ACTION_SEND_MAIL:
                res = "ACTION_SEND_MAIL";
                break;
            case Actions.ACTION_ERROR:
                res = "ACTION_ERROR";
                break;
            case Actions.ACTION_FINISH:
                res = "ACTION_FINISH";
                break;
            default:
                res = "";
                break;
        }
        return res+": ";
    }

    private String getActionDesc(String action)
    {
        switch (action){
            case Actions.ACTION_SAVE_EMAIL:
                return getActionCode(action)+"Подтвердите выбранные данные";//": Подтверждение email-адреса";
            case Actions.ACTION_SET_LAST_STEP:
                return getActionCode(action)+"Сохранение выбраных данных";
            case Actions.ACTION_SUBMIT:
                return getActionCode(action)+"Авторизация полиса";
            case Actions.ACTION_CREATE_VISIT:
                return getActionCode(action)+"Резервирование талона";
            case Actions.ACTION_SEND_MAIL:
                return getActionCode(action)+"Отправка email-уведомления";
            case Actions.ACTION_ERROR:
                return getActionCode(action)+"Ошибка взаимодействия с сервером";
            case Actions.ACTION_FINISH:
                return getActionCode(action)+"Талон успешно зарезервирован\nИнформация о резервирование талона отправлена на email";
        };
        return "";
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_KEY_CUR_ACTION, mCurAction);
        outState.putBoolean(SAVE_KEY_CUR_SEND_REQ, mSendRequest);
        outState.putString(SAVE_KEY_CUR_SOAP, mCurSoap);
    }

    @TargetApi(14)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        myDb = DBFactory.getInstance().getDBHelper(CurTalon.class);
        mTalon = CurTalon.get("1");
        mCurPolis = Polis.getPolis(mTalon.getPolisId());
        mDoctor = Doctor.get(mTalon.getDoctor());
        mClinic = LPU.get4LpuCode(mTalon.getLpu());
        mSpec = Speciality.get(mTalon.getSpec());

        if (savedInstanceState != null) {
            mCurAction = savedInstanceState.getString(SAVE_KEY_CUR_ACTION);
            mSendRequest = savedInstanceState.getBoolean(SAVE_KEY_CUR_SEND_REQ);
            mCurSoap = savedInstanceState.getString(SAVE_KEY_CUR_SOAP);
        }
        else{
            /*
            if (Utils.isDebugMode()) {
                mCurAction = Actions.ACTION_FINISH;
                mSendRequest = false;
                addTalon();
            }
            else{
                */
                mCurAction = Actions.ACTION_SAVE_EMAIL;
                mSendRequest = true;
            //}
            mCurSoap = "";
        }
    }

    @TargetApi(14)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirm_new_talon, container, false);
        mTextViewDbgTxt = (TextView) v.findViewById(R.id.dlg_coftal_dbg_txt);
        mTextViewDbgTitle = (TextView) v.findViewById(R.id.dlg_coftal_dbg_title);

        mTextViewDbgTxt.setVisibility(Utils.isDebugMode() == true ? View.VISIBLE : View.INVISIBLE);

        //mTextViewDbgTitle.setVisibility(Utils.isDebugMode(getActivity().getApplicationContext(), false) == true ? View.VISIBLE : View.INVISIBLE);

        mTextViewTalonNumTxt = (TextView) v.findViewById(R.id.dlg_conftal_talon_num_txt);
        mTextViewTalonNumVal = (TextView) v.findViewById(R.id.dlg_conftal_talon_num_val);
        mTextViewTalonNumTxt.setVisibility(View.INVISIBLE);
        mTextViewTalonNumVal.setVisibility(mTextViewTalonNumTxt.getVisibility());


        mTextViewDateTime = (TextView) v.findViewById(R.id.dlg_conftal_dt);
        mTextViewDateTime.setText(mTalon.getDateStr().toString()+" "+ mTalon.getTimeStr().toString());


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
                procAction();
            }
        });

        btnCancle = (Button) v.findViewById(R.id.dialog_confirm_talon_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  sendResult(0);
               //  onPostExecuteUI(Debug.testResp);
                  getActivity().finish();
            }
        });
        mTextViewDbgTitle.setText(getActionDesc(mCurAction));
        return v;
    }

    private void addTalon()
    {
        Talon curTalon = new Talon(mTalon.getId(),mTalon.getCity(),mTalon.getLpu(),mTalon.getSpec(),mTalon.getDoctor(),mTalon.getDaySchedule(),mTalon.getTimeSchedule(),mTalon.getTimeStr(),mTalon.getDateStr(),mTalon.getDocPost(),mTalon.getPolisId(),mTalon.getStubNum(),"0","null","null");
        //if (Utils.isDebugMode()) curTalon.setStubNum("НП-20ВВ");
        curTalon.saveToDataBase(true);

        HttpServ httpServ = HttpServ.getInstance(mActivity);
        httpServ.getPatientOrder(true, mCurPolis, curTalon, null);

    }

    private void procAction(){
        Log.d(DbgTAG,"createTalon-->");
        try {

            Log.d(DbgTAG,"	mCurAction: "+mCurAction);

            mTextViewDbgTitle.setText(getActionDesc(mCurAction));

            RequestParams params;
            params = new RequestParams();
            switch (mCurAction){
                case Actions.ACTION_SAVE_EMAIL:
                    params.add("email", mCurPolis.getEmail());
                    mCurSoap = "doctor_appointment/save_email";
                    break;
                case Actions.ACTION_SET_LAST_STEP:
                    params.add("DTTID", mTalon.getTimeSchedule());
                    params.add("lpuCode", mTalon.getLpu().toString());
                    params.add("scenery", "0");
                    mCurSoap = "doctor_appointment/set_last_step";
                    break;
                case Actions.ACTION_SUBMIT:
                    params.add("birthday",mCurPolis.getBirthday());
                    params.add("nPol",mCurPolis.getPolusNum());
                    params.add("scenery","1");
                    params.add("sPol","");
                    mCurSoap = "doctor_appointment/submit";
                    break;
                case Actions.ACTION_CREATE_VISIT:
                    params.add("DTTID", mTalon.getTimeSchedule());
                    params.add("lpuCode", mTalon.getLpu().toString());
                    mCurSoap = "doctor_appointment/create_visit";
                    break;
                case Actions.ACTION_SEND_MAIL:
                    params.add("datetime", mTalon.getDateStr()+" "+ mTalon.getTimeStr());
                    params.add("lpuCode", mTalon.getLpu().toString());
                    params.add("doctorData",mSpec.getName());
                    params.add("email",mCurPolis.getEmail());
                    params.add("fio",mDoctor.getFio());
                    params.add("lpuAddress",mClinic.getADDRESS());
                    params.add("lpuName",mClinic.getNAME());
                    params.add("pol",mCurPolis.getPolusNum());
                    params.add("stubNum", mTalon.getStubNum());
                    mCurSoap = "doctor_appointment/send_mail";
                    break;
                case Actions.ACTION_FINISH:
          //          if (Utils.isDebugMode()) addTalon();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    mSendRequest = false;
                    break;
            }
            if (mSendRequest)
                new MyAsyncHttp(getActivity(),"POST", mCurSoap, "Получение информации с сервера", params, this, true ).execute();

        } catch(Exception e){
            Log.d(DbgTAG,"	error: "+e.getMessage());
            Utils.safePrintError(e); // e.printStackTrace();
        }
        Log.d(DbgTAG,"createTalon--<");
    }

    private void sendResult(int resultCode){
        //if (getTargetFragment() == null) return;


        //Intent i = new Intent();
        /*
        i.putExtra(EXTRA_SERV_TITLE, mTitle);
        i.putExtra(EXTRA_SERV_URL, mUrl);
        i.putExtra(EXTRA_SERV_CODE, mCode);
*/

        //    getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
       // for (int i = 0; i < 10000; i++) mTextViewDbgTxt.setText(String.valueOf(i));

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_FRAGMENT){

        }
    }

    private class AsyncParseResp extends AsyncTask<Void, String, String>
    {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        private static final String DbgTAG = "AsyncParseResp";
        private String mAction;
        private JSONObject mJSONObject;
        private HashMap<String, String> mMap;

        public AsyncParseResp(Context context, String action, JSONObject jsonObject)
        {
            mContext = context;
            mProgressDialog = new ProgressDialog(context);
            mAction = action;
            mJSONObject = jsonObject;
            mMap = new HashMap<String, String>();

        }

        //onPreExecute() – выполняется перед doInBackground(). Имеет доступ к UI
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Подождите. Анализ результата");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        //doInBackground() – основной метод, который выполняется в новом потоке. Не имеет доступа к UI.
        @Override
        protected String doInBackground(Void... params) {

            if (mJSONObject != null){
                boolean lv_success = Utils.getjsonBool(mJSONObject,"success");
                mMap.put("success",String.valueOf(lv_success));
                if (mAction.equals(Actions.ACTION_SEND_MAIL))
                {
                    if (mJSONObject.has("message")) mMap.put("message",Utils.getjsonString(mJSONObject,"message"));
                }
                if (mAction == Actions.ACTION_CREATE_VISIT) {
                    try {
                        JSONObject jitem = mJSONObject.getJSONObject("items");
                        String resultVisit = jitem.getString("CreateVisitResult");
                        mMap.put("resultVisit", resultVisit);


                        try {
                            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
                            XmlPullParser xmlParser = xmlFactory.newPullParser();
                            xmlParser.setInput(new StringReader(resultVisit));

                            String curTagName = "";

                            while (xmlParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                                final String TAG = "xmlParser";
                                String tmp = "";

                                switch (xmlParser.getEventType()) {
                                    case XmlPullParser.START_DOCUMENT:
                                        Log.d(TAG, "Начало документа");
                                        break;
                                    // начало тэга
                                    case XmlPullParser.START_TAG:
                                        curTagName = xmlParser.getName();
                                        Log.d(TAG,
                                                "START_TAG: имя тега = " + curTagName
                                                        + ", уровень = " + xmlParser.getDepth()
                                                        + ", число атрибутов = "
                                                        + xmlParser.getAttributeCount());
                                        tmp = "";
                                        for (int i = 0; i < xmlParser.getAttributeCount(); i++) {
                                            String curAttrName = xmlParser.getAttributeName(i);
                                            String curAtteValue = xmlParser.getAttributeValue(i);
                                            tmp = tmp + curAttrName + " = " + curAtteValue + ", ";

                                            if (curTagName.equals(getResources().getString(R.string.xmlCVT_CreateVisitResult))){
                                                mMap.put(curAttrName, curAtteValue);
                                            }
                                        }
                                        if (!TextUtils.isEmpty(tmp))
                                            Log.d(TAG, "Атрибуты: " + tmp);
                                        break;
                                    // конец тега
                                    case XmlPullParser.END_TAG:
                                        Log.d(TAG, "END_TAG: имя тега = " + xmlParser.getName());
                                        curTagName = "/"+xmlParser.getName();
                                        break;
                                    // содержимое тега
                                    case XmlPullParser.TEXT:
                                        Log.d(TAG, "текст = " + xmlParser.getText());
                                        if(curTagName.equals(getResources().getString(R.string.xmlCVT_ErrorDescription))){
                                            mMap.put(curTagName, xmlParser.getText());
                                        }
                                        break;

                                    default:
                                        break;
                                }
                                xmlParser.next();
                            }
                        }catch (XmlPullParserException x){
                            Utils.safePrintError(x); //     x.printStackTrace();
                        }
                        catch (IOException io) {
                            Utils.safePrintError(io); //     io.printStackTrace();
                        }
                    } catch (JSONException e){
                        Utils.safePrintError(e); //     e.printStackTrace();
                    }
                }

            }
            return null;
        }

        //onPostExecute() – выполняется после doInBackground() (может не вызываться, если AsyncTask был отменен). Имеет доступ к UI. Используйте его для обновления пользовательского интерфейса, как только ваша фоновая задача завершена. Данный обработчик при вызове синхронизируется с потоком GUI, поэтому внутри него вы можете безопасно изменять элементы пользовательского интерфейса.
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ConfirmTalonFragment_old.this.onAsyncParseRespResult(mCurAction, mMap);
            if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        }

        //onProgressUpdate(). Имеет доступ к UI. Переопределите этот обработчик для публикации промежуточных обновлений в пользовательский интерфейс. При вызове он синхронизируется с потоком GUI, поэтому в нём вы можете безопасно изменять элементы пользовательского интерфейса
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d(DbgTAG, "onProgressUpdate -->");
            Log.d(DbgTAG, " values[0]: "+values[0]);
            mProgressDialog.setMessage(values[0]);
            Log.d(DbgTAG, "onProgressUpdate --<");
        }


    }

}
