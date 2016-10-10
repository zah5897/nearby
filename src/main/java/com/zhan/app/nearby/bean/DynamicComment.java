package com.zhan.app.nearby.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;

public class DynamicComment {
	@ColumnType
	private long id;
	@JsonIgnore
	private long user_id;

	@JsonIgnore
	private long dynamic_id;

	private String content;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JSONField(format = "yyyy-MM-dd")
	private Date comment_time;
	@ColumnType
	private User comment_user;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public long getDynamic_id() {
		return dynamic_id;
	}
	public void setDynamic_id(long dynamic_id) {
		this.dynamic_id = dynamic_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getComment_time() {
		return comment_time;
	}

	public void setComment_time(Date comment_time) {
		this.comment_time = comment_time;
	}
	public User getComment_user() {
		return comment_user;
	}
	public void setComment_user(User comment_user) {
		this.comment_user = comment_user;
	}

}
