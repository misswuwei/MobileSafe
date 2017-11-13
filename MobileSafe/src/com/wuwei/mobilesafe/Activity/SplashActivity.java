package com.wuwei.mobilesafe.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.R.layout;
import com.wuwei.mobilesafe.R.menu;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.StreamUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.R.integer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	
	protected static final String tag = "SplashActivity";

	protected static final int UPDATE_VERSION = 100;

	protected static final int ENTER_HOME = 101;

	protected static final int URI_ERROR = 102;

	protected static final int IO_ERROR = 103;

	protected static final int JSON_ERROR = 104;
	private TextView tv_version_name;
	private int mLocalVersionCode;
	private String mVersionDes;
	private String mDownloadUrl;
	private RelativeLayout rl_root;
	
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			//根据结果码做出相应判断
			switch (msg.what) {
			case UPDATE_VERSION:
				//弹出对话框提示用户更新,
				showUpdateDialog();
				break;
			case ENTER_HOME:
				//跳转到主页面
				enterHome();
				break;
			case URI_ERROR:
				//Uri有错,弹出toast提示，抽成工具类
				ToastUtil.show(SplashActivity.this,"URI出错");//这里的参数不能写this，因为指向hander
				
				//同时，即使出了异常，用户还是能够进入主程序
				enterHome();
				break;
			case JSON_ERROR:
				//json有错
				ToastUtil.show(SplashActivity.this,"json出错");
				enterHome();
				break;
			case IO_ERROR:
				//io有错
				ToastUtil.show(SplashActivity.this,"io出错");
				enterHome();
				break;
	
		}
	}
};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// [1]去除当前Activity头title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// [2]初始化ui，将版本号同步到首页界面上
		intiUI();

		// [3]初始化数据
		intiData();
		
		//[4]为该页面设置一个动画过度效果
		intiAnimation();
		
		//[5]初始化应用中所需要的数据库
		initDB();
		
		//[6]在桌面生成快捷方式
		initShortCut();
	}

	/**
	 * 生成快捷方式的方法
	 */
	private void initShortCut() {
		//如果还没生成图标
		if (!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)) {
			//首先要匹配桌面系统源码生成快捷方式的广播的action
			Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			
			//为快捷方式设置图标
			intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, 
					BitmapFactory.decodeResource(getResources(),
							R.drawable.shortcat));//参数二为获取资源文件下的图标的方法
			//为快捷方式设置名称
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士2015");
			
			//通过匹配隐式意图设置开启对象(匹配参数自己在清单文件写)
			Intent shortCutIntent = new Intent("android.intent.action.Home");
			shortCutIntent.addCategory("android.intent.category.DEFAULT");
			
			//为快捷方式设置开启对象
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
			
			//发送广播让桌面接收生成快捷方式
			sendBroadcast(intent);/**记得添加权限*/
			
			//将生成图标的flag存储
			SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
		}
	}

	private void initDB() {
		// TODO Auto-generated method stub
		//1.号码归属地数据库的拷贝过程的方法
		initAddressDB("address.db");
		
		//2.号码归属地数据库的拷贝过程的方法
		initAddressDB("commonnum.db");
		
		//3.将病毒数据库拷贝到本地
		initAddressDB("antivirus.db");
	}

	/**  将需要拷贝的数据库文件拷贝到资产文件assets安卓工程中
	 * @param string 拷贝的文件名
	 */
	private void initAddressDB(String dbName) {
		/**
		 * 将数据高倍到安卓工程中能够获取使用的方法
		 * 1，将需要拷贝的数据库文件拷贝到assets文件夹下
		 * */
		//1.创建一个系统定义好的可使用的文件路径
		File filesDir = getFilesDir();
		
		//2.创建一个文件将路径和具体文件放入
		File file = new File(filesDir, dbName);
		
		//3.判断 文件是否已经存在，若已经存在则没必要在拷贝一次
		if (file.exists()) {
			return;
		}
		
		InputStream stream = null;
		FileOutputStream fos = null;
		//没存在则将文件通过流的形式读取出来
		//先将第三方资产文件读取出来
		try {
			stream = getAssets().open(dbName);
			
			//4将文件读取出来后将其写入指定的文件当中
			fos = new FileOutputStream(file);
			
			//4.1设定每次读取文件的大小
			byte[] bs = new byte[1024];
			int temp = -1;
			while ((temp = stream.read(bs))!=-1) {
				fos.write(bs, 0, temp);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			//5.最后将所有使用 过的流关闭
			if (stream!=null && fos!=null) {
				try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}

	/**
	 * 为该页面设置一个动画过度效果
	 */
	private void intiAnimation() {//*****ctrl+t可以查看方法
		// TODO Auto-generated method stub
		//创建透明动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		
		//设置时长
		alphaAnimation.setDuration(3000);
		
		//通过布局控件开启动画
		rl_root.startAnimation(alphaAnimation);
	}

	/**
	 * 弹出对话框提示用户更新
	 */
	protected void showUpdateDialog() {
		// TODO Auto-generated method stub
		Builder builder = new AlertDialog.Builder(this);
		//设置图标
		builder.setIcon(R.drawable.ic_launcher);
		
		//设置title标题和内容
		builder.setTitle("版本更新");
		builder.setMessage(mVersionDes);
		
		//设置对话框积极按钮
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//进入下载地址下载
				downloadApk();
			}
		});
		builder.show();
		
		builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//直接进入主页面
				enterHome();
			}
		});
	}

	/**
	 * 下载新版本的方法
	 */
	protected void downloadApk() {
		// TODO Auto-generated method stub
		//1.判断sd卡是否可以，若可以，则使用SD卡进行存储
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//2.获取sd卡路径
			String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mobilesafe74.apk";
			
			//3.发送请求，获取apk，放在指定路径
			HttpUtils httpUtils = new HttpUtils();
			//下载方法，参数（下载地址，存放路径，）
			httpUtils.download(mDownloadUrl, sdpath, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// TODO Auto-generated method stub
					//下载成功时调用
					File apkFile = arg0.result;//这个文件是下载在sd里的spk
					Log.i(tag, "下载成功");
					
					//下载成功后还要帮用户调用方法安装下载好的apk
					installApk(apkFile);
					
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					//下载失败时调用
					Log.i(tag, "下载失败");
				}
				
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					//开始下载时调用
					Log.i(tag, "开始下载");
					super.onStart();
				}
				
				@Override
				public void onLoading(long total, long current,boolean isUploading) {//参数为：1，总大小 2，当前位置，3.是否在下载
					// TODO Auto-generated method stub
					//正在下载时调用
					Log.i(tag, "正在下载");
					Log.i(tag, "total="+total);
					Log.i(tag, "current="+current);
					super.onLoading(total, current, isUploading);
				}
			});
		}
	}

	/**调用该方法调用系统安装apk的页面安装apk
	 * @param fileapk文件
	 */
	protected void installApk(File file) {
		// TODO Auto-generated method stub
		//调用系统安装apk的方法，使用隐式意图进行配对
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		//设置安装文件的路径
		intent.setData(Uri.fromFile(file));
		
		//设置安装文件的类型
		intent.setType("application/vnd.android.package-archive");
		
		//使用第二种方法开始activity，为了让用户点击返回后不停留在版本更新页面,
		startActivityForResult(intent, 0);
	}
	
	/* (non-Javadoc)在一个activity里开启另一个Activity后点击返回调用该方法
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//即使用户点击返回后也要让用户进入软件主页面
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 跳转到主页面的方法
	 */
	protected void enterHome() {
		// TODO Auto-generated method stub
		Intent intent= new Intent(this,HomeActivity.class);
		startActivity(intent);
		//进入主页面之后，让进场页面关闭
		finish();
	}

	/**
	 * 初始化数据的方法
	 */
	private void intiData() {
		// TODO Auto-generated method stub
		// [3.1]设置首页应用版本的名称
		tv_version_name.setText("版本名称：" + getVersionName());

		// [3.2]获取本地服务器的版本号
		mLocalVersionCode = getVersionCode();

		// [3.3]检查服务器端版本号,结合设置界面自动更新操作的逻辑判断是否需要调用检查版本的方法
		if (SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
			checkVersion();
		}else {
			/*自动更新一关闭，则进入主页面，但是不能跳过首页，多以要让其沉睡4秒，但是不能用Thread.slssp，因为在首页
			 * 有7秒的判断，首页所在的逻辑是主线程，不能让主线程睡，所以这里直接使用发消息延时处理的方法，
			 */
			//方法1mHandler.sendMessageDelayed(msg, 4000);
			
			//发送消息4秒后在处理消息所对应的逻辑方法
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
		

	}

	/**
	 * 检查服务器端版本号
	 */
	private void checkVersion() {
		// TODO Auto-generated method stub
		// [3.3.1]创建一个线程实现网络操作
		new Thread() {

			public void run() {
				//当run()方法开始运行时，开始记录时间，以便过度时间不会太长
				long startTime = System.currentTimeMillis();
				
				//创建一个消息用于发送
				Message msg = Message.obtain();
				
				try {
					// [3.3.2]封装url地址。10.0.2.2是谷歌封装好的，用于模拟器访问电脑上的tomcat服务器端
					URL url = new URL("http://10.0.2.2:8080/update.json");
					//开启一个链接
					HttpURLConnection openConnection = (HttpURLConnection) url.openConnection();
					
					//设置请求超时时间
					openConnection.setConnectTimeout(2000);
					
					//设置读取超时时间
					openConnection.setReadTimeout(2000);
					
					//设置请求方法，默认的为GET
					//openConnection.setRequestMethod("POST");设置方法
					
					//获取响应码并判断
					if (openConnection.getResponseCode()==200) {//响应成功
						//以流的形式将数据获取下来
						InputStream is = openConnection.getInputStream();
						
						//[3.3.2]创建一个工具类将流转换成字符串
						String json= StreamUtil.StreamtoString(is);
						Log.i(tag, json);
						
						//解析json文件
						JSONObject jsonObject = new JSONObject(json);
						
						//获取json文件信息
						mVersionDes = jsonObject.getString("versionDes");
						mDownloadUrl = jsonObject.getString("downloadUrl");
						
						//对比版本号
						if (mLocalVersionCode<Integer.parseInt(jsonObject.getString("versionCode"))) {
							//说明有新版本，弹出提示框，由于是更新ui的方法，不能在线程中更新，只能使用消息机制
							//先设置状态码
							msg.what=UPDATE_VERSION;
						}else {
							//说明版本没升级，进入主页面
							msg.what=ENTER_HOME;
						}
						
					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					msg.what=URI_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what=IO_ERROR;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what=JSON_ERROR;
					e.printStackTrace();
				}finally{
					//调用方法发送消息
					mHandler.sendMessage(msg);
					//为了让画面过度不要太快，还要让系统休眠1s
					long endTime = System.currentTimeMillis();
					if (endTime-startTime<4000) {
						try {
							Thread.sleep(4000-(endTime-startTime));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
			};
		}.start();

		/*
		 * 开启线程的第二种方法 new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * 
		 * } });
		 */
	}

	/**
	 * 获取版本号，返回0则失败，非0成功
	 */
	private int getVersionCode() {
		// TODO Auto-generated method stub
		// [3.1.1]获取包管理对象packageManager
		PackageManager pm = getPackageManager();

		// [3.1.2]从包管理对象中获取指定包名的基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);// 参数一为快捷获取包名的方法，0代表基本信息

			// 获取packageInfo包信息获取清单文件的版本名称并返回
			return packageInfo.versionCode;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * 获取应用版本的名称：从清单文件中
	 */
	private String getVersionName() {
		// TODO Auto-generated method stub
		// [3.1.1]获取包管理对象packageManager
		PackageManager pm = getPackageManager();

		// [3.1.2]从包管理对象中获取指定包名的基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);// 参数一为快捷获取包名的方法，0代表基本信息

			// 获取packageInfo包信息获取清单文件的版本名称并返回
			return packageInfo.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化ui的方法
	 */
	private void intiUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
