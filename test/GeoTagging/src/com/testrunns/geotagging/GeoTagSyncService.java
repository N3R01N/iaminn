package com.testrunns.geotagging;

import java.text.ParseException;
import java.util.List;

import com.testrunns.geotagging.DownloadImageTask.PicSyncListener;
import com.testrunns.geotagging.GetXMLTask.SyncListener;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class GeoTagSyncService extends IntentService implements SyncListener, PicSyncListener {

	public static final String URL = "http://wi-gate.technikum-wien.at:60660/marker/getMarker";

	private final IBinder mBinder = new LocalBinder();
	private NewGeoTagsListener mListener = null;
	private NewPicListener mPicListener = null;

	public class LocalBinder extends Binder {
		GeoTagSyncService getService() {
			return GeoTagSyncService.this;
		}
	}

	public IBinder onBind(Intent i) {
		return mBinder;
	}

	public GeoTagSyncService() {
		super("GeoTagSyncService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("Service", "service started!");

		String newUrl = URL;
		Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI + "/syncserver/" + 4);
		String[] projection = { GeoTagTable.GEOTAG_KEY_TIME };
		Cursor c = getContentResolver()
				.query(uri, projection, null, null, null);

		if (c != null && c.moveToFirst()) {
			String time = c.getString(0);
			Log.e("GeoTagSyncService","last geoTag from Server time: "+time);
			time = time.replaceAll("\\s+", "%20");
			newUrl += "?date=" + time;
		}
		Log.e("Service","trying to get Sync!\n"+newUrl);
		GetXMLTask syncTask = new GetXMLTask();
		syncTask.setSyncListener(this);
		syncTask.execute(newUrl);
	}

	public void onDestroy() {
		Log.e("SyncTask", "onDestroy");
		setNewGeoTagsListener(null);
	}

	@Override
	public void syncHasResult(List<GeoTag> tags) {
		Log.e("Service", "sync had results:" + tags.size());
		if(mListener != null){
			mListener.addNewGeoTagsFromSync(tags);
			
			for (GeoTag gtag : tags) {
				if(gtag != null){
					DownloadImageTask dlit = new DownloadImageTask();
					dlit.setSyncListener(this);
					Log.e("External key","----"+gtag.getExternalKey());
					dlit.addNameValuePair("test", gtag.getExternalKey());
					dlit.execute();
				}
			}
		} 
		else Log.e("Service","listener = null");
		
		syncAllUserGeoTags();
		
	}

	private void syncAllUserGeoTags() {
		Log.i("syncAllUserGeoTags","in der Methode");
		Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI + "/sync/" + 3);
		String[] projection = { GeoTagTable.GEOTAG_KEY_ID,
				GeoTagTable.GEOTAG_KEY_NAME, GeoTagTable.GEOTAG_KEY_LONG,
				GeoTagTable.GEOTAG_KEY_LAT, GeoTagTable.GEOTAG_KEY_TYPE,
				GeoTagTable.GEOTAG_KEY_PICPATH, GeoTagTable.GEOTAG_KEY_TIME,
				GeoTagTable.GEOTAG_KEY_EXTERNKEY };
		Cursor c = getContentResolver().query(uri, projection, null, null, null);
		if(c != null && c.moveToFirst()){
			do {
				GeoTag g = null;

				try {
					g = new GeoTag();
				} catch (ParseException e) {
					Log.e("ViewMapActivity","failed to create geoTag\n"+e.getMessage());
				}

				g.setId(c.getInt(GeoTagTable.GEOTAG_COL_ID));
				g.setName(c.getString(GeoTagTable.GEOTAG_COL_NAME));
				g.setLatitude(c.getDouble(GeoTagTable.GEOTAG_COL_LAT));
				g.setLongitude(c.getDouble(GeoTagTable.GEOTAG_COL_LONG));
				g.setType(c.getInt(GeoTagTable.GEOTAG_COL_TYPE));
				g.setPicpath(c.getString(GeoTagTable.GEOTAG_COL_PICPATH));
				g.setTime(c.getString(GeoTagTable.GEOTAG_COL_TIME));
				g.setExternalKey(c.getString(GeoTagTable.GEOTAG_COL_EXTERNKEY));
				
				Log.i("syncAllUserGeoTags",""+g);
			} while (c.moveToNext());
		}
		else{
			Log.i("syncAllUserGeoTags","no new user tags found!");
		}
	}

	@Override
	public void syncHasNoResult() {
		Log.e("Service", "sync had no result!");
		syncAllUserGeoTags();
	}
	
	public void setNewGeoTagsListener(NewGeoTagsListener listener){
		Log.e("Service", "setting listener!");
		mListener = listener;
	}
	
	public void setNewPicListener(NewPicListener listener){
		Log.e("Service", "setting listener!");
		mPicListener = listener;
	}


	@Override
	public void PicSyncHasResult(Bitmap bitmap, String externalId) {
		Log.e("Service","adding pic:"+externalId);
		if(mPicListener != null) mPicListener.addPicToGeoTag(bitmap, externalId);
	}

	@Override
	public void PicSyncHasNoResult() {
		Log.e("Service", "pic sync had no result!");		
	}
	
	
	public static interface NewGeoTagsListener{
		void addNewGeoTagsFromSync(List<GeoTag> tags);
	}
	
	public static interface NewPicListener{
		void addPicToGeoTag(Bitmap bitmap, String externalId);
	}

}
