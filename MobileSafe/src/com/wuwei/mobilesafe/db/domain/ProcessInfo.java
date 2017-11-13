package com.wuwei.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

/**
 * @author 伟 封装进程信息的javabean
 */
public class ProcessInfo {

	public String name;// 应用名称
	public String packagename;// 应用包名
	public Drawable icon;// 用于图标
	public long memSize;// 占用大小
	public boolean isSystem;// 是否是系统应用
	public boolean isCheck;// 是否被选择
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public String getPackagename() {
		return packagename;
	}
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

}
