package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 17.10.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class DoctorList {
    @Unique
    private String Id;
    private String DateFrom;
    private String DateTo;
    private Integer DocPost;

    public DoctorList() {
    }

    public DoctorList(String id, String dateFrom, String dateTo, Integer docPost) {
        Id = id;
        DateFrom = dateFrom;
        DateTo = dateTo;
        DocPost = docPost;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDateFrom() {
        return DateFrom;
    }

    public void setDateFrom(String dateFrom) {
        DateFrom = dateFrom;
    }

    public String getDateTo() {
        return DateTo;
    }

    public void setDateTo(String dateTo) {
        DateTo = dateTo;
    }

    public Integer getDocPost() {
        return DocPost;
    }

    public void setDocPost(Integer docPost) {
        DocPost = docPost;
    }

    @Override
    public String toString() {
        return "DoctorList{" +
                "Id=" + Id +
                ", DateFrom=" + DateFrom +
                ", DateTo=" + DateTo +
                ", DocPost=" + DocPost +
                '}';
    }
}
