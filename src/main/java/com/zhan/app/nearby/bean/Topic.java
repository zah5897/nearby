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
	@ColumnType
	private String icon_origin;
	private String big_icon;
	@ColumnType
	private String big_icon_origin;
	private String description;
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private Date create_time;
    @ColumnType
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

	public String getBig_icon() {
		return big_icon;
	}

	public void setBig_icon(String big_icon) {
		this.big_icon = big_icon;
	}

	public String getIcon_origin() {
		return icon_origin;
	}

	public void setIcon_origin(String icon_origin) {
		this.icon_origin = icon_origin;
	}

	public String getBig_icon_origin() {
		return big_icon_origin;
	}

	public void setBig_icon_origin(String big_icon_origin) {
		this.big_icon_origin = big_icon_origin;
	}
	
	
}
