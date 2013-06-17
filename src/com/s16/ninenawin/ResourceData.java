package com.s16.ninenawin;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class ResourceData {
	
	private static final String TAG = "ResourceData";

	static String LAST_LEVEL_PREFS = "last_level";
	static String IF_DONE_LIST_PREFS = "done_list";
	static String LEVEL_1_SDATE_PREFS = "sdate_level1";
	static String LEVEL_2_SDATE_PREFS = "sdate_level2";
	static String LEVEL_3_SDATE_PREFS = "sdate_level3";
	static String LEVEL_4_SDATE_PREFS = "sdate_level4";
	static String LEVEL_5_SDATE_PREFS = "sdate_level5";
	static String LEVEL_6_SDATE_PREFS = "sdate_level6";
	static String LEVEL_7_SDATE_PREFS = "sdate_level7";
	static String LEVEL_8_SDATE_PREFS = "sdate_level8";
	static String LEVEL_9_SDATE_PREFS = "sdate_level9";
	
	private static int mLastLevel;
	private static String[] mSDateList;
	
	static int[][] CODE_ARRAY;
	static {
		CODE_ARRAY = new int[][] {
				{ 1, 1, 0 }, { 2, 8, 0 }, { 3, 3, 0 }, { 4, 6, 0 }, { 5, 4, 1 }, { 6, 2, 0 }, { 0, 5, 0 }, { 1, 0, 0 }, { 2, 7, 0 },
				{ 3, 2, 0 }, { 4, 0, 0 }, { 5, 4, 0 }, { 6, 7, 0 }, { 0, 5, 1 }, { 1, 3, 0 }, { 2, 6, 0 }, { 3, 1, 0 }, { 4, 8, 0 },
				{ 5, 3, 0 }, { 6, 1, 0 }, { 0, 5, 0 }, { 1, 8, 0 }, { 2, 6, 1 }, { 3, 4, 0 }, { 4, 7, 0 }, { 5, 2, 0 }, { 6, 0, 0 },
				{ 0, 4, 0 }, { 1, 2, 0 }, { 2, 6, 0 }, { 3, 0, 0 }, { 4, 7, 1 }, { 5, 5, 0 }, { 6, 8, 0 }, { 0, 3, 0 }, { 1, 1, 0 },
				{ 2, 5, 0 }, { 3, 3, 0 }, { 4, 7, 0 }, { 5, 1, 0 }, { 6, 8, 1 }, { 0, 6, 0 }, { 1, 0, 0 }, { 2, 4, 0 }, { 3, 2, 0 },
				{ 4, 6, 0 }, { 5, 4, 0 }, { 6, 8, 0 }, { 0, 2, 0 }, { 1, 0, 1 }, { 2, 7, 0 }, { 3, 1, 0 }, { 4, 5, 0 }, { 5, 3, 0 },
				{ 6, 7, 0 }, { 0, 5, 0 }, { 1, 0, 0 }, { 2, 3, 0 }, { 3, 1, 1 }, { 4, 8, 0 }, { 5, 2, 0 }, { 6, 6, 0 }, { 0, 4, 0 },
				{ 1, 8, 0 }, { 2, 6, 0 }, { 3, 1, 0 }, { 4, 4, 0 }, { 5, 2, 1 }, { 6, 0, 0 }, { 0, 3, 0 }, { 1, 7, 0 }, { 2, 5, 0 },
				{ 3, 0, 0 }, { 4, 7, 0 }, { 5, 2, 0 }, { 6, 5, 0 }, { 0, 3, 1 }, { 1, 1, 0 }, { 2, 4, 0 }, { 3, 8, 0 }, { 4, 6, 0 },
			};
	}
	
	static int[] RES_DAY_EN_ARRAY;
	static {
		RES_DAY_EN_ARRAY = new int[] {
				R.string.day_1_en, R.string.day_2_en
			  , R.string.day_3_en, R.string.day_4_en
			  , R.string.day_5_en, R.string.day_6_en
			  , R.string.day_7_en };
	}
	
	static int[] RES_DAY_MM_ARRAY;
	static {
		RES_DAY_MM_ARRAY = new int[] {
				R.string.day_1_mm, R.string.day_2_mm
			  , R.string.day_3_mm, R.string.day_4_mm
			  , R.string.day_5_mm, R.string.day_6_mm
			  , R.string.day_7_mm };
	}
	
	static int[] RES_VALUE_MM_ARRAY;
	static {
		RES_VALUE_MM_ARRAY = new int[] {
				R.string.value_1_mm, R.string.value_2_mm
			  , R.string.value_3_mm, R.string.value_4_mm
			  , R.string.value_5_mm, R.string.value_6_mm
			  , R.string.value_7_mm, R.string.value_8_mm
			  , R.string.value_9_mm };
	}
	
	static int[] RES_ROUND_MM_ARRAY;
	static {
		RES_ROUND_MM_ARRAY = new int[] {
				R.string.round_1_mm, R.string.round_2_mm
			  , R.string.round_3_mm, R.string.round_4_mm
			  , R.string.round_5_mm, R.string.round_6_mm
			  , R.string.round_7_mm, R.string.round_8_mm
			  , R.string.round_9_mm };
	}
	
	static int[] RES_LEVEL_ID_ARRAY;
	static {
		RES_LEVEL_ID_ARRAY = new int[] { 
				  R.string.level_1_mm, R.string.level_2_mm
				, R.string.level_3_mm, R.string.level_4_mm
				, R.string.level_5_mm, R.string.level_6_mm
				, R.string.level_7_mm, R.string.level_8_mm
				, R.string.level_9_mm };
	}
	
	static CharSequence[] getLevels(Context context) {
		Resources res = context.getResources();
		CharSequence[] array = new CharSequence[RES_LEVEL_ID_ARRAY.length]; 
		
		for(int i = 0; i < RES_LEVEL_ID_ARRAY.length; i++) {
			array[i] = res.getString(RES_LEVEL_ID_ARRAY[i]);
		}
		
		return array;
	}
	
	static ItemDetail getItem(Context context, Calendar currentDate, boolean reloadPref) {
		if(currentDate == null) return null;
		
		if((reloadPref) || (mSDateList == null)) {
			loadPreferences(context);
		}
		
		int levelIndex = -1;
		Calendar startDate = null;
		for(int i = 0; i < mSDateList.length; i++) {
			String dateString = mSDateList[i];
			Calendar refDate = Utils.dateParse(dateString);
			if(refDate != null) {
				if(Utils.dateCompare(refDate, currentDate) >= 0) {
					refDate.roll(Calendar.DAY_OF_YEAR, 9);
					if(Utils.dateCompare(refDate, currentDate) < 0) {
						levelIndex = i;
						startDate = Utils.dateParse(dateString);
						Utils.TRACE(TAG, "getItem, StartDate : " + dateString + " , Level : " + levelIndex);
					}
				}
			}
		}
		
		if((levelIndex > -1) && (startDate != null)) {
			Resources res = context.getResources();
			int index = (levelIndex * 9) + (currentDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR));
			Utils.TRACE(TAG, "getItem, Level : " + levelIndex + " , Index : " + index);
			
			int[] codeList = CODE_ARRAY[index];
			ItemDetail detail = new ItemDetail();
			
			detail.setName(res.getString(RES_DAY_EN_ARRAY[codeList[0]]));
			detail.setDay(res.getString(RES_DAY_MM_ARRAY[codeList[0]]));
			detail.setDayValue(codeList[0] + 1);
			detail.setValue(res.getString(RES_VALUE_MM_ARRAY[codeList[1]]));
			detail.setRound(res.getString(RES_ROUND_MM_ARRAY[codeList[1]]));
			detail.setIsVege(codeList[2] == 1);
			detail.setDate(currentDate);
			
			return detail;
		}
		
		return null;
	}
	
	static ArrayList<ItemDetail> getResults(Context context, int level, Calendar startDate) {
		ArrayList<ItemDetail> results = new ArrayList<ItemDetail>();
		Resources res = context.getResources();
		
		Calendar refDate = null;
		
		if(startDate != null) {
			refDate = Utils.dateClone(startDate);
		}
		
		for(int i = 0; i < 9; i++) {
			int index = ((level - 1) * 9) + i;
			
			if(index >= CODE_ARRAY.length) break;
			
			int[] codeList = CODE_ARRAY[index];	
			
			ItemDetail detail = new ItemDetail();
			
			detail.setName(res.getString(RES_DAY_EN_ARRAY[codeList[0]]));
			detail.setDay(res.getString(RES_DAY_MM_ARRAY[codeList[0]]));
			detail.setDayValue(codeList[0] + 1);
			detail.setValue(res.getString(RES_VALUE_MM_ARRAY[codeList[1]]));
			detail.setRound(res.getString(RES_ROUND_MM_ARRAY[codeList[1]]));
			detail.setIsVege(codeList[2] == 1);
			
			if(startDate != null) {
				Calendar date = Calendar.getInstance();
				date.set(refDate.get(Calendar.YEAR), refDate.get(Calendar.MONTH), refDate.get(Calendar.DATE));
				detail.setDate(date);
				refDate.roll(Calendar.DAY_OF_YEAR, true);
			}
			
			detail.setIsDone(false);
			
			results.add(detail);
		}
		
		return results;
	}
	
	static void loadPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		mLastLevel = prefs.getInt(ResourceData.LAST_LEVEL_PREFS, 1);
		
		mSDateList = new String[9];
		mSDateList[0] = prefs.getString(ResourceData.LEVEL_1_SDATE_PREFS, null);
		mSDateList[1] = prefs.getString(ResourceData.LEVEL_2_SDATE_PREFS, null);
		mSDateList[2] = prefs.getString(ResourceData.LEVEL_3_SDATE_PREFS, null);
		mSDateList[3] = prefs.getString(ResourceData.LEVEL_4_SDATE_PREFS, null);
		mSDateList[4] = prefs.getString(ResourceData.LEVEL_5_SDATE_PREFS, null);
		mSDateList[5] = prefs.getString(ResourceData.LEVEL_6_SDATE_PREFS, null);
		mSDateList[6] = prefs.getString(ResourceData.LEVEL_7_SDATE_PREFS, null);
		mSDateList[7] = prefs.getString(ResourceData.LEVEL_8_SDATE_PREFS, null);
		mSDateList[8] = prefs.getString(ResourceData.LEVEL_9_SDATE_PREFS, null);
	}
	
	static void savePreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putInt(ResourceData.LAST_LEVEL_PREFS, mLastLevel);
    	
    	editor.putString(ResourceData.LEVEL_1_SDATE_PREFS, mSDateList[0]);
    	editor.putString(ResourceData.LEVEL_2_SDATE_PREFS, mSDateList[1]);
    	editor.putString(ResourceData.LEVEL_3_SDATE_PREFS, mSDateList[2]);
    	editor.putString(ResourceData.LEVEL_4_SDATE_PREFS, mSDateList[3]);
    	editor.putString(ResourceData.LEVEL_5_SDATE_PREFS, mSDateList[4]);
    	editor.putString(ResourceData.LEVEL_6_SDATE_PREFS, mSDateList[5]);
    	editor.putString(ResourceData.LEVEL_7_SDATE_PREFS, mSDateList[6]);
    	editor.putString(ResourceData.LEVEL_8_SDATE_PREFS, mSDateList[7]);
    	editor.putString(ResourceData.LEVEL_9_SDATE_PREFS, mSDateList[8]);
    	
    	editor.commit();
	}
	
	static void reset() {
    	mLastLevel = -1;
    	mSDateList = new String[9];
	}
	
	static int getLastLevel() {
		return mLastLevel;
	}
	static void setLastLevel(int value) {
		mLastLevel = value;
	}
	
	static String[] getStartDateArray() {
		return mSDateList;
	}
	static String getStartDateString(int index) {
		if ((index < 0) || (index > 8)) return null;
		return mSDateList[index];
	}
	static Calendar getStartDate(int index) {
		if ((index < 0) || (index > 8)) return null;
		String dateString = mSDateList[index];
		if((dateString == null) || (dateString == "")) return null;
		return Utils.dateParse(dateString);
	}
	static void setStartDate(Calendar value, int index) {
		if ((index < 0) || (index > 8)) return;
		mSDateList[index] = Utils.getDateString(value);
	}
}
