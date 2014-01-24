package com.testrunns.geotagging;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;


public class FileCreator {
	
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
}
