package com.s16.dhammadroid.preference;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import com.s16.dhammadroid.R;
import com.transitionseverywhere.AutoTransition;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.Preference;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmPreference extends Preference {

	protected static final String TAG = AlarmPreference.class.getSimpleName(); 
	
	private static final Uri NO_RINGTONE_URI = Uri.EMPTY;
	
	private static final float EXPAND_DECELERATION = 1f;
    private static final float COLLAPSE_DECELERATION = 0.7f;
    
    private static final int ANIMATION_DURATION = 300;
    private static final int EXPAND_DURATION = 300;
    private static final int COLLAPSE_DURATION = 250;

    private static final int ROTATE_180_DEGREE = 180;
    private static final float ALARM_ELEVATION = 8f;
    private static final float TINTED_LEVEL = 0.09f;
    
    // Number if days in the week.
    private static final int DAYS_IN_A_WEEK = 7;

    // Value when all days are set
    private static final int ALL_DAYS_SET = 0x7f;

    // Value when no days are set
    private static final int NO_DAYS_SET = 0;
    
    //private static final int REQUEST_CODE_RINGTONE = 1;
    private static final int DELAY_MS_SELECTION_PLAYED = 300;
    
    private static final int SELECTED_COLOR = 0xffffffff;
    
	private String[] mShortWeekDayStrings;
    private String[] mLongWeekDayStrings;

    private OnShowTimePicker mOnShowTimePicker;
    
    // This determines the order in which it is shown and processed in the UI.
    private final int[] DAY_ORDER = new int[] {
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
    };

    public static interface OnShowTimePicker {
        public boolean onShowTimePicker(int hourOfDay, int minute, boolean is24HourView);
    }
    
    public static final class Alarm {
    	private int id;
        private boolean enabled;
        private int hour;
        private int minutes;
        private int daysOfWeekBitSet;
        private boolean vibrate;
        private String label;
        private Uri alert;
        
        public Alarm() {
        	final Calendar calendar = Calendar.getInstance();
        	hour = calendar.get(Calendar.HOUR_OF_DAY);
        	minutes = calendar.get(Calendar.MINUTE);
        }
        
        private Alarm(String value) {
        	if (!TextUtils.isEmpty(value)) {
        		Uri uri = Uri.parse(value);
        		for(String key : uri.getQueryParameterNames()) {
        			String param = uri.getQueryParameter(key);
        			if (param != null) {
        				if ("id".equals(key)) {
        					id = Integer.parseInt(param);
        				} else if ("enabled".equals(key)) {
        					enabled = param.equals("true");
        				} else if ("hour".equals(key)) {
        					hour = Integer.parseInt(param);
        				} else if ("minutes".equals(key)) {
        					minutes = Integer.parseInt(param);
        				} else if ("daysOfWeekBitSet".equals(key)) {
        					daysOfWeekBitSet = Integer.parseInt(param);
        				} else if ("vibrate".equals(key)) {
        					vibrate = param.equals("true");
        				} else if ("label".equals(key)) {
        					label = param;
        				} 
        			}
        		}
        		
        		String uriString = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
        		alert = Uri.parse(uriString);
        	}
        }
        
        public int getId() {
        	return id;
        }
        
        public boolean isEnabled() {
        	return enabled;
        }
        public void setEnabled(boolean value) {
        	enabled = value;
        }
        
        public int getHour() {
        	return hour;
        }
        public void setHour(int value) {
        	hour = value;
        }
        
        public int getMinutes() {
        	return minutes;
        }
        public void setMinutes(int value) {
        	minutes = value;
        }
        
        public boolean isVibrateEnabled() {
        	return vibrate;
        }
        public void setVibrateEnabled(boolean value) {
        	vibrate = value;
        }
        
        public String getLabel() {
        	return label;
        }
        
        public Uri getRingtoneUri() {
        	return alert;
        }
        public void setRingtoneUri(Uri value) {
        	alert = value;
        }
        
        private int convertBitIndexToDay(int bitIndex) {
            return (bitIndex + 1) % DAYS_IN_A_WEEK + 1;
        }
        
        private int convertDayToBitIndex(int day) {
            return (day + 5) % DAYS_IN_A_WEEK;
        }
        
        /**
         * Enables or disable certain days of the week.
         *
         * @param daysOfWeek Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, etc.
         */
        private void setDaysOfWeek(boolean value, int ... daysOfWeek) {
            for (int day : daysOfWeek) {
                setDaysOfWeekBit(convertDayToBitIndex(day), value);
            }
        }
        
        private void setDaysOfWeekBit(int bitIndex, boolean set) {
            if (set) {
            	daysOfWeekBitSet |= (1 << bitIndex);
            } else {
            	daysOfWeekBitSet &= ~(1 << bitIndex);
            }
        }
        
        private boolean isBitEnabled(int bitIndex) {
            return ((daysOfWeekBitSet & (1 << bitIndex)) > 0);
        }
        
        private void clearAllDays() {
        	daysOfWeekBitSet = NO_DAYS_SET;
        }
        
        public int getDaysOfWeekBitSet() {
        	return daysOfWeekBitSet;
        }
        
        public String getDaysOfWeekString(Context context, boolean showNever, boolean forAccessibility) {
        	
        	if (daysOfWeekBitSet == NO_DAYS_SET) {
        		return showNever ? context.getText(R.string.alarm_never).toString() : "";
        	}
        	
        	if (daysOfWeekBitSet == ALL_DAYS_SET) {
        		return context.getText(R.string.alarm_every_day).toString();
        	}
        	
        	// count selected days
            int dayCount = 0;
            int bitSet = daysOfWeekBitSet;
            while (bitSet > 0) {
                if ((bitSet & 1) == 1) dayCount++;
                bitSet >>= 1;
            }
        	
        	// short or long form?
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] dayList = (forAccessibility || dayCount <= 1) ?
                    dfs.getWeekdays() :
                    dfs.getShortWeekdays();
                    
            StringBuilder ret = new StringBuilder();
            // selected days
            for (int bitIndex = 0; bitIndex < DAYS_IN_A_WEEK; bitIndex++) {
                if ((daysOfWeekBitSet & (1 << bitIndex)) != 0) {
                    ret.append(dayList[convertBitIndexToDay(bitIndex)]);
                    dayCount -= 1;
                    if (dayCount > 0) ret.append(context.getText(R.string.alarm_day_concat));
                }
            }
            return ret.toString();
        }
        
        public HashSet<Integer> getSetDays() {
            final HashSet<Integer> result = new HashSet<Integer>();
            for (int bitIndex = 0; bitIndex < DAYS_IN_A_WEEK; bitIndex++) {
                if (isBitEnabled(bitIndex)) {
                    result.add(convertBitIndexToDay(bitIndex));
                }
            }
            return result;
        }
        
        public int[] getDaysOfWeek() {
        	int[] result = new int[DAYS_IN_A_WEEK];
        	for (int bitIndex = 0; bitIndex < DAYS_IN_A_WEEK; bitIndex++) {
                if (isBitEnabled(bitIndex)) {
                    result[bitIndex] = convertBitIndexToDay(bitIndex);
                } else {
                	result[bitIndex] = -1;
                }
            }
        	return result;
        }

        public boolean isRepeating() {
            return daysOfWeekBitSet != NO_DAYS_SET;
        }

        /**
         * Return the time when a alarm should fire.
         *
         * @return the time
         */
        public Calendar getAlarmTime() {
            Calendar time = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, time.get(Calendar.YEAR));
            calendar.set(Calendar.MONTH, time.get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            // If we are still behind the passed in time, then add a day
            if (calendar.getTimeInMillis() <= time.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            return calendar;
        }
        
        @Override
        public String toString() {
        	if (alert != null) {
        		Uri.Builder builder = alert.buildUpon();
        		builder.appendQueryParameter("id", String.valueOf(id));
        		builder.appendQueryParameter("enabled", (enabled ? "true" : "false"));
        		builder.appendQueryParameter("hour", String.valueOf(hour));
        		builder.appendQueryParameter("minutes", String.valueOf(minutes));
        		builder.appendQueryParameter("daysOfWeekBitSet", String.valueOf(daysOfWeekBitSet));
        		builder.appendQueryParameter("vibrate", (vibrate ? "true" : "false"));
        		builder.appendQueryParameter("label", label);
        		return builder.build().toString();
        	}
        	return "";
        }
    }
    
    public static Alarm createAlarm(String urlString) {
    	if (!TextUtils.isEmpty(urlString)) {
    		return new Alarm(urlString);
    	}
    	return null;
    }
    
    private final Runnable mScrollRunnable = new Runnable() {
        @Override
        public void run() {
        	if (mAlarmView != null && mParentList != null) {
        		Rect rect = new Rect(mAlarmView.getLeft(), mAlarmView.getTop(), mAlarmView.getRight(), mAlarmView.getBottom());
        		mParentList.requestChildRectangleOnScreen(mAlarmView, rect, false);
        	}
        }
    };
    
    private Runnable mPlayRingToneCallback = new Runnable() {
		
		@Override
		public void run() {
			if (mSampleRingtonePos > -1) {
        		Ringtone ringtone = mRingtoneManager.getRingtone(mSampleRingtonePos);
        		if (ringtone != null) {
        			ringtone.play();
        		}
        	}
		}
	};
    
    private Alarm mAlarm;
    private Activity mActivity; 
	
	private LinearLayout mAlarmItem;
	private TextView mTitle;
	private TextTime mClock;
	private TextView mTomorrowLabel;
	private Switch mOnoff;
	private TextView mLabel;
	private TextView mDaysOfWeek;
	private View mExpandArea;
	private View mSummary;
	private CheckBox mRepeat;
	private LinearLayout mRepeatDays;
	private Button[] mDayButtons = new Button[7];
	private CheckBox mVibrate;
	private TextView mRingtone;
	private View mHairLine;
	private View mArrow;
	private View mCollapseExpandArea;
	private View mAlarmView;
	private ListView mParentList;
	
	private boolean mHasVibrator;
	private Typeface mRobotoThin;
	private Typeface mRobotoNormal;
	private int mCollapseExpandHeight;
	private Interpolator mExpandInterpolator;
    private Interpolator mCollapseInterpolator;
    private Transition mRepeatTransition;
    private boolean mExpanded;
    
    private RingtoneManager mRingtoneManager;
    private Cursor mRingtoneCursor;
    private Handler mHandler;
    private int mSampleRingtonePos;
	
	public AlarmPreference(Context context) {
		super(context);
		init(context);
	}
	
	public AlarmPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public AlarmPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	protected Activity getActivity() {
		return mActivity;
	}

	private void init(Context context) {
		setLayoutResource(R.layout.layout_alarm_preference);
		
		mHasVibrator = ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator();
		
		mAlarm = new Alarm();
		mAlarm.alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		
		DateFormatSymbols dfs = new DateFormatSymbols();
        mShortWeekDayStrings = getShortWeekdays();
        mLongWeekDayStrings = dfs.getWeekdays();
        
        mRobotoThin = Typeface.create("sans-serif-thin", Typeface.NORMAL);
        mRobotoNormal = Typeface.create("sans-serif", Typeface.NORMAL);
        mCollapseExpandHeight = (int)context.getResources().getDimension(R.dimen.alarm_collapse_expand_height);
        mExpandInterpolator = new DecelerateInterpolator(EXPAND_DECELERATION);
        mCollapseInterpolator = new DecelerateInterpolator(COLLAPSE_DECELERATION);
        
        mRepeatTransition = new AutoTransition();
        mRepeatTransition.setDuration(ANIMATION_DURATION / 2);
        mRepeatTransition.setInterpolator(new AccelerateDecelerateInterpolator());
        
        mHandler = new Handler();
        mRingtoneManager = new RingtoneManager(context);
        mRingtoneManager.setType(RingtoneManager.TYPE_ALARM);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		mAlarmView = super.onCreateView(parent);
		
		if (parent != null) {
			if (parent.getContext() instanceof Activity) {
				mActivity = (Activity)parent.getContext();
			}
			
			if (parent instanceof ListView) {
				mParentList = (ListView)parent;
			}
		}
		
		mAlarmItem = (LinearLayout) mAlarmView.findViewById(R.id.alarm_item);
		mTitle = (TextView) mAlarmView.findViewById(R.id.title);
        mTomorrowLabel = (TextView) mAlarmView.findViewById(R.id.tomorrowLabel);
        mClock = (TextTime) mAlarmView.findViewById(R.id.digital_clock);
        mClock.setTypeface(mRobotoThin);
        mOnoff = (Switch) mAlarmView.findViewById(R.id.onoff);
        mOnoff.setTypeface(mRobotoNormal);
        mLabel = (TextView) mAlarmView.findViewById(R.id.label);
        mDaysOfWeek = (TextView) mAlarmView.findViewById(R.id.daysOfWeek);
        mSummary = mAlarmView.findViewById(R.id.summary);
        mExpandArea = mAlarmView.findViewById(R.id.expand_area);
        mHairLine = mAlarmView.findViewById(R.id.hairline);
        mArrow = mAlarmView.findViewById(R.id.arrow);
        mRepeat = (CheckBox) mAlarmView.findViewById(R.id.repeat_onoff);
        mRepeatDays = (LinearLayout) mAlarmView.findViewById(R.id.repeat_days);
        mCollapseExpandArea = mAlarmView.findViewById(R.id.collapse_expand);
        
        // Build button for each day.
        int textColor = getContext().getResources().getColor(R.color.alarm_clock_gray);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        for (int i = 0; i < 7; i++) {
            final Button dayButton = new Button(getContext());
            dayButton.setLayoutParams(params);
            dayButton.setBackgroundResource(R.drawable.toggle_circle_light);
            dayButton.setGravity(Gravity.CENTER);
            dayButton.setTextColor(textColor);
            dayButton.setText(mShortWeekDayStrings[i]);
            dayButton.setContentDescription(mLongWeekDayStrings[DAY_ORDER[i]]);
            mRepeatDays.addView(dayButton);
            mDayButtons[i] = dayButton;
        }
        
        mVibrate = (CheckBox) mAlarmView.findViewById(R.id.vibrate_onoff);
        mRingtone = (TextView) mAlarmView.findViewById(R.id.choose_ringtone);
		
		return mAlarmView;
	}
	
	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		
		restorePersistedValue();
		mAlarm.id = getKey().hashCode();
		mAlarm.label = getTitle() != null ? getTitle().toString() : "";
		mTitle.setText(getTitle());
		
		// We must unset the listener first because this maybe a recycled view so changing the
        // state would affect the wrong alarm.
        mOnoff.setOnCheckedChangeListener(null);
        mOnoff.setChecked(mAlarm.enabled);
        
        if (!isEnabled()) {
        	setAlarmItemBackgroundAndElevation(mAlarmItem, true /* expanded */);
            setDigitalTimeAlpha(true);
            mOnoff.setEnabled(false);
        } else {
        	mOnoff.setEnabled(true);
            setAlarmItemBackgroundAndElevation(mAlarmItem, false /* expanded */);
            setDigitalTimeAlpha(mOnoff.isChecked());
        }
        
        mClock.setFormat((int)getContext().getResources().getDimension(R.dimen.alarm_label_size));
        mClock.setTime(mAlarm.hour, mAlarm.minutes);
        mClock.setClickable(true);
        mClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	showTimeEditDialog(mAlarm);
            	expandAlarm(true);
            	mAlarmItem.post(mScrollRunnable);
            }
        });
        
        final CompoundButton.OnCheckedChangeListener onOffListener =
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked != mAlarm.enabled) {
                    if (!isAlarmExpanded(mAlarm)) {
                        // Only toggle this when alarm is collapsed
                        setDigitalTimeAlpha(checked);
                    }
                    mAlarm.enabled = checked;
                    updateAlarm(mAlarm, mAlarm.enabled);
                }
            }
        };
        
        if (mAlarm.isRepeating()) {
        	mTomorrowLabel.setVisibility(View.GONE);
        } else {
        	mTomorrowLabel.setVisibility(View.VISIBLE);
        	final Resources resources = getContext().getResources();
        	final String labelText = isTomorrow(mAlarm) ?
                    resources.getString(R.string.alarm_tomorrow) :
                    resources.getString(R.string.alarm_today);
            mTomorrowLabel.setText(labelText);
        }
        mOnoff.setOnCheckedChangeListener(onOffListener);
		
		boolean expanded = isAlarmExpanded(mAlarm);
		mExpandArea.setVisibility(expanded? View.VISIBLE : View.GONE);
		mLabel.setVisibility(expanded? View.GONE : View.VISIBLE);
		mSummary.setVisibility(expanded? View.GONE : View.VISIBLE);
		mHairLine.setVisibility(expanded ? View.GONE : View.VISIBLE);
		mArrow.setRotation(expanded ? ROTATE_180_DEGREE : 0);
		
		mArrow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isAlarmExpanded(mAlarm)) {
					// Is expanded, make collapse call.
					collapseAlarm(true);
				} else {
					// Is collapsed, make expand call.
					expandAlarm(true);
				}
			}
		});
		
		// Set the repeat text or leave it blank if it does not repeat.
        final String daysOfWeekStr = mAlarm.getDaysOfWeekString(getContext(), false, false);
        if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
            mDaysOfWeek.setText(daysOfWeekStr);
            mDaysOfWeek.setContentDescription(mAlarm.getDaysOfWeekString(getContext(), false, true));
            mDaysOfWeek.setVisibility(View.VISIBLE);
            mDaysOfWeek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expandAlarm(true);
                    mAlarmItem.post(mScrollRunnable);
                }
            });

        } else {
            mDaysOfWeek.setVisibility(View.GONE);
        }
		
		if (expanded) {
			expandAlarm(false);
		}
		
		mAlarmItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAlarmExpanded(mAlarm)) {
                    collapseAlarm(true);
                } else {
                    expandAlarm(true);
                }
            }
        });
	}
	
	@Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }
	
	@Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValueObj) {
        String defaultValue = (String) defaultValueObj;
        if (restorePersistedValue) {
            return;
        }
        
        // If we are setting to the default value, we should persist it.
        if (!TextUtils.isEmpty(defaultValue)) {
        	mAlarm = new Alarm(defaultValue);
        }
	}
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		if (mAlarm != null) {
			mAlarm.label = title == null ? "" : title.toString();
		}
		if (mTitle != null) {
			mTitle.setText(title);
		}
	}
	
	@Override
	public void setSummary(CharSequence summary) {
		super.setSummary(summary);
		if (mLabel != null) {
			mLabel.setText(summary);
		}
	}
	
	public boolean isAlarmEnabled() {
		if (mAlarm == null) return false;
		return mAlarm.enabled;
	}
	
	public int getAlarmHour() {
		if (mAlarm == null) return 0;
		return mAlarm.hour;
	}
	
	public int getAlarmMinutes() {
		if (mAlarm == null) return 0;
		return mAlarm.minutes;
	}
	
	public boolean isRepeating() {
		if (mAlarm == null) return false;
		return mAlarm.isRepeating();
	}
	
	public int[] getRepeatDays() {
		if (mAlarm == null) return null;
		return mAlarm.getDaysOfWeek();
	}
	
	public boolean isVibrateEnabled() {
		if (mAlarm == null) return false;
		return mAlarm.vibrate;
	}
	
	public Uri getAlarmRingtoneUri() {
		if (mAlarm == null) return null;
		return mAlarm.alert;
	}

    public void setTime(int hourOfDay, int minute) {
        updateTimeSet(hourOfDay, minute);
    }

    public void setOnShowTimePicker(OnShowTimePicker listener) {
        mOnShowTimePicker = listener;
    }

	private String[] getShortWeekdays() {
        final String[] shortWeekdays = new String[7];
        final SimpleDateFormat format = new SimpleDateFormat("EEEEE");
        // Create a date (2014/07/20) that is a Sunday
        long aSunday = new GregorianCalendar(2014, Calendar.JULY, 20).getTimeInMillis();
        for (int day = 0; day < 7; day++) {
            shortWeekdays[day] = format.format(new Date(aSunday + day * DateUtils.DAY_IN_MILLIS));
        }
        return shortWeekdays;
    }
	
	private boolean isAlarmExpanded(Alarm alarm) {
		return mExpanded;
	}
	
	private boolean isTomorrow(Alarm alarm) {
        final Calendar now = Calendar.getInstance();
        final int alarmHour = alarm.hour;
        final int currHour = now.get(Calendar.HOUR_OF_DAY);
        return alarmHour < currHour ||
                    (alarmHour == currHour && alarm.minutes <= now.get(Calendar.MINUTE));
    }
	
	private void launchRingTonePicker(Alarm alarm) {
		Uri oldRingtone = NO_RINGTONE_URI.equals(alarm.alert) ? null : alarm.alert;
		if (mRingtoneCursor == null) {
			mRingtoneCursor = mRingtoneManager.getCursor();
		}
		
		if (mRingtoneCursor != null && mRingtoneCursor.moveToFirst()) {
			int checkedItem = mRingtoneManager.getRingtonePosition(oldRingtone);
			final DialogInterface.OnClickListener clickListener =  new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mRingtoneManager.stopPreviousRingtone();
					
					if (which == DialogInterface.BUTTON_NEGATIVE) {
						dialog.cancel();
					} else if (which == DialogInterface.BUTTON_POSITIVE) {
						if (mSampleRingtonePos > -1) {
							Uri uri = mRingtoneManager.getRingtoneUri(mSampleRingtonePos);
							mAlarm.alert = uri;
							updateAlarm(mAlarm, true);
							updateRingTone(uri);
						}
					} else {
						playRingTone(which);
					}
				}
			};
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setSingleChoiceItems(mRingtoneCursor, checkedItem, MediaStore.Audio.Media.TITLE, clickListener);
			builder.setTitle(R.string.alarm_ringtone);
			builder.setNegativeButton(android.R.string.cancel, clickListener);
			builder.setPositiveButton(android.R.string.ok, clickListener);
			
			builder.create().show();
		}
	}
	
	private void playRingTone(int position) {
		mRingtoneManager.stopPreviousRingtone();
		mSampleRingtonePos = position;
		mHandler.removeCallbacks(mPlayRingToneCallback);
		mHandler.postDelayed(mPlayRingToneCallback, DELAY_MS_SELECTION_PLAYED);
	}
	
	private void showTimeEditDialog(Alarm alarm) {
        boolean isHandel = false;
        if (mOnShowTimePicker != null) {
            isHandel = mOnShowTimePicker.onShowTimePicker(alarm.hour, alarm.minutes,
                    DateFormat.is24HourFormat(getContext()));
        }

        if (!isHandel) {
            final TimePickerDialog.OnTimeSetListener callBack = new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    updateTimeSet(hourOfDay, minute);
                }
            };

            TimePickerDialog dialog = new TimePickerDialog(getContext(), callBack, alarm.hour, alarm.minutes,
                    DateFormat.is24HourFormat(getContext()));
            dialog.show();
        }
	}
	
	private void restorePersistedValue() {
		String value = getPersistedString(null);
		if (!TextUtils.isEmpty(value)) {
			mAlarm = new Alarm(value);
		}
	}
	
	private void updateTimeSet(int hourOfDay, int minute) {
		mAlarm.hour = hourOfDay;
		mAlarm.minutes = minute;
		mAlarm.enabled = true;
		
		mClock.setTime(mAlarm.hour, mAlarm.minutes);
		mOnoff.setChecked(mAlarm.enabled);
		updateAlarm(mAlarm, true);
	}
	
	private void updateAlarm(Alarm alarm, boolean popToast) {
		persistString(alarm != null ? alarm.toString() : "");
        if (popToast) {
            popAlarmSetToast(alarm.getAlarmTime().getTimeInMillis());
        }
	}

    private String formatToast(long timeInMillis) {
        long delta = timeInMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" :
                (days == 1) ? getContext().getString(R.string.alarm_day) :
                        getContext().getString(R.string.alarm_days, Long.toString(days));

        String minSeq = (minutes == 0) ? "" :
                (minutes == 1) ? getContext().getString(R.string.alarm_minute) :
                        getContext().getString(R.string.alarm_minutes, Long.toString(minutes));

        String hourSeq = (hours == 0) ? "" :
                (hours == 1) ? getContext().getString(R.string.alarm_hour) :
                        getContext().getString(R.string.alarm_hours, Long.toString(hours));

        boolean dispDays = days > 0;
        boolean dispHour = hours > 0;
        boolean dispMinute = minutes > 0;

        int index = (dispDays ? 1 : 0) | (dispHour ? 2 : 0) | (dispMinute ? 4 : 0);
        String[] formats = getContext().getResources().getStringArray(R.array.alarm_set);

        return String.format(formats[index], daySeq, hourSeq, minSeq);
    }

    private void popAlarmSetToast(long timeInMillis) {
        String toastText = formatToast(timeInMillis);
        Toast toast = Toast.makeText(getContext(), toastText, Toast.LENGTH_LONG);
        toast.show();
    }
	
	private void updateRingTone(Uri ringtoneUri) {
		final String ringtoneName;
        if (NO_RINGTONE_URI.equals(ringtoneUri)) {
        	ringtoneName = getContext().getResources().getString(R.string.alarm_silent_summary);
        } else {
        	ringtoneName = getRingToneTitle(ringtoneUri);
        }
        mRingtone.setText(ringtoneName);
        mRingtone.setContentDescription(getContext().getResources().getString(R.string.alarm_ringtone_description) + " " + mRingtone);
	}
	
	private String getRingToneTitle(Uri uri) {
        // This is slow because a media player is created during Ringtone object creation.
        Ringtone ringTone = RingtoneManager.getRingtone(getContext(), uri);
        String title = ringTone.getTitle(getContext());
        return title;
    }
	
	private void setAlarmItemBackgroundAndElevation(LinearLayout layout, boolean expanded) {
        if (expanded) {
            layout.setBackgroundColor(getTintedBackgroundColor());
            ViewCompat.setElevation(layout, ALARM_ELEVATION);
        } else {
            layout.setBackgroundResource(R.drawable.alarm_background_normal);
            ViewCompat.setElevation(layout, 0f);
        }
    }
	
	private int getSelectedColor() {
		return SELECTED_COLOR;
	}

    private int getTintedBackgroundColor() {
        final int c = getSelectedColor();
        final int red = Color.red(c) + (int) (TINTED_LEVEL * (255 - Color.red(c)));
        final int green = Color.green(c) + (int) (TINTED_LEVEL * (255 - Color.green(c)));
        final int blue = Color.blue(c) + (int) (TINTED_LEVEL * (255 - Color.blue(c)));
        return Color.rgb(red, green, blue);
    }
    
    // Sets the alpha of the digital time display. This gives a visual effect
    // for enabled/disabled and expanded/collapsed alarm while leaving the
    // on/off switch more visible
    private void setDigitalTimeAlpha(boolean enabled) {
    	float alpha = enabled ? 1f : 0.69f;
        mClock.setAlpha(alpha);
    }
    
    private void updateDaysOfWeekButtons(Alarm alarm) {
        HashSet<Integer> setDays = alarm.getSetDays();
        for (int i = 0; i < 7; i++) {
            if (setDays.contains(DAY_ORDER[i])) {
                turnOnDayOfWeek(i);
            } else {
                turnOffDayOfWeek(i);
            }
        }
    }

    private void turnOffDayOfWeek(int dayIndex) {
        final Button dayButton = mDayButtons[dayIndex];
        dayButton.setActivated(false);
        dayButton.setTextColor(getContext().getResources().getColor(R.color.alarm_clock_gray));
    }

    private void turnOnDayOfWeek(int dayIndex) {
        final Button dayButton = mDayButtons[dayIndex];
        dayButton.setActivated(true);
        dayButton.setTextColor(getSelectedColor());
    }
    
    private void bindExpandArea() {
    	// Views in here are not bound until the item is expanded.
    	
        if (mAlarm.isRepeating()) {
            mRepeat.setChecked(true);
            mRepeatDays.setVisibility(View.VISIBLE);
        } else {
            mRepeat.setChecked(false);
            mRepeatDays.setVisibility(View.GONE);
        }
        
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Animate the resulting layout changes.
                TransitionManager.beginDelayedTransition(mParentList, mRepeatTransition);

                final boolean checked = ((CheckBox)view).isChecked();
                if (checked) {
                    // Show days
                    mRepeatDays.setVisibility(View.VISIBLE);

                    if (!mAlarm.isRepeating()) {
                        mAlarm.setDaysOfWeek(true, DAY_ORDER);
                    }
                    updateDaysOfWeekButtons(mAlarm);
                } else {
                    // Hide days
                    mRepeatDays.setVisibility(View.GONE);

                    // Remove all repeat days
                    mAlarm.clearAllDays();
                }

                updateAlarm(mAlarm, false);
            }
        });
        
        updateDaysOfWeekButtons(mAlarm);
        for (int i = 0; i < 7; i++) {
            final int buttonIndex = i;

            mDayButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final boolean isActivated = mDayButtons[buttonIndex].isActivated();
                    mAlarm.setDaysOfWeek(!isActivated, DAY_ORDER[buttonIndex]);
                    if (!isActivated) {
                        turnOnDayOfWeek(buttonIndex);
                    } else {
                        turnOffDayOfWeek(buttonIndex);

                        // See if this was the last day, if so, un-check the repeat box.
                        if (!mAlarm.isRepeating()) {
                            // Animate the resulting layout changes.
                            TransitionManager.beginDelayedTransition(mParentList, mRepeatTransition);

                            mRepeat.setChecked(false);
                            mRepeatDays.setVisibility(View.GONE);
                        }
                    }
                    updateAlarm(mAlarm, false);
                }
            });
        }
        
        if (!mHasVibrator) {
            mVibrate.setVisibility(View.INVISIBLE);
        } else {
            mVibrate.setVisibility(View.VISIBLE);
            if (!mAlarm.vibrate) {
                mVibrate.setChecked(false);
            } else {
                mVibrate.setChecked(true);
            }
        }
        
        mVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean checked = ((CheckBox) v).isChecked();
                mAlarm.vibrate = checked;
                updateAlarm(mAlarm, false);
            }
        });
        
        updateRingTone(mAlarm.alert);
        mRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRingTonePicker(mAlarm);
            }
        });
        
        mLabel.setText(getSummary());
    }
	
	private void expandAlarm(boolean animate) {
		if (mExpanded) return;
		mExpanded = true;
		
		bindExpandArea();
		
		// Save the starting height so we can animate from this value.
        final int startingHeight = mAlarmItem.getHeight();
        
        // Set the expand area to visible so we can measure the height to animate to.
        setAlarmItemBackgroundAndElevation(mAlarmItem, true /* expanded */);
        
		mExpandArea.setVisibility(View.VISIBLE);
		mLabel.setVisibility(View.VISIBLE);
		
		// Show digital time in full-opaque when expanded, even when alarm is disabled
        setDigitalTimeAlpha(true /* enabled */);
        
		if (!animate || mParentList == null) {
			// Set the "end" layout and don't do the animation.
			mArrow.setRotation(ROTATE_180_DEGREE);
			return;
		}
		
		// Add an onPreDrawListener, which gets called after measurement but before the draw.
        // This way we can check the height we need to animate to before any drawing.
        // Note the series of events:
        //  * expandArea is set to VISIBLE, which causes a layout pass
        //  * the view is measured, and our onPreDrawListener is called
        //  * we set up the animation using the start and end values.
        //  * the height is set back to the starting point so it can be animated down.
        //  * request another layout pass.
        //  * return false so that onDraw() is not called for the single frame before
        //    the animations have started.
		final ViewTreeObserver observer = mParentList.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				// We don't want to continue getting called for every listview drawing.
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }
                // Calculate some values to help with the animation.
                final int endingHeight = mAlarmItem.getHeight();
                final int distance = endingHeight - startingHeight;
                final int collapseHeight = mCollapseExpandArea.getHeight();

                // Set the height back to the start state of the animation.
                mAlarmItem.getLayoutParams().height = startingHeight;
                // To allow the expandArea to glide in with the expansion animation, set a
                // negative top margin, which will animate down to a margin of 0 as the height
                // is increased.
                // Note that we need to maintain the bottom margin as a fixed value (instead of
                // just using a listview, to allow for a flatter hierarchy) to fit the bottom
                // bar underneath.
                FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)mExpandArea.getLayoutParams();
                expandParams.setMargins(0, -distance, 0, collapseHeight);
                mAlarmItem.requestLayout();
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                        .setDuration(EXPAND_DURATION);
                animator.setInterpolator(mExpandInterpolator);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						Float value = (Float) animation.getAnimatedValue();

                        // For each value from 0 to 1, animate the various parts of the layout.
                        mAlarmItem.getLayoutParams().height = (int)(value * distance + startingHeight);
                        FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)mExpandArea.getLayoutParams();
                        expandParams.setMargins(0, (int) -((1 - value) * distance), 0, collapseHeight);
                        mArrow.setRotation(ROTATE_180_DEGREE * value);
                        mSummary.setAlpha(1 - value);
                        mHairLine.setAlpha(1 - value);

                        mAlarmItem.requestLayout();
					}
				});
                
                // Set everything to their final values when the animation's done.
                animator.addListener(new Animator.AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator animation) {}
					
					@Override
					public void onAnimationRepeat(Animator animation) {}
					
					@Override
					public void onAnimationEnd(Animator animation) {
						// Set it back to wrap content since we'd explicitly set the height.
                        mAlarmItem.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                        mArrow.setRotation(ROTATE_180_DEGREE);
                        mLabel.setVisibility(View.VISIBLE);
                        mSummary.setVisibility(View.GONE);
                        mHairLine.setVisibility(View.GONE);
					}
					
					@Override
					public void onAnimationCancel(Animator animation) {}
				});
                animator.start();
                
				return false;
			}
		});
	}
	
	private void bindNoneExpandArea() {
		if (mAlarm.isRepeating()) {
        	mTomorrowLabel.setVisibility(View.GONE);
        } else {
        	mTomorrowLabel.setVisibility(View.VISIBLE);
        	final Resources resources = getContext().getResources();
        	final String labelText = isTomorrow(mAlarm) ?
                    resources.getString(R.string.alarm_tomorrow) :
                    resources.getString(R.string.alarm_today);
            mTomorrowLabel.setText(labelText);
        }
		
		// Set the repeat text or leave it blank if it does not repeat.
        final String daysOfWeekStr = mAlarm.getDaysOfWeekString(getContext(), false, false);
        if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
            mDaysOfWeek.setText(daysOfWeekStr);
            mDaysOfWeek.setContentDescription(mAlarm.getDaysOfWeekString(getContext(), false, true));
            mDaysOfWeek.setVisibility(View.VISIBLE);
        } else {
            mDaysOfWeek.setVisibility(View.GONE);
        }
	}
	
	private void collapseAlarm(boolean animate) {
		if (!mExpanded) return;
		mExpanded = false;
		
		bindNoneExpandArea();
		
		// Save the starting height so we can animate from this value.
        final int startingHeight = mAlarmItem.getHeight();
        
        // Set the expand area to gone so we can measure the height to animate to.
        setAlarmItemBackgroundAndElevation(mAlarmItem, false /* expanded */);
		mExpandArea.setVisibility(View.GONE);
		setDigitalTimeAlpha(mOnoff.isChecked());
		
		if (!animate || mParentList == null) {
			// Set the "end" layout and don't do the animation.
			mArrow.setRotation(0);
			mHairLine.setTranslationY(0);
			return;
		}
		
		// Add an onPreDrawListener, which gets called after measurement but before the draw.
        // This way we can check the height we need to animate to before any drawing.
        // Note the series of events:
        //  * expandArea is set to GONE, which causes a layout pass
        //  * the view is measured, and our onPreDrawListener is called
        //  * we set up the animation using the start and end values.
        //  * expandArea is set to VISIBLE again so it can be shown animating.
        //  * request another layout pass.
        //  * return false so that onDraw() is not called for the single frame before
        //    the animations have started.
		final ViewTreeObserver observer = mParentList.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(this);
                }

                // Calculate some values to help with the animation.
                final int endingHeight = mAlarmItem.getHeight();
                final int distance = endingHeight - startingHeight;

                // Re-set the visibilities for the start state of the animation.
                mExpandArea.setVisibility(View.VISIBLE);
                mLabel.setVisibility(View.GONE);
                mSummary.setVisibility(View.VISIBLE);
                mHairLine.setVisibility(View.VISIBLE);
                mSummary.setAlpha(1);

                // Set up the animator to animate the expansion.
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f)
                        .setDuration(COLLAPSE_DURATION);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        Float value = (Float) animator.getAnimatedValue();

                        // For each value from 0 to 1, animate the various parts of the layout.
                        mAlarmItem.getLayoutParams().height = (int)(value * distance + startingHeight);
                        FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)mExpandArea.getLayoutParams();
                        expandParams.setMargins(0, (int) (value * distance), 0, mCollapseExpandHeight);
                        mArrow.setRotation(ROTATE_180_DEGREE * (1 - value));
                        mLabel.setAlpha(value);
                        mSummary.setAlpha(value);
                        mHairLine.setAlpha(value);

                        mAlarmItem.requestLayout();
                    }
                });
                animator.setInterpolator(mCollapseInterpolator);
                // Set everything to their final values when the animation's done.
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Set it back to wrap content since we'd explicitly set the height.
                        mAlarmItem.getLayoutParams().height = LayoutParams.WRAP_CONTENT;

                        FrameLayout.LayoutParams expandParams = (FrameLayout.LayoutParams)mExpandArea.getLayoutParams();
                        expandParams.setMargins(0, 0, 0, mCollapseExpandHeight);

                        mExpandArea.setVisibility(View.GONE);
                        mArrow.setRotation(0);
                    }
                });
                animator.start();

                return false;
            }
        });
	}
}
