package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Mike on 01.03.2018.
 */

@Database(version = UpdateText.curDbVersion)
public class LastSelectedLpu {
    @Unique
    private Integer ID;
    private String lpuId;
    private Integer cntSelected;


    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getLpuId() {
        return lpuId;
    }

    public void setLpuId(String lpuId) {
        this.lpuId = lpuId;
    }

    public Integer getCntSelected() {
        return cntSelected;
    }

    public void setCntSelected(Integer cntSelected) {
        this.cntSelected = cntSelected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LastSelectedLpu that = (LastSelectedLpu) o;

        if (ID != null ? !ID.equals(that.ID) : that.ID != null) return false;
        if (lpuId != null ? !lpuId.equals(that.lpuId) : that.lpuId != null) return false;
        return cntSelected != null ? cntSelected.equals(that.cntSelected) : that.cntSelected == null;
    }

    @Override
    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = 31 * result + (lpuId != null ? lpuId.hashCode() : 0);
        result = 31 * result + (cntSelected != null ? cntSelected.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LastSelectedLpu{" +
                "ID=" + ID +
                ", lpuId=" + lpuId +
                ", cntSelected=" + cntSelected +
                '}';
    }


    public static LastSelectedLpu get(int id) {
        DBHelper db = DBFactory.getInstance().getDBHelper(LastSelectedLpu.class);
        List<Object> ls = db.query("ID=?", new String[]{String.valueOf(id)}, LastSelectedLpu.class, null);
        if (ls.size() > 0) {
            LastSelectedLpu r = new LastSelectedLpu();
            r = (LastSelectedLpu) ls.get(0);
            return r;
        }
        return null;
    }


    public static LastSelectedLpu getForLpuId(String lpuId) {
        DBHelper db = DBFactory.getInstance().getDBHelper(LastSelectedLpu.class);
        List<Object> ls = db.query("lpuId=?", new String[]{lpuId}, LastSelectedLpu.class, null);
        if (ls.size() > 0) {
            LastSelectedLpu r = new LastSelectedLpu();
            r = (LastSelectedLpu) ls.get(0);
            return r;
        }
        return null;
    }

    public void saveToDataBase() {
        DBHelper db = DBFactory.getInstance().getDBHelper(LastSelectedLpu.class);
        db.insertOrReplace(this);

    }

    public void incCntSelected()
    {
        this.cntSelected = this.cntSelected + 1;
    }


    public void appendDB(boolean isNeedIncId)
    {
        if (isNeedIncId){
            DBHelper db = DBFactory.getInstance().getDBHelper(LastSelectedLpu.class);
            Long maxId = db.getMaxId(LastSelectedLpu.class) + 1;
            this.ID = maxId.intValue();
        }
        saveToDataBase();
    }

}