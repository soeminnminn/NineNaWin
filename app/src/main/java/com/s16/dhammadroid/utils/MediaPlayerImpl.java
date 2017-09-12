package com.s16.dhammadroid.utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.widget.SeekBar;
import android.widget.TextView;

public class MediaPlayerImpl {

	private final String mZeroDuration = String.format("%02d:%02d", 0, 0);

	private final Context mContext;
	private MediaPlayer mMediaPlayer;
	private final SeekBar mSeekPlayer;
	private final TextView mTxtPlayerStart;
	private final TextView mTxtPlayerEnd;
	private boolean mIsPrepared;
	private long mUiThreadId;
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
    
    public MediaPlayerImpl(Context context, SeekBar seekbar, TextView position, TextView duration) {
		mContext = context;
		mSeekPlayer = seekbar;
		mTxtPlayerStart = position;
		mTxtPlayerEnd = duration;
		mUiThreadId = Thread.currentThread().getId();
	}

	protected Context getContext() {
		return mContext;
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
			mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mIsPrepared = true;
					int duration = mMediaPlayer.getDuration();
					mSeekPlayer.setMax(duration);
					mTxtPlayerStart.setText(mZeroDuration);
					mTxtPlayerEnd.setText(millisecondsToTimeString(duration, false));
				}
			});

			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					performCompletion(mp);
				}
			});
			
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

	public MediaPlayerImpl prepare(Uri audioUri) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}

		try {
			mMediaPlayer.reset();
			//mMediaPlayer.setDataSource(getContext(), audioUri);
			mMediaPlayer.setDataSource(audioUri.toString());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mIsPrepared = true;
					int duration = mMediaPlayer.getDuration();
					mSeekPlayer.setMax(duration);
					mTxtPlayerStart.setText(mZeroDuration);
					mTxtPlayerEnd.setText(millisecondsToTimeString(duration, false));
				}
			});
			mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					performCompletion(mp);
				}
			});

			mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					mSeekPlayer.setSecondaryProgress(percent);
				}
			});

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
    	return mMediaPlayer != null && mIsPrepared && mMediaPlayer.isPlaying();
    }
    
    public boolean isPaused() {
    	return mMediaPlayer != null && mIsPrepared && !mMediaPlayer.isPlaying();
    }
    
    public void start() {
		if (mMediaPlayer != null) {
			if (!mIsPrepared) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						start();
					}
				}, 200);

			} else if (!mMediaPlayer.isPlaying()) {
				mMediaPlayer.start();
				updateProgressBar();
			}
		}
    }
    
    public void pause() {
    	if (mMediaPlayer != null && mIsPrepared && mMediaPlayer.isPlaying()) {
    		mMediaPlayer.pause();
    	}
    }
    
    public void stop() {
		if (mIsPrepared) {
			mHandler.removeCallbacks(mUpdateTimeTask);
			if (mMediaPlayer != null) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				mMediaPlayer.release();
				mIsPrepared = false;
				mMediaPlayer = null;
			}
			mSeekPlayer.setProgress(0);
			mTxtPlayerStart.setText(mZeroDuration);
			mTxtPlayerEnd.setText(mZeroDuration);
		}
	}
    
    public void destroy() {
		if (mIsPrepared) {
			mHandler.removeCallbacks(mUpdateTimeTask);
			if (mMediaPlayer != null) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				mMediaPlayer.release();
				mIsPrepared = false;
			}
		}
    }
    
    private void performCompletion(MediaPlayer mp) {
    	mHandler.removeCallbacks(mUpdateTimeTask);
    	mSeekPlayer.setProgress(0);
		mTxtPlayerStart.setText(mZeroDuration);
		
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

	protected void runOnUiThread(Runnable runnable, boolean checkThread) {
		if (getContext() instanceof Activity) {
			if (checkThread) {
				if (Thread.currentThread().getId() != mUiThreadId) {
					((Activity)getContext()).runOnUiThread(runnable);
				} else {
					runnable.run();
				}
			} else {
				((Activity)getContext()).runOnUiThread(runnable);
			}
		}
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
