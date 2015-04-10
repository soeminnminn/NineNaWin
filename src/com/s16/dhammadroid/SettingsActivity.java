package com.s16.dhammadroid;

import com.s16.dhammadroid.fragment.SettingsFragment;
import com.s16.drawing.TypefaceSpan;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;

public class SettingsActivity extends Activity {
	
	protected Context getContext() {
		return this;
	}
	
	protected void setActionBarTitle(CharSequence title) {
		if (title != null) {
			SpannableString s = new SpannableString(title);
			s.setSpan(new TypefaceSpan(getContext(), "zawgyi.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			getActionBar().setTitle(s);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState == null) {
			SettingsFragment fragment = new SettingsFragment(getContext());
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.prefsContainer, fragment);
			transaction.commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				finish();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
