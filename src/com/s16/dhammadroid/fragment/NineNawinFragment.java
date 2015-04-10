package com.s16.dhammadroid.fragment;

import java.util.Calendar;
import java.util.List;

import com.s16.dhammadroid.Constants;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;
import com.s16.dhammadroid.data.NineNawinResData;
import com.s16.dhammadroid.data.NineNawinResData.ItemDetail;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NineNawinFragment extends Fragment {
	
	public static class ItemListAdapter	extends ArrayAdapter<NineNawinResData.ItemDetail> {

		private final LayoutInflater mInflater;
		private final Context mContext;
		private final Typeface mTypeface;
		
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
		
		public ItemListAdapter(Context context, ItemDetail[] objects) {
			super(context, 0, objects);
			mInflater = LayoutInflater.from(context);
			mContext = context;
			mTypeface = Constants.getZawgyiTypeface(context);
			
			final Resources res = context.getResources();
			mBkgGreen = res.getDrawable(R.drawable.ninenawin_bkg_green);
			mBkgGreenSelected = res.getDrawable(R.drawable.ninenawin_bkg_green_selected);
			mBkgPink = res.getDrawable(R.drawable.ninenawin_bkg_pink);
			mBkgYellow = res.getDrawable(R.drawable.ninenawin_bkg_yellow);
		}
		
		@SuppressWarnings("deprecation")
		@SuppressLint("InflateParams")
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.item_details_view, null);
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
			
			final ItemDetail item = getItem(position);
			holder.textViewDay.setText(Utility.ZawGyiDrawFix(item.getDay()));
			holder.textViewDate.setText(item.getDateString());
			holder.textViewValue.setText(Utility.ZawGyiDrawFix(item.getValue()));
			holder.textViewRound.setText(Utility.ZawGyiDrawFix(item.getRound()));
			
			if(NineNawinResData.getIsToday(item)) {
				if(item.getIsVege()) {
					holder.textViewNote.setText("(" + Utility.ZawGyiDrawFix(mContext.getText(R.string.nn_vege_date)) + ")");
					holder.linearLayoutItem.setBackgroundDrawable(mBkgGreenSelected);
				} else {
					holder.textViewNote.setText(null);
					holder.linearLayoutItem.setBackgroundDrawable(mBkgGreen);
				}
			} else {
				if(item.getIsVege()) {
					holder.textViewNote.setText("(" + Utility.ZawGyiDrawFix(mContext.getText(R.string.nn_vege_date)) + ")");
					holder.linearLayoutItem.setBackgroundDrawable(mBkgPink);
				} else {
					holder.textViewNote.setText(null);
					holder.linearLayoutItem.setBackgroundDrawable(mBkgYellow);
				}
			}
			
			return convertView;
		}
	}
	
	private ListView mListView;
	private ItemListAdapter mItemListAdapter;
	
	protected Context getContext() {
		return getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nine_nawin, container, false);
		mListView = (ListView)rootView.findViewById(R.id.listViewNineNawin);
		
		int position = getArgsPosition();
		Calendar startDate = NineNawinResData.getStartDate(position);
		List<ItemDetail> array = NineNawinResData.getResults(getContext(), position + 1, startDate);
		mItemListAdapter = new ItemListAdapter(getContext(), array.toArray(new ItemDetail[array.size()]));
		mListView.setAdapter(mItemListAdapter);
		
		return rootView;
	}
	
	public void notifyDataSetChanged() {
		if (mListView == null) return;
		int position = getArgsPosition();
		Calendar startDate = NineNawinResData.getStartDate(position);
		List<ItemDetail> array = NineNawinResData.getResults(getContext(), position + 1, startDate);
		mItemListAdapter = new ItemListAdapter(getContext(), array.toArray(new ItemDetail[array.size()]));
		mListView.setAdapter(mItemListAdapter);
	}
	
	public CharSequence getTitle() {
		CharSequence title = getContext().getString(R.string.nn_app_mm);
		int position = getArgsPosition();
		if (position > NineNawinResData.LEVEL_COUNT) {
			return Utility.ZawGyiDrawFix(title);
		}
		return Utility.ZawGyiDrawFix(title + " - " +NineNawinResData.getLevels(getContext())[position]);
	}
	
	protected int getArgsPosition() {
		Bundle extras = getArguments();
		if (extras != null) {
			return extras.getInt("position");
		}
		return 0;
	}
	
	protected String getArgsEntryName() {
		Bundle extras = getArguments();
		if (extras != null) {
			return extras.getString("name", "");
		} 
		return "";
	}
}
