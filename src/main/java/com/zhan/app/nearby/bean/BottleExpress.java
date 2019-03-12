package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * t_user_express 
 * @author zah
 *
 */
public class BottleExpress implements Serializable{
   
	private long uid;
	private long to_uid;
	private String content;
	private Date create_time;
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public long getTo_uid() {
		return to_uid;
	}
	public void setTo_uid(long to_uid) {
		this.to_uid = to_uid;
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
	
	
}
