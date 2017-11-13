package com.wuwei.mobilesafe.service;

import java.util.List;

import com.wuwei.mobilesafe.Activity.EnterPsdActivity;
import com.wuwei.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class AppLockService extends Service {

	private Boolean isWatch;
	private AppLockDao mDao;
	private List<String> locklist;
	private String skipPackName;
	private MyContentObserver mContentObserver;
	private InnerReceiver mInnerReceiver;

	@Override
	public void onCreate() {
		// 创建一个可以控制死循环的控件
		isWatch = true;

		// 获取dao对象
		mDao = AppLockDao.getInstance(this);

		// 先接收拦截页面发送的广播获取过滤条件
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SKIP");
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		
		//创建一个ContentObserver对象设置方法
		mContentObserver = new MyContentObserver(new Handler());
		//注册一个观察者匹配数据库删除和更新方法设置的内容观察者,true代表唯一匹配，false代表包含匹配
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);
		
		// 执行看门狗死循环的方法
		watchDog();
		super.onCreate();

	}
	
	class MyContentObserver extends ContentObserver{

		public MyContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
		//当数据库发生变化时内容观察者执行notify时调用onChange方法
		@Override
		public void onChange(boolean selfChange) {
			//获取数据库更新后的数据
			locklist = mDao.findAll();
			super.onChange(selfChange);
		}
		
	}

	class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			skipPackName = intent.getStringExtra("packagename");
			// 在循环中做遍历判断
		}

	}

	private void watchDog() {
		// 开启子线程检测任务栈
		new Thread() {

			public void run() {
				// 先拿到本地集合的上锁的应用
				locklist = mDao.findAll();

				// 开启循环检测
				while (isWatch) {
					// 拿到activity管理者
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

					// 获取开启应用的任务栈(参数1：代表当前开启或最后一个开启的任务栈)
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					// 拿到信息后获取包名判断
					String packageName = runningTaskInfo.topActivity
							.getPackageName();

					if (locklist.contains(packageName)) {
						if (packageName.equals(skipPackName)) {// 如果已经成功输入解锁密码
							// 不需要拦截
						} else {
							// 弹出拦截界面
							Intent intent = new Intent(getApplicationContext(),
									EnterPsdActivity.class);
							// 传递数据
							intent.putExtra("packageName", packageName);
							/** 由于是在任务栈开启Activity，该服务可能没有任务栈，所以还要创建任务栈 */
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);
						}

					}
					/** 由于循环一直开启会打量占用内存，所以还要让线程适当是睡眠一段时间 */
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}.start();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		//做收尾处理
		isWatch = false;
		if (mContentObserver!=null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		if (mInnerReceiver!=null) {
			unregisterReceiver(mInnerReceiver);
		}
		super.onDestroy();
	}

	
}
