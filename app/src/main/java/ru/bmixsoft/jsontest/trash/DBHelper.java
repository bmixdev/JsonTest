package ru.bmixsoft.jsontest.trash;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TBL_CITY = "city";

    public static final String DATABASE_NAME = "jsonTestDB";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public class CityRec implements BaseColumns {
        public static final String fldId = "id";
        public static final String fldName = "c_name";
        public static final String fldOkato = "c_okato";
        public static final String fldCntClinics = "c_cnt_clinics";
    }

    private static final String SQL_CREATE_TABLE_CITY = "create table " + TBL_CITY +" ("
            + CityRec.fldId + " integer primary key," // autoincrement
            + CityRec.fldName + " text,"
            + CityRec.fldOkato + " text,"
            + CityRec.fldCntClinics + " integer"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_TABLE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CITY);
        this.onCreate(db);
    }

}