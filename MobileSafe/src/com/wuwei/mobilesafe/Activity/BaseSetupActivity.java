package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	
	private GestureDetector gestureDetector;
	/* (non-Javadoc) 该类用于封装设置页面跳转和手势操作的方法
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//监听手势的方法，e1代表用户手指落下的起始位置，e2代表终点位置
				if (e1.getX()-e2.getX()<0) {//说明用户向左滑，执行上一页操作
					//调用上一页的方法
					showPrePage();
					
				}if (e1.getX()-e2.getX()>0) {//说明用户向右滑，执行下 一页操作
					showNextPage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}
	
	/**
	 * 进入下一页的抽象方法，由子类自己定义
	 */
	protected abstract void showPrePage();

	/**
	 * 进入上一页的抽象方法，由子类自己定义
	 */
	protected abstract void showNextPage();
	
	public void nextPage(View view) {
		showNextPage();
	}
	
	public void prePage(View view) {
		showPrePage();
		
	}
	
	

	//为页面设置手势滑动操作
		@Override
		public boolean onTouchEvent(MotionEvent event) {//该方法监听手势操作
			//Even是传递进来的用户的操作，通过管理者执行
			gestureDetector.onTouchEvent(event);
			return super.onTouchEvent(event);
		}
}
