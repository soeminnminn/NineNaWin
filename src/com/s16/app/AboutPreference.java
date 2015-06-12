package com.s16.app;

import com.s16.dhammadroid.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.DialogPreference;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class AboutPreference extends DialogPreference {

	private static final float PADDING = 10.0f;
	
	public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public AboutPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
	}
	
	private float convertDpToPixel(float dp){
	    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
	}
	
	@Override
	protected void onBindView(View view) {
		try {
			PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
			String versionText = getContext().getText(R.string.version_text).toString(); 
			setSummary(String.format(versionText, pInfo.versionName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		super.onBindView(view);
	}
	
	@Override
	protected View onCreateDialogView() {
		String html = getContext().getText(R.string.about_text).toString();
		final TextView message = new TextView(getContext());
		int padding = (int)convertDpToPixel(PADDING);
		message.setPadding(padding, padding, padding, padding);
		//message.setTextColor(context.getResources().getColor(android.R.color.black));
		message.setMovementMethod(LinkMovementMethod.getInstance());
		message.setText(Html.fromHtml(html));
		return message;
	}

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.action_about);
		builder.setNegativeButton(getContext().getText(android.R.string.ok), this);
		builder.setPositiveButton(null, null);
    }
	
	public static void showAboutDialog(Context context) {
		AboutPreference prefs = new AboutPreference(context, null);
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		prefs.onPrepareDialogBuilder(dialogBuilder);
		View contentView = prefs.onCreateDialogView();
		dialogBuilder.setView(contentView);
		dialogBuilder.show();
    }
}
