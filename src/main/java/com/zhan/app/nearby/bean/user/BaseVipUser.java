package com.zhan.app.nearby.bean.user;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhan.app.nearby.bean.City;

@SuppressWarnings("serial")
@Table(name = "t_user")
public class BaseVipUser extends BaseUser {

	@Transient
	@JsonProperty("is_vip")
	private boolean isVip;
	
	
	@JsonIgnore
	private int city_id;
	private City city;
	@JsonIgnore
	private int birth_city_id;
	private City birth_city;
	
	@JsonIgnore
	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
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

	public void setBirth_city(City birth_city) {
		this.birth_city = birth_city;
	}

	
}
