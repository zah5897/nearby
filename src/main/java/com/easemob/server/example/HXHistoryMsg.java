package com.easemob.server.example;

import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Table(name = "t_hx_history_msg")
public class HXHistoryMsg {

	private String msg_id;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date send_time;
	private long from_id;
	private long to_id;
	private String chat_type;
	private String type;
	private String content;

	@Transient
	private String from_nick_name;
	@Transient
	private String from_avatar;
	@Transient
	private String to_nick_name;
	@Transient
	private String to_avatar;

	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public Date getSend_time() {
		return send_time;
	}

	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}

	public long getFrom_id() {
		return from_id;
	}

	public void setFrom_id(long from_id) {
		this.from_id = from_id;
	}

	public long getTo_id() {
		return to_id;
	}

	public void setTo_id(long to_id) {
		this.to_id = to_id;
	}

	public String getChat_type() {
		return chat_type;
	}

	public void setChat_type(String chat_type) {
		this.chat_type = chat_type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFrom_nick_name() {
		return from_nick_name;
	}

	public void setFrom_nick_name(String from_nick_name) {
		this.from_nick_name = from_nick_name;
	}

	public String getFrom_avatar() {
		return from_avatar;
	}

	public void setFrom_avatar(String from_avatar) {
		this.from_avatar = from_avatar;
	}

	public String getTo_nick_name() {
		return to_nick_name;
	}

	public void setTo_nick_name(String to_nick_name) {
		this.to_nick_name = to_nick_name;
	}

	public String getTo_avatar() {
		return to_avatar;
	}

	public void setTo_avatar(String to_avatar) {
		this.to_avatar = to_avatar;
	}

}
