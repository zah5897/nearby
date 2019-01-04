package com.zhan.app.nearby.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.util.TextUtils;

public class ManagerUser {
	private long user_id;

	private String nick_name;
	private String age;
	private String sex; // 0 男 1 女
	private String avatar;
	private String origin_avatar;
	private Date birthday;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date create_time;

	@ColumnType
	private long create_time_v2;
	@JsonIgnore
	private String _ua;
	@ColumnType
	private int _from;
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
		this.create_time_v2=create_time.getTime()/1000;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public long getCreate_time_v2() {
		return create_time_v2;
	}

	public String get_ua() {
		return _ua;
	}

	public void set_ua(String _ua) {
		this._ua = _ua;
		if(!TextUtils.isEmpty(_ua)) {
			if(_ua.startsWith("a")) {
			   this.set_from(1);
			}else {
				this.set_from(2);
			}
		}
	}

	public int get_from() {
		return _from;
	}

	public void set_from(int _from) {
		this._from = _from;
	}
	
	
}
