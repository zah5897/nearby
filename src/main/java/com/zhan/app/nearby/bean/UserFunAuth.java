package com.zhan.app.nearby.bean;

import java.io.Serializable;


@SuppressWarnings("serial")
public class UserFunAuth implements Serializable{
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
