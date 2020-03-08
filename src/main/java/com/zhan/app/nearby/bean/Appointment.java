package com.zhan.app.nearby.bean;

import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.bean.user.BaseUser;

/**
 * 约会对象类
 * 
 * @author zah
 *
 */
@Table(name = "t_appointment")
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@JsonIgnore
	private long uid;
	private String description;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date create_time; // 该约会创建实际那
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	private Date appointment_time; // 该约会创建实际那
	private int time_stage;
	
	private int status; // 该条约会状态
	
	@JsonIgnore
	private int theme_id; // 主题
	
	private String street;
	
	@JsonIgnore
	private int city_id;
	@JsonIgnore
	private String image;
	@JsonIgnore
	private String channel;
	private String lat,lng;

	@Transient
	private BaseUser user; //发布者
	@Transient
	private City city; //所在城市
	
	@Transient
	private AppointmentTheme theme; //所在城市

	@Transient
	private List<String>  images;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

 
 

	public int getTime_stage() {
		return time_stage;
	}

	public void setTime_stage(int time_stage) {
		this.time_stage = time_stage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

 

	public int getTheme_id() {
		return theme_id;
	}

	public void setTheme_id(int theme_id) {
		this.theme_id = theme_id;
	}

	public AppointmentTheme getTheme() {
		return theme;
	}

	public String getChannel() {
		return channel;
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

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setTheme(AppointmentTheme theme) {
		this.theme = theme;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	 

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}
 

	public Date getAppointment_time() {
		return appointment_time;
	}

	public void setAppointment_time(Date appointment_time) {
		this.appointment_time = appointment_time;
	}

}
