package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.RedPackageGetHistory;
import com.zhan.app.nearby.bean.Reward;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.MeetListUser;
import com.zhan.app.nearby.comm.BottleAnswerState;
import com.zhan.app.nearby.comm.BottleState;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.MsgState;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.PropertyMapperUtil;

@Repository("bottleDao")
public class BottleDao extends BaseDao<Bottle> {
	public static final String TABLE_BOTTLE_POOL = "t_bottle_pool";
	@Resource
	private VipDao vipDao;

	@Resource
	private CityDao cityDao;
	@Resource
	private UserDao userDao;

	public static final int BOTTLE_LIMIT_COUNT = 150;

	public Bottle getBottleById(long id) {
		String sql = "select bottle.*,user.nick_name,user.sex,user.avatar,user.isvip from " + getTableName()
				+ " bottle left join t_user user on bottle.user_id=user.user_id where bottle.id=?";
		List<Bottle> bottles = jdbcTemplate.query(sql, new Object[] { id },
				new BeanPropertyRowMapper<Bottle>(Bottle.class) {
					@Override
					public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
						Bottle bottle = super.mapRow(rs, rowNumber);
						bottle.setSender(resultSetToUser(rs));
						return bottle;
					}
				});
		if (bottles.size() > 0) {
			return bottles.get(0);
		} else {
			return null;
		}
	}

	public Bottle getBottleByRand() {
		String sql = "select bottle.*,user.nick_name,user.sex,user.avatar,u.isvip from " + getTableName()
				+ " bottle left join t_user user on bottle.user_id=user.user_id  order by rand() limit 1";
		List<Bottle> bottles = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Bottle>(Bottle.class) {
			@Override
			public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Bottle bottle = super.mapRow(rs, rowNumber);
				bottle.setSender(resultSetToUser(rs));
				return bottle;
			}
		});
		if (bottles.size() > 0) {
			return bottles.get(0);
		} else {
			return null;
		}
	}

	public Bottle getBottle(long id) {
		String sql = "select * from " + getTableName() + " where id=?";
		List<Bottle> bottles = jdbcTemplate.query(sql, new Object[] { id }, getSimpleBottleMapper());
		if (bottles.isEmpty()) {
			return null;
		}
		return bottles.get(0);
	}

	public Bottle getMeetBottleByUserId(long user_id) {
		String sql = "select b.*,u.nick_name,u.avatar,u.isvip from t_bottle  b left join t_user u on b.user_id=u.user_id where b.user_id=? and b.type=?";
		List<Bottle> bottles = jdbcTemplate.query(sql, new Object[] { user_id, BottleType.MEET.ordinal() },
				new BeanPropertyRowMapper<Bottle>(Bottle.class) {

					@Override
					public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
						Bottle bottle = super.mapRow(rs, rowNumber);
						bottle.setSender(resultSetToUser(rs));
						return bottle;
					}
				});

		if (bottles.isEmpty()) {
			return null;
		}
		return bottles.get(bottles.size() - 1);
	}

	public Bottle getBottleBySenderAndType(long user_id, int type) {
		String sql = "select * from " + getTableName() + " where user_id=? and type=?";
		List<Bottle> ids = jdbcTemplate.query(sql, new Object[] { user_id, type }, getSimpleBottleMapper());
		if (ids.isEmpty()) {
			return null;
		}
		return ids.get(0);
	}

	public int insertToPool(Bottle bottle) {
		String sql = "insert into " + TABLE_BOTTLE_POOL + " (bottle_id,user_id,type,create_time) values (?,?,?,?)";
		return jdbcTemplate.update(sql,
				new Object[] { bottle.getId(), bottle.getUser_id(), bottle.getType(), bottle.getCreate_time() });
	}

	public Long getBottleSenderId(long bottle_id) {
		return jdbcTemplate.queryForObject("select user_id from t_bottle where id=? limit 1",
				new Object[] { bottle_id }, Long.class);
	}

	public List<Bottle> getBottleRandomInPool(long user_id, int limit) {
		String sql = "select b.*,u.nick_name ,u.gender,u.avatar,u.isvip, from t_bottle as b right join (select  * from t_bottle_pool where user_id<>? order by rand() limit ?) as p on b.id=p.bottle_id left join t_user u on b.user_id=u.id";
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

	// 最新版本支持
	public List<Bottle> getBottlesLeastVersion(long user_id, Integer sex, int limit, int type) {
		String sexCondition = sex == null ? "" : " and u.sex=" + sex;
		if (type == -1) {
			String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
					+ " u.sex,u.isvip ,c.name as city_name from t_bottle_pool p "
					+ " left join  t_bottle b  on p.bottle_id=b.id " + "left join t_user u on b.user_id=u.user_id "
					+ " left join t_sys_city c on u.birth_city_id=c.id "
					+ " where u.avatar<>'illegal.jpg' and  b.state<>?  and b.user_id<>?  and b.type<>? and b.type<>?  "
					+ sexCondition + " order by  rand()  limit ?";
			return jdbcTemplate.query(sql, new Object[] { BottleState.BLACK.ordinal(), user_id,
					BottleType.DM_TXT.ordinal(), BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
		} else {
			String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
					+ " u.sex ,u.isvip,c.name as city_name from t_bottle_pool p "
					+ " left join  t_bottle b  on p.bottle_id=b.id " + "left join t_user u on b.user_id=u.user_id "
					+ " left join t_sys_city c on u.birth_city_id=c.id "
					+ " where u.avatar<>'illegal.jpg'  and b.state<>?  and b.user_id<>?   and b.type=? " + sexCondition
					+ " order by  rand()  limit ?";
			return jdbcTemplate.query(sql, new Object[] { BottleState.BLACK.ordinal(), user_id, type, limit },
					getBottleMapper());
		}
	}

	public List<Bottle> getBottlesIOS_REVIEW(long user_id, Integer sex, int limit, int type) {
		if (type != -1) {
			if (type == BottleType.DM_TXT.ordinal() || type == BottleType.DM_VOICE.ordinal()) {
				return new ArrayList<Bottle>();
			}
		}
		String sexCondition = sex == null ? "" : " and u.sex=" + sex;
		if (type == -1) {
			String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
					+ " u.sex,u.isvip ,c.name as city_name from t_bottle  b   left join t_user u on b.user_id=u.user_id "
					+ " left join t_sys_city c on u.birth_city_id=c.id"
					+ "  where u.avatar<>'illegal.jpg' and  b.user_id<>? and b.state=?  and b.type<>? and b.type<>? "
					+ sexCondition + "  order by  rand()   limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.IOS_REVIEW.ordinal(),
					BottleType.DM_TXT.ordinal(), BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
		} else if (type == BottleType.VOICE.ordinal()) {
			String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
					+ " u.sex,u.isvip ,c.name as city_name from t_bottle  b   left join t_user u on b.user_id=u.user_id "
					+ " left join t_sys_city c on u.birth_city_id=c.id"
					+ "  where u.avatar<>'illegal.jpg' and  b.user_id<>? and b.state=?  and b.typ=>? " + sexCondition
					+ " and  DATEDIFF(b.create_time,now()) <-7 order by  rand()   limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.IOS_REVIEW.ordinal(), type, limit },
					getBottleMapper());
		} else if (type == BottleType.DRAW_GUESS.ordinal()) {
			String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
					+ " u.sex ,u.isvip,c.name as city_name from t_bottle  b   left join t_user u on b.user_id=u.user_id "
					+ " left join t_sys_city c on u.birth_city_id=c.id"
					+ "  where u.avatar<>'illegal.jpg' and  b.user_id<>? and b.state=?  and b.type=? " + sexCondition
					+ " and    b.answer_state=?  order by  rand()   limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.IOS_REVIEW.ordinal(), type,
					BottleAnswerState.NORMAL.ordinal(), limit }, getBottleMapper());
		} else {
			String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
					+ " u.sex ,u.isvip,c.name as city_name from t_bottle  b   left join t_user u on b.user_id=u.user_id "
					+ " left join t_sys_city c on u.birth_city_id=c.id"
					+ "  where u.avatar<>'illegal.jpg' and  b.user_id<>? and b.state=?  and b.type=? " + sexCondition
					+ "    order by  rand()   limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.IOS_REVIEW.ordinal(), type, limit },
					getBottleMapper());
		}
	}

	/**
	 * 获取最近5分钟的 弹幕瓶子
	 * 
	 * @param user_id
	 * @param limit
	 * @param type
	 * @param state
	 * @return
	 */
	public List<Bottle> getLatestDMBottles(long user_id, int limit, int type, BottleState state, int timeType) {
		if (type != -1) {
			if (type != BottleType.DM_TXT.ordinal() || type != BottleType.DM_VOICE.ordinal()) {
				return new ArrayList<Bottle>();
			}
		}
		if (state == BottleState.NORMAL) {
			if (type == -1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
						+ " u.sex ,u.isvip,c.name as city_name from t_bottle_pool p "
						+ " left join  t_bottle b  on p.bottle_id=b.id " + " left join t_user u on b.user_id=u.user_id "
						+ " left join t_sys_city c on u.birth_city_id=c.id"

						+ " where b.user_id<>? and  u.avatar<>'illegal.jpg' and  b.state<>? and (b.type=? or b.type=?) "
						+ fiflterHadGetWithout(user_id, timeType) + "  order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.BLACK.ordinal(),
						BottleType.DM_TXT.ordinal(), BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
						+ " u.sex ,u.isvip,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id "
						+ " left join t_sys_city c on u.birth_city_id=c.id "
						+ " where b.user_id<>? and  u.avatar<>'illegal.jpg' and b.state<>? and b.type=? "
						+ fiflterHadGetWithout(user_id, timeType) + " order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.BLACK.ordinal(), type, limit },
						getBottleMapper());
			}
		} else {
			if (type == -1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
						+ " u.sex ,u.isvip,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id "
						+ " left join t_sys_city c on u.birth_city_id=c.id"
						+ "  where  b.user_id<>? and u.avatar<>'illegal.jpg' and   b.state=?  and (b.type=? or b.type=?) "
						+ fiflterHadGetWithout(user_id, timeType) + "  order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), BottleType.DM_TXT.ordinal(),
						BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id,"
						+ " u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id "
						+ " left join t_sys_city c on u.birth_city_id=c.id "
						+ " where b.user_id<>? and  u.avatar<>'illegal.jpg' and   b.state=?  and b.type=? "
						+ fiflterHadGetWithout(user_id, timeType) + " order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), type, limit },
						getBottleMapper());
			}
		}

	}

	private String fiflterHadGetWithout(long user_id, int timeType) {
		if (timeType == 0) {
			return " and p.bottle_id not in (select bid from t_dm_bottle_had_get where uid=" + user_id + ") ";
		} else {
			return " and p.create_time >= now()-interval 30 day ";
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

	private BaseUser resultSetToUser(ResultSet rs) throws SQLException {
		BaseUser sender = new BaseUser();
		sender.setUser_id(rs.getLong("user_id"));
		sender.setNick_name(rs.getString("nick_name"));
		sender.setAvatar(rs.getString("avatar"));
		sender.setIsvip(rs.getInt("isvip"));
		ImagePathUtil.completeAvatarPath(sender, true);
		return sender;
	}

	public List<Bottle> getMineBottles(long user_id, int page, int page_size) {
		String sql = "select b.*,count(b.id) as view_nums from t_bottle b left join t_bottle_scan s on b.id=s.bottle_id where b.user_id=? and b.type<>?  group by b.id order by b.id desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { user_id, BottleType.MEET.ordinal(), (page - 1) * page_size, page_size },
				getSimpleBottleMapper());
	}

	public int delete(long user_id, long bottle_id) {
		jdbcTemplate.update("delete from " + TABLE_BOTTLE_POOL + " where user_id=? and bottle_id=?",
				new Object[] { user_id, bottle_id });
		return jdbcTemplate.update("delete from " + getTableName() + " where user_id=? and id=?",
				new Object[] { user_id, bottle_id });
	}

	public int logScan(long user_id, long bottle_id) {
		String sql = "insert ignore  into t_bottle_scan (bottle_id,user_id,create_time)  values (?,?,?)";
		return jdbcTemplate.update(sql, new Object[] { bottle_id, user_id, new Date() });
	}

	public List<BaseUser> getScanUserList(long bottle_id, int limit) {
		String sql = "select user.user_id,user.nick_name,user.avatar,user.sex,user.isvip from t_bottle_scan scan left join t_user user on scan.user_id=user.user_id where scan.bottle_id=? order by scan.create_time limit ?";
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

	public boolean checkExistMeetBottleAndReUse(long user_id) {
		int pool_count = jdbcTemplate.queryForObject("select count(*) from t_bottle_pool where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() }, Integer.class);

		int bottle_count = jdbcTemplate.queryForObject("select count(*) from t_bottle where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() }, Integer.class);
		if (pool_count > 0) {
			return false;
		}

		if (bottle_count == 0) {
			return true;
		}
		if (bottle_count > 0) {
			Bottle b = getBottleBySenderAndType(user_id, BottleType.MEET.ordinal());
			if (b != null) {
				b.setCreate_time(new Date());
				insertToPool(b);
				return false;
			}
		}
		return true;
	}

	public List<Long> getMeetBottleIDByUser(long user_id) {
		return jdbcTemplate.queryForList("select id from t_bottle where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() }, Long.class);
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

	public List<Bottle> getBottlesByState(int state, int pageSize, int pageIndex, long bottle_id) {

		Object[] param;
		String sql;
		if (bottle_id > 0) {
			sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where b.id=? and  b.state=? and b.type<>? order by p.create_time  desc limit ?,?";
			if (state == -1) {
				sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where b.id=? and  b.state<>? and b.type<>? order by p.create_time desc limit ?,?";
			}
			param = new Object[] { bottle_id, state, BottleType.MEET.ordinal(), (pageIndex - 1) * pageSize, pageSize };
		} else {
			sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where   b.state=? and b.type<>? order by p.create_time  desc limit ?,?";
			if (state == -1) {
				sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where     b.state<>? and b.type<>? order by p.create_time desc limit ?,?";
			}
			param = new Object[] { state, BottleType.MEET.ordinal(), (pageIndex - 1) * pageSize, pageSize };
		}

		return jdbcTemplate.query(sql, param, new BeanPropertyRowMapper<Bottle>(Bottle.class) {
			@Override
			public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Bottle b = super.mapRow(rs, rowNumber);
				BaseUser u = new BaseUser();
				u.setUser_id(rs.getLong("user_id"));
				u.setNick_name(rs.getString("nick_name"));
				u.setAvatar(rs.getString("avatar"));
				ImagePathUtil.completeAvatarPath(u, true);
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

	private BeanPropertyRowMapper<Bottle> getBottleMapper() {
		return new BeanPropertyRowMapper<Bottle>(Bottle.class) {
			@Override
			public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Bottle bottle = super.mapRow(rs, rowNumber);
				BaseUser user = new BaseUser();
				user.setUser_id(rs.getLong("user_id"));
				user.setNick_name(rs.getString("nick_name"));
				user.setBirthday(rs.getDate("birthday"));
				user.setAvatar(rs.getString("avatar"));
				user.setSex(rs.getString("sex"));
				user.setIsvip(rs.getInt("isvip"));
				ImagePathUtil.completeAvatarPath(user, true);
				bottle.setSender(user);

				if (bottle.getType() == BottleType.DRAW_GUESS.ordinal()) {
					ImagePathUtil.completeBottleDrawPath(bottle);
				}
				int b_city = rs.getInt("birth_city_id");
				int c_city = rs.getInt("city_id");
				user.setBirth_city(cityDao.getCityById(b_city));
				user.setCity(cityDao.getCityById(c_city));
				return bottle;
			}
		};
	}

	public int markDMBottleHadGet(long user_id, long bottle_id) {
		try {
			return jdbcTemplate.update("insert ignore  into t_dm_bottle_had_get (uid,bid) values(?,?)",
					new Object[] { user_id, bottle_id });
		} catch (Exception e) {
		}
		return 0;
	}

	public List<MeetListUser> getMeetList(long user_id, int page, int count) {
		String sql = "select msg.create_time, u.user_id,u.nick_name,u.avatar,u.sex ,u.type ,u.isvip from t_dynamic_msg msg "
				+ "left join t_user u on msg.by_user_id=u.user_id "
				+ "where msg.user_id=? and msg.type=? and msg.isReadNum=? order by msg.create_time desc limit ?,?";
		return jdbcTemplate
				.query(sql,
						new Object[] { user_id, DynamicMsgType.TYPE_MEET.ordinal(), MsgState.NUREAD.ordinal(),
								(page - 1) * count, count },
						new BeanPropertyRowMapper<MeetListUser>(MeetListUser.class));
	}

	public List<Long> getExistTxtBottle(long user_id, String content) {
		String sql = "select id from t_bottle where user_id=? and type=? and content=?";
		return jdbcTemplate.queryForList(sql, new Object[] { user_id, BottleType.TXT.ordinal(), content }, Long.class);
	}

	public int deleteFromPool(long id) {
		return jdbcTemplate.update("delete from t_bottle_pool where bottle_id=" + id);
	}

	public int clearIllegalMeetBottle(long uid) {
		int c = jdbcTemplate.update("delete from t_bottle_pool where type=? and user_id=?",
				new Object[] { BottleType.MEET.ordinal(), uid });
		c = jdbcTemplate.update("delete from t_bottle where type=? and user_id=?",
				new Object[] { BottleType.MEET.ordinal(), uid });
		return c;
	}

	public int clearPoolBottleByUserId(long uid) {
		return jdbcTemplate.update("delete from t_bottle_pool where  user_id=?", new Object[] { uid });
	}

	public int clearBottleByUserId(long uid) {
		return jdbcTemplate.update("delete from t_bottle where  user_id=?", new Object[] { uid });
	}

	public List<String> loadAnswerToDraw(Integer count) {
		return jdbcTemplate.queryForList("select *from t_answer_to_draw order by rand() limit " + count, String.class);
	}

	public void insertAnswer(String answer) {
		jdbcTemplate.update("insert into t_answer_to_draw  (answer) values (?) ", new Object[] { answer });
	}

	public void updateAnswerState(long bottle_id, int ordinal) {
		jdbcTemplate.update("update t_bottle set answer_state=? where id=?", new Object[] { ordinal, bottle_id });
	}

	public List<Reward> rewardHistoryGroup(long user_id) {

		String rewardSql = "select count(*) as count,reward, "
				+ " bottle_id ,uid ,create_time,answer from t_reward_history where uid=? group by reward";

		String sqlReward = "select r.*,b.*,ru.nick_name,ru.avatar from (" + rewardSql
				+ ")  r left join t_bottle b on r.bottle_id=b.id left join t_user ru on r.uid=ru.user_id order by r.create_time desc";
		return jdbcTemplate.query(sqlReward, new Object[] { user_id }, new BeanPropertyRowMapper<Reward>(Reward.class));
	}

	public List<Reward> rewardHistory(long user_id, int page, int count) {
		String sqlReward = "select r.*,b.*,ru.nick_name,ru.avatar from t_reward_history r left join t_bottle b on r.bottle_id=b.id left join t_user ru on r.uid=ru.user_id where r.uid=?   order by r.create_time desc limit ?,?";
		return jdbcTemplate.query(sqlReward, new Object[] { user_id, (page - 1) * count, count },
				new BeanPropertyRowMapper<Reward>(Reward.class) {
					@Override
					public Reward mapRow(ResultSet rs, int rowNumber) throws SQLException {
						Reward r = super.mapRow(rs, rowNumber);
						Bottle b = (Bottle) PropertyMapperUtil.prase(Bottle.class, rs);
						b.setSender(userDao.getBaseUserNoToken(b.getUser_id()));
						ImagePathUtil.completeAvatarPath(b.getSender(), true);
						BaseUser u = new BaseUser();
						u.setUser_id(rs.getLong("uid"));
						u.setNick_name(rs.getString("nick_name"));
						u.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(u, true);
						ImagePathUtil.completeBottleDrawPath(b);
						r.setBottle(b);
						r.setUser(u);
						return r;
					}
				});
	}

	public void updateBottleContent(long id, String content) {
		jdbcTemplate.queryForList("update t_bottle set content=? where id=?", new Object[] { content, id });

	}

	public int getSizeByType(int type) {
		String sql = "select count(*) from t_bottle_pool where type=" + type;
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public long getLimitId(int type, int limit) {
		String sql = "select bottle_id from t_bottle_pool where type=? order by bottle_id desc limit ?,?";
		List<Long> ids = jdbcTemplate.queryForList(sql, new Object[] { type, limit - 1, 1 }, Long.class);
		if (ids.isEmpty()) {
			return 0;
		} else {
			return ids.get(0);
		}
	}

	public void removePoolBottleKeepSize(int type, long last_id) {
		String sql = "delete from t_bottle_pool   where  type=? and  bottle_id<?  order by bottle_id desc";
		jdbcTemplate.update(sql, new Object[] { type, last_id });
	}

	public void keepVoiceByDay(int day) {
		String sql = "delete  from t_bottle_pool  where type=?  and  TO_DAYS( NOW( ) ) - TO_DAYS( create_time) > ?";
		jdbcTemplate.update(sql, new Object[] { BottleType.VOICE.ordinal(), day });
		sql = "delete  from t_bottle  where type=?  and  TO_DAYS( NOW( ) ) - TO_DAYS( create_time) > ?";
		jdbcTemplate.update(sql, new Object[] { BottleType.VOICE.ordinal(), 8 });
	}

	public void keepRedPackageByDay(int day) {
		String sql = "delete  from t_bottle_pool  where type=?  and  TO_DAYS( NOW( ) ) - TO_DAYS( create_time) > ?";
		jdbcTemplate.update(sql, new Object[] { BottleType.RED_PACKAGE.ordinal(), day });
	}

	public void removeMeetBottle(long user_id) {
		jdbcTemplate.update("delete from t_bottle_pool where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() });
		jdbcTemplate.update("delete from t_bottle where user_id=? and type=?",
				new Object[] { user_id, BottleType.MEET.ordinal() });
	}

	public void updateRedPackage(String restCount, int count, int restCoin, long bid) {
		jdbcTemplate.update("update t_bottle set answer=? ,red_package_count=?,red_package_coin_rest=? where id=?",
				new Object[] { restCount, count, restCoin, bid });
	}

	public int saveRedPackageHistory(long uid, long bid, int coin) {
		return jdbcTemplate.update(
				"insert into t_redpackage_get_history (uid,bid,red_package_coin_get,create_time) values(?,?,?,?)",
				new Object[] { uid, bid, coin, new Date() });
	}

	public List<RedPackageGetHistory> getRedPackageHistoryByBid(long bid) {
		return jdbcTemplate.query(
				"select h.* ,u.nick_name,u.avatar from t_redpackage_get_history h left join t_user u on h.uid=u.user_id where h.bid=?",
				new Object[] { bid }, new BeanPropertyRowMapper<RedPackageGetHistory>(RedPackageGetHistory.class) {
					@Override
					public RedPackageGetHistory mapRow(ResultSet rs, int rowNumber) throws SQLException {
						RedPackageGetHistory h = super.mapRow(rs, rowNumber);
						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("uid"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						ImagePathUtil.completeAvatarPath(user, true);
						h.setUser(user);
						return h;
					}
				});
	}

	private BeanPropertyRowMapper<Bottle> getSimpleBottleMapper() {
		return new BeanPropertyRowMapper<Bottle>(Bottle.class) {
			@Override
			public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Bottle bottle = (Bottle) PropertyMapperUtil.prase(Bottle.class, rs);
				return bottle;
			}
		};
	}

}
