package com.zhan.app.nearby.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.property.MsgAttention;
import com.zhan.app.nearby.bean.user.BaseUser;

public class DynamicMessage {
	private long id;
	private long dynamic_id;
	private String content;
	@JsonIgnore
	private long user_id;
	@JsonIgnore
	private long by_user_id;

	private int type;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date create_time;

	
	private int isReadNum;
	@ColumnType
	private BaseUser user;
	@ColumnType
	private UserDynamic dynamic;

	private MsgAttention attention;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDynamic_id() {
		return dynamic_id;
	}

	public void setDynamic_id(long dynamic_id) {
		this.dynamic_id = dynamic_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public UserDynamic getDynamic() {
		return dynamic;
	}

	public void setDynamic(UserDynamic dynamic) {
		this.dynamic = dynamic;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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

	public long getBy_user_id() {
		return by_user_id;
	}

	public void setBy_user_id(long by_user_id) {
		this.by_user_id = by_user_id;
	}

	public MsgAttention getAttention() {
		return attention;
	}

	public void setAttention(MsgAttention attention) {
		this.attention = attention;
	}

	public int getIsReadNum() {
		return isReadNum;
	}

	public void setIsReadNum(int isReadNum) {
		this.isReadNum = isReadNum;
	}

	 

}
