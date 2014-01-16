package com.testrunns.geotagging;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewMapActivity extends FragmentActivity implements OnInfoWindowClickListener {
//public class ViewMapActivity extends FragmentActivity implements LoaderCallbacks<Cursor>, OnInfoWindowClickListener {
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	private GoogleMap map;
	private ServletCaller servlet;
	private GetXMLTask XMLservlet;
	public static final String URL = "http://wi-gate.technikum-wien.at:60660/marker/getMarker";
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
		/*geoTags = new ArrayList<GeoTag>();
		openHelper = new GeoTagOpenHelper(this);
		geoTags = openHelper.getGeoTagFromCategory("1");
		Log.d("wi11b031","wi11b031 size db:"+geoTags.size());
		*/
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setOnInfoWindowClickListener(this);
		XMLservlet = new GetXMLTask();
		XMLservlet.execute(new String[] { URL }, this);
		button1.bringToFront();
		imageView.setVisibility(View.INVISIBLE);
		imageView.setOnClickListener(myhandler);
		//getSupportLoaderManager().initLoader(LOADER_ID, null, this);
		
		 if (map!=null){
			 
			 
			 //Log.d("wi11b031","wi11b031 " + servlet.getServletString());

			 //Log.d("wi11b031","wi11b031 " + servlet.getServletString());
	          Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
	              .title("Hamburg"));
	          Marker kiel = map.addMarker(new MarkerOptions()
	              .position(KIEL)
	              .title("Kiel")
	              .snippet("Kiel is cool<img src='http://www.colourbox.de/preview/1450780-303470-der-einsame-baum-isoliert-die-willow-ordinary.jpg' width='320' height='400' name='Baumbild' alt='Abendbaum'>")
	              
	              .icon(BitmapDescriptorFactory
	                  .fromResource(R.drawable.ic_launcher)));
	          
	          map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
	          map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	         /* Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI+"/geotag/"+AddGeoTagActivity.SHOW_ALL);
	          getContentResolver().query(uri, null, null, null, null);*/
	          //getSupportLoaderManager().initLoader(0, null, this);  
	        }
		// Move the camera instantly to hamburg with a zoom of 15.
		

		// Zoom in, animating the camera.
		
		/*
	    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	            .getMap();
	        
	       
	        
	        keytool -list -v -alias androiddebugkey \
	        -keystore C:\Users\p\.android\debug.keystore \
	        -storepass android -keypass android 
	        */
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
			 // String mCurrentPhotoPath = "/sdcard/DCIM/Camera/1.jpg";
			  imageView.setImageBitmap(result);
			    /*int targetW = imageView.getWidth();
			    int targetH = imageView.getHeight();

			    // Get the dimensions of the bitmap
			    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			    bmOptions.inJustDecodeBounds = true;
			    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
			    int photoW = bmOptions.outWidth;
			    int photoH = bmOptions.outHeight;

			    // Determine how much to scale down the image
			    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

			    // Decode the image file into a Bitmap sized to fill the View
			    bmOptions.inJustDecodeBounds = false;
			    bmOptions.inSampleSize = scaleFactor;
			    bmOptions.inPurgeable = true;
			  Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
			  File extStore = Environment.getExternalStorageDirectory();
			  Log.d("wi11b031","wi11b031 pafd test" + bitmap + "   extstroe  " + extStore.getPath());
			  imageView.setImageBitmap(bitmap);*/
		  }
		}
	@Override
	public void onInfoWindowClick(Marker marker) {
		java.net.URL url;
		new DownloadImageTask((ImageView) findViewById(R.id.imageView))
	        .execute("http://mypics.at/d/1526-12/Wiese.jpg");
		imageView.setVisibility(View.VISIBLE);
		imageView.bringToFront();
		/*DownloadImageTask dit = new DownloadImageTask(imageView);
			url = new java.net.URL("");
			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			imageView.setImageBitmap(bmp);*/
	}
/*
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = {GeoTagTable.GEOTAG_KEY_ID, GeoTagTable.GEOTAG_KEY_NAME,GeoTagTable.GEOTAG_KEY_LONG,GeoTagTable.GEOTAG_KEY_LAT,GeoTagTable.GEOTAG_KEY_TYPE, GeoTagTable.GEOTAG_KEY_PICPATH, GeoTagTable.GEOTAG_KEY_TIME, GeoTagTable.GEOTAG_KEY_EXTERNKEY};
		Uri tempURI = Uri.parse(GeoTagContentProvider.CONTENT_URI+"/geotag/"+AddGeoTagActivity.SHOW_ALL);
		//JokeContentProvider jokeCP = new JokeContentProvider();
		// = new String[5];
		CursorLoader cl = new CursorLoader(this, tempURI, projection, null, null, null);
		//cl.setUri();

		
		return cl;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		Log.d("wi11b031","wi11b031 db "+arg1.getCount());
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		//m_jokeAdapter.swapCursor(null);
		
	}*/
}

