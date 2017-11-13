package com.wuwei.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.engine.ProcessInfoProvide;
import com.wuwei.mobilesafe.receiver.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.drm.DrmStore.Action;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private Timer mTimer;
	private InnerReceiver mInnerReceiver;
	String tag = "UpdateWidgetService";

	@Override
	public void onCreate() {
		//服务已开启就创建定时器定时执行逻辑
		startTimer();
		
		//监听用户锁屏和开锁的事件
		IntentFilter intentFilter = new IntentFilter();
		//开锁
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		//解锁
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		
		super.onCreate();
	}
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//接到广播后判断
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				startTimer();
			}else {
				cencelTimer();
			}
			
		}
		
	}
	
	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				//调用更新Widget的方法
				updateAppWidget();
				Log.i(tag, "计时器在运行");
			}
		}, 0, 10000);
	}

	/**
	 * 取消定时器
	 */
	public void cencelTimer() {
		if (mTimer!=null) {
			mTimer.cancel();
		}
	}

	protected void updateAppWidget() {
		//获取AppWidgetManager
		AppWidgetManager aWM = AppWidgetManager.getInstance(this);
		
		//将窗体对象的布局转换成view,参数二为需要转换的布局
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
		
		//设置内容
		remoteViews.setTextViewText(R.id.tv_process_count,"进程总数："+ProcessInfoProvide.getProcessCount(this));
		String AvailSpace = Formatter.formatFileSize(this, ProcessInfoProvide.getAvailSpace(this));
		remoteViews.setTextViewText(R.id.tv_process_memory,"可用内存："+AvailSpace);
		
		
		//当用户点击控件时要跳转到应用主页
		//先通过隐式意图匹配跳转主页的参数
		Intent intent = new Intent("android.intent.action.Home");
		intent.addCategory("android.intent.category.DEFAULT");
		//pendingIntent:延时意图，用户点击时执行的意图，参数二代表延迟时间
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		//为设置的控件设置点击执行延时intent的方法
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);
		
		//当用户点击一键清理时要跳转到广播里执行清理方法
		//先通过隐式意图匹配跳转主页的参数
		Intent castIntent = new Intent("android.intent.action.KILL_BACKROUND_PROCESS");
		//pendingIntent:延时意图，用户点击时执行的意图，参数二代表延迟时间
		PendingIntent pendingCastIntent = PendingIntent.getBroadcast(this, 0, castIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		//为设置的控件设置点击执行延时intent的方法
		remoteViews.setOnClickPendingIntent(R.id.bt_btn_clear, pendingCastIntent);
		
		
		//创建ComponentName参数设置执行的广播名，参数二为广播类的字节码文件
		ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
		//更新Appwidget
		aWM.updateAppWidget(componentName, remoteViews);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		//服务关闭，就关闭相应的广播
		if (mInnerReceiver!=null) {
			unregisterReceiver(mInnerReceiver);
		}
		//移除最后一个窗体时会间接调用destroy，这时也要关闭计时器
		cencelTimer();
		super.onDestroy();
	}

}
