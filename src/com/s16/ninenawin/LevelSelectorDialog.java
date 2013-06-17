package com.s16.ninenawin;


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
import android.widget.LinearLayout;
import android.widget.TextView;

public class LevelSelectorDialog extends Dialog implements View.OnClickListener {

	private final String mDividerTag = "DIVIDER";
	private CharSequence mTitle;
	private int mSelectedLevel;
	
	private LinearLayout mHolder;
	private OnClickListener mOnClickListener;
	
	private int mItemWidth;
	private int mItemHeight;
	
	public LevelSelectorDialog(Context context, int selectedLevel) {
		super(context);
		
		mSelectedLevel = selectedLevel;
		
		final Resources res = context.getResources();
		final DisplayMetrics dm = res.getDisplayMetrics();
		mItemWidth = Math.round(res.getFraction(R.fraction.dialog_item_width, dm.widthPixels, dm.widthPixels));
		mItemHeight = Math.round(res.getFraction(R.fraction.dialog_item_height, dm.heightPixels, dm.heightPixels));
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.level_selector);
        
        TextView titleView = (TextView)findViewById(R.id.textViewTitle);
        titleView.setText(mTitle);
        
        mHolder = (LinearLayout)findViewById(R.id.layoutSelectionHolder);
        
        Button btnCancel = (Button)findViewById(R.id.dialogButtonNegative);
        btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LevelSelectorDialog.this.cancel();
			}
		});
        
        createList();
	}
	
	@Override
	public void onClick(View view) {
		if(mOnClickListener != null) {
			int which = getChildIndex(view, mHolder);
			mOnClickListener.onClick(this, which);
		}
		dismiss();
	}
	
	@Override
	public void setTitle(CharSequence value) {
		super.setTitle(value);
		mTitle = value;
	}
	
	public void setOnClickListener(OnClickListener onClickListener) {
		mOnClickListener = onClickListener;	
	}
	
	private void createList() {
		if(mHolder == null) return;
		
		final Resources res = getContext().getResources();
		CharSequence[] arrList = ResourceData.getLevels(getContext());
		
		mHolder.removeAllViews();
		for(int i = 0; i < arrList.length; i++) {
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
    		if(i == (mSelectedLevel - 1)) {
    			button.setTextColor(res.getColor(R.color.white));
    			button.setBackgroundResource(R.drawable.holo_actionbar_button_selected);
    		} else {
    			button.setTextColor(res.getColor(R.color.bright_foreground_holo_light));
    			button.setBackgroundResource(R.drawable.holo_actionbar_button);
    		}
    		button.setLayoutParams(new LayoutParams(mItemWidth, mItemHeight));
    		button.setTypeface(Utils.getTypeFace(getContext()));
    		button.setText(arrList[i]);
    		button.setOnClickListener(this);
    		
    		mHolder.addView(button);
    		mHolder.measure(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		}
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
