package com.wuwei.mobilesafe.Activity;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.Md5Util;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

		private GridView gv_home;
		private String[] mTitleSrts;
		private int[] mDrawableIds;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_home);
			
			//[1.0]初始化控件
			intiUI();
			
			//[2.0]初始化数据
			intiData();
		}

		/**
		 * 初始化数据
		 */
		private void intiData() {
			//[2.1]准备图片和文字数组，各9个
			mTitleSrts = new String[]{
					"手机防盗","通讯卫士","软件管理","进程管理","流量统计","手机杀毒 ","缓存清理","高级工具","设置中心"
			};
			
			//通过id（int类型）找到图片
			mDrawableIds = new int[]{
					R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
					R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
					R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings,
			};
			
			//[2.2]为9宫格控件设置数字适配器（同listview）
			gv_home.setAdapter(new Myadapter());
			
			//[2.3]为9宫格设置监听点击事件
			gv_home.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					// TODO Auto-generated method stub
					//根据点击的条目设置点击事件
					switch (position) {
					case 0://说明点击的是手机防盗的功能
					//第一次进入展示对话框先设置密码
						showDialog();
						
						break;
					case 1://说明点击的是通讯卫士的功能
						//跳转到手机位置首页
						startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
						break;
						
					case 2://说明点击的是软件管理的功能
						//跳转到手机位置首页
						startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
						break;
						
					case 3://说明点击的是進程管理的功能
						//跳转到手机位置首页
						startActivity(new Intent(getApplicationContext(),ProcessManagerActivity.class));
						break;
						
					case 5://说明点击的是手机杀毒的功能
						//跳转到手机杀毒首页
						startActivity(new Intent(getApplicationContext(),AntiVirusActivity.class));
						break;
						
					case 6://说明点击的是缓存清理的功能
						//跳转到手机杀毒选项卡页面
						startActivity(new Intent(getApplicationContext(),BaseClearCacheActivity.class));
						break;
						
					case 7://说明 是高级工具模块
						//跳转到高级工具模块
						startActivity(new Intent(getApplicationContext(),AToolActivity.class));
						break;
						
					case 8://说明是第9个模块
						Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
						startActivity(intent);
						break;

					
					}
				}
			});
		}
		
		protected void showDialog() {
			// TODO Auto-generated method stub
			//判断是否是第一次进来
			String string = SpUtil.getString(this,ConstantValue.MOBILE_SAFE_PSD,""); 
			if (TextUtils.isEmpty(string)) {//为空字符串
				//第一回进设置初始密码
				showSetPsdDialog();
			}else {//不是第一回进
				showComfirmPsdDialog();
			}
			
		}
	
		/**
		 * 不是第一回进入手机防盗展示的对话框
		 */
		private void showComfirmPsdDialog() {
			// TODO Auto-generated method stub
			
			Builder builder = new AlertDialog.Builder(this);
			
			final AlertDialog alertDialog = builder.create();
			
			//加载设置好的布局后将对话框展示出来,
			final View view=View.inflate(this, R.layout.dialog_comfirm_psd, null);
			
			//为了兼容低版本样式的问题（低版本安卓默认存在内边距），所以这里要用五个参数的api
			alertDialog.setView(view, 0, 0, 0, 0);//四个参数为上下左右的参数，将其都调为0
			alertDialog.show();
			
			//给确认和取消按钮设置点击事件
			Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
			Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
			
			//确认按钮的点击事件
			bt_submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//在拿到控件获取相应的内
					EditText et_comfirm_psd = (EditText) view.findViewById(R.id.et_comfirm_psd);
					String comfirm_psd = et_comfirm_psd.getText().toString();
					
					//做相应的判断
					if (!TextUtils.isEmpty(comfirm_psd)) {
						String set_psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
						//再判断两次输入密码是否一致
						if (set_psd.equals(Md5Util.encoder(comfirm_psd))) {
							//一致则进入手机防盗页面
							Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
							startActivity(intent);
							
							alertDialog.dismiss();//防止用户点击返回后对话框没有销毁
							
						}else {
							//不一致则提示用户
							ToastUtil.show(getApplicationContext(), "防盗密码有误");
						}
						
					}else {//若有一个为空
						ToastUtil.show(getApplicationContext(), "密码不能为空");
						
					}
				}
			});
			
			//取消按钮的点击事件
			bt_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					
				}
			});
			
		}

		/**
		 * 第一回进入手机防盗展示的对话框
		 */
		private void showSetPsdDialog() {
			// TODO Auto-generated method stub
			//初始对话框
			Builder builder = new AlertDialog.Builder(this);
			
			final AlertDialog alertDialog = builder.create();
			
			//加载设置好的布局后将对话框展示出来
			final View view=View.inflate(this, R.layout.dialog_set_psd, null);

			//为了兼容低版本样式的问题（低版本安卓默认存在内边距），所以这里要用五个参数的api
			alertDialog.setView(view, 0, 0, 0, 0);//四个参数为上下左右的参数，将其都调为0
			alertDialog.show();
			
			//给确认和取消按钮设置点击事件
			Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
			Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
			
			//确认按钮的点击事件
			bt_submit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//在拿到控件获取相应的内容
					EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);//则合理要加view，因为控件附着在view上
					EditText et_comfirm_psd = (EditText) view.findViewById(R.id.et_comfirm_psd);
					String set_psd = et_set_psd.getText().toString();
					String comfirm_psd = et_comfirm_psd.getText().toString();
					
					//做相应的判断
					if (!TextUtils.isEmpty(set_psd)&&!TextUtils.isEmpty(comfirm_psd)) {//当两个密码不为空
						//再判断两次输入密码是否一致
						if (set_psd.equals(comfirm_psd)) {
							//一致则进入手机防盗页面
							Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
							startActivity(intent);
							
							alertDialog.dismiss();//防止用户点击返回后对话框没有销毁
							
							//最后将数据存储在文件里
							SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, Md5Util.encoder(set_psd));
							
						}else {
							//不一致则提示用户
							ToastUtil.show(getApplicationContext(), "确认密码有误");
						}
						
					}else {//若有一个为空
						ToastUtil.show(getApplicationContext(), "密码不能为空");
						
					}
				}
			});
			
			//取消按钮的点击事件
			bt_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					
				}
			});
			
			
		}

		//设置数字适配器
		class Myadapter extends BaseAdapter{

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				//条目总数
				return mTitleSrts.length;
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return mTitleSrts[arg0];//根据参数获取对应的数据
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;//返回对应的参数id
			}

			@Override
			public View getView(int position, View arg1, ViewGroup arg2) {
				//需要返回每一个条目所对应的布局条目
				// TODO Auto-generated method stub
				View view=View.inflate(getApplicationContext(), R.layout.gridview_item, null);
				
				//设置每个功能模块的内容
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
				
				iv_icon.setBackgroundResource(mDrawableIds[position]);
				tv_title.setText(mTitleSrts[position]);
				return view;
			}
			
		}

		/**
		 * 初始化控件
		 */
		private void intiUI() {
			gv_home = (GridView) findViewById(R.id.gv_home);
		}

		
}
