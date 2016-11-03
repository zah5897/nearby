package com.zhan.app.nearby.bean;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zhan.app.nearby.annotation.ColumnType;
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UserDynamicRelationShip implements Serializable{
	/**
	 * 
	 */
	@ColumnType
	private static final long serialVersionUID = 1L;
	@JsonIgnore
	private long user_id;  
	private long dynamic_id;  
	private int relation_ship;
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
	public int getRelation_ship() {
		return relation_ship;
	}
	public void setRelation_ship(int relation_ship) {
		this.relation_ship = relation_ship;
	}
	 
}
