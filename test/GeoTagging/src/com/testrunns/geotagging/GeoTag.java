package com.testrunns.geotagging;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.util.Log;

public class GeoTag {

	private static final String TAG = "GeoTag";

	public static final String NEW_TAG = "new";
	public static final String NO_PIC = "noPic";
	
	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private int id;
	private String name;
	private double longitude;
	private double latitude;
	private int type;
	private String picpath;
	private String text;
	private Date time;
	private String externalKey;

	public GeoTag() throws ParseException {
		id = 0;
		name = "unknown";
		longitude = 0;
		latitude = 0;
		type = 0;
		picpath = "unknown";
		time = new Date(0);
		externalKey = NEW_TAG;
	}

	public GeoTag(String name, double lo, double la, int type, String text,
			String pic) {
		Log.i("GeoTag","constructor sollte von AddGeoTagActivity aufgerufen werden!");
		id = 0;
		this.name = name;
		longitude = lo;
		latitude = la;
		this.type = type;
		picpath = pic;
		time = new Date(0);
		externalKey = NEW_TAG;
		Log.w(TAG, "new geotag");
	}
	
	public GeoTag(int id, String name, double lo, double la, int type, String text,
			String pic, String time){
		Log.i("GeoTag","constructor sollte von XMLParser aufgerufen werden! id:"+id);
		this.id = 0;
		this.name = name;
		longitude = lo;
		latitude = la;
		this.type = type;
		picpath = pic;
		setTime(time);
		this.externalKey = ""+id;
		Log.w(TAG, "new geotag, new constructor"+this);
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
		return df.format(time);
	}

	public void setTime(String time) {
		try {
			this.time =  (Date) df.parse(time);
		} catch (ParseException e) {
			Log.e("GeoTag","failed to parse date!");
		}	
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

	public String toString() {
		return "id: " + id + "\nname: " + name + "\nlong: " + longitude
				+ "\nlat: " + latitude + "\ntype: " + type + "\npic path: "
				+ picpath + "\nexternal id: " + externalKey + "\ndate: " + getTime();
	}

	public boolean equals(Object obj) {
		if (obj instanceof GeoTag) {
			return this.getExternalKey() == ((GeoTag) obj).getExternalKey();
		}
		return false;
	}

}
