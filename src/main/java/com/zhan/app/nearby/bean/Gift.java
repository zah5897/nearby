package com.zhan.app.nearby.bean;

import com.zhan.app.nearby.annotation.ColumnType;

public class Gift {
    @ColumnType
	private long id;
	private String name;
	private int price;
	private int old_price;
	private String image_url;
	private String description;
	private String remark;

	@ColumnType
	private String origin_image_url;
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
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public float getPrice() {
		return price;
	}
	 
	public int getOld_price() {
		return old_price;
	}
	public void setOld_price(int old_price) {
		this.old_price = old_price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getOrigin_image_url() {
		return origin_image_url;
	}
	public void setOrigin_image_url(String origin_image_url) {
		this.origin_image_url = origin_image_url;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}
