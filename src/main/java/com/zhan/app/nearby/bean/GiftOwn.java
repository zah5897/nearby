package com.zhan.app.nearby.bean;

import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.BaseVipUser;

@SuppressWarnings("serial")
@Table(name = "t_gift_own")
public class GiftOwn extends Gift {
	private long user_id;
	private int count;
	private long give_uid;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd  HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date give_time;

	@Transient
	private long give_time_v2;

	@Transient
	private BaseVipUser receiver;
	@Transient
	private BaseVipUser sender;

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
		this.give_time_v2 = give_time.getTime() / 1000;
	}

	public BaseVipUser getReceiver() {
		return receiver;
	}

	public void setReceiver(BaseVipUser receiver) {
		this.receiver = receiver;
	}

	public BaseVipUser getSender() {
		return sender;
	}

	public void setSender(BaseVipUser sender) {
		this.sender = sender;
	}

	@Override
	public boolean equals(Object obj) {
		GiftOwn g = (GiftOwn) obj;
		return g.getId() == this.getId();
	}

	public long getGive_time_v2() {
		return give_time_v2;
	}
}
