package com.wuwei.mobilesafe.Activity;

import java.util.List;

import com.wuwei.mobilesafe.R;
import com.wuwei.mobilesafe.engine.ConmonnumDao;
import com.wuwei.mobilesafe.engine.ConmonnumDao.Child;
import com.wuwei.mobilesafe.engine.ConmonnumDao.Group;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberQueryActivity extends Activity {

	private ExpandableListView elv_common_number;
	private List<Group> mGroup;
	private mAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commno_number);
		
		initUI();
		
		initDate();
	}

	/**
	 * 给ExpandableListView准备数据并填充数据
	 */
	private void initDate() {
		//创建引擎类工具
		ConmonnumDao commonnumdao = new ConmonnumDao();
		
		//拿到数据库数据
		mGroup = commonnumdao.getGroup();
		
		//设置数据
		mAdapter = new mAdapter();
		elv_common_number.setAdapter(new mAdapter());
		
		//给条目设置点击事件
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			//条目被点击时调用
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//调用拨打电话的方法
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				
				return false;
			}
		});
	}

	protected void startCall(String number) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
	
	class mAdapter extends BaseExpandableListAdapter{

		//Group总数
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mGroup.size();
		}

		//获取groupPosition下条目Child总数
		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mGroup.get(groupPosition).childs.size();
		}

		//获取Group对象
		@Override
		public Group getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mGroup.get(groupPosition);
		}

		//获取Child对象
		@Override
		public Child getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return mGroup.get(groupPosition).childs.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		//设置Group条目布局
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			//这里由于条目简单不需要专门写布局
			TextView textView = new TextView(getApplicationContext());
			textView.setText("          "+getGroup(groupPosition).Name);
			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);//参数一为设置单位
			return textView;
		}

		//设置Child条目布局
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			
			tv_name.setText(getChild(groupPosition, childPosition).name);
			tv_phone.setText(getChild(groupPosition, childPosition).number);
			return view;
		}

		//Child条目是否注册点击事件：返回值决定
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
}
