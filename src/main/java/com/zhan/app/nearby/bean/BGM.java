package com.zhan.app.nearby.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BGM {
	private String url;
	private String name;
	private String author;
	@JsonIgnore
	private int state;
	@JsonIgnore
	private Date create_time;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

}
