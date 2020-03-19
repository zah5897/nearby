package com.zhan.app.nearby.bean;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.bean.user.BaseUser;

import io.swagger.annotations.ApiModel;
@ApiModel(description= "短视频对象")
@Table(name = "t_short_video")
public class Video {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@JsonIgnore
	private long uid;
	
	@JsonIgnore
	private String video_name;
	@JsonIgnore
	private String thumb_img_name;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	private Date create_time;
	@Transient
	private long create_time_v2;
	
	private float duration;
	@Transient
	private String thumb_url;
	@Transient
	private String url;
	
	private String title;
	
	
	private int type; //0 普通视频 ，1头像视频 ，2动态视频
	private int comment_count;
	
	private int praise_count;
	
	
	private int share_count;
	
	private int store_count;
	
	
	private int status; //视频审核状态
	private int secret_level; // 0 推荐，1 私密
	private String channel;  
	
	@Transient
	private BaseUser user;
	
	
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

	
	 
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
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
		if(create_time!=null) {
			create_time_v2=create_time.getTime()/1000;
		}
	}
 

 

	 

	public long getId() {
		return id;
	}
	public void setId(long id) {
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

	public long getCreate_time_v2() {
		return create_time_v2;
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
	public BaseUser getUser() {
		return user;
	}
	public void setUser(BaseUser user) {
		this.user = user;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getSecret_level() {
		return secret_level;
	}
	public void setSecret_level(int secret_level) {
		this.secret_level = secret_level;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}

}
