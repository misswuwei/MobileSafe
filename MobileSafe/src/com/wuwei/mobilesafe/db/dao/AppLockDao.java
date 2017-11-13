package com.wuwei.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.wuwei.mobilesafe.db.AppLockOpenHelper;

public class AppLockDao {
	private AppLockOpenHelper applockOpenHelper;
	private Context Content;

	/**
	 * BlackNumberDao单例模式写法如下
	 * */

	// 1，私有化构造方法
	private AppLockDao(Context context) {
		this.Content = context;
		applockOpenHelper = new AppLockOpenHelper(context);
	}

	// 2,声明一个当前类的对象
	private static AppLockDao blackNumberDao = null;

	// 3,提供一个静态方法，如果当前类为空就创建一个新的类
	public static AppLockDao getInstance(Context context) {
		// 给blackNumberDao赋值
		if (blackNumberDao == null) {
			blackNumberDao = new AppLockDao(context);
		}
		return blackNumberDao;

	}

	public void insert(String packagename) {
		SQLiteDatabase writableDatabase = applockOpenHelper
				.getWritableDatabase();

		// 创建ContentValues封装键值对
		ContentValues values = new ContentValues();
		values.put("packagename", packagename);

		// 调用插入方法
		writableDatabase.insert("applock", null, values);

		// 用完记得关闭
		writableDatabase.close();
		
		//设置内容观察者观察数据库变化
		Content.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}

	public void delect(String packagename) {
		SQLiteDatabase writableDatabase = applockOpenHelper
				.getWritableDatabase();

		// 调用删除方法
		writableDatabase.delete("applock", "packagename = ?",
				new String[] { packagename });

		// 用完记得关闭
		writableDatabase.close();
		
		//设置内容观察者观察数据库变化
		Content.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}

	public List<String> findAll() {
		SQLiteDatabase writableDatabase = applockOpenHelper
				.getWritableDatabase();

		// 调用查询方法
		Cursor cursor = writableDatabase.query("applock", new String[]{"packagename"}, null, null, null, null, null);
		List<String> applockList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			String packagename = cursor.getString(0);
			applockList.add(packagename);
		}
		// 用完记得关闭
		writableDatabase.close();
		
		
		return applockList;
	}

}
