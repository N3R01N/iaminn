package com.testrunns.geotagging;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GeoTagOpenHelper extends SQLiteOpenHelper{
	
	private static final String TAG = "GeoTagOpenHelper";
	
	public static final String DATABASE_NAME = "geotagdatabase.db";
	
	public static final int DATABASE_VERSION = 2;
	
	public GeoTagOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		GeoTagTable.onCreate(database);	
		Log.d(TAG,"on Create");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		GeoTagTable.onUpgrade(database, oldVersion, newVersion);
		Log.d(TAG,"on Upgrade || from:"+oldVersion+" to: "+newVersion);
	}

}
