package com.zhan.app.nearby.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class Image implements Serializable {
	public long id; // 主键
	@JsonIgnore
	private String local_image_name;

	private String thumb; // 无关数据库，主要json展示 缩略图
	private String origin;
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
	
	
}