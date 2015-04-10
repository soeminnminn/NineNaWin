package com.s16.dhammadroid.fragment;

import com.s16.dhammadroid.Constants;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;
import com.s16.dhammadroid.data.DhammaDataParser;
import com.s16.widget.LocalWebView;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	
	private String mTitle;
	
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
			
			if (Constants.usedDetailWebview()) {
				LocalWebView webView = (LocalWebView)rootView.findViewById(R.id.detailWebView);
				setDefinition(webView, entry);	
			} else {
				createView(inflater, rootView, entry);
			}
		}
		
		return rootView;
	}
	
	protected void setDefinition(LocalWebView webView, DhammaDataParser.Entry itemData) {
		
		final String newUrl = Constants.URL_DEFINITION + "?name=" + itemData.name;
		String result = getDefinitionHtml(itemData);
		webView.loadDataWithBaseURL(newUrl, result
				, Constants.MIME_TYPE, Constants.ENCODING, newUrl);
	}
	
	private String getDefinitionHtml(DhammaDataParser.Entry entry) {
		if (entry == null) return "";
		String html = "<html>";
		html += "<head>";
		
		html += "<meta content=\"" + Constants.MIME_TYPE + "; charset=" + Constants.ENCODING + "\" http-equiv=\"content-type\">";
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
		
		TextView desc = (TextView)detailView.findViewById(R.id.textDetailDesc);
		desc.setTypeface(Constants.getZawgyiTypeface(getContext()));
		desc.setText(Html.fromHtml(Utility.ZawGyiDrawFix(entry.body).toString()));
		
		TextView subTitle = (TextView)detailView.findViewById(R.id.textDetailSubTitle);
		TextView subDesc = (TextView)detailView.findViewById(R.id.textDetailSubDesc);
		if (!TextUtils.isEmpty(entry.description_title) && !TextUtils.isEmpty(entry.description_body)) {
			subTitle.setTypeface(Constants.getZawgyiTypeface(getContext()), Typeface.BOLD);
			subTitle.setText(Utility.ZawGyiDrawFix(entry.description_title));
			
			subDesc.setTypeface(Constants.getZawgyiTypeface(getContext()));
			subDesc.setText(Html.fromHtml(Utility.ZawGyiDrawFix(entry.description_body).toString()));
			
		} else {
			subTitle.setVisibility(View.GONE);
			subDesc.setVisibility(View.GONE);
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
}
