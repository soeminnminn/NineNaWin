package com.s16.dhammadroid;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.widget.TextView;

public class Common {
	
	public static final String MIME_TYPE = "text/html";
    public static final String ENCODING = "utf-8";
    
    public static final String PREFS_ABOUT = "prefs_about"; 
    public static final String PREFS_FONT_SIZE = "prefs_font_size";
    
    public static final String URL_DEFAULT = "about:blank";
	public static final String URL_DEFINITION = "file:///android_asset/definition.html";
	public static final String URL_NOT_FOUND = "file:///android_asset/not_found.html";
	
	public static final String ANDROID_DATA = "/Android/data/";
	public static final String AUDIO_DIR = "/DhammaDroidAudio";
	private static String DATA_FOLDER;
	
	private static Typeface ZAWGYI_TYPEFACE;
	private static Typeface SEGMENT_SEVEN_TYPEFACE;
	private static Typeface HOLO_ICON_TYPEFACE;
	private static Typeface MATERIAL_ICON_TYPEFACE;
	
	private static float[] FONT_SIZE_VALUES;
	
	public static File getDataFolder(Context context) {
		if(DATA_FOLDER == null) {
			DATA_FOLDER = ANDROID_DATA + context.getPackageName() + File.separator  + "files/";
		}
		
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + DATA_FOLDER;
			File folder = new File(path);
			if (!folder.exists()) {
				if (folder.mkdirs()) {
					File noMedia = new File(folder.getPath(), ".nomedia");
					if (!noMedia.exists()) {
						try {
							noMedia.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return folder;
	    }
		
		return null;
	}
	
	public static boolean usedDetailWebview() {
		return false;
	}
	
	public static File getAudioFolder(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_DIR;
			File folder = new File(path);
			if (!folder.exists()) {
				if (folder.mkdirs()) {
					File noMedia = new File(folder.getPath(), ".nomedia");
					if (!noMedia.exists()) {
						try {
							noMedia.createNewFile();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			return folder;
	    }
		
		return null;
	}
	
	public static Typeface getZawgyiTypeface(Context context) {
		if (ZAWGYI_TYPEFACE == null) {
			ZAWGYI_TYPEFACE = Typeface.createFromAsset(context.getAssets(), "fonts/zawgyi.ttf");
		}
		return ZAWGYI_TYPEFACE;
	}
	
	public static Typeface getSegmentSevenTypeface(Context context) {
		if (SEGMENT_SEVEN_TYPEFACE == null) {
			SEGMENT_SEVEN_TYPEFACE = Typeface.createFromAsset(context.getAssets(), "fonts/segment_seven.ttf");
		}
		return SEGMENT_SEVEN_TYPEFACE;
	}
	
	public static Typeface getHoloIconTypeface(Context context) {
		if (HOLO_ICON_TYPEFACE == null) {
			HOLO_ICON_TYPEFACE = Typeface.createFromAsset(context.getAssets(), "fonts/holo_icon.ttf");
		}
		return HOLO_ICON_TYPEFACE;
	}
	
	public static Typeface getMaterialIconTypeface(Context context) {
		if (MATERIAL_ICON_TYPEFACE == null) {
			MATERIAL_ICON_TYPEFACE = Typeface.createFromAsset(context.getAssets(), "fonts/material_icon.ttf");
		}
		return MATERIAL_ICON_TYPEFACE;
	}
	
	public static void setViewTextSize(TextView view) {
		if (view == null) return;
		
		final Context context = view.getContext();
		final Resources res = context.getResources();
		
		if (FONT_SIZE_VALUES == null) {
			FONT_SIZE_VALUES = new float[] {
					res.getDimension(R.dimen.detail_text_size_small),
					res.getDimension(R.dimen.detail_text_size_medium),
					res.getDimension(R.dimen.detail_text_size_large)
			};
		}
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String defFontValue = res.getString(R.string.prefs_font_size_default);
		int fontSizeVal = Integer.parseInt(preferences.getString(Common.PREFS_FONT_SIZE, defFontValue));
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, FONT_SIZE_VALUES[fontSizeVal]);
	}
}
