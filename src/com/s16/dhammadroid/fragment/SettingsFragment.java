package com.s16.dhammadroid.fragment;

import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment {

	static final String PREFS_ABOUT = "prefs_about"; 
	
	private Context mContext;
	
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
        
        Preference prefsAbout = findPreference(PREFS_ABOUT);
        try {
        	PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			prefsAbout.setSummary(pInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        
        prefsAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Utility.showAboutDialog(mContext);
				return false;
			}
        });
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (mContext == null) {
			mContext = inflater.getContext();
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
