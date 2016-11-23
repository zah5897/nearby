package com.zhan.app.nearby.bean;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
public class City {
	private int id;
	private String name;
//	@JsonIgnore
	@JSONField(serialize = false)
	private int parent_id;
	private List<City> children;
	private int type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public List<City> getChildren() {
		return children;
	}

	public void setChildren(List<City> children) {
		this.children = children;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
