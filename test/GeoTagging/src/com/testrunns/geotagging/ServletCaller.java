package com.testrunns.geotagging;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class ServletCaller {
	public static final String URL = "http://wi-gate.technikum-wien.at:60660/marker/getMarker";
	private String ServletString = "";
	
	
	public ServletCaller() {
        GetXMLTask task = new GetXMLTask();
        task.execute(new String[] { URL });
	}
	 
	public void setServletString(String ServStr) {
		ServletString = ServStr;
	}
	
	public String getServletString() {
		return ServletString;
	}
	
    private class GetXMLTask extends AsyncTask<String, Void, String> {
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
            //outputText.setText(output);
        	Log.d("wi11b031","wi11b031 hahshs" + output);
        	setServletString(output);
        }
    }
}

