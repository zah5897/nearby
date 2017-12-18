package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;

public class UserDynamic implements Serializable {
	/**
	 * 
	 */
	@ColumnType
	private static final long serialVersionUID = 1L;

	@ColumnType
	private long id; // 主键

	// @JsonIgnore
	@JSONField(serialize = false)
	private long user_id; // 外键，关联user id字段，不json 序列化

	private String description; // 描述
	private String lat;
	private String lng;
	private String addr;
	private String street;
	private String city;
	private String region;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd")
	private Date create_time;

	@JSONField(serialize = false)
	private String local_image_name;

	@ColumnType
	private String thumb; // 无关数据库，主要json展示 缩略图
	@ColumnType
	private String origin; // 无关数据库，主要json展示 原始图

	private int praise_count;
	private String can_comment = "1";

	private int browser_count;
	@ColumnType // 客户端组合数据
	private int like_state;

	@JSONField(serialize = false)
	private int province_id;
	// @JsonIgnore
	@JSONField(serialize = false)
	private int city_id;
	// @JsonIgnore
	@JSONField(serialize = false)
	private int district_id;
	@ColumnType
	private User user;
	
	private long topic_id;
	private String ip;

	
	private int state;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	@JSONField(format = "yyyy-MM-dd")
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

	public String getCan_comment() {
		return can_comment;
	}

	public void setCan_comment(String can_comment) {
		this.can_comment = can_comment;
	}

	public String getLocal_image_name() {
		return local_image_name;
	}

	public void setLocal_image_name(String local_image_name) {
		this.local_image_name = local_image_name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getBrowser_count() {
		return browser_count;
	}

	public void setBrowser_count(int browser_count) {
		this.browser_count = browser_count;
	}

	public int getLike_state() {
		return like_state;
	}

	public void setLike_state(int like_state) {
		this.like_state = like_state;
	}

	public int getProvince_id() {
		return province_id;
	}

	public void setProvince_id(int province_id) {
		this.province_id = province_id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public int getDistrict_id() {
		return district_id;
	}

	public void setDistrict_id(int district_id) {
		this.district_id = district_id;
	}

	public long getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(long topic_id) {
		this.topic_id = topic_id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
