package com.s16.ninenawin;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class Utils {
	
	private static Typeface mTypeface;
	
	public static Typeface getTypeFace(Context context) {
		if(mTypeface == null) {
			mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/zawgyi.ttf");
		}
		
		return mTypeface;
	}
	
	public static void TRACE(String tag, String message) {
		if(BuildConfig.DEBUG) {
			Log.i(tag, message);
		}
	}
	
	public static Calendar dateClone(Calendar value) {
		if(value == null) return null;
		
		Calendar refDate = Calendar.getInstance();
		refDate.set(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE));
		
		return refDate;
	}
	
	public static Calendar dateParse(String value) {
		Calendar date = null;
		if((value == null) || (value.length() != 10)) return date;
		
		int day = Integer.valueOf(value.substring(0, 2));
		int month = Integer.valueOf(value.substring(3, 5));
		int year = Integer.valueOf(value.substring(6, 10));
		
		date = Calendar.getInstance();
		date.set(year, month - 1, day);
		
		return date;
	}
	
	public static String getDateString(Calendar value) {
		if(value == null) return "";
		int day = value.get(Calendar.DAY_OF_MONTH);
		int month = value.get(Calendar.MONTH) + 1;
		return ((day < 10) ? "0" + day : day) + "/" + ((month < 10) ? "0" + month : month) + "/" + value.get(Calendar.YEAR); 
	}
	
	public static int dateCompare(Calendar value1, Calendar value2) {
		if((value1 == null) && (value2 == null)) return 0;
		if(value2 == null) return -1;
		if(value1 == null) return 1;
		
		int day1 = value1.get(Calendar.DAY_OF_MONTH);
		int month1 = value1.get(Calendar.MONTH);
		int year1 = value1.get(Calendar.YEAR);
		
		int day2 = value2.get(Calendar.DAY_OF_MONTH);
		int month2 = value2.get(Calendar.MONTH);
		int year2 = value2.get(Calendar.YEAR);
		
		if(year1 > year2) return -1;
		if(year1 < year2) return 1;
		
		if(year1 == year2) {
			if(month1 > month2) return -1;
			if(month1 < month2) return 1;
			
			if(month1 == month2) {
				if(day1 > day2) return -1;
				if(day1 < day2) return 1;
				
				if(day1 == day2) return 0;
			}
		}
		
		return 0;
	}
	
}
