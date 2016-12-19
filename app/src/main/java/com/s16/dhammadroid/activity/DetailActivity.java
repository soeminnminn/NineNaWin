package com.s16.dhammadroid.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.s16.app.AboutPreference;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.adapter.DetailPagerAdapter;
import com.s16.dhammadroid.adapter.ListDataContainer;
import com.s16.dhammadroid.data.NineNawinResData;
import com.s16.dhammadroid.fragment.NineNawinSetDateFragment;
import com.s16.dhammadroid.utils.AudioPlayerUi;
import com.s16.drawing.TypefaceSpan;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, AudioPlayerUi.UiInteractionCallback {

    protected static final String TAG = DetailActivity.class.getSimpleName();

    private static final int PERMISSION_ACCESS_CODE = 0x1121;
    private static final String[] PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    };

    private ViewPager mViewPager;
    private DetailPagerAdapter mPagerAdapter;
    private AudioPlayerUi mAudioPlayerUi;

    private View.OnClickListener mFabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            performFloatingClick();
        }
    };

    protected Context getContext() {
        return this;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new TypefaceSpan(getContext(), "zawgyi.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            super.setTitle(s);

        } else {
            super.setTitle(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestPermission();

        View fab = findViewById(R.id.fab);
        fab.setOnClickListener(mFabClick);

        mViewPager = (ViewPager)findViewById(R.id.viewPager);

        mAudioPlayerUi = new AudioPlayerUi(getContext(), (ViewGroup)findViewById(R.id.playerTab), this);

        if (isNineNawin()) {
            NineNawinResData.loadPreferences(getContext());

            int position = NineNawinResData.getLastLevel() - 1;
            DetailPagerAdapter.DetailAdapter adapter = new DetailPagerAdapter.NineNawinPagerAdapter(getContext());
            mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), adapter);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setCurrentItem(position);
            setTitle(mPagerAdapter.getPageTitle(position));

            mAudioPlayerUi.setAudioFrameVisible(false);

        } else {
            int position = getArgsPosition();
            DetailPagerAdapter.DetailAdapter adapter = new DetailPagerAdapter.DetailPagePagerAdapter(getContext());
            mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), adapter);
            mViewPager.setAdapter(mPagerAdapter);
            mViewPager.setCurrentItem(position);
            setTitle(mPagerAdapter.getPageTitle(position));

            fab.setVisibility(View.GONE);

            mAudioPlayerUi.setAudioFrameVisible(true);
            mAudioPlayerUi.updateAudioFrame(position);
        }
        mViewPager.addOnPageChangeListener(this);
    }

    private void requestPermission() {

        boolean isGranted = true;
        for (String permission : PERMISSIONS) {
            isGranted = isGranted && (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED);
        }

        if (!isGranted) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ACCESS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isGranted = grantResults.length > 0;
        if (requestCode == PERMISSION_ACCESS_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    Log.i(TAG, "isGranted = " + isGranted);
                    isGranted = isGranted && (grantResult == PackageManager.PERMISSION_GRANTED);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!isGranted) {
            // Not access
            finish();
        }
    }

    @Override
    public void onDestroy() {
        mAudioPlayerUi.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isNineNawin()) {
            getMenuInflater().inflate(R.menu.ninenawin, menu);
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_set_date:
                performNinNawinSetDate();
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTitle(mPagerAdapter.getPageTitle(position));
        mAudioPlayerUi.stopPlayer();
        mAudioPlayerUi.updateAudioFrame(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void performSettings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void performNinNawinSetDate() {
        NineNawinSetDateFragment fragment = new NineNawinSetDateFragment();

        int index = mViewPager.getCurrentItem();
        int dayValue = NineNawinResData.getDayValue(index + 1);
        Calendar startDate = NineNawinResData.getStartDate(index);
        if (startDate != null) {
            Bundle args = new Bundle();
            args.putInt("selectDay", dayValue);
            args.putLong("selectedDate", startDate.getTimeInMillis());
            fragment.setArguments(args);
        }

        fragment.setOnSetDateListener(new NineNawinSetDateFragment.OnSetDateListener() {

            @Override
            public void onSetDate(Calendar value) {
                int currentLevel = mViewPager.getCurrentItem();
                NineNawinResData.setStartDate(value, currentLevel);
                mPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onClearDate() {
                NineNawinResData.reset();
                mPagerAdapter.notifyDataSetChanged();
            }

        });
        fragment.show(getSupportFragmentManager(), "NineNawinSetDateFragment");
    }

    private void performFloatingClick() {
        Intent intent = new Intent(getContext(), CounterActivity.class);
        startActivity(intent);
    }

    protected boolean isNineNawin() {
        return (ListDataContainer.NINENAWIN_ENTRY.equals(getArgsEntryName()));
    }

    protected String getArgsEntryName() {
        Bundle extras = getIntent().getExtras();
        return extras.getString("name");
    }

    protected int getArgsPosition() {
        Bundle extras = getIntent().getExtras();
        return extras.getInt("position");
    }

    @Override
    public int getCurrentPagePosition() {
        if (mViewPager != null) {
            return mViewPager.getCurrentItem();
        }
        return 0;
    }

    @Override
    public boolean hasAudio(int position) {
        if (mPagerAdapter == null) {
            return false;
        }
        return mPagerAdapter.hasAudio(position);
    }

    @Override
    public boolean canPlayAudio(int position) {
        if (mPagerAdapter == null) {
            return false;
        }
        return mPagerAdapter.canPlayAudio(position);
    }

    @Override
    public String getAudioFile(int position) {
        if (mPagerAdapter == null) {
            return null;
        }
        return mPagerAdapter.getAudioFile(position);
    }

    @Override
    public String getAudioFileUrl(int position) {
        if (mPagerAdapter == null) {
            return null;
        }
        return mPagerAdapter.getAudioFileUrl(position);
    }
}
