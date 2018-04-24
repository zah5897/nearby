package com.zhan.app.nearby.bean;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.user.BaseUser;

public class Report {
	@ColumnType
	private int id;
	private long user_id;
	private long target_id;
	private String tag_id;
	private String content;
	private int type;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date approval_time;
	private int approval_result;

	private BaseUser user;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getTarget_id() {
		return target_id;
	}

	public void setTarget_id(long target_id) {
		this.target_id = target_id;
	}

	public String getTag_id() {
		return tag_id;
	}

	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public Date getApproval_time() {
		return approval_time;
	}

	public void setApproval_time(Date approval_time) {
		this.approval_time = approval_time;
	}

	public int getApproval_result() {
		return approval_result;
	}

	public void setApproval_result(int approval_result) {
		this.approval_result = approval_result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
