package ru.bmixsoft.jsontest.fragment.options;

import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.ID;

import ru.bmixsoft.jsontest.utils.UpdateText;

/**
 * Created by Михаил on 17.11.2016.
 */
@Database(version = UpdateText.curDbVersion)
public class Option {

    @ID
    private Integer Id;
    private String Code;
    private String Name;
    private String Value;
    private String TypeValue;
    private Integer IsVisible;


    public Option() {}

    public Option(Integer id, String code, String name, String value, String typeValue, Integer isVisible) {
        Id = id;
        Code = code;
        Name = name;
        Value = value;
        TypeValue = typeValue;
        IsVisible = isVisible;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
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

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getTypeValue() {
        return TypeValue;
    }

    public void setTypeValue(String typeValue) {
        TypeValue = typeValue;
    }

    public Integer getIsVisible() {
        return IsVisible;
    }

    public void setIsVisible(Integer isVisible) {
        IsVisible = isVisible;
    }

    @Override
    public String toString() {
        return "Option{" +
                "Id=" + Id +
                ", Code=" + Code +
                ", Name='" + Name + '\'' +
                ", Value='" + Value + '\'' +
                ", TypeValue='" + TypeValue + '\'' +
                ", IsVisible='" + IsVisible + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (Id != null ? !Id.equals(option.Id) : option.Id != null) return false;
        return Id != null ? Id.equals(option.Id) : option.Id == null;
    }

    @Override
    public int hashCode() {
        int result = 31 * (Id != null ? Id.hashCode() : 0);
        return result;
    }
}
