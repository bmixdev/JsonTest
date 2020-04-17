package ru.bmixsoft.jsontest.fragment.whatnew;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Mike on 15.02.2018.
 */

@Database(version = UpdateText.curDbVersion)
public class WhatNew {
    @Unique
    private Integer id;
    private Integer versionCode;
    private String text;

    public WhatNew()
    {

    }

    public WhatNew(Integer versionCode, String text)
    {
        this.versionCode = versionCode;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhatNew whatNew = (WhatNew) o;

        if (id != null ? !id.equals(whatNew.id) : whatNew.id != null) return false;
        return versionCode != null ? versionCode.equals(whatNew.versionCode) : whatNew.versionCode == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (versionCode != null ? versionCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WhatNew{" +
                "id=" + id +
                ", versionCode='" + versionCode + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
