package com.zhan.app.nearby.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;

public class Avatar {
	@ColumnType // 忽略保存
	private int id;
	@JSONField(serialize = false)
	private long uid;
	private String avatar;
	@ColumnType // 忽略保存
	private String origin_avatar;

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

}
