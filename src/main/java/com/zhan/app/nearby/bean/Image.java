package com.zhan.app.nearby.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class Image {
	public long id; // 主键

	@JSONField(serialize = false)
	private String local_image_name;
	
	private String thumb; // 无关数据库，主要json展示 缩略图
	private String origin; // 无关数据库，主要json展示 原始图

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
