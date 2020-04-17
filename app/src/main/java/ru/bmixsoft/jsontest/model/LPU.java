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
public class LPU {
            @Unique
            private String ID;
            private String LPUCODE;
            private String EMAIL;
            private String SITEURL;
            private String NAME;
            private String IP;
            private String ADDRESS;
            private String PHONE;
            private Integer ACCESSIBILITY;
            private String CHILDREN;
            private String CITY;
            private Integer isWaitingList;
            private Integer isCallDocHome;
            private String latitude;
            private String longitude;

        public LPU(){

        }

    public LPU( String ID, String LPUCODE, String EMAIL, String SITEURL, String NAME, String IP, String ADDRESS, String PHONE, Integer ACCESSIBILITY, String CHILDREN, String CITY, Integer isWaitingList, Integer isCallDocHome, String latitude, String longitude) {
        this.longitude = longitude;
        this.ID = ID;
        this.LPUCODE = LPUCODE;
        this.EMAIL = EMAIL;
        this.SITEURL = SITEURL;
        this.NAME = NAME;
        this.IP = IP;
        this.ADDRESS = ADDRESS;
        this.PHONE = PHONE;
        this.ACCESSIBILITY = ACCESSIBILITY;
        this.CHILDREN = CHILDREN;
        this.CITY = CITY;
        this.isWaitingList = isWaitingList;
        this.isCallDocHome = isCallDocHome;
        this.latitude = latitude;
    }


    @Override
    public String toString() {
        return "LPU{" +
                "ID=" + ID + "\n" +
                ", LPUCODE=" + LPUCODE  + "\n" +
                ", EMAIL='" + EMAIL + '\''  + "\n" +
                ", SITEURL='" + SITEURL + '\''  + "\n" +
                ", NAME='" + NAME + '\''  + "\n" +
                ", IP='" + IP + '\''  + "\n" +
                ", ADDRESS='" + ADDRESS + '\''  + "\n" +
                ", PHONE='" + PHONE + '\''  + "\n" +
                ", ACCESSIBILITY=" + ACCESSIBILITY  + "\n" +
                ", CHILDREN=" + CHILDREN  + "\n" +
                ", CITY=" + CITY  + "\n" +
                ", isWaitingList=" + isWaitingList  + "\n" +
                ", isCallDocHome=" + isCallDocHome  + "\n" +
                ", latitude='" + latitude + '\''  + "\n" +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    public static LPU get4LpuCode(String LPUCODE)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(LPU.class);
        List<Object> ls = db.query("LPUCODE=?", new String[]{LPUCODE}, LPU.class, null);
        if (ls.size() > 0) {
            LPU l = new LPU();
            l = (LPU) ls.get(0);
            return l;
        }
        return null;
    }

    public static LPU get(String id)
    {
        DBHelper db = DBFactory.getInstance().getDBHelper(LPU.class);
        List<Object> ls = db.query("ID=?", new String[]{id}, LPU.class, null);
        if (ls.size() > 0) {
            LPU l = new LPU();
            l = (LPU) ls.get(0);
            return l;
        }
        return null;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setLPUCODE(String LPUCODE) {
        this.LPUCODE = LPUCODE;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public void setSITEURL(String SITEURL) {
        this.SITEURL = SITEURL;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public void setACCESSIBILITY(Integer ACCESSIBILITY) {
        this.ACCESSIBILITY = ACCESSIBILITY;
    }

    public void setCHILDREN(String CHILDREN) {
        this.CHILDREN = CHILDREN;
    }

    public void setCITY(String CITY) {
        this.CITY = CITY;
    }

    public void setIsWaitingList(Integer isWaitingList) {
        this.isWaitingList = isWaitingList;
    }

    public void setIsCallDocHome(Integer isCallDocHome) {
        this.isCallDocHome = isCallDocHome;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getID() {
        return ID;
    }

    public String getLPUCODE() {
        return LPUCODE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public String getSITEURL() {
        return SITEURL;
    }

    public String getNAME() {
        return NAME;
    }

    public String getIP() {
        return IP;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public String getPHONE() {
        return PHONE;
    }

    public Integer getACCESSIBILITY() {
        return ACCESSIBILITY;
    }

    public String getCHILDREN() {
        return CHILDREN;
    }

    public String getCITY() {
        return CITY;
    }

    public Integer getIsWaitingList() {
        return isWaitingList;
    }

    public Integer getIsCallDocHome() {
        return isCallDocHome;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
