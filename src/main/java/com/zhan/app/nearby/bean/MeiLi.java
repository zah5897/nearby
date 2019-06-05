package com.zhan.app.nearby.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhan.app.nearby.bean.user.BaseUser;

/**
 * 魅力排行榜数据
 * 
 * @author zah
 *
 */
@SuppressWarnings("serial")
public class MeiLi implements Serializable{
	private int value;
	private int shanbei;
	private int be_like_count;
	private BaseUser user;
	@JsonProperty("is_vip")
	private boolean is_vip;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
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

	@JsonIgnore
	public boolean isIs_vip() {
		return is_vip;
	}

	@JsonIgnore
	public void setIs_vip(boolean is_vip) {
		this.is_vip = is_vip;
	}

}
