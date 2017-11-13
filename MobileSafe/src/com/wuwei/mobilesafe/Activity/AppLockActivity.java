package com.wuwei.mobilesafe.Activity;

import java.util.ArrayList;
import java.util.List;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.Activity.AppManagerActivity.ViewHolder;
import com.wuwei.mobilesafe.Activity.CommonNumberQueryActivity.mAdapter;
import com.wuwei.mobilesafe.db.dao.AppLockDao;
import com.wuwei.mobilesafe.db.domain.AppInfo;
import com.wuwei.mobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity {

	private Button bt_unlock;
	private Button bt_lock;
	private TextView tv_unlock;
	private TextView tv_lock;
	private LinearLayout ll_lock;
	private LinearLayout ll_unlock;
	private ListView lv_lock;
	private ListView lv_unlock;
	private List<AppInfo> appInfolist;
	private List<AppInfo> mUnlocklist;
	private List<AppInfo> mLocklist;
	private MyAdapter mUnAdapter;
	private AppLockDao mDao;
	private MyAdapter myLockAdapter;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mUnAdapter = new MyAdapter(false);
			lv_unlock.setAdapter(mUnAdapter);

			myLockAdapter = new MyAdapter(true);
			lv_lock.setAdapter(myLockAdapter);
		};
	};
	private TranslateAnimation mTranslateAnimation;

	class MyAdapter extends BaseAdapter {
		private Boolean isLock;

		/**
		 * @param isLock
		 *            这里为了区分加锁与未加锁，添加一个构造方法
		 */
		MyAdapter(Boolean isLock) {
			this.isLock = isLock;
		}

		@Override
		public int getCount() {
			if (isLock) {// 加锁
				// getCount已经判断好了分类，直接在该方法中设置标题
				tv_lock.setText("已加锁应用：" + mLocklist.size());
				return mLocklist.size();
			} else {
				// getCount已经判断好了分类，直接在该方法中设置标题
				tv_unlock.setText("未加锁应用：" + mUnlocklist.size());
				return mUnlocklist.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {// 加锁
				return mLocklist.get(position);
			} else {
				return mUnlocklist.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.listview_islock_item, null);

				viewHolder = new ViewHolder();

				viewHolder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				viewHolder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				viewHolder.iv_islock = (ImageView) convertView
						.findViewById(R.id.iv_islock);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			final View view = convertView;
			
			// 设置数据
			final AppInfo appInfo = getItem(position);
			viewHolder.iv_icon.setBackgroundDrawable(appInfo.icon);
			viewHolder.tv_name.setText(appInfo.Name);
			if (isLock) {
				viewHolder.iv_islock.setBackgroundResource(R.drawable.lock);
			} else {
				viewHolder.iv_islock.setBackgroundResource(R.drawable.unlock);
			}

			// 处理点击锁按钮后的ui变化,
			viewHolder.iv_islock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					/**
					 * 会有这样的情况：执行动画的过程与集合删除的过程同步执行，但是集合删除
					 * 的快一些，导致当前条目的索引被下一个索引顶替，从而使得下一个索引的条目
					 * 代替执行动画，为了避免这类情况，要对动画执行监听
					 * */
					view.startAnimation(mTranslateAnimation);
					mTranslateAnimation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						//动画执行结束的时候调用
						@Override
						public void onAnimationEnd(Animation animation) {
							//先判断当前点中的是解锁还是开锁
							if (isLock) {//说明点击的解锁
								//加锁集合删除数据
								mLocklist.remove(appInfo);
								//数据库删除数据
								mDao.delect(appInfo.PackageName);
								//未加锁集合添加数据
								mUnlocklist.add(appInfo);
								//告知数据适配器刷新
								myLockAdapter.notifyDataSetChanged();
								
							}else {//说明点击的加锁
								//加锁集合添加数据
								mLocklist.add(appInfo);
								//数据库添加数据
								mDao.insert(appInfo.PackageName);
								//未加锁集合删除数据
								mUnlocklist.remove(appInfo);
								//告知数据适配器刷新
								mUnAdapter.notifyDataSetChanged();
							}
						}
					});
					

				}
			});

			return convertView;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_islock;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);

		initUI();

		initDate();

		// 设置一个动画
		initAnimation();
	}

	/**
	 * 设置一个程序锁过度动画
	 */
	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		
		mTranslateAnimation.setDuration(500);
	}

	private void initDate() {
		// 调用方法将应用信息分类
		getDate();
	}

	private void getDate() {
		// 开线程做耗时操作
		new Thread() {
			public void run() {
				// 获取应用信息
				appInfolist = AppInfoProvider
						.getAppInfolist(getApplicationContext());

				// 创建两个集合分别存储上锁与不上锁的应用信息
				mUnlocklist = new ArrayList<AppInfo>();
				mLocklist = new ArrayList<AppInfo>();

				// 获取本地数据库已加锁的应用信息
				mDao = AppLockDao.getInstance(getApplicationContext());
				List<String> lockPackageList = mDao.findAll();

				for (AppInfo appInfo : appInfolist) {
					if (lockPackageList.contains(appInfo.PackageName)) {
						// 该应用放到已加锁集合中
						mLocklist.add(appInfo);
					} else {
						// 该应用放到未加锁集合中
						mUnlocklist.add(appInfo);
					}
				}
				// 发送消息通知数字适配器可以使用了
				handler.sendEmptyMessage(0);

			};
		}.start();
	}

	private void initUI() {
		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);

		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);

		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);

		lv_lock = (ListView) findViewById(R.id.lv_lock);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);

		// 拿到控件设置点击事件
		bt_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点中已加锁按钮则让未加锁listview隐藏，同时控件的图片也要同步切换
				ll_lock.setVisibility(View.VISIBLE);
				ll_unlock.setVisibility(View.GONE);

				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);

			}
		});

		bt_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 与点中bt_lock正相反
				ll_lock.setVisibility(View.GONE);
				ll_unlock.setVisibility(View.VISIBLE);

				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);

			}
		});
	}
}
