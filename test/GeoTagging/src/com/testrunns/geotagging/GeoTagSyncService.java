package com.testrunns.geotagging;

import java.util.List;

import com.testrunns.geotagging.GetXMLTask.SyncListener;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class GeoTagSyncService extends IntentService implements SyncListener {

	public static final String URL = "http://wi-gate.technikum-wien.at:60660/marker/getMarker";

	private final IBinder mBinder = new LocalBinder();
	private NewGeoTagsListener mListener = null;

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
		Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI + "/sync/" + 4);
		String[] projection = { GeoTagTable.GEOTAG_KEY_TIME };
		Cursor c = getContentResolver()
				.query(uri, projection, null, null, null);

		if (c != null && c.moveToFirst()) {
			c.moveToLast();
			String time = c.getString(0);
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
	}

	@Override
	public void syncHasResult(List<GeoTag> tags) {
		Log.e("Service", "sync had results:" + tags.size());
		for (GeoTag gtag : tags) {
			if(gtag != null){
				DownloadImageTask dlit = new DownloadImageTask();
				dlit.addNameValuePair("test", gtag.getPicpath());
			}
		}
		if(mListener != null) mListener.addNewGeoTagsFromSync(tags);
		else Log.e("Service","listener = null");
	}

	@Override
	public void syncHasNoResult() {
		Log.e("Service", "sync had no result!");
	}
	
	public void setNewGeoTagsListener(NewGeoTagsListener listener){
		Log.e("Service", "setting listener!");
		mListener = listener;
	}
	
	public static interface NewGeoTagsListener{
		void addNewGeoTagsFromSync(List<GeoTag> tags);
	}

}
