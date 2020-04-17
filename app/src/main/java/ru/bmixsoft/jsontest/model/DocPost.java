package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 17.10.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class DocPost {
    @Unique
    private String Id;
    private String Doctor;
    private String Uchastok;
    private String Room;
    private String Post;
    private String Sepatation;

    public DocPost() {
    }

    public DocPost(String id, String doctor, String uchastok, String room, String post, String sepatation) {
        Id = id;
        Doctor = doctor;
        Uchastok = uchastok;
        Room = room;
        Post = post;
        Sepatation = sepatation;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDoctor() {
        return Doctor;
    }

    public void setDoctor(String doctor) {
        Doctor = doctor;
    }

    public String getUchastok() {
        return Uchastok;
    }

    public void setUchastok(String uchastok) {
        Uchastok = uchastok;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getPost() {
        return Post;
    }

    public void setPost(String post) {
        Post = post;
    }

    public String getSepatation() {
        return Sepatation;
    }

    public void setSepatation(String sepatation) {
        Sepatation = sepatation;
    }

    public String getDescription()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(!Sepatation.isEmpty() ? Sepatation : "").append(" ");
        sb.append(!Uchastok.isEmpty() ? "Участок " + Uchastok : "").append(" ");
        sb.append(!Room.isEmpty() ? "Кабинет " + Room : "").append(" ");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "DocPost{" +
                "Id=" + Id +
                ", Doctor=" + Doctor +
                ", Uchastok='" + Uchastok + '\'' +
                ", Room='" + Room + '\'' +
                ", Post='" + Post + '\'' +
                ", Sepatation='" + Sepatation + '\'' +
                '}';
    }
}
