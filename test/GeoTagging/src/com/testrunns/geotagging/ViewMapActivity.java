package com.testrunns.geotagging;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewMapActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, OnInfoWindowClickListener {
	private GoogleMap map;
	private GetXMLTask XMLservlet;
	public static final String URL = "http://wi-gate.technikum-wien.at:60660/marker/";
    TextView outputText;
    ImageView imageView;
    Button button1;
    GeoTagOpenHelper openHelper;
    List<GeoTag> geoTags;
    private static final int LOADER_ID = 1;
    public void switchActivity(View view){
        Intent intent = new Intent(this, AddGeoTagActivity.class);
        startActivity(intent);
}
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_map);
		outputText = (TextView) findViewById(R.id.textView);
		imageView = (ImageView) findViewById(R.id.imageView);
		button1 = (Button) findViewById(R.id.button1);
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setOnInfoWindowClickListener(this);
		button1.bringToFront();
		if(isNetworkAvailable()){
			XMLservlet = new GetXMLTask();
			XMLservlet.execute(new String[] { URL + "getMarker" }, this);
		}
		imageView.setVisibility(View.INVISIBLE);
		imageView.setOnClickListener(myhandler);
		
	}
	
	public void onStart(){
		super.onStart();
		getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_map, menu);
		return true;
	}
	
	View.OnClickListener myhandler = new View.OnClickListener() {
	    public void onClick(View v) {
	    	if(v != null)
	    		imageView.setVisibility(View.INVISIBLE);
	    }
	};

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		  ImageView bmImage;

		  public DownloadImageTask(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try {
		        InputStream in = new java.net.URL(urldisplay).openStream();
		        mIcon11 = BitmapFactory.decodeStream(in);
		      } catch (Exception e) {
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }

		  protected void onPostExecute(Bitmap result) {
			  imageView.setImageBitmap(result);

		  }
		}
	
	@Override
	public void onInfoWindowClick(Marker marker) {
		HashMap<String, String> data = new HashMap<String, String>();
		//data.put("test", marker.getId());
		//data.put("key2", "value2");
		
		//XMLservlet.execute(new String[] { URL + "DisplayImage" }, this, data);
		
		
		new DownloadImageTask((ImageView) findViewById(R.id.imageView))
	        .execute("http://mypics.at/d/1526-12/Wiese.jpg");
		imageView.setVisibility(View.VISIBLE);
		imageView.bringToFront();
		/*DownloadImageTask dit = new DownloadImageTask(imageView);
			url = new java.net.URL("");
			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			imageView.setImageBitmap(bmp);*/
	}
	
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = {GeoTagTable.GEOTAG_KEY_ID, GeoTagTable.GEOTAG_KEY_NAME,GeoTagTable.GEOTAG_KEY_LONG,GeoTagTable.GEOTAG_KEY_LAT,GeoTagTable.GEOTAG_KEY_TYPE, GeoTagTable.GEOTAG_KEY_PICPATH, GeoTagTable.GEOTAG_KEY_TIME, GeoTagTable.GEOTAG_KEY_EXTERNKEY};
		Uri tempURI = Uri.parse(GeoTagContentProvider.CONTENT_URI+"/type/"+AddGeoTagActivity.SHOW_ALL);

		CursorLoader cl = new CursorLoader(this, tempURI, projection, null, null, null);
		Log.d("wi11b031","ende von onCreateLoader !"+arg1);
		return cl;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		Log.d("wi11b031","bin im onLoadFinished");
		if (cursor != null){
			cursor.moveToFirst();
			do{
				GeoTag g = null;

				g = new GeoTag();
				
				g.setId(cursor.getInt(GeoTagTable.GEOTAG_COL_ID));
				g.setName(cursor.getString(GeoTagTable.GEOTAG_COL_NAME));
				g.setLatitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LAT));
				g.setLongitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LONG));
				g.setType(cursor.getInt(GeoTagTable.GEOTAG_COL_TYPE));
				g.setPicpath(cursor.getString(GeoTagTable.GEOTAG_COL_PICPATH));
				g.setTime(cursor.getString(GeoTagTable.GEOTAG_COL_TIME));
				g.setExternalKey(cursor.getString(GeoTagTable.GEOTAG_COL_EXTERNKEY));
				
				
				Log.w("wi11b031","GeoTag: " +g);
				Log.w("wi11b031","---------------------------");
				
				LatLng pos = new LatLng(g.getLatitude(), g.getLongitude());
				
				 Marker test = map.addMarker(new MarkerOptions().position(pos)
			              .title(g.getName()));
				 map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
			}while(cursor.moveToNext());
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		Log.d("wi11b031","wi11b031 on Loader Reset");
		
	}
}

