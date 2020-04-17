package net.qiushao.lib.dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import java.util.HashMap;

import ru.bmixsoft.jsontest.utils.Utils;

/**
 * Created by shaoqiu on 15-8-29.
 */
enum DBType {
    INTEGER {
        @Override
        public String getName() {
            return "INTEGER";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_INTEGER;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindLong(index, ((Number) Utils.nvl(object, 0)).longValue());
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return cursor.getInt(index);
        }

        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return cursor.getInt(cursor.getColumnIndex(fldName));
        }
    },

    BYTE {
        @Override
        public String getName() {
            return "INTEGER";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_INTEGER;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindLong(index, ((Number)Utils.nvl(object,0)).longValue());
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return ((Number)cursor.getInt(index)).byteValue();
        }

        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return ((Number)cursor.getInt(cursor.getColumnIndex(fldName))).byteValue();
        }
    },

    SHORT {
        @Override
        public String getName() {
            return "INTEGER";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_INTEGER;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindLong(index, ((Number)Utils.nvl(object,0)).longValue());
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            short value = ((Number)cursor.getInt(index)).shortValue();
            return value;
        }


        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return ((Number)cursor.getInt(cursor.getColumnIndex(fldName))).shortValue();
        }
    },

    CHAR {
        @Override
        public String getName() {
            return "INTEGER";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_INTEGER;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            char c = ((Character)Utils.nvl(object,"null")).charValue();
            statement.bindLong(index, (int)c);
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return (char)cursor.getInt(index);
        }

        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return (char)cursor.getInt(cursor.getColumnIndex(fldName));
        }
    },

    LONG {
        @Override
        public String getName() {
            return "INTEGER";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_INTEGER;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindLong(index, ((Number)Utils.nvl(object,0)).longValue());
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return cursor.getLong(index);
        }
        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return cursor.getLong(cursor.getColumnIndex(fldName));
        }
    },

    BOOLEAN {
        @Override
        public String getName() {
            return "INTEGER";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_INTEGER;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            if((Boolean) Utils.nvl(object, false)) {
                statement.bindLong(index, 1);
            } else {
                statement.bindLong(index, 0);
            }
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            int b = cursor.getInt(index);
            return b == 1;
        }

        @Override
        public Object getValue(Cursor cursor, String fldName) {
            int b = cursor.getInt(cursor.getColumnIndex(fldName));
            return b == 1;
        }
    },

    TEXT {
        @Override
        public String getName() {
            return "TEXT";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_STRING;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindString(index, String.valueOf(Utils.nvl(object,"null")));
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return cursor.getString(index);
        }
        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return cursor.getString(cursor.getColumnIndex(fldName));
        }


    },

    FLOAT {
        @Override
        public String getName() {
            return "REAL";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_FLOAT;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindDouble(index, ((Number) Utils.nvl(object,0.0)).doubleValue());
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return cursor.getFloat(index);
        }

        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return cursor.getFloat(cursor.getColumnIndex(fldName));
        }
    },

    DOUBLE {
        @Override
        public String getName() {
            return "REAL";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_FLOAT;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindDouble(index, ((Number)Utils.nvl(object,0.0)).doubleValue());
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return cursor.getDouble(index);
        }

        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return cursor.getDouble(cursor.getColumnIndex(fldName));
        }
    },

    BLOB {
        @Override
        public String getName() {
            return "BLOB";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_BLOB;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindBlob(index, (byte[])object);
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return cursor.getBlob(index);
        }

        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return cursor.getBlob(cursor.getColumnIndex(fldName));
        }
    },

    NULL {
        @Override
        public String getName() {
            return "NULL";
        }

        @Override
        public int getType() {
            return Cursor.FIELD_TYPE_NULL;
        }

        @Override
        public void bindArg(SQLiteStatement statement, int index, Object object) {
            statement.bindNull(index);
        }

        @Override
        public Object getValue(Cursor cursor, int index) {
            return null;
        }
        @Override
        public Object getValue(Cursor cursor, String fldName) {
            return null;
        }
    };

    public abstract String getName();
    public abstract int getType();
    public abstract void bindArg(SQLiteStatement statement, int index, Object object);
    public abstract Object getValue(Cursor cursor, int index);

    public abstract Object getValue(Cursor cursor, String fldName);


    private static HashMap<Class<?>, DBType> map;
    static {
        map = new HashMap<Class<?>, DBType>();

        map.put(byte.class, BYTE);
        map.put(Byte.class, BYTE);

        map.put(char.class, CHAR);
        map.put(Character.class, CHAR);

        map.put(short.class, SHORT);
        map.put(Short.class, SHORT);

        map.put(int.class, INTEGER);
        map.put(Integer.class, INTEGER);

        map.put(long.class, LONG);
        map.put(Long.class, LONG);

        map.put(String.class, TEXT);

        map.put(float.class, FLOAT);
        map.put(Float.class, FLOAT);

        map.put(double.class, DOUBLE);
        map.put(Double.class, DOUBLE);

        map.put(boolean.class, BOOLEAN);
        map.put(Boolean.class, BOOLEAN);
    }

    public static boolean isSupportType(Class<?> claz) {
        return true;
    }

    public static DBType getDBType(Class<?> claz) {
        return map.get(claz);
    }

}
