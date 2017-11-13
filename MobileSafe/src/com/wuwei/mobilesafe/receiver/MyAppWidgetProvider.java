package com.wuwei.mobilesafe.receiver;

import com.wuwei.mobilesafe.service.UpdateWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

@SuppressLint("NewApi") 
public class MyAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}
	
	//创建第一个窗体小部件调用的方法
	public void onEnabled(Context context) {
		//窗体创建时就开启服务用作监听数据的更新
		context.startService(new Intent(context, UpdateWidgetService.class));
		
		super.onEnabled(context);
	}
	
	//生成一个窗体小部件调用的方法
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	
	//当窗体小部件的宽高发生改变时调用
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}
	
	//删除一个窗体小部件时调用的方法
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}
	
	//删除最后一个窗体小部件时调用
	public void onDisabled(Context context) {
		//最后一个窗体小部件删除时关闭服务
		context.stopService(new Intent(context, UpdateWidgetService.class));
		super.onDisabled(context);
	}
	
}
