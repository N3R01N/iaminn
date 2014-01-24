package com.testrunns.geotagging;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.testrunns.geotagging.GetXMLTask.SyncListener;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
	// ImageView bmImage;
	private List<NameValuePair> nameValuePair;
	DefaultHttpClient httpClient;
	HttpPost post;
	HttpResponse response = null;
	PicSyncListener listener;
	

	public DownloadImageTask() {
		// this.bmImage = bmImage;
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				System.getProperty("http.agent"));
		nameValuePair = new ArrayList<NameValuePair>(1);
	}

	protected Bitmap download_Image(String url) {
		String urldisplay = url;
		Bitmap output = null;
		if (url != null) {
			try {
				post = new HttpPost(url);

				UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(
						nameValuePair, HTTP.UTF_8);
				post.setEntity(p_entity);
				post.addHeader(nameValuePair.get(0).getName(), nameValuePair
						.get(0).getValue());
				response = (HttpResponse) httpClient.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {

					HttpEntity entity = response.getEntity();
					InputStream instream = entity.getContent();
					output = BitmapFactory.decodeStream(instream);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return output;

	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if(result == null) Log.e("DownloadImageTask","result = null");
		else if(nameValuePair.get(0).getValue().toString()==null) Log.e("DownloadImageTask","externalKey = null");
		else{
			listener.PicSyncHasResult(result, nameValuePair.get(0).getValue().toString());
		}
		
	}

	public void addNameValuePair(String name, String value) {

		nameValuePair.add(new BasicNameValuePair(name, value));
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		return download_Image("http://wi-gate.technikum-wien.at:60660/marker/DisplayImage");
	}
	
	public void setSyncListener(PicSyncListener listener){
		this.listener = listener;
	}
	
	public static interface PicSyncListener{
		void PicSyncHasResult(Bitmap bitmap, String externalId);
		void PicSyncHasNoResult();
	}

}