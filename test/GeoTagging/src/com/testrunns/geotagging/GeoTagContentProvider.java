package com.testrunns.geotagging;

import java.util.Arrays;
import java.util.HashSet;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class GeoTagContentProvider extends ContentProvider {
	
	private static final String TAG = "GeoTagContentProvider";
	
	/** The geotag database. */
	private GeoTagOpenHelper database;

	/** Values for the URIMatcher. */
	private static final int GEOTAG_ID = 1;
	private static final int GEOTAG_TYPE = 2;

	/** The authority for this content provider. */
	private static final String AUTHORITY = "com.testrunns.geotagging.contentprovider";

	/**
	 * The database table to read from and write to, and also the root path for
	 * use in the URI matcher. This is essentially a label to a two-dimensional
	 * array in the database filled with rows of jokes whose columns contain
	 * joke data.
	 */
	private static final String BASE_PATH = "geotag_table";

	/**
	 * This provider's content location. Used by accessing applications to
	 * interact with this provider.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	/**
	 * Matches content URIs requested by accessing applications with possible
	 * expected content URI formats to take specific actions in this provider.
	 */
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/geotag/#", GEOTAG_ID);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/type/#", GEOTAG_TYPE);
	}
	
	@Override
	public boolean onCreate() {
		database = new GeoTagOpenHelper(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		Log.d(TAG,"query: "+uri);
		
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		
		checkColumns(projection);
		
		builder.setTables(GeoTagTable.DATABASE_TABLE_GEOTAG);
		
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		case GEOTAG_TYPE:
			
			String type = uri.getLastPathSegment();
			
			if(!type.equals(AddGeoTagActivity.SHOW_ALL)){
				builder.appendWhere(GeoTagTable.GEOTAG_KEY_TYPE + "=" + type);
			}
			else{
				selection = null;
			}
			
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = builder.query(db, projection, selection, null, null, null, null);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG,"delete: "+uri);
		
		SQLiteDatabase db = database.getWritableDatabase();
		
		int count = 0;
		
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		case GEOTAG_ID:
			String deleteId = uri.getLastPathSegment();
			count = db.delete(GeoTagTable.DATABASE_TABLE_GEOTAG, GeoTagTable.GEOTAG_KEY_ID + "=" + deleteId, null);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		if(count > 0) getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG,"inserting: "+uri);
		
		SQLiteDatabase db = database.getWritableDatabase();
		
		long id = 0;
		
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		case GEOTAG_ID:
			id = db.insert(GeoTagTable.DATABASE_TABLE_GEOTAG, null, values);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return Uri.parse(BASE_PATH+"/"+id);
	}

	

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Log.d(TAG,"update: "+uri);
		
		SQLiteDatabase db = database.getWritableDatabase();
		
		int count = 0;
		
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		case GEOTAG_ID:
			String updateId = uri.getLastPathSegment();
			count = db.update(GeoTagTable.DATABASE_TABLE_GEOTAG, values, GeoTagTable.GEOTAG_KEY_ID + "=" + updateId, null);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		if(count > 0) getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	/**
	 * Verifies the correct set of columns to return data from when performing a
	 * query.
	 * 
	 * @param projection
	 *            The set of columns about to be queried.
	 */
	private void checkColumns(String[] projection) {
		String[] available = { GeoTagTable.GEOTAG_KEY_ID, GeoTagTable.GEOTAG_KEY_TEXT,
				GeoTagTable.GEOTAG_KEY_LONG, GeoTagTable.GEOTAG_KEY_LAT, GeoTagTable.GEOTAG_KEY_TYPE,
				GeoTagTable.GEOTAG_KEY_PICPATH, GeoTagTable.GEOTAG_KEY_TIME, GeoTagTable.GEOTAG_KEY_EXTERNKEY};

		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));

			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

}
