package com.wuwei.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.db.domain.ProcessInfo;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

//独立执行进程数据逻辑的引擎
public class ProcessInfoProvide {

	/**
	 * @param ctx
	 *            传递一个上下文方便调用
	 * @return 返回进程总数
	 */
	public static int getProcessCount(Context ctx) {
		// 获取Activity管理者
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 调用方法获取当前正在运行的进程的总数
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();

		return runningAppProcesses.size();
	};

	/**
	 * 该方法获取可用内存的大小（没转换格式）
	 * 
	 * @param ctx
	 *            传递一个上下文方便调用
	 * @return 可用空间的大小（单位为byte）返回0 为错误
	 */
	public static long getAvailSpace(Context ctx) {
		// 获取Activity管理者
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 构建存储可用内存的对象（这里要把鼠标放在MemoryInfo对象上导包）
		MemoryInfo memoryInfo = new MemoryInfo();

		// 将对象作为存储参数调用方法获取可用内存大小
		am.getMemoryInfo(memoryInfo);// 这时memoryInfo已经赋值

		return memoryInfo.availMem;
	}

	/**
	 * 该方法获取总内存的大小
	 * 
	 * @param ctx
	 *            传递一个上下文方便调用
	 * @return 可用总内存的大小（单位为byte）
	 */
	public static long getTotalSpace(Context ctx) {
		/*
		 * 获取Activity管理者 ActivityManager am = (ActivityManager) ctx
		 * .getSystemService(Context.ACTIVITY_SERVICE);
		 * 
		 * //构建存储可用内存的对象（这里要把鼠标放在MemoryInfo对象上导包） MemoryInfo memoryInfo = new
		 * MemoryInfo();
		 * 
		 * //将对象作为存储参数调用方法获取可用内存大小
		 * am.getMemoryInfo(memoryInfo);//这时memoryInfo已经赋值
		 * 
		 * return memoryInfo.totalMem;
		 */// 这是4.1.1版本以上才能使用的

		// 读取内存文件信息（proc/memininfo文件的第一行数据）
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String readLine = bufferedReader.readLine();
			// 将字符串转换成数组
			char[] charArray = readLine.toCharArray();

			// 创建StringBuffer用于存储
			StringBuffer stringBuffer = new StringBuffer();

			// 遍历筛选
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {
					stringBuffer.append(c);
				}
			}

			// 要将字符串转换成long类型返回
			return Long.parseLong(stringBuffer.toString()) * 1024;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 关闭流对象
			if (fileReader != null && bufferedReader != null) {
				try {
					fileReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return 0;
	}

	/**
	 * @param ctx
	 *            传递一个上下文方便调用
	 * @return 封装所有进程的所有信息 的集合
	 */
	public static List<ProcessInfo> getProcessInfo(Context ctx) {
		// 创建集合对象用于返回
		List<ProcessInfo> precessInfoList = new ArrayList<ProcessInfo>();
		// 获取Activity管理者和package管理者
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = ctx.getPackageManager();

		// 调用方法获取进程信息集合
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();

		// 遍历集合获取信息
		for (RunningAppProcessInfo Info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			// 获取进程名
			processInfo.packagename = Info.processName;
			// 获取进程占用的大小(参数为info封装好的pid值)
			android.os.Debug.MemoryInfo[] processMemoryInfo = am
					.getProcessMemoryInfo(new int[] { Info.pid });
			// 获取索引为0的对象，该对象封装了占用大小的值
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			// 赋值单位为kb所以要乘以1024
			processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
			// 获取用户名称
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						processInfo.packagename, 0);
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				// 获取图标
				processInfo.icon = applicationInfo.loadIcon(pm);
				// 判断是否为系统进程
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				// 该报错需要处理
				// 若找不到该进程名，将包名赋值给进程名
				processInfo.name = Info.processName;
				processInfo.icon = ctx.getResources().getDrawable(
						R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
			precessInfoList.add(processInfo);
		}
		return precessInfoList;

	}

	/**
	 * @param ctx
	 *            上下文对象
	 * @param info
	 *            需要杀死的进程对象
	 */
	public static void killProcess(Context ctx, ProcessInfo info) {
		// 拿到窗体管理者
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 调用杀死进程的方法
		/** 加权限：android.permission.KILL_BACKGROUND_PROCESSES **/
		am.killBackgroundProcesses(info.packagename);

	}

	/**
	 * 杀死所有进程的方法
	 * 
	 * @param ctx
	 *            上下文环境
	 */
	public static void killAllProcess(Context ctx) {
		// 拿到窗体管理者
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取所有进程
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			if (runningAppProcessInfo.processName.equals(ctx.getPackageName())) {
				continue;
			}
			/** 加权限：android.permission.KILL_BACKGROUND_PROCESSES **/
			am.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
		

	}
}
