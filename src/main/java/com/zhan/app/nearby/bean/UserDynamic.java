package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.bean.user.BaseUser;
@SuppressWarnings("serial")
@Table(name = "t_user_dynamic")
public class UserDynamic implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id; // 主键

	@JsonIgnore
	private long user_id; // 外键，关联user id字段，不json 序列化

	private String description; // 描述
	private String lat;
	private String lng;
	private String addr;
	private String street;
	private String city;
	private String region;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	private Date create_time;

	@Transient
	private long create_time_v2;
	
	@JsonIgnore
	private String local_image_name;

	@Transient
	private String thumb; // 无关数据库，主要json展示 缩略图
	@Transient
	private String origin; // 无关数据库，主要json展示 原始图

	private int praise_count;
	private String can_comment = "1";

	private int browser_count;
	@Transient // 客户端组合数据
	private int like_state;

	@JsonIgnore
	private int province_id;
	// @JsonIgnore
	@JsonIgnore
	private int city_id;
	// @JsonIgnore
	@JsonIgnore
	private int district_id;
	@Transient
	private BaseUser user;

	private long topic_id;
	private String ip;

	private int state;
	
	
	private int _from=0;
	
	private int comment_count;
	private int flower_count;
	
	
	private int found_status;
	private int manager_flag;

	
	
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

	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
		if(create_time!=null) {
			this.create_time_v2=create_time.getTime()/1000;
		}
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

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
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

	public long getCreate_time_v2() {
		return create_time_v2;
	}

	public int get_from() {
		return _from;
	}

	public void set_from(int _from) {
		this._from = _from;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public int getFlower_count() {
		return flower_count;
	}

	public void setFlower_count(int flower_count) {
		this.flower_count = flower_count;
	}

	public int getFound_status() {
		return found_status;
	}

	public void setFound_status(int found_status) {
		this.found_status = found_status;
	}

	public int getManager_flag() {
		return manager_flag;
	}

	public void setManager_flag(int manager_flag) {
		this.manager_flag = manager_flag;
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
