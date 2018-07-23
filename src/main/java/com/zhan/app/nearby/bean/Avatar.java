package com.zhan.app.nearby.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;

public class Avatar {
	@ColumnType // 忽略保存
	private int id;
	@JsonIgnore
	private long uid;
	private String avatar;
	@ColumnType // 忽略保存
	private String origin_avatar;
	
	@JsonIgnore
	private String illegal_avatar;
	@JsonIgnore
	private Date checked_time;
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getOrigin_avatar() {
		return origin_avatar;
	}

	public void setOrigin_avatar(String origin_avatar) {
		this.origin_avatar = origin_avatar;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIllegal_avatar() {
		return illegal_avatar;
	}

	public void setIllegal_avatar(String illegal_avatar) {
		this.illegal_avatar = illegal_avatar;
	}

	public Date getChecked_time() {
		return checked_time;
	}

	public void setChecked_time(Date checked_time) {
		this.checked_time = checked_time;
	}

}
