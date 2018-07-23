package com.zhan.app.nearby.bean;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.user.BaseUser;

public class Bottle {
	@ColumnType
	private long id;
	private String content;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date create_time;
	private int type;

	@JsonIgnore
	private long user_id;
	@ColumnType
	private BaseUser sender;
	@ColumnType
	private List<BaseUser> scan_user_list;
	@ColumnType
	private int view_nums;

	private int state = 0;

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

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public BaseUser getSender() {
		return sender;
	}

	public void setSender(BaseUser sender) {
		this.sender = sender;
	}

	public List<BaseUser> getScan_user_list() {
		return scan_user_list;
	}

	public void setScan_user_list(List<BaseUser> scan_user_list) {
		this.scan_user_list = scan_user_list;
	}

	public int getView_nums() {
		return view_nums;
	}

	public void setView_nums(int view_nums) {
		this.view_nums = view_nums;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
