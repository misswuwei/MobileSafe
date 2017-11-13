package com.wuwei.mobilesafe.view;

import com.wuwei.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	/*
	 * 为了能够在创建该类的对象时，只调用三个参数的方法
	 * 需要在前两个方法中添加到三个参数，如下：1，将super改为this，将指向对象改为该类
	 * */

	//添加名空间
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.wuwei.mobilesafe";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDestitle;
	private String mDesoff;
	private String mDdeson;

	public SettingItemView(Context context) {
		this(context,null);//添加一个null指向第二个构造方法
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);//指向第三个构造方法
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//组合控件的使用方法，在类中将一个布局转成view
		View.inflate(context, R.layout.setting_item_view, this);
		//这里第三参数是指挂载对象，上述方法中将一个xml文件转换成view，在挂载在当前类上，这是才能够使用
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		
		//***用自定义控件的自定义属性更新ui
		initAttrs(attrs);
		
		//***拿到自定义属性设置title
		tv_title.setText(mDestitle);
		
		
		
	}

	/**
	 * @param attrs	res下values下定义好的attrs属性集合
	 * 
	 */
	private void initAttrs(AttributeSet attrs) {
		//调用属性集attrs的方法，传入自定义控件设置好的名空间和属性名获取相应的属性值
		mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDdeson = attrs.getAttributeValue(NAMESPACE, "deson");
	}

	/**
	 * @return	判断选择控件是否选中，true or false
	 * 
	 * 
	 * 
	 */
	//拿到控件的选择状态
	public boolean isCheck(){
		return cb_box.isChecked();
		
	}
	
	//设置更细UI的方法以便UI做相应的操作
	public void setCheck(boolean isCheck){
		//做相应的ui变化
		cb_box.setChecked(isCheck);
		
		if (isCheck) {
			tv_des.setText(mDdeson);
		}else {
			tv_des.setText(mDesoff);
		}
	}
}
