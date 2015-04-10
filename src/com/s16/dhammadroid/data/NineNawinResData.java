package com.s16.dhammadroid.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.s16.dhammadroid.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class NineNawinResData {
	
	protected static final String TAG = NineNawinResData.class.getSimpleName();
	
	public static class ItemDetail {
		private String mName;
		private String mDay;
		private Calendar mDate;
		private String mValue;
		private String mRound;
		private int mDayValue;
		private boolean mIsDone;
		private boolean mIsVege;
		
		public ItemDetail() {
		}
		
		public ItemDetail(String name, String day, String value, String round) {
			mName = name;
			mDay = day;
			mValue = value;
			mRound = round;
		}
		
		public String getName() {
			return mName;
		}
		public void setName(String value) {
			mName = value;
		}

		public String getDay() {
			return mDay;
		}
		public void setDay(String value) {
			mDay = value;
		}
		
		public int getDayValue() {
			return mDayValue;
		}
		public void setDayValue(int value) {
			mDayValue = value;
		}

		public String getDateString() {
			if(mDate == null) return "";
			String dateStr = NineNawinResData.getDateString(mDate);
			
			if ((dateStr == null) || (dateStr == "")) {
				return "";
			}
			
			return " - " + dateStr;
		}
		public Calendar getDate() {
			return mDate;
		}
		public void setDate(Calendar value) {
			mDate = value;
		}

		public String getValue() {
			return mValue;
		}
		public void setValue(String value) {
			mValue = value;
		}

		public String getRound() {
			return mRound;
		}
		public void setRound(String value) {
			mRound = value;
		}
		
		public boolean getIsVege() {
			return mIsVege;
		}
		public void setIsVege(boolean value) {
			mIsVege = value;
		}
		
		public boolean getIsDone() {
			return mIsDone;
		}
		public void setIsDone(boolean value) {
			mIsDone = value;
		}
	}
	
	public static int LEVEL_COUNT = 9;
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
	
	private static String[] mSDateList;
	
	private static int[][] CODE_ARRAY;
	static {
		CODE_ARRAY = new int[][] { // Day, Text, Vege
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
	
	private static int[] RES_DAY_EN_ARRAY;
	static {
		RES_DAY_EN_ARRAY = new int[] {
				R.string.nn_day_1_en, R.string.nn_day_2_en
			  , R.string.nn_day_3_en, R.string.nn_day_4_en
			  , R.string.nn_day_5_en, R.string.nn_day_6_en
			  , R.string.nn_day_7_en };
	}
	
	private static int[] RES_DAY_MM_ARRAY;
	static {
		RES_DAY_MM_ARRAY = new int[] {
				R.string.nn_day_1_mm, R.string.nn_day_2_mm
			  , R.string.nn_day_3_mm, R.string.nn_day_4_mm
			  , R.string.nn_day_5_mm, R.string.nn_day_6_mm
			  , R.string.nn_day_7_mm };
	}
	
	private static int[] RES_VALUE_MM_ARRAY;
	static {
		RES_VALUE_MM_ARRAY = new int[] {
				R.string.nn_value_1_mm, R.string.nn_value_2_mm
			  , R.string.nn_value_3_mm, R.string.nn_value_4_mm
			  , R.string.nn_value_5_mm, R.string.nn_value_6_mm
			  , R.string.nn_value_7_mm, R.string.nn_value_8_mm
			  , R.string.nn_value_9_mm };
	}
	
	private static int[] RES_ROUND_MM_ARRAY;
	static {
		RES_ROUND_MM_ARRAY = new int[] {
				R.string.nn_round_1_mm, R.string.nn_round_2_mm
			  , R.string.nn_round_3_mm, R.string.nn_round_4_mm
			  , R.string.nn_round_5_mm, R.string.nn_round_6_mm
			  , R.string.nn_round_7_mm, R.string.nn_round_8_mm
			  , R.string.nn_round_9_mm };
	}
	
	private static int[] RES_LEVEL_ID_ARRAY;
	static {
		RES_LEVEL_ID_ARRAY = new int[] { 
				  R.string.nn_level_1_mm, R.string.nn_level_2_mm
				, R.string.nn_level_3_mm, R.string.nn_level_4_mm
				, R.string.nn_level_5_mm, R.string.nn_level_6_mm
				, R.string.nn_level_7_mm, R.string.nn_level_8_mm
				, R.string.nn_level_9_mm };
	}
	
	public static CharSequence[] getLevels(Context context) {
		Resources res = context.getResources();
		CharSequence[] array = new CharSequence[RES_LEVEL_ID_ARRAY.length]; 
		
		for(int i = 0; i < RES_LEVEL_ID_ARRAY.length; i++) {
			array[i] = res.getString(RES_LEVEL_ID_ARRAY[i]);
		}
		
		return array;
	}
	
	public static ItemDetail getItem(Context context, Calendar currentDate, boolean reloadPref) {
		if(currentDate == null) return null;
		
		if((reloadPref) || (mSDateList == null)) {
			loadPreferences(context);
		}
		
		int levelIndex = -1;
		Calendar startDate = null;
		for(int i = 0; i < mSDateList.length; i++) {
			String dateString = mSDateList[i];
			Calendar refDate = dateParse(dateString);
			if(refDate != null) {
				if(dateCompare(refDate, currentDate) >= 0) {
					refDate.roll(Calendar.DAY_OF_YEAR, 9);
					if(dateCompare(refDate, currentDate) < 0) {
						levelIndex = i;
						startDate = dateParse(dateString);
						//Utils.TRACE(TAG, "getItem, StartDate : " + dateString + " , Level : " + levelIndex);
					}
				}
			}
		}
		
		if((levelIndex > -1) && (startDate != null)) {
			Resources res = context.getResources();
			int index = (levelIndex * 9) + (currentDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR));
			//Utils.TRACE(TAG, "getItem, Level : " + levelIndex + " , Index : " + index);
			
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
	
	public static List<ItemDetail> getResults(Context context, int level, Calendar startDate) {
		List<ItemDetail> results = new ArrayList<ItemDetail>();
		Resources res = context.getResources();
		
		Calendar refDate = null;
		
		if(startDate != null) {
			refDate = dateClone(startDate);
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
	
	public static void loadPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		mSDateList = new String[9];
		mSDateList[0] = prefs.getString(NineNawinResData.LEVEL_1_SDATE_PREFS, null);
		mSDateList[1] = prefs.getString(NineNawinResData.LEVEL_2_SDATE_PREFS, null);
		mSDateList[2] = prefs.getString(NineNawinResData.LEVEL_3_SDATE_PREFS, null);
		mSDateList[3] = prefs.getString(NineNawinResData.LEVEL_4_SDATE_PREFS, null);
		mSDateList[4] = prefs.getString(NineNawinResData.LEVEL_5_SDATE_PREFS, null);
		mSDateList[5] = prefs.getString(NineNawinResData.LEVEL_6_SDATE_PREFS, null);
		mSDateList[6] = prefs.getString(NineNawinResData.LEVEL_7_SDATE_PREFS, null);
		mSDateList[7] = prefs.getString(NineNawinResData.LEVEL_8_SDATE_PREFS, null);
		mSDateList[8] = prefs.getString(NineNawinResData.LEVEL_9_SDATE_PREFS, null);
	}
	
	public static void savePreferences(Context context) {
		if (mSDateList == null) return;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putString(NineNawinResData.LEVEL_1_SDATE_PREFS, mSDateList[0]);
    	editor.putString(NineNawinResData.LEVEL_2_SDATE_PREFS, mSDateList[1]);
    	editor.putString(NineNawinResData.LEVEL_3_SDATE_PREFS, mSDateList[2]);
    	editor.putString(NineNawinResData.LEVEL_4_SDATE_PREFS, mSDateList[3]);
    	editor.putString(NineNawinResData.LEVEL_5_SDATE_PREFS, mSDateList[4]);
    	editor.putString(NineNawinResData.LEVEL_6_SDATE_PREFS, mSDateList[5]);
    	editor.putString(NineNawinResData.LEVEL_7_SDATE_PREFS, mSDateList[6]);
    	editor.putString(NineNawinResData.LEVEL_8_SDATE_PREFS, mSDateList[7]);
    	editor.putString(NineNawinResData.LEVEL_9_SDATE_PREFS, mSDateList[8]);
    	
    	editor.commit();
	}
	
	public static void reset() {
    	mSDateList = new String[9];
	}
	
	public static int getLastLevel() {
		Calendar current = Calendar.getInstance();
		for(int i=0; i<9; i++) {
			Calendar startDate = getStartDate(i);
			if (startDate == null) break;
			if (dateCompare(startDate, current) > -1) {
				startDate.roll(Calendar.DAY_OF_YEAR, 9);
				if (dateCompare(current, startDate) > -1) {
					return i+1;
				}
			}
		}
		return 1;
	}
	
	public static String[] getStartDateArray() {
		return mSDateList;
	}
	
	public static String getStartDateString(int index) {
		if ((index < 0) || (index > 8)) return null;
		return mSDateList[index];
	}
	
	public static int getDayValue(int level) {
		int index = ((level - 1) * 9);
		if (index < 0 || index >= CODE_ARRAY.length) return 0;
		return CODE_ARRAY[index][0] + 1;
	}
	
	public static Calendar getStartDate(int index) {
		if ((index < 0) || (index > 8)) return null;
		String dateString = mSDateList[index];
		if((dateString == null) || (dateString == "")) return null;
		return dateParse(dateString);
	}
	
	public static void setStartDate(Calendar value, int index) {
		if ((index < 0) || (index > 8)) return;
		if (value == null) {
			mSDateList = new String[9];
			return;
		}
		//mSDateList[index] = getDateString(value);
		Calendar cal = dateClone(value);
		while(index > 0) {
			cal.roll(Calendar.DAY_OF_YEAR, -9);
			index--;
		}
		for(int i=0;i<mSDateList.length;i++) {
			mSDateList[i] = getDateString(cal);
			cal.roll(Calendar.DAY_OF_YEAR, 9);
		}
	}
	
	public static boolean getIsToday(ItemDetail item) {
		Calendar rightNow = Calendar.getInstance(); 
		return (dateCompare(rightNow, item.getDate()) == 0);
	}
	
	private static String getDateString(Calendar value) {
		if(value == null) return "";
		int day = value.get(Calendar.DAY_OF_MONTH);
		int month = value.get(Calendar.MONTH) + 1;
		return ((day < 10) ? "0" + day : day) + "/" + ((month < 10) ? "0" + month : month) + "/" + value.get(Calendar.YEAR); 
	}
	
	private static Calendar dateClone(Calendar value) {
		if(value == null) return null;
		
		Calendar refDate = Calendar.getInstance();
		refDate.set(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE));
		
		return refDate;
	}
	
	private static Calendar dateParse(String value) {
		Calendar date = null;
		if((value == null) || (value.length() != 10)) return date;
		
		int day = Integer.valueOf(value.substring(0, 2));
		int month = Integer.valueOf(value.substring(3, 5));
		int year = Integer.valueOf(value.substring(6, 10));
		
		date = Calendar.getInstance();
		date.set(year, month - 1, day);
		
		return date;
	}
	
	private static int dateCompare(Calendar value1, Calendar value2) {
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
