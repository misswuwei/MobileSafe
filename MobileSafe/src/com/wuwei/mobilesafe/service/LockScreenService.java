package com.wuwei.mobilesafe.service;

import com.wuwei.mobilesafe.engine.ProcessInfoProvide;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {

	private InnerReceiver mInnerReceiver;

	@Override
	public void onCreate() {
		//监听手机锁屏的状态
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		mInnerReceiver = new InnerReceiver();
		//注册广播
		registerReceiver(mInnerReceiver, intentFilter);
		super.onCreate();
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//将锁屏杀死所有进程的方法写在ProcessInfoProvide
			ProcessInfoProvide.killAllProcess(context);
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		//服务关闭的时候取消对广播的监听
		if (mInnerReceiver != null) {
			unregisterReceiver(mInnerReceiver);
		}
		super.onDestroy();
	}

}
