package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhan.app.nearby.bean.user.BaseUser;

@SuppressWarnings("serial")
@Table(name = "t_game_score")
public class GameScore implements Serializable {
	private int score;
	private long uid;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;
	private String gid;
	@Transient
	private BaseUser user;
	
	@Transient
	private int position=-1;

	@Transient
	private long create_time_v2;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
		this.create_time_v2 = create_time.getTime() / 1000;
	}

    

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public long getCreate_time_v2() {
		return create_time_v2;
	}

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
}
