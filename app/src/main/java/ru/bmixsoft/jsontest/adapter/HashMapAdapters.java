package ru.bmixsoft.jsontest.adapter;

import android.database.Cursor;
import android.util.Log;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;

import java.util.ArrayList;
import java.util.HashMap;

import ru.bmixsoft.jsontest.model.LpuLinkPolis;
import ru.bmixsoft.jsontest.model.Polis;
import ru.bmixsoft.jsontest.multiview.data.DataProvider;

/**
 * Created by Михаил on 17.02.2017.
 */
public class HashMapAdapters {
    public static final String TAG_CITY_ID="cityId";
    public static final String TAG_LPU_ID="lpuId";


    public static ArrayList<HashMap<String, Object>> getLpuLnkPolis(Polis polis)
    {
        final String tagMethod = "getLpuLnkPolis";
        ArrayList<HashMap<String, Object>> res = new ArrayList<HashMap<String, Object>>();
        res.clear();
        if (polis != null) {
                   /*
        HashMap<String, Object> mapGroup = new HashMap<String, Object>();
        //  map.put(TAG_RESULT_CODE, c.getString("LPUCODE"));
        mapGroup.put(MyExpeListAdapter.TAG_NAME, "Список доступных поликлиник");
        mapGroup.put(MyExpeListAdapter.TAG_VALUME, "");
        mapGroup.put(DataProvider.KEY_PARENT_NODE, null);
        mapGroup.put(DataProvider.KEY_PARENT_GROUP, "1");

        res.add(mapGroup);
        */

            DBHelper mDBHelper = DBFactory.getInstance().getDBHelper(LpuLinkPolis.class);

            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("select l.NAME as lpuName, l.ADDRESS as addr, l.PHONE as phone, l.ID as lpu_id, cc.fldID as city_id ");
            sqlQuery.append("from LPU as l ");
            sqlQuery.append("inner join LpuLinkPolis as lnk on l.ID = lnk.LPU_ID ");
            sqlQuery.append("inner join city as cc on l.CITY = cc.fldID ");
            sqlQuery.append("where lnk.POLIS_ID = ? ");
            sqlQuery.append("order by l.NAME");
            Cursor cursor = mDBHelper.rawQuery(sqlQuery.toString(), new String[]{polis.getId()});
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, Object> mapItem = new HashMap<String, Object>();

                        String itemStr1 = new String(cursor.getString(cursor.getColumnIndex("lpuName".toUpperCase())));
                        String itemStr2 = new String("Адрес: " + cursor.getString(cursor.getColumnIndex("addr".toUpperCase())) + "\n" + "Телефон: " + cursor.getString(cursor.getColumnIndex("phone".toUpperCase())));
                        mapItem.put(MyExpeListAdapter.TAG_NAME, itemStr1);
                        mapItem.put(MyExpeListAdapter.TAG_VALUME, itemStr2);
                        mapItem.put(DataProvider.KEY_PARENT_NODE, null);//mapGroup);
                        mapItem.put(DataProvider.KEY_PARENT_GROUP, "0");
                        mapItem.put(HashMapAdapters.TAG_CITY_ID, cursor.getString(cursor.getColumnIndex("city_id".toUpperCase())));
                        mapItem.put(HashMapAdapters.TAG_LPU_ID, cursor.getString(cursor.getColumnIndex("lpu_id".toUpperCase())));

                        res.add(mapItem);

                    } while (cursor.moveToNext());
                }
            } else Log.d(tagMethod, "Курсор пустой!");
        /*
        ArrayList<LpuLinkPolis> arrayList = (ArrayList<LpuLinkPolis>) mDBHelper.getArrayList("POLIS_ID=?",new String[]{polis.getId().toString()}, LpuLinkPolis.class, "");

        int idx = 0;
        for (LpuLinkPolis i: arrayList) {
            LPU lpu = LPU.get(i.getLPU_ID());
            HashMap<String, Object> mapItem = new HashMap<String, Object>();

            String itemStr1 = new String(lpu.getNAME());
            String itemStr2 = new String("Адрес: "+lpu.getADDRESS() +"\n" +"Телефон: " + lpu.getPHONE());
            idx++;
            mapItem.put(MyExpeListAdapter.TAG_NAME, "#"+idx+" "+itemStr1);
            mapItem.put(MyExpeListAdapter.TAG_VALUME, itemStr2);
            mapItem.put(DataProvider.KEY_PARENT_NODE, null);//mapGroup);
            mapItem.put(DataProvider.KEY_PARENT_GROUP, "0");

            res.add(mapItem);
        }
        */

            return res;
        }
        else
        {
            return null;
        }
    }
}
