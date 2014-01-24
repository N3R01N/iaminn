package com.testrunns.geotagging;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class AddImageTask extends AsyncTask<GeoTag, Void, String>{
	private List<NameValuePair> nameValuePair;
	DefaultHttpClient httpClient;
	HttpPost post;
	HttpResponse response = null;
	List<GeoTag> tagsToUpload;
	public AddImageTask() {
	      //this.bmImage = bmImage;
		  httpClient = new DefaultHttpClient();
		  httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                System.getProperty("http.agent"));
		  nameValuePair = new ArrayList<NameValuePair>();
		  tagsToUpload = new ArrayList<GeoTag>();
	  }


	private String upload_Geotag(String url) {
		String urldisplay = url;
		String output = null;
		if(url != null)
        {   
	        try {
	            post = new HttpPost(url);
	            UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8);
	            post.setEntity(p_entity);
	            
	            for(int i = 0; i < nameValuePair.size(); i++) {
	            post.addHeader(nameValuePair.get(i).getName(), nameValuePair.get(i).getValue());
	            }

	            response = (HttpResponse) httpClient.execute(post);
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
	            	byte[] result = EntityUtils.toByteArray(response.getEntity());
	                output = new String(result, "UTF-8");
	            }
	        }
	        catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		Log.d("wi11b031","wi11b031 output: "+ output);
    return output;
	}


	@Override
	protected String doInBackground(GeoTag... geoTags) {
		String x = null;
		tagsToUpload.clear();//profisorisch
		//02Hotel Stephansplatz16.3722000122070348.208698272705081Stephansplatz[B@2a8663582014-01-15 00:00:00.
		tagsToUpload.add(new GeoTag("Slackline-Meetingpoint",16.408675,48.207515 , 1, "imagedesc", "imagename"));//profisorisch
		for (GeoTag gtag : tagsToUpload) {
			if(gtag != null){
				this.tagsToUpload.add(gtag);
				nameValuePair.add(new BasicNameValuePair("name", gtag.getName()));
				nameValuePair.add(new BasicNameValuePair("lon", ""+gtag.getLongitude()));
				nameValuePair.add(new BasicNameValuePair("lat", ""+gtag.getLatitude()));
				nameValuePair.add(new BasicNameValuePair("type", ""+gtag.getType()));
				nameValuePair.add(new BasicNameValuePair("text", gtag.getText()));
				nameValuePair.add(new BasicNameValuePair("picpath", gtag.getPicpath()));
				x = upload_Geotag("http://wi-gate.technikum-wien.at:60660/marker/UploadGeotag");
			}
		}
		return x; //hier returne zeitpunkt vom Tag
		/*for (GeoTag gtag : geoTags) {
			if(gtag != null){
				this.tagsToUpload.add(gtag);
				nameValuePair
				return upload_Geotag("http://wi-gate.technikum-wien.at:60660/marker/UploadGeotag");
			}
		}*/
		
		
		
	}
	@Override
    protected void onPostExecute(String result) {
		Log.d("wi11b031","wi11b031 result: "+ result);
    }

}
