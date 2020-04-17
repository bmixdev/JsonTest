package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.AddParentColumns;
import net.qiushao.lib.dbhelper.annotation.Database;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 08.02.2017.
 */

@Database(version = UpdateText.curDbVersion)
@AddParentColumns(isNeedAdd = true)
public class CurTalon  extends Talon{


    public CurTalon() {
    }

    public CurTalon(String id, String city, String lpu, String spec, String doctor, String daySchedule, String timeSchedule, String timeStr, String dateStr, String docPost, String polisId, String stubNum, String dVTId, String roomNum, String pRVSName) {
        super(id, city, lpu, spec, doctor, daySchedule, timeSchedule, timeStr, dateStr, docPost, polisId, stubNum, dVTId, roomNum, pRVSName);
    }

    public static CurTalon get(String id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(CurTalon.class);
        List<Object> ls = db.query("Id=?", new String[]{"1"}, CurTalon.class, null);
        if (ls.size() > 0) {
            CurTalon r = new CurTalon();
            r = (CurTalon) ls.get(0);
            return r;
        }
        return null;
    }

    public void saveToDataBase()
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(CurTalon.class);
        db.insertOrReplace(this);
    }

    @Override
    public String toString() {
        return "CurTalon{} "+super.toString();
    }
}
