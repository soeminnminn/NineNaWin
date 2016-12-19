package android.support.v4.preference;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.s16.dhammadroid.R;

/**
 * Created by SMM on 11/4/2016.
 */

public class SeekBarDialogPreference extends DialogPreference {

    private static final String TAG = "SeekBarDialogPreference";

    private Drawable mMyIcon;
    private ImageView mIconImageView;
    private SeekBar mSeekBar;

    public SeekBarDialogPreference(Context context) {
        super(context);
        initialize(context, null, R.attr.dialogPreferenceStyle, 0);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, R.attr.dialogPreferenceStyle, 0);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // Steal the XML dialogIcon attribute's value
        mMyIcon = getDialogIcon();
        setDialogIcon(null);

        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected View onCreateDialogView() {
        int padding = (int)convertDpToPixel(20);
        LinearLayout contentLayout = new LinearLayout(getContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        mIconImageView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mIconImageView.setLayoutParams(params);
        mIconImageView.setPadding(0, padding, 0, 0);
        mIconImageView.setId(android.R.id.icon);
        contentLayout.addView(mIconImageView);

        mSeekBar = new SeekBar(getContext());
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mSeekBar.setLayoutParams(params);

        mSeekBar.setPadding(padding, padding, padding, padding);
        mSeekBar.setId(R.id.seekbar);
        contentLayout.addView(mSeekBar);

        return contentLayout;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        final ImageView iconView = mIconImageView;
        if (mMyIcon != null) {
            iconView.setImageDrawable(mMyIcon);
        } else {
            iconView.setVisibility(View.GONE);
        }
    }

    protected SeekBar getSeekBar() {
        return mSeekBar;
    }

    private float convertDpToPixel(float dp){
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
