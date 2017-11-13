package com.wuwei.mobilesafe.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SDClearCacheActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(getApplicationContext());
		textView.setText("SD卡页面");
		setContentView(textView);
	}
}
