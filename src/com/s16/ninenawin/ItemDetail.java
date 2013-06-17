package com.s16.ninenawin;

import java.util.Calendar;

public class ItemDetail {
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
		String dateStr = Utils.getDateString(mDate);
		
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
