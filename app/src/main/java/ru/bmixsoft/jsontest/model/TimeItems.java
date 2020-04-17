package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 18.10.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class TimeItems {
    @Unique
    private String Id;  // PosId
    private String Collection_Id;
    private String Time;
    private Integer BusyFlag;
    private Integer BusyTypeCode;
    private Integer FlagAccess;
    private Integer FlagVisitMaker;

    public TimeItems() {
    }

    public TimeItems(String id, String collection_Id, String time, Integer busyFlag, Integer busyTypeCode, Integer flagAccess, Integer flagVisitMaker) {
        Id = id;
        Collection_Id = collection_Id;
        Time = time;
        BusyFlag = busyFlag;
        BusyTypeCode = busyTypeCode;
        FlagAccess = flagAccess;
        FlagVisitMaker = flagVisitMaker;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCollection_Id() {
        return Collection_Id;
    }

    public void setCollection_Id(String collection_Id) {
        Collection_Id = collection_Id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public Integer getBusyFlag() {
        return BusyFlag;
    }

    public void setBusyFlag(Integer busyFlag) {
        BusyFlag = busyFlag;
    }

    public Integer getBusyTypeCode() {
        return BusyTypeCode;
    }

    public void setBusyTypeCode(Integer busyTypeCode) {
        BusyTypeCode = busyTypeCode;
    }

    public Integer getFlagAccess() {
        return FlagAccess;
    }

    public void setFlagAccess(Integer flagAccess) {
        FlagAccess = flagAccess;
    }

    public Integer getFlagVisitMaker() {
        return FlagVisitMaker;
    }

    public void setFlagVisitMaker(Integer flagVisitMaker) {
        FlagVisitMaker = flagVisitMaker;
    }

    @Override
    public String toString() {
        return "TimeItems{" +
                "Id=" + Id +
                ", Collection_Id=" + Collection_Id +
                ", Time='" + Time + '\'' +
                ", BusyFlag=" + BusyFlag +
                ", BusyTypeCode=" + BusyTypeCode +
                ", FlagAccess=" + FlagAccess +
                ", FlagVisitMaker=" + FlagVisitMaker +
                '}';
    }
}
