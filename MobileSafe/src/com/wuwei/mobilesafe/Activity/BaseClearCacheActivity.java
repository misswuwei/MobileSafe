package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class BaseClearCacheActivity extends TabActivity {/**继承TabActivity**/
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_clear_cache);
		
		//调用方法生成选项卡1
		TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
		
		//选项卡2
		TabSpec tab2 = getTabHost().newTabSpec("clear_ad_cache").setIndicator("SD卡清理");
		
		//给选项卡设置跳转页面
		tab1.setContent(new Intent(this,ClearCacheActivity.class));
		tab2.setContent(new Intent(this,SDClearCacheActivity.class));
		
		//给选项卡设置宿主才能生效
		getTabHost().addTab(tab1);
		getTabHost().addTab(tab2);
	}

}
