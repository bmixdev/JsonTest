package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 22.11.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class Polis {
    @Unique
    private String Id;
    private String LastName;
    private String MiddleName;
    private String FirstName;
    private String PhoneNumber;
    private String Email;
    private String PolusNum;
    private String Birthday;

    public Polis() {
    }

    public Polis(String id, String lastName, String middleName, String firstName, String phoneNumber, String email, String polusNum, String birthday) {
        Id = id;
        LastName = lastName;
        MiddleName = middleName;
        FirstName = firstName;
        PhoneNumber = phoneNumber;
        Email = email;
        PolusNum = polusNum;
        Birthday = birthday;

    }

    public static Polis getPolis(String id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(Polis.class);
        List<Object> ls = db.query("Id=?", new String[]{id}, Polis.class, null);
        if (ls.size() > 0) {
            Polis polis = new Polis();
            polis = (Polis) ls.get(0);
            return polis;
        }
        return null;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPolusNum() {
        return PolusNum;
    }

    public void setPolusNum(String polusNum) {
        PolusNum = polusNum;
    }

    public String getFio()
    {
        return this.LastName + " " +
                this.MiddleName + " " +
                this.FirstName;
    }

    public String getPolisText()
    {
        try {

            return getFio() + "\n" + this.PolusNum;
        }
        catch (Exception e
                )
        {
            return "";
        }
    }

    @Override
    public String toString() {
        return "Polis{" +
                "Id=" + Id +
                ", LastName='" + LastName + '\'' +
                ", MiddleName='" + MiddleName + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Email='" + Email + '\'' +
                ", PolusNum='" + PolusNum + '\'' +
                ", Birthday='" + Birthday + '\'' +
                '}';
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polis polis = (Polis) o;

        return Id == polis.Id;
    }


    @Override
    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (LastName != null ? LastName.hashCode() : 0);
        result = 31 * result + (MiddleName != null ? MiddleName.hashCode() : 0);
        result = 31 * result + (FirstName != null ? FirstName.hashCode() : 0);
        result = 31 * result + (PhoneNumber != null ? PhoneNumber.hashCode() : 0);
        result = 31 * result + (Email != null ? Email.hashCode() : 0);
        result = 31 * result + (PolusNum != null ? PolusNum.hashCode() : 0);
        result = 31 * result + (Birthday != null ? Birthday.hashCode() : 0);
        return result;
    }
}
