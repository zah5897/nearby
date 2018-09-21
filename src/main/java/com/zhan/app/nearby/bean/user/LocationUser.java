package com.zhan.app.nearby.bean.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.City;

public class LocationUser extends SimpleUser {

	public LocationUser(long user_id) {
		super(user_id);
	}

	public LocationUser() {
	}

	private String lat;
	private String lng;

	@JsonIgnore
	private int city_id;
	private City city;
	@JsonIgnore
	private int birth_city_id;
	private City birth_city;

	// @JsonIgnore
	@JsonIgnore
	private String disc;

	@ColumnType
	@JsonProperty("is_vip")
	private boolean isVip;
	public String getLat() {
		return lat;
	}

	@Override
	public String getToken() {
		// TODO Auto-generated method stub
		return super.getToken();
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

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public int getBirth_city_id() {
		return birth_city_id;
	}

	public void setBirth_city_id(int birth_city_id) {
		this.birth_city_id = birth_city_id;
	}

	public City getBirth_city() {
		return birth_city;
	}

	public String getDisc() {
		return disc;
	}

	public void setDisc(String disc) {
		this.disc = disc;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
		if (city != null) {
			city.setChildren(null);
			setCity_id(city.getId());
		}
	}

	public void setBirth_city(City birth_city) {
		this.birth_city = birth_city;
		if (birth_city != null) {
			birth_city.setChildren(null);
			setBirth_city_id(birth_city.getId());
		}
	}

	 @JsonIgnore
	public boolean isVip() {
		return isVip;
	}
	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

	/**
	 * 隐藏系统信息
	 */
	public void hideSysInfo() {
		// 获取别人的信息时不能显示别人的登陆token
		setToken(null);
		// 获取信息的时候不能暴露手机号码
		setMobile(null);
		set_ua(null);
		// 隐藏位置信息
		setLat(null);
		setLng(null);
	}
}
