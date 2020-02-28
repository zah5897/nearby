package com.zhan.app.nearby.bean;

import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Transient;


@Table(name = "t_video")
public class TelVideo {
	private String id;
	private long uid;
	private String client_uuid;
	private int type;// 0 start ,1 live, 2 stop
	private Date create_time;
	@Transient
	private long create_time_v2;
	private int time_value;
	private int state;   //0为未扣费记录，1为已扣费记录

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getClient_uuid() {
		return client_uuid;
	}

	public void setClient_uuid(String client_uuid) {
		this.client_uuid = client_uuid;
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
		if (create_time != null) {
			this.create_time_v2 = create_time.getTime() / 1000;
		}
	}

	public long getCreate_time_v2() {
		return create_time_v2;
	}

	public void setCreate_time_v2(long create_time_v2) {
		this.create_time_v2 = create_time_v2;
	}

	public int getTime_value() {
		return time_value;
	}

	public void setTime_value(int time_value) {
		this.time_value = time_value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
