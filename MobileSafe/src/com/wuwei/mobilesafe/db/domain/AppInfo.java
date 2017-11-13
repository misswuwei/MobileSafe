package com.wuwei.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

/**
 * @author 伟
 *封装应用信息的javabean
 */
public class AppInfo {
	//（名称、包名、图标、（内存orSD卡）、（系统or用户））
	public String Name;
	public String PackageName;
	public Drawable icon;
	public boolean isSdCard;
	public boolean isSystem;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getPackageName() {
		return PackageName;
	}
	public void setPackageName(String packageName) {
		PackageName = packageName;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public boolean isSdCard() {
		return isSdCard;
	}
	public void setSdCard(boolean isSdCard) {
		this.isSdCard = isSdCard;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	
}
