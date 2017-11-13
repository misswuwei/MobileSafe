package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.engine.AddressDao;

import android.app.Activity;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceActivity.Header;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryAddressActivity extends Activity {

	private EditText et_phone;
	private Button bt_query;
	private TextView tv_query_result;
	private String mAddress;
	private String phone;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_query_result.setText(mAddress);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		initUI();
	}

	private void initUI() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		bt_query = (Button) findViewById(R.id.bt_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		
		
		
		//给查询按钮设置点击事件
		bt_query.setOnClickListener(new OnClickListener() {	

			@Override
			public void onClick(View v) {
				phone = et_phone.getText().toString();
				
				//[2]查询是耗时操作，要在子线程中调用
				if (!TextUtils.isEmpty(phone)) {//输入框不为null
					query(phone);
				}else {//为null则执行抖动效果
					//执行抖动的效果是通过apiDemo里写好的逻辑去执行的
					 Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
				     et_phone.startAnimation(shake);
				     
				     //同时还要让手机震动,通过系统服务拿到震动服务
				     Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				     vibrator.vibrate(1000);//直接震动1秒
				     /******
				      * 震动需要权限：android.permission.VIBRATE
				      */
				     //第二种方法
//				     vibrator.vibrate(new long[]{1000,2000},-1);
				     /**参数一long[震动事件，震动间隔]（可以写多个）。
				      * 参数二为重次数-1为1次，2为2次，3为3次
				      * **/
				}
				
			}
		});
		
		/**实时监听的方法：给控件设置文本变化的监听事件
		 * */
		et_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {//文本改变后调用的方法
				phone = et_phone.getText().toString();
				query(phone);
			}
		});
	}


	/** 查询号码归属地的方法
	 * @param phone 查询的号码
	 */
	protected void query(final String phone) {
		new Thread(){
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				//拿到查询结果后不能在子线程中 做更新ui的操作，要用消息机制
				handler.sendEmptyMessage(0);
			};
			
		}.start();
	}
}
