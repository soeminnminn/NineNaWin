package com.s16.app;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.SystemUiUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class ActivityHelper {

	private final Activity mActivity;
	
	// variables for loading layout
	private ViewGroup mLoadingLayout;
	private ViewGroup mMainContentLayout;
	private Animation mAnimationFadeIn;
	private Animation mAnimationFadeOut;
	
	// variables that control the Action Bar auto hide behavior (aka "quick recall")
	private static final int HEADER_HIDE_ANIM_DURATION = 300;
	private static final int ACTIONBAR_AUTO_HIDE_SENSIVITY = 48;
	private static final int ACTIONBAR_AUTO_HIDE_MIN_Y = 152;
	
	private boolean mActionBarAutoHideEnabled = false;
	private int mActionBarAutoHideSensivity = 0;
	private int mActionBarAutoHideMinY = 0;
	private int mActionBarAutoHideSignal = 0;
	private boolean mActionBarShown = true;
	private List<View> mHideableHeaderViews = new ArrayList<View>();
	
	private long mUiThreadId;
	
	public static ActivityHelper createInstance(Activity activity) {
		return new ActivityHelper(activity);
	}
	
	private ActivityHelper(Activity activity) {
		mActivity = activity;
		mUiThreadId = Thread.currentThread().getId();
	}
	
	protected Activity getActivity() {
		return mActivity;
	}
	
	protected Context getContext() {
		return mActivity;
	}
	
	protected Resources getResources() {
		return getContext().getResources();
	}
	
	protected Window getWindow() {
		return mActivity.getWindow();
	}
	
	public View getContentView() {
		ViewGroup decorView = (ViewGroup)getWindow().getDecorView();
		return decorView.findViewById(android.R.id.content);
	}
	
	@SuppressLint("RtlHardcoded")
	public ActivityHelper setFloatingActionButton(View view, int sizeResId, int verticalMarginResId, int horizontalMarginResId) {
		if (view == null) return this;
		
		if (view.getParent() != null) {
			((ViewGroup)view.getParent()).removeView(view);
		}
		
		ViewGroup decorView = (ViewGroup)getWindow().getDecorView();
		ViewGroup contentView = (ViewGroup)decorView.findViewById(android.R.id.content);
		
		int size = (int)getResources().getDimension(sizeResId);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		if (contentView == null)
			params.bottomMargin = (int)getResources().getDimension(verticalMarginResId) + SystemUiUtils.getNavigationBarHeight(getActivity());
		else 
			params.bottomMargin = (int)getResources().getDimension(verticalMarginResId);
		params.rightMargin = (int)getResources().getDimension(horizontalMarginResId);
		view.setLayoutParams(params);
		
		if (contentView == null)
			decorView.addView(view);
		else
			contentView.addView(view);
		return this;
	}
	
	public ActivityHelper setupLoading() {
		if (mLoadingLayout != null) return this;
		mLoadingLayout = new RelativeLayout(getContext());
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.alignWithParent = true;
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		ProgressBar progressLoading = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
		progressLoading.setIndeterminate(true);
		mLoadingLayout.addView(progressLoading, params);
		
		ViewGroup decorView = (ViewGroup)getWindow().getDecorView();
		ViewGroup contentView = (ViewGroup)decorView.findViewById(android.R.id.content);
		mMainContentLayout = (ViewGroup)contentView.getChildAt(0);
		contentView.addView(mLoadingLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mLoadingLayout.setVisibility(View.GONE);
		
		mAnimationFadeIn = new AlphaAnimation(0, 1);
		mAnimationFadeIn.setInterpolator(new DecelerateInterpolator());
		mAnimationFadeIn.setDuration(1000);
		
		mAnimationFadeOut = new AlphaAnimation(1, 0);
		mAnimationFadeOut.setInterpolator(new AccelerateInterpolator());
		mAnimationFadeOut.setStartOffset(1000);
		mAnimationFadeOut.setDuration(1000);
		
		return this;
	}
	
	public boolean isLoadingShowing() {
		if (mLoadingLayout == null || mMainContentLayout == null) return false;
		return (mLoadingLayout.getVisibility() == View.VISIBLE);
	}
	
	public ActivityHelper showLoading() {
		return showLoading(true);
	}
	
	public ActivityHelper showLoading(boolean animate) {
		if (mLoadingLayout == null || mMainContentLayout == null) return this;
		if (isLoadingShowing()) return this;
		
		if (animate) {
			mLoadingLayout.startAnimation(mAnimationFadeIn);
			if (mMainContentLayout.getVisibility() == View.VISIBLE) {
				mMainContentLayout.startAnimation(mAnimationFadeOut);
			}
		} else {
			mLoadingLayout.clearAnimation();
			mMainContentLayout.clearAnimation();
		}
		mLoadingLayout.setVisibility(View.VISIBLE);
		mMainContentLayout.setVisibility(View.GONE);
		return this;
	}
	
	public ActivityHelper hideLoading() {
		return hideLoading(true);
	}
	
	public ActivityHelper hideLoading(boolean animate) {
		if (mLoadingLayout == null || mMainContentLayout == null) return this;
		if (!isLoadingShowing()) return this;
		if (animate) {
			if (mLoadingLayout.getVisibility() == View.VISIBLE) {
				mLoadingLayout.startAnimation(mAnimationFadeOut);
			}
			mMainContentLayout.startAnimation(mAnimationFadeIn);
		} else {
			mLoadingLayout.clearAnimation();
			mMainContentLayout.clearAnimation();
		}
		mLoadingLayout.setVisibility(View.GONE);
		mMainContentLayout.setVisibility(View.VISIBLE);
		return this;
	}
	
	/**
	* Initializes the Action Bar auto-hide (aka Quick Recall) effect.
	*/
	private void initActionBarAutoHide() {
		mActionBarAutoHideEnabled = true;
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mActionBarAutoHideMinY = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ACTIONBAR_AUTO_HIDE_MIN_Y, dm);
		mActionBarAutoHideSensivity = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ACTIONBAR_AUTO_HIDE_SENSIVITY, dm);
	}
	
	public ActivityHelper registerHideableHeaderView(View hideableHeaderView) {
        if (!mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.add(hideableHeaderView);
        }
        return this;
    }

	public ActivityHelper deregisterHideableHeaderView(View hideableHeaderView) {
        if (mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.remove(hideableHeaderView);
        }
        return this;
    }
	
    public ActivityHelper autoShowOrHideActionBar(boolean show) {
		if (show == mActionBarShown) {
			return this;
		}
		mActionBarShown = show;
		for (View view : mHideableHeaderViews) {
            if (show) {
                view.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                view.animate()
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
		return this;
	}
	
	public boolean isActionBarAutoHideEnabled() {
		return mActionBarAutoHideEnabled;
	}

	public ActivityHelper enableActionBarAutoHide(final ListView listView) {
		initActionBarAutoHide();
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			final static int ITEMS_THRESHOLD = 3;
			int lastFvi = 0;
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE, 
						lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE : 
						lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE);
				// < MIN, == 0, > MAX 
				lastFvi = firstVisibleItem;
			}
		});
		return this;
	}
	
	public ActivityHelper enableActionBarAutoHide(final ScrollView scrollView) {
		initActionBarAutoHide();	
		
		final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
                ViewTreeObserver.OnScrollChangedListener() {
			private int lastY = 0;
			
			@Override
			public void onScrollChanged() {
				int scrollY = scrollView.getScrollY();
				if (lastY < scrollY) {
					autoShowOrHideActionBar(false);
				} else if (lastY > scrollY) {
					autoShowOrHideActionBar(true);
				}
				lastY = scrollY;
			}
		};
		
		scrollView.setOnTouchListener(new View.OnTouchListener() {
		    private ViewTreeObserver observer;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (observer == null) {
		            observer = scrollView.getViewTreeObserver();
		            observer.addOnScrollChangedListener(onScrollChangedListener);
		        } else if (!observer.isAlive()) {
		            observer.removeOnScrollChangedListener(onScrollChangedListener);
		            observer = scrollView.getViewTreeObserver();
		            observer.addOnScrollChangedListener(onScrollChangedListener);
		        }
		        return false;
			}
		});
		return this;
	}
	
	/*
	public ActivityHelper enableActionBarAutoHide(final RecyclerView recyclerView) {
		initActionBarAutoHide();
		recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			final static int MIN_DY = -5;
			final static int MAX_DY = 50;
			
			@Override
	        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
	            super.onScrollStateChanged(recyclerView, newState);
	        }

	        @Override
	        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
	            super.onScrolled(recyclerView, dx, dy);
	            if (dy > MAX_DY) {
	            	//scroll up
	            	autoShowOrHideActionBar(false);
	            } else if (dy < MIN_DY) {
	            	//scroll down
	            	autoShowOrHideActionBar(true);
	            }
	        }
		});
		return this;
	}*/
	
	/**
	* Indicates that the main content has scrolled (for the purposes of showing/hiding
	* the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
	* (if the underlying view supports it) or may be approximate indications:
	* deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
	* "scrolled backward indeterminately". currentY may be 0 to mean "somewhere close to the
	* start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
	*/
	private void onMainContentScrolled(int currentY, int deltaY) {
		if (deltaY > mActionBarAutoHideSensivity) {
			deltaY = mActionBarAutoHideSensivity;
		} else if (deltaY < -mActionBarAutoHideSensivity) {
			deltaY = -mActionBarAutoHideSensivity;
		}
		if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
			// deltaY is a motion opposite to the accumulated signal, so reset signal
			mActionBarAutoHideSignal = deltaY;
		} else {
			// add to accumulated signal
			mActionBarAutoHideSignal += deltaY;
		}
		boolean shouldShow = currentY < mActionBarAutoHideMinY || (mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
		autoShowOrHideActionBar(shouldShow);
	}
	
	public ActivityHelper setHideOnContentScrollEnabled(boolean hideOnContentScroll) {
		try {
			Method method = Activity.class.getMethod("getActionBar");
			if (method != null) {
				Object actionbar = method.invoke(getActivity());
				if (actionbar != null) {
					Method method1 = actionbar.getClass().getMethod("setHideOnContentScrollEnabled", Boolean.class);
					if (method1 != null) {
						method1.invoke(actionbar, Boolean.valueOf(hideOnContentScroll));
					}
				}
			}
		} catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        }
		return this;
	}
	
	public ActivityHelper runOnUiThread(Runnable runnable, boolean checkThread) {
		if (checkThread) {
			if (Thread.currentThread().getId() != mUiThreadId) {
				getActivity().runOnUiThread(runnable);
			} else {
				runnable.run();
			}		
		} else {
			getActivity().runOnUiThread(runnable);
		}
		return this;
	}
}
