package com.wuwei.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.R.integer;

public class StreamUtil {

	/**
	 * @param is:代表数据流
	 * @return 返回值代表转换后的字符串，返回null代表异常
	 */
	public static String StreamtoString(InputStream is) {
		//在存储的过程中，将读取的内容存在缓存中，然后一次性转换成字符串返回，由于是小文件
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		//读流操作，直到没有为止
		byte[] buffer = new byte[1024];
		
		int temp=-1;
		
		try {
			while ((temp=is.read(buffer))!=-1) {
				//将读取的数据写在bos里
				bos.write(buffer, 0, temp);
			}
			//返回转换好的字符串
			return bos.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				//使用结束要关闭，先读后写
				is.close();
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
		
		
	}
	
}
