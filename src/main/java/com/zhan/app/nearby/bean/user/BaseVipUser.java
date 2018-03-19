package com.zhan.app.nearby.bean.user;

import com.zhan.app.nearby.annotation.ColumnType;

public class BaseVipUser extends BaseUser {

	@ColumnType
	private boolean isVip;

	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

}
