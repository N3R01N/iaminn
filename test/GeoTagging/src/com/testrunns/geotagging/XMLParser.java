package com.testrunns.geotagging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Xml;

public class XMLParser {
	private static final String ns = null;
	//auf exakte schreibweise inkl. groﬂkleinschreibung achten!
	private static final String Wrapper = "All";
	private static final String ID = "ID";
	private static final String Name = "name";
	private static final String Lon = "lon";
	private static final String Lat = "lat";
	private static final String Text = "text";
	private static final String Picpath = "picpath";
	private static final String ExternalKey = "externalkey";
    public List<GeoTag> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    private List<GeoTag> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<GeoTag> entries = new ArrayList<GeoTag>();

        parser.require(XmlPullParser.START_TAG, ns, Wrapper);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Marker")) {
                entries.add(readGeoTag(parser));
            } else {
                skip(parser);
            }
        }  
        return entries;
    }
    
    

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private GeoTag readGeoTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Marker");
        int id, type = 1;
        String name = null, text = null, picpath = null, externalkey = null;
        Bitmap pic;
        double lon = 0, lat = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String parsername = parser.getName();
            if (parsername.equals(ID)) {
                id = readID(parser);
            } else if (parsername.equals(Name)) {
            	name = readName(parser);
            } else if (parsername.equals(Lon)) {
            	lon = readLongitude(parser);
            } else if (parsername.equals(Lat)) {
                lat = readLatitude(parser);
            } else if (parsername.equals("type")) {
                type = readType(parser);
            } else if (parsername.equals(Text)) {
                text = readText(parser);
            } else if (parsername.equals(Picpath)) {
            	pic = readPicpath(parser);
            } else if (parsername.equals(ExternalKey)) {
            	externalkey = readExternalKey(parser);
            }else {
                skip(parser);
            }
        }
        return new GeoTag(name, lon, lat, type, text, picpath);
    }

    private int readID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ID);
        int title = Integer.parseInt(readTextHelper(parser));
        parser.require(XmlPullParser.END_TAG, ns, ID);
        return title;
    }
    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, Name);
        String title = readTextHelper(parser);
        parser.require(XmlPullParser.END_TAG, ns, Name);
        return title;
    }
    private double readLongitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, Lon);
        double title = Double.parseDouble(readTextHelper(parser));
        parser.require(XmlPullParser.END_TAG, ns, Lon);
        return title;
    }
    private double readLatitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, Lat);
        double title = Double.parseDouble(readTextHelper(parser));
        parser.require(XmlPullParser.END_TAG, ns, Lat);
        return title;
    }
    private int readType(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "type");
        int title = Integer.parseInt(readTextHelper(parser));
        parser.require(XmlPullParser.END_TAG, ns, "type");
        return title;
    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, Text);
        String title = readTextHelper(parser);
        parser.require(XmlPullParser.END_TAG, ns, Text);
        return title;
    }
    private Bitmap readPicpath(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, Picpath);
        String title = readTextHelper(parser);
        BitmapFactory.Options bmo = new BitmapFactory.Options();
        bmo.inPreferredConfig = Config.ARGB_8888;
        byte[] byteArray = new byte[10000];
        
        for(int i=0; i < title.length();i++)
        {
        	//Log.d("wi11b031","wi11b031 char+ " + title.getBytes(title).toString());
        	byteArray[i] = (byte)title.charAt(i);
        	
        }
        Log.d("wi11b031","wi11b031 char+ " + byteArray);
        parser.require(XmlPullParser.END_TAG, ns, Picpath);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length,bmo);
        Log.d("wi11b031","wi11b031 asgfd+ " + BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length,bmo));
        if(bitmap != null) 
        {
            Log.d("wi11b031","wi11b031 asgfd+ ");// + BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length,bmo));
            
        	
        }
        return bitmap;
    }
    private String readExternalKey(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ExternalKey);
        String title = readTextHelper(parser);
        parser.require(XmlPullParser.END_TAG, ns, ExternalKey);
        return title;
    }
    
    

    //Hilfsmethode
    private String readTextHelper(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }
    
}
