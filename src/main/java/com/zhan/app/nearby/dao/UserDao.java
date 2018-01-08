package com.zhan.app.nearby.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.mapper.SimpkleUserMapper;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.bean.user.LocationUser;
import com.zhan.app.nearby.bean.user.SimpleUser;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.SQLUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("userDao")
public class UserDao extends BaseDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

	public BaseUser getUser(long id) {
		List<BaseUser> list = jdbcTemplate.query("select *from t_user user where user.user_id=?", new Object[] { id },
				new SimpkleUserMapper());
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

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
	public List<BaseUser> getUsers(int pageSize, int currentPage, int type, String keyword) {
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
	public List<BaseUser> getUsers(int pageSize, int currentPage, String keyword) {
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

	public int updateToken(long userId, String token, String _ua, Date last_login_time) {
		return jdbcTemplate.update("update t_user set token=?,_ua=?,last_login_time=? where user_id=?",
				new Object[] { token, _ua, last_login_time, userId });
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
			String footsteps, String want_to_where, Integer birth_city_id) {

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

	public String getDeviceToken(long user_id) {
		return jdbcTemplate.queryForObject("select device_token from t_user user where user.user_id=?",
				new Object[] { user_id }, String.class);
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
			jdbcTemplate.update("insert into t_user_relationship (user_id,with_user_id,relationship) values(?,?,?)",
					new Object[] { user_id, with_user_id, relationship.ordinal() });
		}

	}

	public List<Long> getAllUserIds(long last_id, int page) {
		List<Long> ids = jdbcTemplate.query("select user_id from t_user where user_id>? order by user_id limit ?",
				new Object[] { last_id, page }, new BeanPropertyRowMapper<Long>(Long.class));
		return ids;
	}

	public List<BaseUser> getRandomUser(long user_id, int realCount, int gender) {
		String sql = "select * from t_user where user_id not in (select uid from t_found_user_relationship where state=? order by uid desc) and  user_id<>? and avatar<>? and sex<>? order by  RAND() limit ?";
		List<BaseUser> users = jdbcTemplate.query(sql,
				new Object[] { FoundUserRelationship.GONE.ordinal(), user_id, "", gender, realCount },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		return users;
	}

	public List<BaseUser> getRandomMeetBottleUser(int realCount) {
		String sql = "select u.* from t_user_meet_bottle_recommend mb  left join t_user u on mb.uid=u.user_id order by  RAND() limit ?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { realCount },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
		return users;
	}

	public String getUserAvatar(long user_id) {
		String sql = "select avatar from t_user where user_id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { user_id }, String.class);
	}

	public List<BaseUser> getUserSimple(long user_id) {
		String sql = "select user_id,nick_name,sex,avatar,signature,birthday from t_user where user_id=?";
		return jdbcTemplate.query(sql, new Object[] { user_id }, new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	public int isLikeMe(long user_id, long with_user_id) {
		return jdbcTemplate.queryForObject(
				"select count(*) from t_user_relationship where user_id=? and with_user_id=? and relationship=?",
				new Object[] { with_user_id, user_id, Relationship.LIKE.ordinal() }, Integer.class);
	}

	/**
	 * 获取用户的动态消息（例如表白信等）
	 * 
	 * @param user_id
	 * @return
	 */
	public List<DynamicMessage> getUserDynamicMsgs(long user_id) {
		return null;
	}

	/**
	 * 获取用户总数
	 * 
	 * @return
	 */
	public int getUserSize(String keyword) {
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
	public int getUserSize(int type, String keyword) {
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
	public List<BaseUser> getFoundBlackUsers(int pageSize, int pageIndex) {
		String sql = "select u.* from t_found_user_relationship bu left join t_user u on bu.uid=u.user_id  where bu.state=? order by bu.uid desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { FoundUserRelationship.GONE.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	/**
	 * 获取发现用户黑名单总数
	 * 
	 * @return
	 */
	public int getFoundBlackUsers() {
		return jdbcTemplate.queryForObject("select count(*) from t_found_user_relationship where state=?",
				new Object[] { FoundUserRelationship.GONE.ordinal() }, Integer.class);
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
		return jdbcTemplate.queryForObject("select sex from t_user where user_id=?", new Object[] { user_id },
				String.class);
	}
}
