package com.zhan.app.nearby.bean.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;
import com.zhan.app.nearby.annotation.ColumnType;
import com.zhan.app.nearby.bean.Tag;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.util.DateTimeUtil;

public class DetailUser extends LocationUser {
	public DetailUser(long user_id) {
		super(user_id);
	}

	public DetailUser() {
	}

	@ColumnType // 忽略保存
	private List<UserDynamic> images;
	// 数据库中以逗号分割，json不序列号
	// @JsonIgnore
	@JSONField(serialize = false)
	private String job_ids;
	@ColumnType // 忽略保存
	private List<Tag> jobs;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String my_tag_ids;

	@ColumnType // 忽略保存
	public List<UserDynamic> getImages() {
		return images;
	}

	public void setImages(List<UserDynamic> images) {
		this.images = images;
	}

	// @JsonIgnore
	private List<Tag> my_tags;
	// @JsonIgnore
	@JSONField(serialize = false)
	private String interest_ids;
	@ColumnType // 忽略保存
	// @JsonIgnore
	private List<Tag> interest;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String animal_ids;
	@ColumnType // 忽略保存
	// @JsonIgnore
	private List<Tag> favourite_animal;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String music_ids;
	@ColumnType // 忽略保存
	// @JsonIgnore
	private List<Tag> favourite_music;
	// @JsonIgnore
	@JSONField(serialize = false)
	private String weekday_todo_ids;
	@ColumnType // 忽略保存

	// @JsonIgnore
	private List<Tag> weekday_todo;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String footstep_ids;
	@ColumnType // 忽略保存

	// @JsonIgnore
	private List<Tag> footsteps;

	// @JsonIgnore
	@JSONField(serialize = false)
	private String want_to_where;

	public String getJob_ids() {
		return job_ids;
	}

	public void setJob_ids(String job_ids) {
		this.job_ids = job_ids;
	}

	public List<Tag> getJobs() {
		return jobs;
	}

	public void setJobs(List<Tag> jobs) {
		this.jobs = jobs;
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


	// @JsonIgnore
	@JSONField(serialize = false)
	public Object getBasicUserInfoMap() {
		Map<String, Object> basicInfo = new HashMap<String, Object>();
		basicInfo.put("user_id", getUser_id());
		basicInfo.put("nick_name", getNick_name() != null ? getNick_name() : new String());
		basicInfo.put("age", DateTimeUtil.getAge(getBirthday()));
		basicInfo.put("sex", getSex() != null ? getSex() : new String());
		basicInfo.put("avatar", getAvatar() != null ? getAvatar() : new String());
		basicInfo.put("origin_avatar", getOrigin_avatar() != null ? getOrigin_avatar() : new String());
		basicInfo.put("signature", getSignature() != null ? getSignature() : new String());
		basicInfo.put("birthday", DateTimeUtil.parseBirthday(getBirthday()));
		basicInfo.put("weight", getWeight() != null ? getWeight() : new String());
		basicInfo.put("height", getHeight() != null ? getHeight() : new String());
		basicInfo.put("images", images != null ? images : new ArrayList<UserDynamic>());
		basicInfo.put("jobs", jobs != null ? jobs : new ArrayList<Tag>());

		if (getCity() != null) {
			basicInfo.put("city", getCity());
		}
		if (getBirth_city() != null) {
			basicInfo.put("birth_city", getBirth_city());
		}
		return basicInfo;
	}

	/**
	 * 分类显示用户详情信息
	 */

}
