package com.zhan.app.nearby.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.user.BaseUser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(description= "短视频对象")
public class Video {
	private String id;
	@JsonIgnore
	private long uid;
	
	@JsonIgnore
	private String video_name;
	@JsonIgnore
	private String thumb_img_name;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;
	@ColumnType
	private long create_time_v2;
	private int duration;
	@ColumnType
	private String thumb_url;
	@ColumnType
	private String url;
	
	private String title;
	
	
	private int comment_count;
	
	private int praise_count;
	
	
	private int share_count;
	
	private int store_count;
	
	public int getComment_count() {
		return comment_count;
	}
	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	public int getPraise_count() {
		return praise_count;
	}
	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}
	public int getShare_count() {
		return share_count;
	}
	public void setShare_count(int share_count) {
		this.share_count = share_count;
	}
	public int getStore_count() {
		return store_count;
	}
	public void setStore_count(int store_count) {
		this.store_count = store_count;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	private BaseUser sender;
	public BaseUser getSender() {
		return sender;
	}
	public void setSender(BaseUser sender) {
		this.sender = sender;
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	 

	public String getVideo_name() {
		return video_name;
	}

	public void setVideo_name(String video_name) {
		this.video_name = video_name;
	}

	public String getThumb_img_name() {
		return thumb_img_name;
	}

	public void setThumb_img_name(String thumb_img_name) {
		this.thumb_img_name = thumb_img_name;
	}

	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

}
