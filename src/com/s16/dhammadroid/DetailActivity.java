package com.s16.dhammadroid;

import java.io.File;
import java.util.Calendar;

import com.s16.dhammadroid.data.DhammaDataParser;
import com.s16.dhammadroid.data.ListDataContainer;
import com.s16.dhammadroid.data.NineNawinResData;
import com.s16.dhammadroid.download.Downloader;
import com.s16.dhammadroid.fragment.DetailFragment;
import com.s16.dhammadroid.fragment.NineNawinFragment;
import com.s16.dhammadroid.fragment.NineNawinSetDateFragment;
import com.s16.drawing.TypefaceSpan;
import com.s16.widget.SlidingUpTabDrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.SystemUiUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity {

	protected static final String TAG = DetailActivity.class.getSimpleName(); 
	
	private abstract class DetailPagerAdapter extends FragmentPagerAdapter {
		public DetailPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public boolean hasAudio(int position) {
			return false;
		}
		
		public boolean canPlayAudio(int position) {
			return false;
		}
		
		public String getAudioFile(int position) {
			return null;
		}
		
		public String getAudioFileUrl(int position) {
			return null;
		}
	}

	private class PagePagerAdapter extends DetailPagerAdapter {
		
		private DetailFragment[] mFragmentArr;

		public PagePagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentArr = new DetailFragment[getCount()];
		}
		
		private DhammaDataParser.Entry getEntry(int position) {
			if (position > -1 && position < getCount()) {
				return DhammaDataParser.ENTRIES.get(position);
			}
			return null;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			if (mFragmentArr[position] != null) {
				return mFragmentArr[position].getTitle();
			} else {
				DhammaDataParser.Entry entry = getEntry(position);
				if (entry != null) {
					return Utility.ZawGyiDrawFix(entry.title);
				}
			}
	        return "";
	    }

		@Override
		public Fragment getItem(int position) {
			if (mFragmentArr[position] == null) {
				mFragmentArr[position] = new DetailFragment();
			}
			Bundle args = new Bundle();
			args.putInt("position", position);
			args.putString("name", getArgsEntryName());
			mFragmentArr[position].setArguments(args);
			
			return mFragmentArr[position];
		}

		@Override
		public int getCount() {
			return DhammaDataParser.ENTRIES.size();
		}
		
		@Override
		public boolean hasAudio(int position) {
			DhammaDataParser.Entry entry = getEntry(position);
			if (entry != null) {
				return !TextUtils.isEmpty(entry.soundFile) && !TextUtils.isEmpty(entry.soundUrl); 
			}
			return false;
		}
		
		@Override
		public boolean canPlayAudio(int position) {
			DhammaDataParser.Entry entry = getEntry(position);
			if (entry != null && !TextUtils.isEmpty(entry.soundFile)) {
				File audioDir = Constants.getAudioFolder(getContext());
				if (audioDir != null) {
					File audioFile = new File(audioDir, entry.soundFile);
					return audioFile.exists();
				}
			}
			return false;
		}
		
		@Override
		public String getAudioFile(int position) {
			DhammaDataParser.Entry entry = getEntry(position);
			if (entry != null && !TextUtils.isEmpty(entry.soundFile)) {
				return entry.soundFile;
			}
			return null;
		}
		
		@Override
		public String getAudioFileUrl(int position) {
			DhammaDataParser.Entry entry = getEntry(position);
			if (entry != null && !TextUtils.isEmpty(entry.soundUrl)) {
				return entry.soundUrl;
			}
			return null;
		}
	}
	
	private class NineNawinPagerAdapter extends DetailPagerAdapter {

		private NineNawinFragment[] mFragmentArr;
		
		public NineNawinPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentArr = new NineNawinFragment[getCount()];
		}

		@Override
		public CharSequence getPageTitle(int position) {
			CharSequence title = getString(R.string.nn_app_mm);
			if (position > getCount()) {
				return Utility.ZawGyiDrawFix(title);
			} else if (mFragmentArr[position] != null) {
				return mFragmentArr[position].getTitle();
			} else {
				return Utility.ZawGyiDrawFix(title + " - " + NineNawinResData.getLevels(getContext())[position]);
			}
		}
		
		@Override
		public Fragment getItem(int position) {
			if (mFragmentArr[position] == null) {
				mFragmentArr[position] = new NineNawinFragment();
			}
			Bundle args = new Bundle();
			args.putInt("position", position);
			args.putString("name", getArgsEntryName());
			mFragmentArr[position].setArguments(args);
			
			return mFragmentArr[position];
		}

		@Override
		public int getCount() {
			return NineNawinResData.LEVEL_COUNT;
		}
		
		@Override
		public void notifyDataSetChanged() {
			for (int i=0;i<getCount();i++) {
				if (mFragmentArr[i] != null) {
					mFragmentArr[i].notifyDataSetChanged();
				}
			}
			super.notifyDataSetChanged();
		}
	}
	
	private ViewPager mViewPager;
	private DetailPagerAdapter mPagerAdapter;
	private ViewGroup mPlayerFrame;
	
	private static final int IC_PLAY = R.drawable.ic_play;
	private static final int IC_PAUSE = R.drawable.ic_pause;
	private static final int IC_DOWNLOAD = R.drawable.ic_file_download;
	
	private Downloader mDownloader;
	private ImageButton mButtonPlay;
	private TextView mTxtPlayerMessage;
	private TextView mTxtPlayerStart;
	private TextView mTxtPlayerEnd;
	
	private String mAudioFileUrl;
	private String mAudioFileName;
	private MediaPlayerImpl mMediaPlayer;
	private SeekBar mSeekPlayer;

	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			setActionBarTitle(mPagerAdapter.getPageTitle(position));
			stopPlayer();
			updateAudioFrame(position);
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};
	
	private View.OnClickListener mDownloadClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!TextUtils.isEmpty(mAudioFileUrl)) {
				long refrenceId = getDownloadRefId();
				if (refrenceId < 0) {
					refrenceId = mDownloader.download(mAudioFileUrl, Constants.getAudioFolder(getApplicationContext()), mAudioFileName);
					setDownloadRefId(refrenceId);
					updateDownloadFrame();
				} else {
					// Is Downloading...
				}
			}
		}
	};
	
	private Downloader.OnDownloadCompleteListener mDownloadComplete = new Downloader.OnDownloadCompleteListener() {
		
		@Override
		public void onDownloadComplete(Uri uri, long refrenceId) {
			if (uri != null && mAudioFileUrl != null) {
				//Log.i("onDownloadComplete", uri.getPath());
				removeDownloadRefId();
				
				if (uri.getLastPathSegment().equals(mAudioFileName)) {
					Toast.makeText(getContext(), String.format(getContext().getString(R.string.message_download_complete), mAudioFileName)
							, Toast.LENGTH_LONG).show();
					updateAudioFrame(mViewPager.getCurrentItem());
				}
			}
		}
	};
	
	private View.OnClickListener mPlayClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (mMediaPlayer.isPlaying()) {
				mButtonPlay.setImageResource(IC_PLAY);
				mMediaPlayer.pause();
			} else {
				if (!mMediaPlayer.isPaused()) {
					createPlayer(mAudioFileName);
				}
				
				mButtonPlay.setImageResource(IC_PAUSE);
				mMediaPlayer.start();
			}
		}
	};
	
	private View.OnClickListener mFloatingButtonClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getContext(), CounterActivity.class);
			startActivity(intent);
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
	
	private long getDownloadRefId() {
		int position = mViewPager.getCurrentItem();
		String prefsKey = "DownloadRefId_" + position;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		return prefs.getLong(prefsKey, -1L);
	}
	
	private void setDownloadRefId(long refrenceId) {
		int position = mViewPager.getCurrentItem();
		String prefsKey = "DownloadRefId_" + position;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(prefsKey, refrenceId);
		editor.commit();
	}
	
	private void removeDownloadRefId(){
		int position = mViewPager.getCurrentItem();
		String prefsKey = "DownloadRefId_" + position;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
		if (prefs.contains(prefsKey)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove(prefsKey);
			editor.commit();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		/*
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		if (title != null) {
			title.setTypeface(Constants.getZawgyiTypeface(this));
		}
		*/
		
		mViewPager = (ViewPager)findViewById(R.id.detailViewPager);
		//mViewPager.setPageTransformer(false, new DepthPageTransformer());
		
		mPlayerFrame = createSlidingUpTabDrawer();
		
		mTxtPlayerMessage = (TextView)mPlayerFrame.findViewById(R.id.textViewPlayerMessage);
		mTxtPlayerStart = (TextView)mPlayerFrame.findViewById(R.id.textViewPlayerStart);
		mTxtPlayerEnd = (TextView)mPlayerFrame.findViewById(R.id.textViewPlayerEnd);
		mButtonPlay = (ImageButton)mPlayerFrame.findViewById(R.id.imageButtonPlayer);
		mSeekPlayer = (SeekBar)mPlayerFrame.findViewById(R.id.seekBarPlayer);
		
		mMediaPlayer = new MediaPlayerImpl(mSeekPlayer, mTxtPlayerStart, mTxtPlayerEnd);
		mMediaPlayer.setMediaPlayerEventListener(new MediaPlayerImpl.MediaPlayerEventListener() {
			
			@Override
			public void onProgressChanged(MediaPlayer mp, int progress, boolean fromUser) {
			}
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mButtonPlay.setImageResource(IC_PLAY);
			}
		});
		
		mDownloader = Downloader.newInstance(getContext());
		mDownloader.setOnDownloadCompleteListener(mDownloadComplete);
		
		ImageButton floatingButton = (ImageButton)findViewById(R.id.action_counter);
		floatingButton.setOnClickListener(mFloatingButtonClick);
		
		if (isNineNawin()) {
			NineNawinResData.loadPreferences(getContext());
			
			int position = NineNawinResData.getLastLevel() - 1;
			mPagerAdapter = new NineNawinPagerAdapter(getFragmentManager());
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setOnPageChangeListener(mPageChangeListener);
			mViewPager.setCurrentItem(position);
			mPlayerFrame.setVisibility(View.GONE);
			
			setFloatingActionButton(floatingButton);
			setActionBarTitle(mPagerAdapter.getPageTitle(position));
		} else {
			floatingButton.setVisibility(View.GONE);
			
			int position = getArgsPosition();
			mPagerAdapter = new PagePagerAdapter(getFragmentManager());
			mViewPager.setAdapter(mPagerAdapter);
			mViewPager.setOnPageChangeListener(mPageChangeListener);
			mViewPager.setCurrentItem(position);
			setActionBarTitle(mPagerAdapter.getPageTitle(position));
			updateAudioFrame(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isNineNawin()) {
			getMenuInflater().inflate(R.menu.ninenawin, menu);
		} else {
			getMenuInflater().inflate(R.menu.main, menu);
		}
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (isNineNawin()) {
			MenuItem menuItemSetDate = menu.findItem(R.id.action_set_date);
			if (menuItemSetDate != null) {
				MenuItemCompat.setShowAsAction(menuItemSetDate, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
			}
		} else {
			MenuItem menuItemSettings = menu.findItem(R.id.action_settings);
			if (menuItemSettings != null) {
				MenuItemCompat.setShowAsAction(menuItemSettings, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
			}
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				//NavUtils.navigateUpFromSameTask(this);
				finish();
				break;
			case R.id.action_set_date:
				performNinnawinSetDate();
				break;
			case R.id.action_settings:
				performSettings();
				break;
			case R.id.action_about:
				Utility.showAboutDialog(getContext());
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onStop() {
		NineNawinResData.savePreferences(getContext());
        super.onStop();
    }
	
	@Override
    public void onDestroy() {
		if (mMediaPlayer != null) {
			mMediaPlayer.destroy();
		}
		if (mDownloader != null) {
			mDownloader.destroy();
		}
		super.onDestroy();
    }
	
	@SuppressLint("RtlHardcoded")
	protected void setFloatingActionButton(View view) {
		if (view == null) return;
		
		if (view.getParent() != null) {
			((ViewGroup)view.getParent()).removeView(view);
		}
		
		ViewGroup decorView = (ViewGroup)getWindow().getDecorView();
		ViewGroup contentView = (ViewGroup)decorView.findViewById(android.R.id.content);
		
		int size = (int)getResources().getDimension(R.dimen.fab_button_size);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
		params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		if (contentView == null)
			params.bottomMargin = (int)getResources().getDimension(R.dimen.fab_button_vertical_margin) + SystemUiUtils.getNavigationBarHeight(this);
		else 
			params.bottomMargin = (int)getResources().getDimension(R.dimen.fab_button_vertical_margin);
		params.rightMargin = (int)getResources().getDimension(R.dimen.fab_button_horizontal_margin);
		view.setLayoutParams(params);
		
		if (contentView == null)
			decorView.addView(view);
		else
			contentView.addView(view);
	}
	
	private void performNinnawinSetDate() {
		
		NineNawinSetDateFragment fragment = new NineNawinSetDateFragment();
		
		int index = mViewPager.getCurrentItem();
		int dayValue = NineNawinResData.getDayValue(index + 1);
		Calendar startDate = NineNawinResData.getStartDate(index);
		if (startDate != null) {
			Bundle args = new Bundle();
			args.putInt("selectDay", dayValue);
			args.putLong("selectedDate", startDate.getTimeInMillis());
			fragment.setArguments(args);
		}
		
		fragment.setOnSetDateListener(new NineNawinSetDateFragment.OnSetDateListener() {
			
			@Override
			public void onSetDate(Calendar value) {
				int currentLevel = mViewPager.getCurrentItem();
				NineNawinResData.setStartDate(value, currentLevel);
				mPagerAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onClearDate() {
				NineNawinResData.reset();
				mPagerAdapter.notifyDataSetChanged();
			}
			
		});
		fragment.show(getFragmentManager(), "NineNawinSetDateFragment");
	}
	
	private void performSettings() {
		Intent intent = new Intent(getContext(), SettingsActivity.class);
		startActivity(intent);
	}
	
	private void updateAudioFrame(int position) {
		if (mPagerAdapter == null) return;
		if (mPagerAdapter.hasAudio(position)) {
			mAudioFileName = mPagerAdapter.getAudioFile(position);
			mAudioFileUrl =  mPagerAdapter.getAudioFileUrl(position);
			
			mPlayerFrame.setVisibility(View.VISIBLE);
			boolean isDownloading = updateDownloadFrame();
			if (!isDownloading && mPagerAdapter.canPlayAudio(position)) {
				mSeekPlayer.setVisibility(View.VISIBLE);
				mTxtPlayerStart.setVisibility(View.VISIBLE);
				mTxtPlayerEnd.setVisibility(View.VISIBLE);
				mTxtPlayerMessage.setVisibility(View.GONE);
				mButtonPlay.setImageResource(IC_PLAY);
				mButtonPlay.setOnClickListener(mPlayClick);
			} else {
				mSeekPlayer.setVisibility(View.GONE);
				mTxtPlayerStart.setVisibility(View.GONE);
				mTxtPlayerEnd.setVisibility(View.GONE);
				mTxtPlayerMessage.setVisibility(View.VISIBLE);
				mButtonPlay.setImageResource(IC_DOWNLOAD);
				mButtonPlay.setOnClickListener(mDownloadClick);
			}
		} else {
			mPlayerFrame.setVisibility(View.GONE);
		}
	}
	
	protected boolean updateDownloadFrame() {
		mTxtPlayerMessage.setText(R.string.message_download);
		
		long refId = getDownloadRefId();
		if (refId > -1L) {
			Downloader.ResultStatus status = mDownloader.checkStatus(refId);
			if (status.status == DownloadManager.STATUS_FAILED || status.status == DownloadManager.STATUS_SUCCESSFUL) {
				removeDownloadRefId();
				return true;
			}
			
			mTxtPlayerMessage.setText(String.format(getString(R.string.download_noti_description), mAudioFileName));
		}
		return (refId > -1L);
	}
	
	protected void stopPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
		if (mButtonPlay != null) {
			mButtonPlay.setImageResource(IC_PLAY);
		}
	}
	
	protected void createPlayer(String fileName) {
		File audioDir = Constants.getAudioFolder(getContext());
		File audioFile = new File(audioDir, fileName);
		mMediaPlayer.prepare(audioFile);
	}
	
	protected boolean isNineNawin() {
		return (ListDataContainer.NINENAWIN_ENTRY.equals(getArgsEntryName()));
	}
	
	protected String getArgsEntryName() {
		Bundle extras = getIntent().getExtras();
		return extras.getString("name");
	}

	protected int getArgsPosition() {
		Bundle extras = getIntent().getExtras();
		return extras.getInt("position");
	}
	
	protected SlidingUpTabDrawer createSlidingUpTabDrawer() {
		RelativeLayout rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
		LayoutInflater inflater = LayoutInflater.from(getContext()); 
		SlidingUpTabDrawer slidingDrawer = (SlidingUpTabDrawer)inflater.inflate(R.layout.layout_tab_player, rootLayout, false);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		rootLayout.addView(slidingDrawer, params);
		
		return slidingDrawer;
	}
}