package com.zhan.app.nearby.bean;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;

public class Topic {
	@ColumnType
	private long id;
	private String name;
	private String icon;
	private Date create_time;
    
	@JsonIgnore
	private List<UserDynamic> dynamics;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public List<UserDynamic> getDynamics() {
		return dynamics;
	}

	public void setDynamics(List<UserDynamic> dynamics) {
		this.dynamics = dynamics;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
}
