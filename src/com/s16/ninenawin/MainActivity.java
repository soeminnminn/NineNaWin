package com.s16.ninenawin;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements SwipeGestureDetector.ISwipeGestureListener {

	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final int REQUEST_SETTINGS = 0;
	
	private int mCurrentLevel;
	private int mStartDay;
	private Calendar mStartDate;
	
	private TextView mTextViewTitle;
	private ListView mListViewMain;
	private GestureDetector gestureDetector;
	
	public MainActivity() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		ResourceData.loadPreferences(getBaseContext());
		
		mCurrentLevel = ResourceData.getLastLevel(); 
		if(mCurrentLevel < 1) {
			mCurrentLevel = 1;
		}
		
		mTextViewTitle = (TextView)findViewById(R.id.textViewHeader);
		mListViewMain = (ListView)findViewById(R.id.listViewMain);
		
		createListView(mCurrentLevel);
		
		mTextViewTitle.setTypeface(Utils.getTypeFace(getBaseContext()));
		setHeaderText(mCurrentLevel);
		
		final ImageButton imageButtonLeft = (ImageButton)findViewById(R.id.imageButtonLeft);
		imageButtonLeft.setOnClickListener(new Button.OnClickListener(){
 		  public void onClick(View arg0) {
  			 onRightSwipe();
 		  }
        });
		
		final ImageButton imageButtonRight = (ImageButton)findViewById(R.id.imageButtonRight);  
		imageButtonRight.setOnClickListener(new Button.OnClickListener(){
 		  public void onClick(View arg0) {
 			 onLeftSwipe();
 		  }
        });
		
		gestureDetector = new GestureDetector(new SwipeGestureDetector(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void createListView(int level) {
		if(mListViewMain == null) return;
		
		ArrayList<ItemDetail> itemDetailList = ResourceData.getResults(this, level, ResourceData.getStartDate(level - 1));
		mStartDay = itemDetailList.get(0).getDayValue();
		
		mListViewMain.setAdapter(new ItemListBaseAdapter(this, itemDetailList, Utils.getTypeFace(getBaseContext())));
		mListViewMain.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
			        return true;
			    }
				return false;
			}
		});
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {
		return super.onCreateDialog(id);
	}
	
	@Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        case R.id.action_setdate:
        	showDateSelection();
            return true;
            
        case R.id.action_setlevel:
        	showLevelSelection();
        	break;
            
        case R.id.action_settings:
        	Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
			startActivityForResult(intent, REQUEST_SETTINGS);
            return true;
            
        default:
            break;
        }
    	
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
	
	@Override
    protected void onStop() {
		ResourceData.savePreferences(getBaseContext());
		sendWidgetUpdate();
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
      if (gestureDetector.onTouchEvent(event)) {
        return true;
      }
      return super.onTouchEvent(event);
	}
    
    public void onLeftSwipe() {
    	mCurrentLevel++;
    	
    	if(mCurrentLevel > 9)
    		mCurrentLevel = 9;
    	
    	this.showListItem();
    }

    public void onRightSwipe() {
    	mCurrentLevel--;
    	
    	if(mCurrentLevel < 1) 
    		mCurrentLevel = 1;
    	
    	this.showListItem();
    }
    
    public synchronized void onActivityResult(final int requestCode, int resultCode, final Intent data) {
		if(requestCode == REQUEST_SETTINGS) {
			showListItem();
			sendWidgetUpdate();
		}
	}
    
    private void sendWidgetUpdate() {
    	ComponentName componentName = new ComponentName(getApplicationContext(), ItemWidgetProvider.class);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
		
		Intent intent = new Intent(getApplicationContext(), ItemWidgetProvider.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		getApplicationContext().sendBroadcast(intent);
    }
	
	private void setHeaderText(int level) {
		mTextViewTitle.setText(getResources().getText(R.string.app_mm) + " - " + getResources().getText(ResourceData.RES_LEVEL_ID_ARRAY[level - 1]));
	}
	
	private void showDateSelection() {
		
		final DateSelectorDialog selectorDialog = new DateSelectorDialog(this, mStartDay, mStartDate);
		selectorDialog.setTitle(R.string.action_set_date);
		
		selectorDialog.setOnClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Calendar selectedDate = selectorDialog.get(which);
    			//Log.i(TAG, Utils.getDateString(selectedDate));
    			
				ResourceData.setStartDate(selectedDate, mCurrentLevel - 1);
    			showListItem(mCurrentLevel, selectedDate);
			}
		});
		
		selectorDialog.show();
	}
	
	private void showLevelSelection() {
		final LevelSelectorDialog selectorDialog = new LevelSelectorDialog(this, mCurrentLevel);
		selectorDialog.setTitle(R.string.action_set_level);
		
		selectorDialog.setOnClickListener(new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCurrentLevel = which + 1;
				ResourceData.setLastLevel(mCurrentLevel); 
				showListItem();
			}
		});
		
		selectorDialog.show();
	}
	
	private void showListItem() {
		this.showListItem(mCurrentLevel, ResourceData.getStartDate(mCurrentLevel - 1));
	}
	
	private void showListItem(int level, Calendar startDate) {
		if(mListViewMain == null) return;
		
		ArrayList<ItemDetail> itemDetailList = ResourceData.getResults(this, level, startDate);
		
		mStartDay = itemDetailList.get(0).getDayValue();
		if(itemDetailList.get(0).getDate() != null) {
			mStartDate = itemDetailList.get(0).getDate();
		}
		
		ItemListBaseAdapter adapter = (ItemListBaseAdapter)mListViewMain.getAdapter();
		if(adapter != null) {
			adapter.setData(itemDetailList);
			mListViewMain.invalidate();
			
			setHeaderText(level);
		}
	}
}
