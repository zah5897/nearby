package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.BottleExpress;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.BaseVipUser;
import com.zhan.app.nearby.bean.user.LocationUser;
import com.zhan.app.nearby.comm.BottleState;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("bottleDao")
public class BottleDao extends BaseDao {
	public static final String TABLE_BOTTLE = "t_bottle";
	public static final String TABLE_BOTTLE_POOL = "t_bottle_pool";
	public static final String TABLE_BOTTLE_COMMENT = "t_bottle_comment";
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private VipDao vipDao;

	// ---------------------------------------bottle-------------------------------------------------
	public long insert(Bottle bottle) {
		long id = saveObj(jdbcTemplate, TABLE_BOTTLE, bottle);
		bottle.setId(id);
		return id;
	}

	public Bottle getBottleById(long id) {
		String sql = "select bottle.*,user.nick_name,user.gender,user.avatar from " + TABLE_BOTTLE
				+ " bottle left join t_user user on bottle.user_id=user.id where bottle.id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { id }, new BeanPropertyRowMapper<Bottle>(Bottle.class) {

			@Override
			public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Bottle bottle = super.mapRow(rs, rowNumber);
				bottle.setSender(resultSetToUser(rs));
				return bottle;
			}

		});
	}

	public int insertToPool(Bottle bottle) {
		String sql = "insert into " + TABLE_BOTTLE_POOL + " (bottle_id,user_id,type,create_time) values (?,?,?,?)";
		return jdbcTemplate.update(sql,
				new Object[] { bottle.getId(), bottle.getUser_id(), bottle.getType(), bottle.getCreate_time() });
	}

	public List<Bottle> getBottleRandomInPool(long user_id, int limit) {
		String sql = "select b.*,u.nick_name ,u.gender,u.avatar from t_bottle as b right join (select  * from t_bottle_pool where user_id<>? order by rand() limit ?) as p on b.id=p.bottle_id left join t_user u on b.user_id=u.id";
		return jdbcTemplate.query(sql, new Object[] { user_id, limit },
				new BeanPropertyRowMapper<Bottle>(Bottle.class) {

					@Override
					public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
						Bottle bottle = super.mapRow(rs, rowNumber);
						bottle.setSender(resultSetToUser(rs));
						return bottle;
					}

				});
	}

	public List<Bottle> getBottles(long user_id, int limit, Integer type, BottleState state) {

		BeanPropertyRowMapper<Bottle> mapper = new BeanPropertyRowMapper<Bottle>(Bottle.class) {
			@Override
			public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Bottle bottle = super.mapRow(rs, rowNumber);
				LocationUser user = new LocationUser();
				user.setUser_id(rs.getLong("user_id"));
				user.setNick_name(rs.getString("nick_name"));
				user.setBirthday(rs.getDate("birthday"));
				user.setAvatar(rs.getString("avatar"));
				user.setSex(rs.getString("sex"));
				user.setAge(rs.getString("age"));
				user.setVip(vipDao.isVip(user.getUser_id()));
				ImagePathUtil.completeAvatarPath(user, true);
				bottle.setSender(user);

				int city_id = rs.getInt("birth_city_id");
				String cityName = rs.getString("city_name");
				if (!TextUtils.isEmpty(cityName)) {
					City city = new City();
					city.setId(city_id);
					city.setName(cityName);
					user.setBirth_city(city);
				}
				return bottle;
			}
		};
		if (type == null) {

			if (state == BottleState.NORMAL) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.state<>? "
						+ fiflterBlock() + "  order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.BLACK.ordinal(), user_id,
						Relationship.BLACK.ordinal(), limit }, mapper);
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.state=? "
						+ fiflterBlock() + "  order by RAND()  limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, state.ordinal(), user_id, Relationship.BLACK.ordinal(), limit },
						mapper);
			}

		} else {

			if (state == BottleState.NORMAL) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.type=? and b.state<>? "
						+ fiflterBlock() + " order by RAND()  limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, type, BottleState.BLACK.ordinal(), user_id, Relationship.BLACK.ordinal(), limit },
						mapper);
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.type=? and b.state=? "
						+ fiflterBlock() + " order by RAND()  limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, type, state.ordinal(), user_id, Relationship.BLACK.ordinal(), limit },
						mapper);
			}

		}

	}

	private String fiflterBlock() {
		return " and p.user_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) ";
	}

	public List<Bottle> getBottlesByGender(long user_id, int limit, int gender, Integer type, BottleState state) {

		BeanPropertyRowMapper<Bottle> mapper = new BeanPropertyRowMapper<Bottle>(Bottle.class) {
			@Override
			public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Bottle bottle = super.mapRow(rs, rowNumber);
				LocationUser user = new LocationUser();
				user.setUser_id(rs.getLong("user_id"));
				user.setNick_name(rs.getString("nick_name"));
				user.setBirthday(rs.getDate("birthday"));
				user.setAvatar(rs.getString("avatar"));
				user.setSex(rs.getString("sex"));
				user.setAge(rs.getString("age"));
				user.setVip(vipDao.isVip(user.getUser_id()));
				ImagePathUtil.completeAvatarPath(user, true);
				bottle.setSender(user);

				int city_id = rs.getInt("birth_city_id");
				String cityName = rs.getString("city_name");
				if (!TextUtils.isEmpty(cityName)) {
					City city = new City();
					city.setId(city_id);
					city.setName(cityName);
					user.setBirth_city(city);
				}

				return bottle;
			}
		};
		if (type == null) {
			// 固定性别
			if (gender == 0 || gender == 1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.state=?   and u.sex=? order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), gender, limit }, mapper);
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.state=? order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), limit }, mapper);
			}

		} else {
			// 固定性别
			if (gender == 0 || gender == 1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>?  and b.type=? and b.state=? and u.sex=? order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, type, state.ordinal(), gender, limit }, mapper);
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.type=? and b.state=?   order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, type, state.ordinal(), limit }, mapper);
			}

		}

	}

	public boolean exist(long bottle_id) {
		String sql = "select count(*) from t_bottle where id=?";
		int count = jdbcTemplate.queryForObject(sql, new Object[] { bottle_id }, Integer.class);
		return count > 0;
	}

	public int removeFromPool(long id) {
		String sql = "delete from " + TABLE_BOTTLE_POOL + " where bottle_id=?";
		return jdbcTemplate.update(sql, new Object[] { id });
	}

	public boolean existBottles(String content, String img) {
		return jdbcTemplate.queryForObject("select count(*) from t_bottle where content=? and image=?",
				new Object[] { content, img }, Integer.class) > 0;
	}

	private BaseVipUser resultSetToUser(ResultSet rs) throws SQLException {
		BaseVipUser sender = new BaseVipUser();
		sender.setUser_id(rs.getLong("user_id"));
		sender.setNick_name(rs.getString("nick_name"));
		sender.setAvatar(rs.getString("avatar"));
		sender.setVip(vipDao.isVip(sender.getUser_id()));
		return sender;
	}

	public List<Bottle> getMineBottles(long user_id, long last_id, int page_size) {
		long real_last_id = last_id <= 0 ? Integer.MAX_VALUE : last_id;
		String sql = "select bottle.*,coalesce(bs.view_nums,0) as view_nums from " + TABLE_BOTTLE
				+ " bottle left join t_bottle_scan_nums bs on bottle.id=bs.bottle_id "
				+ "  where bottle.user_id=? and bottle.id<? and bottle.type<>? order by bottle.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { user_id, real_last_id, BottleType.MEET.ordinal(), page_size },
				new BeanPropertyRowMapper<Bottle>(Bottle.class));
	}

	public int delete(long user_id, long bottle_id) {
		jdbcTemplate.update("delete from " + TABLE_BOTTLE_POOL + " where user_id=? and bottle_id=?",
				new Object[] { user_id, bottle_id });
		return jdbcTemplate.update("delete from " + TABLE_BOTTLE + " where user_id=? and id=?",
				new Object[] { user_id, bottle_id });
	}

	public int logScan(long user_id, long bottle_id) {
		String sql = "insert into t_bottle_scan (bottle_id,user_id,create_time)  values (?,?,?)";
		return jdbcTemplate.update(sql, new Object[] { bottle_id, user_id, new Date() });
	}

	public List<BaseUser> getScanUserList(long bottle_id, int limit) {
		String sql = "select user.user_id,user.nick_name,user.avatar,user.sex from t_bottle_scan scan left join t_user user on scan.user_id=user.user_id where scan.bottle_id=? order by scan.create_time limit ?";
		return jdbcTemplate.query(sql, new Object[] { bottle_id, limit },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	/**
	 * 获取用户浏览总数
	 * 
	 * @param bottle_id
	 * @return
	 */
	public int getScanUserCount(long bottle_id) {
		String sql = "select count(*) from t_bottle_scan scan left join t_user user on scan.user_id=user.user_id where scan.bottle_id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { bottle_id }, Integer.class);
	}

	/**
	 * 根据性别随机获取头像不为空的用户
	 * 
	 * @param limit
	 * @param gender
	 * @return
	 */
	public List<BaseUser> getRandomScanUserList(int limit, int gender) {
		String sql = "select user_id,nick_name,avatar,sex from t_user where avatar<>? and sex=? order by  rand() limit ?";
		return jdbcTemplate.query(sql, new Object[] { "", gender, limit },
				new BeanPropertyRowMapper<BaseUser>(BaseUser.class));
	}

	private String getAgeSql() {
		return " (year(now())-year(u.birthday)-1) + ( DATE_FORMAT(u.birthday, '%m%d') <= DATE_FORMAT(NOW(), '%m%d') ) as age ";
	}

	public boolean isExistMeetTypeBottle(long user_id) {
		int pool_count = jdbcTemplate.queryForObject("select count(*) from t_bottle_pool where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() }, Integer.class);
		int bottle_count = jdbcTemplate.queryForObject("select count(*) from t_bottle where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() }, Integer.class);
		return pool_count > 0 || bottle_count > 0;
	}

	public List<Long> getMeetBottleIDByUser(long user_id) {
		return jdbcTemplate.queryForList("select id from t_bottle where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() }, Long.class);
	}

	public int insertExpress(BottleExpress express) {
		return saveObjSimple(jdbcTemplate, "t_user_express", express);
	}

	public int clearExpireAudioBottle(int maxValidate) {
		String sql = "delete from t_bottle_pool where  type=? and round((UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(create_time))/60)>=?";
		return jdbcTemplate.update(sql, new Object[] { BottleType.VOICE.ordinal(), maxValidate });
	}

	public int hasSend(String id) {
		int count = jdbcTemplate.queryForObject("select count(*) from t_auto_text_bottle where id=?",
				new String[] { id }, Integer.class);
		return count;
	}

	public int insertAutoSendTextBottle(String id) {
		int r = jdbcTemplate.update("insert into t_auto_text_bottle (id) values(?)", new Object[] { id });
		return r;
	}

	public long getRandomUidToSendAutoBottle() {
		List<Long> rs = jdbcTemplate.queryForList("select *from t_auto_send_bottle_users ORDER BY RAND() LIMIT 1",
				Long.class);
		if (rs.size() > 0) {
			return rs.get(0);
		}
		return -1;
	}

	public List<Bottle> getBottlesByState(int state, int pageSize, int pageIndex) {
		String sql = "select b.*,u.nick_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where b.state=? order by p.create_time  desc limit ?,?";
		if (state == -1) {
			sql = "select b.*,u.nick_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where b.state<>? order by p.create_time desc limit ?,?";
		}
		return jdbcTemplate.query(sql, new Object[] { state, (pageIndex - 1) * pageSize, pageSize },
				new BeanPropertyRowMapper<Bottle>(Bottle.class) {
					@Override
					public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
						Bottle b = super.mapRow(rs, rowNumber);

						BaseUser u = new BaseUser();
						u.setUser_id(rs.getLong("user_id"));
						u.setNick_name(rs.getString("nick_name"));
						b.setSender(u);
						return b;
					}
				});
	}

	public int getBottleCountWithState(int state) {
		String sql = "select count(*) from t_bottle_pool p left join t_bottle b on p.bottle_id=b.id where b.state=?";
		if (state == -1) {
			sql = "select count(*) from t_bottle_pool p left join  t_bottle  b on p.bottle_id=b.id where b.state<>?";
		}
		return jdbcTemplate.queryForObject(sql, new Object[] { state }, Integer.class);
	}

	public int changeBottleState(int id, int to_state) {
		return jdbcTemplate.update("update t_bottle set state=? where id=?", new Object[] { to_state, id });
	}

}
