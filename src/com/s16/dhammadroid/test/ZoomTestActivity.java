package com.s16.dhammadroid.test;

import com.s16.data.LoremIpsum;
import com.s16.dhammadroid.R;
import com.s16.widget.PinchZoomTextView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;

public class ZoomTestActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom_test);
		
		PinchZoomTextView textView = (PinchZoomTextView)findViewById(R.id.textZoom);
		textView.setText(getLoremIpsum());
		textView.setMovementMethod(new ScrollingMovementMethod());
	}
	
	private Spanned getLoremIpsum() {
		LoremIpsum loremIpsum = new LoremIpsum();
		String[] paragraphs = loremIpsum.getParagraphs(3);
		StringBuilder builder = new StringBuilder();
		for(String str : paragraphs) {
			builder.append("<p>");
			builder.append(str);
			builder.append("</p>");
		}
		return Html.fromHtml(builder.toString());
	}
}
