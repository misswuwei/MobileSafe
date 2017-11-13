package com.wuwei.mobilesafe.receiver;

import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {//创建之后记得配置receiver

	private String tag = "BootReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		/*******
		 * 该广播用于监听sim变更处理的操作，当手机重启后执行以下操作
		 * ***想要该广播能够执行还必要要添加权限android.permission.RECEIVE_BOOT_COMPLETED***
		 * */
		Log.i(tag, "手机已重启");
		
		//所有的逻辑都是建立在用户开启手机防盗的前提下
		if (SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false)) {
			
			//[1]通过context拿到手机 管理者
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			
			//[2]再通过手机管理者拿到手机sim号码
			String sim_number = tm.getSimSerialNumber();
			
			//[3]与sp中存放的sim作比较
			String sp_sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
			if (sim_number.equals(sp_sim_number)) {//说明sp卡没变更
				
			}else {//说明sim卡以变更，向安全号码发送短信
				//[4]拿到SmsManager获取管理者
				SmsManager smsManager = SmsManager.getDefault();
				
				//[5]通过管理者发送短信(参数一为发送号码，参数三为内容，其他的不用管)
				smsManager.sendTextMessage(SpUtil.getString(context, ConstantValue.CONTACT_PHONE, null), null, "您的sim卡已变更", null, null);
			}
			
		}				
		
	}

}
