package com.wuwei.mobilesafe.Activity;

import java.io.File;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.engine.SmsBackUp;
import com.wuwei.mobilesafe.engine.SmsBackUp.CallBack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AToolActivity extends Activity {// 高级工具页面
	private TextView tv_query_phone_address;
	private TextView tv_sms_backup;
	private TextView tv_commonnumber_query;
	private TextView tv_applock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);

		// 归属地模块代码逻辑处理
		initPhoneAddress();

		// 短信备份逻辑处理
		initSmsBackUp();
		
		// 常用号码查询的代码逻辑处理
		initCommonNumberQuery();
		
		// 程序锁的代码逻辑处理
		initAppLock();
	}

	/**
	 * 程序锁的代码逻辑处理
	 */
	private void initAppLock() {
		tv_applock = (TextView) findViewById(R.id.tv_applock);
		tv_applock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//开启页面
				startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
			}
		});
	}

	/**
	 *  常用号码查询的代码逻辑处理
	 */
	private void initCommonNumberQuery() {
		tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
		tv_commonnumber_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//开启页面
				startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
			}
		});
	}

	/**
	 * 短信备份逻辑处理
	 */
	private void initSmsBackUp() {
		// 找到控件设置点击事件
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//弹出对话框作进度展示
				showSmsBackUpDialog();
			}
		});
	}

	/**
	 * 展示短信备份的对话框
	 */
	protected void showSmsBackUpDialog() {
		//创建一个带进度条的对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);//参数为依附的Activity
		//设置参数
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		//设置样式，参数还是封装在ProgressDialog里的参数
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//展示出来
		progressDialog.show();
		
		//将短信备份的方法封装在engine包下引擎中,由于方法中可能查询多条数据，所以要开线程
		new Thread(){
			public void run() {
				//获取外部存储空间，在获取绝对路径+File.separator（/）+创建的文件名
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"sms.xml";
				
				//调用写好的存储短信的方法
				SmsBackUp.BackUp(getApplicationContext(),path,new CallBack() {
					
					@Override
					public void setProgress(int index) {
						progressDialog.setProgress(index);
					}
					
					@Override
					public void setMax(int max) {
						progressDialog.setMax(max);
					}
				});
				
				//调用结束后关闭对话框
				progressDialog.dismiss();
			};
		}.start();
	
	}

	/**
	 * 查询号码归属地的模块
	 */
	private void initPhoneAddress() {
		// 找到控件设置点击事件
		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 点击控件时跳转到号码查询号码归属地页面
				startActivity(new Intent(getApplicationContext(),
						QueryAddressActivity.class));
			}
		});
	}

}
