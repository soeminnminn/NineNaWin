package com.s16.dhammadroid.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.s16.dhammadroid.R;

import android.content.Context;

public class DhammaDataParser {
	
	public static List<Category> CATEGORIES = new ArrayList<Category>();
	public static List<Entry> ENTRIES = new ArrayList<Entry>();
	
	public static class Category {
		public String name;
		public String title;
		public List<Integer> entries = new ArrayList<Integer>();
	}
	
	public static class Entry {
		public String category_name;
		
		public String name;
		public String title;
		public String soundUrl;
		public String soundFile;
		public String body;
		
		public String description_title;
		public String description_body;
	}
	
	public static void parse(Context context) {
		final InputStream stream = context.getResources().openRawResource(R.raw.data);
		try {
        	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            
			final XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(stream, "utf-8");
			
			int eventType = xpp.getEventType();
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	String name = xpp.getName();
	        	switch (eventType){
	            	case XmlPullParser.START_TAG:
	            		if (name.equalsIgnoreCase("categories")) {
	            			parseCategories(xpp);
	            		} else if (name.equalsIgnoreCase("entry")) {
	            			parseEntryItem(xpp);
	            		}
	            		break;
	            		
	            	case XmlPullParser.TEXT:
	                    break;
	                    
	            	case XmlPullParser.END_TAG:
	            		if (name.equalsIgnoreCase("entries")) {
	            			return;
	            		}
	            		break;
	        	}
	        	eventType = xpp.next();
	        }
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void parseEntryItem(XmlPullParser xpp) {
		String text = null;
		Entry entry = null;
		try {
			boolean soundStart = false;
			boolean descriptionStart = false;
			
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
	        	String name = xpp.getName();
	        	switch (eventType){
	            	case XmlPullParser.START_TAG:
	            		if (name.equalsIgnoreCase("entry")) {
	            			entry = new Entry();
	            			entry.name = "entry_" + ENTRIES.size();
	            			entry.category_name = xpp.getAttributeValue(null, "category");
	            		} else if (name.equalsIgnoreCase("sound")) {
	            			soundStart = true;
	            		} else if (name.equalsIgnoreCase("description")) {
	            			descriptionStart = true;
	            		}
	            		break;
	            		
	            	case XmlPullParser.TEXT:
	                    text = xpp.getText();
	                    if (text != null) {
	                    	text = text.trim();
	                    }
	                    break;
	                    
	            	case XmlPullParser.END_TAG:
	            		if (soundStart && name.equalsIgnoreCase("url")) {
	            			entry.soundUrl = text;
	            		} else if (soundStart && name.equalsIgnoreCase("fileName")) {
	            			entry.soundFile = text;
	            		} else if (name.equalsIgnoreCase("sound")) {
	            			soundStart = false;
	            		} else if (descriptionStart && name.equalsIgnoreCase("title")) {
	            			entry.description_title = text;
	            		} else if (descriptionStart && name.equalsIgnoreCase("body")) {
	            			entry.description_body = text;
	            		} else if (name.equalsIgnoreCase("description")) {
	            			descriptionStart = false;
	            		} else if (name.equalsIgnoreCase("title")) {
	            			entry.title = text;
	            		} else if (name.equalsIgnoreCase("body")) {
	            			entry.body = text;
	            		} else if (name.equalsIgnoreCase("entry")) {
	            			ENTRIES.add(entry);
	            			int categoryIndex = findCategory(entry.category_name);
	            			if (categoryIndex > -1) {
	            				CATEGORIES.get(categoryIndex).entries.add(Integer.valueOf(ENTRIES.size() - 1));
	            			}
	            			return;
	            		}
	            		break;
	        	}
	        	eventType = xpp.next();
	        }
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void parseCategories(XmlPullParser xpp) {
		String text = null;
		Category category = null;
		try {
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
	        	String name = xpp.getName();
	        	switch (eventType){
	            	case XmlPullParser.START_TAG:
	            		if (name.equalsIgnoreCase("category")) {
	            			category = new Category();
	            			category.name = xpp.getAttributeValue(null, "name");
	            		}
	            		break;
	            		
	            	case XmlPullParser.TEXT:
	                    text = xpp.getText();
	                    if (text != null) {
	                    	text = text.trim();
	                    }
	                    break;
	                    
	            	case XmlPullParser.END_TAG:
	            		if (name.equalsIgnoreCase("categories")) {
	            			return;
	            		} else if (name.equalsIgnoreCase("category")) {
	            			category.title = text;
	            			CATEGORIES.add(category);
	            		}
	            		break;
	        	}
	        	eventType = xpp.next();
	        }
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static int findCategory(String name) {
		for(int i=0; i<CATEGORIES.size(); i++) {
			Category item = CATEGORIES.get(i);
			if (item != null && item.name != null && item.name.equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}
}
