package com.testrunns.geotagging;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewMapActivity extends Fragment implements
		LoaderCallbacks<Cursor>, OnInfoWindowClickListener {

	private GoogleMap map;
	
	TextView outputText;
	ImageView imageView;
	LoaderManager mLoaderManager;

	private HashMap<Marker, GeoTag> geoTags;

	private static final int LOADER_ID = 1;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.activity_view_map, container, false);
		outputText = (TextView) rootView.findViewById(R.id.textView);
		imageView = (ImageView) rootView.findViewById(R.id.imageView);

		geoTags = new HashMap<Marker, GeoTag>();

		mLoaderManager = getActivity().getSupportLoaderManager();
		mLoaderManager.initLoader(LOADER_ID, null, this);

		map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setOnInfoWindowClickListener(this);
		imageView.setVisibility(View.INVISIBLE);
		imageView.setOnClickListener(myhandler);
		Log.e("ViewMapActivity", "onCreateView fertig!");
		return rootView;
	}

	public void restartLoader(){
		if(mLoaderManager != null){
			mLoaderManager.restartLoader(LOADER_ID, null, this);
		}
		else Log.e("ViewMapActivity", "mLoaderManager = null");
		
		
	}

	View.OnClickListener myhandler = new View.OnClickListener() {
		public void onClick(View v) {
			if (v != null)
				imageView.setVisibility(View.INVISIBLE);
		}
	};
	
	public void onAttatch(Activity activity){
		super.onAttach(activity);
		Log.i("CallBackMethods","onAttatch");
	}
	
	public void onStart(){
		super.onStart();
		Log.i("CallBackMethods","onStart");
	}
	
	public void onResume(){
		super.onResume();
		Log.i("CallBackMethods","onResume");
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		String picPath = geoTags.get(marker).getPicpath();
		restartLoader();
		if (!picPath.equals(GeoTag.NO_PIC)) {
			Bitmap pic = BitmapFactory.decodeFile(picPath);
			Bitmap thumbnail = ThumbnailUtils.extractThumbnail(pic, 400, 400);
			imageView.setImageBitmap(thumbnail);
			imageView.setVisibility(View.VISIBLE);
			imageView.bringToFront();

		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		String[] projection = { GeoTagTable.GEOTAG_KEY_ID,
				GeoTagTable.GEOTAG_KEY_NAME, GeoTagTable.GEOTAG_KEY_LONG,
				GeoTagTable.GEOTAG_KEY_LAT, GeoTagTable.GEOTAG_KEY_TYPE,
				GeoTagTable.GEOTAG_KEY_PICPATH, GeoTagTable.GEOTAG_KEY_TIME,
				GeoTagTable.GEOTAG_KEY_EXTERNKEY };

		Uri tempURI = Uri.parse(GeoTagContentProvider.CONTENT_URI + "/type/"
				+ AddGeoTagActivity.SHOW_ALL);

		CursorLoader cl = new CursorLoader(getActivity(), tempURI, projection, null,
				null, null);
		return cl;
	}
	

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (cursor != null && cursor.moveToFirst()) {
			do {
				GeoTag g = null;

				try {
					g = new GeoTag();
				} catch (ParseException e) {
					Log.e("ViewMapActivity","failed to create geoTag\n"+e.getMessage());
				}

				g.setId(cursor.getInt(GeoTagTable.GEOTAG_COL_ID));
				g.setName(cursor.getString(GeoTagTable.GEOTAG_COL_NAME));
				g.setLatitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LAT));
				g.setLongitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LONG));
				g.setType(cursor.getInt(GeoTagTable.GEOTAG_COL_TYPE));
				g.setPicpath(cursor.getString(GeoTagTable.GEOTAG_COL_PICPATH));
				g.setTime(cursor.getString(GeoTagTable.GEOTAG_COL_TIME));
				g.setExternalKey(cursor.getString(GeoTagTable.GEOTAG_COL_EXTERNKEY));

				LatLng pos = new LatLng(g.getLatitude(), g.getLongitude());

				Marker test = map.addMarker(new MarkerOptions().position(pos)
						.title(g.getName()));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
				
				Log.w("view Geo Tag",""+g.getName());

				geoTags.put(test, g);
			} while (cursor.moveToNext());
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d("wi11b031", "on Loader Reset");
	}


}
