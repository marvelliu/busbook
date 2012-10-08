package com.marvel.busbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class Global {
	
	private Context context = null;
	private Resources resources = null;

	public void setResources(Resources resources) {
		this.resources = resources;
	}
	private static Global global = null;
	final String TAG = "SearchBus";
	
	public  String buslinepath = "//sdcard//busbook//";
	public String buslinefile = "beijing.txt";
	public final String encoding = "gbk";
	
	public boolean sortResult = true;
	
	private Global(){
	}
	
	public static Global getInstance()
	{
		if(global == null)
			global = new Global();
		return global;		
	}
	
	public void InitPref()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		buslinefile = prefs.getString("lstViewDataFiles", "beijing.txt");
		sortResult = prefs.getBoolean("chkSortResult", true);
    	BusInfoData.getInstance().ensureLoaded(this.resources,false);
	}
	

	public void setContext(Context context) {
		this.context = context;
	}
	public Context getContext() {
		return this.context;
	}

}
