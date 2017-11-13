package com.wuwei.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wuwei.mobilesafe.db.BlackNumberOpenHelper;
import com.wuwei.mobilesafe.db.domain.BlackNumberInfo;

public class BlackNumberDao {
	private BlackNumberOpenHelper blackNumberOpenHelper;

	/**
	 * BlackNumberDao单例模式写法如下
	 * */

	// 1，私有化构造方法
	private BlackNumberDao(Context context) {
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}

	// 2,声明一个当前类的对象
	private static BlackNumberDao blackNumberDao = null;

	// 3,提供一个静态方法，如果当前类为空就创建一个新的类
	public static BlackNumberDao getInstance(Context context) {
		// 给blackNumberDao赋值
		if (blackNumberDao == null) {
			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;

	}

	/**
	 * 为数据库增加一个条目的方法
	 * 
	 * @param phone
	 *            添加的黑名单号码
	 * @param mode
	 *            拦截模式（1：短信 2：电话 3：所以（电话加短信））
	 */
	public void insert(String phone, String mode) {
		// 开启数据库
		SQLiteDatabase writableDatabase = blackNumberOpenHelper
				.getWritableDatabase();

		// 创建ContentValues封装键值对
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		values.put("mode", mode);

		// 调用插入方法
		writableDatabase.insert("blacknumber", null, values);

		// 用完记得关闭
		writableDatabase.close();
	}

	/**
	 * 根据传递的电话号码为数据库删除一个条目的方法
	 * 
	 * @param phone
	 *            删除的黑名单号码
	 */
	public void delete(String phone) {
		// 开启数据库
		SQLiteDatabase writableDatabase = blackNumberOpenHelper
				.getWritableDatabase();

		// 调用删除的方法whereClause：删除条件
		writableDatabase.delete("blacknumber", "phone = ?",
				new String[] { phone });

		// 用完记得关闭
		writableDatabase.close();
	}

	/**
	 * 根据传递的电话号码和拦截模式为数据库修改一个条目的方法
	 * 
	 * @param phone
	 *            更新的黑名单号码
	 * @param mode
	 *            更新的拦截模式（1：短信 2：电话 3：所以（电话加短信））
	 */
	public void update(String phone, String mode) {
		// 开启数据库
		SQLiteDatabase writableDatabase = blackNumberOpenHelper
				.getWritableDatabase();

		// 创建ContentValues封装键值对
		ContentValues values = new ContentValues();
		// values.put("phone", phone);
		values.put("mode", mode);

		// 调用插入方法
		writableDatabase.update("blacknumber", values, "phone = ?",
				new String[] { phone });

		// 用完记得关闭
		writableDatabase.close();
	}

	/**
	 * 为数据库增加一个条目的方法
	 * 
	 * @param phone
	 *            添加的黑名单号码
	 * @param mode
	 *            拦截模式（1：短信 2：电话 3：所以（电话加短信））
	 */
	public List<BlackNumberInfo> findAll() {
		// 开启数据库
		SQLiteDatabase writableDatabase = blackNumberOpenHelper
				.getWritableDatabase();

		// 调用插入方法columns：查询条件
		Cursor query = writableDatabase.query("blacknumber", new String[] {
				"phone", "mode" }, null, null, null, null, "_id desc");

		// 创建集合封装javabean
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();

		while (query.moveToNext()) {
			// 将查询结果封装到javaBean中
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = query.getString(0);
			blackNumberInfo.mode = query.getString(1);

			// 添加到集合中
			blackNumberInfos.add(blackNumberInfo);
		}
		// 关闭游标
		query.close();

		// 用完记得关闭
		writableDatabase.close();
		return blackNumberInfos;
	}

	/**
	 * 为数据库增加20逆序条目的方法
	 * 
	 * @param phone
	 *            添加的黑名单号码
	 * @param mode
	 *            拦截模式（1：短信 2：电话 3：所以（电话加短信））
	 */
	public List<BlackNumberInfo> find(int index) {
		// 开启数据库
		SQLiteDatabase writableDatabase = blackNumberOpenHelper
				.getWritableDatabase();

		// 调用插入方法columns：查询条件
		Cursor query = writableDatabase
				.rawQuery(
						"select phone,mode from blacknumber order by _id desc limit ?,20;",
						new String[] { index + "" });

		// 创建集合封装javabean
		List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();

		while (query.moveToNext()) {
			// 将查询结果封装到javaBean中
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.phone = query.getString(0);
			blackNumberInfo.mode = query.getString(1);

			// 添加到集合中
			blackNumberInfos.add(blackNumberInfo);
		}
		// 关闭游标
		query.close();

		// 用完记得关闭
		writableDatabase.close();
		return blackNumberInfos;
	}

	/**
	 * 查询数据库数据总条数的方法
	 * 
	 * @return 返回数据库总数 若为0 则异常
	 */
	public int getCount() {
		// 开启数据库
		SQLiteDatabase writableDatabase = blackNumberOpenHelper
				.getWritableDatabase();

		// 调用插入方法columns：查询条件
		Cursor query = writableDatabase.rawQuery(
				"select count(*) from blacknumber;", null);

		int count = 0;
		while (query.moveToNext()) {
			count = query.getInt(0);
		}
		// 关闭游标
		query.close();

		// 用完记得关闭
		writableDatabase.close();

		return count;
	}

	/**
	 * @param phone当前拨打来的电话号码
	 * @return 查询结果： 1.拦截短信 2拦截电话 3拦截所有 0.没有/异常
	 */
	public int getMode(String phone) {
		// 开启数据库
		SQLiteDatabase writableDatabase = blackNumberOpenHelper.getWritableDatabase();

		// 调用插入方法columns：查询条件
		Cursor cursor = writableDatabase.query("blacknumber", new String[]{"mode"}, "phone=?", new String[]{phone}, null, null, null);
		
		int mode = 0;
		if(cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		
		//关闭流
		writableDatabase.close();
		cursor.close();
		
		return mode;

	}
}
