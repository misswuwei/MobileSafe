package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.service.LockScreenService;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.ServiceUtil;
import com.wuwei.mobilesafe.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {

	private CheckBox cb_show_system;
	private CheckBox cb_lock_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		
		//是否显示系统进程的方法
		initSystemShow();
		
		//是否锁屏清理进程的方法
		initLockScreenClear();
	}

	private void initLockScreenClear() {
		cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		
		//将服务的状态与check做绑定回显
		boolean isrunning = ServiceUtil.isRunning(getApplicationContext(), "com.wuwei.mobilesafe.service.LockScreenService");
		cb_lock_clear.setChecked(isrunning);
		if (isrunning) {
			cb_lock_clear.setText("锁屏清理已开启");
		}else {
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		
		//设置点击事件
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_lock_clear.setText("锁屏清理已开启");
					//同时开启服务
					startService(new Intent(getApplicationContext(),LockScreenService.class));
				}else {
					cb_lock_clear.setText("锁屏清理已关闭");
					stopService(new Intent(getApplicationContext(),LockScreenService.class));
				}
				
			}
		});
	}

	private void initSystemShow() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		
		//先做回显处理
		boolean ischeck = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SYSTEM_SHOW, false);
		//处理check的状态和内容
		cb_show_system.setChecked(ischeck);
		if (ischeck) {
			cb_show_system.setText("隐藏系统进程已开启");
		}else {
			cb_show_system.setText("隐藏系统进程已关闭");
		}
		
		//设置点击事件
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_show_system.setText("隐藏系统进程已开启");
				}else {
					cb_show_system.setText("隐藏系统进程已关闭");
				}
				//存储用户选择的状态
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.SYSTEM_SHOW, isChecked);
			}
		});
	}
}
