package com.zhan.app.nearby.bean;

import java.io.Serializable;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Table(name = "t_like_dynamic")
public class UserDynamicRelationShip implements Serializable {
	@JsonIgnore
	private long user_id;
	private long dynamic_id;
	private int relationship;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getDynamic_id() {
		return dynamic_id;
	}

	public void setDynamic_id(long dynamic_id) {
		this.dynamic_id = dynamic_id;
	}

	public int getRelationship() {
		return relationship;
	}

	public void setRelationship(int relationship) {
		this.relationship = relationship;
	}

 

}
