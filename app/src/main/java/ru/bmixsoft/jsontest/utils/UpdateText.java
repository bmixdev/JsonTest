package ru.bmixsoft.jsontest.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.qiushao.lib.dbhelper.DBHelper;

import ru.bmixsoft.jsontest.fragment.whatnew.FactoryWhatNew;

/**
 * Created by Михаил on 09.12.2017.
 */

public class UpdateText {

    public static final int curDbVersion = 44;

    public static void updateAppVersion(Context mActivityContext,int mCurVersionCode, int mBuildVersionCode, DBHelper mDBHelper)
    {
/*
        if ( mCurVersionCode <= getVersionBuilder(1,0,0,29))
        {
//        LibOption lo = LibOption.getInstance(mActivityContext);
          lo.addOptions(new Option(8, "onRunService", "Включить сервис", "0", LibOption.typeBoolean, 1));
        }
        */
        FactoryWhatNew.getInstance(mActivityContext, mDBHelper).updateWhatNew(mBuildVersionCode);

        Utils.msgSuccess("Обновление конфигурации до версии " + mBuildVersionCode);
    }

    public static void updateDataBaseVersion(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        /*
       // db.execSQL("DROP TABLE IF EXISTS " + tableName);
        if (newVersion == 4) {
            db.execSQL("ALTER TABLE Talon ADD COLUMN DVTID INTEGER");
            db.execSQL("ALTER TABLE Talon ADD COLUMN RoomNum TEXT");
            db.execSQL("ALTER TABLE Talon ADD COLUMN PRVSName TEXT");
        }
        if (newVersion == 5) {
            db.execSQL("ALTER TABLE CurTalon ADD COLUMN DVTID INTEGER");
            db.execSQL("ALTER TABLE CurTalon ADD COLUMN RoomNum TEXT");
            db.execSQL("ALTER TABLE CurTalon ADD COLUMN PRVSName TEXT");
        }
        if (newVersion == 6)
        {
            db.execSQL("DELETE FROM Talon");
            db.execSQL("DELETE FROM CurTalon");
        }
        if (newVersion == 7)
        {
            db.execSQL("DROP TABLE IF EXISTS Talon");
            db.execSQL("DROP TABLE IF EXISTS CurTalon");
        }
        if (newVersion == 10)
        {
            db.execSQL("DROP TABLE IF EXISTS Talon");
            db.execSQL("DROP TABLE IF EXISTS CurTalon");
        }
        if (newVersion == 12)
        {
            db.execSQL("DROP TABLE IF EXISTS FavoritesDoct");
        }
        if (newVersion == 18)
        {
            db.execSQL("DELETE FROM Option");
        }
        if (newVersion == 19)
        {
            db.execSQL("DROP TABLE IF EXISTS Option");
        }
        if (newVersion == 20)
        {
            db.execSQL("ALTER TABLE Talon ADD COLUMN SERIALVERSIONUID LONG");
        }
        if (newVersion == 24)
        {
            db.execSQL("DELETE FROM CITY");
            db.execSQL("DELETE FROM LPU");
            db.execSQL("DELETE FROM SPECIALITY");
            db.execSQL("DELETE FROM DOCTOR");
            db.execSQL("DELETE FROM TALON");
            db.execSQL("DELETE FROM FAVORITESDOCT");
        }
        if (newVersion == 25)
        {
            db.execSQL("DELETE FROM Option");
        }
        */
        if (newVersion == 37)
        {
            db.execSQL("DELETE FROM OPTION");
        }

        if (newVersion < 45)
        {
            db.execSQL("DROP TABLE IF EXISTS CITY");
            db.execSQL("DROP TABLE IF EXISTS CURTALON");
            db.execSQL("DROP TABLE IF EXISTS DAYSCHEDUL");
            db.execSQL("DROP TABLE IF EXISTS DAYSCHEDULELIST");
            db.execSQL("DROP TABLE IF EXISTS DOCTOR");
            db.execSQL("DROP TABLE IF EXISTS DOCTORLIST");
            db.execSQL("DROP TABLE IF EXISTS DOCPOST");
            db.execSQL("DROP TABLE IF EXISTS TALON");
            db.execSQL("DROP TABLE IF EXISTS FAVORITESDOCT");
            db.execSQL("DROP TABLE IF EXISTS ITEMSPECS");
            db.execSQL("DROP TABLE IF EXISTS LPU");
            db.execSQL("DROP TABLE IF EXISTS LPULINKPOLIS");
            db.execSQL("DROP TABLE IF EXISTS LASTSELECTEDLPU");
            db.execSQL("DROP TABLE IF EXISTS POLIS");
            db.execSQL("DROP TABLE IF EXISTS SPECIALITY");
            db.execSQL("DROP TABLE IF EXISTS TALONINCALENDAR");

         //   db.execSQL("DROP TABLE TIMEITEMS");

        }
    }
}
