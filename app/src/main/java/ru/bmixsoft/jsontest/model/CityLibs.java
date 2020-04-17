package ru.bmixsoft.jsontest.model;
import java.util.*;
import android.content.*;

public class CityLibs
{
	private ArrayList<City> mCitys;
	
	private static CityLibs sCityLibs;
	private Context mAppContext;
	private CityLibs(Context appContext) {
		mAppContext = appContext;
		mCitys = new ArrayList<City>();
	}
	public static CityLibs get(Context c) {
		return null;
	}
	
	public ArrayList<City> getCitys() {
		return mCitys;
	}
	public City getCitys(Integer id) {
		for (City c : mCitys) {
			if (c.getFldID().equals(id))
				return c;
		}
		return null;
	}
	
}
