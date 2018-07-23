package com.zhan.app.nearby.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;

public class PersonalInfo {
	private long user_id;
	@ColumnType
	@JsonIgnore
	private String token;
	private String personal_name;
	private String personal_id;
	private String mobile;
	private String zhifubao_access_number;
	private String aid;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getPersonal_name() {
		return personal_name;
	}

	public void setPersonal_name(String personal_name) {
		this.personal_name = personal_name;
	}

	public String getPersonal_id() {
		return personal_id;
	}

	public void setPersonal_id(String personal_id) {
		this.personal_id = personal_id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getZhifubao_access_number() {
		return zhifubao_access_number;
	}

	public void setZhifubao_access_number(String zhifubao_access_number) {
		this.zhifubao_access_number = zhifubao_access_number;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
