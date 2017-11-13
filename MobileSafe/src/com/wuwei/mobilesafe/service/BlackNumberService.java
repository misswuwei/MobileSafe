package com.wuwei.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.wuwei.mobilesafe.db.dao.BlackNumberDao;
import com.wuwei.mobilesafe.service.AddressService.MyPhoneStateListener;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.R.string;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.sax.EndElementListener;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackNumberService extends Service {
	
	private InnerSmsRecevice innerSmsRecevice;
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;
	private BlackNumberDao mDao;
	private myContentObserver mContentObserver;
	@Override
	public void onCreate() {
		//创建数据库对象方便使用
		mDao = BlackNumberDao.getInstance(getApplicationContext());
		
		//开启拦截手机短信的逻辑
		//动态注册系统短信的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		//为了能让应用能够优于其他应用获取到广播，将其的优先级调到很高
		intentFilter.setPriority(1000);//1000是最高
		
		innerSmsRecevice = new InnerSmsRecevice();
		//注册广播
		registerReceiver(innerSmsRecevice, intentFilter);
		
		
		
		//监听手机来电，先拿到管理者
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		//创建参数一用于编写状态改变的具体对象
		mPhoneStateListener = new MyPhoneStateListener(); 
		//设置监听
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);//参数二为监听的事件类型
		/**写到这里记得添加权限：android.permission.READ_PHONE_STATE
		 * */
		super.onCreate();
	}
	
	class MyPhoneStateListener extends PhoneStateListener{
		//手动重写下方法，当手机状态发生改变时会触发 的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE://空闲状态，没有任何活动
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK://摘机状态
				break;
			case TelephonyManager.CALL_STATE_RINGING://响铃状态
				//在响铃状态直接挂断即可
				endCall(incomingNumber);
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}
	
	class InnerSmsRecevice extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			//[1]获取当前手机的短信内容.通过意图拿到数据在通过key值获取
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			
			//[2]遍历存储所有信息的短信数组，获取短信内容
			for(Object object : objects){
				/*[3]获取短信对象,这里的参数是byte[],可以将object强转为byte[]
				 * 因为object是通用的
				 */
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				
				//[4]上面拿到的是一条短信数据的sms，还要根据sms拿到对应的短信内容
				String Address = sms.getOriginatingAddress();//电话信息
				String messageBody = sms.getMessageBody();
				
				//传递拿到的电话号码做数据库查询
				int mode = mDao.getMode(Address);
				System.out.println("mode="+mode);
				//做相应判断
				if(mode == 1 ||mode == 3) {
					abortBroadcast();
				}
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 使用反射调用安卓系统源码隐藏方法的逻辑
	 */
	public void endCall(String incomingNumber) {
		//传递拿到的电话号码做数据库查询
		int mode = mDao.getMode(incomingNumber);
		
		//做相应判断
		if (mode == 2 ||mode == 3) {
			/**
			 * 由于挂断电话是影响系统的方法，所以谷歌将其隐藏在aidl文件中
			 * 要去aidl文件中找到endCall的方法
			 * 要想调用endCall方法就要在安卓系统源码TelephonyManager里查找stub
			 * 关键字查到获取ITelephone对象的方法：
			 * ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
			 * 有了方法后要想使用还需要创建一个同名包名将aidl文件拷贝到包下
			 * 拷贝结束后会报错，因为该aidl文件还关联了另外一个aidl文件
			 * 同样创建一个同包名将另一个文件拷贝进去
			 * */
			//ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
			/**
			 * 这里又会报错，因为ServiceManager该类安卓不让使用对开发中隐藏，
			 * 所以这里要使用反射：
			 * */
			//通过反射创建系统源码需要的参数
			try {
				//反射：1获取ServiceManager的字节码文件
				Class<?> clazz = Class.forName("android.os.ServiceManager"); 
				
				//反射：2拿到对象创建方法
				Method method = clazz.getMethod("getService", String.class); 
				/*参数而要到用ctrl+左键点击查看Context.TELEPHONY_SERVICE的类型
				 * */
				
				//反射：3通过调用反射方法调用指定方法
				IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);//参数一由于静态方法不需要对象
				
				//这时就可以执行上面的aidl文件中ITelephone的获取了
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				
				//拿到对象调用结束电话的方法
				iTelephony.endCall();
				/*************记得添加权限************/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * 参数要到TelephonyManager源码里找到ServiceManager所引用的包
			 * */
			
			/*处理完挂断电话的逻辑的同时还要通过内容 解析者将通讯记录的条目删除，但是可能
			 * 出现这种情况，数据还在插入过程中提前完成了删除操作，导致记录还在，这时就
			 * 需要内容观察者：
			 * 先拿到通话记录表的所在位置
			 * data/data/com.android.provide.contacts/databases/contacts2.db/calls
			 * 再根据源码组拼Uri地址
			 * content：//call_log/calls
			 * */
			//先创建内容观察者对象用作参数
			mContentObserver = new myContentObserver(new Handler(),incomingNumber);
			
			//在内容解析器上注册内容观察者,参数二为检验返回，设置能找到到就为true
			getContentResolver().registerContentObserver(Uri.parse("content：//call_log/calls"), true, mContentObserver);
		}
		
		
	}

	class myContentObserver extends ContentObserver{
		private String phone ; 
		public myContentObserver(Handler handler,String phone) {
			super(handler);/***这里的super必须是第一句话**/
			this.phone = phone;
			
		}
		
		//数据库calls表发生变化时会调用该方法
		public void onChange(boolean selfChange) {
		//等电话打来数据插入在删除
		getContentResolver().delete(Uri.parse("content：//call_log/calls"), "number = ?", new String[]{phone});
		super.onChange(selfChange);
		/**记得添加权限*/
		}
	}
	
	@Override
	public void onDestroy() {
		//注销广播
		if (innerSmsRecevice!=null) {
			unregisterReceiver(innerSmsRecevice);
		}
		
		//取消对电话的监听
		if (mPhoneStateListener!=null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		
		//关闭内容观察者
		if (mContentObserver!=null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		super.onDestroy();
	}
	
	
}
