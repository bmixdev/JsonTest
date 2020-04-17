package ru.bmixsoft.jsontest.fragment.whatnew;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;

import ru.bmixsoft.jsontest.fragment.options.LibOption;
import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by Mike on 15.02.2018.
 */

public class FactoryWhatNew {

    private static FactoryWhatNew instance;
    private Context context;
    private DBHelper dbHelper;

    public static FactoryWhatNew getInstance(Context context, DBHelper dbHelper)
    {
        if (instance == null)
        {
            instance = new FactoryWhatNew(context, dbHelper);
        }
        return instance;
    }

    public FactoryWhatNew(Context context, DBHelper dbHelper)
    {
        this.context = context;
        this.dbHelper = dbHelper;
        this.instance = this;
    }

    public void append(int versionCode, String text)
    {
        WhatNew wn = new WhatNew(versionCode, text);
        boolean isExist = dbHelper.exists("versionCode = ?", new String[]{String.valueOf(versionCode)}, WhatNew.class);
        if (!isExist)
            dbHelper.insertOrReplace(wn);
    }

    public ArrayList<WhatNew> getWhatNews()
    {
        try {


            dbHelper = DBFactory.getInstance(context.getApplicationContext()).getDBHelper(WhatNew.class);
            ArrayList<WhatNew> list = (ArrayList<WhatNew>) dbHelper.getArrayList(WhatNew.class, "versionCode desc");
            return list;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getAllNews()
    {
        StringBuilder sb = new StringBuilder();

        try {

            ArrayList<WhatNew> list = getWhatNews();
            if (list != null && list.size() > 0) {
                for (WhatNew wn : list) {
                    sb.append(wn.getText());
                }
            }
        }
        catch (Exception e)
        {
            Utils.showErrorDlg((FragmentActivity) context, "Ошибка получения описания новой версии", Utils.errStack(e));
        }
        return sb.toString();
    }

    public void updateWhatNew(int versionCode)
    {
        String textNew;
        StringBuilder sb = new StringBuilder();
        switch (versionCode){
            case 10117 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.17</H2>")
                        .append("<br> * Добавлена возможность установки напоминания в календарь по зарезервированному талону")
                        .append("<br> * Удаление информации из календаря при отмене талона")
                        .append("<br> * Обновление информации о текущих талонах при помощи свайпа")
                        .append("<br> * Диалог описания новой версии")
                        .append("<br> * Доработки интерфейса")
                        .append("<br> * Исправление ошибок")
                        .append("</BODY></HTML>");
                break;
            }
            case 10118 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.18</H2>")
                        .append("<br> * Добавлена проверка на достуность web-портала")
                        .append("<br> * Доработка интерфейса")
                        .append("<br> *** Добавлена возможность копирования текста на экране \"Подтверждение талона\"")
                        .append("<br> *** Доработан диалог выбора даты рождения")
                        .append("<br> * Исправление ошибок")
                        .append("</BODY></HTML>");
                break;
            }
            case 10120 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.20</H2>")
                        .append("<H3>!!!! Для корректной работы новой версии, информация о ранее заведенных полисах и избранных записях будет очищена. Необходимо еще раз завести нужную Вам информацию. </H3>")
                        .append("<br> * Кардинально переделана работа с web-порталом")
                        .append("<br> * Исправление основной ошибки \"Данные отсутствуют\"")
                        .append("</BODY></HTML>");
                break;
            }
            case 10121 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.21</H2>")
                        .append("<br> * Переделан интерфейс ввода даты рождения")
                        .append("<br> * Исправление ошибки \"Данные отсутствуют\" при нажатие на кнопку \"Оформить новый талон\" на выбранном полисе ")
                        .append("<br> * Исправление мелких ошибок")
                        .append("</BODY></HTML>");
                break;
            }
            case 10126 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.26</H2>")
                        .append("<br> * Исправлена ошибка получения списка поликлиник")
                        .append("<br> * Исправлено отображение времени приема врача ")
                        .append("<br> * Исправление мелких ошибок")
                        .append("</BODY></HTML>");
                break;
            }
            case 10127 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.27</H2>")
                        .append("<br> * Исправлена ошибка работы с Android 9")
                        .append("<br> * Переделана панель навигации ")
                        .append("<br> * Добавлена возможность переноса талона но другое время")
                        .append("</BODY></HTML>");
                break;
            }
            case 10128 : {
                        sb.append("<HTML><BODY><H2>Версия 1.0.1.28</H2>")
                        .append("<br> * Небольшие исправления")
                        .append("<br> * Исправление ошибки с автопоиском талонов к избранным врачам ")
                        .append("</BODY></HTML>");
                break;
            }
            case 10129 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.29</H2>")
                        .append("<br><H3>Исправлена проблема с доступом к web-ресурсу!</H3>")
                        .append("</BODY></HTML>");
                break;
            }
            case 10130 : {
                sb.append("<HTML><BODY><H2>Версия 1.0.1.30</H2>")
                        .append("<br><H3>Исправление ошибок!</H3>")
                        .append("</BODY></HTML>");
                break;
            }
        }
        LibOption.setOption(context, "showWhatNew", true);
        append(versionCode, sb.toString());
    }
}
