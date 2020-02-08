package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.util.DateTimeUtil;

@Repository("managerDao")
public class ManagerDao extends BaseDao {
	public static final String TABLE_USER_DYNAMIC = "t_user_dynamic";
	public static final String TABLE_HOME_FOUND_SELECTED = "t_home_found_selected";
	public static final String TABLE_DYNAMIC_COMMENT = "t_dynamic_comment";
	public static final String TABLE_LIKE_DYNAMIC_STATE = "t_like_dynamic";
	public static final String TABLE_TOPIC = "t_topic";
	@Resource
	private JdbcTemplate jdbcTemplate;
	// private static Logger log = Logger.getLogger(ManagerDao.class);

	public long insertDynamic(UserDynamic dyanmic) {
		return saveObj(jdbcTemplate, TABLE_USER_DYNAMIC, dyanmic);
	}

	public List<UserDynamic> getHomeFoundSelected(int pageIndex, int pageSize) {
		String sql = "select dynamic.*, user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
				+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id  where selected.selected_state=?   order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { ImageStatus.SELECTED.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new DynamicMapper());

	}

	public List<UserDynamic> getUnSelected(int pageIndex, int pageSize) {
		String sql = "select dynamic.*  ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC
				+ " dynamic left join t_user user on  dynamic.user_id=user.user_id  where dynamic.id not in(select dynamic_id from "
				+ TABLE_HOME_FOUND_SELECTED
				+ " where selected_state=? or selected_state=? )  and dynamic.state=1  order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { ImageStatus.SELECTED.ordinal(), ImageStatus.IGNORE.ordinal(),
				(pageIndex - 1) * pageSize, pageSize }, new DynamicMapper());

	}

	public int getHomeFoundSelectedCount() {
		String sql = "select  count(*) from " + TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
				+ " selected on dynamic.id=selected.dynamic_id    where selected.selected_state=? and dynamic.state=1 ";
		return jdbcTemplate.queryForObject(sql, new Object[] { ImageStatus.SELECTED.ordinal() }, Integer.class);
	}

	// 获取未选中的（前提为被审核通过的）
	public int getUnSelectedCount() {
		String sql = "select count(*) from " + TABLE_USER_DYNAMIC
				+ " dynamic    where dynamic.id not in(select dynamic_id from " + TABLE_HOME_FOUND_SELECTED
				+ " where selected_state=? or selected_state=?) and dynamic.state=1 ";
		return jdbcTemplate.queryForObject(sql,
				new Object[] { ImageStatus.SELECTED.ordinal(), ImageStatus.SELECTED.ordinal() }, Integer.class);
	}

	public int removeFromSelected(long id) {
		String sql = "delete from " + TABLE_HOME_FOUND_SELECTED + " where dynamic_id=? and selected_state=?";
		return jdbcTemplate.update(sql, new Object[] { id, ImageStatus.SELECTED.ordinal() });
	}

	public int removeDyanmicByState(long id, DynamicState state) {
		String sql = "delete from " + TABLE_USER_DYNAMIC + " where id=? and state=?";
		return jdbcTemplate.update(sql, new Object[] { id, state.ordinal() });
	}

	// 修改动态的状态
	public int updateDynamicState(long id, DynamicState state) {
		String sql = "update  t_user_dynamic set state=? where id=? ";
		return jdbcTemplate.update(sql, new Object[] { state.ordinal(), id });
	}

	public int removeUserDynamic(long id) {
		String sql = "delete from " + TABLE_USER_DYNAMIC + " where id=?";
		return jdbcTemplate.update(sql, new Object[] { id });
	}

	public int addToSelected(long id) {
		String checkHas = "select count(*) from " + TABLE_HOME_FOUND_SELECTED
				+ " where dynamic_id=? and selected_state=?";
		int count = jdbcTemplate.queryForObject(checkHas, new Object[] { id, ImageStatus.SELECTED.ordinal() },
				Integer.class);

		if (count < 1) {
			String sql = "insert into " + TABLE_HOME_FOUND_SELECTED + " values (?, ?)";
			return jdbcTemplate.update(sql, new Object[] { id, ImageStatus.SELECTED.ordinal() });
		}
		return 0;
	}

	public int ignore(long id) {
		String checkHas = "select count(*) from " + TABLE_HOME_FOUND_SELECTED
				+ " where dynamic_id=? and selected_state=?";
		int count = jdbcTemplate.queryForObject(checkHas, new Object[] { id, ImageStatus.IGNORE.ordinal() },
				Integer.class);

		if (count < 1) {
			String sql = "insert into " + TABLE_HOME_FOUND_SELECTED + " values (?, ?)";
			return jdbcTemplate.update(sql, new Object[] { id, ImageStatus.IGNORE.ordinal() });
		}
		return 0;
	}

	public long insertTopic(Topic topic) {
		return saveObj(jdbcTemplate, TABLE_TOPIC, topic);
	}

	public List<Topic> loadTopic() {
		return jdbcTemplate.query("select *from " + TABLE_TOPIC, new BeanPropertyRowMapper<Topic>(Topic.class));
	}

	public void delTopic(long id) {
		jdbcTemplate.update("delete  from " + TABLE_TOPIC + " where id=?", new Object[] { id });
	}

	public int setUserFoundRelationshipState(long uid, FoundUserRelationship gone) {
		String tableName = "t_found_user_relationship";

		if (gone == FoundUserRelationship.VISIBLE) {
			return jdbcTemplate.update("delete from " + tableName + " where uid=?", new Object[] { uid });
		}
		int count = jdbcTemplate.queryForObject("select count(*) from t_found_user_relationship where uid=?",
				new Object[] { uid }, int.class);
		if (count == 0) {
			String[] columns = { "uid", "state" ,"action_time"};
			return saveObj(jdbcTemplate, tableName, columns, new Object[]  { uid, FoundUserRelationship.GONE.ordinal() ,new Date()}) ;
		} else {
			return jdbcTemplate.update("update " + tableName + " set state=?,action_time where uid=?", new Object[] { FoundUserRelationship.GONE.ordinal(),new Date(),uid});
		}
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
		return jdbcTemplate.queryForObject(sql, new Object[] { UserType.OFFIEC.ordinal(),UserType.THRID_CHANNEL.ordinal() }, int.class);
	}

	public List<ManagerUser> listNewUser(int pageIndex, int pageSize, int type) {

		String sql = "select user.user_id ,user._ua,user.nick_name,user.avatar,user.sex,user.type,coalesce(ship.state,0) as state,user.create_time from t_user user"
				+ "  left join t_found_user_relationship ship on user.user_id=ship.uid where  (type=? or type=?) and";
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
		return jdbcTemplate.query(sql, new Object[] { UserType.OFFIEC.ordinal(),UserType.THRID_CHANNEL.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new BeanPropertyRowMapper<ManagerUser>(ManagerUser.class));
	}

	/**
	 * 添加到发现用户黑名单
	 * 
	 * @param user_id
	 * @return
	 */
	public int editUserFoundState(long user_id, FoundUserRelationship ship) {
		int count = jdbcTemplate.update("update t_found_user_relationship set state=?,action_time=? where uid=?",
				new Object[] { ship.ordinal(),new Date(), user_id });
		if (count !=1) {
			String sql = "insert into t_found_user_relationship values (?, ?,?)";
			return jdbcTemplate.update(sql, new Object[] { user_id, ship.ordinal() ,new Date()});
		}
		return count;
	}

	public int removeUserFoundState(long user_id) {
		return jdbcTemplate.update("delete from t_found_user_relationship where uid=?", new Object[] { user_id });
	}

	public int editUserMeetBottle(long user_id, int fun,String ip,String by) {
		if (fun == 1) {
			String checkHas = "select count(*) from t_user_meet_bottle_recommend where uid=?";
			int count = jdbcTemplate.queryForObject(checkHas, new Object[] { user_id }, Integer.class);
			if (count < 1) {
				String sql = "insert into t_user_meet_bottle_recommend values (?,?,?,?)";
				return jdbcTemplate.update(sql, new Object[] { user_id,ip ,by,new Date()});
			}
		} else {
			return jdbcTemplate.update("delete from t_user_meet_bottle_recommend where uid=?",
					new Object[] { user_id });
		}

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
				ex.put("create_time", DateTimeUtil.parse(rs.getTimestamp("create_time")));
				ex.put("rmb_fen", rs.getInt("rmb_fen"));
				ex.put("finish_time", DateTimeUtil.parse(rs.getTimestamp("finish_time")));
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
				new Object[] { agreeWait.ordinal(),new Date(), id });
	}

	
	public int queryM(String name,String pwd) {
		String sql = "select  count(*) from t_m where name=? and pwd=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { name ,pwd}, Integer.class);
	}
	public int updateMPwd(String name,String pwd) {
		String sql = "update t_m set pwd=?  where name=?";
		return jdbcTemplate.update(sql, new Object[] {pwd,name});
	}
	
	public int queryAllowed(String ip) {
		String sql = "select  count(*) from t_wips where aip=?";
		return jdbcTemplate.queryForObject(sql, new Object[] {ip}, Integer.class);
	}
}
