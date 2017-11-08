package com.zhan.app.nearby.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;

public class GiftOwn extends Gift {
	private long user_id;
	private int count;
	private long give_uid;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date give_time;

	
	@ColumnType
	private User receiver;
	@ColumnType
	private User sender;
	
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getGive_uid() {
		return give_uid;
	}

	public void setGive_uid(long give_uid) {
		this.give_uid = give_uid;
	}

	public Date getGive_time() {
		return give_time;
	}

	public void setGive_time(Date give_time) {
		this.give_time = give_time;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

}