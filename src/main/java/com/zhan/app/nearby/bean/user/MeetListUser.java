package com.zhan.app.nearby.bean.user;

import java.util.Date;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@SuppressWarnings("serial")
@Table(name = "t_sys_user")
public class MeetListUser extends BaseVipUser {
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;

	@Override
	public Date getCreate_time() {
		return super.getCreate_time();
	}

	@Override
	public void setCreate_time(Date create_time) {
		super.setCreate_time(create_time);
	}
}
