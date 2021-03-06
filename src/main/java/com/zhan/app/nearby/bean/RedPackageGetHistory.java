package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.bean.user.BaseUser;

@SuppressWarnings("serial")
@Table(name = "t_redpackage_get_history")
public class RedPackageGetHistory implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private long bid;
	private long uid;
	private int red_package_coin_get;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;
	@Transient
	private long create_time_v2;

	@JsonIgnore
	@Transient
	private BaseUser user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getBid() {
		return bid;
	}

	public void setBid(long bid) {
		this.bid = bid;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getRed_package_coin_get() {
		return red_package_coin_get;
	}

	public void setRed_package_coin_get(int red_package_coin_get) {
		this.red_package_coin_get = red_package_coin_get;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
		this.create_time_v2 = create_time.getTime() / 1000;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

}
