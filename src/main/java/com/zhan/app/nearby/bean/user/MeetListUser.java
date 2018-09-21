package com.zhan.app.nearby.bean.user;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonFormat;

public class MeetListUser extends BaseUser {
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;

	@Override
	public Date getCreate_time() {
		// TODO Auto-generated method stub
		return super.getCreate_time();
	}

	@Override
	public void setCreate_time(Date create_time) {
		// TODO Auto-generated method stub
		super.setCreate_time(create_time);
	}
}
