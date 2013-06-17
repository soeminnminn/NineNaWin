package com.s16.ninenawin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

public class NotificationService extends Service {

	private NotificationReceiver mNotificationReceiver;
	private Handler handlerUpdate = new Handler();
	private final static int iHandlerUpdateTime = 1000 * 3;
	
	public NotificationService(Context context) {
	}
	
	@Override
	public void onCreate() {
		//schedule handler update date task
		handlerUpdate.removeCallbacks(handlerUpdateTask);
		handlerUpdate.postDelayed(handlerUpdateTask, iHandlerUpdateTime);		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
	}
	
	@Override
	public void onDestroy() {
		handlerUpdate.removeCallbacks(handlerUpdateTask);		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private Runnable handlerUpdateTask = new Runnable() {
		public void run() {
			try {
				RefreshData();
			} finally {
				handlerUpdate.postDelayed(this, iHandlerUpdateTime);
			}
		}
	};
  
	private synchronized void RefreshData() {
	}
	
	@SuppressWarnings("unused")
	private void setNotification(boolean visible) {
    	final String ACTION = "com.s16.ninenawin.NOTIFY";
        final int ID = 1;
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        
        if (visible && mNotificationReceiver == null) {
            int icon = R.drawable.stat_notify_alarm;
            CharSequence text = "Keyboard notification enabled.";
            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, text, when);

            mNotificationReceiver = new NotificationReceiver(this);
            final IntentFilter pFilter = new IntentFilter(ACTION);
            registerReceiver(mNotificationReceiver, pFilter);
            
            Intent notificationIntent = new Intent(ACTION);
            PendingIntent contentIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, notificationIntent, 0);
            
            String title = "Show Hacker's Keyboard";
            String body = "Select this to open the keyboard. Disable in settings.";
            
            notification.flags |= Notification.FLAG_ONGOING_EVENT; // | Notification.FLAG_NO_CLEAR;
            notification.setLatestEventInfo(getApplicationContext(), title, body, contentIntent);
            notificationManager.notify(ID, notification);
        } else if (mNotificationReceiver != null) {
            notificationManager.cancel(ID);
            unregisterReceiver(mNotificationReceiver);
            mNotificationReceiver = null;
        }
    }
}