package com.zhan.app.nearby.bean;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;

public class UserDynamic {
	@ColumnType
	private long id; // 主键
	@JsonIgnore
	private long user_id; // 外键，关联user id字段，不json 序列化

	private String desciption; // 描述
	@JsonIgnore
	private String lat;
	@JsonIgnore
	private String lng;
	private String addr;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JSONField(format = "yyyy-MM-dd")
	private Date create_time;

	@JsonIgnore
	private String local_image_name;

	@ColumnType
	private String thumb; // 无关数据库，主要json展示 缩略图
	@ColumnType
	private String origin; // 无关数据库，主要json展示 原始图

	private int praise_count;
	@JsonIgnore
	private int can_common = 1;

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

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
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

	public int getPraise_count() {
		return praise_count;
	}

	public void setPraise_count(int praise_count) {
		this.praise_count = praise_count;
	}

	public int getCan_common() {
		return can_common;
	}

	public void setCan_common(int can_common) {
		this.can_common = can_common;
	}

	public String getLocal_image_name() {
		return local_image_name;
	}

	public void setLocal_image_name(String local_image_name) {
		this.local_image_name = local_image_name;
	}

}
