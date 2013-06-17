package com.s16.ninenawin;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemListBaseAdapter extends BaseAdapter {

	//private static final int[] EMPTY_STATE_SET = null;
	//private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
	//private static final int[] LONG_PRESSABLE_STATE_SET = { android.R.attr.state_long_pressable };
	
	private ArrayList<ItemDetail> itemDetailArrayList;
	
	private Drawable mBkgGreen;
	private Drawable mBkgGreenSelected;
	private Drawable mBkgPink;
	private Drawable mBkgYellow;
	
	static class ViewHolder {
		TextView textViewDay;
		TextView textViewDate;
		TextView textViewValue;
		TextView textViewRound;
		TextView textViewNote;
		LinearLayout linearLayoutItem;
	}
	
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private Typeface mTypeface;
	
	public ItemListBaseAdapter(Context context, ArrayList<ItemDetail> results, Typeface typeface) {
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		itemDetailArrayList = results;
		mTypeface = typeface;
		
		final Resources res = mContext.getResources();
		mBkgGreen = res.getDrawable(R.drawable.bkg_green);
		mBkgGreenSelected = res.getDrawable(R.drawable.bkg_green_selected);
		mBkgPink = res.getDrawable(R.drawable.bkg_pink);
		mBkgYellow = res.getDrawable(R.drawable.bkg_yellow);
	}
	
	@Override
	public int getCount() {
		return itemDetailArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemDetailArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_details_view, null);
			holder = new ViewHolder();
			
			holder.textViewDay = (TextView)convertView.findViewById(R.id.textViewDay);
			holder.textViewDate = (TextView)convertView.findViewById(R.id.textViewDate);
			holder.textViewValue = (TextView)convertView.findViewById(R.id.textViewValue);
			holder.textViewRound = (TextView)convertView.findViewById(R.id.textViewRound);
			holder.textViewNote = (TextView)convertView.findViewById(R.id.textViewNote);
			holder.linearLayoutItem = (LinearLayout)convertView.findViewById(R.id.linearLayoutItem);
			
			holder.textViewDay.setTypeface(mTypeface);
			holder.textViewDate.setTypeface(mTypeface);
			holder.textViewValue.setTypeface(mTypeface);
			holder.textViewRound.setTypeface(mTypeface);
			holder.textViewNote.setTypeface(mTypeface);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		final ItemDetail item = itemDetailArrayList.get(position);
		holder.textViewDay.setText(item.getDay());
		holder.textViewDate.setText(item.getDateString());
		holder.textViewValue.setText(item.getValue());
		holder.textViewRound.setText(item.getRound());
		
		if(getIsToday(item)) {
			if(item.getIsVege()) {
				holder.textViewNote.setText("(" + mContext.getText(R.string.vege_date) + ")");
				holder.linearLayoutItem.setBackgroundDrawable(mBkgGreenSelected);
			} else {
				holder.textViewNote.setText(null);
				holder.linearLayoutItem.setBackgroundDrawable(mBkgGreen);
			}
		} else {
			if(item.getIsVege()) {
				holder.textViewNote.setText("(" + mContext.getText(R.string.vege_date) + ")");
				holder.linearLayoutItem.setBackgroundDrawable(mBkgPink);
			} else {
				holder.textViewNote.setText(null);
				holder.linearLayoutItem.setBackgroundDrawable(mBkgYellow);
			}
		}
		
		return convertView;
	}
	
	public void setData(ArrayList<ItemDetail> value) {
		itemDetailArrayList = value;
		super.notifyDataSetChanged();
	}
	
	private boolean getIsToday(ItemDetail item) {
		Calendar rightNow = Calendar.getInstance(); 
		return (Utils.dateCompare(rightNow, item.getDate()) == 0);
	}

}
