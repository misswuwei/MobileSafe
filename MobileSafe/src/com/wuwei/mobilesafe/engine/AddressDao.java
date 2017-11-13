package com.wuwei.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

public class AddressDao {
	/**
	 * 该类在引擎包中，所以一个使用方法会和常规不一致
	 * */
	// 1.指定访问的数据库的路径
	public static String path = "data/data/com.wuwei.mobilesafe/files/address.db";
	private static String mLocation = "未知号码";

	/**
	 * 开启数据库,在数据库中查询传递的号码的归属地
	 * 
	 * @param phone
	 *            查询的电话号码
	 */
	public static String getAddress(String phone) {
		/**
		 * 先对号码进行正则处理，判断用户输入的是否是正常的手机号
		 * */
		// 创建正则公式
		String regularExpression = "^1[3-8]\\d{9}";
		
		String area = "区号"; 
		
		// 开启数据库,并且以只读的形式开启
	    SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
							SQLiteDatabase.OPEN_READONLY);

		if (phone.matches(regularExpression)) {

			// 对传入的电话号码做处理,正则算法处理和前7位截取处理
			phone = phone.substring(0, 7);


			// 进行数据库查询
			Cursor cursor = db.query("data1", new String[]{ "outkey" },
					"id = ?", new String[] {phone}, null, null, null);

			// 进行非NULL判断
			if (cursor.moveToNext()) {
				// 获取查询结果
				String outkey = cursor.getString(0);
				
				System.out.println("outkey="+outkey);

				// 再根据查询结果进行二次查询
				Cursor indexCursor = db.query("data2",
						new String[] { "location" }, "id = ?",
						new String[] { outkey }, null, null, null);
				if (indexCursor.moveToNext()) {
					mLocation = indexCursor.getString(0);
					System.out.println("location="+mLocation);
				}
			}else {//没查到结果
				mLocation = "未知号码";
			}
		}else {//如果不是正常的号码
			int length =  phone.length();
			switch (length) {
			case 3://110  120  114  119
				mLocation = "报警电话";
				break;
			case 4://5566  5558
				mLocation = "模拟电话";
				break;
			case 5://  10086  95566
				mLocation = "服务电话";
				break;
			case 7:
				mLocation = "本地电话";
				break;
			case 8:
				mLocation = "本地电话";
				break;
			case 11://3（区号）+8（座机号码），要对区号进行data2表的查询
				//先截取区号
				phone = phone.substring(0, 3);
				Cursor query = db.query("data2", new String[]{"location"}, "area = ?", new String[]{phone}, null, null, null);
				if(query.moveToNext()){
					mLocation = query.getString(0);
				}else {
					mLocation = "未知号码";
				}
				break;
			case 12://4（区号）+8（外地座机号码），要对区号进行data2表的查询
				//先截取区号
				phone = phone.substring(0, 4);
				Cursor query1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{phone}, null, null, null);
				if(query1.moveToNext()){
					mLocation = query1.getString(0);
				}else {
					mLocation = "未知号码";
				}
				break;
			}
			
		}
		return mLocation;
	}
	

}
