package com.s16.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.s16.dhammadroid.R;
import com.s16.drawing.MaterialProgressDrawable;

/**
 * Created by SMM on 10/21/2016.
 */
public class MaterialListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)super.onCreateView(inflater, container, savedInstanceState);
        ProgressBar progressLoading = findProgressBar(root);
        if (progressLoading != null) {
            MaterialProgressDrawable progressDrawable = new MaterialProgressDrawable(getContext(), progressLoading);
            progressDrawable.updateSizes(MaterialProgressDrawable.LARGE);
            progressDrawable.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
            progressLoading.setIndeterminateDrawable(progressDrawable);
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
}
