package com.zhan.app.nearby.bean.user;

import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name = "t_user")
public class LoginUser extends LocationUser {

	@Override
	public String getToken() {
		return super.getToken();
	}
}
