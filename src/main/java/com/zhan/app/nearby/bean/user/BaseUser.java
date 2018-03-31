package com.zhan.app.nearby.bean.user;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.comm.UserType;

public class BaseUser {

	public BaseUser(long user_id) {
		this.user_id = user_id;
	}

	public BaseUser() {
	}

	@ColumnType
	private long user_id;
	@JSONField(serialize = false)
	private String mobile;

	@JSONField(serialize = false)
	private String password;
	private String name;
	private String nick_name;
	private String sex; // 0 女，1 男，2 未知

	private String avatar;
	@ColumnType // 忽略保存
	private String origin_avatar;

	// 区分游客和正式用户
	private short type = (short) UserType.OFFIEC.ordinal(); // 默认为正式用户

	@JSONField(serialize = false)
	private Date create_time;

	private String token;
	@JSONField(name = "_ua", serialize = false)
	private String _ua;
	@JSONField(serialize = false)
	private String aid;

	@JSONField(format = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	@JSONField(serialize = false)
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JSONField(serialize = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	@JSONField(serialize = false)
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public String get_ua() {
		return _ua;
	}

	public void set_ua(String _ua) {
		this._ua = _ua;
	}
}
