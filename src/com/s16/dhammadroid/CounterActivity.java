package com.s16.dhammadroid;

import com.s16.drawing.TypefaceSpan;
import com.s16.widget.SlidingTab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.provider.Settings;

public class CounterActivity extends Activity {
	
	protected static final long VIBRATE_SHORT = 30;
	
	private static final String SAVE_STATE_ROUND = "counter_round";
	private static final String SAVE_STATE_COUNT = "counter_count";
	
	private Vibrator mVibrator;
	private TextView mTxtRound;
	private TextView mTxtCount;
	private int mRound = 0;
	private int mCount = 0;
	
	private View.OnClickListener mCountButtonClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mCount++;
			vibrate(VIBRATE_SHORT);
			updateCount();
		}
	};
	
	private SlidingTab.OnTriggerListener mTriggerListener = new SlidingTab.OnTriggerListener() {
		
		@Override
		public void onTrigger(View v, int whichHandle) {
			if (whichHandle == SlidingTab.OnTriggerListener.LEFT_HANDLE) {
				resetValue();
			} else {
				setValue();
			}
		}
		
		@Override
		public void onGrabbedStateChange(View v, int grabbedState) {
		}
	}; 
	
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
		setContentView(R.layout.activity_counter);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setActionBarTitle(Utility.ZawGyiDrawFix(getString(R.string.count_title)));
		
		TextView countButton = (TextView)findViewById(R.id.txtCountButton);
		countButton.setClickable(true);
		countButton.setOnClickListener(mCountButtonClick);
		
		SlidingTab slidingTab = (SlidingTab)findViewById(R.id.slideSetReset);
		//slidingTab.setLeftHintText(R.string.action_reset);
		//slidingTab.setRightHintText(R.string.action_set);
		slidingTab.setOnTriggerListener(mTriggerListener);
		
		slidingTab.setLeftTabResources(R.drawable.ic_slide_reset, R.drawable.jog_tab_target_gray, 
				R.drawable.jog_tab_bar_left_generic, R.drawable.jog_tab_left_generic);
		slidingTab.setRightTabResources(R.drawable.ic_slide_set, R.drawable.jog_tab_target_gray, 
				R.drawable.jog_tab_bar_right_generic, R.drawable.jog_tab_right_generic);
		
		mTxtRound = (TextView)findViewById(R.id.txtRoundValue);
		mTxtRound.setTypeface(Constants.getSegmentSevenTypeface(getContext()));
		mTxtCount = (TextView)findViewById(R.id.txtCountValue);
		mTxtCount.setTypeface(Constants.getSegmentSevenTypeface(getContext()));
		
		if (savedInstanceState != null) {
			mRound = savedInstanceState.getInt(SAVE_STATE_ROUND);
			mCount = savedInstanceState.getInt(SAVE_STATE_COUNT);
		} else {
			loadPrefrences();
		}
		
		updateCount();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (outState != null) {
			outState.putInt(SAVE_STATE_ROUND, mRound);
			outState.putInt(SAVE_STATE_COUNT, mCount);
		}
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
    protected void onStop() {
		savePrefrences();
        super.onStop();
    }
	
	private void loadPrefrences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		mRound = prefs.getInt(SAVE_STATE_ROUND, 0);
		mCount = prefs.getInt(SAVE_STATE_COUNT, 0);
	}
	
	private void savePrefrences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(SAVE_STATE_ROUND, mRound);
		editor.putInt(SAVE_STATE_COUNT, mCount);
		editor.commit();
	}
	
	private void updateCount() {
		if (mTxtRound != null && mTxtCount != null) {
			if (mCount >= 108) {
				mCount = 0;
				mRound++;
			}
			
			mTxtRound.setText(String.format("%04d", mRound));
			mTxtCount.setText(String.format("%03d", mCount));
		}
	}
	
	private void resetValue() {
		mCount = 0;
		mRound = 0;
		updateCount();
		vibrate(VIBRATE_SHORT);
	}
	
	private void setValue() {
		vibrate(VIBRATE_SHORT);
		
		final NumberPicker picker = new NumberPicker(getContext());
		picker.setMaxValue(9999);
		picker.setMinValue(0);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		params.leftMargin = (int)getResources().getDimension(R.dimen.activity_horizontal_margin);
		params.rightMargin = (int)getResources().getDimension(R.dimen.activity_horizontal_margin);
		picker.setLayoutParams(params);
		
		FrameLayout content = new FrameLayout(getContext());
		content.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
		content.addView(picker);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(R.string.action_set_round);
		builder.setView(content);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mRound = picker.getValue();
				updateCount();
			}
		});
		builder.create().show();
	}
	
	/**
     * Triggers haptic feedback.
     */
    private synchronized void vibrate(long duration) {
        final boolean hapticEnabled = Settings.System.getInt(
        		getContext().getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 1) != 0;
        if (hapticEnabled) {
            if (mVibrator == null) {
                mVibrator = (android.os.Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
            }
            mVibrator.vibrate(duration);
        }
    }
}
