package ru.bmixsoft.jsontest.httpserv;

import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 15.02.2017.
 */
public final class  Actions {

    public static final String PARAM_CITY = "city";
    public static final String PARAM_LPU_CODE = "lpu_code";
    public static final String PARAM_DOC_POST = "doc_post";
    public static final String PARAM_DATE_POST = "date_post";
    public static final String PARAM_PARENT_ITEM = "parent_item";
    public static final String PARAM_DOCTOR_ID = "doctor_id";
    public static final String PARAM_DAYSCHEDUL_ID = "dayschedul_id";
    public static final String PARAM_SPEC_ID = "spec_id";
    public static final String PARAM_POLIS = "POLIS";
    public static final String PARAM_TALON = "TALON";
    public static final String PARAM_CUR_TALON = "CUR_TALON";
    public static final String PARAM_POLIS_ID = "POLIS_ID";


    public static final String ACTION_PERSONAL = "personal";

    public static final String ACTION_SAVE_EMAIL = "save_email";
    public static final String ACTION_SET_LAST_STEP = "set_last_step";
    public static final String ACTION_SUBMIT = "submit";
    public static final String ACTION_CREATE_VISIT = "create_visit";
    public static final String ACTION_SEND_MAIL = "send_mail";
    public static final String ACTION_ERROR = "error";
    public static final String ACTION_FINISH = "finish";
    public static final String ACTION_PATIENT_ORDERS = "patient_orders";
    public static final String ACTION_ENTRY = "entry";
    public static final String ACTION_CANCEL_VISIT = "cancel";
    public static final String SOAP_DOCTOR_APP = "https://uslugi.mosreg.ru/zdrav/doctor_appointment/";

    public static final String SOAP_DOCTOR_APP_REST = "https://uslugi.mosreg.ru/zdrav/doctor_appointment_rest/";

    public static final String ACTION_GET_CITY_LIST = "city_list";
    public static final String ACTION_GET_LPU_LIST = "lpu_list";
    public static final String ACTION_GET_SPEC_LIST = "lpu";
    public static final String ACTION_GET_DOCT_LIST = "doctors_list";
    public static final String ACTION_GET_DOCT_LIST_NEW = "api/doctors";
    public static final String ACTION_GET_TALON_LIST = "doctor/";


    public static final String HTTP_REQ_POST = "POST";
    public static final String HTTP_REQ_GET = "GET";

    public static final String RESULT_KEY_SUCCESS = "success";
    public static final String RESULT_KEY_DESC = "description";
    public static final String RESULT_KEY_RESPONSE = "response";


    public static final String RESULT_ARRLST_RESULT_CODE = "resultCode";
    public static final String RESULT_ARRLST_RESULT_TIME_STR = "resultTimeStr";
    public static final String RESULT_ARRLST_RESULT_DATE_STR = "resultDateStr";
    public static final String RESULT_ARRLST_RESULT_TIME_ID = "resultTimeId";
    public static final String RESULT_ARRLST_RESULT_DOCPOST_ID = "resultDocPostId";
    public static final String RESULT_ARRLST_RESULT_DOCTOR_ID = "resultDoctorId";
    public static final String RESULT_ARRLST_RESULT_DAYSCHEDUL_ID = "resultDaySchedulId";

    public static final String RESULT_ARRLST_NAME = "name";
    public static final String RESULT_ARRLST_VALUME = "volume";
    public static final String RESULT_ARRLST_AVALIBLE= "avalible";
    public static final String RESULT_ARRLST_AVALIBLE_CNT= "avalible_cnt";
    public static final String RESULT_ARRLST_DATE= "date";
    public static final String RESULT_ARRLST_IS_NEED_HTTP= "is_need_http";
    public static final String RESULT_ARRLST_BUSY_DAY= "busyDay";


    public static final String json_tag_items = "items";
    public static final String json_tag_timeItems = "timeItems";

    public static String getActionCode(String action)
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

    public static String getActionDesc(String action)
    {
        switch (action){
            case Actions.ACTION_SAVE_EMAIL:
                return getActionCode(action)+"Подтверждение выбранных данных";//": Подтверждение email-адреса";
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

}
