package com.zhan.app.nearby.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;

public class DynamicComment {
	@ColumnType
	private long id;
	// @JsonIgnore
	@JSONField(serialize = false)
	private long user_id;

	// @JsonIgnore
	@JSONField(serialize = false)
	private long dynamic_id;

	private String content;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date comment_time;
	@ColumnType
	private User user;
	
	private long at_user_id;
	private long at_comment_id;
	@ColumnType
	private User at_user;
	
	

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getAt_user_id() {
		return at_user_id;
	}

	public void setAt_user_id(long at_user_id) {
		this.at_user_id = at_user_id;
	}

	public User getAt_user() {
		return at_user;
	}

	public void setAt_user(User at_user) {
		this.at_user = at_user;
	}

	public long getAt_comment_id() {
		return at_comment_id;
	}

	public void setAt_comment_id(long at_comment_id) {
		this.at_comment_id = at_comment_id;
	}

}
