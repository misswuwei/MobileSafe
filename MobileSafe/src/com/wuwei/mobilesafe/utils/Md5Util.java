package com.wuwei.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.R.integer;


public class Md5Util {

	/**
	 * @param psd	需要进行MD5加密的密码
	 * @return	加密后返回的密码
	 */
	public static String encoder(String psd) {
		//[0]MD5加盐操作
		psd = psd + "mobilesafe";
		
		try {
			//[1]指定加密的算法类型
			MessageDigest digest = MessageDigest.getInstance("MD5"); 
			
			//[2]将需要转换的字符串转换成byte[]数组进行随机哈希的过程
			byte[] bs = digest.digest(psd.getBytes());//将字符转换成byte的方法
			/*
			 * 这时，bs里面已将字符串转换成16位的字符串，还要让其翻倍，变成32位的字符串
			 * */
			
			//用于最后拼接处理
			StringBuffer stringBuffer = new StringBuffer();
			
			//[3]转换成32位的加密步骤如下，遍历bs
			for (byte b : bs) {
				
				//[4]
				int i = b & 0xff;//byte b为8位，0xff也是8位，做与操作后赋值给4个字节的int就变成了32位
				
				//[5]将i转成16进制字符
				String hexString = Integer.toHexString(i); //每循环一次产生两位字符 ，一共16次
				
				//[6]因为可能会产生只有一位字符的情况，还要判断做赋0处理
				if (hexString.length()<2) {
					hexString="0"+hexString;
				}	
				
				//拼接字符串
				stringBuffer.append(hexString);
			}
			String string = stringBuffer.toString();
			return string;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
		
	}
}
