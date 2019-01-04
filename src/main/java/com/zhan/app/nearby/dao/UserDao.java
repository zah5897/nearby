package com.zhan.app.nearby.dao;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Avatar;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.bean.mapper.SimpkleUserMapper;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.BaseVipUser;
import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.bean.user.LocationUser;
import com.zhan.app.nearby.comm.AvatarIMGStatus;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.SQLUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository
public class UserDao extends BaseDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

	// public int update(String nickName, String info, short sex, String avater,
	// long id) {
	// return jdbcTemplate.update("update t_user set
	// nick_name=?,info=?,sex=?,avatar=? where user_id=?",
	// new Object[] { nickName, info, sex, avater, id });
	// }

	// public List<?> getList() {
	// List<User> list = jdbcTemplate.query("select *from t_user user", new
	// SimpkleUserMapper());
	// return list;
	// }
	/**
	 * 根据类型获取用户
	 * 
	 * @param pageSize
	 * @param currentPage
	 * @param type
	 * @return
	 */
	public List<BaseUser> getUsers(int pageSize, int currentPage, int type, String keyword, long user_id) {

		if (user_id > 0) {
			return jdbcTemplate.query(
					"select *from t_user   where user_id=? and  type=? order by user_id desc limit ?,?",
					new Object[] { user_id, type, (currentPage - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}

		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.query("select *from t_user  where type=? order by user_id desc limit ?,?",
					new Object[] { type, (currentPage - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		} else {
			return jdbcTemplate.query(
					"select *from t_user  where type=? and nick_name like ? order by user_id desc limit ?,?",
					new Object[] { type, "%" + keyword + "%", (currentPage - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}
	}

	/**
	 * 获取所有用户
	 * 
	 * @param pageSize
	 * @param currentPage
	 * @return
	 */
	public List<BaseUser> getUsers(int pageSize, int currentPage, String keyword, long user_id) {

		if (user_id > 0) {
			return jdbcTemplate.query("select *from t_user where user_id=? order by user_id desc limit ?,?",
					new Object[] { user_id, (currentPage - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}

		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.query("select *from t_user order by user_id desc limit ?,?",
					new Object[] { (currentPage - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		} else {
			return jdbcTemplate.query("select *from t_user where nick_name like ? order by user_id desc limit ?,?",
					new Object[] { "%" + keyword + "%", (currentPage - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}
	}

	public BaseUser findBaseUserByMobile(String mobile) {
		List<BaseUser> list = jdbcTemplate.query("select  *   from t_user  where mobile=?", new Object[] { mobile },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public int isUidExist(long user_id) {
		return jdbcTemplate.queryForObject("select count(*) from t_user where user_id=" + user_id, Integer.class);
	}

	public LocationUser findLocationUserByMobile(String mobile) {
		List<LocationUser> list = jdbcTemplate.query(
				"select user.* ,city.name as city_name from t_user user left join t_sys_city city on user.city_id=city.id where user.mobile=?",
				new Object[] { mobile }, new BeanPropertyRowMapper<LocationUser>(LocationUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public LocationUser findLocationUserById(long user_id) {
		List<LocationUser> list = jdbcTemplate.query(
				"select user.* ,city.name as city_name from t_user user left join t_sys_city city on user.city_id=city.id where user.user_id=?",
				new Object[] { user_id }, new BeanPropertyRowMapper<LocationUser>(LocationUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public BaseUser findUserByDeviceId(String deviceId) {
		List<BaseUser> list = jdbcTemplate.query("select *from t_user user where user.mobile=? and type=?",
				new Object[] { deviceId, UserType.VISITOR.ordinal() }, new SimpkleUserMapper());
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public LocationUser findLocationUserByDeviceId(String deviceId) {
		List<LocationUser> list = jdbcTemplate.query("select *from t_user user where user.mobile=? and type=?",
				new Object[] { deviceId, UserType.VISITOR.ordinal() },
				new BeanPropertyRowMapper<LocationUser>(LocationUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public Serializable insert(BaseUser user) {
		user.setCreate_time(new Date());
		return saveObj(jdbcTemplate, "t_user", user);
	}

	public void delete(long id) {
		jdbcTemplate.update("delete from t_user where user_id=?", new Object[] { id });
	}

	public int getUserCountByMobile(String mobile) {
		int count = jdbcTemplate.queryForObject("select count(*) from t_user user where user.mobile=?",
				new String[] { mobile }, Integer.class);
		return count;
	}

	public int updateToken(long userId, String token, Date last_login_time) {
		return jdbcTemplate.update("update t_user set token=?,last_login_time=? where user_id=?",
				new Object[] { token, last_login_time, userId });
	}

	public int updatePassword(String mobile, String password) {
		return jdbcTemplate.update("update t_user set password=? where mobile=?", new Object[] { password, mobile });
	}

	public int updateAvatar(long userId, String newAcaar) {
		return jdbcTemplate.update("update t_user set avatar=? where user_id=?", new Object[] { newAcaar, userId });
	}

	public int updateLocation(long user_id, String lat, String lng) {
		return jdbcTemplate.update("update t_user set lat=?,lng=? where user_id=?", new Object[] { lat, lng, user_id });
	}

	public int updateVisitor(long user_id, String app_id, String device_token, String lat, String lng, String zh_cn) {
		return jdbcTemplate.update("update t_user set app_id=?, device_token=?,zh_cn=?, lat=?,lng=? where user_id=?",
				new Object[] { app_id, device_token, zh_cn, lat, lng, user_id });
	}

	public DetailUser getUserDetailInfo(long user_id) {
		List<DetailUser> list = jdbcTemplate.query("select *from t_user user where user.user_id=?",
				new Object[] { user_id }, new BeanPropertyRowMapper<DetailUser>(DetailUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public int modify_info(long user_id, String nick_name, String birthday, String job, String height, String weight,
			String signature, String my_tags, String interests, String animals, String musics, String weekday_todo,
			String footsteps, String want_to_where, Integer birth_city_id, String contact) {

		String sql = "update t_user set ";
		StringBuilder names = new StringBuilder();
		List<Object> values = new ArrayList<Object>();
		if (nick_name != null) {
			names.append("nick_name=?");
			values.add(nick_name);
		}

		Date birthdayDate = DateTimeUtil.parseDate(birthday);

		if (birthdayDate != null) {
			if (values.size() > 0) {
				names.append(",birthday=?");
			} else {
				names.append("birthday=?");
			}
			values.add(birthdayDate);
		}

		SQLUtil.appendSql(names, job, "job_ids", values);
		SQLUtil.appendSql(names, height, "height", values);
		SQLUtil.appendSql(names, weight, "weight", values);
		SQLUtil.appendSql(names, signature, "signature", values);
		SQLUtil.appendSql(names, my_tags, "my_tag_ids", values);
		SQLUtil.appendSql(names, interests, "interest_ids", values);
		SQLUtil.appendSql(names, animals, "animal_ids", values);
		SQLUtil.appendSql(names, musics, "music_ids", values);
		SQLUtil.appendSql(names, weekday_todo, "weekday_todo_ids", values);
		SQLUtil.appendSql(names, footsteps, "footstep_ids", values);
		SQLUtil.appendSql(names, want_to_where, "want_to_where", values);
		if (birth_city_id != null) {
			SQLUtil.appendSql(names, String.valueOf(birth_city_id), "birth_city_id", values);
		}

		if (contact != null) {
			if (values.size() > 0) {
				names.append(",contact=?");
			} else {
				names.append("contact=?");
			}
			values.add(contact);
		}

		if (values.size() == 0) {
			return 0;
		}
		values.add(user_id);
		sql += names.toString() + " where user_id=?";

		Object[] params = values.toArray();
		return jdbcTemplate.update(sql, params);
	}

	public int visitorToNormal(long user_id, String mobile, String password, String token, String nick_name,
			Date birthday, String sex, String avatar, Date last_login_time) {
		String sql = "update t_user set mobile=?,password=?,token=?,nick_name=?,birthday=?,sex=?,avatar=?,type=?,last_login_time=? where user_id=?";
		return jdbcTemplate.update(sql, new Object[] { mobile, password, token, nick_name, birthday, sex, avatar,
				UserType.OFFIEC.ordinal(), last_login_time, user_id });
	}

	public int uploadToken(long user_id, String token, String zh_cn) {
		String sql = "update t_user set device_token=?,zh_cn=? where user_id=?";
		return jdbcTemplate.update(sql, new Object[] { token, zh_cn, user_id });
	}

	public int uploadLastLoginTime(long user_id) {
		String sql = "update t_user set last_login_time=? where user_id=?";
		return jdbcTemplate.update(sql, new Object[] { new Date(), user_id });
	}

	public Object setCity(Long user_id, Integer city_id) {
		return jdbcTemplate.update("update  t_user set city_id=? where user.user_id=?",
				new Object[] { city_id, user_id });
	}

	public void updateRelationship(Long user_id, Long with_user_id, Relationship relationship) {

		int count = jdbcTemplate.queryForObject(
				"select count(*) from t_user_relationship where user_id=? and with_user_id=?",
				new Object[] { user_id, with_user_id }, Integer.class);
		if (count < 1) {
			jdbcTemplate.update(
					"insert into t_user_relationship (user_id,with_user_id,relationship,create_time) values(?,?,?,?)",
					new Object[] { user_id, with_user_id, relationship.ordinal(), new Date() });
		} else {
			jdbcTemplate.update("update t_user_relationship set relationship=? where  user_id=? and with_user_id=?",
					new Object[] { relationship.ordinal(), user_id, with_user_id });
		}

	}

	public List<Long> getAllUserIds(long last_id, int page) {
		List<Long> ids = jdbcTemplate.query("select user_id from t_user where user_id>? order by user_id limit ?",
				new Object[] { last_id, page }, new BeanPropertyRowMapper<Long>(Long.class));
		return ids;
	}

	public List<BaseUser> getFoundUserRandom(long user_id, int realCount, int gender) {

		String sql = "select u.* from t_found_user_relationship f left join t_user u on f.uid=u.user_id where f.state=? and f.uid<>? and u.avatar is not null and u.sex<>? order by  RAND() limit ?";

		// String sql = "select * from t_user where user_id not in (select uid from
		// t_found_user_relationship where state=? order by uid desc) and user_id<>? and
		// avatar<>? and sex<>? order by RAND() limit ?";
		// String sql = "select * from t_user where user_id not in (select uid from
		// t_found_user_relationship where state=? order by uid desc) and user_id<>? and
		// avatar<>? and sex<>? order by RAND() limit ?";
		List<BaseUser> users = jdbcTemplate.query(sql,
				new Object[] { FoundUserRelationship.VISIBLE.ordinal(), user_id, gender, realCount },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		return users;
	}

	public List<MeiLi> getNewRegistUsers(int page, int count) {
		// String sql = "select u.*, v.* from t_user u left join (select
		// TIMESTAMPDIFF(DAY,now(),end_time) as dayDiff,start_time,user_id from
		// t_user_vip ) v on u.user_id=v.user_id where v.dayDiff >=0 and u.type=? order
		// by v.start_time desc limit ?,?";
		String sql = "select u.*, v.*,c.name as city_name from t_user u left join (select TIMESTAMPDIFF(DAY,now(),end_time) as dayDiff,start_time,user_id from t_user_vip ) v on u.user_id=v.user_id left join t_sys_city c on u.city_id=c.id   where v.dayDiff >=0 and  u.type=? order by v.start_time desc limit ?,?";
		List<MeiLi> users = jdbcTemplate.query(sql,
				new Object[] { UserType.OFFIEC.ordinal(), (page - 1) * count, count }, new RowMapper<MeiLi>() {
					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						// m.setValue(rs.getInt("week_meili"));
						// m.setShanbei(rs.getInt("amount"));
						// m.setBe_like_count(rs.getInt("like_count"));

						LocationUser user = new LocationUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);
						int dayDiff = rs.getInt("dayDiff");
						m.setIs_vip(dayDiff >= 0);
						user.setVip(m.isIs_vip());

						int cid = rs.getInt("city_id");

						if (cid > 0) {
							City c = new City();
							c.setId(cid);
							c.setName(rs.getString("city_name"));
							user.setCity(c);
						}
						return m;
					}

				});
		return users;
	}

	public List<BaseUser> getRandomMeetBottleUser(int realCount) {
		String sql = "select u.* from t_user_meet_bottle_recommend mb  left join t_user u on mb.uid=u.user_id order by  RAND() limit ?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { realCount },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		return users;
	}

	
	public void clearExpireMeetBottleUser() {
		String sql = "delete from  t_user_meet_bottle_recommend where DATEDIFF(create_time,now()) <-2";
		jdbcTemplate.update(sql);
	}
	
	public void removeMeetBottleUserByUserId(long uid) {
		jdbcTemplate.update("delete from t_user_meet_bottle_recommend where uid=" + uid);
	}

	public String getUserAvatar(long user_id) {
		String sql = "select avatar from t_user where user_id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { user_id }, String.class);
	}

	public BaseUser getBaseUser(long user_id) {
		String sql = "select user_id,nick_name,sex,avatar,signature,birthday,token from t_user where user_id=?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { user_id },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	public BaseVipUser getBaseVipUser(long user_id) {
		String sql = "select u.user_id,u.nick_name,u.sex,u.avatar,u.signature,u.birthday,u.token ,vip.vip_id from t_user u left join t_user_vip vip on u.user_id=vip.user_id  where u.user_id=?";
		List<BaseVipUser> users = jdbcTemplate.query(sql, new Object[] { user_id },
				new BeanPropertyRowMapper<BaseVipUser>(BaseVipUser.class) {
					@Override
					public BaseVipUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
						BaseVipUser vipUser = super.mapRow(rs, rowNumber);
						String vipStr = rs.getString("vip_id");
						if (!TextUtils.isEmpty(vipStr) && !"null".equals(vipStr)) {
							vipUser.setVip(true);
						} else {
							vipUser.setVip(false);
						}
						return vipUser;
					}
				});
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	public String getUserToken(long user_id) {
		try {
			String sql = "select token from t_user where user_id=?";
			return jdbcTemplate.queryForObject(sql, new Object[] { user_id }, String.class);
		} catch (Exception e) {
			return null;
		}
	}

	public int isLikeMe(long user_id, long with_user_id) {
		return jdbcTemplate.queryForObject(
				"select count(*) from t_user_relationship where user_id=? and with_user_id=? and relationship=?",
				new Object[] { with_user_id, user_id, Relationship.LIKE.ordinal() }, Integer.class);
	}

	/**
	 * 获取用户总数
	 * 
	 * @return
	 */
	public int getUserSize(String keyword, long user_id) {

		if (user_id > 0) {
			return jdbcTemplate.queryForObject("select count(*) from t_user where user_id=?", new Object[] { user_id },
					Integer.class);
		}

		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.queryForObject("select count(*) from t_user", Integer.class);
		} else {
			return jdbcTemplate.queryForObject("select count(*) from t_user where nick_name like ?",
					new Object[] { "%" + keyword + "%" }, Integer.class);
		}

	}

	/**
	 * 根据类型获取用户总数
	 * 
	 * @param type
	 * @return
	 */
	public int getUserSize(int type, String keyword, long user_id) {
		if (user_id > 0) {
			return jdbcTemplate.queryForObject("select count(*) from t_user where user_id=?", new Object[] { user_id },
					Integer.class);
		}

		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.queryForObject("select count(*) from t_user where type=?", new Object[] { type },
					Integer.class);
		} else {
			return jdbcTemplate.queryForObject("select count(*) from t_user where type=? and nick_name like ?",
					new Object[] { type, "%" + keyword + "%" }, Integer.class);
		}
	}

	/**
	 * 获取发现黑名单用户
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<BaseUser> getFoundUsersByState(int pageSize, int pageIndex, FoundUserRelationship ship) {
		String sql = "select u.* from t_found_user_relationship bu left join t_user u on bu.uid=u.user_id  where bu.state=? order by bu.action_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { ship.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	/**
	 * 获取发现用户黑名单总数
	 * 
	 * @return
	 */
	public int getFoundUsersCountByState(FoundUserRelationship ship) {
		return jdbcTemplate.queryForObject("select count(*) from t_found_user_relationship where state=?",
				new Object[] { ship.ordinal() }, Integer.class);
	}

	/**
	 * 获取邂逅瓶推荐用户
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @param keyword
	 * @return
	 */
	public List<BaseUser> getAllMeetBottleRecommendUser(int pageSize, int pageIndex, String keyword) {
		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.query(
					"select u.* from t_user_meet_bottle_recommend mb left join t_user u on mb.uid=u.user_id order by mb.uid desc limit ?,?",
					new Object[] { (pageIndex - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		} else {
			return jdbcTemplate.query(
					"select u.* from t_user_meet_bottle_recommend mb left join t_user u on mb.uid=u.user_id where u.nick_name like ? order by mb.uid desc limit ?,?",
					new Object[] { "%" + keyword + "%", (pageIndex - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		}

	}

	public int getMeetBottleRecommendUserSize(String keyword) {
		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.queryForObject("select count(*) from t_user_meet_bottle_recommend", Integer.class);
		} else {
			return jdbcTemplate.queryForObject(
					"select count(*) from t_user_meet_bottle_recommend mb left join t_user u on mb.uid=u.user_id where u.nick_name like ?",
					new Object[] { "%" + keyword + "%" }, Integer.class);
		}
	}

	public String getUserGenderByID(long user_id) {
		try {
			return jdbcTemplate.queryForObject("select sex from t_user where user_id=?", new Object[] { user_id },
					String.class);
		} catch (Exception e) {
		}
		return "";

	}

	/**
	 * 获取喜欢我的人的列表
	 * 
	 * @param user_id
	 * @param page_index
	 * @param count
	 * @return
	 */
	public List<BaseUser> getLikeList(long user_id, Integer page_index, Integer count) {
		String sql = "select u.user_id,u.nick_name,u.avatar,u.sex from t_user_relationship tur left join t_user u on tur.user_id= u.user_id where tur.with_user_id=? and  tur.relationship=? order by tur.create_time desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { user_id, Relationship.LIKE.ordinal(), (page_index - 1) * count, count },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	/**
	 * 获取最后一个喜欢我的人
	 * 
	 * @param user_id
	 * @return
	 */
	public List<BaseUser> getLaskLikeMe(long user_id) {
		return jdbcTemplate.query(
				"select u.user_id,u.nick_name,u.avatar,u.sex from t_user_relationship tur left join t_user u on tur.user_id= u.user_id where tur.with_user_id=? and  tur.relationship=? order by tur.create_time desc limit 1",
				new Object[] { user_id, Relationship.LIKE.ordinal() },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	public int getUserCountByIDToken(long user_id, String token) {
		return jdbcTemplate.queryForObject("select count(*) from t_user where user_id=? and token=?",
				new Object[] { user_id, token }, Integer.class);
	}

	public Relationship getRelationShip(long user_id, long user_id_for) {
		try {
			Integer ship = jdbcTemplate.queryForObject(
					"select relationship from t_user_relationship where user_id=? and with_user_id=?",
					new Object[] { user_id, user_id_for }, Integer.class);
			if (ship != null) {
				return Relationship.values()[ship];
			} else {
				return Relationship.DEFAULT;
			}
		} catch (Exception e) {
			return Relationship.DEFAULT;
		}
	}

	public int addSpecialUser(long uid) {

		int countObj = isUidExist(uid);
		if (countObj == 0) {
			return -2;
		}
		countObj = jdbcTemplate.queryForObject("select count(*)  from t_special_user where uid=" + uid, Integer.class);
		if (countObj > 0) {
			return -1;
		}
		return jdbcTemplate.update("insert into t_special_user (uid,create_time) values(?,?)",
				new Object[] { String.valueOf(uid), new Date() });
	}

	public int delSpecialUser(long uid) {
		return jdbcTemplate.update("delete from t_special_user where uid=?", new Object[] { String.valueOf(uid) });
	}

	public List<BaseUser> loadSpecialUsers(int pageIndex, int limit) {
		String sql = "select u.user_id,u.nick_name,u.avatar,u.sex,u.type,u.birthday from t_special_user us left join t_user u on us.uid=u.user_id order by us.create_time desc limit ?,?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { (pageIndex - 1) * limit, limit },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		return users;
	}

	public int getSpecialUsersCount() {
		String sql = "select count(*) from t_special_user";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int updateAccountState(long user_id, int state) {
		return jdbcTemplate.update("update t_user set account_state=? where user_id=?",
				new Object[] { state, user_id });
	}

	public int getUserState(long user_id) {
		try {
			return jdbcTemplate.queryForObject("select state from  t_found_user_relationship where uid=?",
					new Object[] { user_id }, Integer.class);
		} catch (Exception e) {
		}
		return FoundUserRelationship.VISIBLE.ordinal();
	}

	public int todayCheckInCount(long user_id) {
		String sql = "select count(*) from t_check_in_record  where uid=" + user_id
				+ " and to_days(check_in_date) = to_days(now())";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int todayCheckIn(long user_id) {
		String sql = "select count(*) from t_check_in_record  where uid=" + user_id;
		int hasExist = jdbcTemplate.queryForObject(sql, Integer.class);
		if (hasExist > 0) {
			return jdbcTemplate.update("update t_check_in_record set check_in_date=? where uid=?",
					new Object[] { new Date(), user_id });
		} else {
			return jdbcTemplate.update("insert into t_check_in_record (uid,check_in_date) values(?,?)",
					new Object[] { user_id, new Date() });
		}
	}

	public int saveAvatar(long uid, String avatar) {
		String sql = "insert into t_user_avatars (uid,avatar,state) values(?,?,?)";
		return jdbcTemplate.update(sql, new Object[] { uid, avatar, 0 });
	}

	public String deleteAvatar(long user_id, String avatar_id) {
		List<String> avatars = jdbcTemplate.queryForList(
				"select avatar from t_user_avatars where uid=" + user_id + " and id=" + avatar_id, String.class);
		jdbcTemplate.update("delete from t_user_avatars  where uid=" + user_id + " and  id=" + avatar_id);
		if (avatars.size() > 0) {
			return avatars.get(0);
		}
		return null;
	}

	public String getLastAvatar(long user_id) {
		return jdbcTemplate.queryForObject(
				"select avatar from t_user_avatars where uid=" + user_id + " order by id desc  limit 1", String.class);
	}

	public String getCurrentAvatar(long user_id) {
		return jdbcTemplate.queryForObject("select avatar from t_user where user_id=" + user_id, String.class);
	}

	public Integer getAvatarIdByName(String avatarName) {
		return jdbcTemplate.queryForObject("select id from t_user_avatars where avatar='" + avatarName + "'",
				Integer.class);
	}

	public int getAvatarCount(long user_id) {
		return jdbcTemplate.queryForObject("select count(*) from t_user_avatars where uid=" + user_id, Integer.class);
	}

	public List<Avatar> getUserAvatars(long user_id) {
		return jdbcTemplate.query("select *  from t_user_avatars where uid=" + user_id + " order by id desc limit 6",
				new BeanPropertyRowMapper<Avatar>(Avatar.class));
	}

	public List<String> getUserAvatarsString(long user_id) {
		return jdbcTemplate.queryForList("select avatar  from t_user_avatars where uid=" + user_id, String.class);
	}
	
	
	public long editAvatarState(int id, int state) {
		String sql = "select uid, avatar from t_user_avatars where id=" + id;
		Map<String, Object> r = jdbcTemplate.queryForMap(sql);

		String avatar = r.get("avatar").toString();
		long uid = (long) r.get("uid");

		AvatarIMGStatus status = AvatarIMGStatus.values()[state];
		String illegalName = "illegal.jpg";
		if (status == AvatarIMGStatus.ILLEGAL) {
			sql = "update t_user_avatars set avatar=?,illegal_avatar=?,state=?,checked_time=? where id=?";
			int count = jdbcTemplate.update(sql, new Object[] { illegalName, avatar, state, new Date(), id });
			if (count == 1) {
				sql = "update t_user set avatar=? where user_id=? and avatar=?";
				jdbcTemplate.update(sql, new Object[] { illegalName, uid, avatar });
			}
		} else {
		}
		return uid;
	}

	public long editAvatarStateByUserId(long uid, int state) {
		AvatarIMGStatus status = AvatarIMGStatus.values()[state];
		String illegalName = "illegal.jpg";
		if (status == AvatarIMGStatus.ILLEGAL) {
			String sql = "update t_user set avatar=? where user_id=?";
			jdbcTemplate.update(sql, new Object[] { illegalName, uid });
		}
		return uid;
	}

	public List<String> loadIllegalAvatar() {
		return jdbcTemplate.queryForList(
				"select illegal_avatar from t_user_avatars  where  state=? and checked_time < date_sub(NOW(), INTERVAL 2 DAY)",
				new Object[] { AvatarIMGStatus.ILLEGAL.ordinal() }, String.class);
	}

	public List<String> loadAvatarByUid(long uid) {
		return jdbcTemplate.queryForList(
				"select avatar from t_user_avatars  where  uid="+uid, String.class);
	}
	
	
	public String getContact(long user_id) {
		String contact = jdbcTemplate.queryForObject("select contact from t_user where user_id=" + user_id,
				String.class);
		return contact;
	}

	public int markContactRel(long user_id, long target_uid) {
		return jdbcTemplate.update("insert into t_contact_get_rel (uid,target_uid) values(?,?)",
				new Object[] { user_id, target_uid });
	}

	public boolean hadGetContact(long user_id, long target_uid) {
		int count = jdbcTemplate.queryForObject("select count(*) from t_contact_get_rel where uid=? and target_uid=?",
				new Object[] { user_id, target_uid }, Integer.class);
		return count > 0;
	}

	public boolean checkExistByIdAndPwd(long user_id, String md5_pwd) {
		int count = jdbcTemplate.queryForObject("select count(*) from t_user where user_id=? and password=?",
				new Object[] { user_id, md5_pwd }, Integer.class);
		return count > 0;
	}

	public void saveUserOnline(long uid) {
		jdbcTemplate.update("insert into t_user_online (uid,check_time) values(?,?)", new Object[] { uid, new Date() });
	}

	public void updateOnlineCheckTime(long uid) {
		jdbcTemplate.update("update t_user_online set check_time=? where uid=?", new Object[] { new Date(), uid });
	}

	public List<BaseUser> getOnlineUsers(int page, int count) {
		String sql = "select u.user_id,u.nick_name,u.avatar from t_user_online l left join t_user u on l.uid=u.user_id order by l.check_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { (page - 1) * count, count },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	public void removeOnline(long uid) {
		jdbcTemplate.update("delete from t_user_online where uid=" + uid);
	}

	public List<Long> getLatestLoginUserIds(int limit) {
		String sql = "select user_id from t_user where user_id not in(select uid from t_user_online) order by last_login_time desc limit "
				+ limit;
		return jdbcTemplate.queryForList(sql, Long.class);
	}

	public void removeTimeoutOnlineUsers(int timeoutDay) {
		String sql = "delete from t_user_online where check_time < DATE_SUB(NOW(),INTERVAL ? DAY)";
		jdbcTemplate.update(sql, new Object[] { timeoutDay });
	}

	// 根据状态获取审核的头像列表
	public List<BaseUser> listConfirmAvatars(int state, int pageSize, int pageIndex) {
		String sql = "select v.id, u.user_id,u.nick_name,v.avatar from t_user_avatars v left join t_user u on v.uid=u.user_id where v.state=? order by v.id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { state, (pageIndex - 1) * pageSize, pageSize },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class) {
					@Override
					public BaseUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
						BaseUser user = super.mapRow(rs, rowNumber);
						user.setContact(String.valueOf(rs.getInt("id")));
						return user;
					}
				});
	}

	public int getCountOfConfirmAvatars() {
		return jdbcTemplate.queryForObject("select count(*) from t_user_avatars where state=0", Integer.class);
	}

	public List<String> checkRegistIP(int limitCount) {
		List<String> list= jdbcTemplate.queryForList("select ip from (select count(*) as count,ip from t_user group by ip) d where d.count>=? and d.ip is not null",new Object[] {String.valueOf(limitCount)},String.class);
		 return list;
	}
	
	public List<Long> loadIllegalRegistUids(String illegalIP) {
		List<Long> uids= jdbcTemplate.queryForList("select user_id from t_user where ip='"+illegalIP+"'",Long.class);
		 return uids;
	}
	public void deleteIllegalUser(long uid) {
		jdbcTemplate.update("delete from t_user where user_id="+uid);
	}
	
}
