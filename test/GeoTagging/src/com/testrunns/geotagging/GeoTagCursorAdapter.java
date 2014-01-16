package com.testrunns.geotagging;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

public class GeoTagCursorAdapter {

	public GeoTagCursorAdapter(Context context, Cursor c, int flags) {
	}
	
	private GeoTag getGeoTagFromCursor(Cursor cursor){
		GeoTag theGeoTag = null;
		if (cursor != null){
			theGeoTag = new GeoTag();
			theGeoTag.setId(cursor.getInt(GeoTagTable.GEOTAG_COL_ID));
			theGeoTag.setName(cursor.getString(GeoTagTable.GEOTAG_COL_NAME));
			theGeoTag.setLongitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LONG));
			theGeoTag.setLatitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LAT));
			theGeoTag.setPicpath(cursor.getString(GeoTagTable.GEOTAG_COL_PICPATH));
			theGeoTag.setTime(cursor.getString(GeoTagTable.GEOTAG_COL_TIME));
			theGeoTag.setType(cursor.getInt(GeoTagTable.GEOTAG_COL_TYPE));
			
		}
		return theGeoTag;
	}

}
