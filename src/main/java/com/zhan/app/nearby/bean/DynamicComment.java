package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.bean.user.BaseVipUser;

@SuppressWarnings("serial")
@Table(name = "t_dynamic_comment")
public class DynamicComment implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@JsonIgnore
	private long user_id;

	@JsonIgnore
	private long dynamic_id;

	private String content;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	private Date comment_time;
	@Transient
	private long comment_time_v2;
	@Transient
	private BaseVipUser user;
	
	@JsonIgnore //上层评论
	private long pid;
	
	@Transient
	private List<DynamicComment> sub_comm;
	
	//评论状态
	@JsonIgnore
	private int status;
 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getDynamic_id() {
		return dynamic_id;
	}

	public void setDynamic_id(long dynamic_id) {
		this.dynamic_id = dynamic_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getComment_time() {
		return comment_time;
	}

	public void setComment_time(Date comment_time) {
		this.comment_time = comment_time;
		this.comment_time_v2=comment_time.getTime()/1000;
	}

	 
	public BaseVipUser getUser() {
		return user;
	}

	public void setUser(BaseVipUser user) {
		this.user = user;
	}

	 
    public long getComment_time_v2() {
	    return comment_time_v2;
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public List<DynamicComment> getSub_comm() {
		return sub_comm;
	}

	public void setSub_comm(List<DynamicComment> sub_comm) {
		this.sub_comm = sub_comm;
	}
    
    
}
