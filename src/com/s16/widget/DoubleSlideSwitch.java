package com.s16.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DoubleSlideSwitch extends FrameLayout {

	//private boolean mIsBeingDragged = false;
	private TextView mLeftSlider;
	private TextView mRightSlider;
	private boolean mLeftBeingDragged = false;
	private float mLeftSliderX = 0f;
	private float mLeftSliderStartX = 0f;
	
	private boolean mRightBeingDragged = false;
	
	private View.OnTouchListener mLeftTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent ev) {
			int action = ev.getAction() & MotionEvent.ACTION_MASK;
			switch (action) {
	        	case MotionEvent.ACTION_MOVE:
	        		if (mLeftBeingDragged) {
	        			float newX = ev.getX();
	        			float diffX = (newX - mLeftSliderX);
	        			float nextX = v.getX() + diffX;
	        			if (nextX < 0) {
		        			mLeftSliderX = newX;
		        			v.setX(nextX);
	        			}
	        		}
	        		break;
	        		
	        	case MotionEvent.ACTION_DOWN:
	        		if (!mLeftBeingDragged) {
		        		mLeftBeingDragged = true;
		        		mLeftSliderStartX = v.getX();
		        		mLeftSliderX = ev.getX();
	        		}
	        		break;
	        		
	        	case MotionEvent.ACTION_CANCEL:
	            case MotionEvent.ACTION_UP:
	            	if (mLeftBeingDragged) {
		            	mLeftBeingDragged = false;
		            	mLeftSliderX = 0f;
		            	v.setX(mLeftSliderStartX);
	            	}
	                break;
	                
	            default:
	            	break;
	        }
			
			return false;
		}
	};
	
	private View.OnTouchListener mRightTouchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent ev) {
			int action = ev.getAction() & MotionEvent.ACTION_MASK;
			switch (action) {
	        	case MotionEvent.ACTION_MOVE:
	        		if (mRightBeingDragged) {
	        			
	        		}
	        		break;
	        		
	        	case MotionEvent.ACTION_DOWN:
	        		mRightBeingDragged = true;
	        		break;
	        		
	        	case MotionEvent.ACTION_CANCEL:
	            case MotionEvent.ACTION_UP:
	            	mRightBeingDragged = false;
	                break;
	                
	            default:
	            	break;
	        }
			return false;
		}
	};
	
	public DoubleSlideSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	@SuppressLint("RtlHardcoded")
	private void init(Context context) {
		if (isInEditMode()) {
			return;
		}
		
		FrameLayout.LayoutParams leftSlideParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
		leftSlideParams.gravity = Gravity.LEFT;
		mLeftSlider = new TextView(context);
		mLeftSlider.setLayoutParams(leftSlideParams);
		mLeftSlider.setClickable(true);
		mLeftSlider.setOnTouchListener(mLeftTouchListener);
		addView(mLeftSlider);
		
		FrameLayout.LayoutParams rightSlideParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
		rightSlideParams.gravity = Gravity.RIGHT;
		mRightSlider = new TextView(context);
		mRightSlider.setLayoutParams(rightSlideParams);
		mRightSlider.setOnTouchListener(mRightTouchListener);
		addView(mRightSlider);
		
		mLeftSlider.setBackgroundColor(0xffff0000);
		mRightSlider.setBackgroundColor(0xff0000ff);
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (isInEditMode()) {
			return;
		}
		
		//int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);
		//int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
		
		int width = getMeasuredWidth();
		//int maxSliderWidth = (width / 3) + (width / 2);
		//int widthSpec = MeasureSpec.makeMeasureSpec(width * 2, MeasureSpec.EXACTLY);
		int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
		
		int sliderWidth = (width / 3) + (width / 2);
		int sliderWidthSpec = MeasureSpec.makeMeasureSpec(sliderWidth, MeasureSpec.EXACTLY);
		
		mLeftSlider.measure(sliderWidthSpec, heightSpec);
		mLeftSlider.setX(-(width / 2));
		
		//mRightSlider.measure(sliderWidthSpec, heightSpec);
		//mRightSlider.setX((width / 3) * 2);
	}
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //mLeftSlider.setLeft(-(w / 2));
	}
	
	@Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
	}
	
	/*
	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }
        
        switch (action & MotionEvent.ACTION_MASK) {
        	case MotionEvent.ACTION_MOVE:
        		break;
        		
        	case MotionEvent.ACTION_DOWN:
        		break;
        		
        	case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
                
            default:
            	break;
        }
        
		//return super.onInterceptTouchEvent(ev);
        return mIsBeingDragged;
	}*/
}
