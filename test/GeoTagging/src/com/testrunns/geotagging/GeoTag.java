package com.testrunns.geotagging;

import android.util.Log;

public class GeoTag {
	
	private static final String TAG = "GeoTag";
	public static final String NEW_TAG = "new";
	
	private int id;
	private String name;
	private double longitude;
	private double latitude;
	private int type;
	private String picpath;
	private String text;
	private String time;
	private String externalKey;
	
	public GeoTag(){
		id = 0;
		name ="unknown";
		longitude = 0;
		latitude = 0;
		type = 0;
		picpath = "unknown";
		time = "unknown";
		externalKey = "unknown";
	}
	
	public GeoTag(String name, double lo, double la, int type, String text, String pic){
		id = 0;
		this.name = name;
		longitude = lo;
		latitude = la;
		this.type = type;
		picpath = pic;
		time = NEW_TAG;
		externalKey = NEW_TAG;
		Log.w(TAG,"new geotag");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPicpath() {
		return picpath;
	}
	
	public String getText() {
		return text;
	}

	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}
	
	public String toString(){
		return "id: "+id
				+"\nname: "+name
				+"\nlong: "+longitude
				+"\nlat: "+latitude
				+"\ntype: "+type
				+"\npic path: "+picpath
				+"\nexternal id: "+externalKey;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof GeoTag){
			return this.getExternalKey() == ((GeoTag) obj).getExternalKey();
		}
		return false;
	}

}
