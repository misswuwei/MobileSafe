package com.wuwei.mobilesafe.receiver;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.service.LocationService;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	private ComponentName mDeviceAdminSample;
	private DevicePolicyManager mDPM;

	@Override
	public void onReceive(Context context, Intent intent) {
		/**该方法用于监听用户是否发送了相应的报警短信
		 * 
		 * 该广播需要用于接收短信：需要添加权限android.permission.RECEIVE_SMS
		 * 
		 * */
		
		//所有的逻辑都建立在用户已经开启防盗保护
		boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
		
		if (open_security) {
			//[1]获取当前手机的短信内容.通过意图拿到数据在通过key值获取
			Object[] smsobject = (Object[]) intent.getExtras().get("pdus");
			
			//[2]遍历存储所有信息的短信数组，获取短信内容
			for(Object object : smsobject){
				/*[3]获取短信对象,这里的参数是byte[],可以将object强转为byte[]
				 * 因为object是通用的
				 */
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				
				//[4]上面拿到的是一条短信数据的sms，还要根据sms拿到对应的短信内容
				String originatingAddress = sms.getOriginatingAddress();//电话信息
				String messageBody = sms.getMessageBody();//短信内容
				
				//[5]对拿到的短信内容进行判断是否含有关键字
				if (messageBody.contains("#*alarm*#")) {
					//播放报警音乐，通过mediaplayer
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.closer);//参数二为播放文件的id
					/**
					 * 将资源放置在工程并能够别识别的步骤：
					 * 1，在res下创建raw文件夹，将音乐文件复制到raw文件夹下即可
					 * **/
					//循环播放
					mediaPlayer.setLooping(true);
					mediaPlayer.start();		
				}
				
				/*
				 * 当接收到这条短信时，需要将手机的gps位置发送给安全号码
				 * */
				if (messageBody.contains("#*location*#")) {
					/*获取经纬度的方法需要一直进行，所以要将逻辑放在一个服务里
					 * 这里不能直接调用startService，因为广播this不指向context
					*/
					context.startService(new Intent(context,LocationService.class));
				}
				
				//创建ComponentName用作参数
				mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);//参数不能用this，因为在广播里
				//创建DevicePolicyManager调用管理在功能
				mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				
				//远程删除数据
				if (messageBody.contains("#*wipedate*#")) {
					
					//如果设备管理器已激活则通过管理者掉调用设备管理者删除数据的功能
					if (mDPM.isAdminActive(mDeviceAdminSample)) {
						mDPM.wipeData(0);
					}else {
						//提示用户激活
						//先激活设备管理器
						Intent intents = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
						
						
	                    intents.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
	                    
	                    intents.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"设备管理器");
	                    //这里的参数二的错误是没有识别字符串，随便改一个字符串即可
	                    
	                    context.startActivity(intents);
	                    
	                    ToastUtil.show(context, "请先激活超级管理员");
					}
					
				}
				
				//远程锁屏
				if (messageBody.contains("#*lockscreen*#")) {
					//如果设备管理器已激活则通过管理者掉调用设备管理者删除数据的功能
					if (mDPM.isAdminActive(mDeviceAdminSample)) {
						//调用锁屏的方法
						mDPM.lockNow();
						//在设置密码
						mDPM.resetPassword("123", 0);
					}else {
						//提示用户激活
						//先激活设备管理器
						Intent intents = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
						
						
	                    intents.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
	                    
	                    intents.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"设备管理器");
	                    //这里的参数二的错误是没有识别字符串，随便改一个字符串即可
	                    
	                    context.startActivity(intents);
	                    
	                    ToastUtil.show(context, "请先激活超级管理员");
					}

				}
			}
		}
	}

}
