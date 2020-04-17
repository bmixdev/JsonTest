package ru.bmixsoft.jsontest.httpserv;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.loopj.android.http.RequestParams;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ru.bmixsoft.jsontest.R;
import ru.bmixsoft.jsontest.fragment.dialog.InfoDialogFragment;
import ru.bmixsoft.jsontest.model.City;
import ru.bmixsoft.jsontest.model.CurTalon;
import ru.bmixsoft.jsontest.model.DaySchedul;
import ru.bmixsoft.jsontest.model.DayScheduleList;
import ru.bmixsoft.jsontest.model.DocPost;
import ru.bmixsoft.jsontest.model.Doctor;
import ru.bmixsoft.jsontest.model.DoctorList;
import ru.bmixsoft.jsontest.model.LPU;
import ru.bmixsoft.jsontest.model.LpuLinkPolis;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.model.Speciality;
import ru.bmixsoft.jsontest.model.Talon;
import ru.bmixsoft.jsontest.model.TimeItems;
import ru.bmixsoft.jsontest.multiview.data.DataProvider;
import ru.bmixsoft.jsontest.utils.MyAsyncHttp;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 15.02.2017.
 */
public class HttpServ implements MyAsyncHttp.Callback {
    private static final String DbgTAG = "AsyncJSoupHttpHelper";

    protected static HttpServ instance;
    private Context mContext;

    private boolean mAuthorizeSuccess = false;

    private String mCurAction; // текущее действие
    private String mCurSubAction; // текущее под действие

    private String mPrevAction; // текущее действие
    private boolean mSendRequest; // нужно ли отправлять запрос
    private String mCurSoap;    // текущий запрос
    private String mTypeRequest;

    private Callback mCallback;
    private DBHelper mDBHelper;
    private ArrayList<Polis> mListPolis;
    private int idxArrayPolis;

    private HashMap<String, Object> mParams;
    private HashMap<String, Object> mResult;
    private ArrayList<HashMap<String, Object>> mResultArrayList;
    private String mErrMsg;

    private boolean mIsNeedShowProcDialog;

    private static boolean mAvaibleServer = true;
    private static boolean mAvaibleServerProcFinish = false;

    public interface Callback {
        // добавить обновление UI
        public void onProc(HashMap<String, Object> result);

        public void onFinish(HashMap<String, Object> result, ArrayList<HashMap<String, Object>> resultArrayList);
    }

    public static HttpServ getInstance(final Context context) {
        if (instance == null) {
            synchronized (HttpServ.class) {
                if (instance == null) {
                    instance = new HttpServ(context);
                }
            }
        } else {
        }
/*
        AsyncJSoupHttpHelper task = JSoupHelper.checkAvailableSite(context, new JSoupHelper.Callback() {
            @Override
            public void onFinish(boolean success, HashMap<String, Object> result) {
                mAvaibleServer = (int) result.get(AsyncJSoupHttpHelper.hmk_success) == 1 ? true : false;

                mAvaibleServerProcFinish = true;
                String title = "";
                if (result.containsKey(AsyncJSoupHttpHelper.hmk_result_head_txt))
                    title = (String) result.get(AsyncJSoupHttpHelper.hmk_result_head_txt);

//                if (!mAvaibleServer) {
                    InfoDialogFragment.show((FragmentActivity) context, context.getString(R.string.chkAvailableServer), title, (String) result.get(AsyncJSoupHttpHelper.hmk_result), (int) result.get(AsyncJSoupHttpHelper.hmk_success) == 1 ? InfoDialogFragment.TypeDialog.INFO : InfoDialogFragment.TypeDialog.ERROR);

  //              }
            }
        });

        try {
            task.get(10 * 1000, TimeUnit.MILLISECONDS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
  */
        instance.mErrMsg = "";
        instance.mContext = context;
        return instance;
    }

    public HttpServ(Context context) {
        mContext = context;
        mDBHelper = DBFactory.getInstance().getDBHelper(LPU.class);

        mAuthorizeSuccess = false;

        mCurAction = new String(); // текущее действие
        mCurSubAction = new String(); // текущее под действие

        mPrevAction = new String(); // текущее действие
        mSendRequest = false; // нужно ли отправлять запрос
        mCurSoap = new String();    // текущий запрос
        mTypeRequest = new String();

        mListPolis = new ArrayList<Polis>();
        mParams = new HashMap<String, Object>();
        mResult = new HashMap<String, Object>();
        mResultArrayList = new ArrayList<>();
        mErrMsg = new String();
        mIsNeedShowProcDialog = true;

    }

    public void setIsNeedShowProcDialog(boolean isShow) {
        mIsNeedShowProcDialog = isShow;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void parseResponseAuthorization(JSONObject jsonObject) {
        boolean isSuccess = false;
        if (jsonObject.has("success")) {
            try {

                Polis parPolis = (Polis) mParams.get(Actions.PARAM_POLIS);
                Talon parTalon = (Talon) mParams.get(Actions.PARAM_TALON);

                boolean lv_success = jsonObject.getBoolean("success");
                if (lv_success == false) {
                    String tmpErrMsg = "В авторизации полиса № " + parPolis.getPolusNum() + "(" + parPolis.getFio() + ") отказано!\nВозможные причины:\n  1. Неверно введен номер полиса или дата рождения\n  2. Временные проблемы на web-сервере";
                    //Utils.msgWarning(tmpErrMsg);
                    putMapResult(false, tmpErrMsg);
                    mAuthorizeSuccess = false;
                    if (!mAuthorizeSuccess) {
                        InfoDialogFragment.show((FragmentActivity) mContext, mContext.getString(R.string.chkAvailableServer), mContext.getString(R.string.errorWrkSite), tmpErrMsg, InfoDialogFragment.TypeDialog.ERROR);
                    }
                    return;
                }


                JSONArray jsonArrayItems = jsonObject.getJSONObject("items").getJSONArray("lpu");
                for (int i = 0; i < jsonArrayItems.length(); i++) {
                    JSONObject jsonItem = jsonArrayItems.getJSONObject(i);

                    LPU lpu = new LPU(Utils.getjsonString(jsonItem, "id")
                            , Utils.getjsonString(jsonItem, "code")
                            , Utils.getjsonString(jsonItem, "email")
                            , Utils.getjsonString(jsonItem, "site")
                            , Utils.getjsonString(jsonItem, "full_title")
                            , ""
                            , Utils.getjsonString(jsonItem, "address")
                            , Utils.getjsonString(jsonItem, "phones")
                            , Utils.getjsonInt(jsonItem, "accessibility")
                            , Utils.getjsonString(jsonItem, "children")
                            , Utils.getjsonString(jsonItem, "city_id")
                            , Utils.getjsonInt(jsonItem, "is_waiting_list")
                            , Utils.getjsonInt(jsonItem, "is_call_doctor_home")
                            , Utils.getjsonString(jsonItem, "latitude")
                            , Utils.getjsonString(jsonItem, "longitude")
                    );
                    mDBHelper.insert(lpu);

                    if (!mDBHelper.exists("POLIS_ID=? AND LPU_ID=?",
                            new String[]{parPolis.getId(), lpu.getID()}, LpuLinkPolis.class)) {
                        Long maxId = mDBHelper.getMaxId(LpuLinkPolis.class) + 1;
                        LpuLinkPolis lnk = new LpuLinkPolis(maxId.intValue()
                                , parPolis.getId()
                                , lpu.getID()
                        );
                        mDBHelper.insert(lnk);
                    }
                    isSuccess = true;
                }
                if (isSuccess) {
                    long cnt = mDBHelper.size("POLIS_ID = ?", new String[]{parPolis.getId().toString()}, LpuLinkPolis.class);
                    /*
                    Utils.d("Полис успешно авторизирован\n" +
                            "Кол-во доступных поликлиник: " + cnt
                    );
                    */
                    mAuthorizeSuccess = true;
                }
                putMapResult(isSuccess, "");
            } catch (JSONException je) {
                Utils.safePrintError(je); // je.printStackTrace();
                mErrMsg = je.getMessage();
                String tmpErrMsg = "#HS1 Ошибка обработки результата:\n" + je.getMessage();
                //Utils.msgError(tmpErrMsg);
                putMapResult(false, tmpErrMsg);
            }
        }
        else
        {
            putMapResult(false, Utils.getjsonString(jsonObject, "message"));
        }
    }

    private void refreshTalonData(Talon talonDb, Talon talonServ) {
        //обновить идентификатор визита
        if (talonDb.getDVTID() == null || !talonServ.getDVTID().equals(talonDb.getDVTID()))
            talonDb.setDVTID(talonServ.getDVTID());
        //обновить фамилию врача
        if (talonDb.getV_Family() == null || !talonServ.getV_Family().equals(talonDb.getV_Family()))
            talonDb.setV_Family(talonServ.getV_Family());
        //обновить имя врача
        if (talonDb.getV_Name() == null || !talonServ.getV_Name().equals(talonDb.getV_Name()))
            talonDb.setV_Name(talonServ.getV_Name());
        //обновить отчество врача
        if (talonDb.getV_Ot() == null || !talonServ.getV_Ot().equals(talonDb.getV_Ot()))
            talonDb.setV_Ot(talonServ.getV_Ot());
        //обновить номер кабинета
        if (talonDb.getRoomNum() == null || !talonServ.getRoomNum().equals(talonDb.getRoomNum()))
            talonDb.setRoomNum(talonServ.getRoomNum());
        //обновить специализацию врача
        if (talonDb.getPRVSName() == null || !talonServ.getPRVSName().equals(talonDb.getPRVSName()))
            talonDb.setPRVSName(talonServ.getPRVSName());
        //обновить дату визита
        if (talonDb.getShortDate() == null || !talonServ.getShortDate().equals(talonDb.getShortDate()))
            talonDb.setShortDate(talonServ.getShortDate());
        //обновить время визита
        if (talonDb.getTime_from() == null || !talonServ.getTime_from().equals(talonDb.getTime_from()))
            talonDb.setTime_from(talonServ.getTime_from());
        talonDb.saveToDataBase();

    }

    private void parseResponcePatientOrder(JSONObject jsonObject) {
        boolean isSuccess = false;
        if (jsonObject.has("success")) {
            try {

                Polis parPolis = (Polis) mParams.get(Actions.PARAM_POLIS);
                Talon parTalon = (Talon) mParams.get(Actions.PARAM_TALON);

                boolean lv_success = jsonObject.getBoolean("success");
                if (lv_success == false)
                {
                    String tmpErrMsg = Utils.getjsonString(jsonObject, "message"); //= "В авторизации полиса №" + parPolis.getPolusNum() + "(" + parPolis.getFio() + ") отказано!\nПроверьте правильность введенных данных полиса!";
                   // Utils.msgWarning(tmpErrMsg);
                    putMapResult(false, tmpErrMsg);
                    return;
                }

                ArrayList<Talon> listTalons = new ArrayList<Talon>();

                int cntRefreshTalons = 0;

                Object jItem = jsonObject.get("items");
                if (jItem instanceof JSONArray) {
                    JSONArray jsonArrayItems = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArrayItems.length(); i++) {
                        JSONObject jsonItem = jsonArrayItems.getJSONObject(i);
                        Talon curTalon = new Talon();
                        curTalon.setId(Utils.getjsonString(jsonItem, "id"));
                        JSONObject jDoctor = jsonItem.getJSONObject("doctor");
                        curTalon.setV_Family(Utils.getjsonString(jDoctor, "family"));
                        curTalon.setV_Name(Utils.getjsonString(jDoctor, "name"));
                        curTalon.setV_Ot(Utils.getjsonString(jDoctor, "surname"));
                        curTalon.setRoomNum(Utils.getjsonString(jDoctor, "room"));
                        curTalon.setPRVSName(Utils.getjsonString(jDoctor, "separation") + "," + Utils.getjsonString(jDoctor, "position"));
                        curTalon.setShortDate(Utils.getjsonString(jsonItem, "date_record"));
                        curTalon.setTime_from(Utils.getjsonString(jsonItem, "time_record"));
                        curTalon.setStubNum(Utils.getjsonString(jsonItem, "ticket"));

                        curTalon.setLpu(Utils.getjsonString(jsonItem, "lpu_code"));

                        curTalon.setDVTID(Utils.getjsonString(jsonItem, "id"));

                        curTalon.setDoctor(Utils.getjsonString(jDoctor,"person_id"));
                        curTalon.setSpec(Utils.getjsonString(jDoctor,"department"));
                        listTalons.add(curTalon);
                    }
                    ArrayList<Talon> listTalonsDb = (ArrayList<Talon>) mDBHelper.getArrayList(Talon.class, null);
                    //if (listTalonsDb.size() != listTalons.size()) // если кол-во полисов в локальной базе отлизается от результата с сервера
                    //{
                    int cntDelete = mDBHelper.deleteCnt("PolisId = ?", new String[]{parPolis.getId().toString()}, Talon.class);
                    for (Talon talonServ : listTalons) {
                        //если обновить по конкретному талону
                        if (parTalon != null) {
                            if (!talonServ.getStubNum().equals(parTalon.getStubNum())) continue;
                        }
/*
                        boolean isExistTalon = false;
                        if (listTalonsDb.size() > 0) {
                            for (Talon talonDb : listTalonsDb) {
                                // поиск талона в базе по ключу LPU+StubNum
                                if (talonServ.getLpu().equals(talonDb.getLpu()) &&
                                        talonServ.getStubNum().equals(talonDb.getStubNum())) {
                                    isExistTalon = true;
                                    refreshTalonData(talonDb, talonServ);
                                    cntRefreshTalons++;
                                }
                                if (!isExistTalon) {
                                */
                        //int maxIdTalon = (int) mDBHelper.getMaxId(Talon.class) + 1;
                        String talonUID = DBHelper.getUID();
                        talonServ.setId(talonUID);
                        talonServ.setPolisId(parPolis.getId());
                        talonServ.saveToDataBase();
                        cntRefreshTalons++;
                        /*
                                    isExistTalon = true;
                                }
                            }
                        } else {
                            if (!isExistTalon) {
                                int maxIdTalon = (int) mDBHelper.getMaxId(Talon.class) + 1;
                                talonServ.setId(maxIdTalon);
                                talonServ.setPolisId(parPolis.getId());
                                talonServ.saveToDataBase();
                                cntRefreshTalons++;
                            }
                        }
                        */
                    }
                    //}
                /*
                //обратная синхронизация
                for (Talon talonDB : listTalonsDb) {
                    boolean isExistTalon = false;
                    if (listTalons.size() > 0) {
                        for (Talon talonServ : listTalons) {
                            // поиск талона в базе по ключу LPU+StubNum
                            if (talonDB.getLpu().equals(talonServ.getLpu()) &&
                                    talonDB.getStubNum().equals(talonServ.getStubNum())) {
                                isExistTalon = true;
                                refreshTalonData(talonDB, talonServ);
                                cntRefreshTalons++;
                            }
                        }
                        if (!isExistTalon) {
                            mDBHelper.delete("DVTID = ? and PolisId = ?", new String[]{talonDB.getDVTID().toString(), parPolis.getId().toString()}, Talon.class);
                            cntRefreshTalons++;
                        }
                    } else {
                        mDBHelper.delete("DVTID = ?  and PolisId = ?", new String[]{talonDB.getDVTID().toString(), parPolis.getId().toString()}, Talon.class);
                        cntRefreshTalons++;
                    }
                }
                */
                }
                /*
                Utils.msgSuccess(parPolis.getFio() + " (" + parPolis.getPolusNum() + ")\n" +
                        "Информация о зарезервированных талонах успешно обновлена\n" +
                        "Кол-во обновленных талонов с сервера: " + cntRefreshTalons);
                        */
                putMapResult(true, "");
            } catch (JSONException je) {
                Utils.safePrintError(je); // je.printStackTrace();
                mErrMsg = je.getMessage();
                String tmpErrMsg = "#HS2 Ошибка обработки результата:\n" + je.getMessage();
               // Utils.msgError(tmpErrMsg);
                putMapResult(false, tmpErrMsg);
            } catch (Exception e) {
                Utils.safePrintError(e); // e.printStackTrace();
                mErrMsg = e.getMessage();
                String tmpErrMsg = "#HS3 Ошибка:\n" + e.getMessage();
               // Utils.msgError(tmpErrMsg);
                putMapResult(false, tmpErrMsg);
            }
        }
    }

    private void parseResponseCancelVisit(JSONObject jsonObject) {
        // if (Utils.isDebugMode()) Utils.d(jsonObject.toString());
        boolean isSuccess = false;
        if (jsonObject.has("success")) {
            try {

                Talon parTalon = (Talon) mParams.get(Actions.PARAM_TALON);

                boolean lv_success = jsonObject.getBoolean("success");

                if (lv_success == false) {
                    String tmpErrMsg = "#HS4 Ошибка отмены талона:\n" +  Utils.getjsonString(jsonObject, "message");
                  //  Utils.msgError(tmpErrMsg);
                    putMapResult(false, tmpErrMsg);
                    return;
                }

                JSONObject jsonResult = jsonObject.getJSONObject("result");
                String xResult = Utils.getjsonString(jsonResult, "code");
                String xCode = Utils.getjsonString(jsonResult, "message_code");
                String xErrDescr = Utils.getjsonString(jsonResult, "message");

                if (!xResult.equals("200")) {
                    String tmpErrMsg = "#HS5 Ошибка отмены талона:\n" + xCode + " " + xErrDescr;
            //        Utils.msgError(tmpErrMsg);
                    if (mResult != null) {
                        //mResult.put(Actions.RESULT_KEY_SUCCESS, "false");
                        putMapResult(false, tmpErrMsg);
                    }
                } else {
                    if (mResult != null)
                        mResult.put(Actions.RESULT_KEY_SUCCESS, "true");
                }
            } catch (JSONException je) {
                Utils.safePrintError(je); // je.printStackTrace();
                mErrMsg = je.getMessage();
                putMapResult(false, mErrMsg);
                //Utils.d("Ошибка обработки результата:\n" + je.getMessage());
            }

        }
    } //parseResponseCancelVisit

    private void putMapResult(boolean isSuccess, String description) {
        if (mResult == null) mResult = new HashMap<String, Object>();
        mResult.put(Actions.RESULT_KEY_SUCCESS, Utils.boolToString(isSuccess));
        mResult.put(Actions.RESULT_KEY_DESC, description);
    }

    private void parseResponseCreateVisit(JSONObject jsonObject) {

        //Log.d(DbgTAG, "onAsyncParseRespResult-->");
        //Log.d(DbgTAG, "mCurSubAction: " + mCurSubAction);
        mPrevAction = mCurSubAction;

        String lvDescription = "";
        String lvStubNum = "";
        boolean lvIsSuccess = false;

        if (jsonObject != null) {
            try {

                if (!jsonObject.has(Actions.RESULT_KEY_SUCCESS)) {
                    lvIsSuccess = false;
                    mCurAction = Actions.ACTION_ERROR;
                    throw new Exception("Неизвестный ответ от web-сервиса");
                } else {
                    lvIsSuccess = Utils.getjsonBool(jsonObject, Actions.RESULT_KEY_SUCCESS);
                    if (!lvIsSuccess && !mCurSubAction.equals(Actions.ACTION_CREATE_VISIT)) {
                        lvDescription = "Ошибка резервирования талона. Проверьте правильность введенного номера полиса!";
                    }
                }

                switch (mCurSubAction) {
                    case Actions.ACTION_SAVE_EMAIL:
                        break;
                    case Actions.ACTION_SET_LAST_STEP:
                        break;
                    case Actions.ACTION_SUBMIT:
                        break;
                    case Actions.ACTION_CREATE_VISIT:
                        if (lvIsSuccess) {

                            HashMap<String, String> mapXmlResult = new HashMap<String, String>();

                            try {
                                JSONObject jItem = jsonObject.getJSONObject("items");

                                /*
                                String resultVisit = jitem.getString("CreateVisitResult").replace("\n", "");

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
                                                //Log.d(TAG, "Начало документа");
                                                break;
                                            // начало тэга
                                            case XmlPullParser.START_TAG:
                                                curTagName = xmlParser.getName();
                                                tmp = "";
                                                for (int i = 0; i < xmlParser.getAttributeCount(); i++) {
                                                    String curAttrName = xmlParser.getAttributeName(i);
                                                    String curAtteValue = xmlParser.getAttributeValue(i);
                                                    tmp = tmp + curAttrName + " = " + curAtteValue + ", ";

                                                    if (curTagName.equals(mContext.getResources().getString(R.string.xmlCVT_CreateVisitResult))) {
                                                        mapXmlResult.put(curAttrName, curAtteValue);
                                                    }
                                                }
                                                if (!TextUtils.isEmpty(tmp)) {
                                                    //Log.d(TAG, "Атрибуты: " + tmp);
                                                }
                                                break;
                                            // конец тега
                                            case XmlPullParser.END_TAG:
                                                //Log.d(TAG, "END_TAG: имя тега = " + xmlParser.getName());
                                                curTagName = "/" + xmlParser.getName();
                                                break;
                                            // содержимое тега
                                            case XmlPullParser.TEXT:
                                                //Log.d(TAG, "текст = " + xmlParser.getText());
                                                if (curTagName.equals(mContext.getResources().getString(R.string.xmlCVT_ErrorDescription))) {
                                                    mapXmlResult.put(curTagName, xmlParser.getText());
                                                }
                                                break;

                                            default:
                                                break;
                                        }
                                        xmlParser.next();
                                    }
                                } catch (XmlPullParserException x) {
                                    Utils.safePrintError(x); // x.printStackTrace();
                                    lvIsSuccess = false;
                                } catch (IOException io) {
                                    Utils.safePrintError(io); // io.printStackTrace();
                                    lvIsSuccess = false;
                                }

                                */


                                    lvStubNum = Utils.getjsonString(jItem, "ticket");
                                    String entry_id = Utils.getjsonString(jItem, "entry_id");
                                    String date = Utils.getjsonString(jItem, "date");

                                        if (lvStubNum != null) {
                                            CurTalon parCurTalon;
                                            parCurTalon = (CurTalon) mParams.get(Actions.PARAM_CUR_TALON);
                                            parCurTalon.setStubNum(lvStubNum);
                                            parCurTalon.setDVTID(entry_id);
                                            lvIsSuccess = true;
                                        }

                                        //lvIsSuccess = false;
                                        //putMapResult(false, lvDescription);

                            } catch (JSONException e) {
                                Utils.safePrintError(e); // e.printStackTrace();
                                lvIsSuccess = false;
                                putMapResult(false, e.getMessage());
                            }



                        }
                        else
                        {
                                lvIsSuccess = false;
                            lvDescription = Utils.getjsonString(jsonObject, "message");
                           // putMapResult(false, message_error);
                        }
                        break;

                    case Actions.ACTION_SEND_MAIL:
                        //всегда положительный результат
                        lvIsSuccess = true;
                        if (jsonObject.has("message")) {
                            lvDescription = Utils.getjsonString(jsonObject, "message");
                        } else {
                            lvDescription = "Возникла проблема при отправке уведомелния на email.";
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                Utils.safePrintError(e); // e.printStackTrace();
                mErrMsg = e.getMessage();
                //Log.d(DbgTAG, "error: " + e.getMessage());
            } finally {
                if (lvIsSuccess)
                    lvDescription = Actions.getActionDesc(mCurSubAction);
                putMapResult(lvIsSuccess, lvDescription);
            }
        }
        //Log.d(DbgTAG, "parseResponseCreateVisit--<");
    }

    @Override
    public void onPostExecuteUI(String response, boolean success) {
        //Log.d(DbgTAG, "onPostExecuteUI-->");
        //Log.d(DbgTAG, "responce:\n" + response);
        mResult = new HashMap<String, Object>();
        mResult.put(Actions.RESULT_KEY_RESPONSE, response);
        try {
            if (!success) {
                throw new Exception(response);
            }
            JSONObject json = new JSONObject(response);
            //Log.d(DbgTAG, json.toString());
            switch (mCurAction) {
                case Actions.ACTION_SUBMIT:
                    parseResponseAuthorization(json);
                    break;
                case Actions.ACTION_PATIENT_ORDERS:
                    parseResponcePatientOrder(json);
                    break;
                case Actions.ACTION_CANCEL_VISIT:
                    parseResponseCancelVisit(json);
                    break;
                case Actions.ACTION_CREATE_VISIT:
                    parseResponseCreateVisit(json);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
            mErrMsg = e.getMessage();
            putMapResult(false, e.getMessage() + "\n" + response);
            //Utils.d(e.getMessage());

            mCurAction = Actions.ACTION_ERROR;

        } finally {
            if (mCallback != null) mCallback.onProc(mResult);
        }

        //Log.d(DbgTAG, "onPostExecuteUI--<");
    }

    public void authorizationPoilis(Polis polis, Callback callback, MyAsyncHttp.CallbackNextRequest callbackNextRequest) {
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_POLIS, polis);
        setCallback(callback);
        if (Utils.isDebugMode())
            mDBHelper.clean(LpuLinkPolis.class);
        mCurAction = Actions.ACTION_SUBMIT;
        process(mCurAction, callbackNextRequest, null, "Авторизация полиса №" + polis.getPolusNum());

    }

    public void getPatientOrder(Polis polis, Talon talon, Callback callback, MyAsyncHttp.CallbackNextRequest callbackNextRequest) {
        if (mAuthorizeSuccess) {
            mParams = new HashMap<String, Object>();
            mParams.put(Actions.PARAM_POLIS, polis);
            mParams.put(Actions.PARAM_TALON, talon);
            setCallback(callback);
            mCurAction = Actions.ACTION_PATIENT_ORDERS;
            process(mCurAction, callbackNextRequest, null, "Обновления списка талонов для полиса №" + polis.getPolusNum() + "\n" + polis.getFio());
        } else {
            if (callbackNextRequest != null) callbackNextRequest.onNextRequest();
        }
    }

    public void getPatientOrder(boolean isNeedAuthorization, final Polis polis, final Talon talon, final Callback callback) {
        if (isNeedAuthorization) {
            authorizationPoilis(polis, null, new MyAsyncHttp.CallbackNextRequest() {
                @Override
                public void onNextRequest() {
                 //   if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS6" + mErrMsg);
                    if (mResult != null)
                        if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                            if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue)))
                                getPatientOrder(polis, talon, callback, null);
                }
            });
        }
        getPatientOrder(polis, talon, callback, null);
    }


    public void getPatientOrder(final Polis polis, final Callback callback) {
        authorizationPoilis(polis, null, new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
                getPatientOrder(polis, null, callback, new MyAsyncHttp.CallbackNextRequest() {
                    @Override
                    public void onNextRequest() {
                        if (mResult != null)
                            if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                                if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue)))
                                    if (mListPolis != null) {
                                        idxArrayPolis++;
                                        if (idxArrayPolis < mListPolis.size()) {
                                            Polis nextPolis = mListPolis.get(idxArrayPolis);
                                            getPatientOrder(nextPolis, callback);
                                        } else mCallback.onFinish(mResult, null);
                                    }
                    }
                });
            }
        });
    }

    public void getPatientOrder(final Callback callback) {
        setCallback(callback);
        mDBHelper.clean(Talon.class);
        mListPolis = (ArrayList<Polis>) mDBHelper.getArrayList(Polis.class, null);
        if (mListPolis.size() > 0) {
            idxArrayPolis = 0;
            Polis polis = mListPolis.get(idxArrayPolis);
            getPatientOrder(polis, callback);

        } else {

            //Utils.msgInfo("Полисов для синхронизации не найдено!");
        }
    }

    public void process(String action, MyAsyncHttp.CallbackNextRequest callbackNextRequest, MyAsyncHttp.CallbackParserRequest callbackParserRequest, String msgDlg) {
        //Log.d(DbgTAG, "process-->");
        try {

            if (!mAvaibleServer) {
                return;
            }

            //Log.d(DbgTAG, "	action: " + action);
            // mCurAction = action;
            Polis parPolis = null;
            Talon parTalon = null;
            CurTalon parCurTalon = null;
            if (mParams.containsKey(Actions.PARAM_POLIS))
                parPolis = (Polis) mParams.get(Actions.PARAM_POLIS);
            if (mParams.containsKey(Actions.PARAM_TALON))
                parTalon = (Talon) mParams.get(Actions.PARAM_TALON);
            if (mParams.containsKey(Actions.PARAM_CUR_TALON))
                parCurTalon = (CurTalon) mParams.get(Actions.PARAM_CUR_TALON);

            RequestParams params;
            params = new RequestParams();
            switch (action) {
                case Actions.ACTION_PATIENT_ORDERS:
                    mCurSoap = Actions.SOAP_DOCTOR_APP +Actions.ACTION_GET_DOCT_LIST_NEW +"/"+ Actions.ACTION_ENTRY;
                    mTypeRequest = Actions.HTTP_REQ_GET;
                    break;
                case Actions.ACTION_CANCEL_VISIT:
                    if (parTalon == null)
                        throw new Exception("Параметр parTalon не инициализирован!");
                //    params.add("lpuCode", parTalon.getLpu().toString());
                 //   params.add("visitId", parTalon.getDVTID().toString());
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_DOCT_LIST_NEW + "/"+ Actions.ACTION_ENTRY+"/"+Actions.ACTION_CANCEL_VISIT+"/"+parTalon.getDVTID();
                   // mTypeRequest = Actions.HTTP_REQ_POST;
                    mTypeRequest = Actions.HTTP_REQ_GET;
                    break;

                case Actions.ACTION_SAVE_EMAIL:
                    if (parPolis == null)
                        throw new Exception("Параметр parPolis не инициализирован!");
                    params.add("email", parPolis.getEmail());
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_SAVE_EMAIL;
                    mTypeRequest = Actions.HTTP_REQ_POST;
                    break;
                case Actions.ACTION_SET_LAST_STEP:
                    if (parCurTalon == null)
                        throw new Exception("Параметр parCurTalon не инициализирован!");
                    params.add("DTTID", parCurTalon.getTimeSchedule());
                    params.add("lpuCode", parCurTalon.getLpu().toString());
                    params.add("scenery", "0");
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_SET_LAST_STEP;
                    mTypeRequest = Actions.HTTP_REQ_POST;
                    break;
                case Actions.ACTION_SUBMIT:
                    if (parPolis == null)
                        throw new Exception("Параметр parPolis не инициализирован!");
                    params.add("birthday", parPolis.getBirthday());
                    params.add("nPol", parPolis.getPolusNum());
                    params.add("auth", "1");
                    params.add("sPol", "");
                    params.add("pol", parPolis.getPolusNum());
                    mCurSoap = Actions.SOAP_DOCTOR_APP +"api/"+ Actions.ACTION_PERSONAL;
                    mTypeRequest = Actions.HTTP_REQ_POST;
                    break;
                case Actions.ACTION_CREATE_VISIT:
                    if (parPolis == null)
                        throw new Exception("Параметр parPolis не инициализирован!");

                    if (parCurTalon == null)
                        throw new Exception("Параметр parCurTalon не инициализирован!");

                    params.add("date_visit", parCurTalon.getDateStr());
                    params.add("doctor_id", parCurTalon.getDoctor());
                    params.add("email", parPolis.getEmail());
                    params.add("lpu_code", parCurTalon.getLpu().toString());
                    params.add("time_visit", parCurTalon.getTimeStr());

                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_DOCT_LIST_NEW + "/entry/create";
                    mTypeRequest = Actions.HTTP_REQ_POST;
                    break;
                case Actions.ACTION_SEND_MAIL:
                    if (parPolis == null)
                        throw new Exception("Параметр parPolis не инициализирован!");
                    if (parCurTalon == null)
                        throw new Exception("Параметр parCurTalon не инициализирован!");
                    Speciality curSpec = Speciality.get(parCurTalon.getSpec());
                    Doctor curDoctor = Doctor.get(parCurTalon.getDoctor());
                    LPU curClinic = LPU.get4LpuCode(parCurTalon.getLpu());

                    /*
                    params.add("datetime", parCurTalon.getDateStr() + " " + parCurTalon.getTimeStr());
                    params.add("lpuCode", parCurTalon.getLpu().toString());
                    params.add("doctorData", curSpec.getName());
                    params.add("email", parPolis.getEmail());
                    params.add("fio", curDoctor.getFio());
                    params.add("lpuAddress", curClinic.getADDRESS());
                    params.add("lpuName", curClinic.getNAME());
                    params.add("pol", parPolis.getPolusNum());
                    params.add("stubNum", parCurTalon.getStubNum());
                    */

                    params.add("birthday", parPolis.getBirthday());
                    params.add("confirm", "true");
                    params.add("email", parPolis.getEmail());
                    params.add("id", parCurTalon.getDVTID());
                    params.add("nPol", parPolis.getPolusNum());
                    params.add("sPol", "");
                    params.add("type", "entry");

                    mCurSoap = Actions.SOAP_DOCTOR_APP_REST + Actions.ACTION_SEND_MAIL;
                    mTypeRequest = Actions.HTTP_REQ_POST;
                    break;
                //получение списка городов
                case Actions.ACTION_GET_CITY_LIST:
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_CITY_LIST;
                    mTypeRequest = Actions.HTTP_REQ_GET;
                    break;
                //получени списка поликлиник
                case Actions.ACTION_GET_LPU_LIST:
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_LPU_LIST + "/" + mParams.get(Actions.PARAM_CITY);
                    mTypeRequest = Actions.HTTP_REQ_GET;
                    break;
                //получение списка специализаций
                case Actions.ACTION_GET_SPEC_LIST:
                    params.add("lpuCode", (String) mParams.get(Actions.PARAM_LPU_CODE));
                //    params.add("scenery", "1");
                //    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_SPEC_LIST;
                    mCurSoap = Actions.SOAP_DOCTOR_APP + "api/departments";
                    mTypeRequest = Actions.HTTP_REQ_GET;
                    break;
                // получение списка докторов
                case Actions.ACTION_GET_DOCT_LIST:

                    /*
                    //old

                    params.add("lpuCode", (String) mParams.get(Actions.PARAM_LPU_CODE));
                    params.add("specId", (String) mParams.get(Actions.PARAM_SPEC_ID));
                    params.add("days", "14");
                    params.add("scenery", "1");
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_DOCT_LIST;
                    */
                    params.add("lpuCode", (String) mParams.get(Actions.PARAM_LPU_CODE));
                    params.add("departmentId", String.valueOf(mParams.get(Actions.PARAM_SPEC_ID)));
                    params.add("days", "14");
                    mCurSoap = Actions.SOAP_DOCTOR_APP  + Actions.ACTION_GET_DOCT_LIST_NEW;

                    mTypeRequest = Actions.HTTP_REQ_GET;
                    break;
                case Actions.ACTION_GET_TALON_LIST:
                    /*
                    String orderedStrParams = (String) mParams.get(Actions.PARAM_LPU_CODE) +
                            "/" + ((Integer) mParams.get(Actions.PARAM_DOC_POST)).toString() +
                            "/" + (String) mParams.get(Actions.PARAM_DATE_POST) +
                            "?scenery=1";
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_TALON_LIST + orderedStrParams;
                    */
                    String orderedStrParams = "/" + (String) mParams.get(Actions.PARAM_DOCTOR_ID) +
                            "?day=" + (String) mParams.get(Actions.PARAM_DATE_POST);
                    mCurSoap = Actions.SOAP_DOCTOR_APP + Actions.ACTION_GET_DOCT_LIST_NEW + orderedStrParams;

                    mTypeRequest = Actions.HTTP_REQ_GET;
                    break;
            }

            MyAsyncHttp myAsyncHttp = new MyAsyncHttp(mContext, mTypeRequest, mCurSoap, (String) Utils.nvl(msgDlg, "Получение информации с сервера"), params, this, mIsNeedShowProcDialog);
            myAsyncHttp.setCallbackNextRequest(callbackNextRequest);
            myAsyncHttp.setCallbackParserRequest(callbackParserRequest);
            myAsyncHttp.execute();

        } catch (Exception e) {
            //Log.d(DbgTAG, "	error: " + e.getMessage());
            Utils.safePrintError(e); // e.printStackTrace();
            mErrMsg = e.getMessage();
        }
        //Log.d(DbgTAG, "process--<");
    }

    public void process(String typeRequest
            , String curSoap
            , RequestParams requestParams
            , String msgDlg
            , MyAsyncHttp.Callback callbackPostExecuteUI
            , MyAsyncHttp.CallbackNextRequest callbackNextRequest
            , MyAsyncHttp.CallbackParserRequest callbackParserRequest
            , MyAsyncHttp.CallbackParserRequestByte callbackParserRequestByte
    ) {
        //Log.d(DbgTAG, "process-->");
        try {

            //Log.d(DbgTAG, "	typeRequest: " + typeRequest);
            //Log.d(DbgTAG, "	curSoap: " + curSoap);

            mTypeRequest = typeRequest;
            mCurSoap = curSoap;

            MyAsyncHttp myAsyncHttp = new MyAsyncHttp(mContext, mTypeRequest, mCurSoap, (String) Utils.nvl(msgDlg, "Получение информации с сервера"), requestParams, callbackPostExecuteUI, mIsNeedShowProcDialog);
            myAsyncHttp.setCallbackNextRequest(callbackNextRequest);
            myAsyncHttp.setCallbackParserRequest(callbackParserRequest);
            myAsyncHttp.setCallbackParserRequestByte(callbackParserRequestByte);
            myAsyncHttp.execute();

        } catch (Exception e) {
            //Log.d(DbgTAG, "	error: " + e.getMessage());
            Utils.safePrintError(e); // e.printStackTrace();
            mErrMsg = e.getMessage();
        }
        //Log.d(DbgTAG, "process--<");
    }

    //отмена талона
    public void setCancelVisit(Talon talon, Callback callback, MyAsyncHttp.CallbackNextRequest callbackNextRequest) {
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_TALON, talon);
        mCurAction = Actions.ACTION_CANCEL_VISIT;
        process(mCurAction, callbackNextRequest, null, "Отмена талона №" + talon.getStubNum());
    }

    public void postCancelVisit(final Polis polis, final Talon talon, final Callback callback) {
        setCallback(callback);
        authorizationPoilis(polis, null, new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
          //      if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS7" + mErrMsg);
                if (mResult != null)
                    if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                        if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue)))
                            if (mAuthorizeSuccess) {
                                setCancelVisit(talon, callback, new MyAsyncHttp.CallbackNextRequest() {
                                    @Override
                                    public void onNextRequest() {
                                       // if (mErrMsg != null && !mErrMsg.isEmpty())
                                         //   Utils.msgError("#HS8" + mErrMsg);
                                        callback.onFinish(mResult, null);
                                    }
                                });
                            }
            }
        });
    }



    //зарезервировать талон
    public void postCreateVisit(final Polis polis, final CurTalon curTalon, final Callback callback) {
        setCallback(callback);
        mCurAction = Actions.ACTION_CREATE_VISIT;
        mCurSubAction = Actions.ACTION_SAVE_EMAIL;
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_POLIS, polis);
        mParams.put(Actions.PARAM_CUR_TALON, curTalon);
        //сохранение email
        process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
                //if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS9" + mErrMsg);
                if (mResult != null)
                    if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                        if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue))) {
                            mCurSubAction = Actions.ACTION_SET_LAST_STEP;
                            //сохранение последнего шага
                            process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
                                @Override
                                public void onNextRequest() {
                                    // if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS10" + mErrMsg);
                                    if (mResult != null)
                                        if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                                            if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue))) {
                                                mCurSubAction = Actions.ACTION_SUBMIT;
                                                // авторизация
                                                process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
                                                    @Override
                                                    public void onNextRequest() {
                                                        // if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS11" + mErrMsg);
                                                        if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                                                            if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue))) {
                                                                // резервирование талона
                                                                mCurSubAction = Actions.ACTION_CREATE_VISIT;
                                                                process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
                                                                    @Override
                                                                    public void onNextRequest() {
                                                                        // if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS12" + mErrMsg);
                                                                        if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                                                                            if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue))) {
                                                                                // резервирование талона
                                                                                mCurSubAction = Actions.ACTION_SEND_MAIL;
                                                                                process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
                                                                                    @Override
                                                                                    public void onNextRequest() {
                                                                                        //if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS13" + mErrMsg);
                                                                                        callback.onFinish(mResult, null);
                                                                                    }
                                                                                }, null, Actions.getActionDesc(mCurSubAction));
                                                                            }

                                                                    }
                                                                }, null, Actions.getActionDesc(mCurSubAction));
                                                            }
                                                    }

                                                }, null, Actions.getActionDesc(mCurSubAction));
                                            }
                                }
                            }, null, Actions.getActionDesc(mCurSubAction));
                        }
            }
        }, null, Actions.getActionDesc(mCurSubAction));
    }


    //зарезервировать талон
    public void postCreateVisitNew(final Polis polis, final CurTalon curTalon, final Callback callback) {
        setCallback(callback);
        mCurAction = Actions.ACTION_CREATE_VISIT;
        mCurSubAction = Actions.ACTION_SUBMIT;
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_POLIS, polis);
        mParams.put(Actions.PARAM_CUR_TALON, curTalon);
        //сохранение email
        process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
                                                    @Override
                                                    public void onNextRequest() {
                                                        // if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS11" + mErrMsg);
                                                        if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                                                            if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue))) {
                                                                // резервирование талона
                                                                mCurSubAction = Actions.ACTION_CREATE_VISIT;
                                                                process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
                                                                    @Override
                                                                    public void onNextRequest() {
                                                                        // if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS12" + mErrMsg);
                                                                        if (mResult.containsKey(Actions.RESULT_KEY_SUCCESS))
                                                                            if (mResult.get(Actions.RESULT_KEY_SUCCESS).equals(Utils.getResString(R.string.sTrue))) {
                                                                                // резервирование талона
                                                                                mCurSubAction = Actions.ACTION_SEND_MAIL;
                                                                                process(mCurSubAction, new MyAsyncHttp.CallbackNextRequest() {
                                                                                    @Override
                                                                                    public void onNextRequest() {
                                                                                        //if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS13" + mErrMsg);
                                                                                        callback.onFinish(mResult, null);
                                                                                    }
                                                                                }, null, Actions.getActionDesc(mCurSubAction));
                                                                            }

                                                                    }
                                                                }, null, Actions.getActionDesc(mCurSubAction));
                                                            }
                                                    }

                                                }, null, Actions.getActionDesc(mCurSubAction));

    }


    // парсинг запроса получения населенных пунктов
    private HashMap<String, Object> fillCity(JSONObject json, ArrayList<HashMap<String, Object>> list) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        DBHelper db = DBFactory.getInstance(mContext).getDBHelper(City.class);

        City city = new City();
        city.setFldID( mContext.getString(R.string.prefixCityId) + Utils.getjsonString(json, "ID"));
        city.setFldNAME(Utils.getjsonString(json, "NAME"));
        city.setFldOKATO(Utils.getjsonString(json, "OKATO"));
        city.setFldcount(Utils.getjsonInt(json, "count"));
        db.insert(city);

        String itemStr1 = new String(city.getFldNAME());
        String itemStr2 = new String("Кол-во мед. учреждений: ").concat(city.getFldcount().toString());
        map.put(Actions.RESULT_ARRLST_RESULT_CODE, city.getFldID().toString());
        map.put(Actions.RESULT_ARRLST_NAME, itemStr1);
        map.put(Actions.RESULT_ARRLST_VALUME, itemStr2);
        //map.put(DataProvider.KEY_PARENT_NODE, null);
        map.put(DataProvider.KEY_PARENT_GROUP, "0");
        return map;
    }

    //получить список городов
    public void getCityList(final Callback callback, final String polisId) {
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_POLIS_ID, polisId);
        setCallback(callback);
        mCurAction = Actions.ACTION_GET_CITY_LIST;
        mResultArrayList = new ArrayList<HashMap<String, Object>>();

        // коллбак для определения запуска след. запроса. - просто возвращаем результат в callback
        MyAsyncHttp.CallbackNextRequest callbackNextRequest = new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
               // if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS14" + mErrMsg);
                callback.onFinish(mResult, mResultArrayList);
            }
        };

        // разбор результата
        MyAsyncHttp.CallbackParserRequest callbackParserRequest = new MyAsyncHttp.CallbackParserRequest() {
            @Override
            public void onParse(MyAsyncHttp longTask, String responce, ProgressDialog progressDialog) {
                longTask.onPublishProcess("Обработка данных...");
                try {
                    JSONObject json = new JSONObject(responce);

                    boolean lv_success = json.getBoolean("success");
                    if (lv_success == false) {
                        String lv_code = json.getString("code");
                        if (lv_code.equalsIgnoreCase("fault")) {
                            putMapResult(false, "Отсутствует связь с данным мед. учреждением. Попробуйте позже.");
                            return;
                        }
                    }

                    mResultArrayList = new ArrayList<HashMap<String, Object>>();

                    ArrayList<HashMap<Integer, String>> listLinkedCity = new ArrayList<>();


                    HashMap<String, Object> mapLinkedCity = new HashMap<String, Object>();
                    HashMap<String, Object> mapNotLinkedCity = new HashMap<String, Object>();
                    StringBuilder sqlQuery = new StringBuilder();
                    sqlQuery.append("select distinct cc.fldID as city_id ");
                    sqlQuery.append("from LPU as l ");
                    sqlQuery.append("inner join LpuLinkPolis as lnk on l.ID = lnk.LPU_ID ");
                    sqlQuery.append("inner join City as cc on l.CITY = cc.fldID ");
                    sqlQuery.append("where lnk.POLIS_ID = ? ");
                    sqlQuery.append("order by 1");
                    listLinkedCity = mDBHelper.rawQueryHashMap(sqlQuery.toString(), new String[]{(String)(mParams.get(Actions.PARAM_POLIS_ID))});


                    // Log.d(DbgTAG, "listLinkedCity.size: "+String.valueOf(listLinkedCity.size()));
                    if (listLinkedCity != null && listLinkedCity.size() > 0) {
                        mapLinkedCity.put(Actions.RESULT_ARRLST_NAME, mContext.getResources().getString(R.string.itemsLinkedCity));
                        mapLinkedCity.put(Actions.RESULT_ARRLST_VALUME, mContext.getResources().getString(R.string.itemsLinkedCity_footer));
                        mapLinkedCity.put(DataProvider.KEY_PARENT_NODE, null);
                        mapLinkedCity.put(DataProvider.KEY_PARENT_GROUP, "1");
                        mResultArrayList.add(mapLinkedCity);

                        mapNotLinkedCity.put(Actions.RESULT_ARRLST_NAME, mContext.getResources().getString(R.string.itemsNotLinkedCity));
                        mapNotLinkedCity.put(Actions.RESULT_ARRLST_VALUME, mContext.getResources().getString(R.string.itemsNotLinkedCity_footer));
                        mapNotLinkedCity.put(DataProvider.KEY_PARENT_NODE, null);
                        mapNotLinkedCity.put(DataProvider.KEY_PARENT_GROUP, "1");
                        mResultArrayList.add(mapNotLinkedCity);
                    }

                    JSONArray jsonArrayItems = json.getJSONArray(Actions.json_tag_items);
                    for (int i = 0; i < jsonArrayItems.length(); i++) {
                        JSONObject jsonItem = jsonArrayItems.getJSONObject(i);
                        HashMap<String, Object> map = fillCity(jsonItem, mResultArrayList);
                        if (listLinkedCity != null && listLinkedCity.size() > 0) {
                            boolean isLinkedCity = false;
                            String curCityId = (String) map.get(Actions.RESULT_ARRLST_RESULT_CODE);
                            for (HashMap<Integer, String> m : listLinkedCity) {
                                String tmpCityId = m.get(0);
                                if (tmpCityId.equals(curCityId)) {
                                    isLinkedCity = true;
                                    break;
                                }
                            }
                            if (isLinkedCity) {
                                map.put(DataProvider.KEY_PARENT_NODE, mapLinkedCity);
                            } else {
                                map.put(DataProvider.KEY_PARENT_NODE, mapNotLinkedCity);
                            }
                        }
                        mResultArrayList.add(map);
                    }

                } catch (Exception e) {
                    Utils.safePrintError(e); // e.printStackTrace();
                    mErrMsg = e.getMessage();
                    //Utils.d("onParse: "+e.getMessage());
                }
            }
        };

        process(mCurAction, callbackNextRequest, callbackParserRequest, "Получение списка городов");

    }


    // парсинг результат от getLpuList
    private void fillLpuList(JSONObject json, ArrayList<HashMap<String, Object>> list) {
        try {
            JSONObject jitem = json.getJSONObject("items");
            Iterator<String> iter = jitem.keys();
            DBHelper sqlTblLpu = DBFactory.getInstance(mContext).getDBHelper(LPU.class);
            while (iter.hasNext()) {
                String key = iter.next();
                JSONObject c = jitem.getJSONObject(key);
                HashMap<String, Object> map = new HashMap<String, Object>();
                LPU lpu = new LPU(c.getString("ID")
                        , c.getString("LPUCODE")
                        , c.getString("EMAIL")
                        , c.getString("SITEURL")
                        , c.getString("NAME")
                        , c.getString("IP")
                        , c.getString("ADDRESS")
                        , c.getString("PHONE")
                        , Utils.getjsonInt(c, "ACCESSIBILITY") //c.getInt("ACCESSIBILITY")
                        , c.getString("CHILDREN")
                        , c.getString("CITY")
                        , 0
                        , 0 //c.getInt("isCallDocHome")
                        , "" //c.getString("latitude")
                        , "" //c.getString("longitude")
                );
                if (c.has("isWaitingList")) lpu.setIsWaitingList(c.getInt("isWaitingList"));
                sqlTblLpu.insert(lpu);
                String itemStr1 = new String(lpu.getNAME());
                String itemStr2 = new String("Адрес: " + lpu.getADDRESS() + "\n" + "Телефон: " + lpu.getPHONE());
                map.put(Actions.RESULT_ARRLST_RESULT_CODE, c.getString("LPUCODE"));
                map.put(Actions.RESULT_ARRLST_NAME, itemStr1);
                map.put(Actions.RESULT_ARRLST_VALUME, itemStr2);
                map.put(DataProvider.KEY_PARENT_NODE, null);
                map.put(DataProvider.KEY_PARENT_GROUP, "0");
                list.add(map);
            }
        } catch (JSONException e) {
            Utils.safePrintError(e); // e.printStackTrace();
            mErrMsg = e.getMessage();
        }
    }


    //получить список поликлиник
    public void getLpuList(String city, final Callback callback) {
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_CITY, city.replace(mContext.getString(R.string.prefixCityId),""));

        setCallback(callback);
        mCurAction = Actions.ACTION_GET_LPU_LIST;

        mResultArrayList = new ArrayList<HashMap<String, Object>>();

        // коллбак для определения запуска след. запроса. - просто возвращаем результат в callback
        MyAsyncHttp.CallbackNextRequest callbackNextRequest = new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
                //if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS15" + mErrMsg);
                callback.onFinish(mResult, mResultArrayList);
            }
        };

        // разбор результата
        MyAsyncHttp.CallbackParserRequest callbackParserRequest = new MyAsyncHttp.CallbackParserRequest() {
            @Override
            public void onParse(MyAsyncHttp longTask, String responce, ProgressDialog progressDialog) {
                longTask.onPublishProcess("Обработка данных...");
                try {
                    JSONObject json = new JSONObject(responce);

                    boolean lv_success = json.getBoolean("success");
                    if (lv_success == false) {
                        String lv_code = json.getString("code");
                        if (lv_code.equalsIgnoreCase("fault")) {
                            putMapResult(false, "Отсутствует связь с данным мед. учреждением. Попробуйте позже.");
                            return;
                        }
                    }

                    fillLpuList(json, mResultArrayList);

                } catch (Exception e) {
                    Utils.safePrintError(e); // e.printStackTrace();
                    mErrMsg = e.getMessage();
                    //Utils.d("onParse: "+e.getMessage());
                }
            }
        };
        process(mCurAction, callbackNextRequest, callbackParserRequest, "Получение списка поликлиник");
    }


    // парсинг запроса получения специализаций
    private void fillSpeciality(JSONObject json, ArrayList<HashMap<String, Object>> list) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        DBHelper db = DBFactory.getInstance(mContext).getDBHelper(Speciality.class);
        try {
            //JSONObject jsonSpeciality = json.getJSONObject("Specialty");
            Speciality speciality = new Speciality();
            speciality.setId(Utils.getjsonString(json, "id"));
            speciality.setCode(Utils.getjsonString(json, "code"));
            speciality.setName(Utils.getjsonString(json, "title"));
            db.insert(speciality);
            String itemStr1 = new String(speciality.getName());
            String itemStr2 = new String("Кол. талонов: ").concat(String.valueOf(Utils.getjsonInt(json, "volume")));
            map.put(Actions.RESULT_ARRLST_RESULT_CODE, speciality.getId().toString());
            map.put(Actions.RESULT_ARRLST_NAME, itemStr1);
            map.put(Actions.RESULT_ARRLST_VALUME, itemStr2);
            map.put(DataProvider.KEY_PARENT_NODE, null);
            map.put(DataProvider.KEY_PARENT_GROUP, "0");
            list.add(map);
           } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
            mErrMsg = e.getMessage();
        }
    }

    //получить список специализаций
    public void getSpecList(String lpuCode, final Callback callback) {
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_LPU_CODE, lpuCode);
        setCallback(callback);
        mCurAction = Actions.ACTION_GET_SPEC_LIST;
        mResultArrayList = new ArrayList<HashMap<String, Object>>();

        // коллбак для определения запуска след. запроса. - просто возвращаем результат в callback
        MyAsyncHttp.CallbackNextRequest callbackNextRequest = new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
                if (mErrMsg != null && !mErrMsg.isEmpty()) {
                    if (mErrMsg.contains("Не найдена"))
                        mErrMsg = "Сервер не доступен. Попробуйте позже!";
                //    Utils.msgError("#HS16" + mErrMsg);
                }
                callback.onFinish(mResult, mResultArrayList);
            }
        };

        // разбор результата
        MyAsyncHttp.CallbackParserRequest callbackParserRequest = new MyAsyncHttp.CallbackParserRequest() {
            @Override
            public void onParse(MyAsyncHttp longTask, String responce, ProgressDialog progressDialog) {
                longTask.onPublishProcess("Обработка данных...");
                try {
                    JSONObject json = new JSONObject(responce);

                    boolean lv_success = json.getBoolean("success");
                    if (lv_success == false) {
                        String lv_code = json.getString("code");
                        if (lv_code.equalsIgnoreCase("fault")) {
                            putMapResult(false, "Отсутствует связь с данным мед. учреждением. Попробуйте позже.");
                            return;
                        }
                    }

                    mResultArrayList = new ArrayList<HashMap<String, Object>>();

                    JSONArray jsonArrayItems = json.getJSONArray(Actions.json_tag_items);
                    for (int i = 0; i < jsonArrayItems.length(); i++) {
                        JSONObject jsonItem = jsonArrayItems.getJSONObject(i);
                        fillSpeciality(jsonItem, mResultArrayList);
                    }

                } catch (Exception e) {
                    Utils.safePrintError(e); // e.printStackTrace();
                    mErrMsg = e.getMessage();
                }
            }
        };

        process(mCurAction, callbackNextRequest, callbackParserRequest, "Получение списка специализаций");

    }


    // парсинг запроса получения докторов
    private void fillDoctors(JSONObject json, ArrayList<HashMap<String, Object>> list, boolean fillOnlyDocts, String doctorId) {
        HashMap<String, Object> groupMap = new HashMap<String, Object>();
        DBHelper db = DBFactory.getInstance(mContext).getDBHelper(Doctor.class);
        int cntAllTalons = 0;
        try {

         //   JSONObject jsonDocPost = json.getJSONObject("DocPost");
        //    JSONObject jsonDoctor = jsonDocPost.getJSONObject("Doctor");
            Doctor doctor = new Doctor(Utils.getjsonString(json, "id"),
                    Utils.getjsonString(json, "family"),
                    Utils.getjsonString(json, "name"),
                    Utils.getjsonString(json, "surname")
            );

            if (!doctorId.equals("-1") && ! doctor.getId().equals(doctorId)) return;

            db.insert(doctor);
            DocPost docPost = new DocPost(Utils.getjsonString(json, "person_id"),
                    doctor.getId(),
                    Utils.getjsonString(json, "type_name"),
                    Utils.getjsonString(json, "room"),
                    Utils.getjsonString(json, "position"),
                    Utils.getjsonString(json, "separation")
            );
            db.insert(docPost);

            String itemStr1 = new String(doctor.getFamily() + " " + doctor.getName() + " " + doctor.getPatronymic());
            groupMap.put(Actions.RESULT_ARRLST_NAME, itemStr1);
            groupMap.put(DataProvider.KEY_PARENT_NODE, null);
            groupMap.put(DataProvider.KEY_PARENT_GROUP, fillOnlyDocts ? "0" : "1");
            list.add(groupMap);

            if (fillOnlyDocts) {
                groupMap.put(Actions.RESULT_ARRLST_VALUME, docPost.getDescription());
                groupMap.put(Actions.RESULT_ARRLST_AVALIBLE, "1");
                groupMap.put(Actions.RESULT_ARRLST_IS_NEED_HTTP, false);
                groupMap.put(Actions.RESULT_ARRLST_RESULT_DOCTOR_ID, doctor.getId());
                groupMap.put(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID, docPost.getId());
                groupMap.put(Actions.RESULT_ARRLST_RESULT_DAYSCHEDUL_ID, "0");
                groupMap.put(Actions.RESULT_ARRLST_RESULT_CODE, doctor.getId().toString());

                return;
            }


           // JSONObject jsonDayScheduleList = json.getJSONObject("DayScheduleList");
            JSONArray jsonDaySchedulArr = json.getJSONArray("schedule");
            for (int i = 0; i < jsonDaySchedulArr.length(); i++) {
                JSONObject jsonDaySchedulObj = jsonDaySchedulArr.getJSONObject(i);
                String timeFrom = Utils.getjsonString(jsonDaySchedulObj, "time_from");
                String timeTo = Utils.getjsonString(jsonDaySchedulObj, "time_to");

                String hourFrom = "-1";
                String minutFrom = "-1";
                String hourTo = "-1";
                String minutTo = "-1";

                if (!timeFrom.isEmpty())
                {
                    hourFrom = timeFrom.substring(0,1);
                    minutFrom = timeFrom.substring(3,4);
                }


                if (!timeTo.isEmpty())
                {
                    hourTo = timeTo.substring(0,1);
                    minutTo = timeTo.substring(3,4);
                }

                String timePeriodStr = new String();
                JSONObject jsonDocBusyType = jsonDaySchedulObj.getJSONObject("docBusyType");
                String descrTime    = Utils.getjsonString(jsonDocBusyType, "name");
                if (!timeFrom.isEmpty() && !timeTo.isEmpty())
                    {
                        timePeriodStr = timeFrom + "-"+ timeTo;
                    }

                DaySchedul daySchedul = new DaySchedul("0"
                        , Utils.getjsonString(jsonDaySchedulObj, "date")
                        , Integer.valueOf(hourFrom)
                        , Integer.valueOf(hourTo)
                        , Integer.valueOf(minutFrom)
                        , Integer.valueOf(minutTo)
                        , Utils.getjsonInt(jsonDaySchedulObj, "count_tickets")
                        , 0//Utils.getjsonInt(jsonDaySchedulObj, "FlagAccess")
                        , ""//Utils.getjsonString(jsonDaySchedulObj, "OutReason")
                );

                cntAllTalons += daySchedul.getTicketCount();

                HashMap<String, Object> itemMap = new HashMap<String, Object>();
                String subtemStr1 = Utils.strToDateWeekStr(daySchedul.getDate(), "dd.MM.yyyy");
                itemMap.put(Actions.RESULT_ARRLST_NAME, subtemStr1);
                String subitemStr2;

                if (daySchedul.getTicketCount() == 0) {
             //       subitemStr2 = "Нет приема";
                    itemMap.put(DataProvider.KEY_PARENT_GROUP, "0");
                }
                else
                {
               //      subitemStr2 = timeFrom + "-"+ timeTo+"\nКол-во талонов: " + daySchedul.getTicketCount();
                     itemMap.put(DataProvider.KEY_PARENT_GROUP, "1");
                }
                subitemStr2 = descrTime + (timePeriodStr.isEmpty() == true ? "" : ": "+timePeriodStr) + "\nКол-во свободных талонов: " + daySchedul.getTicketCount();

                if (Utils.isDebugMode()) subitemStr2 = subitemStr2 + "\n" + daySchedul.toString();
                itemMap.put(Actions.RESULT_ARRLST_VALUME, subitemStr2);
                itemMap.put(Actions.RESULT_ARRLST_AVALIBLE_CNT, daySchedul.getTicketCount());

                itemMap.put(Actions.RESULT_ARRLST_AVALIBLE, daySchedul.getTicketCount() != 0 ? "1" : "0");


                itemMap.put(DataProvider.KEY_PARENT_NODE, groupMap);
                itemMap.put(Actions.RESULT_ARRLST_RESULT_DATE_STR, daySchedul.getDate().toString().substring(0, 10));
                itemMap.put(Actions.RESULT_ARRLST_IS_NEED_HTTP, true);
                itemMap.put(Actions.RESULT_ARRLST_RESULT_DOCTOR_ID, doctor.getId());
                itemMap.put(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID, docPost.getId());
                itemMap.put(Actions.RESULT_ARRLST_RESULT_DAYSCHEDUL_ID, daySchedul.getId());

                list.add(itemMap);

               // String itemStr2 = "Кол. талонов: " + cntAllTalons;
                String itemStr2 = "Кол. талонов: " + json.getString("count_tickets");

                groupMap.put(Actions.RESULT_ARRLST_VALUME, itemStr2);
            }
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
            mErrMsg = e.getMessage();
        }
    }


    //получить список докторов и дат
    public void getDoctList(String lpuCode, String specId, final Callback callback, final boolean fillOnlyDocts, final String doctorId, String msgProc) {
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_LPU_CODE, lpuCode);
        mParams.put(Actions.PARAM_SPEC_ID, specId);
        setCallback(callback);
        mCurAction = Actions.ACTION_GET_DOCT_LIST;

        mResultArrayList = new ArrayList<HashMap<String, Object>>();

        // коллбак для определения запуска след. запроса. - просто возвращаем результат в callback
        MyAsyncHttp.CallbackNextRequest callbackNextRequest = new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
                //if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS17" + mErrMsg);
                callback.onFinish(mResult, mResultArrayList);
            }
        };

        // разбор результата
        MyAsyncHttp.CallbackParserRequest callbackParserRequest = new MyAsyncHttp.CallbackParserRequest() {
            @Override
            public void onParse(MyAsyncHttp longTask, String responce, ProgressDialog progressDialog) {
                longTask.onPublishProcess("Обработка данных...");
                try {
                    JSONObject json = new JSONObject(responce);

                    boolean lv_success = json.getBoolean("success");
                    if (lv_success == false) {
                        String lv_code = json.getString("code");
                        if (lv_code.equalsIgnoreCase("fault")) {
                            putMapResult(false, "Отсутствует связь с данным мед. учреждением. Попробуйте позже.");
                            return;
                        }
                    }

                    mResultArrayList = new ArrayList<HashMap<String, Object>>();

                    DBHelper dbh = DBFactory.getInstance(mContext).getDBHelper(DoctorList.class);
                    dbh.clean(DoctorList.class);
                    dbh.clean(DayScheduleList.class);
                    dbh.clean(DaySchedul.class);

                    JSONArray jsonArrayItems = json.getJSONArray(Actions.json_tag_items);
                    for (int i = 0; i < jsonArrayItems.length(); i++) {
                        JSONObject jsonItem = jsonArrayItems.getJSONObject(i);
                        if (jsonItem.has("doctors")) {
                            JSONArray jArrDoctors = jsonItem.getJSONArray("doctors");
                            for (int j = 0; j < jArrDoctors.length(); j++) {
                                JSONObject jItemDoctor = jArrDoctors.getJSONObject(j);
                                fillDoctors(jItemDoctor, mResultArrayList, fillOnlyDocts, doctorId);
                            }
                        }
                    }

                } catch (Exception e) {
                    Utils.safePrintError(e); // e.printStackTrace();
                    mErrMsg = e.getMessage();
                }
            }
        };

        process(mCurAction, callbackNextRequest, callbackParserRequest, (String) Utils.nvl(msgProc, "Получение списка специалистов"));

    }

    // парсинг запроса получения докторов
    private void fillTimes(JSONObject jsonItem, ArrayList<HashMap<String, Object>> list) {
        try {

            TimeItems timeItems = new TimeItems(Utils.getjsonString(jsonItem, "id")
                    , (String) mParams.get(Actions.PARAM_DAYSCHEDUL_ID)
                    , Utils.getjsonString(jsonItem, "time")
                    , Utils.getjsonInt(jsonItem, "docBusyType")
                    , Utils.getjsonInt(jsonItem, "docBusyCode")
                    , Utils.getjsonBool(jsonItem, "access") == true ? 1 : 0
                    , Utils.getjsonInt(jsonItem, "visit_maker")
            );

            //db.insert(timeItems);

            int busyDay = Utils.getjsonBool(jsonItem, "busy") == false ? 0 : 1;
            String busyText = Utils.getjsonString(jsonItem, "description");

            if (busyDay == 0){
                busyText = "Запись возможна";
            }

            HashMap<String, Object> talonTime = new HashMap<String, Object>();
            String talonTimeStr1 = timeItems.getTime();
            String talonTimeStr2 = busyText;
            if (Utils.isDebugMode()) talonTimeStr2 = talonTimeStr2 + "\n" + timeItems.toString();
            talonTime.put(Actions.RESULT_ARRLST_RESULT_CODE, mParams.get(Actions.PARAM_DOCTOR_ID));
            talonTime.put(Actions.RESULT_ARRLST_RESULT_TIME_STR, timeItems.getTime());
            talonTime.put(Actions.RESULT_ARRLST_RESULT_DATE_STR, mParams.get(Actions.PARAM_DATE_POST));
            talonTime.put(Actions.RESULT_ARRLST_RESULT_TIME_ID, timeItems.getId() + "");
            talonTime.put(Actions.RESULT_ARRLST_RESULT_DOCPOST_ID, mParams.get(Actions.PARAM_DOC_POST));
            talonTime.put(Actions.RESULT_ARRLST_NAME, talonTimeStr1);
            talonTime.put(Actions.RESULT_ARRLST_VALUME, talonTimeStr2);

            talonTime.put(Actions.RESULT_ARRLST_AVALIBLE, busyDay == 0 ? "1" : "0");
            talonTime.put(Actions.RESULT_ARRLST_BUSY_DAY, String.valueOf(busyDay));

            HashMap<String, Object> parentMap = (HashMap<String, Object>) mParams.get(Actions.PARAM_PARENT_ITEM);
            parentMap.put(Actions.RESULT_ARRLST_IS_NEED_HTTP, false);

            talonTime.put(DataProvider.KEY_PARENT_NODE, mParams.get(Actions.PARAM_PARENT_ITEM));
            talonTime.put(DataProvider.KEY_PARENT_GROUP, "0");
            list.add(talonTime);
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
            mErrMsg = e.getMessage();
        }
    }

    //получить список свободного времени за дату
    public void getTimeList(String lpuCode, String doctorId, String docPost, String datePost, String daySchedulId, HashMap<String, Object> parentItem, ArrayList<HashMap<String, Object>> list, final Callback callback) {
        mParams = new HashMap<String, Object>();
        mParams.put(Actions.PARAM_LPU_CODE, lpuCode);
        mParams.put(Actions.PARAM_DOC_POST, docPost);
        mParams.put(Actions.PARAM_DATE_POST, datePost);
        mParams.put(Actions.PARAM_PARENT_ITEM, parentItem);
        mParams.put(Actions.PARAM_DAYSCHEDUL_ID, daySchedulId);
        mParams.put(Actions.PARAM_DOCTOR_ID, doctorId);
        setCallback(callback);
        mCurAction = Actions.ACTION_GET_TALON_LIST;

        mResultArrayList = list;

        // коллбак для определения запуска след. запроса. - просто возвращаем результат в callback
        MyAsyncHttp.CallbackNextRequest callbackNextRequest = new MyAsyncHttp.CallbackNextRequest() {
            @Override
            public void onNextRequest() {
             //   if (mErrMsg != null && !mErrMsg.isEmpty()) Utils.msgError("#HS18" + mErrMsg);
                callback.onFinish(mResult, mResultArrayList);
            }
        };

        // разбор результата
        MyAsyncHttp.CallbackParserRequest callbackParserRequest = new MyAsyncHttp.CallbackParserRequest() {
            @Override
            public void onParse(MyAsyncHttp longTask, String responce, ProgressDialog progressDialog) {
                longTask.onPublishProcess("Обработка данных...");
                try {
                    JSONObject json = new JSONObject(responce);

                    boolean lv_success = json.getBoolean("success");
                    if (lv_success == false) {
                        String lv_code = json.getString("code");
                        if (lv_code.equalsIgnoreCase("fault")) {
                            putMapResult(false, "Отсутствует связь с данным мед. учреждением. Попробуйте позже.");
                            return;
                        }
                    }

                    JSONObject jsonItem = json.getJSONObject("item");
                    JSONArray jsonArrayItems = jsonItem.getJSONArray("schedule");
                    for (int i = 0; i < jsonArrayItems.length(); i++) {
                        JSONObject jsonItem2 = jsonArrayItems.getJSONObject(i);
                        JSONArray jsonItemsTime = jsonItem2.getJSONArray("items");
                        for (int j = 0; j < jsonItemsTime.length(); j++) {
                            JSONObject jsonTime = jsonItemsTime.getJSONObject(j);
                            fillTimes(jsonTime, mResultArrayList);
                        }
                    }

                } catch (Exception e) {
                    Utils.safePrintError(e); // e.printStackTrace();
                    mErrMsg = e.getMessage();
                }
            }
        };

        process(mCurAction, callbackNextRequest, callbackParserRequest, "Получение талонов");

    }


}
