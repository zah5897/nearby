package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhan.app.nearby.bean.user.BaseUser;

@SuppressWarnings("serial")
@Table(name = "t_report_record")
public class Report implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private long user_id;
	private long target_id;
	private String tag_id;
	@Transient
	private String tag_txt;
	private String content;
	private int type;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date create_time;
	@Transient
	private long create_time_v2;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date approval_time;
	
	@Transient
	private long approval_time_v2;
	
	
	private int approval_result;
	@Transient
	private BaseUser user;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getTarget_id() {
		return target_id;
	}

	public void setTarget_id(long target_id) {
		this.target_id = target_id;
	}

	public String getTag_id() {
		return tag_id;
	}

	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
		this.create_time_v2=create_time.getTime()/1000;
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public Date getApproval_time() {
		return approval_time;
	}

	public void setApproval_time(Date approval_time) {
		this.approval_time = approval_time;
		if(approval_time!=null) {
			this.approval_time_v2=approval_time.getTime()/1000;
		}
	}

	public int getApproval_result() {
		return approval_result;
	}

	public void setApproval_result(int approval_result) {
		this.approval_result = approval_result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public long getCreate_time_v2() {
		return create_time_v2;
	}
	
	public long getApproval_time_v2() {
		return approval_time_v2;
	}

	public String getTag_txt() {
		return tag_txt;
	}

	public void setTag_txt(String tag_txt) {
		this.tag_txt = tag_txt;
	}
	
	
}
