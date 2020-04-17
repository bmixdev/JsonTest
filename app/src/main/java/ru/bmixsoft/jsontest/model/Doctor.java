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
public class Doctor {
    @Unique
    private String Id;
    private String Family;
    private String Name;
    private String Patronymic;

    public Doctor() {

    }

    public Doctor(String id, String family, String name, String patronymic) {
        Id = id;
        Family = family;
        Name = name;
        Patronymic = patronymic;
    }

    public static Doctor get(String id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(Doctor.class);
        List<Object> ls = db.query("Id=?", new String[]{id}, Doctor.class, null);
        if (ls.size() > 0) {
            Doctor doctor = new Doctor();
            doctor = (Doctor) ls.get(0);
            return doctor;
        }
        return null;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFamily() {
        return Family;
    }

    public void setFamily(String family) {
        Family = family;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPatronymic() {
        return Patronymic;
    }

    public void setPatronymic(String patronymic) {
        Patronymic = patronymic;
    }

    public String getFio()
    {
        return Family+" "+Name+" "+Patronymic;
    }

    public String getShortFio()
    {
        return Family+" "+Name.substring(0, 1)+". "+Patronymic.substring(0, 1)+".";
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "Id=" + Id +
                ", Family='" + Family + '\'' +
                ", Name='" + Name + '\'' +
                ", Patronymic='" + Patronymic + '\'' +
                '}';
    }
}
