package com.wuwei.mobilesafe.service;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.engine.AddressDao;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mViewToast;
	private WindowManager mWM;
	private String mAaddress;
	private int startX;
	private int startY;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//在Handler里做更新ui的操作
			tv_toast.setText(mAaddress);
		};
	};
	private TextView tv_toast;
	private int[] mDrawableIds;
	private InnerOutCallReceiver mInnerOutCallReceiver;

	@Override
	public void onCreate() {//服务开启时调用
		//[1]第一次开启服务以后，就需要去管理吐司的显示
		mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		
		//创建参数一用于编写状态改变的具体对象
		mPhoneStateListener = new MyPhoneStateListener(); 
		//设置监听
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);//参数二为监听的事件类型
		/**写到这里记得添加权限：android.permission.READ_PHONE_STATE
		 * */
		
		//拿到窗体对象让viewtoast能够挂载在上面
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		
		/**实现了点归属地后，还要实现拨打电话时显示归属地的逻辑，先做广播监听
		 * 先设置广播过滤器
		*/
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		//监听电话广播需要权限
		
		mInnerOutCallReceiver = new InnerOutCallReceiver();
		
		//一定要记得注册广播
		registerReceiver(mInnerOutCallReceiver, intentFilter); 
		
		super.onCreate();
	}
	
	class InnerOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//接收到广播后执行的逻辑
			//先拿到拨打出去的电话号码，调用方法获取拨出电话号码的字符串
			String phone = getResultData();
			
			showToast(phone);
		}
		
	}

	class MyPhoneStateListener extends PhoneStateListener{
		//手动重写下方法，当手机状态发生改变时会触发 的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE://空闲状态，没有任何活动
				//当电话挂断的时候需要将toast销毁，第一次进来的时候没有mViewToast
				if (mWM!=null && mViewToast!=null) {
					mWM.removeView(mViewToast);
				}
				break;
				
			case TelephonyManager.CALL_STATE_OFFHOOK://摘机状态
				
				break;
				
			case TelephonyManager.CALL_STATE_RINGING://响铃状态
				//展示toast显示号码归属地
				showToast(incomingNumber);
				System.out.println("电话响了");
				break;

			
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	/**
	 * 展示号码归属地toast的方法	
	 * 要在该方法里复写toast的源码修改参数
	 */
	public void showToast(String incomingNumber) {
		//下为2.3源码toast定义的参数
		final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
               //将不能触摸的参数去掉 | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //这里不需要动画params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        //响铃的时候显示toast和电话一样，将参数改为电话类型
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        
        //指定toast的位置在左上角
        params.gravity = Gravity.LEFT+Gravity.TOP;
        
        //将指定toast的布局转换
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        //找到控件显示号码归属地        
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);
        
        //创建一个int[]与之前的mToastStyle[]做资源匹配
        
        mDrawableIds = new int[]{R.drawable.call_locate_white,
        		R.drawable.call_locate_orange,
        		R.drawable.call_locate_blue,
        		R.drawable.call_locate_gray,
        		R.drawable.call_locate_green};
        
        //获取用户选择的索引
        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);
        
        //获取sp中存储的用户设置的显示位置
        params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
        
        //将转换后的view挂载在窗体上。添加权限：android.permission.SYSTEM_ALERT_WINDOW
        mWM.addView(mViewToast, mParams);//参数二为上面指定toast的指定参数规则
        
        //给toast控件设置触摸事件
        tv_toast.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) tv_toast.getX();
					startY = (int) tv_toast.getY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					//获取移动后的坐标
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					
					//计算控件的偏移X和Y
					int disX = moveX-startX;
					int disY = moveY-startY;
					
					//拿到控件的原始位置计算每一次移动的最终显示位置
					int left = tv_toast.getLeft()+disX;
					int right = tv_toast.getRight()+disX;
					int top = tv_toast.getTop()+disY;
					int bottom = tv_toast.getBottom()+disY;
					
					//做容错处理，让显示框不能超出屏幕边缘
					if (left<0) {//小于左边框
						return true;
						
					}
					if (right>mWM.getDefaultDisplay().getWidth()) {//大于右边框
						return true;
					}
					if (top<0) {//大于屏幕高度
						return true;
					}
					if (bottom>mWM.getDefaultDisplay().getHeight()-22) {//小于屏幕底部
						return true;
					}
					
					//根据最终坐标设置位置
					tv_toast.layout(left, top, right, bottom);
					
					//因为控件的位置在移动中发生了变化，所以还要实时将控件的起始坐标更改
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_UP:
					//用户抬起手势的时候将控件的位置(左上)存储到sp里
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, tv_toast.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, tv_toast.getTop());
					
					
					break;
				
				}
				
				//返回值是事件响应的开关，所以要将其改为true
				return true;
			}
		});
        
        //先根据电话调用方法查到归属地
        query(incomingNumber);
	}

	private void query(final String incomingNumber) {
		//开启一个子线程做查询操作
		new Thread(){
			public void run() {
				mAaddress = AddressDao.getAddress(incomingNumber);
				//在子线程不能更新ui，要发送消息
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	@Override
	public void onDestroy() {//服务关闭时调用
		//取消对手机状态的监听并销毁吐司	
		if (mTM!=null && mPhoneStateListener!=null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
			/**参数一为监听对象，二为取消监听
			 * */
		}
		
		//同时需要将去电展示taost的广播一并去掉
		if (mInnerOutCallReceiver!=null) {
			unregisterReceiver(mInnerOutCallReceiver);
		}
		super.onDestroy();
	}
}
