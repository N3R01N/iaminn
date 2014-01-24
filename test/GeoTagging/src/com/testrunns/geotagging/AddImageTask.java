package com.testrunns.geotagging;

import java.io.ByteArrayOutputStream;
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
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;

public class AddImageTask extends AsyncTask<GeoTag, Void, String>{
 private List<NameValuePair> nameValuePair;
 DefaultHttpClient httpClient;
 HttpPost post;
 HttpResponse response = null;
 String output;
 GeoTag tag;
 SyncWithServerListener mListener;
 
 
 public AddImageTask() {
       //this.bmImage = bmImage;
    httpClient = new DefaultHttpClient();
    httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                System.getProperty("http.agent"));
    nameValuePair = new ArrayList<NameValuePair>();
   }


 private String upload_Geotag(String url) {
  output = null;
  Log.d("wi11b031","wi11b031 url: "+url);
  if(url != null)
        {   
         try {
             post = new HttpPost(url);
             
             for(int i = 0; i < nameValuePair.size()-1; i++) {
                Log.d("wi11b031","wi11b031 namevalues " +nameValuePair.get(i).getValue());
              post.addHeader(nameValuePair.get(i).getName(), nameValuePair.get(i).getValue());
             }
             UrlEncodedFormEntity p_entity = new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8);
             
             post.setEntity(p_entity);
             response = (HttpResponse) httpClient.execute(post);
              HttpEntity httpEntity = response.getEntity();
                 output = EntityUtils.toString(httpEntity);
             //}
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
  
  for (GeoTag gtag : geoTags) {
   if(gtag != null){
	   Log.e("AddImageTask","doInBackground: "+gtag);
	   tag = gtag;
    nameValuePair.add(new BasicNameValuePair("name", gtag.getName()));
    nameValuePair.add(new BasicNameValuePair("lon", ""+gtag.getLongitude()));
    nameValuePair.add(new BasicNameValuePair("lat", ""+gtag.getLatitude()));
    nameValuePair.add(new BasicNameValuePair("type", ""+gtag.getType()));
    nameValuePair.add(new BasicNameValuePair("text", gtag.getText()));
    String picPath = gtag.getPicpath();
    if (!picPath.equals(GeoTag.NO_PIC)) {
     Bitmap pic = BitmapFactory.decodeFile(picPath);         
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          pic.compress(Bitmap.CompressFormat.JPEG, 100, stream); //compress to which format you want.
          byte [] byte_arr = stream.toByteArray();
          String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
          Log.d("wi11b031","wi11b031 image:"+image_str.length());
          nameValuePair.add(new BasicNameValuePair("pic",image_str));
    }
    
    x = upload_Geotag("http://wi-gate.technikum-wien.at:60660/marker/UploadGeotag");
   }
  }
  return x; 
 }

 @Override
    protected void onPostExecute(String result) {
	 if(result != null && result.length() > 0){
		 Log.e("AddImageTask","result: "+result);
		 String[] arr = result.split(",");
		 int externalId = Integer.parseInt(arr[0]);
		 String time = arr[1];
		 Log.e("AddImageTask","externalId : "+externalId +" time: "+time);
		 if(mListener != null){
			 mListener.serverSyncedWithResult(externalId, tag.getId(), time);
		 }
	 }	
    }
 
 public void setListener(SyncWithServerListener listener){
	 mListener = listener;
 }
 
 public interface SyncWithServerListener{
	 public void serverSyncedWithResult(int externalId, int id, String time);
 }

}