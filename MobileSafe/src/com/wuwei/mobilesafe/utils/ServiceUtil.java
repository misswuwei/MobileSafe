package com.wuwei.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	
	private static ActivityManager mAM;

	/**该方法用于对传递进来的服务进行判断是否开启是工具类
	 * @param ctx	上下文环境，因为一般类无法获取getSystemSrevice，需要context获取
	 * @param ServiceName	需要判断是否开启的服务的名称
	 * @return	true 开启	false 关闭
	 */
	public static boolean isRunning(Context ctx,String ServiceName){
		//[1]通过传递的context获取管理者
		mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		
		//[2]通过管理者获取所有开启的方法的集合
		List<RunningServiceInfo> runningServices = mAM.getRunningServices(100); //参数为获取的数量，一般都是10个以下
		
		//[3]对集合进行遍历和传递进来的服务对比判断
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			
			if (runningServiceInfo.service.getClassName().equals(ServiceName)) {
				return true;
			}
			
		}
		return false;
	} 

}
