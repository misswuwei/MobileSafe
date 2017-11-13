package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {
	
	private EditText et_phone_number;
	private Button bt_select_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		initUI();
	}
	
	private void initUI() {
		//显示和输入号码的控件
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		//*****当用户从第四个页面点击返回时，要将sp中的数据回显出来
		String phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
		et_phone_number.setText(phone);
		
		bt_select_number = (Button) findViewById(R.id.bt_select_number);
		//给跳转到选择联系人控件设置点击事件
		bt_select_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),ContectListActivity.class);
				
				//因为开启的Activity页面会有返回数据，所以要调用另一个方法
				startActivityForResult(intent, 0);
			}
		});
		
		
	}

	/* (non-Javadoc)当由其他页面的数据返回到该页面时调用的方法
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//当由其他页面的数据返回到该页面时调用的方法
		super.onActivityResult(requestCode, resultCode, data);
		/*[16]在联系人页面，当用户直接点击返回时，返回的intent会为null，因此要做容错处理
		 * */
		if (data!=null) {
			//[17]获取数据并做去-处理
			String phone = data.getStringExtra("phone").replace("-", "").replace(" ", "").trim();
			et_phone_number.setText(phone);
			
			//[18]然后将数据存储在sp中
			SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
		}
	}
	

	@Override
	protected void showPrePage() {
		//跳转到第二个界面
		Intent intent = new Intent(getApplicationContext(),Setup2Activity.class);
		startActivity(intent);
		
		finish();
		
		//执行res下anim中已写好的动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	@Override
	protected void showNextPage() {
		//跳转到第四个界面
		//判断用户是否已经输入电话号码
		String phone = et_phone_number.getText().toString().trim();
		if (!TextUtils.isEmpty(phone)) {
			//将数据存贮在sp中
			SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
			Intent intent = new Intent(getApplicationContext(),Setup4Activity.class);
			startActivity(intent);
			
			finish();
		}else {
			//弹出toast提示用户
			ToastUtil.show(this, "请输入电话号码");
		}
		//执行res下anim中已写好的动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}
	
	

}
