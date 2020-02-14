package com.zhan.app.nearby.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.user.BaseUser;

public class VideoComment {
	@ColumnType
	private int id;
	private String video_id;
	private String content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;
	@ColumnType
	private long create_time_v2;
	private int time_point;
	private long uid;
	
	@ColumnType
	private Video video;
	@ColumnType
	private BaseUser user;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVideo_id() {
		return video_id;
	}
	public void setVideo_id(String video_id) {
		this.video_id = video_id;
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
		if(create_time!=null) {
			this.create_time_v2=create_time.getTime()/1000;
		}
	}
	public int getTime_point() {
		return time_point;
	}
	public void setTime_point(int time_point) {
		this.time_point = time_point;
	}
	public long getUid() {
		return uid;
	}
	
	public long getCreate_time_v2() {
		return create_time_v2;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}
	public Video getVideo() {
		return video;
	}
	public void setVideo(Video video) {
		this.video = video;
	}
	public BaseUser getUser() {
		return user;
	}
	public void setUser(BaseUser user) {
		this.user = user;
	}
	
	
}
