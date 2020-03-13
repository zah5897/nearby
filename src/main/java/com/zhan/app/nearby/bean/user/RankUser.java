package com.zhan.app.nearby.bean.user;

import javax.persistence.Table;

@SuppressWarnings("serial")
@Table(name = "t_user")
public class RankUser extends SimpleUser {
	private int shanbei;

	public int getShanbei() {
		return shanbei;
	}

	public void setShanbei(int shanbei) {
		this.shanbei = shanbei;
	}

}
