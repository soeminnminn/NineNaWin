package com.s16.ninenawin;

import android.content.Context;
import android.media.MediaPlayer;

public class AlarmSound {
	// fields
	private Context context = null;
	MediaPlayer mMediaPlayer = null;

	// methods
	public AlarmSound(Context context) {
		this.context = context;
	}

	public void play() {
		mMediaPlayer = MediaPlayer.create(context, R.raw.alarm);
		mMediaPlayer.start();
	}
	
	public void finalize() {
		mMediaPlayer.release();
	}

}
