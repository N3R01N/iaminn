package com.testrunns.geotagging;

import java.io.ByteArrayInputStream;
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
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class GetXMLTask extends AsyncTask<String, String, String> {
	private HashMap<String, String> mData = null;// post data
	
	ViewMapActivity caller;
	XMLParser parser = new XMLParser();
	byte[] pic;
	@Override
    protected String doInBackground(String... urls) {
		byte[] result = null;
		String output = null;
        if(urls != null)
        {   
	        try {
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost post = new HttpPost(urls[0]);
	            
	            ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	            Iterator<String> it = mData.keySet().iterator();
	            while (it.hasNext()) {
	                String key = it.next();
	                nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
	            }
	            post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
	            HttpResponse response = httpClient.execute(post);
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
	                result = EntityUtils.toByteArray(response.getEntity());
	                output = new String(result, "UTF-8");
	            }
	        }
	        catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }
	        catch (Exception e) {
	        }
        }
        return output;

    }



    @Override
    protected void onPostExecute(String output) {
    	InputStream stream;
    	List<GeoTag> geoTagList;
    	try {
			stream = new ByteArrayInputStream(output.getBytes("UTF-8"));
			try {
				geoTagList = parser.parse(stream);
				pic = new byte[100000];
				if(geoTagList != null)
					caller.outputText.setText(geoTagList.get(1).getPicpath());
					caller.outputText.bringToFront();
					
					
					pic = geoTagList.get(1).getPicpath().getBytes();
					
					
					Bitmap bmp=BitmapFactory.decodeByteArray(pic,0,pic.length);
	            	caller.imageView.setImageBitmap(bmp);
					//caller.outputText.setText(geoTagList.get(1).getPicpath());
					caller.imageView.bringToFront();
					caller.imageView.setVisibility(View.VISIBLE);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }

	public void execute(String[] strings, ViewMapActivity viewMapActivity) {
		this.execute(strings);
		caller = viewMapActivity;
		
	}
	
	public void execute(String[] strings, ViewMapActivity viewMapActivity, HashMap<String, String> data) {
		mData = data;
		this.execute(strings);
		caller = viewMapActivity;
		
	}
}