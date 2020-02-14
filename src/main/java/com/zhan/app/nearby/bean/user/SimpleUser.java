package com.zhan.app.nearby.bean.user;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;

@SuppressWarnings("serial")
public class SimpleUser extends BaseUser {

	public SimpleUser(long user_id) {
		super(user_id);
	}

	public SimpleUser() {
	}

	@ColumnType // 不用插入数据库字段
	private String age;
	private String signature;


	private String weight;// 体重
	private String height;// 身高
	
	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	private Date last_login_time;

	@ColumnType // 不用插入数据库字段
	private long last_login_time_v2;
	
	// 区分国外用户
	@JsonIgnore
	private String zh_cn;
	// 设备token
	@JsonIgnore
	private String device_token;

	@JsonIgnore
	private String openid;
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
		if(last_login_time!=null) {
			last_login_time_v2=last_login_time.getTime()/1000;
		}
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

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public long getLast_login_time_v2() {
		return last_login_time_v2;
	}
}
