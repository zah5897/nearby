package com.zhan.app.nearby.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.util.DateTimeUtil;

public class User {
	public User() {
	}

	public User(long user_id) {
		this.user_id = user_id;
	}

	@ColumnType
	private long user_id;
	private String mobile;

	@JSONField(serialize = false)
	private String password;
	private String name;
	private String nick_name;
	@ColumnType // 不用插入数据库字段
	private String age;
	private String sex; // 0 男 1 女
	private String avatar;
	private String signature;
	private String lat;
	private String lng;
	@JSONField(name = "_ua", serialize = false)
	// @JsonIgnore
	private String _ua;

	@ColumnType // 忽略保存
	private String origin_avatar;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;
	private String token;

	// 以下属性为组合属性

	@ColumnType // 忽略保存
	private List<UserDynamic> images;

	// 数据库中以逗号分割，json不序列号
	// @JsonIgnore
	@JSONField(serialize = false)
	private String job_ids;
	@ColumnType // 忽略保存
	private List<Tag> jobs;

	private String weight;// 体重
	private String height;// 身高

	// @JsonIgnore
	@JSONField(serialize = false)
	private String my_tag_ids;
	@ColumnType // 忽略保存

	// @JsonIgnore
	@JSONField(serialize = false)
	private List<Tag> my_tags;
	// @JsonIgnore
	@JSONField(serialize = false)
	private String interest_ids;
	@ColumnType // 忽略保存
	// @JsonIgnore
	@JSONField(serialize = false)
	private List<Tag> interest;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String animal_ids;
	@ColumnType // 忽略保存
	// @JsonIgnore
	@JSONField(serialize = false)
	private List<Tag> favourite_animal;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String music_ids;
	@ColumnType // 忽略保存
	// @JsonIgnore
	@JSONField(serialize = false)
	private List<Tag> favourite_music;
	// @JsonIgnore
	@JSONField(serialize = false)
	private String weekday_todo_ids;
	@ColumnType // 忽略保存

	// @JsonIgnore
	@JSONField(serialize = false)
	private List<Tag> weekday_todo;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String footstep_ids;
	@ColumnType // 忽略保存

	// @JsonIgnore
	@JSONField(serialize = false)
	private List<Tag> footsteps;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String want_to_where;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String disc;

	// 区分游客和正式用户
	private short type = (short) UserType.OFFIEC.ordinal(); // 默认为正式用户

	// 区分国外用户
	// @JsonIgnore
	@JSONField(serialize = false)
	private String zh_cn;

	// 设备token
	// @JsonIgnore
	@JSONField(serialize = false)
	private String device_token;
	@JSONField(serialize=false)
	private int city_id;
	private City city;
	@JSONField(serialize=false)
	private int birth_city_id;
	private City birth_city;

	@JSONField(serialize=false)
	private String app_id;

	
	@JSONField(serialize=false)
	private Date last_login_time;
	
	
	
	
	
	
	
	public Date getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Date last_login_time) {
		this.last_login_time = last_login_time;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getAge() {
		return age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String get_ua() {
		return _ua;
	}

	public void set_ua(String _ua) {
		this._ua = _ua;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOrigin_avatar() {
		return origin_avatar;
	}

	public void setOrigin_avatar(String origin_avatar) {
		this.origin_avatar = origin_avatar;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public List<UserDynamic> getImages() {
		return images;
	}

	public void setImages(List<UserDynamic> images) {
		this.images = images;
	}

	public String getJob_ids() {
		return job_ids;
	}

	public void setJob_ids(String job_ids) {
		this.job_ids = job_ids;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public List<Tag> getJobs() {
		return jobs;
	}

	public void setJobs(List<Tag> jobs) {
		this.jobs = jobs;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getMy_tag_ids() {
		return my_tag_ids;
	}

	public void setMy_tag_ids(String my_tag_ids) {
		this.my_tag_ids = my_tag_ids;
	}

	public List<Tag> getMy_tags() {
		return my_tags;
	}

	public void setMy_tags(List<Tag> my_tags) {
		this.my_tags = my_tags;
	}

	public String getInterest_ids() {
		return interest_ids;
	}

	public void setInterest_ids(String interest_ids) {
		this.interest_ids = interest_ids;
	}

	public List<Tag> getInterest() {
		return interest;
	}

	public void setInterest(List<Tag> interest) {
		this.interest = interest;
	}

	public String getAnimal_ids() {
		return animal_ids;
	}

	public void setAnimal_ids(String animal_ids) {
		this.animal_ids = animal_ids;
	}

	public List<Tag> getFavourite_animal() {
		return favourite_animal;
	}

	public void setFavourite_animal(List<Tag> favourite_animal) {
		this.favourite_animal = favourite_animal;
	}

	public String getMusic_ids() {
		return music_ids;
	}

	public void setMusic_ids(String music_ids) {
		this.music_ids = music_ids;
	}

	public List<Tag> getFavourite_music() {
		return favourite_music;
	}

	public void setFavourite_music(List<Tag> favourite_music) {
		this.favourite_music = favourite_music;
	}

	public String getWeekday_todo_ids() {
		return weekday_todo_ids;
	}

	public void setWeekday_todo_ids(String weekday_todo_ids) {
		this.weekday_todo_ids = weekday_todo_ids;
	}

	public List<Tag> getWeekday_todo() {
		return weekday_todo;
	}

	public void setWeekday_todo(List<Tag> weekday_todo) {
		this.weekday_todo = weekday_todo;
	}

	public String getFootstep_ids() {
		return footstep_ids;
	}

	public void setFootstep_ids(String footstep_ids) {
		this.footstep_ids = footstep_ids;
	}

	public List<Tag> getFootsteps() {
		return footsteps;
	}

	public void setFootsteps(List<Tag> footsteps) {
		this.footsteps = footsteps;
	}

	public String getWant_to_where() {
		return want_to_where;
	}

	public void setWant_to_where(String want_to_where) {
		this.want_to_where = want_to_where;
	}

	public String getDisc() {
		return disc;
	}

	public void setDisc(String disc) {
		this.disc = disc;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getZh_cn() {
		return zh_cn;
	}

	public void setZh_cn(String zh_cn) {
		this.zh_cn = zh_cn;
	}

	public String getDevice_token() {
		return device_token;
	}

	public void setDevice_token(String device_token) {
		this.device_token = device_token;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
		if (city!=null) {
			city.setChildren(null);
		}
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public int getBirth_city_id() {
		return birth_city_id;
	}

	public void setBirth_city_id(int birth_city_id) {
		this.birth_city_id = birth_city_id;
	}

	public City getBirth_city() {
		return birth_city;
	}

	public void setBirth_city(City birth_city) {
		this.birth_city = birth_city;
		if (birth_city!=null) {
			birth_city.setChildren(null);
		}
	}

	/**
	 * 隐藏系统信息
	 */
	public void hideSysInfo() {
		// 获取别人的信息时不能显示别人的登陆token
		setToken(null);
		// 获取信息的时候不能暴露手机号码
		setMobile(null);
		set_ua(null);
		// 隐藏位置信息
		setLat(null);
		setLng(null);
	}

	// @JsonIgnore
	@JSONField(serialize = false)
	public Object getBasicUserInfoMap() {
		Map<String, Object> basicInfo = new HashMap<String, Object>();
		basicInfo.put("user_id", user_id);
		basicInfo.put("nick_name", nick_name != null ? nick_name : new String());
		basicInfo.put("age", DateTimeUtil.getAge(getBirthday()));
		basicInfo.put("sex", sex != null ? sex : new String());
		basicInfo.put("avatar", avatar != null ? avatar : new String());
		basicInfo.put("origin_avatar", origin_avatar != null ? origin_avatar : new String());
		basicInfo.put("signature", signature != null ? signature : new String());
		basicInfo.put("birthday", DateTimeUtil.parseBirthday(birthday));
		basicInfo.put("weight", weight != null ? weight : new String());
		basicInfo.put("height", height != null ? height : new String());
		basicInfo.put("images", images != null ? images : new ArrayList<UserDynamic>());
		basicInfo.put("jobs", jobs != null ? jobs : new ArrayList<Tag>());
		
		if(city!=null){
			basicInfo.put("city", city);
		}
		if(birth_city!=null){
			basicInfo.put("birth_city", birth_city);
		}
		return basicInfo;
	}

	/**
	 * 分类显示用户详情信息
	 */

}
