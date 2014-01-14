package com.testrunns.geotagging;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddGeoTagActivity extends FragmentActivity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener{
	
		public static final String SHOW_ALL = "0";
	
	//Camera
		private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
		public static final int MEDIA_TYPE_IMAGE = 1;
		
		//Http connection
		public static final String SERVICE_URL ="http://wi-gate.technikum-wien.at:60360/examples/servlets/servlet/HelloWorldExample";
		
		//Location
		private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
		private Location mCurrentLocation;
		private LocationClient mLocationClient;
		
		Bitmap mImageBitmap;
		ImageView mImageView;
		
		private TextView urlTextView;
		
		
		public static class ErrorDialogFragment extends DialogFragment{
			// Global field to contain the error dialog
	        private Dialog mDialog;
	        // Default constructor. Sets the dialog field to null
	        public ErrorDialogFragment() {
	            super();
	            mDialog = null;
	        }
	        // Set the dialog to display
	        public void setDialog(Dialog dialog) {
	            mDialog = dialog;
	        }
	        // Return a Dialog to the DialogFragment.
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            return mDialog;
	        }

		}
		
		
		protected void onStart(){
			super.onStart();
			mLocationClient.connect();
		}
		
		protected void onStop(){
			Log.d("onStop","stopping mLocationClient!");
			mLocationClient.disconnect();
			super.onStop();	
		}
		

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.layout_add_geo_tag);
			
			mImageView = (ImageView) findViewById(R.id.imageViewPic);
			//urlTextView = (TextView) findViewById(R.id.textView1);
			
			mLocationClient = new LocationClient(this, this, this);
			
			
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.camera, menu);
			return true;
		}
		
		public void onTakePictureButtonClick(View view){
			takePicture(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}

		private void takePicture(int requestCode) {
			Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(takePicIntent, requestCode);	
		}
		
		private void displayImageThumbnail(Intent intent){
			Log.d("Bild","in der displayImageThumbnail methode!");
			Bundle extras = intent.getExtras();
			mImageBitmap = (Bitmap) extras.get("data");
			mImageView.setImageBitmap(mImageBitmap);
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
			case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
				if(resultCode == RESULT_OK) displayImageThumbnail(data);
				break;
			case CONNECTION_FAILURE_RESOLUTION_REQUEST:
				  switch (resultCode) {
                  case Activity.RESULT_OK :
	                  mLocationClient.connect();
                  break;
              }


			default:
				Log.d("onActivityResult","default case in onActivityResult");
				break;
			}
				
			
		}
		
		//----------------Network Connection -----------------------//
		
		public void getUrlclickHandler(View v){
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isConnected()){
				new DownloadWebpageTask().execute(SERVICE_URL);
			}
			else{
				Toast.makeText(this, "conn not OK", Toast.LENGTH_SHORT).show();
			}
		}
		
		private class DownloadWebpageTask extends AsyncTask<String, Void, String>{
			
			

			@Override
			protected String doInBackground(String... urls) {
				try {
					return downloadUrl(urls[0]);
				} catch (IOException e) {
					return "Unable to retrieve page";
				}
			}
			
			
			
			private String downloadUrl(String myurl) throws IOException {
			    InputStream is = null;
			    // Only display the first 500 characters of the retrieved
			    // web page content.
			    int len = 500;
			    
			        
			    try {
			        URL url = new URL(myurl);
			        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			        conn.setReadTimeout(10000 /* milliseconds */);
			        conn.setConnectTimeout(15000 /* milliseconds */);
			        conn.setRequestMethod("GET");
			        conn.setDoInput(true);
			        Log.d("test","hier");
			        
			        // Starts the query
			        conn.connect();
			        int response = conn.getResponseCode();
			        Log.d("downloadUrl", "The response is: " + response);
			        is = conn.getInputStream();

			        // Convert the InputStream into a string
			        String contentAsString = readIt(is, len);
			        return contentAsString;
			        
			    // Makes sure that the InputStream is closed after the app is
			    // finished using it.
			    } finally {
			        if (is != null) {
			            is.close();
			        } 
			    }
			}
			
			// Reads an InputStream and converts it to a String.
			public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
			    Reader reader = null;
			    reader = new InputStreamReader(stream, "UTF-8");        
			    char[] buffer = new char[len];
			    reader.read(buffer);
			    return new String(buffer);
			}

			@Override
			protected void onPostExecute(String result) {
				urlTextView.setText(""+result);
			}
			
		}
		
		
	//-----------------------LOCATION---------------------------------------------

		
		public void getLocation(View view){
			mCurrentLocation = mLocationClient.getLastLocation();
			Log.d("LOCATION","lang: "+mCurrentLocation.getLatitude()+" long: "+mCurrentLocation.getLongitude());
		}
		


		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) {
			 /*
	         * Google Play services can resolve some errors it detects.
	         * If the error has a resolution, try sending an Intent to
	         * start a Google Play services activity that can resolve
	         * error.
	         */
	        if (connectionResult.hasResolution()) {
	            try {
	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(this,CONNECTION_FAILURE_RESOLUTION_REQUEST);
	                /*
	                 * Thrown if Google Play services canceled the original
	                 * PendingIntent
	                 */
	            } catch (IntentSender.SendIntentException e) {
	                // Log the error
	                e.printStackTrace();
	            }
	        } else {
	            /*
	             * If no resolution is available, display a dialog to the
	             * user with the error.
	             */
	            showErrorDialog(connectionResult.getErrorCode());
	        }

			
		}

		private void showErrorDialog(int errorCode) {
			ErrorDialogFragment error = new ErrorDialogFragment();
			error.setDialog(new Dialog(getApplicationContext(),errorCode));
			
		}

		@Override
		public void onConnected(Bundle dataBundle) {
			 Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onDisconnected() {
			Toast.makeText(this, "Disconnected. Please re-connect.",Toast.LENGTH_SHORT).show();	
		}



}
