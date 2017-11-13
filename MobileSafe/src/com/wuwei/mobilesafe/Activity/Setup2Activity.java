package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;
import com.wuwei.mobilesafe.view.SettingItemView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends BaseSetupActivity {
	private SettingItemView siv_sim_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		// 绑定sim卡的逻辑
		initUI();
	}

	private void initUI() {
		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);

		// [2]然后判断是否是第一次进入，若不是，则复写之前的状态
		String sim_number = SpUtil.getString(getApplicationContext(),
				ConstantValue.SIM_NUMBER, "");

		if (sim_number.isEmpty()) {
			// 之前没存储过，用户之前没绑定
			siv_sim_bound.setCheck(false);
		} else {
			// 说明用户之前绑定了sim卡，复写其状态
			siv_sim_bound.setCheck(true);
		}

		// [3]给控件设置点击事件
		siv_sim_bound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 当用户点击控件后，要给checkbox的状态取反
				boolean isCheck = siv_sim_bound.isCheck();

				siv_sim_bound.setCheck(!isCheck);

				// [4]判断是否选中绑定sim卡，若绑定则将sim卡序列号存储进入本地内存
				if (!isCheck) {
					// 存储序列号，先获取序列号，通过系统管理者获取电话管理者
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					// 拿到序列卡号
					String SimSerialNumber = manager.getSimSerialNumber();

					SpUtil.putString(getApplicationContext(),
							ConstantValue.SIM_NUMBER, "5566");

				} else {
					// 若没有绑定或者不再绑定，则删除绑定的节点
					SpUtil.remove(getApplicationContext(),
							ConstantValue.SIM_NUMBER);// ******这一步要添加权限
				}

			}
		});
	}


	@Override
	protected void showPrePage() {
		// 跳转到第一个界面
		Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
		startActivity(intent);

		finish();

		// 执行res下anim中已写好的动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

	}

	@Override
	protected void showNextPage() {
		// 跳转到第三个界面
		// 这里不能直接跳转，要判断是否帮定了sim卡
		String sim_numbber = SpUtil.getString(this, ConstantValue.SIM_NUMBER,"");
		if (!TextUtils.isEmpty(sim_numbber)) {
			// 不为空则已经绑定，这时才能跳转
			Intent intent = new Intent(getApplicationContext(),
					Setup3Activity.class);
			startActivity(intent);

			finish();

		} else {
			// 未绑定，则需要绑定才能跳转，弹出toast提示用户
			ToastUtil.show(this, "请绑定sim卡");

		}
		// 执行res下anim中已写好的动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}

}
