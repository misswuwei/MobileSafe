package com.wuwei.mobilesafe.engine;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class VirusDao {
	/**
	 * 该类在引擎包中，所以一个使用方法会和常规不一致
	 * */
	// 1.指定访问的数据库的路径
	public static String path = "data/data/com.wuwei.mobilesafe/files/antivirus.db";
	
	/**提供病毒数据库信息集合的方法的
	 * @return
	 */
	public static List<String> getVirusList(){
		//开启数据库法方法
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		
		//创建集合封装数据
		List<String> virusList = new ArrayList<String>();
		//查询数据库
		Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
		while (cursor.moveToNext()) {
			virusList.add(cursor.getString(0));
		}
		
		db.close();
		cursor.close();
		
		return virusList;
	}
		

}
