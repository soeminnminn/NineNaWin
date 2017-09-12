package com.s16.dhammadroid.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.R;

import java.io.File;

/**
 * Created by SMM on 10/26/2016.
 */
public class AudioPlayerUi extends ContextWrapper {

    public interface UiInteractionCallback {
        public int getCurrentPagePosition();
        public boolean hasAudio(int position);
        public boolean canPlayAudio(int position);
        public String getAudioFile(int position);
        public String getAudioFileUrl(int position);
    }

    private final ViewGroup mPlayerFrame;
    private UiInteractionCallback mCallback;

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

    private View.OnClickListener mDownloadClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!TextUtils.isEmpty(mAudioFileUrl)) {
                long refrenceId = getDownloadRefId();
                if (refrenceId < 0) {
                    refrenceId = mDownloader.download(mAudioFileUrl, Common.getAudioFolder(getApplicationContext()), mAudioFileName);
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
                    updateAudioFrame(getCurrentPagePosition());
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

    public AudioPlayerUi(Context context, ViewGroup playerFrame, UiInteractionCallback callback) {
        super(context);
        mPlayerFrame = playerFrame;
        mCallback = callback;
        initialize();
    }

    protected Context getContext() {
        return getBaseContext();
    }

    private void initialize() {
        if (mPlayerFrame == null) return;

        mTxtPlayerMessage = (TextView)mPlayerFrame.findViewById(R.id.textViewPlayerMessage);
        mTxtPlayerStart = (TextView)mPlayerFrame.findViewById(R.id.textViewPlayerStart);
        mTxtPlayerEnd = (TextView)mPlayerFrame.findViewById(R.id.textViewPlayerEnd);
        mButtonPlay = (ImageButton)mPlayerFrame.findViewById(R.id.imageButtonPlayer);
        mSeekPlayer = (SeekBar)mPlayerFrame.findViewById(R.id.seekBarPlayer);

        mMediaPlayer = new MediaPlayerImpl(getContext(), mSeekPlayer, mTxtPlayerStart, mTxtPlayerEnd);
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
    }

    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.destroy();
        }
        if (mDownloader != null) {
            mDownloader.destroy();
        }
    }

    private int getCurrentPagePosition() {
        if (mCallback == null) return -1;
        return mCallback.getCurrentPagePosition();
    }

    private long getDownloadRefId() {
        int position = getCurrentPagePosition();
        if (position < 0) return -1L;
        String prefsKey = "DownloadRefId_" + position;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        return prefs.getLong(prefsKey, -1L);
    }

    private void setDownloadRefId(long refrenceId) {
        int position = getCurrentPagePosition();
        if (position < 0) return;
        String prefsKey = "DownloadRefId_" + position;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(prefsKey, refrenceId);
        editor.commit();
    }

    private void removeDownloadRefId(){
        int position = getCurrentPagePosition();
        if (position < 0) return;
        String prefsKey = "DownloadRefId_" + position;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (prefs.contains(prefsKey)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(prefsKey);
            editor.commit();
        }
    }

    public void setAudioFrameVisible(boolean visible) {
        mPlayerFrame.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void updateAudioFrame(int position) {
        if (mCallback == null || position < 0) return;
        if (mCallback.hasAudio(position)) {
            mAudioFileName = mCallback.getAudioFile(position);
            mAudioFileUrl =  mCallback.getAudioFileUrl(position);

            mPlayerFrame.setVisibility(View.VISIBLE);
            boolean isDownloading = updateDownloadFrame();
            if (!isDownloading && mCallback.canPlayAudio(position)) {
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

    public void stopPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        if (mButtonPlay != null) {
            mButtonPlay.setImageResource(IC_PLAY);
        }
    }

    protected void createPlayer(String fileName) {
        File audioDir = Common.getAudioFolder(getContext());
        File audioFile = new File(audioDir, fileName);
        mMediaPlayer.prepare(audioFile);
        /*Uri.Builder uriBuilder = Uri.parse("http://192.168.0.168/file/stream").buildUpon();
        uriBuilder.appendQueryParameter("file", "./uploads/song-mp3/CharTate-Jorny.mp3");
        Log.i("AUDIO", "uri=" + uriBuilder.build().toString());
        mMediaPlayer.prepare(uriBuilder.build());*/
    }
}
