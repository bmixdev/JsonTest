package ru.bmixsoft.jsontest.tableview.model;

/**
 * Created by evrencoskun on 11/06/2017.
 */

public class Cell {

    private String m_strId;
    private Object mObjectData;
    private String mShowData;
    private int mRow;
    private int mCol;

    public Cell(String p_strId) {
        this.m_strId = p_strId;
        this.mObjectData = new Object();
        this.mShowData = "";
        this.mRow = 0;
        this.mCol = 0;
    }

    public Cell(String p_strId, Object p_strData, String pShowData, int row, int col) {
        this.m_strId = p_strId;
        this.mObjectData = p_strData;
        this.mShowData = pShowData;
        this.mRow = row;
        this.mCol = col;
    }

    public String getId() {
        return m_strId;
    }

    public Object getData() {
        return mObjectData;
    }

    public void setData(Object pObjectData) {
        mObjectData = pObjectData;
    }

    public String getShowData() {
        return mShowData;
    }

    public void setShowData(String pShowData) {
        mShowData = pShowData;
    }


    public void setRow(int row) {
        this.mRow = row;
    }

    public int getRow() {
        return this.mRow;
    }

    public void setCol(int col) {
        this.mCol = col;
    }

    public int getCol() {
        return this.mCol;
    }

}

