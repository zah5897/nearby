package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhan.app.nearby.annotation.ColumnType;

public class VipUser implements Serializable{
	private long user_id;
	private int vip_id;
	private String aid;
	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	private Date start_time;
	@ColumnType
	private long start_time_v2;
	
	
	
	
	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	private Date end_time;
	@ColumnType
	private Date end_time_v2;
	private String last_order_no;

	@ColumnType
	private int dayDiff;
	
	private String mark;
	
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getVip_id() {
		return vip_id;
	}

	public void setVip_id(int vip_id) {
		this.vip_id = vip_id;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
		this.start_time_v2=start_time.getTime()/1000;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
		this.end_time_v2=end_time;
	}

	public String getLast_order_no() {
		return last_order_no;
	}

	public void setLast_order_no(String last_order_no) {
		this.last_order_no = last_order_no;
	}

	public int getDayDiff() {
		return dayDiff;
	}

	public void setDayDiff(int dayDiff) {
		this.dayDiff = dayDiff;
	}

	 public long getStart_time_v2() {
		return start_time_v2;
	}
	 public Date getEnd_time_v2() {
		return end_time_v2;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
	 
	 
}
