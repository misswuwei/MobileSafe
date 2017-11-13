package com.wuwei.mobilesafe.Activity;

import java.util.List;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.db.dao.BlackNumberDao;
import com.wuwei.mobilesafe.db.domain.BlackNumberInfo;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/*对listview的优化：
 * 1，（对view优化）为了避免每次调用getView()方法会创建多个View而占用内存，需要复用convertView
 * 当convertView=null时将转化的布局复制给convertView，之后不再重复创建和赋值
 * 
 * 2,（对FindViewById优化）为了避免每次调用getView()方法都会执行find()控件的方法，要创建ViewHolder做优化
 * 将控件对象创建在ViewHolder类中，当convertView=null时将find的对象赋值给ViewHolder中
 * 的对象，之后不再执行相同的逻辑
 * 
 * 3,将ViewHolder做为tag存在convertView中，保证了该对象和convertView一样只在第一回
 * convertView=null做了一次创建
 * 
 * 4，将ViewHolder类指定为static让其内存空间只有一个，不会多次创建
 * */

public class BlackNumberActivity extends Activity {
	private Button bt_add;
	private ListView lv_blacknumber;
	private BlackNumberDao mDao;
	private List<BlackNumberInfo> mBlackNumberList;
	private MyAdapter mAdapter;
	private int mode=1;//默认为1因为布局默认为短信
	private boolean mIsLoad = false;
	private int Count = 0;
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			//如果是第一次进来则创建对象，否则就刷新adapter
			if (mAdapter == null) {
				//拿到数据适配器
				mAdapter = new MyAdapter();
				
				//设置数据
				lv_blacknumber.setAdapter(mAdapter);
			}else {
				mAdapter.notifyDataSetChanged();
			}
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		
		initUI();
		
		initData();
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			//优化listview
			if (convertView==null) {
				convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
				
				viewHolder = new ViewHolder();
				
				//为ViewHolder赋值
				viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
				
				//将viewHolder对象存在convertView的tag中
				convertView.setTag(viewHolder);
			} else {
				//convertView不为null则直接获取convertView内存取的viewHolder
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			//给删除数据控件设置点击事件
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//执行删除逻辑,通过索引删除
					mDao.delete(mBlackNumberList.get(position).phone);
					
					//集合同步删除
					mBlackNumberList.remove(position);
					
					//告诉数据适配器做设置变更做数据改变
					if (mAdapter!=null) {
						mAdapter.notifyDataSetChanged();
					}
				}
			});
			
			//设置数据
			viewHolder.tv_phone.setText(mBlackNumberList.get(position).phone);
			int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			
			switch (mode) {
			case 1:
				viewHolder.tv_mode.setText("拦截短信");
				break;
			case 2:
				viewHolder.tv_mode.setText("拦截电话");
				break;
			case 3:
				viewHolder.tv_mode.setText("拦截所有");
				break;

			}
			
			
			return convertView;
		}

	}
	
	static class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}

	private void initData() {
		
		//开子线程做查询
		new Thread(){

			public void run() {
				//通过拿到BlackNumberDao单例拿到黑名单数据库的数据做展示	
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				
				mBlackNumberList = mDao.find(0);
				//拿到集合后发送消息做ui更新
				
				//查询数据条目总数以便做查询
				Count = mDao.getCount();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		//找到控件
		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		//添加点击事件
		bt_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//展示对话框
				showDialog();
			}
		});
		
		//为了实现分页查询，给listview设置滑动监听事件
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			
			@Override
			//当滑动状态发生改变的时候调用
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING;	飞速滑动
//				OnScrollListener.SCROLL_STATE_IDLE;		空闲状态
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;		手指一致处于按下状态滑动
			
				if (mBlackNumberList!=null) {
					
					if (scrollState == OnScrollListener.SCROLL_STATE_IDLE 
							&& lv_blacknumber.getLastVisiblePosition()>=
							mBlackNumberList.size()-1 && !mIsLoad) {
						//mIsLoad防止重复加载的变量
						
						
						//先判断是否还能继续获取数据
						if (mBlackNumberList.size() < Count) {
							//开子线程做查询
							new Thread(){
								public void run() {
									//通过拿到BlackNumberDao单例拿到黑名单数据库的数据做展示	
									mDao = BlackNumberDao.getInstance(getApplicationContext());
									
									//拿到20条数据
									List<BlackNumberInfo> moreitem = mDao.find(mBlackNumberList.size());
									
									//连接到集合中
									mBlackNumberList.addAll(moreitem);
									//拿到集合后发送消息做ui更新
									handler.sendEmptyMessage(0);
								};
							}.start();
						}
						}
						
					}
			}
			
			@Override
			//在滑动中的时候调用
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}

	/**
	 * 展示输入拦截电话的对话框
	 */
	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		//拿到builder调用创建的方法
		final AlertDialog alertDialog = builder.create();
		
		//设置布局
		View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
		alertDialog.setView(view,0,0,0,0);
		
		//找到控件设置点击事件
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cencel);
		
		//先对RadioGroup设置监听事件
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				//拿到选择id做判断
				switch (checkedId) {
				case R.id.rb_sms://短信
					mode = 1;
					break;
				case R.id.rb_phone://电话
					mode = 2;
					break;
				case R.id.rb_all://所有
					mode = 3;
					break;
				}
			}
		});
		
		//给确认按钮设置点击事件
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//1，拿到用户输入的数据
				String phone = et_phone.getText().toString();
				
				//2，往数据库添加数据
				mDao.insert(phone, mode+"");//mode是int要加""
				
				/*3，数据库做更新了但是作显示的集合没做更新，要让其保持同步
				 * 方法：手动往集合里添加对象
				 * */
				BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
				blackNumberInfo.phone = phone;
				blackNumberInfo.mode = mode+"";
				mBlackNumberList.add(0,blackNumberInfo);
				
				//4让Adapter刷新更新ui
				if (mAdapter!=null) {
					mAdapter.notifyDataSetChanged();//通知数据适配器做设置改变
				}
				
				//5关闭对话框
				alertDialog.dismiss();
			}
		});
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//用户取消时关闭对话框
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}
}
