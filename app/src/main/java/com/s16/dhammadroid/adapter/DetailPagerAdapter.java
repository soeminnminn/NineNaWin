package com.s16.dhammadroid.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;

import com.s16.app.NotifyDataSetChangedListener;
import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;
import com.s16.dhammadroid.data.DhammaDataParser;
import com.s16.dhammadroid.data.NineNawinResData;
import com.s16.dhammadroid.fragment.DetailFragment;
import com.s16.dhammadroid.fragment.NineNawinFragment;

import java.io.File;

/**
 * Created by SMM on 10/24/2016.
 */
public class DetailPagerAdapter extends FragmentPagerAdapter {

    private final DetailAdapter mBaseAdapter;
    private Fragment[] mFragmentArray;

    public DetailPagerAdapter(FragmentManager manager, DetailAdapter baseAdapter) {
        super(manager);
        mBaseAdapter = baseAdapter;
        mFragmentArray = new Fragment[getCount()];
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragmentArray[position] == null) {
            mFragmentArray[position] = mBaseAdapter.createFragment();
        }
        Bundle args = new Bundle();
        args.putInt("position", position);
        //args.putString("name", getArgsEntryName());
        mFragmentArray[position].setArguments(args);

        return mFragmentArray[position];
    }

    @Override
    public int getCount() {
        return mBaseAdapter.getCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mBaseAdapter.getPageTitle(position);
    }

    @Override
    public void notifyDataSetChanged() {
        for (int i=0;i<getCount();i++) {
            if (mFragmentArray[i] != null &&
                    mFragmentArray[i] instanceof NotifyDataSetChangedListener) {
                ((NotifyDataSetChangedListener)mFragmentArray[i]).notifyDataSetChanged();
            }
        }
        super.notifyDataSetChanged();
    }

    public boolean hasAudio(int position) {
        return mBaseAdapter.hasAudio(position);
    }

    public boolean canPlayAudio(int position) {
        return mBaseAdapter.canPlayAudio(position);
    }

    public String getAudioFile(int position) {
        return mBaseAdapter.getAudioFile(position);
    }

    public String getAudioFileUrl(int position) {
        return mBaseAdapter.getAudioFileUrl(position);
    }

    public static interface DetailAdapter {
        public int getCount();
        public CharSequence getPageTitle(int position);
        public Fragment createFragment();

        public boolean hasAudio(int position);
        public boolean canPlayAudio(int position);
        public String getAudioFile(int position);
        public String getAudioFileUrl(int position);
    }

    public static class DetailPagePagerAdapter implements DetailAdapter {

        private final Context mContext;

        public DetailPagePagerAdapter(Context context) {
            mContext = context;
        }

        private Context getContext() {
            return mContext;
        }

        private DhammaDataParser.Entry getEntry(int position) {
            if (position > -1 && position < getCount()) {
                return DhammaDataParser.ENTRIES.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return DhammaDataParser.ENTRIES.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            DhammaDataParser.Entry entry = getEntry(position);
            if (entry != null) {
                return Utility.ZawGyiDrawFix(entry.title);
            }
            return null;
        }

        @Override
        public Fragment createFragment() {
            return new DetailFragment();
        }

        @Override
        public boolean hasAudio(int position) {
            DhammaDataParser.Entry entry = getEntry(position);
            if (entry != null) {
                return !TextUtils.isEmpty(entry.soundFile) && !TextUtils.isEmpty(entry.soundUrl);
            }
            return false;
        }

        @Override
        public boolean canPlayAudio(int position) {
            DhammaDataParser.Entry entry = getEntry(position);
            if (entry != null && !TextUtils.isEmpty(entry.soundFile)) {
                File audioDir = Common.getAudioFolder(getContext());
                if (audioDir != null) {
                    File audioFile = new File(audioDir, entry.soundFile);
                    return audioFile.exists();
                }
            }
            return false;
        }

        @Override
        public String getAudioFile(int position) {
            DhammaDataParser.Entry entry = getEntry(position);
            if (entry != null && !TextUtils.isEmpty(entry.soundFile)) {
                return entry.soundFile;
            }
            return null;
        }

        @Override
        public String getAudioFileUrl(int position) {
            DhammaDataParser.Entry entry = getEntry(position);
            if (entry != null && !TextUtils.isEmpty(entry.soundUrl)) {
                return entry.soundUrl;
            }
            return null;
        }
    }

    public static class NineNawinPagerAdapter implements DetailAdapter {

        private final Context mContext;

        public NineNawinPagerAdapter(Context context) {
            mContext = context;
        }

        private Context getContext() {
            return mContext;
        }

        @Override
        public int getCount() {
            return NineNawinResData.LEVEL_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence title = getContext().getString(R.string.nn_app_mm);
            return Utility.ZawGyiDrawFix(title + " - " + NineNawinResData.getLevels(getContext())[position]);
        }

        @Override
        public Fragment createFragment() {
            return new NineNawinFragment();
        }

        @Override
        public boolean hasAudio(int position) {
            return false;
        }

        @Override
        public boolean canPlayAudio(int position) {
            return false;
        }

        @Override
        public String getAudioFile(int position) {
            return null;
        }

        @Override
        public String getAudioFileUrl(int position) {
            return null;
        }
    }

}
