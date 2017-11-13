package com.wuwei.mobilesafe.Activity;

import java.util.ArrayList;
import java.util.List;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.db.domain.AppInfo;
import com.wuwei.mobilesafe.db.domain.ProcessInfo;
import com.wuwei.mobilesafe.engine.ProcessInfoProvide;
import com.wuwei.mobilesafe.utils.ConstantValue;
import com.wuwei.mobilesafe.utils.SpUtil;
import com.wuwei.mobilesafe.utils.ToastUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author 伟
 * 
 */
public class ProcessManagerActivity extends Activity implements OnClickListener {

	private TextView tv_process_count;
	private TextView tv_memory_info;
	private ListView lv_process_info;
	private Button bt_select_all;
	private Button bt_select_reverse;
	private Button bt_clear;
	private Button bt_setting;
	private List<ProcessInfo> processInfo;
	private List<ProcessInfo> mSystemAppList = new ArrayList<ProcessInfo>();;
	private List<ProcessInfo> mUnSystemAppList = new ArrayList<ProcessInfo>();;
	private MyAdapet mAdapter;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapet();
			lv_process_info.setAdapter(mAdapter);

			if (tv_des != null) {
				tv_des.setText("用户进程（" + mUnSystemAppList.size() + ")");
			}
		};
	};
	private TextView tv_des;
	protected ProcessInfo mProcessInfo;
	private List<ProcessInfo> mClearList;
	private int processCount;
	private String formatFileSize;
	private String formatFileSize2;
	private long totalSpace;
	private long availSpace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		initUI();

		initTitleDate();

		initList();
	}

	private void initList() {
		getDate();
	}

	private void initTitleDate() {
		processCount = ProcessInfoProvide.getProcessCount(this);
		tv_process_count.setText("进程总数：" + processCount);

		availSpace = ProcessInfoProvide.getAvailSpace(this);
		formatFileSize = Formatter.formatFileSize(this, availSpace);
		totalSpace = ProcessInfoProvide.getTotalSpace(this);
		formatFileSize2 = Formatter.formatFileSize(this, totalSpace);

		tv_memory_info.setText("剩余/总数： " + formatFileSize + "/"
				+ formatFileSize2);

	}

	private void initUI() {
		// 找控件
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

		tv_des = (TextView) findViewById(R.id.tv_des);

		lv_process_info = (ListView) findViewById(R.id.lv_process_info);
		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		bt_select_reverse = (Button) findViewById(R.id.bt_select_reverse);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_setting = (Button) findViewById(R.id.bt_setting);

		// 设置点击事件
		bt_select_all.setOnClickListener(this);
		bt_select_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);

		// 给listview设置滑动监听
		lv_process_info.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 在滑动过程中调用该方法
				/*
				 * firstVisibleItem：第一个可见条目 visibleItemCount:当前屏幕第一个 可见条目
				 * AbsListView：就是listview对象
				 */
				if (mSystemAppList != null && mUnSystemAppList != null) {
					if (firstVisibleItem < mUnSystemAppList.size() + 1) {
						// 滑到了手机应用
						tv_des.setText("手机进程（" + mUnSystemAppList.size() + ")");
					} else {
						// 化道路系统应用
						tv_des.setText("系统进程（" + mSystemAppList.size() + ")");

					}
				}
			}
		});

		// 给listview设置点击事件,让每个条目能够相应条目管理事件
		lv_process_info.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/***
				 * view：是点击条目的view
				 * */

				if (position == 0 || position == mUnSystemAppList.size() + 1) {
					// 不需要相应事件，空实现
					return;
				} else {
					if (position < mUnSystemAppList.size() + 1) {
						// 返回用户应用对象
						mProcessInfo = mUnSystemAppList.get(position - 1);
					} else {
						// 返回系统应用对象
						mProcessInfo = mSystemAppList.get(position
								- mUnSystemAppList.size() - 2);
					}
					if (mProcessInfo.packagename.equals(getPackageName())) {
						// 不让用户选择自身应用
					} else {
						// 让checkbox取反显示,因为cb已经失去焦点，默认为false，所以让取反赋值给自己
						mProcessInfo.isCheck = !mProcessInfo.isCheck;
						CheckBox cb_box = (CheckBox) view
								.findViewById(R.id.cb_box);
						cb_box.setChecked(mProcessInfo.isCheck);
					}
				}
			}
		});
	}

	class MyAdapet extends BaseAdapter {

		@Override
		public int getCount() {
			//这里为了配合隐藏系统进程的逻辑，加判断处理
			boolean ischeck = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SYSTEM_SHOW, false);
			if (ischeck) {
				//让listview只显示用户进程和类型的条目数
				return mUnSystemAppList.size() + 1;
			}else {
				// 返回的总数中多了两个其他类型的条目（显示系统应用和用户应用）
				return mSystemAppList.size() + mUnSystemAppList.size() + 2;
			}
			
		}

		// 根据索引指定相应索引的条目类型，条目类型的状态码指定（0（复用系统），1）
		public int getItemViewType(int position) {
			if (position == 0 || position == mUnSystemAppList.size() + 1) {
				// 指定返回的状态码 0：显示类型
				return 0;
			} else {
				// 指定返回的状态码 1：显示内容
				return 1;
			}

			// return super.getItemViewType(position)+1;
		}

		// 获取数据适配器中条目类型的总数，修改成两种（显示类型和显示内容）
		public int getViewTypeCount() {
			// super.getViewTypeCount()返回值为1，这里改为2
			return super.getViewTypeCount() + 1;
		}

		@Override
		public ProcessInfo getItem(int position) {
			// 这里是根据索引返回相应类型的对象，所以
			if (position == 0 || position == mUnSystemAppList.size() + 1) {
				// 显示内容的时候不需要返回对象
				return null;
			} else {
				if (position < mUnSystemAppList.size() + 1) {
					// 返回用户应用对象
					return mUnSystemAppList.get(position - 1);
				} else {
					// 返回系统应用对象
					return mSystemAppList.get(position
							- mUnSystemAppList.size() - 2);
				}
			}
			// return appInfolist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 先判断显示类型
			if (getItemViewType(position) == 0) {
				ViewHolderType viewHolderType = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_app_item_title, null);
					viewHolderType = new ViewHolderType();
					viewHolderType.tv_title = (TextView) convertView
							.findViewById(R.id.tv_title);
					convertView.setTag(viewHolderType);
				} else {
					viewHolderType = (ViewHolderType) convertView.getTag();
				}
				// 设置数据
				if (position == 0) {
					viewHolderType.tv_title.setText("用户进程（"
							+ mUnSystemAppList.size() + ")");
				} else {
					viewHolderType.tv_title.setText("系统进程（"
							+ mSystemAppList.size() + ")");
				}

				return convertView;

			} else {
				// 显示应用数据
				ViewHolder viewHolder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_process_item, null);

					viewHolder = new ViewHolder();
					viewHolder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					viewHolder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					viewHolder.tv_memory_info = (TextView) convertView
							.findViewById(R.id.tv_memory_info);
					viewHolder.cb_box = (CheckBox) convertView
							.findViewById(R.id.cb_box);

					convertView.setTag(viewHolder);
				} else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				// 设置数据
				viewHolder.iv_icon
						.setBackgroundDrawable(getItem(position).icon);
				viewHolder.tv_name.setText(getItem(position).name);
				String formatFileSize = Formatter.formatFileSize(
						getApplicationContext(), getItem(position).memSize);
				viewHolder.tv_memory_info.setText("占用大小为：" + formatFileSize);

				// 对系统应用隐藏checkbox
				if (getItem(position).packagename.equals(getPackageName())) {
					viewHolder.cb_box.setVisibility(View.GONE);
				} else {
					viewHolder.cb_box.setVisibility(View.VISIBLE);
				}

				// 设置选中状态
				viewHolder.cb_box.setChecked(getItem(position).isCheck);
				return convertView;

			}

		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}

	static class ViewHolderType {
		TextView tv_title;
	}

	/*
	 * (non-Javadoc)处理功能按钮的逻辑
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_select_all:
			selectAll();
			break;
		case R.id.bt_select_reverse:
			selectReverse();
			break;
		case R.id.bt_clear:
			clearAll();
			break;
		case R.id.bt_setting:
			SetTing();
			break;
			}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//从进程进程设置又返回到当前页面的时候调用，这时候刷新数据适配器
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 开启进程设置页面以及相应的逻辑
	 */
	private void SetTing() {
		//因为从设置页面结束后会返回当前页面还要通知数据适配器，所以换一种方法开启
		Intent intent = new Intent(this,ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
		
	}

	/**
	 * 清除进程的方法
	 */
	private void clearAll() {
		//创建集合存储需要杀死的进程
		mClearList = new ArrayList<ProcessInfo>();
		
		//创建一个对象记录删除进程的总大小以更新
		long deleteMem = 0;
		
		//遍历系统进程，
		/**由于在循环中删除集合的内容会出现安全问题，所以要将删除内容提前存放*/
		for (ProcessInfo info : mSystemAppList) {
			if (info.packagename.equals(getPackageName())) {
				continue;
			}else {
				if (info.isCheck) {
					mClearList.add(info);
				}
			}
		}
		//遍历用户进程
		for (ProcessInfo info : mUnSystemAppList) {
			if (info.packagename.equals(getPackageName())) {
				continue;
			}else {
				if (info.isCheck) {
					mClearList.add(info);
				}
			}
		}
		//删除选中的集合中的数据对象，同时让UI刷新
		for (ProcessInfo info : mClearList) {//如果包含就删除
			if (mSystemAppList.contains(info)) {
				mSystemAppList.remove(info);
				deleteMem += info.memSize;
			}
			if (mUnSystemAppList.contains(info)) {
				mUnSystemAppList.remove(info);
				deleteMem += info.memSize;
			}
			//将杀死进程的方法封装在ProcessInfoProvide
			ProcessInfoProvide.killProcess(this,info);
		}
		//同时让线程更新
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
			
		}
		//还要同步更新进程总数和可用内存大小
		tv_process_count.setText("进程总数："+(processCount-mClearList.size()));
		tv_memory_info.setText("可用/剩余："+Formatter.formatFileSize(this, availSpace+deleteMem)+"/"+formatFileSize2);
		
		//弹出toast提示用户
		ToastUtil.show(this, "清理了"+mClearList.size()+"个进程,"+"释放了"+Formatter.formatFileSize(this, deleteMem));
		
	}

	private void selectReverse() {
		for (ProcessInfo mprocessInfo : mUnSystemAppList) {
			if (mprocessInfo.packagename.equals(getPackageName())) {
				//跳过自身应用
				continue;
			}else {
				mprocessInfo.isCheck = !mprocessInfo.isCheck ;
			}
		}
		for (ProcessInfo mprocessInfo : mSystemAppList) {
			if (mprocessInfo.packagename.equals(getPackageName())) {
				//跳过自身应用
				continue;
			}else {
				mprocessInfo.isCheck = !mprocessInfo.isCheck ;
			}
		}
		if (mAdapter != null ) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void selectAll() {
		for (ProcessInfo mprocessInfo : mUnSystemAppList) {
			if (mprocessInfo.packagename.equals(getPackageName())) {
				//跳过自身应用
				continue;
			}else {
				mprocessInfo.isCheck = true ;
			}
		}
		for (ProcessInfo mprocessInfo : mSystemAppList) {
			if (mprocessInfo.packagename.equals(getPackageName())) {
				//跳过自身应用
				continue;
			}else {
				mprocessInfo.isCheck = true ;
			}
		}
		if (mAdapter != null ) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void getDate() {
		// 开线程做耗时操作
		new Thread() {
			public void run() {
				processInfo = ProcessInfoProvide
						.getProcessInfo(getApplicationContext());
				// 为了将用户应用和系统应用分开显示，做判断处理
				for (ProcessInfo Info : processInfo) {
					if (Info.isSystem) {
						// 系统应用
						mSystemAppList.add(Info);
					} else {
						// 用户应用
						mUnSystemAppList.add(Info);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
