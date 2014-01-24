/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.testrunns.geotagging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.testrunns.geotagging.GeoTagSyncService.LocalBinder;
import com.testrunns.geotagging.GeoTagSyncService.NewGeoTagsListener;
import com.testrunns.geotagging.GeoTagSyncService.NewPicListener;

import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class MainActivity extends FragmentActivity implements TabListener, NewGeoTagsListener, NewPicListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the three primary sections of the app. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will display the three primary sections of the
	 * app, one at a time.
	 */
	ViewPager mViewPager;
	GeoTagSyncService mService;
	boolean mBound = false;
	ViewMapActivity mViewMapFragment;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between different app sections, select
						// the corresponding tab.
						// We can also use ActionBar.Tab#select() to do this if
						// we have a reference to the
						// Tab.
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter.
			// Also specify this Activity object, which implements the
			// TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		
		
		mViewMapFragment = (ViewMapActivity) mAppSectionsPagerAdapter.getItem(0);
		
		Log.e("main","mapactivity: "+mViewMapFragment.getClass());
	}

	protected void onStart() {
		super.onStart();
		Intent serviceIntent = new Intent(this, GeoTagSyncService.class);
		startService(serviceIntent);
		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			setServiceListener();
		}

		@Override
		public void onServiceDisconnected(ComponentName service) {
			mBound = false;	
			mService = null;
		}

	};
	
	public void setServiceListener(){
		if(mService != null && mBound){
			 mService.setNewGeoTagsListener(this);
			 mService.setNewPicListener(this);
		}
		else Log.e("MainActivity","something wrong with mService!\n mService: "+mService +" mBound: "+mBound);
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		if(tab.getPosition() == 0 && mViewMapFragment != null) {
			Log.w("main","mviewmap != null");
			mViewMapFragment.restartLoader();
		}
		else Log.w("main","mviewmap = null");
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new ViewMapActivity();
			case 1:
				return new AddGeoTagActivity();

			default:
				return new AddGeoTagActivity();
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "View Map";
			case 1:
				return "Add GeoTag";

			default:
				return "Default";
			}
		}
	}
	
	public File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		return image;
	}

	@Override
	public void addNewGeoTagsFromSync(List<GeoTag> tags) {
		if(tags != null && tags.size() > 0){
			for (GeoTag tag : tags) {
				Log.e("Main Add Tag","tag:"+tag.getExternalKey());
				Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI+"/geotag/"+tag.getId());
				ContentValues cv = new ContentValues();
                cv.put(GeoTagTable.GEOTAG_KEY_NAME, tag.getName());
                cv.put(GeoTagTable.GEOTAG_KEY_LAT, tag.getLatitude());
                cv.put(GeoTagTable.GEOTAG_KEY_LONG, tag.getLongitude());
                cv.put(GeoTagTable.GEOTAG_KEY_TYPE, tag.getType());
                cv.put(GeoTagTable.GEOTAG_KEY_TIME, tag.getTime());
                cv.put(GeoTagTable.GEOTAG_KEY_PICPATH, tag.getPicpath());
                cv.put(GeoTagTable.GEOTAG_KEY_EXTERNKEY, tag.getExternalKey());
                
                Uri idUri = getContentResolver().insert(uri, cv);
                
                int id = Integer.parseInt(idUri.getLastPathSegment());
                Log.d("Main Add Tag","inserting returns id: "+id);
                tag.setId(id); 
			}
		}
		else Log.w("test","bin da aber im else");
		
	}

	@Override
	public void addPicToGeoTag(Bitmap bitmap, String externalId) {
		File newPic = null;
		try {
			newPic = createImageFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(newPic != null){
			FileOutputStream out;
			try {
				out = new FileOutputStream(newPic);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI+"/geotag/"+externalId);
				ContentValues cv = new ContentValues();
				cv.put(GeoTagTable.GEOTAG_KEY_PICPATH, newPic.getAbsolutePath());
				
				int count = getContentResolver().update(uri, cv, null, null);
				Log.i("addPic","count: "+count);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
