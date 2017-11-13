package com.wuwei.mobilesafe.db.domain;

public class BlackNumberInfo {

	public String phone;
	public String mode;
	//生成get方法的快捷方法：Alt+shift+s
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "BlackNumberInfo [phone=" + phone + ", mode=" + mode + "]";
	}
	
}
