package com.wuwei.mobilesafe.Activity;

import org.apache.http.util.LangUtils;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity {
	private ImageView iv_drag;
	private Button bt_top;
	private Button bt_bottom;
	private WindowManager mWM;
	private int height;
	private int width;
	private long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		
		//做位置信息的逻辑处理
		initUI();
	}

	/**
	 * 位置信息的逻辑处理
	 */
	private void initUI() {
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);
		bt_top = (Button) findViewById(R.id.bt_top);
		
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		height = mWM.getDefaultDisplay().getHeight();
		width = mWM.getDefaultDisplay().getWidth();
		
		//拿到sp中存储的值做回显
		int locationX = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
		int locationY = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
		//因为iv_drag在Relativelayout上，所以要拿到Relativelayout对象设置一个显示位置的规则
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = locationX;
		layoutParams.topMargin = locationY;
		
		//拿到位置规则给控件设置回显位置
		iv_drag.setLayoutParams(layoutParams);
		
		//让控件随着移动做变化
		if (locationY<height/2) {
			bt_top.setVisibility(View.INVISIBLE);
			bt_bottom.setVisibility(View.VISIBLE);
		}else {
			bt_top.setVisibility(View.VISIBLE);
			bt_bottom.setVisibility(View.INVISIBLE);
		}
		
		//给控件设置点击事件让其能够双击回到中心
		iv_drag.setOnClickListener(new OnClickListener() {	

			@Override
			public void onClick(View v) {
				//调用系统定义好的方法记录点击间隔
				/**参数一：原数组
				 * 参数二：原数组拷贝位置的起始索引值
				 * 参数三：目标数组
				 * 参数四：目标组拷贝位置的起始索引值
				 * 参数五：拷贝的长度
				 * */
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if (mHits[mHits.length-1]-mHits[0]<500) {//间隔小于500
					//获取控件中心位置上下左右值
					int left = width/2 - iv_drag.getWidth()/2;
					int right = left + iv_drag.getWidth();
					int top = height/2 - iv_drag.getHeight()/2;
					int bottom = top + iv_drag.getHeight();
					//设置位置
					iv_drag.layout(left, top, right, bottom);
					
					//将设置好的位置存到sp里
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, left);
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, top);
				}
			}
		}) ;
		
		//给控件设置touch监听
		iv_drag.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) iv_drag.getX();
					startY = (int) iv_drag.getY();
					break;
					
				case MotionEvent.ACTION_MOVE:
					//获取移动后的坐标
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					
					//计算控件的偏移X和Y
					int disX = moveX-startX;
					int disY = moveY-startY;
					
					//拿到控件的原始位置计算每一次移动的最终显示位置
					int left = iv_drag.getLeft()+disX;
					int right = iv_drag.getRight()+disX;
					int top = iv_drag.getTop()+disY;
					int bottom = iv_drag.getBottom()+disY;
					
					//做容错处理，让显示框不能超出屏幕边缘
					if (left<0) {//小于左边框
						return true;
						
					}
					if (right>width) {//大于右边框
						return true;
					}
					if (top<0) {//大于屏幕高度
						return true;
					}
					if (bottom>height-22) {//小于屏幕底部
						return true;
					}
					
					//让控件随着移动做变化
					if (top<height/2) {
						bt_top.setVisibility(View.INVISIBLE);
						bt_bottom.setVisibility(View.VISIBLE);
					}else {
						bt_top.setVisibility(View.VISIBLE);
						bt_bottom.setVisibility(View.INVISIBLE);
					}
					
					//根据最终坐标设置位置
					iv_drag.layout(left, top, right, bottom);
					
					//因为控件的位置在移动中发生了变化，所以还要实时将控件的起始坐标更改
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					
				case MotionEvent.ACTION_UP:
					//用户抬起手势的时候将控件的位置(左上)存储到sp里
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
					
					
					break;
				
				}
				
				//返回值是事件响应的开关，所以要将其改为true
				return false;
			}
		});
	}
}
