package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 17.10.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class DayScheduleList {
    @Unique
    private String Id;
    private int CollectionId;
    private Integer DaySchedule;

    public DayScheduleList() {
    }

    public DayScheduleList(String id, int collectionId, Integer daySchedule) {
        Id = id;
        DaySchedule = daySchedule;
        CollectionId = collectionId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public Integer getDaySchedule() {
        return DaySchedule;
    }

    public void setDaySchedule(Integer daySchedule) {
        DaySchedule = daySchedule;
    }

    @Override
    public String toString() {
        return "DayScheduleList{" +
                "Id=" + Id +
                ", CollectionId=" + CollectionId +
                ", DaySchedule=" + DaySchedule +
                '}';
    }
}
