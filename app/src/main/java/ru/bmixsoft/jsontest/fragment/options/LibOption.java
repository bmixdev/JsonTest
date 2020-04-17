package ru.bmixsoft.jsontest.fragment.options;

import android.content.Context;
import android.util.Log;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;

import ru.bmixsoft.jsontest.BuildConfig;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Михаил on 17.11.2016.
 */
public class LibOption {

    private static final String TAG = "Option";

    public static final String typeBoolean = "BOOLEAN";
    public static final String typeButton = "BUTTON";
    public static final String typeString = "STRING";
    public static final String typeInt    = "INT";
    public static final String typeTimeInterval    = "TIMEINTERVAL";

    private ArrayList<Option> mOptions;

    private static LibOption instance;
    private Context mAppContext;
    private DBHelper mDBHelper;

    public LibOption(Context appContext) {
        mAppContext = appContext;
        mDBHelper = DBFactory.getInstance(appContext).getDBHelper(Option.class);
        mOptions = (ArrayList<Option>) mDBHelper.getArrayList(Option.class, null);
        instance = this;
    }

    public ArrayList<Option>  fillOptions()
    {
        mOptions = (ArrayList<Option>) mDBHelper.getArrayList(Option.class, null);
        return mOptions;
    }

    public void dropCache()
    {
        instance = null;
        mOptions = null;
        new LibOption(mAppContext);
    }

    public static LibOption getInstance(Context c) {
        if (instance == null) {
            instance = new LibOption(c);
        }
        return instance;
    }

    public static String getOptionValue(Context c, String code) {
        if (instance == null) {
            instance = new LibOption(c);
        }
        Option option = instance.getOption(code);
        return option != null ? option.getValue() : "";
    }

    public static int getOptionValueInt(Context c, String code) {
        if (instance == null) {
            instance = new LibOption(c);
        }
        Option option = instance.getOption(code);
        return option != null ? Integer.valueOf(option.getValue()) : -1;
    }


    public static boolean getOptionValueBool(Context c, String code) {
        if (instance == null) {
            instance = new LibOption(c);
        }
        Option option = instance.getOption(code);
        return option != null ? (option.getValue().equals("1") ? true : false) : false;
    }


    public Option getOption(String code) {
        Option curOption = null;
        for (Option opt : mOptions) {
            String curCode = opt.getCode();
            if (code.equals(curCode)) {
                curOption = opt;
                break;

            }
        }
        return curOption;
    }

    public void setOption(String optionCode, String value) {
        for (Option opt : mOptions) {
            String curCode = opt.getCode();
            if (optionCode.equals(curCode)) {
                opt.setValue(value);
                break;

            }
        }
        refresh();
    }

    public static void setOption(Context c,String optionCode, String value) {

        if (instance == null) {
            instance = new LibOption(c);
        }

        for (Option opt : instance.mOptions) {
            String curCode = opt.getCode();
            if (optionCode.equals(curCode)) {
                opt.setValue(value);
                break;

            }
        }
        instance.refresh();
    }

    public static void setOption(Context c,String optionCode, boolean value) {

        if (instance == null) {
            instance = new LibOption(c);
        }

        for (Option opt : instance.mOptions) {
            String curCode = opt.getCode();
            if (optionCode.equals(curCode)) {
                opt.setValue(value==true?"1":"0");
                break;

            }
        }
        instance.refresh();
    }

    public void setVisible(String optionCode, boolean value) {


        for (Option opt : instance.mOptions) {
            String curCode = opt.getCode();
            if (optionCode.equals(curCode)) {
                opt.setIsVisible(value==true?1:0);
                break;
            }
        }
        instance.refresh();
    }


    public static void setOption(Context c,String optionCode, int value) {

        if (instance == null) {
            instance = new LibOption(c);
        }

        for (Option opt : instance.mOptions) {
            String curCode = opt.getCode();
            if (optionCode.equals(curCode)) {
                opt.setValue(String.valueOf(value));
                break;

            }
        }
        instance.refresh();
    }


    public Option getOptionById(Integer id) {
        Option curOption = null;
        for (Option opt : mOptions) {
            if (opt.getId().equals(id)) {
                curOption = opt;
                break;
            }
        }
        return curOption;
    }

    public void addOptions(Option c) {

        if (! mOptions.contains(c)) {
            mOptions.add(c);
            refresh();
        }
        else
        {
            for (Option o: mOptions) {
                if (o.equals(c)){
                   // o.setIsVisible(c.getIsVisible());
                    break;
                }
            }
        }
    }

    public void drop(){
        mDBHelper.clean(Option.class);
    }

    public ArrayList<Option> getOptions() {
        return mOptions;
    }

    public void deleteOptions(Option c) {
        mOptions.remove(c);
        refresh();
    }


    public void refresh() {
        try {
            mDBHelper.clean(Option.class);
            for (Option opt : mOptions) {
                mDBHelper.insertOrReplace(opt);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving options: " + e);
        }
    }


    public ArrayList<Option> initOptions()
    {
       // drop();
        ArrayList<Option>  resArray = instance.getOptions();

       // if (resArray.size() == 0) {
            instance.addOptions(new Option(0, "onDebugMode", "Включить отладочный режим", "0", LibOption.typeBoolean, 0));

            instance.addOptions(new Option(1, "dropSqlLite", "Очистить базу данных приложения", "Выполнить", LibOption.typeButton, 0));

            instance.addOptions(new Option(2, "colorHeader", "Цветовая схема", String.valueOf(Utils.getColorHeader()), LibOption.typeButton, 1));

            instance.addOptions(new Option(3, "openSqlViewer", "Открыть SQL-viewer", "Открыть", LibOption.typeButton, 0));

            instance.addOptions(new Option(4, "updateApp", "Обновить приложение", "Обновить", LibOption.typeButton, 0));

            instance.addOptions(new Option(5, "curVersionApp", "Текущая версия", BuildConfig.VERSION_NAME, LibOption.typeString, 0));
            instance.addOptions(new Option(6, "curVersionAppCode", "Код текущий версии", String.valueOf(BuildConfig.VERSION_CODE), LibOption.typeString, 0));

            instance.addOptions(new Option(7, "curVersionAppIsUpdate", "Версия обновлена", "False", LibOption.typeString, 0));

            instance.addOptions(new Option(8, "onRunService", "Сервис автоматического поиска талонов по избранным записям", "0", LibOption.typeBoolean, 1));
            instance.addOptions(new Option(9, "onIntervalRun", "Периодичность запуска сервиса (мин.)", "15", LibOption.typeInt, 1));
            instance.addOptions(new Option(10, "avalibleIntervalService", "Разрешенный интервал работы сервиса", "20:00-22:00", LibOption.typeTimeInterval, 1));

            instance.addOptions(new Option(11, "autoSyncTalons", "Автоматическая синхронизация талонов на вкладке \"Мои талона\"", "0", LibOption.typeBoolean, 1));

            instance.addOptions(new Option(12, "checkNewVersionOnHttp", "Получать новую версию приложения с сервера", "0", LibOption.typeBoolean, 0));
               instance.addOptions(new Option(13, "showHelpUserLayout", "Отобразить первоначальный помошник пользователя", "1", LibOption.typeBoolean, 0));
            instance.addOptions(new Option(14, "btnTest", "Тестирование нового функционала", "ОК", LibOption.typeButton, 0));
            instance.addOptions(new Option(15, "btnShowHelpUser", "Помощь при работе с приложением", "Показать", LibOption.typeButton, 0));
            instance.addOptions(new Option(16, "cntRunApp", "Количество запусков приложения", "1", LibOption.typeInt, 0));
            instance.addOptions(new Option(17, "showModeRateDialog", "Режим отображения диалога оценки(0 - не оценили, 1 - позже, 2 - никогда)", "0", LibOption.typeInt, 0));
            instance.addOptions(new Option(18, "shareLinkToApp", "Поделиться ссылкой на приложение", "Отправить", LibOption.typeButton, 0));
        instance.addOptions(new Option(19, "showWhatNew", "Отобразить диалог что нового","1", LibOption.typeInt, 0));
        refresh();
        resArray = instance.getOptions();
       // }
        return  resArray;
    }

}
