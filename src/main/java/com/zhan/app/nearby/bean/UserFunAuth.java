package com.zhan.app.nearby.bean;

import java.io.Serializable;

import com.zhan.app.nearby.annotation.ColumnType;

public class UserFunAuth implements Serializable{
	@ColumnType
	private int id;
	private String auth;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	 

}
