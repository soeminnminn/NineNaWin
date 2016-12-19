package com.s16.dhammadroid.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.s16.app.NotifyDataSetChangedListener;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.adapter.NineNawinListAdapter;
import com.s16.dhammadroid.data.NineNawinResData;

import java.util.Calendar;
import java.util.List;

/**
 * Created by SMM on 10/21/2016.
 */
public class NineNawinFragment extends Fragment
        implements NotifyDataSetChangedListener {

    private ListView mListView;
    private NineNawinListAdapter mListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_nine_nawin, container, false);

        mListView = (ListView)rootView.findViewById(R.id.listView);
        notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void notifyDataSetChanged() {
        if (mListView == null) return;
        int position = getArgsPosition();
        Calendar startDate = NineNawinResData.getStartDate(position);
        List<NineNawinResData.ItemDetail> array = NineNawinResData.getResults(getContext(), position + 1, startDate);
        mListAdapter = new NineNawinListAdapter(getContext(), array.toArray(new NineNawinResData.ItemDetail[array.size()]));
        mListView.setAdapter(mListAdapter);
    }

    protected int getArgsPosition() {
        Bundle extras = getArguments();
        if (extras != null) {
            return extras.getInt("position");
        }
        return 0;
    }
}
