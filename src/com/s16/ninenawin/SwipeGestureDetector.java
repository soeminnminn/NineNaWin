package com.s16.ninenawin;

import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;

public class SwipeGestureDetector extends SimpleOnGestureListener {
	
	private static final String TAG = SwipeGestureDetector.class.getSimpleName();
	
	// Swipe properties, you can change it to make the swipe
    // longer or shorter and speed
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 200;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    public interface ISwipeGestureListener {
    	void onLeftSwipe();
    	void onRightSwipe();
    }
    
    private ISwipeGestureListener mListener;
    
    public SwipeGestureDetector(ISwipeGestureListener listener) {
    	mListener = listener;
    }
    
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    	if(mListener == null) return false;
    	
    	try {
    		float diffAbsY = Math.abs(e1.getY() - e2.getY());
    		final float absX = Math.abs(velocityX);
            //final float absY = Math.abs(velocityY);
    		final float deltaX = e1.getX() - e2.getX();
    		//final float deltaY = e1.getY() - e2.getY();

    		if (diffAbsY > SWIPE_MAX_OFF_PATH)
    			return false;
       
    		// Left swipe
    		if (deltaX > SWIPE_MIN_DISTANCE && absX > SWIPE_THRESHOLD_VELOCITY) {
    			mListener.onLeftSwipe();
    		// Right swipe
    		} else if (-deltaX > SWIPE_MIN_DISTANCE && absX > SWIPE_THRESHOLD_VELOCITY) {
    			mListener.onRightSwipe();
    		}
    		
    		/* // Down swipe
    		if (deltaY > SWIPE_MIN_DISTANCE && absY > SWIPE_THRESHOLD_VELOCITY) {
    			mListener.onLeftSwipe();
    		// Up swipe
    		} else if (-deltaY > SWIPE_MIN_DISTANCE && absY > SWIPE_THRESHOLD_VELOCITY) {
    			mListener.onRightSwipe();
    		}*/
    	} catch (Exception e) {
    		Log.e(TAG, "Error on gestures");
    	}
    	return false;
    }
}
