package com.testrunns.geotagging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import com.testrunns.geotagging.AddImageTask.SyncWithServerListener;
import com.testrunns.geotagging.DownloadImageTask.PicSyncListener;
import com.testrunns.geotagging.GetXMLTask.SyncListener;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class GeoTagSyncService extends IntentService implements SyncListener,
		PicSyncListener, SyncWithServerListener {

	public static final String URL = "http://wi-gate.technikum-wien.at:60660/marker/getMarker";

	public GeoTagSyncService() {
		super("GeoTagSyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("Service", "service started!");

		String newUrl = URL;
		Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI + "/syncserver/"
				+ 4);
		String[] projection = { GeoTagTable.GEOTAG_KEY_TIME };
		Cursor c = getContentResolver()
				.query(uri, projection, null, null, null);

		if (c != null && c.moveToFirst()) {
			String time = c.getString(0);
			Log.e("GeoTagSyncService", "last geoTag from Server time: " + time);
			time = time.replaceAll("\\s+", "%20");
			newUrl += "?date=" + time;
		}
		Log.e("Service", "trying to get Sync!\n" + newUrl);
		GetXMLTask syncTask = new GetXMLTask();
		syncTask.setSyncListener(this);
		syncTask.execute(newUrl);
	}

	public void onDestroy() {
		Log.e("SyncTask", "onDestroy");
	}

	@Override
	public void syncHasResult(List<GeoTag> tags) {
		Log.e("Service", "sync had results:" + tags.size());

		addNewGeoTagsFromSync(tags);

		for (GeoTag gtag : tags) {
			if (gtag != null) {
				DownloadImageTask dlit = new DownloadImageTask();
				dlit.setSyncListener(this);
				Log.e("External key", "----" + gtag.getExternalKey());
				dlit.addNameValuePair("test", gtag.getExternalKey());
				dlit.execute();
			}
		}

		syncAllUserGeoTags();
	}

	private void syncAllUserGeoTags() {
		int CURSOR_ID_POS = 0;
		int CURSOR_NAME_POS = 1;
		int CURSOR_LAT_POS = 2;
		int CURSOR_LON_POS = 3;
		int CURSOR_TYPE_POS = 4;		
		int CURSOR_TEXT_POS = 5;
		int CURSOR_PICPATH_POS = 6;

		Log.i("syncAllUserGeoTags", "in der Methode");
		Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI + "/sync/" + 3);
		String[] projection = { GeoTagTable.GEOTAG_KEY_ID, GeoTagTable.GEOTAG_KEY_NAME,
				GeoTagTable.GEOTAG_KEY_LONG, GeoTagTable.GEOTAG_KEY_LAT,
				GeoTagTable.GEOTAG_KEY_TYPE, GeoTagTable.GEOTAG_KEY_TEXT, GeoTagTable.GEOTAG_KEY_PICPATH };
		Cursor c = getContentResolver()
				.query(uri, projection, null, null, null);
		if (c != null && c.moveToFirst()) {
			AddImageTask uploadTask = new AddImageTask();
			do {
				GeoTag g = null;

				try {
					g = new GeoTag();
				} catch (ParseException e) {
					Log.e("ViewMapActivity",
							"failed to create geoTag\n" + e.getMessage());
				}
				
				g.setId(c.getInt(CURSOR_ID_POS));
				g.setName(c.getString(CURSOR_NAME_POS));
				g.setLatitude(c.getDouble(CURSOR_LAT_POS));
				g.setLongitude(c.getDouble(CURSOR_LON_POS));
				g.setType(c.getInt(CURSOR_TYPE_POS));
				g.setText(c.getString(CURSOR_TEXT_POS));
				g.setPicpath(c.getString(CURSOR_PICPATH_POS));

				Log.i("syncAllUserGeoTags", "" + g);
				uploadTask.setListener(this);
				uploadTask.execute(g);
			} while (c.moveToNext());
		} else {
			Log.i("syncAllUserGeoTags", "no new user tags found!");
		}
	}

	// -------------------------------------------

	public void addNewGeoTagsFromSync(List<GeoTag> tags) {
		if (tags != null && tags.size() > 0) {
			for (GeoTag tag : tags) {
				Log.e("Main Add Tag", "tag:" + tag.getExternalKey());
				Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI
						+ "/geotag/" + tag.getId());
				ContentValues cv = new ContentValues();
				cv.put(GeoTagTable.GEOTAG_KEY_NAME, tag.getName());
				cv.put(GeoTagTable.GEOTAG_KEY_LAT, tag.getLatitude());
				cv.put(GeoTagTable.GEOTAG_KEY_LONG, tag.getLongitude());
				cv.put(GeoTagTable.GEOTAG_KEY_TYPE, tag.getType());
				cv.put(GeoTagTable.GEOTAG_KEY_TIME, tag.getTime());
				cv.put(GeoTagTable.GEOTAG_KEY_PICPATH, tag.getPicpath());
				cv.put(GeoTagTable.GEOTAG_KEY_EXTERNKEY, tag.getExternalKey());
				cv.put(GeoTagTable.GEOTAG_KEY_TEXT, tag.getText());

				Log.i("MainActivity", "values: " + cv);

				Uri idUri = getContentResolver().insert(uri, cv);

				int id = Integer.parseInt(idUri.getLastPathSegment());
				Log.d("Main Add Tag", "inserting returns id: " + id);
				tag.setId(id);
			}
		} else
			Log.w("test", "bin da aber im else");
	}

	@Override
	public void syncHasNoResult() {
		Log.e("Service", "sync had no result!");
		syncAllUserGeoTags();
	}

	@Override
	public void PicSyncHasResult(Bitmap bitmap, String externalId) {
		Log.e("Service", "adding pic:" + externalId);
		File newPic = null;
		try {
			newPic = new FileCreator().createImageFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (newPic != null) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(newPic);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI
						+ "/geotag/" + externalId);
				ContentValues cv = new ContentValues();
				cv.put(GeoTagTable.GEOTAG_KEY_PICPATH, newPic.getAbsolutePath());

				int count = getContentResolver().update(uri, cv, null, null);
				Log.i("addPic", "count: " + count);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void PicSyncHasNoResult() {
		Log.e("Service", "pic sync had no result!");
	}

	@Override
	public void serverSyncedWithResult(int externalId, int id, String time) {
		Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI
				+ "/geotag/" + id);
		ContentValues cv = new ContentValues();
		cv.put(GeoTagTable.GEOTAG_KEY_TIME, externalId);
		cv.put(GeoTagTable.GEOTAG_KEY_EXTERNKEY, time);
		
		int count = getContentResolver().update(uri, cv, null, null);
		Log.d("Service","count: "+count);		
	}
}
