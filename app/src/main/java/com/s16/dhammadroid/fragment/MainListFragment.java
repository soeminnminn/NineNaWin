package com.s16.dhammadroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.s16.app.MaterialListFragment;
import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.activity.DetailActivity;
import com.s16.dhammadroid.data.DhammaDataParser;
import com.s16.dhammadroid.adapter.ListDataContainer;

/**
 * Created by SMM on 10/21/2016.
 */
public class MainListFragment extends MaterialListFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String TAG = MainListFragment.class.getSimpleName();
    private ListDataContainer mDataContainer;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchXML();
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (listView.getAdapter().getItemViewType(position) == ListDataContainer.RowType.LIST_ITEM.ordinal()) {
            performItemClick(listView, view, position, id);
        }
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Common.PREFS_FONT_SIZE.equals(key)) {
            if (mDataContainer != null) {
                setListAdapter(mDataContainer.getAdapter());
            }
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

        }.execute(getContext());
    }

    private ListAdapter buildData() {
        mDataContainer = ListDataContainer.newInstance(getContext());
        return mDataContainer.getAdapter();
    }

    private void performItemClick(AdapterView<?> parent, View view, int position, long id) {
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
}
