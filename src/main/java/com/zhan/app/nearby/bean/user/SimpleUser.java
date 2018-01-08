package com.zhan.app.nearby.bean.user;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;

public class SimpleUser extends BaseUser {

	public SimpleUser(long user_id) {
		super(user_id);
	}

	public SimpleUser() {
	}

	@ColumnType // 不用插入数据库字段
	private String age;
	private String signature;

	@JSONField(format = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	private String weight;// 体重
	private String height;// 身高

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date last_login_time;

	// 区分国外用户
	// @JsonIgnore
	@JSONField(serialize = false)
	private String zh_cn;
	// 设备token
	// @JsonIgnore
	@JSONField(serialize = false)
	private String device_token;

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public Date getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Date last_login_time) {
		this.last_login_time = last_login_time;
	}



	public String getZh_cn() {
		return zh_cn;
	}

	public void setZh_cn(String zh_cn) {
		this.zh_cn = zh_cn;
	}

	public String getDevice_token() {
		return device_token;
	}

	public void setDevice_token(String device_token) {
		this.device_token = device_token;
	}

}
