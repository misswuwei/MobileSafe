package com.wuwei.mobilesafe.Activity;

import java.util.ArrayList;
import java.util.List;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.Activity.BlackNumberActivity.ViewHolder;
import com.wuwei.mobilesafe.db.domain.AppInfo;
import com.wuwei.mobilesafe.engine.AppInfoProvider;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity implements OnClickListener {
	private ListView lv_app_list;
	private List<AppInfo> appInfolist;
	private AppInfo mAppInfo = null;
	private List<AppInfo> mSystemAppList = new ArrayList<AppInfo>();
	private List<AppInfo> mUnSystemAppList = new ArrayList<AppInfo>();

	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			MyAdapet adapter = new MyAdapet();
			lv_app_list.setAdapter(adapter);
			
			if (tv_des != null) {
				tv_des.setText("用户应用（"+mUnSystemAppList.size()+")");
			}
		};
	};
	private TextView tv_des;
	private PopupWindow mPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		
		//处理磁盘和sd卡的内存大小的方法
		initTitle();
		
		//为listView填充数据
		initList();

	}
	
	class MyAdapet extends BaseAdapter{

		@Override
		public int getCount() {
			//返回的总数中多了两个其他类型的条目（显示系统应用和用户应用）
			return mSystemAppList.size()+mUnSystemAppList.size()+2;
		}

		//根据索引指定相应索引的条目类型，条目类型的状态码指定（0（复用系统），1）
		public int getItemViewType(int position) {
			if (position == 0 || position == mUnSystemAppList.size()+1) {
				//指定返回的状态码	0：显示类型
				return 0;
			}else {
				//指定返回的状态码	1：显示内容
				return 1;
			}
			
			//return super.getItemViewType(position)+1;
		}
		
		//获取数据适配器中条目类型的总数，修改成两种（显示类型和显示内容）
		public int getViewTypeCount() {
			// super.getViewTypeCount()返回值为1，这里改为2
			return super.getViewTypeCount()+1;
		}
		
		@Override
		public AppInfo getItem(int position) {
			//这里是根据索引返回相应类型的对象，所以
			if (position == 0 || position == mUnSystemAppList.size()+1) {
				//显示内容的时候不需要返回对象
				return null;
			}else {
				if (position < mUnSystemAppList.size()+1) {
					//返回用户应用对象
					return mUnSystemAppList.get(position-1);
				}else {
					//返回系统应用对象
					return mSystemAppList.get(position-mUnSystemAppList.size()-2);
				}
			}
			//return appInfolist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//先判断显示类型
			if (getItemViewType(position) == 0) {
				ViewHolderType viewHolderType = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
					
					viewHolderType = new ViewHolderType();
					viewHolderType.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					convertView.setTag(viewHolderType);
				}else {
					viewHolderType = (ViewHolderType) convertView.getTag();
				}
				//设置数据
				if (position == 0) {
					viewHolderType.tv_title.setText("用户应用（"+mUnSystemAppList.size()+")");
				}else{
					viewHolderType.tv_title.setText("系统应用（"+mSystemAppList.size()+")");
				}
				
				return convertView;
				
			}else {
				//显示应用数据
				ViewHolder viewHolder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
					
					viewHolder = new ViewHolder();
					viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					viewHolder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
					
					convertView.setTag(viewHolder);
				}else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				//设置数据
				viewHolder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				viewHolder.tv_name.setText(getItem(position).Name);
				if (getItem(position).isSdCard) {
					viewHolder.tv_path.setText("SD卡应用");
				}else {
					viewHolder.tv_path.setText("手机应用");
				}
				return convertView;
				
			}
			
		}
		
	}

	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;
	}
	
	static class ViewHolderType{
		TextView tv_title;
	}
	
	/**
	 * 为listView填充数据
	 */
	private void initList() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		tv_des = (TextView) findViewById(R.id.tv_des);
		
		getDate();
		
		//给listview设置滑动监听
		lv_app_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//在滑动过程中调用该方法
				/*firstVisibleItem：第一个可见条目
				 * visibleItemCount:当前屏幕第一个 可见条目
				 * AbsListView：就是listview对象
				 * */
				if (mSystemAppList != null && mUnSystemAppList != null) {
					if (firstVisibleItem < mUnSystemAppList.size()+1) {
						//滑到了手机应用
						tv_des.setText("手机应用（"+mUnSystemAppList.size()+")");
					}else {
						//化道路系统应用
						tv_des.setText("系统应用（"+mSystemAppList.size()+")");

					}
				}
			}
		});
		
		//给listview设置点击事件,让每个条目能够相应条目管理事件
		lv_app_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/***view：是点击条目的view
				 * */
				
				if (position == 0 || position == mUnSystemAppList.size()+1) {
					//不需要相应事件，空实现
					return ;
				}else {
					if (position < mUnSystemAppList.size()+1) {
						//返回用户应用对象
						mAppInfo = mUnSystemAppList.get(position-1);
					}else {
						//返回系统应用对象
						mAppInfo = mSystemAppList.get(position-mUnSystemAppList.size()-2);
					}
					//弹出管理框
					ShowPopupWindow(view);
				}
			}
		});
	}

	/**
	 * 获取list应用数据的方法
	 */
	private void getDate() {
		//开线程做耗时操作
		new Thread(){
			public void run() {
				appInfolist = AppInfoProvider.getAppInfolist(getApplicationContext());
				//为了将用户应用和系统应用分开显示，做判断处理
				for (AppInfo appInfo : appInfolist) {
					if (appInfo.isSystem) {
						//系统应用
						mSystemAppList.add(appInfo);
					}else {
						//用户应用
						mUnSystemAppList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 弹出条目管理框
	 */
	protected void ShowPopupWindow(View view) {
		//添加展示布局
		View popupwinView = View.inflate(getApplicationContext(), R.layout.popupwindow_layout, null);
		
		//找到控件设置点击事件
		TextView tv_uninstall = (TextView) popupwinView.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupwinView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupwinView.findViewById(R.id.tv_share);
		
		//另一种注册点击事件的方法
		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		
		//为popupwindow窗体的弹出设置相应的动画
		//从不透明到透明的动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(500);
		alphaAnimation.setFillAfter(true);
		
		//圆心扩散的动画0-1从没有到填充满整个控件
		ScaleAnimation scaleAnimation = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(1000);
		scaleAnimation.setFillAfter(true);
		/*Animation.RELATIVE_TO_SELF:依赖于控件本身
		 * 0.5f：控件x的一半
		 * */
		
		//设置一个动画集合将两个动画添加到集合中
		AnimationSet animationSet = new AnimationSet(true);//两个动画执行(共享)一个数学函数
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		
		mPopupWindow = new PopupWindow(popupwinView, 
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		
		//要设置一个背景才能让回退按钮生效，所以创建一个透明的背景(new ColorDrawable())
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		
		//指定窗体的位置showAsDropDown（在指定view的下面在通过偏移x和y的坐标调整）
		mPopupWindow.showAsDropDown(view, 50,-view.getHeight());
		
		//通过展示的view执行动画集合
		popupwinView.startAnimation(animationSet);
	}

	/**
	 * 获取磁盘和sd卡的内存大小
	 */
	private void initTitle() {
		//先找相关控件
		TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		
		//获取内部磁盘大小
		String path = Environment.getDataDirectory().getAbsolutePath();
		
		//获取SD卡的大小
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		//通过路径获取可用空间的大小
		String memorySize = Formatter.formatFileSize(this, getAvailSpace(path));
		String sdMemorySize = Formatter.formatFileSize(this, getAvailSpace(sdPath));

		//设置标题
		tv_memory.setText("本地磁盘："+memorySize);
		tv_sd_memory.setText("SD卡："+sdMemorySize);
	}

	/**
	 * @param path	返回传递参数路径下的可以大小
	 */
	@SuppressWarnings("deprecation")
	private long getAvailSpace(String path) {
		//创建封装可用磁盘大小的类
		StatFs statFs = new StatFs(path);
		
		/*区块：指的是系统分配大小的最小单位
		 * 可以内存大小=区块个数*区块大小（电脑的区块大小为4kb）
		 */
		//获取手机区块的总数
		long count = statFs.getAvailableBlocks();
		
		//获取手机区块的大小
		long size = statFs.getBlockSize();
		
		return count*size;
		/*这里不能返回int类型值，因为一个int能代表最大数值为2g，而手机内存的大小一般在16g
		 * 以上*/
	}

	//接口未实现的方法
	/* 在该方法中处理popupWindows三个功能块的点击事件
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_uninstall://点击了卸载
			if (mAppInfo.isSystem) {
				ToastUtil.show(getApplicationContext(), "系统应用不能卸载");
			}else {
				//通过匹配系统api源码卸载方法的属性值
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
				startActivity(intent);
			}
			break;
			
		case R.id.tv_start://点击了开启
			//获取包管理者拿到桌面启动器
			PackageManager pm = getPackageManager();
			Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.PackageName);
			if (launchIntentForPackage != null) {
				startActivity(launchIntentForPackage);
			}else {
				ToastUtil.show(getApplicationContext(), "此应用不能被开启");
			}
			break;
			
		case R.id.tv_share://点击了分享,这里的分享指短息
			//发送一条短信
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用:"+mAppInfo.getName());
			intent.setType("text/plain");
			startActivity(intent);
			
			break;
		}
		
		//点击了窗体之后要关闭窗体
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}
	
	//在方法在Activity重新获取焦点的时候调用
	protected void onResume() {
		//当用户卸载一个应用以后为了让list刷新列表，重新调用获取数据的方法
		getDate();
		super.onResume();
	}
}
