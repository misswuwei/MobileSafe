package com.wuwei.mobilesafe.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceAdmin extends DeviceAdminReceiver {
	/**实际上DeviceAdminReceiver是继承BroadcastReceiver的，所以实际上这是一个广播
	 * */

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
	}
}
