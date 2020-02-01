package com.zhan.app.nearby.bean;

import java.util.Date;

import com.zhan.app.nearby.annotation.ColumnType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
@ApiModel(description= "短视频对象")
public class Video {
	@ApiModelProperty(value = "客户端生成的文件名称")
	private String id;
	private long uid;
	
	private Date create_time;
	@ColumnType
	private long create_time_v2;
	@ApiModelProperty(value = "视频长度")
	private int duration;
	@ColumnType
	@ApiModelProperty(value = "视频缩略图路径")
	private String thumb;
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}
	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
		if (create_time != null) {
			this.create_time_v2 = create_time.getTime() / 1000;
		}
	}

	public long getCreate_time_v2() {
		return create_time_v2;
	}

	public void setCreate_time_v2(long create_time_v2) {
		this.create_time_v2 = create_time_v2;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	 
}
