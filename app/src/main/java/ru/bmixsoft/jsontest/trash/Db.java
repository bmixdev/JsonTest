package ru.bmixsoft.jsontest.trash;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ru.bmixsoft.jsontest.model.City;

public class Db {

    private static final String LOG_TAG = "my_tag";
    DBHelper dbHelper;
    Context context;
    Cursor cursor;
    SQLiteDatabase db;
    List<City> mCityList;

    public Db(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }
    // возвращает количество записей в таблице
    public int getCntCity() {

        db = dbHelper.getReadableDatabase();

        cursor = db.query(DBHelper.TBL_CITY, null, null, null, null, null, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    public boolean existCity(String fldId)
    {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.
                rawQuery("select 1 from "+DBHelper.TBL_CITY+" where "+DBHelper.CityRec.fldId+" = ?", new String[] { fldId });
        if (!cursor.moveToFirst()) return false; else return true;
    }

    /*
    // метод для обновления email
    public void updateEmail(String name, String newEmail){
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.KEY_EMAIL, "newemail@newemail.com");
        String[] args = new String[]{name};
        db.update(DbHelper.TABLE_NAME, cv, "name = ?", args);
    }
    */
    public void updateCity(String fldId, String fldName)
    {
        db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.CityRec.fldName, fldName);

        String[] args = new String[]{fldId};
        db.update(DBHelper.TBL_CITY, cv, DBHelper.CityRec.fldId+" = ?",args);

    }

    public void insertCity(String fldId, String fldName, String fldOkato, int fldCntClinics)
    {
        if (! existCity(fldId)) {
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.CityRec.fldId, fldId);
            cv.put(DBHelper.CityRec.fldName, fldName);
            cv.put(DBHelper.CityRec.fldOkato, fldOkato);
            cv.put(DBHelper.CityRec.fldCntClinics, fldCntClinics);
            db.insert(DBHelper.TBL_CITY, null, cv);
        }
    }

    // метод для удаления строки по id
    public void deleteCity(int id) {
        db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TBL_CITY, DBHelper.CityRec.fldId + "=" + id, null);
    }

    // метод возвращающий коллекцию всех данных
    public List<City> getFriends() {
        cursor = db.query(DBHelper.TBL_CITY, null, null, null, null, null, null);
        mCityList = new ArrayList<City>();

        if (cursor.moveToFirst()) {

            int fldId = cursor.getColumnIndex(DBHelper.CityRec.fldId);
            int fldName = cursor.getColumnIndex(DBHelper.CityRec.fldName);
            int fldOkato = cursor.getColumnIndex(DBHelper.CityRec.fldOkato);
            int fldCntClinics = cursor.getColumnIndex(DBHelper.CityRec.fldCntClinics);
            do {
                City friend = new City(cursor.getString(fldId),
                        cursor.getString(fldName), cursor.getString(fldOkato), cursor.getInt(fldCntClinics));
                mCityList.add(friend);
            } while (cursor.moveToNext());

        } else {
            Log.d(LOG_TAG, "В базе нет данных!");
        }

        cursor.close();
        return mCityList;
    }
    // здесь закрываем все соединения с базой и класс-помощник
    public void close() {
        dbHelper.close();
        db.close();
    }

}