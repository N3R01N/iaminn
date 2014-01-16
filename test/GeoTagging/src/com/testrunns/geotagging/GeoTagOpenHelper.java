package com.testrunns.geotagging;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GeoTagOpenHelper extends SQLiteOpenHelper{
	
	private static final String TAG = "GeoTagOpenHelper";
	
	public static final String DATABASE_NAME = "geotagdatabase.db";
	
	public static final int DATABASE_VERSION = 2;
	
	private SQLiteDatabase database;
	
	public GeoTagOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		GeoTagTable.onCreate(database);	
		this.database = database;
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		GeoTagTable.onUpgrade(database, oldVersion, newVersion);
		
	}
    public List<GeoTag> getGeoTagFromCategory(String cat) { 
        String[] columns = new String[] {GeoTagTable.GEOTAG_KEY_ID,GeoTagTable.GEOTAG_KEY_NAME };//,GeoTagTable.GEOTAG_KEY_LAT,GeoTagTable.GEOTAG_KEY_LONG,GeoTagTable.GEOTAG_KEY_TYPE,GeoTagTable.GEOTAG_KEY_PICPATH,GeoTagTable.GEOTAG_KEY_EXTERNKEY }; 
        GeoTag geoTag;
        List<GeoTag> geoTags = new ArrayList<GeoTag>();
        
       // Cursor c = database.query(GeoTagTable.DATABASE_TABLE_GEOTAG, columns, null, null, null, null, null); 
        /*for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) { 
            if(c.getString(1) != null && c.getString(1) != "null" ) 
            	geoTag = new GeoTag(c.getString(1),c.getDouble(2), c.getDouble(3), c.getInt(4), c.getString(5), c.getString(6));
        } */   
             
        return geoTags; 
    } 
}
