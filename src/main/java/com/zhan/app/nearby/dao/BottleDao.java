package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.BottleExpress;
import com.zhan.app.nearby.bean.Reward;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.BaseVipUser;
import com.zhan.app.nearby.bean.user.LocationUser;
import com.zhan.app.nearby.bean.user.MeetListUser;
import com.zhan.app.nearby.comm.BottleState;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.MsgState;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.PropertyMapperUtil;

@Repository("bottleDao")
public class BottleDao extends BaseDao {
	public static final String TABLE_BOTTLE = "t_bottle";
	public static final String TABLE_BOTTLE_POOL = "t_bottle_pool";
	public static final String TABLE_BOTTLE_COMMENT = "t_bottle_comment";
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private VipDao vipDao;

	@Resource
	private CityDao cityDao;
	@Resource
	private UserDao userDao;

	// ---------------------------------------bottle-------------------------------------------------
	public long insert(Bottle bottle) {
		long id = saveObj(jdbcTemplate, TABLE_BOTTLE, bottle);
		bottle.setId(id);
		return id;
	}

	public Bottle getBottleById(long id) {
		String sql = "select bottle.*,user.nick_name,user.sex,user.avatar from " + TABLE_BOTTLE
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

	public Bottle getBottle(long id) {
		String sql = "select * from " + TABLE_BOTTLE + " where id=?";
		List<Bottle> bottles = jdbcTemplate.query(sql, new Object[] { id },
				new BeanPropertyRowMapper<Bottle>(Bottle.class));
		if (bottles.isEmpty()) {
			return null;
		}
		return bottles.get(0);
	}

	public Bottle getBottleBySenderAndType(long user_id, int type) {
		String sql = "select * from " + TABLE_BOTTLE + " where user_id=? and type=?";
		List<Bottle> ids = jdbcTemplate.query(sql, new Object[] { user_id, type },
				new BeanPropertyRowMapper<Bottle>(Bottle.class));
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

	public List<Bottle> getBottles(long user_id, int limit, int type, BottleState state) {
		if (type != -1) {
			if (type == BottleType.DM_TXT.ordinal() || type == BottleType.DM_VOICE.ordinal()) {
				return new ArrayList<Bottle>();
			}
		}

		if (state == BottleState.NORMAL) {
			if (type == -1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id"
						+ " where b.user_id<>? and b.state<>? and b.type<>? and b.type<>? " + fiflterBlock(user_id)
						+ "  order by  rand()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.BLACK.ordinal(),
						BottleType.DM_TXT.ordinal(), BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id "
						+ " where b.user_id<>? and b.state<>? and b.type=?  " + fiflterBlock(user_id)
						+ " order by  rand()   limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.BLACK.ordinal(), type, limit },
						getBottleMapper());
			}
		} else {
			if (type == -1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id"
						+ "  where b.user_id<>? and b.state=?  and b.type<>? and b.type<>? " + fiflterBlock(user_id)
						+ "  order by  rand()   limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), BottleType.DM_TXT.ordinal(),
						BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id "
						+ " where b.user_id<>? and b.state=? and b.type=?  " + fiflterBlock(user_id)
						+ " order by  rand()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), type, limit },
						getBottleMapper());
			}
		}
	}

	// 此版本新增 “我画你猜”瓶子
	public List<Bottle> getBottlesV19(long user_id, int limit, int type, BottleState state) {
		if (type != -1) {
			if (type == BottleType.DM_TXT.ordinal() || type == BottleType.DM_VOICE.ordinal()) {
				return new ArrayList<Bottle>();
			}
		}

		if (state == BottleState.NORMAL) {
			if (type == -1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id"
						+ " where b.user_id<>? and b.state<>? and b.type<>? and b.type<>? and b.type<>? "
						+ fiflterBlock(user_id) + "  order by  rand()  limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, BottleState.BLACK.ordinal(), BottleType.DM_TXT.ordinal(),
								BottleType.DM_VOICE.ordinal(), BottleType.DRAW_GUESS.ordinal(), limit },
						getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id "
						+ " where b.user_id<>? and b.state<>? and b.type=?  and b.type=? " + fiflterBlock(user_id)
						+ " order by  rand()   limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, BottleState.BLACK.ordinal(), type,
						BottleType.DRAW_GUESS.ordinal(), limit }, getBottleMapper());
			}
		} else {
			if (type == -1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id"
						+ "  where b.user_id<>? and b.state=?  and b.type<>? and b.type<>? and b.type<>? "
						+ fiflterBlock(user_id) + "  order by  rand()   limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, state.ordinal(), BottleType.DM_TXT.ordinal(),
								BottleType.DM_VOICE.ordinal(), BottleType.DRAW_GUESS.ordinal(), limit },
						getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id "
						+ " where b.user_id<>? and b.state=? and b.type=? and b.type=? " + fiflterBlock(user_id)
						+ " order by  rand()  limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, state.ordinal(), type, BottleType.DRAW_GUESS.ordinal(), limit },
						getBottleMapper());
			}
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
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id"
						+ " where b.state<>? and (b.type=? or b.type=?) " + fiflterBlock(user_id)
						+ fiflterHadGetWithout(user_id, timeType) + "  order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { BottleState.BLACK.ordinal(), BottleType.DM_TXT.ordinal(),
						BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id "
						+ " where  b.state<>? and b.type=? " + fiflterBlock(user_id)
						+ fiflterHadGetWithout(user_id, timeType) + " order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { BottleState.BLACK.ordinal(), type, limit },
						getBottleMapper());
			}
		} else {
			if (type == -1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id"
						+ "  where   b.state=?  and (b.type=? or b.type=?) " + fiflterBlock(user_id)
						+ fiflterHadGetWithout(user_id, timeType) + "  order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { state.ordinal(), BottleType.DM_TXT.ordinal(),
						BottleType.DM_VOICE.ordinal(), limit }, getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id," + getAgeSql()
						+ ", u.sex ,c.name as city_name from t_bottle_pool p left join  t_bottle b  on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id "
						+ " where  b.state=?  and b.type=? " + fiflterBlock(user_id)
						+ fiflterHadGetWithout(user_id, timeType) + " order by RAND()  limit ?";
				return jdbcTemplate.query(sql, new Object[] { state.ordinal(), type, limit }, getBottleMapper());
			}
		}

	}

	private String fiflterBlock(long user_id) {
		return " and p.user_id not in (select with_user_id from t_user_relationship where user_id=" + user_id
				+ " and relationship=" + Relationship.BLACK.ordinal() + ") ";
	}

	private String fiflterHadGetWithout(long user_id, int timeType) {
		if (timeType == 0) {
			return " and p.bottle_id not in (select bid from t_dm_bottle_had_get where uid=" + user_id + ") ";
		} else {
			return " and p.create_time >= now()-interval 30 day ";
		}
	}

	// private String keepOutHadGet(long user_id) {
	// return " and p.bottle_id not in (select bid from t_had_get_bottle_rel where
	// uid="+user_id+") ";
	// }

	public List<Bottle> getBottlesByGender(long user_id, int limit, int gender, Integer type, BottleState state) {
		if (type == -1) {
			// 固定性别
			if (gender == 0 || gender == 1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.state=?   and u.sex=? order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), gender, limit },
						getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.state=? order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, state.ordinal(), limit }, getBottleMapper());
			}
		} else {
			// 固定性别
			if (gender == 0 || gender == 1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id, u.city_id," + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>?  and b.type=? and b.state=? and u.sex=? order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, type, state.ordinal(), gender, limit },
						getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.type=? and b.state=?   order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, type, state.ordinal(), limit },
						getBottleMapper());
			}
		}
	}

	// 新增对“我画你猜”瓶子类型的过滤
	public List<Bottle> getBottlesByGenderV19(long user_id, int limit, int gender, Integer type, BottleState state) {
		if (type == -1) {
			// 固定性别
			if (gender == 0 || gender == 1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.type<>? and b.state=?   and u.sex=? order by RAND() limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, BottleType.DRAW_GUESS.ordinal(), state.ordinal(), gender, limit },
						getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.type<>? and b.state=? order by RAND() limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, BottleType.DRAW_GUESS.ordinal(), state.ordinal(), limit },
						getBottleMapper());
			}
		} else {
			// 固定性别
			if (gender == 0 || gender == 1) {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id, u.city_id," + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>?  and b.type=? and b.state=? and u.sex=? order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, type, state.ordinal(), gender, limit },
						getBottleMapper());
			} else {
				String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,u.city_id, " + getAgeSql()
						+ ",u.sex,c.name as city_name from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id  left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.type=? and b.state=?   order by RAND() limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, type, state.ordinal(), limit },
						getBottleMapper());
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
		ImagePathUtil.completeAvatarPath(sender, true);
		return sender;
	}

	public List<Bottle> getMineBottles(long user_id, int page, int page_size) {
		String sql = "select bottle.*,coalesce(bs.view_nums,0) as view_nums from " + TABLE_BOTTLE
				+ " bottle left join t_bottle_scan_nums bs on bottle.id=bs.bottle_id "
				+ "  where bottle.user_id=?  and bottle.type<>? order by bottle.id desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { user_id, BottleType.MEET.ordinal(), (page - 1) * page_size, page_size },
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

	public int insertExpress(BottleExpress express) {
		return saveObjSimple(jdbcTemplate, "t_user_express", express);
	}

	public int clearExpireBottle() {
		String sql = "delete from  t_bottle_pool where DATEDIFF(create_time,now()) <-1";
		return jdbcTemplate.update(sql);
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
			sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where b.id=? and  b.state=? order by p.create_time  desc limit ?,?";
			if (state == -1) {
				sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where b.id=? and  b.state<>? order by p.create_time desc limit ?,?";
			}
			param = new Object[] { bottle_id, state, (pageIndex - 1) * pageSize, pageSize };
		} else {
			sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where   b.state=? order by p.create_time  desc limit ?,?";
			if (state == -1) {
				sql = "select b.*,u.nick_name,u.avatar from t_bottle_pool p left join  t_bottle b on p.bottle_id=b.id left join t_user u on b.user_id=u.user_id where     b.state<>? order by p.create_time desc limit ?,?";
			}
			param = new Object[] { state, (pageIndex - 1) * pageSize, pageSize };
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
				Bottle bottle = (Bottle) PropertyMapperUtil.prase(Bottle.class, rs);
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
			return jdbcTemplate.update("insert into t_dm_bottle_had_get (uid,bid) values(?,?)",
					new Object[] { user_id, bottle_id });
		} catch (Exception e) {
		}
		return 0;
	}

	// 清理7天以前的数据
	public void clearExpireAudioBottle() {
		String sql = "delete from    t_bottle_pool where DATEDIFF(create_time,now()) <-7 and type="
				+ BottleType.VOICE.ordinal();
		jdbcTemplate.update(sql);
	}

	public List<MeetListUser> getMeetList(long user_id, int page, int count) {
		String sql = "select msg.create_time, u.user_id,u.nick_name,u.avatar,u.sex ,u.type from t_dynamic_msg msg left join t_user u on msg.by_user_id=u.user_id where msg.user_id=? and msg.type=? and msg.isReadNum=? order by msg.create_time desc limit ?,?";
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

	public void insertReward(Reward reward) {
		saveObjSimple(jdbcTemplate, "t_reward_history", reward);
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
						u.setUser_id(rs.getLong("user_id"));
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

	// in 查询
//	public void clearBottlePoolIds(List<String> ids) {
//		    String sql="delete from t_bottle_pool where bottle_id in (:ids)";
//		    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
//			MapSqlParameterSource parameters = new MapSqlParameterSource();
//			parameters.addValue("ids", ids);
//	        namedParameterJdbcTemplate.update(sql, parameters);
//	
//	}

	public void refreshPool(int keepSize) {
		String sqlCount = "select count(*) from t_bottle_pool";
		int count = jdbcTemplate.queryForObject(sqlCount, Integer.class);

		if (keepSize >= count) {
			return;
		}
		String sql = "delete from t_bottle_pool order by bottle_id  limit ?";
		jdbcTemplate.update(sql, new Object[] { count - keepSize });
	}
}
