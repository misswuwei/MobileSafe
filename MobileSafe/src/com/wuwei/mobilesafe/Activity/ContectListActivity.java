package com.wuwei.mobilesafe.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Text;

import com.wuwei.mobilesafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContectListActivity extends Activity {
	protected static final String tag="ContactActivity";
	private ListView lv_contact;
	private List<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
	private MyAdapter mAdapter;
	
	private Handler mHandler = new Handler(){
		

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			
			lv_contact.setAdapter(mAdapter);
		};
	};
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		
		initUI();
		
		initDate();
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contactList.size();
		}

		@Override
		public HashMap<String, String> getItem(int arg0) {
			// TODO Auto-generated method stub
			return contactList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			//[2]加载显示条目的布局
			View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
			
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			
			tv_name.setText(getItem(arg0).get("name"));
			tv_phone.setText(getItem(arg0).get("phone"));
			
			return view;
		}
		
		
	}

	/**
	 * 获取联系人数据的方法
	 */
	private void initDate() {
		//因为下面是耗时操作，要在子线程中进行
		new Thread(){
			public void run() {
				//[1]获取内容解析者
				ContentResolver contentResolver = getContentResolver();
				
				/*[2]调用查询的方法:Uri：访问的系统联系人的表的路径.projection:查询的子段（string[]）可以是多个。
				 * selection：查询条件
				 * sortOrder：排序方式
				 * */
				Cursor cursor = contentResolver.query(
						Uri.parse("content://com.android.contacts/raw_contacts"),
						new String[]{"contact_id"},
						null, null, null);  
				System.out.println("cursor="+cursor);
				contactList.clear();
				//[3]拿到查询结果后,循环游标直至没有数据为止
				while (cursor.moveToNext()) {
					//[4]先获取联系人在表中的唯一性id值
					String id = cursor.getString(0);
					
					Log.i(tag, "id="+id);
					//[5]再根据id值，在data表和mimetype表联合成的视图中获取date和mimetype子段。根据id找到data1和mimetype
					Cursor indexCursor = contentResolver.query(
							Uri.parse("content://com.android.contacts/data"),
							new String[]{"data1","mimetype"},
							"raw_contact_id = ?", 
							new String[]{id}, null);
					/*data：获取到的是数据，数据包含电话号和名字
					 *mimetype：获取到的是类型，包含电话号指定的类型和姓名指定的类型
					 * */
					
					//[5.1]创建HashMap集合封住数据,
					HashMap<String,String> hashMap = new HashMap<String, String>();
					
					//*******在循环前将list集合清空以免数据重复
					
					
					//[6]拿到的结果值进行循环获取所有的数据
					while (indexCursor.moveToNext()) {
						String data = indexCursor.getString(0);
						String mimetype = indexCursor.getString(1);
						
						Log.i(tag, "date="+data+"mimetype="+mimetype);
						
						//[7]根据拿到数据的mimetype的数据类型判断在进行存储
						if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {//说明是电话号码
							if (!TextUtils.isEmpty(data)) {//当数据不为空时
								hashMap.put("phone", data);
							}							
						}else if (mimetype.equals("vnd.android.cursor.item/name")) {
							if (!TextUtils.isEmpty(data)) {//当数据不为空时
								hashMap.put("name", data);
							}
						} 														
					}
					//[8]查询结果用完后记得关闭 
					indexCursor.close();
					//[9]一次循环结束后将已经存放了一个联系人数据的HashMap集合存放在List里封装
					contactList.add(hashMap);
					
				}
				cursor.close();
				/*[10]这里数据封装在list以后就可以填充listview了，但是由于在子线程里，要使用消息机制
				 * 由于这里不需要复杂操作，只是告诉主线程可以使用已经封装好数据的list了，所以这里发了个空消息
				 * */
				mHandler.sendEmptyMessage(0);
				
			};
		}.start();
		
	}

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.Lv_contact);
		
		//[12]给每个联系人条目设置点击事件
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//[13]通过参数调用方法获取选中的数据，记得做容错处理
				if (mAdapter!=null) {
					//获取当前条目所对应的数据并存入intent
					HashMap<String, String> hashMap = mAdapter.getItem(position);
					
					Intent intent = new Intent();
					intent.putExtra("phone", hashMap.get("phone"));
					
					//****回调设置的方法
					setResult(0, intent);
					
					finish();
				}
			}
		});
		
	}

}
