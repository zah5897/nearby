package com.zhan.app.nearby.bean;

import java.io.Serializable;

import com.zhan.app.nearby.annotation.ColumnType;

public class Vip implements Serializable{
	@ColumnType
	private long id;
	private String name;
	private int term_mount;
	private int amount;
	private int old_amount;
	private String aid;
	private String description;

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

	public int getTerm_mount() {
		return term_mount;
	}

	public void setTerm_mount(int term_mount) {
		this.term_mount = term_mount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getOld_amount() {
		return old_amount;
	}

	public void setOld_amount(int old_amount) {
		this.old_amount = old_amount;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
