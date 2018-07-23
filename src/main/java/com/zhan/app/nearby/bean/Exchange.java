package com.zhan.app.nearby.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Exchange {
	@JsonIgnore
	private long user_id;
	@JsonIgnore
	private String aid;
	private int diamond_count;
	private int rmb_fen;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date finish_time;
	private int state;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public int getDiamond_count() {
		return diamond_count;
	}

	public void setDiamond_count(int diamond_count) {
		this.diamond_count = diamond_count;
	}

	public int getRmb_fen() {
		return rmb_fen;
	}

	public void setRmb_fen(int rmb_fen) {
		this.rmb_fen = rmb_fen;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
