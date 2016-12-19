package com.s16.dhammadroid.utils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

public class Downloader {
	
	private static Downloader INSTANCE;
	
	private final Context mContext;
	private final DownloadManager mDownloadManager;
	private HashMap<Long, Uri> mMap = new LinkedHashMap<Long, Uri>();
	private OnDownloadCompleteListener mOnDownloadCompleteListener;
	
	public static Downloader newInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new Downloader(context);
		}
		return INSTANCE;
	}
	
	public static class ResultStatus {
		public int status;
		public int reason;
		public String filename;
	}
	
	public interface OnDownloadCompleteListener {
		public void onDownloadComplete(Uri uri, long refrenceId);
	}
	
	private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			//check if the broadcast message is for our Enqueued download
			long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			for(Long key : mMap.keySet()) {
				if(referenceId == key.longValue()) {
					onDownloadComplete(mMap.get(key), referenceId);
				}
			}
		}
	};
	
	private Downloader(Context context) {
		mContext = context;
		mDownloadManager = (DownloadManager)context.getSystemService(android.app.Activity.DOWNLOAD_SERVICE);
		context.registerReceiver(mDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
	
	protected Context getContext() {
		return mContext;
	}
	
	public long download(String url, File destDir, String destFileName) {
		return download(url, destDir, destFileName, destFileName, destFileName);
	}
	
	public long download(String url, File destDir, String destFileName, String title, String description) {
		
		Uri uri = Uri.parse(url);
		File destFile = new File(destDir, destFileName);
		if (destFile.exists()) {
			return 0L;
		}
		
		DownloadManager.Request request = new DownloadManager.Request(uri);
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
		request.setAllowedOverRoaming(false);
		request.setTitle(title);
		request.setDescription(description);
		request.setDestinationUri(Uri.fromFile(destFile));
		
		if (mDownloadManager != null) {
			long refrenceId = mDownloadManager.enqueue(request);
			mMap.put(Long.valueOf(refrenceId), uri);
			return refrenceId;
		}

		return -1L;
	}
	
	public void openDownloadActivity() {
		Intent intent = new Intent();
		intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
		getContext().startActivity(intent);
	}
	
	public void cancelDownload(long refrenceId) {
		if (mDownloadManager != null) {
			mDownloadManager.remove(refrenceId);
		}
	}
	
	public ResultStatus checkStatus(long refrenceId) {
		Query downloadQuery = new Query();
		//set the query filter to our previously Enqueued download 
		downloadQuery.setFilterById(refrenceId);

		//Query the download manager about downloads that have been requested.
		Cursor cursor = mDownloadManager.query(downloadQuery);
		if(cursor != null && cursor.moveToFirst()){
			ResultStatus rstStatus = new ResultStatus();
			
			//column for status
			int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
			rstStatus.status = cursor.getInt(columnIndex);
			//column for reason code if the download failed or paused
			int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
			rstStatus.reason = cursor.getInt(columnReason);
			//get the download filename
			int columnFilename = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
			rstStatus.filename = cursor.getString(columnFilename);
			
			cursor.close();
			return rstStatus;
		}
		return null;
	}
	
	public void setOnDownloadCompleteListener(OnDownloadCompleteListener listener) {
		mOnDownloadCompleteListener = listener;
	}
	
	private void onDownloadComplete(Uri uri, long refrenceId) {
		if (mOnDownloadCompleteListener != null) {
			mOnDownloadCompleteListener.onDownloadComplete(uri, refrenceId);
		}
	}
	
	public void destroy() {
		try {
			getContext().unregisterReceiver(mDownloadReceiver);
		} catch (IllegalArgumentException ex) {
			
		}
	}
}
