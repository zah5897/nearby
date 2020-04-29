package com.zhan.app.nearby.bean.user;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhan.app.nearby.bean.Avatar;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.TextUtils;

@SuppressWarnings("serial")
@Table(name = "t_user")
public class BaseUser implements Serializable {

	public BaseUser(long user_id) {
		this.user_id = user_id;
	}

	public BaseUser() {
	}

	private int isFace = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long user_id;
	@JsonIgnore
	private String mobile;

	@JsonIgnore
	private String password;
	private String nick_name;
	private String sex; // 0 女，1 男，2 未知

	@Transient // 忽略保存
	private List<Avatar> avatars;

	private String avatar;
	@Transient // 忽略保存
	private String origin_avatar;

	// 区分游客和正式用户
	private short type = (short) UserType.OFFIEC.ordinal(); // 默认为正式用户

	@JsonIgnore
	private Date create_time;

	private String token;
	@JsonIgnore
	private String _ua;
	@JsonIgnore
	private String aid;

	@JsonFormat(pattern = "yyyy-MM-dd", locale = "zh", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;

	@JsonIgnore
	private int account_state;

	private String contact;
	@JsonIgnore
	private String ip;

	@Transient // 忽略保存
	private int _from = 0; // 1 ios ，2 android

	@Transient // 忽略保存
	private int has_followed;

	@Transient // 忽略保存
	private int fans_count;

	private int meili;
	@Transient // 忽略保存
	@JsonIgnore
	private int isvip;

	@Transient // 忽略保存
	private boolean is_vip;

	
	
	private String lat;
	private String lng;
	
	@JsonIgnore
	private int city_id;
	@Transient // 忽略保存
	private City city;
	@JsonIgnore
	private int birth_city_id;
	@Transient // 忽略保存
	private City birth_city;
	
	
	@Transient // 忽略保存
	private int my_follow_count;
	@Transient // 忽略保存
	private String age;

	private String channel;
	
	
	private int video_cert_status;//是否短视频认证过 -1 认证中，0未认证 ，1已认证
	
	@Transient // 
	private int online_status; //用户在线状态   -1 未知， 0离线，1在线
	
	private int sys_status;//0 正常状态，1黑名单状态
	
	private int found_status;//0 正常状态，1 首页发现推荐
	
	private int bottle_meet_status;//0 正常状态，1 邂逅瓶子推荐
	 
	public int getSys_status() {
		return sys_status;
	}

	public void setSys_status(int sys_status) {
		this.sys_status = sys_status;
	}

	@JsonIgnore
	private String openid;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
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
	}

	public int getMeili() {
		return meili;
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

	public int getIsvip() {
		return isvip;
	}

	public void setIsvip(int isvip) {
		this.isvip = isvip;
		this.is_vip = isvip == 1;
	}

	public boolean isIs_vip() {
		return is_vip;
	}

	public void setIs_vip(boolean is_vip) {
		this.is_vip = is_vip;
	}

	public void setMeili(int meili) {
		this.meili = meili;
	}



	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getAge() {
		return age;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
		this.age=DateTimeUtil.getAge(birthday);
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	@JsonIgnore
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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getOrigin_avatar() {
		return origin_avatar;
	}

	public void setOrigin_avatar(String origin_avatar) {
		this.origin_avatar = origin_avatar;
	}

	public String get_ua() {
		return _ua;
	}

	public void set_ua(String _ua) {
		this._ua = _ua;

		if (!TextUtils.isEmpty(_ua)) {
			if (_ua.startsWith("a")) {
				this.set_from(1);
			} else {
				this.set_from(2);
			}
		}
	}

	public int getAccount_state() {
		return account_state;
	}

	public void setAccount_state(int account_state) {
		this.account_state = account_state;
	}

	public List<Avatar> getAvatars() {
		return avatars;
	}

	public void setAvatars(List<Avatar> avatars) {
		this.avatars = avatars;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getIsFace() {
		return isFace;
	}

	public void setIsFace(int isFace) {
		this.isFace = isFace;
	}

	public int get_from() {
		return _from;
	}

	public void set_from(int _from) {
		this._from = _from;
	}

	public int getHas_followed() {
		return has_followed;
	}

	public void setHas_followed(int has_followed) {
		this.has_followed = has_followed;
	}

	public int getFans_count() {
		return fans_count;
	}

	public void setFans_count(int fans_count) {
		this.fans_count = fans_count;
	}

	public int getMy_follow_count() {
		return my_follow_count;
	}

	public void setMy_follow_count(int my_follow_count) {
		this.my_follow_count = my_follow_count;
	}

	public int getOnline_status() {
		return online_status;
	}

	public void setOnline_status(int online_status) {
		this.online_status = online_status;
	}

	public int getVideo_cert_status() {
		return video_cert_status;
	}

	public void setVideo_cert_status(int video_cert_status) {
		this.video_cert_status = video_cert_status;
	}

	public int getFound_status() {
		return found_status;
	}

	public void setFound_status(int found_status) {
		this.found_status = found_status;
	}

	public int getBottle_meet_status() {
		return bottle_meet_status;
	}

	public void setBottle_meet_status(int bottle_meet_status) {
		this.bottle_meet_status = bottle_meet_status;
	}
 
 

}
