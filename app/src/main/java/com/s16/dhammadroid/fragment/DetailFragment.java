package com.s16.dhammadroid.fragment;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;
import com.s16.dhammadroid.data.DhammaDataParser;

/**
 * Created by SMM on 10/21/2016.
 */
public class DetailFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    public interface DetailInteractionCallback {
        public void setTitle(CharSequence title);
    }

    private String mTitle;
    private TextView mTextDesc;
    private TextView mTextSubTitle;
    private TextView mTextSubDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_detail, container, false);

        int location = getArgsPosition();
        DhammaDataParser.Entry entry = DhammaDataParser.ENTRIES.get(location);
        if (entry != null) {
            mTitle = entry.title;

            mTextDesc = (TextView)rootView.findViewById(R.id.textDetailDesc);
            Common.setViewTextSize(mTextDesc);
            mTextDesc.setTypeface(Common.getZawgyiTypeface(getContext()));
            mTextDesc.setText(Html.fromHtml(Utility.ZawGyiDrawFix(entry.body).toString()));

            mTextSubTitle = (TextView)rootView.findViewById(R.id.textDetailSubTitle);
            mTextSubDesc = (TextView)rootView.findViewById(R.id.textDetailSubDesc);
            Common.setViewTextSize(mTextSubTitle);
            Common.setViewTextSize(mTextSubDesc);
            if (!TextUtils.isEmpty(entry.description_title) && !TextUtils.isEmpty(entry.description_body)) {
                mTextSubTitle.setTypeface(Common.getZawgyiTypeface(getContext()), Typeface.BOLD);
                mTextSubTitle.setText(Utility.ZawGyiDrawFix(entry.description_title));

                mTextSubDesc.setTypeface(Common.getZawgyiTypeface(getContext()));
                mTextSubDesc.setText(Html.fromHtml(Utility.ZawGyiDrawFix(entry.description_body).toString()));

            } else {
                mTextSubTitle.setVisibility(View.GONE);
                mTextSubDesc.setVisibility(View.GONE);
            }
        }

        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
        return rootView;
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Common.PREFS_FONT_SIZE.equals(key)) {
            Common.setViewTextSize(mTextDesc);
            Common.setViewTextSize(mTextSubTitle);
            Common.setViewTextSize(mTextSubDesc);
        }
    }

    protected int getArgsPosition() {
        Bundle extras = getArguments();
        if (extras != null) {
            return extras.getInt("position");
        } else {
            return 0;
        }
    }

    protected String getArgsEntryName() {
        Bundle extras = getArguments();
        if (extras != null) {
            return extras.getString("name", "");
        } else {
            return "";
        }
    }
}
