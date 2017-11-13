package com.wuwei.mobilesafe.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Format;
import java.util.List;

import com.wuwei.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ClearCacheActivity extends Activity {

	protected static final int UNDATE_CACHE_APP = 100;
	protected static final int SCAN_FINISH = 101;
	protected static final int UPDATE_APPNAME = 102;
	protected static final int CLEAR_ALL_ITEM = 103;
	private ProgressBar pb_bar;
	private TextView tv_scan;
	private LinearLayout ll_add_text;
	private Button bt_clear;
	private PackageManager mPm;
	int index = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 判断处理
			switch (msg.what) {
			case UNDATE_CACHE_APP:
				// 将条目布局转换
				View view = View.inflate(getApplicationContext(),
						R.layout.linearlayout_cache_item, null);

				// 设置数据
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_cache_size = (TextView) view
						.findViewById(R.id.tv_cache_size);
				ImageView iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);

				final CacheApp cacheApp = (CacheApp) msg.obj;

				iv_icon.setBackgroundDrawable(cacheApp.icon);
				tv_name.setText(cacheApp.name);
				tv_name.setTextColor(Color.BLACK);
				tv_cache_size.setText(Formatter.formatFileSize(
						getApplicationContext(), cacheApp.cacheSize));
				tv_cache_size.setTextColor(Color.BLACK);

				// 添加到linearlayout中
				ll_add_text.addView(view);
				
				//给条目设置点击事件
				iv_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						//清除单个选中应用的缓存内容(PackageMananger)
						
						/* 以下代码如果要执行成功则需要系统应用才可以去使用的权限
						 * android.permission.DELETE_CACHE_FILES
						 * try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							//2.获取调用方法对象
							Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,IPackageDataObserver.class);
							//3.获取对象调用方法
							method.invoke(mPm, cacheInfo.packagename,new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//删除此应用缓存后,调用的方法,子线程中
									Log.i(tag, "onRemoveCompleted.....");
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
						//源码开发课程(源码(handler机制,AsyncTask(异步请求,手机启动流程)源码))
						//通过查看系统日志,获取开启清理缓存activity中action和data
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+cacheApp.packageName));
						startActivity(intent);						
					}
				});
				break;

			case UPDATE_APPNAME:
				// 设置扫描内容
				tv_scan.setText((String) msg.obj);
				break;

			case SCAN_FINISH:
				tv_scan.setText("扫描完成");
				break;
				
			case CLEAR_ALL_ITEM:
				//一键清除所有条目
				ll_add_text.removeAllViews();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clear_cache);

		initUI();

		initDate();

	}

	private void initDate() {
		// 耗时操作要开子线程
		new Thread() {
			public void run() {
				mPm = getPackageManager();
				List<PackageInfo> appList = mPm.getInstalledPackages(0);

				// 设置进度条总数
				pb_bar.setMax(appList.size());

				// 遍历应用找到有缓存的app
				for (PackageInfo packageInfo : appList) {
					String packname = packageInfo.packageName;

					// 调用自定义方法
					getPackageCache(packname);

					// 每循环一次设置进度条
					index++;
					pb_bar.setProgress(index);

					// 每循环一次发送一条更新扫描条目的消息
					Message msg = Message.obtain();
					msg.what = UPDATE_APPNAME;
					try {
						msg.obj = mPm.getApplicationInfo(packname, 0)
								.loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mHandler.sendMessage(msg);
				}

				// 循环结束后发送结束的消息
				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);

			};
		}.start();

	}

	static class CacheApp {
		public String name;
		public String packageName;
		public long cacheSize;
		public Drawable icon;
	}

	protected void getPackageCache(String packageName) {
		/**
		 * 下方法为安卓源码创建IPackageStatsObserver对象的方法
		 * */
		IPackageStatsObserver mStatsObserver = new IPackageStatsObserver.Stub() {

			@Override
			public void onGetStatsCompleted(PackageStats pStats,
					boolean succeeded) throws RemoteException {
				// 判断该包名所属app的缓存值大小
				long cacheSize = pStats.cacheSize;

				if (cacheSize > 0) {// 说明有缓存
					// 创建对象封装数据
					CacheApp cacheApp = new CacheApp();

					// 发送消息告知主线程更新UI
					Message msg = Message.obtain();
					msg.what = UNDATE_CACHE_APP;
					try {
						// 封装数据
						cacheApp.packageName = pStats.packageName;
						cacheApp.name = mPm
								.getApplicationInfo(pStats.packageName, 0)
								.loadLabel(mPm).toString();
						cacheApp.icon = mPm.getApplicationInfo(
								pStats.packageName, 0).loadIcon(mPm);
						cacheApp.cacheSize = cacheSize;

					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 发送消息
					msg.obj = cacheApp;
					mHandler.sendMessage(msg);
				}

			}
		};
		/***
		 * 通过查看系统源码调用系统获取缓存大小的方法，但是该方法被系统屏蔽了，所以要通过 反射调用方法：
		 * 由于需要方法所在的adil，还需要将adil复制到安卓工程中
		 * */
		try {
			// 获取字节码文件
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");

			// 通过字节码文件获取需要调用的方法
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);

			// 获取对象调用方法,执行到这一步就会调用onGetStatsCompleted方法
			method.invoke(mPm, packageName, mStatsObserver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initUI() {
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_scan = (TextView) findViewById(R.id.tv_scan);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		bt_clear = (Button) findViewById(R.id.bt_clear);

		// 给按钮设置点击事件
		bt_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 清理所有缓存
				 /** 清理缓存的方法封装在PackageManager类的freeStorageAndNotify()方法中
				 * 该方法又用到了另一个aidl文件里的对象，所以现将其aidl文件复制到工程目录下
				 * 由于清除缓存的方法是系统隐藏的方法，所以还要通过反射调用
				 * */
				try {
					// 获取字节码文件
					Class<?> clazz = Class.forName("android.content.pm.PackageManager");

					// 通过字节码文件获取需要调用的方法,参数匹配原系统方法参数
					Method method = clazz.getMethod("freeStorageAndNotify",
							long.class, IPackageDataObserver.class);

					// 获取对象调用方法,执行到这一步就会调用onGetStatsCompleted方法
					method.invoke(mPm, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {
							
								//缓存清理结束后调用的方法
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//发送消息更新UI
									Message msg = Message.obtain();
									msg.what = CLEAR_ALL_ITEM;
									mHandler.sendMessage(msg);
								}
							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 				
			}
		});
	}
}
