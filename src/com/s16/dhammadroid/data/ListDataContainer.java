package com.s16.dhammadroid.data;

import java.util.ArrayList;
import java.util.List;

import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ListDataContainer {
	
	public static final String NINENAWIN_CATEGORY = "category_ninenawin";
	public static final String NINENAWIN_ENTRY = "entry_ninenawin";
	public static final int NINENAWIN_ENTRY_ID = 0x1000;
	
	private final Context mContext;
	private ListAdapter mListAdapter;
	private static ListDataContainer INSTANCE = null;
	
	public static ListDataContainer newInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new ListDataContainer(context);
		}
		return INSTANCE;
	}
	
	public ListDataContainer(Context context) {
		mContext = context;
		buildAdapter(context);
	}
	
	protected Context getContext() {
		return mContext;
	}

	public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

	public interface ListItem {
		public String getName();
		public String getTitle();
		public int getViewType();
	    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
	}
	
	public class Header implements ListItem {
		
		private final String mName;
		private final String mTitle;
		
		public Header(String name, String title) {
			mName = name;
			mTitle = title;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getTitle() {
			if (mTitle != null) {
				return Utility.ZawGyiDrawFix(mTitle).toString();
			}
			return "";
		}

		@Override
		public int getViewType() {
			return RowType.HEADER_ITEM.ordinal();
		}

		@Override
		public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
			View view;
	        if (convertView == null) {
	            view = (View)inflater.inflate(R.layout.simple_list_header, parent, false);
	        } else {
	            view = convertView;
	        }

	        TextView text = (TextView)view.findViewById(android.R.id.text1);
	        Common.setViewTextSize(text);
	        text.setTypeface(Common.getZawgyiTypeface(inflater.getContext()));
	        text.setText(getTitle());
	        view.setClickable(false);
	        return view;
		}
	}
	
	public class Item implements ListItem {
		
		private final String mName;
		private final String mTitle;
		
		public Item(String name, String title) {
			mName = name;
			mTitle = title;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getTitle() {
			if (mTitle != null) {
				return Utility.ZawGyiDrawFix(mTitle).toString();
			}
			return "";
		}

		@Override
		public int getViewType() {
			return RowType.LIST_ITEM.ordinal();
		}

		@Override
		public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
			View view;
	        if (convertView == null) {
	            view = (View)inflater.inflate(R.layout.simple_list_item, parent, false);
	        } else {
	            view = convertView;
	        }

	        TextView text = (TextView)view.findViewById(android.R.id.text1);
	        Common.setViewTextSize(text);
	        text.setTypeface(Common.getZawgyiTypeface(inflater.getContext()));
	        text.setText(getTitle());

	        return view;
		}
		
	}
	
	public static class ListDataAdapter extends ArrayAdapter<ListItem> {

		private final LayoutInflater mInflater;
		
		public ListDataAdapter(Context context, List<ListItem> items) {
			super(context, 0, items);
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
	    public int getViewTypeCount() {
	        return RowType.values().length;
	    }

	    @Override
	    public int getItemViewType(int position) {
	        return getItem(position).getViewType();
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return getItem(position).getView(mInflater, convertView, parent);
	    }
	}
	
	private void buildAdapter(Context context) {
		if (DhammaDataParser.CATEGORIES.size() > 0
				&& DhammaDataParser.ENTRIES.size() > 0) {
			List<ListItem> items = new ArrayList<ListDataContainer.ListItem>();
			
			for(int c=0; c<DhammaDataParser.CATEGORIES.size(); c++) {
				DhammaDataParser.Category category =  DhammaDataParser.CATEGORIES.get(c);
				if (category != null) {
					Header header = new Header(category.name, category.title);
					items.add(header);
					
					if (category.entries.size() > 0) {
						for(int i=0; i<category.entries.size(); i++) {
							int entryIndex = category.entries.get(i).intValue();
							DhammaDataParser.Entry entry = DhammaDataParser.ENTRIES.get(entryIndex);
							if (entry != null) {
								Item item = new Item(entry.name, entry.title);
								items.add(item);
							}
						}
					}
				}
			}
			
			final Resources res = context.getResources();
			items.add(new Header(NINENAWIN_CATEGORY, res.getString(R.string.nn_app_group_mm)));
			items.add(new Item(NINENAWIN_ENTRY, res.getString(R.string.nn_app_mm)));
			
			mListAdapter = new ListDataAdapter(context, items);
		}
	}
	
	public ListAdapter getAdapter() {
		return mListAdapter;
	}
	
	public String getEntryName(int position) {
		if (getAdapter() != null) {
			ListItem item = (ListItem)getAdapter().getItem(position);
			return item.getName();
		}
		return null;
	}
	
	public int getEntryIndex(int position) {
		String name = getEntryName(position);
		if (name != null) {
			if (name.equals(NINENAWIN_ENTRY)) {
				return NINENAWIN_ENTRY_ID;
			}
			for(int i=0; i<DhammaDataParser.ENTRIES.size();i++) {
				DhammaDataParser.Entry entry = DhammaDataParser.ENTRIES.get(i);
				if (name.equals(entry.name)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public DhammaDataParser.Entry findEntry(String name) {
		if (name != null) {
			if (name.equals(NINENAWIN_ENTRY)) {
				return null;
			}
			for(int i=0; i<DhammaDataParser.ENTRIES.size();i++) {
				DhammaDataParser.Entry entry = DhammaDataParser.ENTRIES.get(i);
				if (name.equals(entry.name)) {
					return entry;
				}
			}
		}
		return null;
	}
	
	public DhammaDataParser.Entry findEntry(int position) {
		String name = getEntryName(position);
		return findEntry(name);
	}

}
