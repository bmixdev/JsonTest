package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 27.10.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class Talon {
    @Unique
    private String id;
    private String City;
    private String Lpu;
    private String Spec;
    private String Doctor;
    private String DaySchedule;
    private String TimeSchedule;
    private String TimeStr;
    private String DateStr;
    private String DocPost;
    private String PolisId;
    private String DVTID; //идентификтор визита



    private String V_Family; // фамилия
    private String V_Name; // имя
    private String V_Ot; // отчество врача
    private String RoomNum; // номер кабинета
    private String PRVSName; // специальность врача
    private String ShortDate; // дата талона
    private String Time_from; // время талона
    private String StubNum; // номер талона


    public Talon() {
    }

    public Talon(String id, String city, String lpu, String spec, String doctor, String daySchedule, String timeSchedule, String timeStr, String dateStr, String docPost, String polisId, String stubNum, String dVTID, String roomNum, String pRVSName) {
        this.id = id;
        City = city;
        Lpu = lpu;
        Spec = spec;
        Doctor = doctor;
        DaySchedule = daySchedule;
        TimeSchedule = timeSchedule;
        TimeStr = timeStr;
        DateStr = dateStr;
        DocPost = docPost;
        PolisId = polisId;
        StubNum = stubNum;
        DVTID = dVTID;
        RoomNum = roomNum;
        PRVSName = pRVSName;
    }

    public static Talon get(String id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(Talon.class);
        List<Object> ls = db.query("Id=?", new String[]{id}, Talon.class, null);
        if (ls.size() > 0) {
            Talon r = new Talon();
            r = (Talon) ls.get(0);
            return r;
        }
        return null;
    }


    public void saveToDataBase(boolean isNeedIncId)
    {
        if (isNeedIncId){
            DBHelper db = DBFactory.getInstance().getDBHelper(Talon.class);
            Long maxId = db.getMaxId(Talon.class) + 1;
            this.id = String.valueOf(maxId);
        }
        saveToDataBase();
    }

    public void saveToDataBase()
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(Talon.class);
        db.insertOrReplace(this);
    }

    public String getV_Family() {
        return V_Family;
    }

    public void setV_Family(String v_Family) {
        V_Family = v_Family;
    }

    public String getV_Name() {
        return V_Name;
    }

    public void setV_Name(String v_Name) {
        V_Name = v_Name;
    }

    public String getV_Ot() {
        return V_Ot;
    }

    public void setV_Ot(String v_Ot) {
        V_Ot = v_Ot;
    }

    public String getShortDate() {
        return ShortDate;
    }

    public void setShortDate(String shortDate) {
        ShortDate = shortDate;
    }

    public String getTime_from() {
        return Time_from;
    }

    public void setTime_from(String time_from) {
        Time_from = time_from;
    }

    public String getDocPost() {
        return DocPost;
    }

    public void setDocPost(String docPost) {
        DocPost = docPost;
    }

    public String getDateStr() {
        return DateStr;
    }

    public void setDateStr(String dateStr) {
        DateStr = dateStr;
    }

    public String getTimeStr() {

        return TimeStr;
    }

    public void setTimeStr(String timeStr) {
        TimeStr = timeStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLpu() {
        return Lpu;
    }

    public void setLpu(String lpu) {
        Lpu = lpu;
    }

    public String getSpec() {
        return Spec;
    }

    public void setSpec(String spec) {
        Spec = spec;
    }

    public String getDoctor() {
        return Doctor;
    }

    public void setDoctor(String doctor) {
        Doctor = doctor;
    }

    public String getDaySchedule() {
        return DaySchedule;
    }

    public void setDaySchedule(String daySchedule) {
        DaySchedule = daySchedule;
    }

    public String getTimeSchedule() {
        return TimeSchedule;
    }

    public void setTimeSchedule(String timeSchedule) {
        TimeSchedule = timeSchedule;
    }

    public String getPolisId() {
        return PolisId;
    }

    public void setPolisId(String polisId) {
        PolisId = polisId;
    }

    public String getStubNum() {
        return StubNum;
    }

    public void setStubNum(String stubNum) {
        StubNum = stubNum;
    }

    public String getDVTID() {
        return DVTID;
    }

    public void setDVTID(String DVTID) {
        this.DVTID = DVTID;
    }

    public String getRoomNum() {
        return RoomNum;
    }

    public void setRoomNum(String roomNum) {
        RoomNum = roomNum;
    }

    public String getPRVSName() {
        return PRVSName;
    }

    public void setPRVSName(String PRVSName) {
        this.PRVSName = PRVSName;
    }

    @Override
    public String toString() {
        return "Talon{" +
                "id=" + id +
                ", City=" + City +
                ", Lpu=" + Lpu +
                ", Spec='" + Spec +
                ", Doctor=" + Doctor + '\'' +
                ", DaySchedule='" + DaySchedule + '\'' +
                ", TimeSchedule='" + TimeSchedule + '\'' +
                ", TimeStr='" + TimeStr + '\'' +
                ", DateStr='" + DateStr + '\'' +
                ", DocPost='" + DocPost + '\'' +
                ", PolisId=" + PolisId +
                ", DVTID=" + DVTID +
                ", V_Family='" + V_Family + '\'' +
                ", V_Name='" + V_Name + '\'' +
                ", V_Ot='" + V_Ot + '\'' +
                ", RoomNum='" + RoomNum + '\'' +
                ", PRVSName='" + PRVSName + '\'' +
                ", ShortDate='" + ShortDate + '\'' +
                ", Time_from='" + Time_from + '\'' +
                ", StubNum='" + StubNum + '\'' +
                '}';
    }
}
