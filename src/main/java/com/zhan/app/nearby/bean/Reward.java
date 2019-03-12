package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.user.BaseUser;

public class Reward implements Serializable{
	@JsonIgnore
	private long bottle_id;
	@JsonIgnore
	private long uid;
	private int reward;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date create_time;
	@ColumnType
	private long create_time_v2;
	
	private String answer;

	@ColumnType
	private int count;
	@ColumnType
	private BaseUser user;
	@ColumnType
	private Bottle bottle;

	public long getBottle_id() {
		return bottle_id;
	}

	public void setBottle_id(long bottle_id) {
		this.bottle_id = bottle_id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public Bottle getBottle() {
		return bottle;
	}

	public void setBottle(Bottle bottle) {
		this.bottle = bottle;
	}

	public long getCreate_time_v2() {
		return create_time_v2;
	}

	public void setCreate_time_v2(long create_time_v2) {
		this.create_time_v2 = create_time_v2;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
