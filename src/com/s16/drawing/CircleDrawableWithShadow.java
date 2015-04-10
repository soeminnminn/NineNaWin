package com.s16.drawing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class CircleDrawableWithShadow extends Drawable {

	private static final float SHADOW_SIZE = 15.0f;
	private static final int SHADOW_COLOR = 0xff000000;
	
	private static final int[] SHADOW_COLORS_LIGHT = new int[] {
		0x08000000, 0x09000000, 0x10000000, 0x11000000, 0x12000000, 0x13000000, 0x14000000, 0x15000000, 0x16000000, 0x17000000
	};
	
	private static final int[] SHADOW_COLORS = new int[] {
		0x05757575, 0x06757575, 0x07757575, 0x08757575, 0x09757575, 0x10757575, 0x11757575, 0x12757575, 0x13757575, 0x14757575
	};
	
	private static final int[] SHADOW_PADDING = new int[] {
		3, 2, 2, 1, 1, 1, 1, 1, 1, 1
	};
	
	private Paint mPaint;
	private Paint mShadowPaint;
	private final int[] mShadowColors;
	
	public CircleDrawableWithShadow(Resources resources, int backgroundColor, boolean lightTheme) {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(backgroundColor);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setColor(SHADOW_COLOR);
        
        if (lightTheme) {
        	mShadowColors = SHADOW_COLORS_LIGHT;
        } else {
        	mShadowColors = SHADOW_COLORS;
        }
	}

	@Override
	public void draw(Canvas canvas) {
		//canvas.drawColor(0xff212121);
		int saveCount = canvas.save();
		
		final Paint shadowPaint = mShadowPaint;
		final RectF bounds = new RectF();
		bounds.set(getBounds());
		for(int i=0; i<SHADOW_PADDING.length; i++) {
			shadowPaint.setColor(mShadowColors[i]);
			int padding = SHADOW_PADDING[i];
			bounds.bottom -= (padding * 2);
			bounds.right -= (padding * 2);
			canvas.translate(padding, padding);
			canvas.drawOval(bounds, shadowPaint);
		}
		
		canvas.restoreToCount(saveCount);
		bounds.set(getBounds());
		bounds.bottom -= (SHADOW_SIZE * 2);
		bounds.right -= (SHADOW_SIZE * 2);
		canvas.translate(SHADOW_SIZE, SHADOW_SIZE / 2);
		canvas.drawOval(bounds, mPaint);
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
        mShadowPaint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.OPAQUE;
	}

	@Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
	}
	
	@Override
    public boolean getPadding(Rect padding) {
		padding.set((int)SHADOW_SIZE, (int)(SHADOW_SIZE/2), (int)SHADOW_SIZE, (int)(SHADOW_SIZE/2));
        return true;
	}
}
