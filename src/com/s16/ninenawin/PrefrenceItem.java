package com.s16.ninenawin;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class PrefrenceItem extends Preference implements PreferenceManager.OnActivityDestroyListener {

	private Preference.OnPreferenceClickListener mOnClickListener;
	
	public PrefrenceItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PrefrenceItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setOnClickListener(Preference.OnPreferenceClickListener listener) {
		mOnClickListener = listener;
	}
	
	@Override
	protected void onClick() {
		if(mOnClickListener != null) {
			mOnClickListener.onPreferenceClick(this);
		}
	}

	@Override
	public void onActivityDestroy() {
	}
}
