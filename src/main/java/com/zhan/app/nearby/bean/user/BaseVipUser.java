package com.zhan.app.nearby.bean.user;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;

public class BaseVipUser extends BaseUser {

	@ColumnType
	@JSONField(name = "is_vip")
	private boolean isVip;

	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

}
