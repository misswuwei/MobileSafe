package com.wuwei.mobilesafe.engine;
import java.util.ArrayList;
import java.util.List;

import com.wuwei.mobilesafe.db.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;


public class AppInfoProvider {

	/**返回当前手机所有应用的相关信息（名称、包名、图标、（内存orSD卡）、（系统or用户））
	 * @param ctx 	获取包管理者对象的上下文
	 * @return	返回了封装所有应用信息数据的对象
	 */
	public static List<AppInfo> getAppInfolist(Context ctx) {
		//获取包管理者拿到其他信息
		PackageManager pm = ctx.getPackageManager();
		
		//获取安装在手机上的应用的所有信息的集合
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		
		//创建一个集合将所有添加应用信息数据的对象封装
		List<AppInfo> appInfolists = new ArrayList<AppInfo>();
		
		//遍历获取信息
		for (PackageInfo packageInfo : packageInfos) {
			//封装到javabean
			AppInfo appInfo = new AppInfo();
			//存放应用的包名
			appInfo.PackageName = packageInfo.packageName;
			
			//其他的信息封装在applicationInfo中
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			
			//存放应用名称
			appInfo.Name = applicationInfo.loadLabel(pm).toString();
			
			//存放应用图标
			appInfo.icon = applicationInfo.loadIcon(pm);
			
			//通过Flag判断其属于系统应用还是用户应用在赋值给javabean
			if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				//系统应用
				appInfo.isSystem = true;
			}else {
				//用户安装的应用
				appInfo.isSystem = false;
			}
			
			//通过Flag判断其属于系统应用还是用户应用在赋值给javabean
			if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				//安装在SD卡上
				appInfo.isSdCard = true;
			}else {
				//用户安装的应用
				appInfo.isSdCard = false;
			}
			
			//每循环一次添加到集合
			appInfolists.add(appInfo);
		}
		return appInfolists;
	}
}
