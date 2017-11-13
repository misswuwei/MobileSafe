package com.wuwei.mobilesafe.service;

import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {
	
	//因为服务只需开启一次，所以写在oncreate里
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		//[1]获取手机经纬度,先获取位置管理者
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//[2]以最优的方式获取手机的经纬度坐标
		Criteria criteria = new Criteria();
		
		//[3]设置criteria参数让用户允许使用流量花费
		criteria.setCostAllowed(true);
		
		//[3.1]设置精确度
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//中等精确度，太高会耗电
		
		//[3.2]获取最优的提供经纬度的方式（网络，基站，GPS），手机会根据情况选出一个
		String bestProvider = lm.getBestProvider(criteria, true);
		
		//[4]设置在一定时间一定距离间隔获取经纬度坐标minTime:时间间隔 minDistance；距离间隔
		MyLocationListener myLocationListener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);//参数四为一个接口需要创建一个类实现
	}
	
	class MyLocationListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			//定位位置发生变化时执行
			//[5]获取经纬度值
			double longitude = location.getLongitude();//精度
			double latitude = location.getLatitude();//纬度
			//*********写到这里时要记得添加相应的权限
			/**
			 * android.permission.ACCESS_FINE_LOCATION获取精确GPS坐标的权限
			 * android.permission.ACCESS_MOCK_LOCATION允许模拟器设置模拟坐标的权限
			 * android.permission.ACCESS_COARSE_LOCATION获取粗略坐标的权限
			 * **/
			
			//[6]发送经纬度到安全号码
			//[4]拿到SmsManager获取管理者
			SmsManager smsManager = SmsManager.getDefault();
			
			//[5]通过管理者发送短信(参数一为发送号码，参数三为内容，其他的不用管)
			smsManager.sendTextMessage(SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, null)
					, null, "精度为："+latitude+"  纬度为："+latitude, null, null);
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// 定位状态发生改变时调用
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// 定位开启时调用
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// 定位关闭时调用
			
		}
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
}
