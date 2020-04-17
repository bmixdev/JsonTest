package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.ID;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 16.02.2017.
 */
@Database(version = UpdateText.curDbVersion)
public class LpuLinkPolis {
    @ID
    private int ID;
    private String POLIS_ID;
    private String LPU_ID;

    public LpuLinkPolis() {
    }

    public LpuLinkPolis(int ID, String POLIS_ID, String LPU_ID) {
        this.ID = ID;
        this.POLIS_ID = POLIS_ID;
        this.LPU_ID = LPU_ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPOLIS_ID() {
        return POLIS_ID;
    }

    public void setPOLIS_ID(String POLIS_ID) {
        this.POLIS_ID = POLIS_ID;
    }

    public String getLPU_ID() {
        return LPU_ID;
    }

    public void setLPU_ID(String LPU_ID) {
        this.LPU_ID = LPU_ID;
    }

    @Override
    public String toString() {
        return "LpuLinkPolis{" +
                "ID=" + ID +
                ", POLIS_ID=" + POLIS_ID +
                ", LPU_ID=" + LPU_ID +
                '}';
    }
}
