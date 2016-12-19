package com.s16.dhammadroid.fragment;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.s16.dhammadroid.R;
import com.s16.dhammadroid.preference.AlarmPreference;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

/**
 * Created by SMM on 10/21/2016.
 */
public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        AlarmPreference.OnShowTimePicker, TimePickerDialog.OnTimeSetListener {

    private AlarmPreference mAlarmPreference;

    public SettingsFragment() {

    }

    protected SharedPreferences getSharedPreferences() {
        return getPreferenceManager().getSharedPreferences();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.settings);

        mAlarmPreference = (AlarmPreference)findPreference("prefs_alarm_notification");
        mAlarmPreference.setOnShowTimePicker(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public boolean onShowTimePicker(int hourOfDay, int minute, boolean is24HourView) {
        if (Build.VERSION.SDK_INT < 21) {
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, hourOfDay, minute, is24HourView);
            timePickerDialog.show(getChildFragmentManager(), "TimePickerDialog");
            return true;
        }
        return false;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mAlarmPreference.setTime(hourOfDay, minute);
    }
}
