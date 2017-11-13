package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EnterPsdActivity extends Activity {

	private TextView tv_appname;
	private ImageView iv_appicon;
	private EditText et_unlock_psd;
	private Button bt_commit;
	private String packageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pad);
		//先获取由Intent传输到该Activity的数据
		packageName = getIntent().getStringExtra("packageName");
		
		initUI();
		
		intiDate();
	}

	private void intiDate() {
		//设置应用名称
		tv_appname.setText(packageName);
		//拿到包管理者
		PackageManager pm = getPackageManager();
		try {
			 ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			//设置图标
			iv_appicon.setBackgroundDrawable(applicationInfo.loadIcon(pm));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//给按钮设置点击事件
		bt_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//获取用户输入的密码判断
				if (TextUtils.isEmpty(et_unlock_psd.getText().toString())) {
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}else {
					if (et_unlock_psd.getText().toString().equals("123")) {
						/**
						 * 会出现这样的情况：当应用开启手机应用按home键挂起后，在点击拦截应用
						 * 时，实际上任务栈最上的应用不是拦截应用，因为开启拦截的Activity是附着
						 * 在手机应用的，所以用户输入密码正确后实现上会返回挂起在任务栈的手机卫士
						 * 的页面，所以******要单独为拦截页面开启一个任务栈*****在清单文件中Activity
						 * 属性中配置android:launchMode="singleInstance属性
						 * */
						/***当用户填入的密码没有错误的时候，为了避免每500毫秒检测一次的循环再一次弹出拦截
						 * 需要发送一条广播告知该应用不需要拦截。
						 */
						Intent intent = new Intent("android.intent.action.SKIP");//自己设置Action
						//携带包名用作过滤
						intent.putExtra("packagename", packageName);
						sendBroadcast(intent);
						
						finish();
						
					}else {
						ToastUtil.show(getApplicationContext(), "密码错误");
					}
				}
			}
		});
		
		
	}

	private void initUI() {
		tv_appname = (TextView) findViewById(R.id.tv_appname);
		iv_appicon = (ImageView) findViewById(R.id.iv_appicon);
		et_unlock_psd = (EditText) findViewById(R.id.et_unlock_psd);
		bt_commit = (Button) findViewById(R.id.bt_commit);
		
		
		
	}
	
	//当用户点击回退按钮时调用的方法
	@Override
	public void onBackPressed() {
		/**
		 * 为了避免用户点击回退按钮时回到加锁页面循环又弹出加锁页面这样重复出现导致
		 * 应用无法回退的情况，要重写回退按钮的方法
		 * **/
		//让其回到首页
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(intent.CATEGORY_HOME);
		startActivity(intent);
		super.onBackPressed();
	}
}
