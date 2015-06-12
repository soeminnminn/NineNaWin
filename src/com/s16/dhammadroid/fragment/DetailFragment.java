package com.s16.dhammadroid.fragment;

import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;
import com.s16.dhammadroid.data.DhammaDataParser;
import com.s16.widget.LocalWebView;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment
	implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private String mTitle;
	private TextView mTextDesc;
	private TextView mTextSubTitle;
	private TextView mTextSubDesc;
	
	protected Context getContext() {
		return getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_detail, container, false);
		
		int location = getArgsPosition();
		DhammaDataParser.Entry entry = DhammaDataParser.ENTRIES.get(location);
		if (entry != null) {
			mTitle = entry.title;
			
			if (Common.usedDetailWebview()) {
				LocalWebView webView = (LocalWebView)rootView.findViewById(R.id.detailWebView);
				setDefinition(webView, entry);	
			} else {
				createView(inflater, rootView, entry);
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
	
	protected void setDefinition(LocalWebView webView, DhammaDataParser.Entry itemData) {
		
		final String newUrl = Common.URL_DEFINITION + "?name=" + itemData.name;
		String result = getDefinitionHtml(itemData);
		webView.loadDataWithBaseURL(newUrl, result
				, Common.MIME_TYPE, Common.ENCODING, newUrl);
	}
	
	private String getDefinitionHtml(DhammaDataParser.Entry entry) {
		if (entry == null) return "";
		String html = "<html>";
		html += "<head>";
		
		html += "<meta content=\"" + Common.MIME_TYPE + "; charset=" + Common.ENCODING + "\" http-equiv=\"content-type\">";
		html += "<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=yes, width=device-width\" />";
		html += "<meta name=\"Options\" content=\"{'addfont':false, 'drawfix':false}\">";
		html += "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\">";
		html += "<script type=\"text/javascript\" src=\"js/script.js\"></script>";
		if (TextUtils.isEmpty(entry.title)) {
			html += "<title>Untitled</title>";
		} else {
			html += "<title>" + entry.title + "</title>";
		}
		
		html += "</head>";
		html += "<body>";
		
		html += Utility.ZawGyiDrawFix(entry.body);
		
		if (!TextUtils.isEmpty(entry.description_title) && !TextUtils.isEmpty(entry.description_body)) {
			html += "<hr />";
			html += "<h3>"+Utility.ZawGyiDrawFix(entry.description_title)+"</h3>";
			html += "<p>";
			html += Utility.ZawGyiDrawFix(entry.description_body);
			html += "</p>";
		}
		
		html += "</body>";
		html += "</html>";
		return html;
	}
	
	private void createView(LayoutInflater inflater, ViewGroup rootView, DhammaDataParser.Entry entry) {
		rootView.removeAllViews();
		if (entry == null) return;
		
		ViewGroup detailView = (ViewGroup)inflater.inflate(R.layout.layout_details_view, rootView, false);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		detailView.setLayoutParams(params);
		
		mTextDesc = (TextView)detailView.findViewById(R.id.textDetailDesc);
		Common.setViewTextSize(mTextDesc);
		mTextDesc.setTypeface(Common.getZawgyiTypeface(getContext()));
		mTextDesc.setText(Html.fromHtml(Utility.ZawGyiDrawFix(entry.body).toString()));
		
		mTextSubTitle = (TextView)detailView.findViewById(R.id.textDetailSubTitle);
		mTextSubDesc = (TextView)detailView.findViewById(R.id.textDetailSubDesc);
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
		rootView.addView(detailView);
	}
	
	public CharSequence getTitle() {
		return Utility.ZawGyiDrawFix(mTitle);
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (Common.PREFS_FONT_SIZE.equals(key)) {
			Common.setViewTextSize(mTextDesc);
			Common.setViewTextSize(mTextSubTitle);
			Common.setViewTextSize(mTextSubDesc);
		}
	}
}
