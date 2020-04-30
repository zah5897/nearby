package com.zhan.app.nearby.bean;

import java.io.Serializable;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class SimpleDynamic implements Serializable {
	public long id; // 主键
	@JsonIgnore
	private String local_image_name;

	private String thumb; // 无关数据库，主要json展示 缩略图
	private String origin;
	 
	
	//兼容短视频
	private int type; //0为图片动态 ，1为短视频
	@Transient
	private String video_url; //短视频url
	@JsonIgnore
	private String video_file_short_name;
	private float duration;
	private int secret_level; // 0 推荐，1 私密
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLocal_image_name() {
		return local_image_name;
	}
	public void setLocal_image_name(String local_image_name) {
		this.local_image_name = local_image_name;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getVideo_url() {
		return video_url;
	}
	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
	public String getVideo_file_short_name() {
		return video_file_short_name;
	}
	public void setVideo_file_short_name(String video_file_short_name) {
		this.video_file_short_name = video_file_short_name;
	}
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}
	public int getSecret_level() {
		return secret_level;
	}
	public void setSecret_level(int secret_level) {
		this.secret_level = secret_level;
	}
	
	
}