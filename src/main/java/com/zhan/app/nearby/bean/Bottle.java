package com.zhan.app.nearby.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.user.BaseUser;

public class Bottle implements Serializable{
	@ColumnType
	private long id;
	private String content;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", locale = "zh", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date create_time;
	@ColumnType
	private long create_time_v2;
	private int type;

	@JsonIgnore
	private long user_id;
	@ColumnType
	private BaseUser sender;
	@ColumnType
	private List<BaseUser> scan_user_list;
	@ColumnType
	private int view_nums;

	private int state = 0;
	
	
	//我画你猜 答案
	private String answer;
	//我画你猜 奖励
	private int reward;
	
	private int answer_state = 0;
	
	@JsonIgnore
	private int red_package_count; //红包个数
	@JsonIgnore
	private int red_package_coin_total; //红包金币总数
	@JsonIgnore
	private int red_package_coin_rest; //红包剩余金币数量
	
	//来自
	private int _from=0;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
		this.create_time_v2=create_time.getTime()/1000;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public BaseUser getSender() {
		return sender;
	}

	public void setSender(BaseUser sender) {
		this.sender = sender;
	}

	public List<BaseUser> getScan_user_list() {
		return scan_user_list;
	}

	public void setScan_user_list(List<BaseUser> scan_user_list) {
		this.scan_user_list = scan_user_list;
	}

	public int getView_nums() {
		return view_nums;
	}

	public void setView_nums(int view_nums) {
		this.view_nums = view_nums;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
    public long getCreate_time_v2() {
		return create_time_v2;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	public int getAnswer_state() {
		return answer_state;
	}

	public void setAnswer_state(int answer_state) {
		this.answer_state = answer_state;
	}

	public int get_from() {
		return _from;
	}

	public void set_from(int _from) {
		this._from = _from;
	}

	public int getRed_package_count() {
		return red_package_count;
	}

	public void setRed_package_count(int red_package_count) {
		this.red_package_count = red_package_count;
	}

	public int getRed_package_coin_total() {
		return red_package_coin_total;
	}

	public void setRed_package_coin_total(int red_package_coin_total) {
		this.red_package_coin_total = red_package_coin_total;
	}

	public int getRed_package_coin_rest() {
		return red_package_coin_rest;
	}

	public void setRed_package_coin_rest(int red_package_coin_rest) {
		this.red_package_coin_rest = red_package_coin_rest;
	}
	
	 
}
