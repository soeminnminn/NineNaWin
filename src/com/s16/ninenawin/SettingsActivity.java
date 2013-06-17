package com.s16.ninenawin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

	private static String PREF_RESET_DATA = "reset_data";
	private static String PREF_VERSION_NAME = "version_name";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		
		Preference version = this.findPreference(PREF_VERSION_NAME);
		PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version.setSummary(pInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		PrefrenceItem prefReset = (PrefrenceItem)this.findPreference(PREF_RESET_DATA);
		prefReset.setOnClickListener(this);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        // Setup the initial values        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Set up a listener whenever a key changes            
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes            
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);    
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		Log.i("SettingsActivity", PREF_RESET_DATA);
		
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(SettingsActivity.this);
		dlgBuilder.setTitle(getText(R.string.comfirm_title));
		dlgBuilder.setMessage(getText(R.string.comfirm_clear_message));
		
		dlgBuilder.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ResourceData.reset();
				ResourceData.savePreferences(getBaseContext());
			}
		});
		dlgBuilder.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		AlertDialog dialog = dlgBuilder.create();
		dialog.show();
		
		return false;
	}

}
