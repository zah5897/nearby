package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.comm.DynamicStatus;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.comm.UserFnStatus;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("managerDao")
public class ManagerDao extends BaseDao<ManagerUser> {
	public static final String TABLE_USER_DYNAMIC = "t_user_dynamic";
	public static final String TABLE_DYNAMIC_COMMENT = "t_dynamic_comment";
	public static final String TABLE_LIKE_DYNAMIC_STATE = "t_like_dynamic";
	public static final String TABLE_TOPIC = "t_topic";

//	public long insertDynamic(UserDynamic dyanmic) {
//		return saveObj(jdbcTemplate, TABLE_USER_DYNAMIC, dyanmic);
//	}

	public List<UserDynamic> getHomeFoundSelected(Long user_id, int pageIndex, int pageSize, Long dy_id) {

		if (dy_id!=null) {
			String sql = "select dy.*, user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type ,user.isvip from "
					+ TABLE_USER_DYNAMIC
					+ " dy   left join t_user user on  dy.user_id=user.user_id  where dy.found_status=?  and dy.id=? order by dy.id desc limit ?,?";
			return jdbcTemplate.query(sql,
					new Object[] { UserFnStatus.ENABLE.ordinal(),dy_id, (pageIndex - 1) * pageSize, pageSize },
					new DynamicMapper());
		}else {
			
			if(user_id==null) {
				String sql = "select dy.*, user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type ,user.isvip from "
						+ TABLE_USER_DYNAMIC
						+ " dy   left join t_user user on  dy.user_id=user.user_id  where dy.found_status=?  order by dy.id desc limit ?,?";
				return jdbcTemplate.query(sql,
						new Object[] { UserFnStatus.ENABLE.ordinal(), (pageIndex - 1) * pageSize, pageSize },
						new DynamicMapper());
			}else {
				String sql = "select dy.*, user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type ,user.isvip from "
						+ TABLE_USER_DYNAMIC
						+ " dy   left join t_user user on  dy.user_id=user.user_id  where dy.found_status=?  and  dy.user_id=?  order by dy.id desc limit ?,?";
				return jdbcTemplate.query(sql,
						new Object[] { UserFnStatus.ENABLE.ordinal(),user_id, (pageIndex - 1) * pageSize, pageSize },
						new DynamicMapper());
			}
			 
		}

	}

	public List<UserDynamic> getUnCheckedDynamic(Long user_id, int page, int count) {

		if (user_id != null) {
			String sql = "select dynamic.*  ,user.user_id  ,user.nick_name ,user.avatar,user.sex,user.isvip ,user.birthday,user.type from "
					+ TABLE_USER_DYNAMIC
					+ " dynamic left join t_user user on  dynamic.user_id=user.user_id  where   dynamic.state=? and dynamic.user_id=? order by dynamic.id desc limit ?,?";
			return jdbcTemplate.query(sql,
					new Object[] { DynamicStatus.CREATE.ordinal(), user_id, (page - 1) * count, count },
					new DynamicMapper());
		} else {
			String sql = "select dynamic.*  ,user.user_id  ,user.nick_name ,user.avatar,user.sex,user.isvip ,user.birthday,user.type from "
					+ TABLE_USER_DYNAMIC
					+ " dynamic left join t_user user on  dynamic.user_id=user.user_id  where   dynamic.state=?   order by dynamic.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { DynamicStatus.CREATE.ordinal(), (page - 1) * count, count },
					new DynamicMapper());
		}
	}

	public List<UserDynamic> getUnSelectedDynamic(Long user_id,Long dy_id, String nick_name, int pageIndex, int pageSize) {

		String sql = "select dynamic.*  ,user.user_id  ,user.nick_name ,user.avatar,user.sex,user.isvip ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC
				+ " dynamic left join t_user user on  dynamic.user_id=user.user_id  where dynamic.type=0 and dynamic.found_status=? and dynamic.state=? and dynamic.manager_flag<>1 ";

		List<Object> param = new ArrayList<>();

		param.add(UserFnStatus.DEFAULT.ordinal());
		param.add(DynamicStatus.CHECKED.ordinal());

		if (dy_id!=null) {
			sql += " and dynamic.id=?";
			param.add(dy_id);
		}

		if (user_id != null) {
			sql += " and dynamic.user_id =? ";
			param.add(user_id);
		}
		sql += " order by dynamic.id desc limit ?,?";
		param.add((pageIndex - 1) * pageSize);
		param.add(pageSize);
		return jdbcTemplate.query(sql, param.toArray(), new DynamicMapper());
	}

	public List<UserDynamic> getUnCheckDynamic(int page, int count) {
		String sql = "select dynamic.*  ,user.user_id  ,user.nick_name ,user.avatar,user.sex,user.isvip ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC
				+ " dynamic left join t_user user on  dynamic.user_id=user.user_id  where  dynamic.type=0 and   dynamic.state=? order by dynamic.id desc limit ?,?";

		return jdbcTemplate.query(sql, new Object[] { DynamicStatus.CREATE.ordinal(), (page - 1) * count, count },
				new DynamicMapper());
	}

	public List<UserDynamic> getIllegalDynamic(int page, int count) {
		String sql = "select dynamic.*  ,user.user_id  ,user.nick_name ,user.avatar,user.sex,user.isvip ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC
				+ " dynamic left join t_user user on  dynamic.user_id=user.user_id  where dynamic.type=0 and   dynamic.state=? and dynamic.user_id not in (select user_id from t_user where sys_status<>1) order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { DynamicStatus.ILLEGAL.ordinal(), (page - 1) * count, count },
				new DynamicMapper());
	}

	public int getHomeFoundSelectedCount(Long user_id, Long dy_id) {
		if (dy_id!=null) {
			String sql = "select  count(*) from " + TABLE_USER_DYNAMIC
					+ " dy left join t_user u on dy.user_id=u.user_id  where dy.type=0 and dy.found_status=?   and dy.id=?";
			return jdbcTemplate.queryForObject(sql, new Object[] { UserFnStatus.ENABLE.ordinal(), dy_id }, Integer.class);
		} else {
			if(user_id==null) {
				String sql = "select  count(*) from " + TABLE_USER_DYNAMIC
						+ " dy left join t_user u on dy.user_id=u.user_id  where  dy.type=0 and dy.found_status=? ";

				return jdbcTemplate.queryForObject(sql,	new Object[] { UserFnStatus.ENABLE.ordinal()},Integer.class);
			}else {
				String sql = "select  count(*) from " + TABLE_USER_DYNAMIC
						+ " dy left join t_user u on dy.user_id=u.user_id  where dy.type=0 and  dy.found_status=?   and dy.user_id=? ";

				return jdbcTemplate.queryForObject(sql,	new Object[] { UserFnStatus.ENABLE.ordinal(),user_id },Integer.class);
			}
			 
		}
	}

	// 获取未选中的（前提为被审核通过的）
	public int getUnSelectedCount(Long user_id, Long dy_id) {
		String sql = "select count(*) from " + TABLE_USER_DYNAMIC
				+ " dynamic    where dynamic.type=0 and  dynamic.found_status=?  and dynamic.state=? and manager_flag<>1 ";

		List<Object> param = new ArrayList<Object>();
		param.add(UserFnStatus.DEFAULT.ordinal());
		param.add(DynamicStatus.CHECKED.ordinal());

		if (dy_id!=null) {
			sql += " and dynamic.id=? ";
			param.add(dy_id);
		}

		if (user_id != null) {
			sql += " and dynamic.user_id=? ";
			param.add(user_id);
		}

		return jdbcTemplate.queryForObject(sql, param.toArray(), Integer.class);
	}

	public int getUnCheckedDynamicCount() { // 短视频和图片动态分开处理的，这里只拿图片动态
		String sql = "select count(*) from " + TABLE_USER_DYNAMIC
				+ " dynamic    where  dynamic.type=0 and  dynamic.state=" + DynamicStatus.CREATE.ordinal();
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int getIllegalDynamicCount() {
		String sql = "select count(*) from t_user_dynamic   where   type=0 and   state=2 and  user_id not in (select user_id from t_user where sys_status<>1)";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int removeFromSelected(long id) {
		String sql = "update   " + TABLE_USER_DYNAMIC + " set found_status=? where id=?";
		return jdbcTemplate.update(sql, new Object[] { UserFnStatus.DEFAULT.ordinal(), id });
	}

	public int removeDyanmicByIdAndState(long id, DynamicStatus state) {
		String sql = "delete from " + TABLE_USER_DYNAMIC + " where id=? and state=?";
		return jdbcTemplate.update(sql, new Object[] { id, state.ordinal() });
	}

	// 修改动态的状态
	public int updateDynamicState(long id, DynamicStatus state) {
		if (state == DynamicStatus.ILLEGAL) {
			String sql = "update  t_user_dynamic set state=?,local_image_name=? where id=? ";
			return jdbcTemplate.update(sql, new Object[] { state.ordinal(), "illegal.jpg", id });
		} else {
			String sql = "update  t_user_dynamic set state=? where id=? ";
			return jdbcTemplate.update(sql, new Object[] { state.ordinal(), id });
		}
	}

	// 修改动态的状态
	public int updateDynamicManagerFlag(long id, int flag) {
		String sql = "update  t_user_dynamic set manager_flag=? where id=? ";
		return jdbcTemplate.update(sql, flag, id);
	}

	public int removeUserDynamic(long id) {
		String sql = "delete from " + TABLE_USER_DYNAMIC + " where id=?";
		return jdbcTemplate.update(sql, new Object[] { id });
	}

	public int addToSelected(long id) {
		String sql = "update   " + TABLE_USER_DYNAMIC + " set found_status=? where  id=?";
		return jdbcTemplate.update(sql, new Object[] { UserFnStatus.ENABLE.ordinal(), id });
	}

	public List<Topic> loadTopic() {
		return jdbcTemplate.query("select *from " + TABLE_TOPIC, new BeanPropertyRowMapper<Topic>(Topic.class));
	}

	public void delTopic(long id) {
		jdbcTemplate.update("delete  from " + TABLE_TOPIC + " where id=?", new Object[] { id });
	}

	public int setUserSysState(long uid, SysUserStatus status) {
		return jdbcTemplate.update("update t_user set sys_status=? where user_id=?", status.ordinal(), uid);
	}

	/**
	 * 根据限制条件获取新增用户总数
	 * 
	 * @param type
	 * @return
	 */
	public int getNewUserCount(int type) {
		String sql = "";
		if (type == -1) { // 今日
			sql = "select count(*) from t_user where (type=? or type=?) and  to_days(create_time) = to_days(now())";
		} else if (type == 0) {
			sql = "select count(*) from t_user where (type=? or type=?) and  to_days(create_time) >= (to_days(now())-2)";
		} else if (type == 1) {
			sql = "select count(*) from t_user where (type=? or type=?) and  to_days(create_time) >= (to_days(now())-7)";
		} else if (type == 2) {
			sql = "select count(*) from t_user where (type=? or type=?) and  to_days(create_time) >= (to_days(now())-30)";
		}
		return jdbcTemplate.queryForObject(sql,
				new Object[] { UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal() }, int.class);
	}

	public List<ManagerUser> listNewUser(int pageIndex, int pageSize, int type) {

		String sql = "select user.user_id ,user._ua,user.nick_name,user.avatar,user.sex,user.type,user.channel,user.sys_status as state,user.create_time from t_user user"
				+ "  where  (type=? or type=?) and";
		if (type == -1) { // 今日
			sql += "  to_days(create_time) = to_days(now())";
		} else if (type == 0) {
			sql += " to_days(create_time) >= (to_days(now())-2)";
		} else if (type == 1) {
			sql += " to_days(create_time) >= (to_days(now())-7)";
		} else if (type == 2) {
			sql += " to_days(create_time) >= (to_days(now())-30)";
		}
		sql += " order by user.user_id desc limit ?,?";
		return jdbcTemplate.query(
				sql, new Object[] { UserType.OFFIEC.ordinal(), UserType.THRID_CHANNEL.ordinal(),
						(pageIndex - 1) * pageSize, pageSize },
				new BeanPropertyRowMapper<ManagerUser>(ManagerUser.class));
	}

	/**
	 * 根据限制条件获取新增用户总数
	 * 
	 * @param type
	 * @return
	 */
	public int getAllUserCount(Long uid, int type, String keyword) {
		List<Object> param = new ArrayList<>();

		String sql = "select count(*) from t_user where ";
		if (type == -1) {
			sql += " type<>? ";
		} else {
			sql += " type=?";
		}
		param.add(type);
		if (uid != null) {
			sql += " and user_id=? ";
			param.add(uid);
		}

		if (TextUtils.isNotEmpty(keyword)) {
			sql += " and nick_name like ?";
			param.add("%" + keyword + "%");
		}
		return jdbcTemplate.queryForObject(sql, param.toArray(), Integer.class);
	}

	public List<ManagerUser> listAllUser(Long uid, int page, int count, int type, String keyword) {
		String sql = "select user.user_id ,user._ua,user.nick_name,user.avatar,user.sex,user.type,user.channel,user.sys_status as state,user.create_time from t_user user"
				+ "  where ";

		if (type == -1) {
			sql += " user.type<>? ";
		} else {
			sql += " user.type=? ";
		}

		List<Object> param = new ArrayList<>();
		param.add(type);
		if (uid != null) {
			sql += " and user.user_id=? ";
			param.add(uid);
		}

		if (TextUtils.isNotEmpty(keyword)) {
			sql += " and user.nick_name like ?";
			param.add("%" + keyword + "%");
		}
		sql += " order by user.user_id desc limit ?,?";

		param.add((page - 1) * count);
		param.add(count);
		return jdbcTemplate.query(sql, param.toArray(), new BeanPropertyRowMapper<ManagerUser>(ManagerUser.class));
	}

	public int editUserMeetBottle(long user_id, UserFnStatus fun) {
		jdbcTemplate.update("update t_user set bottle_meet_status=? where user_id=?", fun.ordinal(), user_id);
		return 1;
	}

	/**
	 * 获取提现记录
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @param type
	 */
	public List<Object> getExchangeHistory(int pageSize, int pageIndex, int type) {
		String sql = null;
		Object[] args = null;
		if (type == 1) {
			sql = "select ex.*,u.nick_name as nick_name ,p.* from t_exchange_history ex left join t_user u on ex.user_id=u.user_id left join t_personal_info p on ex.user_id=p.user_id order by create_time limit ?,?";
			args = new Object[] { (pageIndex - 1) * pageSize, pageSize };
		} else if (type == 2) {
			sql = "select ex.*,u.nick_name as nick_name ,p.* from t_exchange_history ex left join t_user u on ex.user_id=u.user_id left join t_personal_info p on ex.user_id=p.user_id where ex.state=? order by create_time limit ?,?";
			args = new Object[] { ExchangeState.IN_EXCHANGE.ordinal(), (pageIndex - 1) * pageSize, pageSize };
		} else {
			sql = "select ex.*,u.nick_name as nick_name ,p.* from t_exchange_history ex left join t_user u on ex.user_id=u.user_id left join t_personal_info p on ex.user_id=p.user_id where ex.state<>? order by create_time limit ?,?";
			args = new Object[] { ExchangeState.IN_EXCHANGE.ordinal(), (pageIndex - 1) * pageSize, pageSize };
		}
		RowMapper<Object> mapper = new RowMapper<Object>() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				HashMap<String, Object> ex = new HashMap<String, Object>();
				ex.put("id", rs.getInt("id"));
				ex.put("user_id", rs.getLong("user_id"));
				ex.put("nick_name", rs.getString("nick_name"));

				ex.put("diamond_count", rs.getInt("diamond_count"));
				ex.put("create_time", DateTimeUtil.format(rs.getTimestamp("create_time")));
				ex.put("rmb_fen", rs.getInt("rmb_fen"));
				ex.put("finish_time", DateTimeUtil.format(rs.getTimestamp("finish_time")));
				ex.put("state", rs.getInt("state"));
				ex.put("personal_name", rs.getString("personal_name"));
				ex.put("zhifubao_access_number", rs.getString("zhifubao_access_number"));
				ex.put("personal_id", rs.getString("personal_id"));
				return ex;
			}
		};
		return jdbcTemplate.query(sql, args, mapper);
	}

	/**
	 * 获取提现数量
	 * 
	 * @param type
	 * @return
	 */
	public int getExchangeHistorySize(int type) {
		if (type == 1) {
			return jdbcTemplate.queryForObject("select count(*) from t_exchange_history where state=?",
					new Object[] { type }, Integer.class);
		} else if (type == 2) {
			return jdbcTemplate.queryForObject("select count(*) from t_exchange_history where state=?",
					new Object[] { ExchangeState.IN_EXCHANGE.ordinal() }, Integer.class);
		} else {
			return jdbcTemplate.queryForObject("select count(*) from t_exchange_history where state<>?",
					new Object[] { ExchangeState.IN_EXCHANGE.ordinal() }, Integer.class);
		}
	}

	/**
	 * 修改提现申请状态
	 * 
	 * @param id
	 * @param agreeWait
	 */
	public int updateExchageState(int id, ExchangeState agreeWait) {
		return jdbcTemplate.update("update t_exchange_history set state=?,finish_time=? where id=?",
				new Object[] { agreeWait.ordinal(), new Date(), id });
	}

	public int queryM(String name, String pwd) {
		String sql = "select  count(*) from t_m where name=? and pwd=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { name, pwd }, Integer.class);
	}

	public int updateMPwd(String name, String pwd) {
		String sql = "update t_m set pwd=?  where name=?";
		return jdbcTemplate.update(sql, new Object[] { pwd, name });
	}

	public int queryAllowed(String ip) {
		String sql = "select  count(*) from t_wips where aip=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { ip }, Integer.class);
	}
}
