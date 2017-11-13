package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Setup1Activity extends BaseSetupActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
		
	}

	@Override
	protected void showPrePage() {
		//第一个页面没有上一页，空实现
		
	}

	@Override
	protected void showNextPage() {
		
		//跳转到第二个界面
		Intent intent = new Intent(getApplicationContext(),Setup2Activity.class);
		startActivity(intent);
		
		finish();
		
		//执行res下anim中已写好的动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}
	
	
	
	

}
