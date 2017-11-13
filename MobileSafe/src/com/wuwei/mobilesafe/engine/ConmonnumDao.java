package com.wuwei.mobilesafe.engine;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;

public class ConmonnumDao {

	public List<Group> getGroup() {
		// 拿到数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"data/data/com.wuwei.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);

		// 查询classList表封装到Group对象中
		Cursor cursor = db.rawQuery("select * from classlist;", null);
		// 创建集合对象用于存储
		List<Group> groupList = new ArrayList<Group>();
		while (cursor.moveToNext()) {
			Group group = new Group();
			group.Name = cursor.getString(0);
			group.idx = cursor.getString(1);
			
			//将查询table表的信息一并封装到group里
			group.childs = getChild(group.idx);
			
			// 添加集合
			groupList.add(group);
		}
		// 循环结束后关闭流
		cursor.close();
		db.close();

		// 将结果集合返回
		return groupList;
	}

	public List<Child> getChild(String idx) {
		// 拿到数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(
				"data/data/com.wuwei.mobilesafe/files/commonnum.db", null,
				SQLiteDatabase.OPEN_READONLY);

		// 查询classList表封装到Group对象中
		Cursor cursor = db.rawQuery("select * from table"+idx+";", null);
		// 创建集合对象用于存储
		List<Child> childList = new ArrayList<Child>();
		while (cursor.moveToNext()) {
			Child child = new Child();
			child._id = cursor.getString(0);
			child.number = cursor.getString(1);
			child.name = cursor.getString(2);
			
			// 添加集合
			childList.add(child);
		}
		// 循环结束后关闭流
		cursor.close();
		db.close();

		// 将结果集合返回
		return childList;

	}

	public class Group {
		public String Name;
		public String idx;
		//将table？的数据同步封装到Group里
		public List<Child> childs;

	}
	
	public class Child {
		public String _id;
		public String number;
		public String name;
		
	}
}
