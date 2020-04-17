package net.qiushao.lib.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import net.qiushao.lib.dbhelper.annotation.AddParentColumns;
import net.qiushao.lib.dbhelper.annotation.ID;
import net.qiushao.lib.dbhelper.annotation.Primary;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ru.bmixsoft.jsontest.utils.UpdateText;
import ru.bmixsoft.jsontest.utils.Utils;

public class DBHelper extends SQLiteOpenHelper {

    private Class<?> claz;
    private SQLiteDatabase db;
    private SQLiteStatement insertStatement;
    private SQLiteStatement insertOrReplaceStatement;
    private SQLiteStatement insertOrIgnoreStatement;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    public final Lock readLock = readWriteLock.readLock();

    private String dbName;
    private String tableName;
    private int tableVersion;
    private String createTableSql;
    private String insertSql;
    private String insertOrReplaceSql;
    private String insertOrIgnoreSql;
    private LinkedList<ColumnInfo> columns;
    private LinkedList<ColumnInfo> primaryColumns;

    DBHelper(Context context, Class<?> claz, String dbName, int version) {
        super(context, dbName, null, version);
        changeClass(claz, dbName, version);
    }

    public void reopen()
    {
        db.close();
        db = getWritableDatabase();
    }

    public void changeClass(Class<?> claz)
    {
        if (claz.equals(this.claz)) return;
        changeClass(claz,this.dbName, this.tableVersion);
    }

    public void changeClass(Class<?> claz, String dbName, int version)
    {
        this.dbName = dbName;
        this.tableName = claz.getSimpleName().toUpperCase();
        tableVersion = version;
        this.claz = claz;
        initDatabaseInfo();
        db = getWritableDatabase();
        db.execSQL(createTableSql);
        rebuildTableSql();
        try {
            insertStatement = db.compileStatement(insertSql);
            insertOrReplaceStatement = db.compileStatement(insertOrReplaceSql);
            insertOrIgnoreStatement = db.compileStatement(insertOrIgnoreSql);
        }
        catch (SQLiteException e)
        {
            //Utils.d(Utils.errStack(e));
            //e.printStackTrace();
            Utils.safePrintError(e);
            /*
            if (e.getMessage().toUpperCase().indexOf("SERIALVERSIONUID") > 0)
            {
                db.execSQL("ALTER TABLE "+claz.getSimpleName()+" ADD COLUMN SERIALVERSIONUID LONG");
            }
            if (e.getMessage().toUpperCase().indexOf("$CHANGE") > 0)
            {
                db.execSQL("ALTER TABLE "+claz.getSimpleName()+" ADD COLUMN $CHANGE LONG");
            }
            */
        }

    }

    public Class<?> getClaz() {
        return claz;
    }

    public void setClaz(Class<?> claz) {
        this.claz = claz;
    }

    /**
     * 插入一个对象到数据库中
     *
     * @param object 要插入数据库的对象
     */

    public void insert_(Object object) {
        changeClass(object.getClass());
        int res = -1;
        writeLock.lock();
        try {
            bindInsertStatementArgs(insertStatement, this.claz.cast(object));
            insertStatement.executeInsert();

        }catch(Exception e){
            Utils.safePrintError(e); //  e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    public int insert(Object object) {
        changeClass(object.getClass());
        int res = -1;
        writeLock.lock();
        try {
            bindInsertStatementArgs(insertStatement, this.claz.cast(object));
            long lastId = insertStatement.executeInsert();

            try {
                int index = 1;
                for (ColumnInfo column : columns) {
                    if (column.isID) {
                        int id = (int)lastId;
                        column.field.setInt(object, id);
                        res = id;
                    }
                }
            } catch (IllegalAccessException e) {
                Utils.safePrintError(e); // e.printStackTrace();
                throw new RuntimeException(e);
            }

        }catch(Exception e){
            Utils.safePrintError(e); //  e.printStackTrace();
        } finally {
            writeLock.unlock();
            return res;
        }
    }

    /**
     * 如果数据库中已经存在相同的主键了，则更新数据，
     * 否则插入对象到数据库
     *
     * @param object 要插入数据库的对象
     */
    public void insertOrReplace(Object object) {
        changeClass(object.getClass());
        writeLock.lock();
        try {
            bindInsertOrReplaceStatementArgs(insertOrReplaceStatement, this.claz.cast(object));
            insertOrReplaceStatement.execute();
        }catch(Exception e){
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 如果数据库中已经存在相同的主键了，则啥都不干，
     * 否则插入对象到数据库
     *
     * @param object 要插入数据库的对象
     */
    public void insertOrIgnore(Object object) {
        changeClass(object.getClass());
        writeLock.lock();
        try {
            bindInsertOrReplaceStatementArgs(insertOrIgnoreStatement, this.claz.cast(object));
            insertOrIgnoreStatement.execute();
        }catch(Exception e){
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 插入Collection 集合中的所有元素到数据库
     *
     * @param objects
     */
    public void insertAll(Collection<Object> objects) {
        changeClass(objects.getClass());
        writeLock.lock();
        db.beginTransaction();
        try {
            for (Object object : objects) {
                bindInsertStatementArgs(insertStatement, this.claz.cast(object));
                insertStatement.executeInsert();
            }
            db.setTransactionSuccessful();
        }catch(Exception e){
            Utils.safePrintError(e); //  e.printStackTrace();
        } finally {
            db.endTransaction();
            writeLock.unlock();
        }
    }

    /**
     * 删除满足条件的数据
     * 例：db.delete("id=?", new Object[]{"1"})
     *
     * @param whereClause 条件表达式
     * @param whereArgs   条件表达式的参数
     *
     */
    public void delete(String whereClause, Object[] whereArgs, Class<?> claz) {
        changeClass(claz);
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(tableName);
        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" where ");
            sql.append(whereClause);
        }

        writeLock.lock();
        try {
            db.execSQL(sql.toString().toUpperCase(), whereArgs);
        }catch(Exception e){
           // e.printStackTrace();
            Utils.safePrintError(e);
        } finally {
            writeLock.unlock();
        }
    }

    public int deleteCnt(String whereClause, Object[] whereArgs, Class<?> claz)
    {
        delete(whereClause, whereArgs, claz);
        int cnt = size(whereClause,(String[]) whereArgs, claz);
        return cnt;
    }

    public Object getObject(Class<?> claz, int id)
    {
        changeClass(claz);

        String fldIdName = "ID";
        for (ColumnInfo column : columns) {
            if (column.isID || column.isUnique) {
                fldIdName = column.field.getName();
                break;
            }
        }

        List<Object> ls = query(fldIdName+"=?", new String[]{String.valueOf(id)}, claz, null);
        if (ls.size() > 0) {
            Object obj = new Object();
            obj =  ls.get(0);
            return obj;
        }
        return null;
    }

    public Object getObject(Class<?> claz, String id)
    {
        changeClass(claz);

        String fldIdName = "ID";
        for (ColumnInfo column : columns) {
            if (column.isID || column.isUnique) {
                fldIdName = column.field.getName();
                break;
            }
        }

        List<Object> ls = query(fldIdName+"=?", new String[]{id}, claz, null);
        if (ls.size() > 0) {
            Object obj = new Object();
            obj =  ls.get(0);
            return obj;
        }
        return null;
    }


    /**
     * 清空数据库
     */
    public void clean(Class<?> claz) {
        changeClass(claz);
        writeLock.lock();
        try {
            db.execSQL(("delete from " + tableName).toUpperCase());
           // db.execSQL("DELETE FROM sqlite_sequence"); //自增列归零
        }catch(Exception e){
            //e.printStackTrace();
            Utils.safePrintError(e);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int update(ContentValues values, String whereClause, String[] whereArgs, Class<?> claz) {
        changeClass(claz);
        writeLock.lock();
        try {
            return db.update(tableName, values, whereClause, whereArgs);
        }catch(Exception e){
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
        return 0;
    }

    /**
     * 条件查询
     *
     * @param whereClause 条件表达式
     * @param args        条件表达式的参数
     * @return 返回满足条件的对象
     */
    public List<Object> query(String whereClause, String[] args, Class<?> claz, String orderBy) {
        changeClass(claz);
        List<Object> list = new ArrayList<>();
        Cursor cursor = null;
        StringBuilder sql = new StringBuilder();
        sql.append("select * from ");
        sql.append(tableName);
        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" where ");
            sql.append(whereClause);
        }
        if (!TextUtils.isEmpty(orderBy))
        {
            sql.append(" order by ");
            sql.append(orderBy);
        }
        readLock.lock();
        try {
            cursor = db.rawQuery(sql.toString().toUpperCase(), args);
            list = cursorToObjects(cursor);
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            if(null != cursor) {
                cursor.close();
            }
            readLock.unlock();
        }
        return list;
    }

    /**
     * 直接写数据库语句查询，有时候查询的条件比较复杂，比如嵌套查询，
     * 使用query(String whereClause, String[] args)方法不能满足，
     * 则可以使用此方法来查询
     *
     * @param sql  完整的数据库语句
     * @param args 数据库语句中"?"的替换参数
     * @return 满足条件的结果集
     */
    public Cursor rawQuery(String sql, String[] args) {
        readLock.lock();
        try {
            return db.rawQuery(sql.toUpperCase(), args);
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return null;
    }

    public ArrayList<HashMap<Integer, String>> rawQueryHashMap(String sql, String[] args) {
        readLock.lock();
        try {
            Cursor cursor = db.rawQuery(sql.toUpperCase(), args);
            ArrayList<HashMap<Integer, String>> resultList = new ArrayList<>();
            if (cursor != null)
            {
                if (cursor.moveToFirst())
                {
                    do{
                        HashMap<Integer, String> map = new HashMap<Integer, String>();
                        for (int i = 0; i < cursor.getColumnCount(); i++) {
                            map.put(i, cursor.getString(i));
                        }
                        resultList.add(map);
                    } while (cursor.moveToNext());
                }
            }
            return  resultList;
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return null;
    }

    public Cursor rawQueryWithError(String sql, String[] args) throws Exception {
        readLock.lock();
        Exception tmpExp;
        try {
            return db.rawQuery(sql.toUpperCase(), args);
        } catch (Exception e) {
            tmpExp = e;
        } finally {
            readLock.unlock();
        }
        if (tmpExp != null)
            throw tmpExp;
        return null;
    }

    /**
     * 执行不带返回值的数据库语句
     *
     * @param sql 完整的数据库语句
     */
    public void execSQL(String sql) {
        writeLock.lock();
        try {
            db.execSQL(sql);
        } catch (Exception e){
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 执行不带返回值的数据库语句
     *
     * @param sql  完整的数据库语句
     * @param args 数据库语句中"?"的替换参数
     */
    public void execSQL(String sql, Object[] args) {
        writeLock.lock();
        try {
            db.execSQL(sql.toUpperCase(), args);
        }catch(Exception e){
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * @return 数据库表数据量，即有多少个记录
     */

    public long size(Class<?> claz) {
        changeClass(claz);
        Cursor cursor = db.rawQuery(("select count(*) from " + getTableName()).toUpperCase(), null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    public int size(String whereClause, String[] args, Class<?> claz) {
        changeClass(claz);
        long count = 0;
        Cursor cursor = null;
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from ");
        sql.append(tableName);
        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" where ");
            sql.append(whereClause);
        }
        readLock.lock();
        try {
            cursor = db.rawQuery(sql.toString().toUpperCase(), args);
            cursor.moveToFirst();
            count = cursor.getLong(0);
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            if(null != cursor) {
                cursor.close();
            }
            readLock.unlock();
        }
        return (int) count;
    }

    public boolean exists(String whereClause, String[] args, Class<?> claz) {
        changeClass(claz);
        long count = 0;
        Cursor cursor = null;
        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from ");
        sql.append(tableName);
        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" where ");
            sql.append(whereClause);
        }
        readLock.lock();
        try {
            cursor = db.rawQuery(sql.toString().toUpperCase(), args);
            cursor.moveToFirst();
            count = cursor.getLong(0);
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        } finally {
            if(null != cursor) {
                cursor.close();
            }
            readLock.unlock();
        }
        return count > 0 ? true : false;
    }

    /**
     * @return максимальный id
     */
    public long getMaxId(Class<?> claz) {
        changeClass(claz);
        Field[] fields = claz.getFields();
        String mId = "ID";
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()) || !DBType.isSupportType(field.getType()))
                continue;
            field.setAccessible(true);
            ColumnInfo columnInfo = new ColumnInfo(field, field.getName(), DBType.getDBType(field.getType()));

            if (null != field.getAnnotation(ID.class)) {
               mId = columnInfo.name;
            }
        }

        Cursor cursor = db.rawQuery(("select max("+mId+") from " + getTableName()).toUpperCase(), null);
        cursor.moveToFirst();
        long res = cursor.getLong(0);
        cursor.close();
        return res;
    }


    public static String getUID()
    {
        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString();
    }

    public List<Object> cursorToObjects(Cursor cursor) {
        ArrayList<Object> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Object object = newInstance(cursor);
                if (object != null) {
                    list.add(object);
                }
            }
        }
        return list;
    }

    public Object newInstance(Cursor cursor) {
        Object object = null;
        try {
            object = claz.newInstance();
            for (ColumnInfo column : columns) {
                column.field.set(this.claz.cast(object), column.type.getValue(cursor, column.name));
            }
        } catch (InstantiationException e) {
            Utils.safePrintError(e); // e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            Utils.safePrintError(e); // e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            Utils.safePrintError(e); // e.printStackTrace();
            throw new RuntimeException(e);
        }
        return (Object) object;
    }

    private void bindInsertStatementArgs(SQLiteStatement statement, Object object) {
        try {
            int index = 1;
            for (ColumnInfo column : columns) {
                if (column.isID) continue;
                int idx =  index++;
                try {
                    column.type.bindArg(statement, idx, column.field.get(object));
                } catch (NullPointerException e)
                {
                   // column.type.bindArg(statement, index++, "NULL");
                    statement.bindString(idx, "null");
                }
            }
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            Utils.safePrintError(e);
            throw new RuntimeException(e);
        }
    }



    private void bindInsertOrReplaceStatementArgs(SQLiteStatement statement, Object object) {
        try {
            int index = 1;
            for (ColumnInfo column : columns) {
                column.type.bindArg(statement, index++, column.field.get(object));
            }
        } catch (IllegalAccessException e) {
            //e.printStackTrace();
            Utils.safePrintError(e);
            throw new RuntimeException(e);
        }
    }

    private void initDatabaseInfo() {
        collectColumns();
        genCreateTableSql();
        genInsertSql();
    }

    private void rebuildTableSql() {
        Cursor cr = db.rawQuery("pragma table_info(" + tableName + ")".toUpperCase(), null);
        ArrayList<String> lstSqlField = new ArrayList<String>();
        if (cr != null) {
            while (cr.moveToNext()) {
                String fldName = cr.getString(cr.getColumnIndex("name"));
                lstSqlField.add(fldName);
            }
        }


        for (ColumnInfo column : columns) {

//            if (column.name.equals("$CHANGE") || column.name.equals("SERIALVERSIONUID")) continue;

            boolean columnNoExists = true;
            for (String str : lstSqlField) {
                if (str.equals(column.name)) {
                    columnNoExists = false;
                    break;
                }
            }
            if (columnNoExists) {
                db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + column.name + " " + column.type.getName().toUpperCase());
            }
        }
    }

    public static Field[] removeElement(Field[] original, int element){
        Field[] n = new Field[original.length - 1];
        System.arraycopy(original, 0, n, 0, element );
        System.arraycopy(original, element+1, n, element, original.length - element-1);
        return n;
    }

    public static Field[] removeElement(Field[] original, String Name){
        Field[] n = new Field[original.length - 1];
        int element = -1;
        for ( int i = 0; i <= original.length - 1; i++) {
           if ( original[i].getName().toUpperCase().equals(Name) )
           {
               element = i;
               break;
           }
        }
        if (element != -1) {
            System.arraycopy(original, 0, n, 0, element);
            System.arraycopy(original, element + 1, n, element, original.length - element - 1);
            return n;
        } else
        {
            return original;
        }
    }

    private void collectColumns() {
        columns = new LinkedList<ColumnInfo>();
        primaryColumns = new LinkedList<ColumnInfo>();
        Field[] fields = claz.getDeclaredFields();
        Field[] fieldsParent = new Field[100];
        int cntArray = 0;
        boolean isNeedAdd = false;
        cntArray =+ fields.length;


        AddParentColumns addParentColumns = claz.getAnnotation(AddParentColumns.class);
        if (addParentColumns != null) {
            isNeedAdd = addParentColumns.isNeedAdd();
            if (isNeedAdd)
            {
                fieldsParent = claz.getSuperclass().getDeclaredFields();

                fieldsParent = removeElement(fieldsParent, "$CHANGE");
                fieldsParent = removeElement(fieldsParent, "SERIALVERSIONUID");

                cntArray = cntArray + fieldsParent.length;
            }
        }
        Field[] fieldsAll = new Field[cntArray];

        System.arraycopy(fields, 0, fieldsAll, 0, fields.length);
        if (isNeedAdd) System.arraycopy(fieldsParent, 0, fieldsAll, fields.length, fieldsParent.length);

        for (Field field : fieldsAll) {
            if (Modifier.isTransient(field.getModifiers()) || !DBType.isSupportType(field.getType()))
                continue;
            field.setAccessible(true);
            ColumnInfo columnInfo = new ColumnInfo(field, field.getName(), DBType.getDBType(field.getType()));
            if (null != field.getAnnotation(Primary.class)) {
                primaryColumns.add(columnInfo);
            }
            if (null != field.getAnnotation(ID.class)) {
                columnInfo.isID = true;
            }
            if (null != field.getAnnotation(Unique.class)) {
                columnInfo.isUnique = true;
            }
            columns.add(columnInfo);
            Debug.d("add column : " + columnInfo.name);
        }
    }

    private void genCreateTableSql() {
        int index = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableName);
        sql.append("(");

        for (ColumnInfo column : columns) {
            column.index = index++;
            if (column.isID) {
                sql.append(column.name);
                sql.append(" integer primary key autoincrement,");
            } else {
                sql.append(column.name);
                sql.append(" ");
                sql.append(column.type.getName());
                if (column.isUnique) {
                    sql.append(" UNIQUE");
                }
                sql.append(",");
            }
        }

        if (primaryColumns.size() > 0) {
            sql.append("primary key(");
            for (ColumnInfo column : primaryColumns) {
                sql.append(column.name);
                sql.append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append("))");
        } else {
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
        }
        createTableSql = sql.toString().toUpperCase();
    }

    private void genInsertSql() {
        StringBuilder insertSB = new StringBuilder();
        StringBuilder insertOrReplaceSB = new StringBuilder();

        insertSB.append("INSERT INTO ");
        insertSB.append(tableName);
        insertSB.append("(");

        insertOrReplaceSB.append("INSERT OR REPLACE INTO ");
        insertOrReplaceSB.append(tableName);
        insertOrReplaceSB.append("(");

        StringBuilder insertValues = new StringBuilder();
        StringBuilder insertOrReplaceValues = new StringBuilder();
        insertValues.append(" VALUES(");
        insertOrReplaceValues.append(" VALUES(");

        for (ColumnInfo column : columns) {
            if (column.isID) {
                insertOrReplaceSB.append(column.name);
                insertOrReplaceSB.append(",");
                insertOrReplaceValues.append("?,");
                continue;
            };
            insertSB.append(column.name);
            insertSB.append(",");
            insertOrReplaceSB.append(column.name);
            insertOrReplaceSB.append(",");
            insertValues.append("?,");
            insertOrReplaceValues.append("?,");
        }

        insertSB.deleteCharAt(insertSB.length() - 1);
        insertSB.append(")");
        insertOrReplaceSB.deleteCharAt(insertOrReplaceSB.length() - 1);
        insertOrReplaceSB.append(")");

        insertValues.deleteCharAt(insertValues.length() - 1);
        insertValues.append(")");
        insertOrReplaceValues.deleteCharAt(insertOrReplaceValues.length() - 1);
        insertOrReplaceValues.append(")");

        insertSB.append(insertValues);
        insertOrReplaceSB.append(insertOrReplaceValues);

        insertSql = insertSB.toString().toUpperCase();
        insertOrReplaceSql = insertOrReplaceSB.toString().toUpperCase();
        insertOrIgnoreSql = insertOrReplaceSql.replaceFirst("OR REPLACE ", "OR IGNORE ").toUpperCase();

        Debug.d("insert sql = " + insertSql);
        Debug.d("insert or replace sql = " + insertOrReplaceSql);
        Debug.d("insert or ignore sql = " + insertOrIgnoreSql);
    }

    public String getDBName() {
        return dbName;
    }

    public int getDBVersion() {
        return tableVersion;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        UpdateText.updateDataBaseVersion(db, oldVersion, newVersion);
        onCreate(db);
    }

    public void refreshDataOnArrayList(ArrayList<?> arrayList, Class<?> claz, boolean isNeedClean)
    {
        try {


            if ( isNeedClean ) clean(claz);

            for (Object obj : arrayList) {
                insertOrReplace(obj);
            }
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        }

    }

    public ArrayList<?> getArrayList(Class<?> claz, String orderBy)
    {
           try {
               ArrayList<Object> arrayList = new ArrayList<>();
                List<Object> ls = query(null, new String[]{}, claz, orderBy);

                for ( Object obj: ls) {
                    arrayList.add(obj);
            }
               return arrayList;
        } catch (Exception e) {
               Utils.safePrintError(e); // e.printStackTrace();
        }
        return null;
    }

    public ArrayList<?> getArrayList(String sqlQuery)
    {
        try {
            ArrayList<Object> arrayList = new ArrayList<>();
            List<Object> ls = cursorToObjects(rawQuery(sqlQuery, null));

            for ( Object obj: ls) {
                arrayList.add(obj);
            }
            return arrayList;
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        }
        return null;
    }

    /*

    Пример использования
     ArrayList<LpuLinkPolis> arrayList = (ArrayList<LpuLinkPolis>) mDBHelper.getArrayList("POLIS_ID=?",new String[]{polis.getId().toString()}, LpuLinkPolis.class);
     */
    public ArrayList<?> getArrayList(String whereClause, String[] args, Class<?> claz, String orderBy)
    {
        try {
            ArrayList<Object> arrayList = new ArrayList<>();
            List<Object> ls = query(whereClause, args, claz, orderBy);

            for ( Object obj: ls) {
                arrayList.add(obj);
            }
            return arrayList;
        } catch (Exception e) {
            Utils.safePrintError(e); // e.printStackTrace();
        }
        return null;
    }

    //вставит аррэйлист в базу
    public void setArrayList(ArrayList<?> arrayList, Class<?> claz)
    {
        changeClass(claz);
        for (Object obj : arrayList)
        {
            insertOrReplace(obj);
        }
    }

}
