package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
@Table(name = "t_user_avatars")
public class Avatar implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@JsonIgnore
	private long uid;
	private String avatar;
	@Transient  // 忽略保存
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
