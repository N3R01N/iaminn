package com.testrunns.geotagging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// newest!

public class AddGeoTagActivity extends FragmentActivity implements
                GooglePlayServicesClient.ConnectionCallbacks,
                GooglePlayServicesClient.OnConnectionFailedListener {
        
        private static final String TAG = "AddGeoTagActivity";

        public static final String SHOW_ALL = "0";

        // Camera
        private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
        public static final int MEDIA_TYPE_IMAGE = 1;

        // Location
        private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
        private Location mCurrentLocation;
        private LocationClient mLocationClient;

        private ImageView mImageView;
        
        private String currentPhotoPath;

        private TextView textViewLat;
        private TextView textViewLong;
        private EditText editTextGeoTagName;
        private EditText editTextGeoTagDescriptiton;

        

        protected void onStart() {
                super.onStart();
                mLocationClient.connect();
        }

        protected void onStop() {
                Log.d("onStop", "stopping mLocationClient!");
                mLocationClient.disconnect();
                super.onStop();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.layout_add_geo_tag);

                mImageView = (ImageView) findViewById(R.id.imageViewPic);
                textViewLat = (TextView) findViewById(R.id.textViewValueLat);
                textViewLong = (TextView) findViewById(R.id.textViewValueLong);
                editTextGeoTagName = (EditText) findViewById(R.id.editTextTagName);
                editTextGeoTagDescriptiton = (EditText) findViewById(R.id.editTextTagDesc);

                mLocationClient = new LocationClient(this, this, this);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.camera, menu);
                return true;
        }
        
        //----------------------- Picture ------------------------------
        
        public File createImageFile() throws IOException{
                // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath =  image.getAbsolutePath();
            Log.d(TAG,"path:" +currentPhotoPath);
            return image;

        }

        public void onTakePictureButtonClick(View view) {
                takePicture(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }

        private void takePicture(int requestCode) {
                Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePicIntent.resolveActivity(getPackageManager()) != null){
                        
                        File photoFile = null;
                        try {
                                photoFile = createImageFile();
                        } catch (IOException e) {
                                Log.d(TAG,"exception: "+e.getMessage());
                        }
                        if(photoFile != null){
                                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                startActivityForResult(takePicIntent, requestCode);
                        }
                        
                }
                
        }

        private void displayImageThumbnail() {
                Log.d("Bild", "in der displayImageThumbnail methode!");
                // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
                 mImageView.setImageBitmap(bitmap);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                switch (requestCode) {
                case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                        if (resultCode == RESULT_OK)
                                displayImageThumbnail();
                        break;
                case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                        switch (resultCode) {
                        case Activity.RESULT_OK:
                                mLocationClient.connect();
                                break;
                        }

                default:
                        Log.d("onActivityResult", "default case in onActivityResult");
                        break;
                }

        }

        // -----------------------LOCATION---------------------------------------------
        
        public static class ErrorDialogFragment extends DialogFragment {
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

        private void getLocation(){
                if(servicesConnected()){
                        mCurrentLocation = mLocationClient.getLastLocation();
                        textViewLat.setText(""+mCurrentLocation.getLatitude());
                        textViewLong.setText(""+mCurrentLocation.getLongitude());
                }
        }

        private boolean servicesConnected() {
                // Check that Google Play services is available
                int resultCode = GooglePlayServicesUtil
                                .isGooglePlayServicesAvailable(this);

                // If Google Play services is available
                if (ConnectionResult.SUCCESS == resultCode) {
                        // In debug mode, log the status
                        Log.d("Location Updates", "Google Play services is available.");
                        // Continue
                        return true;
                        // Google Play services was not available for some reason
                } else {
                        // Get the error code
                        int errorCode = resultCode;
                        // Get the error dialog from Google Play services
                        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                                        errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

                        // If Google Play services can provide an error dialog
                        if (errorDialog != null) {
                                // Create a new DialogFragment for the error dialog
                                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                                // Set the dialog in the DialogFragment
                                errorFragment.setDialog(errorDialog);
                                // Show the error dialog in the DialogFragment
                                errorFragment.show(getSupportFragmentManager(),
                                                "Location Updates");
                        }
                }
                return false;
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
                /*
                 * Google Play services can resolve some errors it detects. If the error
                 * has a resolution, try sending an Intent to start a Google Play
                 * services activity that can resolve error.
                 */
                if (connectionResult.hasResolution()) {
                        try {
                                // Start an Activity that tries to resolve the error
                                connectionResult.startResolutionForResult(this,
                                                CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
                         * If no resolution is available, display a dialog to the user with
                         * the error.
                         */
                        showErrorDialog(connectionResult.getErrorCode());
                }

        }

        private void showErrorDialog(int errorCode) {
                ErrorDialogFragment error = new ErrorDialogFragment();
                error.setDialog(new Dialog(getApplicationContext(), errorCode));

        }

        @Override
        public void onConnected(Bundle dataBundle) {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
                getLocation();

        }

        @Override
        public void onDisconnected() {
                Toast.makeText(this, "Disconnected. Please re-connect.",
                                Toast.LENGTH_SHORT).show();
        }

        //----------------------------save Geo Tag------------------------------
        
        public void saveGeoTag(View view) {
                if (servicesConnected()) {
                        mCurrentLocation = mLocationClient.getLastLocation();
                        Log.d("LOCATION", "lang: " + mCurrentLocation.getLatitude()
                                        + " long: " + mCurrentLocation.getLongitude());
                        
                        String name = editTextGeoTagName.getText().toString();
                        double la = mCurrentLocation.getLatitude();
                        double lo = mCurrentLocation.getLongitude();
                        int type = 1;
                        String pic = GeoTag.NO_PIC;
                        if(currentPhotoPath != null){
                                pic = currentPhotoPath;
                        }
                        
                        if(name.length()==0 || name == ""){
                                Toast.makeText(this, "please enter a name for the tag", Toast.LENGTH_LONG).show();
                                return;
                        } 
                        
                        GeoTag tag = new GeoTag(name, lo, la, type, "",pic);
                        editTextGeoTagName.setText("");
                        editTextGeoTagDescriptiton.setText("");
                        currentPhotoPath = null;
                        addGeoTag(tag);
                }
        }
        
        private void addGeoTag(GeoTag tag) {
                Uri uri = Uri.parse(GeoTagContentProvider.CONTENT_URI+"/geotag/"+tag.getId());
                Log.d(TAG,"uri: "+uri);
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
                Log.d(TAG,"inserting returns id: "+id);
                tag.setId(id);        
        }
        
}