package com.s16.ninenawin;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DateSelectorDialog extends Dialog implements View.OnClickListener {

	private static CharSequence[] MONTH_NAMES;
	static {
		MONTH_NAMES = new CharSequence[] {
				"January", "February", "March", 
				"April", "May", "June", 
				"July", "August", "September", 
				"October", "November", "December"
		};
	}
	
	private final String mDividerTag = "DIVIDER";
	private CharSequence mTitle;
	private Calendar mCurrent;
	private int mSelectday;
	private Calendar mSelectedDate;
	private ArrayList<Calendar> mArrDate;
	
	private TextView mMonthTitle;
	private LinearLayout mHolder;
	private OnClickListener mOnClickListener;
	
	private int mItemWidth;
	private int mItemHeight;
	
	public DateSelectorDialog(Context context, int selectDay, Calendar selectedDate) {
		super(context);
		
		mSelectday = selectDay;
		mSelectedDate = Utils.dateClone(selectedDate);
		
		Calendar rightNow = Calendar.getInstance();
		int month = rightNow.get(Calendar.MONTH); 
		
		mCurrent = Calendar.getInstance();
		mCurrent.set(rightNow.get(Calendar.YEAR), month, 1);
		
		final Resources res = context.getResources();
		final DisplayMetrics dm = res.getDisplayMetrics();
		mItemWidth = Math.round(res.getFraction(R.fraction.dialog_item_width, dm.widthPixels, dm.widthPixels));
		mItemHeight = Math.round(res.getFraction(R.fraction.dialog_item_height, dm.heightPixels, dm.heightPixels));
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.date_selector);
        
        TextView titleView = (TextView)findViewById(R.id.textViewTitle);
        titleView.setText(mTitle);
        
        mMonthTitle = (TextView)findViewById(R.id.textViewHeader); 
        mHolder = (LinearLayout)findViewById(R.id.layoutSelectionHolder);
        
        ImageButton btnPrevious = (ImageButton)findViewById(R.id.imageButtonLeft);
        btnPrevious.setTag("P");
        btnPrevious.setOnClickListener(this);
        
        ImageButton btnNext = (ImageButton)findViewById(R.id.imageButtonRight);
        btnNext.setTag("N");
        btnNext.setOnClickListener(this);
        
        Button btnCancel = (Button)findViewById(R.id.dialogButtonNegative);
        btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DateSelectorDialog.this.cancel();
			}
		});
        
        createList();
	}

	@Override
	public void onClick(View view) {
		Object tagObj = view.getTag();
		if(tagObj == null) {
			if(mOnClickListener != null) {
				int which = getChildIndex(view, mHolder);
				mOnClickListener.onClick(this, which);
			}
			dismiss();
		} else {
			String tag = tagObj.toString(); 
			if(tag == "P") {
				showPrevious();
			} else if(tag == "N") {
				showNext();
			}
		}
	}
	
	@Override
	public void setTitle(CharSequence value) {
		super.setTitle(value);
		mTitle = value;
	}
	
	public void setOnClickListener(OnClickListener onClickListener) {
		mOnClickListener = onClickListener;	
	}
	
	public Calendar get(int index) {
		if(mArrDate == null) return null;
		if((index < 0) || (index >= mArrDate.size())) return null;
		
		return mArrDate.get(index);
	}
	
	private void createList() {
		if(mMonthTitle == null) return;
		if(mHolder == null) return;
		
		Calendar first = Utils.dateClone(mCurrent);
		int monthNow = first.get(Calendar.MONTH);
		mMonthTitle.setText(getMonth(monthNow));
    	
    	int dayOfWeek = first.get(Calendar.DAY_OF_WEEK);
    	while(dayOfWeek != mSelectday) {
    		first.roll(Calendar.DAY_OF_YEAR, true);
    		dayOfWeek = first.get(Calendar.DAY_OF_WEEK);
    	}
    	
    	mArrDate = new ArrayList<Calendar>();
    	int month = first.get(Calendar.MONTH);
    	while(month == monthNow) {
    		mArrDate.add(Utils.dateClone(first));
    		first.roll(Calendar.DAY_OF_YEAR, 7);
    		month = first.get(Calendar.MONTH);
    	}
    	
    	final Resources res = getContext().getResources();
		mHolder.removeAllViews();
    	for(int i = 0; i < mArrDate.size(); i++) {
    		if(i > 0) {
				TextView divider = new TextView(getContext(), null);
				divider.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1));
				divider.setBackgroundResource(R.color.grey_40);
				divider.setTag(mDividerTag);
				divider.setClickable(false);
				divider.setFocusable(false);
				mHolder.addView(divider);
			}
    		
    		Button button = new Button(getContext(), null);
    		if(Utils.dateCompare(mArrDate.get(i), mSelectedDate) == 0) {
    			button.setTextColor(res.getColor(R.color.white));
    			button.setBackgroundResource(R.drawable.holo_actionbar_button_selected);
    		} else {
    			button.setTextColor(res.getColor(R.color.bright_foreground_holo_light));
    			button.setBackgroundResource(R.drawable.holo_actionbar_button);
    		}
    		button.setLayoutParams(new LayoutParams(mItemWidth, mItemHeight));
    		button.setTypeface(Utils.getTypeFace(getContext()));
    		button.setText(Utils.getDateString(mArrDate.get(i)));
    		button.setOnClickListener(this);
    		
    		mHolder.addView(button);
    		mHolder.measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    	}
	}
	
	private CharSequence getMonth(int index) {
		if((index < 0) || (index > 11)) return null;
		return MONTH_NAMES[index];
	}
	
	private void showPrevious() {
		int monthNow = mCurrent.get(Calendar.MONTH);
		
		if(monthNow == 0) {
			mCurrent.roll(Calendar.YEAR, false);
			mCurrent.roll(Calendar.MONTH, 11);
		} else {
			mCurrent.roll(Calendar.MONTH, false);
		}
		createList();
	}
	
	private void showNext() {
		int monthNow = mCurrent.get(Calendar.MONTH);
		
		if(monthNow == 11) {
			mCurrent.roll(Calendar.YEAR, true);
			mCurrent.roll(Calendar.MONTH, -11);
		} else {
			mCurrent.roll(Calendar.MONTH, true);
		}
		
		createList();
	}
	
	private int getChildIndex(View v, ViewGroup parent) {
		int index = -1;
		for(int i = 0; i < parent.getChildCount(); i++) {
			View item = parent.getChildAt(i);
			if((item.getTag() != null) && (item.getTag().toString() == mDividerTag))
				continue;
			
			index++;
			if(item.equals(v))
				return index;
		}
		return index;
	}
}
