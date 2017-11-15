package com.zhan.app.nearby.bean;

/**
 * 魅力排行榜数据
 * 
 * @author zah
 *
 */
public class MeiLi {
	private int value;
	private int shanbei;
	private int be_like_count;
	private User user;
	
	private boolean is_vip;
	

	 

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getShanbei() {
		return shanbei;
	}

	public void setShanbei(int shanbei) {
		this.shanbei = shanbei;
	}

	public int getBe_like_count() {
		return be_like_count;
	}

	public void setBe_like_count(int be_like_count) {
		this.be_like_count = be_like_count;
	}

	public boolean isIs_vip() {
		return is_vip;
	}

	public void setIs_vip(boolean is_vip) {
		this.is_vip = is_vip;
	}

	
}
