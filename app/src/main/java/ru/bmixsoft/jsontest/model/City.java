package ru.bmixsoft.jsontest.model;

import net.qiushao.lib.dbhelper.DBFactory;
import net.qiushao.lib.dbhelper.DBHelper;
import net.qiushao.lib.dbhelper.annotation.Database;
import net.qiushao.lib.dbhelper.annotation.Unique;

import java.util.List;

import ru.bmixsoft.jsontest.utils.UpdateText;

@Database(version = UpdateText.curDbVersion)
public class City
{
	 //@ID
	@Unique
	 private String fldID;
	 private String  fldNAME;
	 private String  fldOKATO;
	 private Integer fldcount;

	public City(){}

	@Override
	public String toString() {
		return "city(" + fldID + ", " + fldNAME + ", " + fldOKATO + ", " + fldcount +")";
	}

	 public City(String id, String name, String okato, Integer count ){
		this.fldID = id;
		this.fldNAME = name;
		this.fldOKATO = okato;
		this.fldcount = count;
	 }

	 public void setFldcount(Integer fldcount)
	 {
		 this.fldcount = fldcount;
	 }

	 public Integer getFldcount()
	 {
		 return fldcount;
	 }

	 public void setFldOKATO(String fldOKATO)
	 {
		 this.fldOKATO = fldOKATO;
	 }

	 public String getFldOKATO()
	 {
		 return fldOKATO;
	 }

	 public void setFldNAME(String fldNAME)
	 {
		 this.fldNAME = fldNAME;
	 }

	 public String getFldNAME()
	 {
		 return fldNAME;
	 }

	 public void setFldID(String fldID)
	 {
		 this.fldID = fldID;
	 }

	 public String getFldID()
	 {
		 return fldID;
	 }

	public static City get(String id)
	{
		DBHelper db = DBFactory.getInstance().getDBHelper(City.class);
		List<Object> ls = db.query("fldID=?", new String[]{id}, City.class, null);
		if (ls.size() > 0) {
			City r = new City();
			r = (City) ls.get(0);
			return r;
		}
		return null;
	}
	 
	 
}
