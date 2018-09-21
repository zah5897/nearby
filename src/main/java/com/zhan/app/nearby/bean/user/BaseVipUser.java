package com.zhan.app.nearby.bean.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhan.app.nearby.annotation.ColumnType;

public class BaseVipUser extends BaseUser {

	@ColumnType
	@JsonProperty("is_vip")
	private boolean isVip;
	@JsonIgnore
	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

}
