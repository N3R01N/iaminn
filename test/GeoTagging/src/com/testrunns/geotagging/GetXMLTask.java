package com.testrunns.geotagging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Log;

public class GetXMLTask extends AsyncTask<String, Void, String> {
	XMLParser parser = new XMLParser();
	SyncListener listener;
	@Override
    protected String doInBackground(String... urls) {
        String output = null;
        for (String url : urls) {
            output = getOutputFromUrl(url);
        }
        return output;
    }

    private String getOutputFromUrl(String url) {
        String output = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    @Override
    protected void onPostExecute(String output) {
    	InputStream stream;
    	List<GeoTag> geoTagList = null;
    	try {
			stream = new ByteArrayInputStream(output.getBytes("UTF-8"));
			try {
				geoTagList = parser.parse(stream);
				if(geoTagList != null && geoTagList.size() > 0 ){
					listener.syncHasResult(geoTagList);
				}
				else{
					listener.syncHasNoResult();
				}
					
			} catch (XmlPullParserException e) {
				Log.w("GetXmlTask","pullperserEcxeption");
				e.printStackTrace();
			} catch (IOException e) {
				Log.w("GetXmlTask","IOException");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			Log.w("GetXmlTask","UnsupportedEncodingException");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

	public void execute(String[] strings, ViewMapActivity viewMapActivity) {
		this.execute(strings);
	}
	
	public void setSyncListener(SyncListener listener){
		this.listener = listener;
	}
	
	public static interface SyncListener{
		void syncHasResult(List<GeoTag> tags);
		void syncHasNoResult();
	}
}