package com.s16.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.TextView;

public class PinchZoomTextView extends TextView {

	private static final float MAX_SCALE = 2.0f;
	private ScaleGestureDetector mScaleDetector;
	private float mOrigTextSize;
	
	private class ScaleListener extends
    	ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float size = getTextSize();
			float factor = detector.getScaleFactor();
			float zoomSize = Math.max(mOrigTextSize, Math.min(size * factor, mOrigTextSize * MAX_SCALE));
			
			setTextSize(TypedValue.COMPLEX_UNIT_PX, zoomSize);
			invalidate();
			return true;
		}
	}
	
	public PinchZoomTextView(Context context) {
		super(context);
		init(context);
	}
	
	public PinchZoomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public PinchZoomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		mOrigTextSize = getTextSize();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    // Let the ScaleGestureDetector inspect all events.
		if (ev.getPointerCount() > 1) {
			mScaleDetector.onTouchEvent(ev);
		}
	    return super.onTouchEvent(ev);
	}
	
}

/*
public class PinchZoomTextView extends TextView {

	private static final int INVALID_POINTER_ID = -1;
	private float mPosX;
	private float mPosY;

	private float mLastTouchX;
	private float mLastTouchY;
	private int mActivePointerId = INVALID_POINTER_ID;
	 
	private float mScaleFactor = 1.f;
	private ScaleGestureDetector mScaleDetector;
	
	private class ScaleListener extends
    	ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(1.0f, Math.min(mScaleFactor, 3.0f));
			invalidate();
			return true;
		}
	}
	
	public PinchZoomTextView(Context context) {
		super(context);
		init(context);
	}
	
	public PinchZoomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public PinchZoomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    // Let the ScaleGestureDetector inspect all events.
	    mScaleDetector.onTouchEvent(ev);

	    final int action = ev.getAction();
	    switch (action & MotionEvent.ACTION_MASK) {
		    case MotionEvent.ACTION_DOWN: {
		        final float x = ev.getX();
		        final float y = ev.getY();
	
		        mLastTouchX = x;
		        mLastTouchY = y;
		        mActivePointerId = ev.getPointerId(0);
		        break;
		    }
	
		    case MotionEvent.ACTION_MOVE: {
		        final int pointerIndex = ev.findPointerIndex(mActivePointerId);
		        final float x = ev.getX(pointerIndex);
		        final float y = ev.getY(pointerIndex);
	
		        // Only move if the ScaleGestureDetector isn't processing a gesture.
		        if (!mScaleDetector.isInProgress()) {
		            final float dx = x - mLastTouchX;
		            final float dy = y - mLastTouchY;
	
		            mPosX += dx;
		            if (mPosX > 0) {
		            	mPosX = 0;
		            }
		            mPosY += dy;
		            if (mPosY > 0) {
		            	mPosY = 0;
		            }
	
		            invalidate();
		        }
	
		        mLastTouchX = x;
		        mLastTouchY = y;
	
		        break;
		    }
	
		    case MotionEvent.ACTION_UP: {
		        mActivePointerId = INVALID_POINTER_ID;
		        break;
		    }
	
		    case MotionEvent.ACTION_CANCEL: {
		        mActivePointerId = INVALID_POINTER_ID;
		        break;
		    }
	
		    case MotionEvent.ACTION_POINTER_UP: {
		        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		        final int pointerId = ev.getPointerId(pointerIndex);
		        if (pointerId == mActivePointerId) {
		            // This was our active pointer going up. Choose a new
		            // active pointer and adjust accordingly.
		            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
		            mLastTouchX = ev.getX(newPointerIndex);
		            mLastTouchY = ev.getY(newPointerIndex);
		            mActivePointerId = ev.getPointerId(newPointerIndex);
		        }
		        break;
		    }
	    }

	    return true;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		int saveCount = canvas.save();
		canvas.scale(mScaleFactor, mScaleFactor);
		
		float dx = mPosX;
		float dy = mPosY;
	    canvas.translate(dx, dy);
		
	    super.onDraw(canvas);
	    
	    canvas.restoreToCount(saveCount);
	}
	
}*/
