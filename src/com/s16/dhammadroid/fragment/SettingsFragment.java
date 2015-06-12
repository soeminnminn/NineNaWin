package com.s16.dhammadroid.fragment;

import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment
	implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Context mContext;
	private ListPreference mFontSizePreference;
	
	public SettingsFragment() {
		
	}
	
	public SettingsFragment(Context context) {
		mContext = context;
	}
	
	protected Context getContext() {
		return mContext;
	}
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.settings);
        
        mFontSizePreference = (ListPreference)findPreference(Common.PREFS_FONT_SIZE);
        
        final SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        updateFontSizeSummary(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
    public void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (mContext == null) {
			mContext = inflater.getContext();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(Common.PREFS_FONT_SIZE)) {
			updateFontSizeSummary(sharedPreferences);
		}
	}
	
	private void updateFontSizeSummary(SharedPreferences sharedPreferences) {
		String defFontValue = getResources().getString(R.string.prefs_font_size_default);
		String value = sharedPreferences.getString(Common.PREFS_FONT_SIZE, defFontValue);
		String summary = getResources().getStringArray(R.array.prefs_font_size_text)[mFontSizePreference.findIndexOfValue(value)];
		mFontSizePreference.setSummary(summary);
	}
}
