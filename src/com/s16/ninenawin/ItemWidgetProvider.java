package com.s16.ninenawin;

import java.util.Calendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class ItemWidgetProvider extends AppWidgetProvider {
	
	private static final String TAG = ItemWidgetProvider.class.getSimpleName();
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Utils.TRACE(TAG, "onUpdate");
		
		ComponentName componentName = new ComponentName(context, ItemWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_details_view);

			ItemDetail item = ResourceData.getItem(context, Calendar.getInstance(), true);
			if(item == null) {
				remoteViews.setTextViewText(R.id.textViewDay, "");
				remoteViews.setTextViewText(R.id.textViewDate, "");
				remoteViews.setTextViewText(R.id.textViewValue, context.getText(R.string.app_mm_desc));
				remoteViews.setTextViewText(R.id.textViewRound, "");
				remoteViews.setTextViewText(R.id.textViewNote, "");
			} else {
				remoteViews.setTextViewText(R.id.textViewDay, item.getDay());
				remoteViews.setTextViewText(R.id.textViewDate, item.getDateString());
				remoteViews.setTextViewText(R.id.textViewValue, item.getValue());
				remoteViews.setTextViewText(R.id.textViewRound, item.getRound());
				if(item.getIsVege()) {
					remoteViews.setTextViewText(R.id.textViewNote, "(" + context.getText(R.string.vege_date) + ")");
				} else {
					remoteViews.setTextViewText(R.id.textViewNote, "");
				}
			}
			
			// Register an onClickListener
		    Intent intent = new Intent(context, ItemWidgetProvider.class);
		    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
	
		    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		    remoteViews.setOnClickPendingIntent(R.id.textViewValue, pendingIntent);
			
		    appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
}
