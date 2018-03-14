package com.zhan.app.nearby.bean;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.comm.UserType;

public class ManagerUser {
	private long user_id;

	private String nick_name;
	private String age;
	private String sex; // 0 男 1 女
	private String avatar;
	private String origin_avatar;
	private Date birthday;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;

	// 区分游客和正式用户
	private short type = (short) UserType.OFFIEC.ordinal(); // 默认为正式用户

	private int state;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getCreate_time() {
		return create_time;
	}
}
