package com.wuwei.mobilesafe.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.engine.VirusDao;
import com.wuwei.mobilesafe.utils.Md5Util;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {
	protected static final int SCANING = 100;
	protected static final int SCANING_FINISH = 101;
	private ImageView iv_scanning;
	private ProgressBar pb_bar;
	private LinearLayout ll_add_text;
	private TextView tv_name;
	private List<String> virusList;
	private int index=0;
	private List<VirusInfo> mVirusInfosList;

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//拿到 that做判断
			switch (msg.what) {
			case SCANING:
				//拿到携带信息
				VirusInfo virusInfo = (VirusInfo) msg.obj;
				
				//做控件显示
				tv_name.setText(virusInfo.virusAppName);
				
				//判断是否病毒从而添加控件
				TextView textView = new TextView(getApplicationContext());
				if (virusInfo.isVirus) {
					textView.setTextColor(Color.RED);
					textView.setText("病毒程序："+virusInfo.virusAppName);
				}else {
					textView.setTextColor(Color.BLACK);
					textView.setText("扫描安全："+virusInfo.virusAppName);
				}
				ll_add_text.addView(textView, 0);
				break;
			case SCANING_FINISH:
				//设置显示
				tv_name.setText("扫描完成");
				//停止动画
				iv_scanning.clearAnimation();
				//卸载相关应用
				uninstallVirusApp();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anit_virus);

		initUI();

		// 页面开启后执行的动画
		initAnimation();

		// 遍历查找病毒
		checkVirus();
	}

	protected void uninstallVirusApp() {
		for (VirusInfo virusInfo : mVirusInfosList) {
			//卸载app的方法
			//通过匹配系统api源码卸载方法的属性值
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:"+virusInfo.packageName));
			startActivity(intent);
		}
	}

	private void checkVirus() {
		// 耗时操作要开子线程
		new Thread() {
			
			public void run() {
				// 拿到病毒集合
				virusList = VirusDao.getVirusList();

				// 获取所有应用的MD5码
				PackageManager pm = getPackageManager();
				// 通过package管理对象通过指定flag获取安装应用的签名文件
				//GET_SIGNATURES获取签名文件	GET_UNINSTALLED_PACKAGES：卸载应用的残余签名文件
				List<PackageInfo> packageInfoList = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);
				
				//拿到pack集合后就可以设置进度条总数
				pb_bar.setMax(packageInfoList.size());
				
				//创建一个存放本地病毒的集合
				mVirusInfosList = new ArrayList<VirusInfo>();
				
				//创建一个放置所有应用的集合
				List<VirusInfo> scanInfoList= new ArrayList<VirusInfo>();
				
				//遍历查询
				for (PackageInfo packageInfo : packageInfoList) {
					//获取签名文件信息的数组
					Signature[] signatures = packageInfo.signatures;
					
					//第一位代表md5信息
					Signature signatur = signatures[0];
					
					//由于MD5信息不是字符串，而md5Utils需要字符串，所以
					String md5value = Md5Util.encoder(signatur.toCharsString());
					
					//创建一个javabean对象
					VirusInfo virusInfo = new VirusInfo();
					
					//根据MD5遍历病毒集合
					if (virusList.contains(md5value)) {
						//若该应用是病毒，存到病毒集合中
						virusInfo.isVirus = true;
						//添加到集合中
						mVirusInfosList.add(virusInfo);
					}else {
						virusInfo.isVirus = false;
					}
					//应用名称要通过applicationinfo.loadLabel获取，还要转成String
					virusInfo.virusAppName = packageInfo.applicationInfo.loadLabel(pm).toString();
					virusInfo.packageName = packageInfo.packageName;
					scanInfoList.add(virusInfo);
					
					//扫描索引+1
					index++;
					
					//设置扫描进度
					pb_bar.setProgress(index);
					
					//由于虚幻太快，用户可能都还没来得及看就结束了了，所以要设置睡眠时间
					try {
						Thread.sleep(50+new Random().nextInt(100));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//发送携带数据信息告知主线程更新UI
					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = virusInfo;
					mHandler.sendMessage(msg);
				}
				//循环结束后代表扫描完成，还要发送一条信息告知扫描完成
				Message msg = Message.obtain();
				msg.what = SCANING_FINISH;
				mHandler.sendMessage(msg);
 			};
		}.start();
	}
	
	class VirusInfo {
		public String virusAppName ;
		public String packageName ;
		public Boolean isVirus ; 
		
	}

	private void initAnimation() {
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 设置时长
		rotateAnimation.setDuration(1000);
		// 设置模式(无限循环)
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		// 设置动画结束的位置为初始位置
		rotateAnimation.setFillAfter(true);
		
		//拿到控件开启动画
		iv_scanning.startAnimation(rotateAnimation);
	}

	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		tv_name = (TextView) findViewById(R.id.tv_name);
	}

}
