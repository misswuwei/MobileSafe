package com.wuwei.mobilesafe.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Contactables;
import android.util.Xml;

public class SmsBackUp {
	private static int index=0;
	private static FileOutputStream fos;
	private static Cursor cursor;
	
	public static void BackUp(Context ctx,String path,CallBack callBack){
		/*需要创建内容解析器，所以要用到content，创建备份文件的 路径还需要传递地址，
		 * 备份完一条短信后要告知进度条，所以还需要传递对话框对象
		 * */
		try {
		//根据路径创建备份文件
		File file = new File(path);
		
		fos = new FileOutputStream(file);
		
		cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"),
				new String[]{"address","date","type","body"}, 
				null, null, null);
		
		//拿到查询结果后给控件设置总长度
		//做容错处理
		if (callBack!=null) {
			callBack.setMax(cursor.getCount());
		}
		
		//序列化数据库中的数据放置到xml中,创建一个xml文件
		XmlSerializer newSerializer = Xml.newSerializer();
		//做相应的设置
		newSerializer.setOutput(fos,"utf-8");//写入文件和方式
		//设置xml的规范DTD
		newSerializer.startDocument("utf-8", true);//设置是否独立存在true
		newSerializer.startTag(null, "smss");
		//遍历查询结果写入文件
		while(cursor.moveToNext()){
			//开始填写数据
			newSerializer.startTag(null, "sms");
			
			newSerializer.startTag(null, "address");
			newSerializer.text(cursor.getString(0));
			newSerializer.endTag(null, "address");
			
			newSerializer.startTag(null, "date");
			newSerializer.text(cursor.getString(1));
			newSerializer.endTag(null, "date");
			
			newSerializer.startTag(null, "type");
			newSerializer.text(cursor.getString(2));
			newSerializer.endTag(null, "type");
			
			newSerializer.startTag(null, "body");
			newSerializer.text(cursor.getString(3));
			newSerializer.endTag(null, "body");
			
			newSerializer.endTag(null, "sms");
			
			//为了让执行过程 
			
			//存完一条让进度条更新
			index++;
			Thread.sleep(500);
			callBack.setProgress(index);
			/**ui在子线程中更新是不允许的，只有progressDialog例外，可以在子线程中做更新
			 * */
			
			
			/**记得添加权限：
			 *    <uses-permission android:name="android.permission.READ_SMS"/>
    				<uses-permission android:name="android.permission.WRITE_SMS"/>
			 * **/
		}
		newSerializer.endTag(null, "smss");
		newSerializer.endDocument();//结束节点
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*回调：能够解决需求来回变化的情况
	 * 1.创建一个接口定义需要变化的为实现的方法
	 * 2.传递一个实现该接口的对象，在该对象中定义需要变化的未实现的方法。
	 * 3.在合适的地方做调用
	 * */
	public interface CallBack{
		//在接口中定义一个设置 进度条总长度的方法
		public void setMax(int max);
		
		//在接口中定义备份过程中做百分比更新的方法
		public void setProgress(int index);
		
	}
}
