package com.testrunns.geotagging;

import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewMapActivity extends Fragment implements
		LoaderCallbacks<Cursor>, OnInfoWindowClickListener {

	private GoogleMap map;
	private GetXMLTask XMLservlet;
	public static final String URL = "http://wi-gate.technikum-wien.at:60660/marker/getMarker";
	TextView outputText;
	ImageView imageView;

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

		getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);

		map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setOnInfoWindowClickListener(this);
		XMLservlet = new GetXMLTask();
		XMLservlet.execute(new String[] { URL }, this);
		imageView.setVisibility(View.INVISIBLE);
		imageView.setOnClickListener(myhandler);
		return rootView;
	}

	public void onStart() {
		super.onStart();
		Log.e("Fragment","onStart!");
		getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
	}

	public void onDestroyView(){
		super.onDestroyView();
		Log.e("Fragment","onDestroyView!");
	}
	public void onDetach(){
		super.onDetach();
		Log.e("Fragment","onDetach!");
	}
	public void onStop(){
		
		Log.e("Fragment","onStop!");
		super.onStop();
	}
	public void onResume(){
		super.onResume();
		Log.e("Fragment","onResume!");
	}
	public void onHiddenChanged(boolean hidden){
		super.onHiddenChanged(hidden);
		Log.e("Fragment","onHiddenChanged:" +hidden);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.view_map, menu);
//		return true;
//	}

	View.OnClickListener myhandler = new View.OnClickListener() {
		public void onClick(View v) {
			if (v != null)
				imageView.setVisibility(View.INVISIBLE);
		}
	};

	@Override
	public void onInfoWindowClick(Marker marker) {
		String picPath = geoTags.get(marker).getPicpath();
		if (!picPath.equals(GeoTag.NO_PIC)) {
			Bitmap pic = BitmapFactory.decodeFile(picPath);
			Bitmap thumbnail = ThumbnailUtils.extractThumbnail(pic, 400, 400);
			imageView.setImageBitmap(thumbnail);
			imageView.setVisibility(View.VISIBLE);
			imageView.bringToFront();

		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { GeoTagTable.GEOTAG_KEY_ID,
				GeoTagTable.GEOTAG_KEY_NAME, GeoTagTable.GEOTAG_KEY_LONG,
				GeoTagTable.GEOTAG_KEY_LAT, GeoTagTable.GEOTAG_KEY_TYPE,
				GeoTagTable.GEOTAG_KEY_PICPATH, GeoTagTable.GEOTAG_KEY_TIME,
				GeoTagTable.GEOTAG_KEY_EXTERNKEY };

		Uri tempURI = Uri.parse(GeoTagContentProvider.CONTENT_URI + "/type/"
				+ AddGeoTagActivity.SHOW_ALL);

		CursorLoader cl = new CursorLoader(getActivity(), tempURI, projection, null,
				null, null);
		Log.d("wi11b031", "ende von onCreateLoader !" + arg1);
		return cl;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		Log.d("wi11b031", "bin im onLoadFinished" + cursor);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				GeoTag g = null;

				g = new GeoTag();

				g.setId(cursor.getInt(GeoTagTable.GEOTAG_COL_ID));
				g.setName(cursor.getString(GeoTagTable.GEOTAG_COL_NAME));
				g.setLatitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LAT));
				g.setLongitude(cursor.getDouble(GeoTagTable.GEOTAG_COL_LONG));
				g.setType(cursor.getInt(GeoTagTable.GEOTAG_COL_TYPE));
				g.setPicpath(cursor.getString(GeoTagTable.GEOTAG_COL_PICPATH));
				g.setTime(cursor.getString(GeoTagTable.GEOTAG_COL_TIME));
				g.setExternalKey(cursor
						.getString(GeoTagTable.GEOTAG_COL_EXTERNKEY));

				Log.w("wi11b031", "GeoTag: " + g);
				Log.w("wi11b031", "---------------------------");

				LatLng pos = new LatLng(g.getLatitude(), g.getLongitude());

				Marker test = map.addMarker(new MarkerOptions().position(pos)
						.title(g.getName()));
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));

				geoTags.put(test, g);
			} while (cursor.moveToNext());
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d("wi11b031", "on Loader Reset");
	}
}
