package com.zhan.app.nearby.bean;

import java.util.Date;

import com.zhan.app.nearby.annotation.ColumnType;

/*
 * 表白
 */
public class Express {
	@ColumnType
	public long id;
	public String content;
	private long user_id;

	private long to_user_id;
	private Date create_time;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getTo_user_id() {
		return to_user_id;
	}

	public void setTo_user_id(long to_user_id) {
		this.to_user_id = to_user_id;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

}
