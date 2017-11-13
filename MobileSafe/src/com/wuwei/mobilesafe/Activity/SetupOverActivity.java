package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	private TextView tv_phone;
	private TextView tv_reset_setup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//先获取是否完成防盗导航的状态
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		
		//然后对其进行判断
		if (setup_over) {
			//如果是true，说明用户完成设置，进入设置完成的界面
			setContentView(R.layout.activity_setup_over);
			
			intiUI();
		}else {
			//如果是false，说明用户没有完成设置，跳转设置防盗导航的第一个页面
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			
			//跳转完成后SetupOverActivity存在没有意义，还要记得关闭该页面
			finish();
		}
		
		
	}

	private void intiUI() {
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 跳转到设置首页
				Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
				startActivity(intent);
			}
		});
	}

}
