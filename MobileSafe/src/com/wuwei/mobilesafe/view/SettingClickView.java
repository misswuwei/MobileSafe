package com.wuwei.mobilesafe.view;

import com.wuwei.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {
	/**该方法用于设置界面归属地显示风格布局所用自定义控件
	 * 为了能够在创建该类的对象时，只调用三个参数的方法
	 * 需要在前两个方法中添加到三个参数，如下：1，将super改为this，将指向对象改为该类
	 * */

	private TextView tv_des;
	private TextView tv_title;

	public SettingClickView(Context context) {
		this(context,null);//添加一个null指向第二个构造方法
		// TODO Auto-generated constructor stub
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		this(context, attrs,0);//指向第三个构造方法
		// TODO Auto-generated constructor stub
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//组合控件的使用方法，在类中将一个布局转成view
		View.inflate(context, R.layout.setting_click_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);		
		
	}
	
	/**	自定义组合控件的title设置方法
	 * @param titlename 需要是设置的title名
	 */
	public void setTitle(String titleName) {
		tv_title.setText(titleName);
	}

	public void setDes(String desName) {
		tv_des.setText(desName);
	}
}
