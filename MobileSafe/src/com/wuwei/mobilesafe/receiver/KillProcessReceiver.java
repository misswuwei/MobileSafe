package com.wuwei.mobilesafe.receiver;

import com.wuwei.mobilesafe.engine.ProcessInfoProvide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		//调用杀死进程的方法
		ProcessInfoProvide.killAllProcess(context);
		
	}

}
