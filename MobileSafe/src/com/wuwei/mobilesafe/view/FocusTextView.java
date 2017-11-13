package com.wuwei.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class FocusTextView extends TextView {

	//通过一个上下文创建控件，一般用在使用java代码创建控件
	public FocusTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	//一般有系统调用或xml转换成java对象调用，由上下文+属性集的方法创建控件
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	//一般有系统调用或xml转换成java对象调用，由上下文+属性集的方法创建控件+style样式
	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	//******在这里自定义重写获取焦点的方法
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;//将参数改为true即可
	}

}
