package com.zhan.app.nearby.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.user.BaseVipUser;

public class DynamicComment {
	@ColumnType
	private long id;
	@JsonIgnore
	private long user_id;

	@JsonIgnore
	private long dynamic_id;

	private String content;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	private Date comment_time;
	@ColumnType
	private long comment_time_v2;
	@ColumnType
	private BaseVipUser user;
	@JsonIgnore
	private long at_user_id;
	@JsonIgnore
	private long at_comment_id;
	@ColumnType
	private BaseVipUser at_user;
	@ColumnType
	private DynamicComment atComment;

	//评论状态
	@JsonIgnore
	private int status;
	
	
	private int subComment;
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

	public long getAt_user_id() {
		return at_user_id;
	}

	public void setAt_user_id(long at_user_id) {
		this.at_user_id = at_user_id;
	}

	public long getAt_comment_id() {
		return at_comment_id;
	}

	public void setAt_comment_id(long at_comment_id) {
		this.at_comment_id = at_comment_id;
	}

	public DynamicComment getAtComment() {
		return atComment;
	}

	public void setAtComment(DynamicComment atComment) {
		this.atComment = atComment;
	}

	public BaseVipUser getUser() {
		return user;
	}

	public void setUser(BaseVipUser user) {
		this.user = user;
	}

	public BaseVipUser getAt_user() {
		return at_user;
	}

	public void setAt_user(BaseVipUser at_user) {
		this.at_user = at_user;
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
    
    
}
