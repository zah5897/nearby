package com.zhan.app.nearby.bean.user;

import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Table(name = "t_user")
public class RankUser extends SimpleUser {
	
	@Transient
	private int shanbei;
	public int getShanbei() {
		return shanbei;
	}

	public void setShanbei(int shanbei) {
		this.shanbei = shanbei;
	}

}
