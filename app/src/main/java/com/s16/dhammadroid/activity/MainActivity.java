package com.s16.dhammadroid.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.s16.app.AboutPreference;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;
import com.s16.dhammadroid.fragment.MainListFragment;
import com.s16.drawing.TypefaceSpan;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(Utility.ZawGyiDrawFix(getString(R.string.main_title)));

        if (savedInstanceState == null) {
            MainListFragment fragment = new MainListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
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

    private void performSettings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }
}
