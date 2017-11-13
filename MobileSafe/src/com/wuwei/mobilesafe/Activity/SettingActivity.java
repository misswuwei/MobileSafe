package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.R.id;
import com.wuwei.mobilesafe.service.AddressService;
import com.wuwei.mobilesafe.service.AppLockService;
import com.wuwei.mobilesafe.service.BlackNumberService;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.ServiceUtil;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.view.SettingClickView;
import com.wuwei.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	private SettingItemView siv_address;
	private SettingClickView scv_toast_style;
	private String[] mToastStyleDes;
	private int toast_style;
	private SettingClickView scv_Location;
	private SettingItemView siv_blacknumber;
	private SettingItemView siv_applock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		//[1.0]做设置更新模块的代码逻辑
		initUpdate();
		
		//[2.0]做号码归属地显示设置的代码逻辑
		initAddress();
		
		//[3.0]做号码归属地显示样式的设置的代码逻辑
		initToastStyle();
		
		//[4.0]做号码归属地显示位置的这种的代码逻辑
		intiLocating();
		
		//[5.0]做黑名单拦截服务的开启关闭逻辑
		initBlacknumber();
		
		//[6.0]做程序锁的开启关闭逻辑
		initAppLock();
	}

	/**
	 * 程序锁的开启关闭逻辑
	 */
	private void initAppLock() {
		siv_applock = (SettingItemView) findViewById(R.id.siv_applock);
		boolean isRunning = ServiceUtil.isRunning(this,"com.wuwei.mobilesafe.service.AppLockService");
		siv_applock.setCheck(isRunning);
		
		//给控件设置点击事件
		siv_applock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean check = siv_applock.isCheck();
				siv_applock.setCheck(!check);
				//判断选择状态做相应的开启或关闭
				if (!check) {
					startService(new Intent(getApplicationContext(),AppLockService.class));
				}else {
					stopService(new Intent(getApplicationContext(),AppLockService.class));
				}
			}
		});
	}

	/**
	 * 黑名单拦截服务的开启关闭逻辑
	 */
	private void initBlacknumber() {
		siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
		boolean isRunning = ServiceUtil.isRunning(this,"com.wuwei.mobilesafe.service.BlackNumberService");
		siv_blacknumber.setCheck(isRunning);
		
		//给控件设置点击事件
		siv_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean check = siv_blacknumber.isCheck();
				siv_blacknumber.setCheck(!check);
				//判断选择状态做相应的开启或关闭
				if (!check) {
					startService(new Intent(getApplicationContext(),BlackNumberService.class));
				}else {
					stopService(new Intent(getApplicationContext(),BlackNumberService.class));
				}
			}
		});
	}

	/**
	 * 号码归属地显示位置的这种的代码逻辑
	 */
	private void intiLocating() {
		scv_Location = (SettingClickView) findViewById(R.id.scv_Location);
		//设置控件描述
		scv_Location.setTitle("归属地 提示框的位置");
		scv_Location.setDes("设置归属地 提示框的位置");
		
		//设置点击事件
		scv_Location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
			}
		});
	}

	/**
	 * 号码归属地显示样式的设置的代码逻辑
	 */
	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		
		//1,设置组合控件title
		scv_toast_style.setTitle("设置归属地显示风格");
		
		//2,创建一个数组用于存储所有样式
		mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
		
		toast_style = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
		
		scv_toast_style.setDes(mToastStyleDes[toast_style]);
		
		//给组合控件设置点击事件
		scv_toast_style.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//当用户点击的时候展示选项框
				showToastStyleDialog();
				
			}
		});
	}

	/**
	 * 展示归属地样式选择框
	 */
	protected void showToastStyleDialog() {
		//拿到builder
		Builder builder = new AlertDialog.Builder(this);//参数this，表示对话框依赖的activity
		
		//设置参数
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
		//参数一，存储样式的String数组，参数二索引值
		builder.setSingleChoiceItems(mToastStyleDes, toast_style, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//将用户的选择保存到spUtil中
				SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
			
				//关闭对话框
				dialog.dismiss();
				
				//参数一为对话框对象，参数二为用户选中的条目索引
				scv_toast_style.setDes(mToastStyleDes[which]);
			}
		});
		
		//当用户点击取消按钮时做相应的处理
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//关闭对话框
				dialog.dismiss();
				
			}
		});
		
		//开启对话框
		builder.show();
	}

	/**
	 * 号码归属地显示设置的代码逻辑
	 */
	private void initAddress() {
		//找到控件设置点击事件
		siv_address = (SettingItemView) findViewById(R.id.siv_address);
		//先判断服务的状态(可能之前已经开启但是被杀死了)***参数二要双击服务的类名选择全路径复制
		boolean isrunning = ServiceUtil.isRunning(this, "com.wuwei.mobilesafe.service.AddressService");
		//根据状态设置回显状态
		siv_address.setCheck(isrunning);
		siv_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击时获取上次点击的状态
				boolean isCheck = siv_address.isCheck(); 
				//为checkbox设置取反状态
				siv_address.setCheck(!isCheck);
				
				//如果当前归属地为开启状态
				if (!isCheck) {
					//开启服务做逻辑处理
					startService(new Intent(getApplicationContext(),AddressService.class));
				}else {
					//不开启服务
					stopService(new Intent(getApplicationContext(),AddressService.class));
				}
				
			}
		});
		
	}

	/**
	 * ****************下面的写法会有一个bug，因为安卓事件响应的顺序会从SettingItemView转换的条目传递到checkbox，而直接点击
	 * checkbox相应在SettingItemView的条目上，导致点击checkbox会没有相应，所以为了解决这个bug，可以让相应事件不传递到checkbox
	 * 所有的逻辑在SettingItemView条目上处理
	 * 
	 * 取消checkbox的焦点的逻辑如下
	 *		 android:clickable="false"
             android:focusable="false"
             android:focusableInTouchMode="false"
	 */
	private void initUpdate() {
		// TODO Auto-generated method stub
		//[1.1]先找到自定义组成控件的id，与一般控件一样
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		
		//[1.1.0]先获取上次已选好的状态显示在控件上，该状态存在SpUtil工具类里
		boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		//加载状态
		siv_update.setCheck(open_update);
		
		//[1.2]设置点击事件
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//获取之前的选择状态。取反后设置成新的显示状态
				boolean ischeck = siv_update.isCheck();
				
				siv_update.setCheck(!ischeck);
				
				//将用户选好的新的checkbox的状态写入SpUtil工具类里
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE,!ischeck);
				//这里不能用this，因为指向setOnClickListener，参数三为!ischeck
			}
		});
	}
}
