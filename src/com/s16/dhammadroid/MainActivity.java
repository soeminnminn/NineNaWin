package com.s16.dhammadroid;

import com.s16.app.AboutPreference;
import com.s16.dhammadroid.data.DhammaDataParser;
import com.s16.dhammadroid.data.ListDataContainer;
import com.s16.drawing.FoldingCirclesDrawable;
import com.s16.drawing.TypefaceSpan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends Activity
	implements SharedPreferences.OnSharedPreferenceChangeListener {

	private MainListFragment mFragment;
	private ListDataContainer mDataContainer;
	
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mDataContainer != null) {
				String entryName = mDataContainer.getEntryName(position);
				if (entryName != null) {
					int index = mDataContainer.getEntryIndex(position);
					Intent intent = new Intent(getContext(), DetailActivity.class);
					intent.putExtra("name", entryName);
					intent.putExtra("position", index);
					startActivity(intent);
				}
			}
		}
	}; 
	
	protected Context getContext() {
		return this;
	}
	
	protected void setActionBarTitle(CharSequence title) {
		if (title != null) {
			SpannableString s = new SpannableString(title);
			s.setSpan(new TypefaceSpan(getContext(), "zawgyi.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			getActionBar().setTitle(s);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setActionBarTitle(Utility.ZawGyiDrawFix(getString(R.string.main_title)));
		if (savedInstanceState == null) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			mFragment = new MainListFragment();
			transaction.replace(R.id.mainContainer, mFragment);
			transaction.commit();
		}
		
		if (mFragment == null) {
			mFragment = (MainListFragment)getFragmentManager().findFragmentById(R.id.mainContainer);	
		}
		
		fetchXML();
		PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
    public void onDestroy() {
		PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItemSettings = menu.findItem(R.id.action_settings);
		if (menuItemSettings != null) {
			MenuItemCompat.setShowAsAction(menuItemSettings, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_settings:
				performSettings();
				break;
			case R.id.action_about:
				AboutPreference.showAboutDialog(getContext());
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Common.PREFS_FONT_SIZE.equals(key)) {
			if (mDataContainer != null) {
				setListAdapter(mDataContainer.getAdapter());
			}
		}
	}
	
	private void performSettings() {
		Intent intent = new Intent(getContext(), SettingsActivity.class);
		startActivity(intent);
	}
	
	private void setListAdapter(ListAdapter adapter) {
		if (mFragment != null) {
			mFragment.setListAdapter(adapter);
			mFragment.setOnItemClickListener(mItemClickListener);
		}
	}
	
	private void fetchXML() {
		new AsyncTask<Context, Void, ListAdapter>() {
			
			@Override
			protected void onPreExecute() {
		    }
			
			@Override
			protected void onPostExecute(ListAdapter result) {
				setListAdapter(result);
		    }

			@Override
			protected ListAdapter doInBackground(Context... params) {
				DhammaDataParser.parse(params[0]);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return buildData();
			}
			
		}.execute(getApplicationContext());
	}
	
	private ListAdapter buildData() {
		mDataContainer = ListDataContainer.newInstance(getContext());
		return mDataContainer.getAdapter();
	}
	
	public static class MainListFragment extends ListFragment {
		
		private AdapterView.OnItemClickListener mItemClickListener; 
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			ViewGroup root = (ViewGroup)super.onCreateView(inflater, container, savedInstanceState);
			ProgressBar progress = findProgressBar(root);
			if (progress != null) {
				Drawable progressDrawable = new FoldingCirclesDrawable.Builder(inflater.getContext()).build();
				progress.setIndeterminateDrawable(progressDrawable);
			}
			return root;
		}
		
		private ProgressBar findProgressBar(ViewGroup root) {
			for(int i=0; i<root.getChildCount(); i++) {
				View v = root.getChildAt(i);
				if (v instanceof ProgressBar) {
					return (ProgressBar)v;
				} else if(v instanceof ViewGroup) {
					ViewGroup vg = (ViewGroup)v;
					return findProgressBar(vg);
				}
			}
			return null;
		}
		
		public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
			mItemClickListener = listener;
		}
		
		@Override  
		public void onListItemClick(ListView l, View v, int position, long id) {
			if (l.getAdapter().getItemViewType(position) == ListDataContainer.RowType.LIST_ITEM.ordinal()) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(l, v, position, id);
				}
			}
		}
	}
}
