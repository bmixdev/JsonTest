package net.qiushao.lib.dbhelper;

import android.content.Context;

import net.qiushao.lib.dbhelper.annotation.Database;

import java.util.concurrent.ConcurrentHashMap;

public class DBFactory {
	private static DBFactory instance;
	private ConcurrentHashMap<String, DBHelper> map;
	private Context context;
	private String dbName;

	public static DBFactory getInstance()
	{
		return  instance;
	}

	public static DBFactory getInstance(Context context) {
		if (instance == null) {
			synchronized (DBFactory.class) {
				if (instance == null) {
					instance = new DBFactory(context);
				}
			}
		}
		return instance;
	}

	private DBFactory(Context context) {
		this.context = context;//.getApplicationContext();
		map = new ConcurrentHashMap<String, DBHelper>();
		dbName = context.getPackageName().replaceAll("\\.", "_")  + ".db";
	}

	public synchronized DBHelper getDBHelper(Class<?> claz) {
        //claz.getName().replaceAll("\\.", "_");
		String dbClassTable = dbName + ":"+claz.getName().toString();
		int version = 1;
		Database database = claz.getAnnotation(Database.class);
		if (database != null) {
			version = database.version();
		}

		if (map.containsKey(dbClassTable)) {
			DBHelper db =  map.get(dbClassTable);
			if(! claz.getName().equals(db.getClaz().getName())) db.changeClass(claz, dbName, version);
				return db;
        }

		DBHelper db = new DBHelper(context, claz, dbName, version);
		map.put(dbClassTable, db);
		return db;
	}

	public synchronized void dropDataBase()
	{
		context.deleteDatabase(dbName);
	}
}
