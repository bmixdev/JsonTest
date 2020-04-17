package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 29.11.2017.
 */
@Database(version = UpdateText.curDbVersion)
public class FavoritesDoct {

    @Unique
    private String Id;
    private String CityId;
    private String LpuId;
    private String SpecId;
    private String DoctId;
    private String PolisId;
    private String  Desc;
    private Integer IsNeedChkJob;

    public FavoritesDoct(){}

    public Integer getIsNeedChkJob() {
        return IsNeedChkJob;
    }

    public void setIsNeedChkJob(Integer isNeedChkJob) {
        IsNeedChkJob = isNeedChkJob;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDoctId() {
        return DoctId;
    }

    public void setDoctId(String doctId) {
        DoctId = doctId;
    }

    public String getPolisId() {
        return PolisId;
    }

    public void setPolisId(String polisId) {
        PolisId = polisId;
    }

    public String getDesc() {
        return Desc;
    }

    public String getDoctFio(){
        Doctor doct = Doctor.get(DoctId);
        return doct.getFio();
    }

    public String getShortDoctFio(){
        Doctor doct = Doctor.get(DoctId);
        return doct.getShortFio();
    }

    public String getInfo()
    {
            return this.getDesc().toString();
    }

    public void setDesc(String desc) {
        Desc = desc;
    }


    public static FavoritesDoct get(String id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(FavoritesDoct.class);
        List<Object> ls = db.query("Id=?", new String[]{id}, FavoritesDoct.class, null);
        if (ls.size() > 0) {
            FavoritesDoct r = new FavoritesDoct();
            r = (FavoritesDoct) ls.get(0);
            return r;
        }
        return null;
    }


    public void appendDB(boolean isNeedIncId)
    {
        if (isNeedIncId){
            DBHelper db = DBFactory.getInstance().getDBHelper(FavoritesDoct.class);
            Long maxId = db.getMaxId(FavoritesDoct.class) + 1;
            this.Id = String.valueOf(maxId);
        }
        saveToDataBase();
    }

    public void saveToDataBase()
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(FavoritesDoct.class);
        db.insertOrReplace(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FavoritesDoct that = (FavoritesDoct) o;

        return Id.equals(that.Id);

    }

    @Override
    public int hashCode() {
        return Id.hashCode();
    }

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    public String getLpuId() {
        return LpuId;
    }

    public void setLpuId(String lpuId) {
        LpuId = lpuId;
    }

    public String getSpecId() {
        return SpecId;
    }

    public void setSpecId(String specId) {
        SpecId = specId;
    }

    @Override
    public String toString() {
        return "FavoritesDoct{" +
                "Id=" + Id +
                ", CityId=" + CityId +
                ", LpuId=" + LpuId +
                ", SpecId=" + SpecId +
                ", DoctId=" + DoctId +
                ", PolisId=" + PolisId +
                ", Desc='" + Desc + '\'' +
                '}';
    }
}
