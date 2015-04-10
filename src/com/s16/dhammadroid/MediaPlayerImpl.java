package com.s16.dhammadroid;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.widget.SeekBar;
import android.widget.TextView;

public class MediaPlayerImpl {

	private MediaPlayer mMediaPlayer;
	private final SeekBar mSeekPlayer;
	private final TextView mTxtPlayerStart;
	private final TextView mTxtPlayerEnd;
	private Handler mHandler = new Handler();
	
	private MediaPlayerEventListener mEventListener;
	
	public interface MediaPlayerEventListener {
		public void onProgressChanged(MediaPlayer mp, int progress, boolean fromUser);
		public void onCompletion(MediaPlayer mp);
	}
	
	private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int currentDuration = mMediaPlayer.getCurrentPosition();
            mSeekPlayer.setProgress(currentDuration);
            mHandler.postDelayed(this, 100);
        }
    };
    
    public MediaPlayerImpl(SeekBar seekbar, TextView position, TextView duration) {
		mSeekPlayer = seekbar;
		mTxtPlayerStart = position;
		mTxtPlayerEnd = duration;
	}
    
    public void setMediaPlayerEventListener(MediaPlayerEventListener listener) {
    	mEventListener = listener;
    }
    
    public MediaPlayerImpl prepare(File audioFile) {
    	if (mMediaPlayer == null) {
    		mMediaPlayer = new MediaPlayer();	
    	}
    	
    	try {
			ParcelFileDescriptor afd = ParcelFileDescriptor.open(audioFile, ParcelFileDescriptor.MODE_READ_ONLY);
			
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(afd.getFileDescriptor());
			mMediaPlayer.prepare();
			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					performCompletion(mp);
				}
			});
			
			int duration = mMediaPlayer.getDuration();
			mSeekPlayer.setMax(duration);
			mTxtPlayerStart.setText("00:00");
			mTxtPlayerEnd.setText(millisecondsToTimeString(duration, false));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mSeekPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	        	mHandler.removeCallbacks(mUpdateTimeTask);
	        	if (mMediaPlayer != null) {
	        		mMediaPlayer.seekTo(seekBar.getProgress());
	            	updateProgressBar();
	        	}
	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	        	mHandler.removeCallbacks(mUpdateTimeTask);
	        }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            	mTxtPlayerStart.setText(millisecondsToTimeString(progress, false));
            	performProgressChanged(progress, fromUser);
            }
	    });
		
    	return this;
    }
    
    public boolean isPlaying() {
    	return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }
    
    public boolean isPaused() {
    	return mMediaPlayer != null && !mMediaPlayer.isPlaying();
    }
    
    public void start() {
    	if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
    		mMediaPlayer.start();
    		updateProgressBar();
    	}
    }
    
    public void pause() {
    	if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
    		mMediaPlayer.pause();
    	}
    }
    
    public void stop() {
		mHandler.removeCallbacks(mUpdateTimeTask);
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mSeekPlayer.setProgress(0);
		mTxtPlayerStart.setText("00:00");
		mTxtPlayerEnd.setText("00:00");
	}
    
    public void destroy() {
    	mHandler.removeCallbacks(mUpdateTimeTask);
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.release();
		}
    }
    
    private void performCompletion(MediaPlayer mp) {
    	mHandler.removeCallbacks(mUpdateTimeTask);
    	mSeekPlayer.setProgress(0);
		mTxtPlayerStart.setText("00:00");
		
    	if (mEventListener != null) {
    		mEventListener.onCompletion(mp);
    	}
    }
    
    private void performProgressChanged(int progress, boolean fromUser) {
    	if (mEventListener != null) {
    		mEventListener.onProgressChanged(mMediaPlayer, progress, fromUser);
    	}
    }
    
    protected void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    
    protected String millisecondsToTimeString(int millis, boolean overHours) {
		if (overHours) {
			return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
		            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
		            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		} else {
			return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
		            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		}
	}
}
