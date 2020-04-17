package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Mike on 14.02.2018.
 */

@Database(version = UpdateText.curDbVersion)
public class TalonInCalendar {
    @Unique
    private Integer id;
    private String talonId;
    private String  talonNum;
    private Integer eventId;

    public TalonInCalendar() {

    }

    public TalonInCalendar(String talonId, String talonNum, Integer eventId) {
        this.talonId = talonId;
        this.eventId = eventId;
        this.talonNum = talonNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTalonId() {
        return talonId;
    }

    public void setTalonId(String talonId) {
        this.talonId = talonId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TalonInCalendar that = (TalonInCalendar) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (talonId != null ? !talonId.equals(that.talonId) : that.talonId != null) return false;
        if (talonNum != null ? !talonNum.equals(that.talonNum) : that.talonNum != null)
            return false;
        return eventId != null ? eventId.equals(that.eventId) : that.eventId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (talonId != null ? talonId.hashCode() : 0);
        result = 31 * result + (talonNum != null ? talonNum.hashCode() : 0);
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        return result;
    }

    public static TalonInCalendar get(int id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(TalonInCalendar.class);
        List<Object> ls = db.query("id=?", new String[]{String.valueOf(id)}, TalonInCalendar.class, null);
        if (ls.size() > 0) {
            TalonInCalendar obj = (TalonInCalendar) ls.get(0);
            return obj;
        }
        return null;
    }

    @Override
    public String toString() {
        return "TalonInCalendar{" +
                "id=" + id +
                ", talonId=" + talonId +
                ", talonNum='" + talonNum + '\'' +
                ", eventId=" + eventId +
                '}';
    }

    public static TalonInCalendar getByTalonId(String talonId)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(TalonInCalendar.class);
        List<Object> ls = db.query("talonId=?", new String[]{talonId}, TalonInCalendar.class, null);
        if (ls.size() > 0) {
            TalonInCalendar obj = (TalonInCalendar) ls.get(0);
            return obj;
        }
        return null;
    }

    public static TalonInCalendar getByTalonNum(String talonNum)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(TalonInCalendar.class);
        List<Object> ls = db.query("talonNum=?", new String[]{talonNum}, TalonInCalendar.class, null);
        if (ls.size() > 0) {
            TalonInCalendar obj = (TalonInCalendar) ls.get(0);
            return obj;
        }
        return null;
    }


    public void saveToDB()
    {

        DBHelper db = DBFactory.getInstance().getDBHelper(TalonInCalendar.class);
        Long maxId = db.getMaxId(TalonInCalendar.class) + 1;
        this.id = maxId.intValue();
        db.insertOrReplace(this);
    }


}
