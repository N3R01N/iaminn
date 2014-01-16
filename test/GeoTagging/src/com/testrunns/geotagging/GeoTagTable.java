package com.testrunns.geotagging;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GeoTagTable {

	/** GeoTag table in the database. */
	public static final String DATABASE_TABLE_GEOTAG = "geotag_table";

	/** GeoTag table column names and IDs for database access. */
	public static final String GEOTAG_KEY_ID = "_id";
	public static final int GEOTAG_COL_ID = 0;

	public static final String GEOTAG_KEY_NAME = "geotag_name";
	public static final int GEOTAG_COL_NAME = GEOTAG_COL_ID + 1;

	public static final String GEOTAG_KEY_LONG = "geotag_longitude";
	public static final int GEOTAG_COL_LONG = GEOTAG_COL_ID + 2;
	
	public static final String GEOTAG_KEY_LAT = "geotag_latitude";
	public static final int GEOTAG_COL_LAT = GEOTAG_COL_ID + 3;
	
	public static final String GEOTAG_KEY_TYPE = "geotag_type";
	public static final int GEOTAG_COL_TYPE = GEOTAG_COL_ID + 4;
	
	public static final String GEOTAG_KEY_PICPATH = "geotag_picpath";
	public static final int GEOTAG_COL_PICPATH = GEOTAG_COL_ID + 5;
	
	public static final String GEOTAG_KEY_TIME = "geotag_time";
	public static final int GEOTAG_COL_TIME = GEOTAG_COL_ID + 6;
	
	public static final String GEOTAG_KEY_EXTERNKEY = "geotag_externkey";
	public static final int GEOTAG_COL_EXTERNKEY = GEOTAG_COL_ID + 7;
	

	/**
	 * SQLite database creation statement. Auto-increments IDs of inserted
	 * jokes. Joke IDs are set after insertion into the database.
	 */
	public static final String DATABASE_CREATE = "create table "+ DATABASE_TABLE_GEOTAG 
			+ " (" + GEOTAG_KEY_ID+ " integer primary key autoincrement, " 
			+ GEOTAG_KEY_NAME+ " text not null, " 
			+ GEOTAG_KEY_LONG + " double not null, "
			+ GEOTAG_KEY_LAT + " double not null, "
			+ GEOTAG_KEY_TYPE + " text not null, "
			+ GEOTAG_KEY_PICPATH + " text not null, "
			+ GEOTAG_KEY_TIME + " text not null, "
			+ GEOTAG_KEY_EXTERNKEY + " text not null);";

	/**
	 * SQLite database table removal statement. Only used if upgrading database.
	 */
	public static final String DATABASE_DROP = "drop table if exists "
			+ DATABASE_TABLE_GEOTAG;

	/**
	 * Initializes the database.
	 * 
	 * @param database
	 *            The database to initialize.
	 */
	public static void onCreate(SQLiteDatabase database) {
		Log.w("database","creating DB");
		database.execSQL( DATABASE_CREATE );
	}

	/**
	 * Upgrades the database to a new version.
	 * 
	 * @param database
	 *            The database to upgrade.
	 * @param oldVersion
	 *            The old version of the database.
	 * @param newVersion
	 *            The new version of the database.
	 */
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(""+GeoTagTable.class.getName(), "Database is upgraded FROM: "+oldVersion +" - TO: "+newVersion);
		database.execSQL(DATABASE_DROP);
		onCreate(database);
	}
	
}
