package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Avatar;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.bean.mapper.SimpkleUserMapper;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.bean.user.LoginUser;
import com.zhan.app.nearby.bean.user.RankUser;
import com.zhan.app.nearby.comm.AccountStateType;
import com.zhan.app.nearby.comm.AvatarIMGStatus;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.SQLUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository
public class UserDao extends BaseDao<BaseUser> {
	@Resource
	private JdbcTemplate jdbcTemplate;

	/**
	 * 鏍规嵁绫诲瀷鑾峰彇鐢ㄦ埛
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
					new Object[] { user_id, type, (currentPage - 1) * pageSize, pageSize }, getEntityMapper());
		}

		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.query("select *from t_user  where type=? order by user_id desc limit ?,?",
					new Object[] { type, (currentPage - 1) * pageSize, pageSize }, getEntityMapper());
		} else {
			return jdbcTemplate.query(
					"select *from t_user  where type=? and nick_name like ? order by user_id desc limit ?,?",
					new Object[] { type, "%" + keyword + "%", (currentPage - 1) * pageSize, pageSize },
					getEntityMapper());
		}
	}

	/**
	 * 鑾峰彇鎵�鏈夌敤鎴�
	 * 
	 * @param pageSize
	 * @param currentPage
	 * @return
	 */
	public List<BaseUser> getUsers(int pageSize, int currentPage, String keyword, long user_id) {

		if (user_id > 0) {
			return jdbcTemplate.query("select *from t_user where user_id=? order by user_id desc limit ?,?",
					new Object[] { user_id, (currentPage - 1) * pageSize, pageSize }, getEntityMapper());
		}

		if (TextUtils.isEmpty(keyword)) {
			return jdbcTemplate.query("select *from t_user order by user_id desc limit ?,?",
					new Object[] { (currentPage - 1) * pageSize, pageSize }, getEntityMapper());
		} else {
			return jdbcTemplate.query("select *from t_user where nick_name like ? order by user_id desc limit ?,?",
					new Object[] { "%" + keyword + "%", (currentPage - 1) * pageSize, pageSize }, getEntityMapper());
		}
	}

	public BaseUser findBaseUserByMobile(String mobile) {
		List<BaseUser> list = jdbcTemplate.query("select  *   from t_user  where mobile=?", new Object[] { mobile },
				getEntityMapper());
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public int isUidExist(long user_id) {
		return jdbcTemplate.queryForObject("select count(*) from t_user where user_id=" + user_id, Integer.class);
	}

	public LoginUser findLocationUserByMobile(String mobile) {
		List<LoginUser> list = jdbcTemplate.query(
				"select user.* ,city.name as city_name from t_user user left join t_sys_city city on user.city_id=city.id where user.mobile=?",
				new Object[] { mobile }, getEntityMapper(LoginUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public LoginUser findLocationUserByOpenid(String openid) {
		List<LoginUser> list = jdbcTemplate.query(
				"select user.* ,city.name as city_name from t_user user left join t_sys_city city on user.city_id=city.id where user.openid like ?",
				new Object[] { "%" + openid + "%" }, getEntityMapper(LoginUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public BaseUser findLocationUserById(long user_id) {
		List<BaseUser> list = jdbcTemplate.query(
				"select user.* ,city.name as city_name from t_user user left join t_sys_city city on user.city_id=city.id where user.user_id=?",
				new Object[] { user_id }, getEntityMapper(BaseUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public String getUserMobileById(long user_id) {
		List<String> ms = jdbcTemplate.queryForList("select mobile from t_user where user_id=?",
				new Object[] { user_id }, String.class);

		if (ms.isEmpty()) {
			return null;
		}
		return ms.get(0);
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

	public LoginUser findLocationUserByDeviceId(String deviceId) {
		List<LoginUser> list = jdbcTemplate.query("select *from t_user user where user.mobile=? and type=?",
				new Object[] { deviceId, UserType.VISITOR.ordinal() },
				new BeanPropertyRowMapper<LoginUser>(LoginUser.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public void delete(long id) {
		jdbcTemplate.update("delete from t_user where user_id=?", new Object[] { id });
	}

	public int getUserCountByMobile(String mobile) {
		int count = jdbcTemplate.queryForObject("select count(*) from t_user user where user.mobile=?",
				new String[] { mobile }, Integer.class);
		return count;
	}

	public int getUserCountByOpenId(String openid) {
		int count = jdbcTemplate.queryForObject("select count(*) from t_user user where user.openid like ?",
				new String[] { "%" + openid + "%" }, Integer.class);
		return count;
	}

	public int updateToken(long userId, String token, Date last_login_time) {
		return jdbcTemplate.update("update t_user set token=?,last_login_time=? where user_id=?",
				new Object[] { token, last_login_time, userId });
	}

	public int updateToken(long userId, String token, Date last_login_time, String device_token) {
		return jdbcTemplate.update("update t_user set token=?,last_login_time=?,device_token=? where user_id=?",
				new Object[] { token, last_login_time, device_token, userId });
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
			String footsteps, String want_to_where, Integer birth_city_id, String contact, String newSex) {

		String sql = "update t_user set ";
		StringBuilder names = new StringBuilder();
		List<Object> values = new ArrayList<Object>();
		if (nick_name != null) {
			names.append("nick_name=?");
			values.add(nick_name);
		}

		if (newSex != null) {
			if (values.size() > 0) {
				names.append(",sex=?");
				values.add(newSex);
			} else {
				names.append("sex=?");
				values.add(newSex);
			}

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
		List<Long> ids = jdbcTemplate.queryForList(
				"select user_id from t_user where user_id>? order by user_id limit ?", new Object[] { last_id, page },
				Long.class);
		return ids;
	}

	public List<BaseUser> getFoundUserRandom(long user_id, int realCount, int gender) {

		String sql = "select u.user_id,u.nick_name,u.avatar from t_found_user_relationship f left join t_user u on f.uid=u.user_id where f.state=? and f.uid<>? and u.avatar is not null and u.sex<>? order by  RAND() limit ?";

		List<BaseUser> users = jdbcTemplate.query(sql,
				new Object[] { FoundUserRelationship.VISIBLE.ordinal(), user_id, gender, realCount },
				getEntityMapper());
		return users;
	}

	public void removeFromFoundUserList(long user_id) {
		String sql = "delete from t_found_user_relationship where uid=" + user_id;
		jdbcTemplate.update(sql);
	}

	@Cacheable(value = "one_hour", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<MeiLi> getNewRegistUsers(int page, int count) {
		String sql = "select u.user_id ,u.nick_name,u.meili,u.isvip, u.avatar,u.isvip, g.coins from t_user u "
				+ " left join t_gift_coins g on g.uid=u.user_id "
				+ " left join t_found_user_relationship fu on u.user_id=fu.uid "
				+ " where  (u.type=? or u.type=?) and   (fu.state is null or fu.state<>1)  and DATE_SUB(CURDATE(), INTERVAL 15 DAY) <= date(create_time) order by u.meili desc limit ?,?";

		List<MeiLi> users = jdbcTemplate.query(sql, new Object[] {UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },
				new RowMapper<MeiLi>() {
					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						m.setValue(rs.getInt("meili"));
						m.setShanbei(rs.getInt("coins"));
						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);
						user.setIsvip(rs.getInt("isvip"));
						return m;
					}

				});
		return users;
	}

	@Cacheable(value = "one_hour", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<RankUser> getNewRegistUsersV2(int page, int count) {
		String sql = "select u.user_id ,u.nick_name,u.meili,u.isvip,u.lat,u.lng, u.avatar,u.isvip, g.coins as shanbei from t_user u "
				+ "left join t_gift_coins g on g.uid=u.user_id "
				+ " left join t_found_user_relationship fu on u.user_id=fu.uid "
				+ "where  (u.type=? or u.type=?) and   (fu.state is null or fu.state<>1)  and DATE_SUB(CURDATE(), INTERVAL 15 DAY) <= date(create_time) order by u.meili desc limit ?,?";

		List<RankUser> users = jdbcTemplate.query(
				sql, new Object[] { UserType.OFFIEC.ordinal(),
						UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },
				new BeanPropertyRowMapper<RankUser>(RankUser.class));
		return users;
	}

	/**
	 * vip姒�
	 * 
	 * @param page
	 * @param count
	 * @return
	 */
	@Cacheable(value = "thrity_minute", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<MeiLi> getVipRankUsers(int page, int count) {
		String sql = "select u.*,c.name as city_name from t_user u " + "left join t_user_vip v on u.user_id=v.user_id "
				+ "left join t_sys_city c on u.city_id=c.id   "
				+ "where u.isvip=1 and  (u.type=? or u.type=?) order by v.start_time desc limit ?,?";
		List<MeiLi> users = jdbcTemplate.query(sql,
				new Object[] { UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },
				new RowMapper<MeiLi>() {
					@Override
					public MeiLi mapRow(ResultSet rs, int rowNum) throws SQLException {
						MeiLi m = new MeiLi();
						// m.setValue(rs.getInt("week_meili"));
						// m.setShanbei(rs.getInt("amount"));
						// m.setBe_like_count(rs.getInt("like_count"));

						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						user.setMeili(rs.getInt("meili"));
						user.setIsvip(1);
						ImagePathUtil.completeAvatarPath(user, true);
						m.setUser(user);
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

	@Cacheable(value = "thrity_minute", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<RankUser> getVipRankUsersV2(int page, int count) {
		String sql = "select u.user_id ,u.nick_name,u.lat,u.lng,u.meili,u.city_id,u.isvip, u.avatar,u.isvip,c.name as city_name from t_user u "
				+ "left join t_user_vip v on u.user_id=v.user_id " + "left join t_sys_city c on u.city_id=c.id   "
				+ "left join t_gift_coins g on u.user_id=g.uid "
				+ "where u.isvip=1 and  (u.type=? or u.type=?) order by v.start_time desc limit ?,?";
		List<RankUser> users = jdbcTemplate.query(sql,
				new Object[] { UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(), (page - 1) * count, count },
				new BeanPropertyRowMapper<RankUser>(RankUser.class) {
					@Override
					public RankUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
						RankUser u = super.mapRow(rs, rowNumber);
						int cid = rs.getInt("city_id");
						if (cid > 0) {
							City c = new City();
							c.setId(cid);
							c.setName(rs.getString("city_name"));
							u.setCity(c);
						}
						return u;
					}
				});
		return users;
	}

	public List<BaseUser> getRandomMeetBottleUser(int realCount) {
		String sql = "select u.* from t_user_meet_bottle_recommend mb  left join t_user u on mb.uid=u.user_id order by  RAND() limit ?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { realCount }, getEntityMapper());
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
		String sql = "select user_id,nick_name,sex,avatar,signature,birthday,token,isvip from t_user where user_id=?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { user_id }, getEntityMapper());
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	public BaseUser getBaseUserNoToken(long user_id) {
		String sql = "select user_id,nick_name,sex,avatar,signature,birthday,isvip from t_user where user_id=?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { user_id }, getEntityMapper());
		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	public BaseUser getBaseVipUser(long user_id) {
		String sql = "select u.user_id,u.nick_name,u.sex,u.avatar,u.signature,u.birthday,u.token ,u.isvip from t_user u  where u.user_id=?";
		List<BaseUser> users = jdbcTemplate.query(sql, new Object[] { user_id },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
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
	 * 鑾峰彇鐢ㄦ埛鎬绘暟
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
	 * 鏍规嵁绫诲瀷鑾峰彇鐢ㄦ埛鎬绘暟
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
	 * 鑾峰彇鍙戠幇榛戝悕鍗曠敤鎴�
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<BaseUser> getFoundUsersByState(int pageSize, int pageIndex, FoundUserRelationship ship) {
		String sql = "select u.* from t_found_user_relationship bu left join t_user u on bu.uid=u.user_id  where bu.state=? order by bu.action_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { ship.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				getEntityMapper());
	}

	/**
	 * 鑾峰彇鍙戠幇鐢ㄦ埛榛戝悕鍗曟�绘暟
	 * 
	 * @return
	 */
	public int getFoundUsersCountByState(FoundUserRelationship ship) {
		return jdbcTemplate.queryForObject("select count(*) from t_found_user_relationship where state=?",
				new Object[] { ship.ordinal() }, Integer.class);
	}

	/**
	 * 鑾峰彇閭傞�呯摱鎺ㄨ崘鐢ㄦ埛
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
					new Object[] { "%" + keyword + "%", (pageIndex - 1) * pageSize, pageSize }, getEntityMapper());
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
	 * 鑾峰彇鍠滄鎴戠殑浜虹殑鍒楄〃
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
				getEntityMapper());
	}

	/**
	 * 鑾峰彇鏈�鍚庝竴涓枩娆㈡垜鐨勪汉
	 * 
	 * @param user_id
	 * @return
	 */
	public List<BaseUser> getLaskLikeMe(long user_id) {
		return jdbcTemplate.query(
				"select u.user_id,u.nick_name,u.avatar,u.sex from t_user_relationship tur left join t_user u on tur.user_id= u.user_id where tur.with_user_id=? and  tur.relationship=? order by tur.create_time desc limit 1",
				new Object[] { user_id, Relationship.LIKE.ordinal() }, getEntityMapper());
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
				getEntityMapper());
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
		List<Integer> states = jdbcTemplate.queryForList(
				"select state from  t_found_user_relationship where uid=? limit 1", new Object[] { user_id },
				Integer.class);
		if (states.isEmpty()) {
			return FoundUserRelationship.VISIBLE.ordinal();
		} else {
			return states.get(0);
		}

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

	public void deleteAvatar(int id) {
		jdbcTemplate.update("delete from t_user_avatars  where id=" + id);
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

	public List<Avatar> listNotCheckedAvatars(int count) {
		return jdbcTemplate.query("select *  from t_user_avatars where state=0  order by id desc limit " + count,
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
			ImageSaveUtils.removeAcatar(avatar);
			sql = "delete from  t_user_avatars where  id=" + id;
			int count = jdbcTemplate.update(sql);
			if (count == 1) {
				sql = "update t_user set avatar=? where user_id=?";
				jdbcTemplate.update(sql, new Object[] { illegalName, uid });
			}
		} else {
		}
		return uid;
	}

	public void editAvatarStateToIllegal(long user_id, String name) {
		String illegalName = "illegal.jpg";
		ImageSaveUtils.removeAcatar(name);
		String sql = "delete from  t_user_avatars where  avatar=?";
		int count = jdbcTemplate.update(sql, new Object[] { name });
		if (count == 1) {
			sql = "update t_user set avatar=? where user_id=?";
			jdbcTemplate.update(sql, new Object[] { illegalName, user_id });
		}
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
		return jdbcTemplate.queryForList("select avatar from t_user_avatars  where  uid=" + uid, String.class);
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
		int count = jdbcTemplate.queryForObject("select count(*) from t_user_online where uid=" + uid, Integer.class);
		if (count == 0) {
			jdbcTemplate.update("insert into t_user_online (uid,check_time) values(?,?)",
					new Object[] { uid, new Date() });
		} else {
			jdbcTemplate.update("update t_user_online set check_time=? where uid=?", new Object[] { new Date(), uid });
		}
	}

	public void updateOnlineCheckTime(long uid) {
		jdbcTemplate.update("update t_user_online set check_time=? where uid=?", new Object[] { new Date(), uid });
	}

	public List<LoginUser> getOnlineUsers(int page, int count) {
		String sql = "select u.user_id,u.nick_name,u.avatar,u.last_login_time ,u.sex " + " from t_user_online l"
				+ "  inner join t_user u on l.uid=u.user_id where u.isFace=1 order by l.check_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { (page - 1) * count, count },
				new BeanPropertyRowMapper<LoginUser>(LoginUser.class));
	}

//	@Cacheable(value = "five_minute", key = "#root.methodName+'_'+#page+'_'+#count")
	public List<LoginUser> getRankOnlineUsers(int count,Date time_point) {
		String sql = "select u.user_id,u.nick_name,u.avatar,l.check_time as last_login_time ,u.sex,u.isvip,u.lat,u.lng ,ifnull(gift.tval,'0') as shanbei " + " from t_user_online l"
				+ "  inner join t_user u on l.uid=u.user_id"
				+ "  left join "
				+ " (select tg.user_id ,sum(tg.val) as tval from (select o.*,o.count*g.price as val from  t_gift_own o left join t_gift g on o.gift_id=g.id) as tg group by tg.user_id) gift "
				+ "on l.uid=gift.user_id "
				+ " where u.isFace=1 ";
		
		
		if(time_point==null) {
			sql+=" order by l.check_time desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { count },
					new BeanPropertyRowMapper<LoginUser>(LoginUser.class));
		}else {
			sql+=" and l.check_time<? order by l.check_time desc limit ?";
			return jdbcTemplate.query(sql, new Object[] {time_point,count },
					new BeanPropertyRowMapper<LoginUser>(LoginUser.class));
		}
	}
	
	public void removeOnline(long uid) {
		jdbcTemplate.update("delete from t_user_online where uid=" + uid);
	}

	public List<Long> getLatestLoginUserIds(int limit) {
		String sql = "select user_id from t_user where user_id not in(select uid from t_user_online) order by last_login_time desc limit "
				+ limit;
		return jdbcTemplate.queryForList(sql, Long.class);
	}

	public Date getUserLastLoginTime(long uid) {
		String sql = "select last_login_time from t_user where user_id = " + uid;
		return jdbcTemplate.queryForObject(sql, Date.class);
	}

	public void removeTimeoutOnlineUsers(int timeoutDay) {
		String sql = "delete from t_user_online where check_time < DATE_SUB(NOW(),INTERVAL ? DAY)";
		jdbcTemplate.update(sql, new Object[] { timeoutDay });
	}

	// 鏍规嵁鐘舵�佽幏鍙栧鏍哥殑澶村儚鍒楄〃
	public List<BaseUser> listConfirmAvatars(int state, int pageSize, int pageIndex, Long user_id) {
		String sql = "select v.id, u.user_id,u.nick_name,u.sex,v.avatar from t_user_avatars v left join t_user u on v.uid=u.user_id where v.state=? order by v.id desc limit ?,?";
		if (user_id != null && user_id > 0) {
			sql = "select v.id, u.user_id,u.nick_name,u.sex,v.avatar from t_user_avatars v left join t_user u on v.uid=u.user_id where v.uid="
					+ user_id + " and  v.state=? order by v.id desc limit ?,?";
		}
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

	public List<BaseUser> listAvatarsByUid(int pageSize, int pageIndex, Long user_id, String nickName) {

		if (user_id == null) {
			String sql = "select v.id, u.user_id,u.nick_name,v.avatar from t_user_avatars v left join t_user u on v.uid=u.user_id where u.nick_name like ? order by v.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { "%" + nickName + "%", (pageIndex - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class) {
						@Override
						public BaseUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
							BaseUser user = super.mapRow(rs, rowNumber);
							user.setContact(String.valueOf(rs.getInt("id")));
							return user;
						}
					});
		} else {
			String sql = "select v.id, u.user_id,u.nick_name,v.avatar from t_user_avatars v left join t_user u on v.uid=u.user_id where v.uid="
					+ user_id + " order by v.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { (pageIndex - 1) * pageSize, pageSize },
					new BeanPropertyRowMapper<BaseUser>(BaseUser.class) {
						@Override
						public BaseUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
							BaseUser user = super.mapRow(rs, rowNumber);
							user.setContact(String.valueOf(rs.getInt("id")));
							return user;
						}
					});
		}
	}

	public int getCountOfConfirmAvatars(Long user_id, int state) {
		if (user_id != null && user_id > 0)
			return jdbcTemplate.queryForObject(
					"select count(*) from t_user_avatars where state=" + state + " and uid=" + user_id, Integer.class);
		else
			return jdbcTemplate.queryForObject("select count(*) from t_user_avatars where state=" + state,
					Integer.class);
	}

	public int getCountOfUserAvatars(Long user_id, String nickName) {

		if (user_id == null) {
			return jdbcTemplate.queryForObject(
					"select count(*) from t_user_avatars v left join t_user u on v.uid=u.user_id where  u.nick_name like ?",
					new String[] { "%" + nickName + "%" }, Integer.class);
		} else {
			return jdbcTemplate.queryForObject("select count(*) from t_user_avatars where  uid=" + user_id,
					Integer.class);
		}

	}

	public List<String> checkRegistIP(int limitCount) {
		List<String> list = jdbcTemplate.queryForList(
				"select ip from (select count(*) as count,ip from t_user group by ip) d where d.count>=? and d.ip is not null",
				new Object[] { String.valueOf(limitCount) }, String.class);
		return list;
	}

	public List<Long> loadIllegalRegistUids(String illegalIP) {
		List<Long> uids = jdbcTemplate.queryForList("select user_id from t_user where ip='" + illegalIP + "'",
				Long.class);
		return uids;
	}

	public void deleteIllegalUser(long uid) {
		jdbcTemplate.update("delete from t_user where user_id=" + uid);
	}

	public int addToFound(long user_id) {
		int count = jdbcTemplate.update("update t_found_user_relationship set state=?,action_time=? where uid=?",
				new Object[] { FoundUserRelationship.VISIBLE.ordinal(), new Date(), user_id });
		if (count != 1) {
			String sql = "insert into t_found_user_relationship values (?, ?,?)";
			return jdbcTemplate.update(sql,
					new Object[] { user_id, FoundUserRelationship.VISIBLE.ordinal(), new Date() });
		}
		return count;
	}

	public void removeFromFound(long user_id) {
		jdbcTemplate.update("delete from  t_found_user_relationship where uid=" + user_id);
	}

	public boolean isFollowed(long user_id, long target_id) {
		return jdbcTemplate.queryForObject("select count(*) from t_user_follow where uid=? and target_id=?",
				new Object[] { user_id, target_id }, Integer.class) > 0;
	}

	public void follow(long user_id, long target_id) {
		String sql = "insert ignore  into t_user_follow (uid,target_id,create_time) values (?, ?,?)";
		jdbcTemplate.update(sql, new Object[] { user_id, target_id,new Date() });
	}

	public void cancelFollow(long user_id, long target_id) {
		String sql = "delete from  t_user_follow where uid=? and target_id=?";
		jdbcTemplate.update(sql, new Object[] { user_id, target_id });
	}

	public int getFansCount(long user_id) {
		String sql = "select count(*) from t_user_follow where target_id=" + user_id;
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int getFollowCount(long user_id) {
		String sql = "select count(*) from t_user_follow where uid=" + user_id;
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int getTaskCount(long user_id, String aid, String task_id, String uuid) {
		return jdbcTemplate.queryForObject(
				"select count(*) from t_task_history where uid=? and aid=? and task_id=? and uuid=?",
				new Object[] { user_id, aid, task_id, uuid }, Integer.class);
	}

	public int savaTaskHistory(long user_id, String aid, String task_id, String uuid, int extra) {
		String sql = "insert into t_task_history (uid,task_id,create_time,aid,uuid,extra) values (?, ?,?,?,?,?)";
		return jdbcTemplate.update(sql, new Object[] { user_id, task_id, new Date(), aid, uuid, extra });
	}

	public List<BaseUser> followUsers(long user_id, boolean isFollowMe, int page, int count) {

		String sql = "select u.user_id,u.nick_name,u.avatar,u.meili,u.isvip from t_user_follow f left join t_user u on f.target_id =u.user_id "
				 + " where f.uid=? limit ?,?";
		if (isFollowMe) { // 鍏虫敞鎴戠殑鐢ㄦ埛
			sql = "select u.user_id,u.nick_name,u.avatar,u.meili,u.isvip from t_user_follow f left join t_user u on f.uid =u.user_id "
				 + " where f.target_id=? limit ?,?";
		}
		return jdbcTemplate.query(sql, new Object[] { user_id, (page - 1) * count, count },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class) {
					@Override
					public BaseUser mapRow(ResultSet rs, int rowNumber) throws SQLException {
						BaseUser user = super.mapRow(rs, rowNumber);
						int cid = rs.getInt("city_id");
						if (cid > 0) {
							City c = new City();
							c.setId(cid);
							c.setName(rs.getString("city_name"));
							user.setCity(c);
						}
						return user;
					}
				});
	}

	
	public List<RankUser> getIWatching(long user_id, int page, int count) {
		String sql = "select u.user_id,u.nick_name,u.avatar,u.meili,u.isvip,u.lat,u.lng,ifnull(gift.tval,'0') as shanbei from t_user_follow f "
				+ "  left join t_user u on f.target_id =u.user_id "
				+ " left join "
				+ " (select tg.user_id ,sum(tg.val) as tval from (select o.*,o.count*g.price as val from  t_gift_own o left join t_gift g on o.gift_id=g.id) as tg group by tg.user_id) gift "
				+ "on f.target_id=gift.user_id "
				+ " where f.uid=? order by f.create_time desc limit ?,?";
		 
		return jdbcTemplate.query(sql, new Object[] { user_id, (page - 1) * count, count },
				new BeanPropertyRowMapper<RankUser>(RankUser.class));
	}
	
	/**
	 * 鑾峰彇娲昏穬鐢ㄦ埛,3涓湀鍓嶆敞鍐岋紝涓旀渶杩�1澶╃櫥闄嗙殑寮傛�х敤鎴�
	 * 
	 * @param page
	 * @param count
	 * @return
	 */
//	@Cacheable(value="one_day",key="#root.methodName+'_'+#sex")
	public List<BaseUser> getActiveUsers(int sex) {
		String sql = "select u.user_id,u.nick_name,u.avatar from t_user u " + "where u.type=1 " + "and u.sex=? "
//				+ "and u.create_time <DATE_SUB(CURDATE(), INTERVAL 3 MONTH) " //鍘绘帀涓変釜鏈堢殑娉ㄥ唽闄愬埗
				+ "and u.last_login_time > DATE_SUB(CURDATE(), INTERVAL 1 DAY) order by rand() limit ? ";
		return jdbcTemplate.query(sql, new Object[] { sex, 1 }, getEntityMapper());
	}

	// 鑾峰彇鏈�杩戜袱澶╃櫥闄嗙殑鐢ㄦ埛
	public List<BaseUser> get2daysLoginUser(long uid, int sex, int day, int count) {
		String sql = "select u.user_id,u.nick_name,u.avatar from t_user u  where u.user_id<>? and u.type=1  and u.sex=? and  DATE_SUB(CURDATE(), INTERVAL ? DAY) <= date(u.last_login_time) order by rand() limit ? ";
		return jdbcTemplate.query(sql, new Object[] { uid, sex, day, count }, getEntityMapper());

	}

	public List<BaseUser> get2daysLoginUserWithOutIds(long uid, int sex, int day, int count, long[] withOutids) {

		if (withOutids == null) {
			String sql = "select u.user_id,u.nick_name,u.avatar from t_user u  where u.user_id<>? and u.type=1  and u.sex=? and  DATE_SUB(CURDATE(), INTERVAL ? DAY) <= date(u.last_login_time) order by rand() limit ? ";
			return jdbcTemplate.query(sql, new Object[] { uid, sex, day, count }, getEntityMapper());
		} else {
			String sql = "select u.user_id,u.nick_name,u.avatar from t_user u  where u.user_id<>? and u.type=1 and u.user_id not in (?) and u.sex=? and  DATE_SUB(CURDATE(), INTERVAL ? DAY) <= date(u.last_login_time) order by rand() limit ? ";
			return jdbcTemplate.query(sql, new Object[] { uid, withOutids, sex, day, count }, getEntityMapper());
		}
	}

//	public List<BaseUser> getActiveUserBySex(int sex, int days, int count) {
//		String sql = "select u.user_id,u.nick_name,u.avatar from t_user u  where   u.sex=? and  DATE_SUB(CURDATE(), INTERVAL ? DAY) <= date(u.last_login_time) order by rand() limit ? ";
//		return jdbcTemplate.query(sql, new Object[] { sex, days, count },
//				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
//	}

	// 获取当前需要匹配的男性账号总数
	public int getNeedMatchManCount(int days) {
		String sql = "select count(*)  from t_user u  where u.sex=1 and (u.type=1 or u.type=3) and u.user_id not in (select uid from t_user_match where  to_days(match_time) = to_days(now()))  and  DATE_SUB(CURDATE(), INTERVAL ? DAY) <= date(u.last_login_time)";
		return jdbcTemplate.queryForObject(sql, new Object[] { days }, Integer.class);
	}

	// 获取今天没做匹配的男性账号
	public List<BaseUser> getActiveManToMatch(int days, int startIndex, int count) {
		String sql = "select u.user_id,u.nick_name,u.type,u.last_login_time ,u.sex ,u.avatar from t_user u "
				+ " where u.sex=1 and (u.type=1 or u.type=3)"
				+ " and u.user_id not in (select uid from t_user_match where  to_days(match_time) = to_days(now())) "
				+ " and  DATE_SUB(CURDATE(), INTERVAL ? DAY) <= date(u.last_login_time) order by u.last_login_time desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { days, startIndex, count }, getEntityMapper());
	}

	// 获取被匹配的女性账号
	public List<BaseUser> getActiveWomenUserNotDoMatch(int count) {
		String sql = "select u.user_id,u.nick_name,u.type,u.last_login_time ,u.sex ,u.avatar from t_user u "
				+ " where u.sex=0 and (u.type=1 or u.type=3)"
				+ " and u.user_id not in (select target_uid from t_user_match where  to_days(match_time) = to_days(now())) "
				+ " order by rand() limit ?";
		return jdbcTemplate.query(sql, new Object[] { count }, getEntityMapper());
	}

	public List<Long> getActiveWomenUserNotDoMatchUids(int count) {
		String sql = "select u.user_id from t_user u " + " where u.sex=0 and (u.type=1 or u.type=3)"
				+ " and u.user_id not in (select target_uid from t_user_match where  to_days(match_time) = to_days(now())) "
				+ " order by u.last_login_time desc limit ?";
		return jdbcTemplate.queryForList(sql, new Object[] { count }, Long.class);
	}

	public void addDeviceToken(long user_id, String device_token) {
		String sql = "insert into t_user_device_token values (?, ?)";
		try {
			jdbcTemplate.update(sql, new Object[] { user_id, device_token });
		} catch (Exception e) {
		}
	}

	public void saveMatchLog(long uid, long target_uid) {
		String sql = "insert into t_user_match values (?, ?,?)";
		jdbcTemplate.update(sql, new Object[] { uid, target_uid, new Date() });
	}

	public void close(long uid) {

//		jdbcTemplate.update("delete from t_bottle_scan where user_id="+uid);//鍒犻櫎鐡跺瓙
//		jdbcTemplate.update("delete from t_check_in_record where uid="+uid);//鍒犻櫎绛惧埌
//		jdbcTemplate.update("delete from t_coin_exchange where uid="+uid);//鍒犻櫎浜ゆ槗淇℃伅
//		jdbcTemplate.update("delete from t_contact_get_rel where uid="+uid+" or target_uid="+uid);//鍒犻櫎鑾峰彇鑱旂郴鏂瑰紡鐨勮褰�

		jdbcTemplate.update("update t_user set account_state=? where user_id=?",
				new Object[] { AccountStateType.CLOSE.ordinal(), uid });

	}

	public int updateAvatarIsFace(long userId, int isFace) {
		return jdbcTemplate.update("update t_user set isFace=? where user_id=?", new Object[] { isFace, userId });
	}

	public int updateAvatarState(int id, int state) {
		return jdbcTemplate.update("update t_user_avatars set state=? where id=?", new Object[] { state, id });
	}

	public void clearUserMatchData() {
		jdbcTemplate.update("delete  from t_user_match where DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(match_time)");
	}

	public int updateUserBirthCity(long user_id, int city_id) {
		String sql = "update t_user set birth_city_id=? where user_id=?";
		return jdbcTemplate.update(sql, new Object[] { city_id, user_id });
	}

	public int getSexModifyTimes(long uid) {
		int exist = jdbcTemplate.queryForObject("select count(*) from t_sex_modify_history where uid=" + uid,
				Integer.class);
		if (exist == 1) {
			return jdbcTemplate.queryForObject("select modify_times from t_sex_modify_history where uid=" + uid,
					Integer.class);
		} else {
			return -1;
		}
	}

	public void updateModifySexTimes(long user_id) {
		int times = getSexModifyTimes(user_id);
		if (times > 0) {
			jdbcTemplate.update("update t_sex_modify_history set modify_times=?,last_modify_time=? where uid=?",
					new Object[] { times + 1, new Date(), user_id });
		} else {
			jdbcTemplate.update(
					"insert ignore into  t_sex_modify_history (uid,modify_times,last_modify_time) values(?,?,?)",
					new Object[] { user_id, 1, new Date() });
		}
	}

	public String getOpenIdByUid(long user_id) {
		return jdbcTemplate.queryForObject("select openid from t_user where user_id=" + user_id, String.class);
	}

	public void updateBaseInfo(long user_id, String nick_name, String avatar, Date birthday, City city) {

		StringBuilder sb = new StringBuilder("update t_user set ");

		List<Object> params = new ArrayList<Object>();
		if (!TextUtils.isEmpty(nick_name)) {
			sb.append(" nick_name=?,");
			params.add(nick_name);
		}
		if (!TextUtils.isEmpty(avatar)) {
			sb.append(" avatar=?,");
			params.add(avatar);
		}
		if (birthday != null) {
			sb.append(" birthday=?,");
			params.add(birthday);
		}
		if (city != null) {
			sb.append(" birth_city_id=?,");
			params.add(city.getId());
		}
		CharSequence chars = sb.subSequence(0, sb.length() - 1);
		sb.setLength(0);
		sb.append(chars);
		sb.append(" where user_id=?");

		params.add(user_id);
		Object[] paramsObjs = params.toArray();
		jdbcTemplate.update(sb.toString(), paramsObjs);
	}

	public int getUnlockChatCount(long user_id, long target_uid) {
		String sql = "select count(*) from t_user_unlock_chat where uid=? and target_uid=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { user_id, target_uid }, Integer.class);
	}

	public void unlockChat(long user_id, long target_uid) {
		String sql = "insert ignore into t_user_unlock_chat(uid,target_uid,create_time) values(?,?,?)";
		jdbcTemplate.update(sql, new Object[] { user_id, target_uid, new Date() });
	}

	public void addMeili(long user_id, int addMeiLi) {
		jdbcTemplate.update("update " + getTableName() + " set meili=meili+? where user_id=?",
				new Object[] { addMeiLi, user_id });
	}

	public void updateUserVipVal(long user_id, boolean isVip) {
		jdbcTemplate.update("update " + getTableName() + " set isvip=? where user_id=?",
				new Object[] { isVip ? 1 : 0, user_id });
	}

	public void updateUserLocation(long uid, String lat, String lng) {
		// TODO Auto-generated method stub
		jdbcTemplate.update("update t_user set lat=?,lng=? where user_id=?",new Object[] {lat,lng,uid});
	}
}
