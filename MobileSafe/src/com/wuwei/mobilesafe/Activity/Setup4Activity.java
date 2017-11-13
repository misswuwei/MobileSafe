package com.wuwei.mobilesafe.Activity;

import java.io.Console;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		initUI();
	}

	private void initUI() {
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		// [1]判断sp里是否有用户之前设置的状态，用作回显
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);

		cb_box.setChecked(open_security);
		if (open_security) {
			cb_box.setText("您已开启防盗保护");
		} else {
			cb_box.setText("您没有开启防盗保护");
		}

		// [2]给cb设置点击事件监听变化
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 当用户点击事做相应变化
				cb_box.setChecked(isChecked);
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_SECURITY, isChecked);

				if (isChecked) {
					cb_box.setText("您已开启防盗保护");
				} else {
					cb_box.setText("您没有开启防盗保护");
				}

			}
		});

	}


	@Override
	protected void showPrePage() {
		// 跳转到第三个界面
		Intent intent = new Intent(getApplicationContext(),
				Setup3Activity.class);
		startActivity(intent);

		finish();

		// 执行res下anim中已写好的动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}

	@Override
	protected void showNextPage() {
		// 跳转到设置结束界面,先判断是否要回显
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			Intent intent = new Intent(getApplicationContext(),
					SetupOverActivity.class);
			startActivity(intent);

			// 这里在跳转的同时还要将设置的状态常量设置为开启
			SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);// 参数要为this

			finish();
		} else {
			ToastUtil.show(getApplicationContext(), "请开启防盗保护");
		}
		// 执行res下anim中已写好的动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}

}
