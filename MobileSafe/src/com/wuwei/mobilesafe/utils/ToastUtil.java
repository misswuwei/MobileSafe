package com.wuwei.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	/**	弹出toast
	 * @param context	上下文
	 * @param text	文本内容
	 */
	public static void show(Context context ,String text ) {
		// TODO Auto-generated method stub
		Toast.makeText(context, text, 1).show();
	}

}
