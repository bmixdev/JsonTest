package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 17.10.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class DaySchedul {
   @Unique
    public String Id;
    private String Date;
    private Integer HourFrom;
    private Integer HourTo;
    private Integer MinuteFrom;
    private Integer MinuteTo;
    private Integer TicketCount;
    private Integer FlagAccess;
    private String OutReason;

    public DaySchedul() {
    }

    public DaySchedul(String id, String date, Integer hourFrom, Integer hourTo, Integer minuteFrom, Integer minuteTo, Integer ticketCount, Integer flagAccess, String outReason) {
        Id = id;
        Date = date;
        HourFrom = hourFrom;
        HourTo = hourTo;
        MinuteFrom = minuteFrom;
        MinuteTo = minuteTo;
        TicketCount = ticketCount;
        FlagAccess = flagAccess;
        OutReason = outReason;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Integer getHourFrom() {
        return HourFrom;
    }

    public void setHourFrom(Integer hourFrom) {
        HourFrom = hourFrom;
    }

    public Integer getHourTo() {
        return HourTo;
    }

    public void setHourTo(Integer hourTo) {
        HourTo = hourTo;
    }

    public Integer getMinuteFrom() {
        return MinuteFrom;
    }

    public void setMinuteFrom(Integer minuteFrom) {
        MinuteFrom = minuteFrom;
    }

    public Integer getMinuteTo() {
        return MinuteTo;
    }

    public void setMinuteTo(Integer minuteTo) {
        MinuteTo = minuteTo;
    }

    public Integer getTicketCount() {
        return TicketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        TicketCount = ticketCount;
    }

    public Integer getFlagAccess() {
        return FlagAccess;
    }

    public void setFlagAccess(Integer flagAccess) {
        FlagAccess = flagAccess;
    }

    public String getOutReason() {
        return OutReason;
    }

    public void setOutReason(String outReason) {
        OutReason = outReason;
    }

    @Override
    public String toString() {
        return "DaySchedul{" +
                "Id=" + Id +
                ", Date='" + Date + '\'' +
                ", HourFrom=" + HourFrom +
                ", HourTo=" + HourTo +
                ", MinuteFrom=" + MinuteFrom +
                ", MinuteTo=" + MinuteTo +
                ", TicketCount=" + TicketCount +
                ", FlagAccess=" + FlagAccess +
                ", OutReason='" + OutReason + '\'' +
                '}';
    }
}
