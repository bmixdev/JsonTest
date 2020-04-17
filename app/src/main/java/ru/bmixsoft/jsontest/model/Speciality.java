package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 17.10.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class Speciality {
    @Unique
    private String Id;
    private String Code;
    private String Name;

    public Speciality(){}

    @Override
    public String toString() {
        return "lpu(" + Id + ", " + Code + ", " + Name
                +")";
    }

    public Speciality(String id, String code, String name) {
        Id = id;
        Code = code;
        Name = name;
    }

    public static Speciality get(String id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(Speciality.class);
        List<Object> ls = db.query("Id=?", new String[]{id}, Speciality.class, null);
        if (ls.size() > 0) {
            Speciality res = new Speciality();
            res = (Speciality) ls.get(0);
            return res;
        }
        return null;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
