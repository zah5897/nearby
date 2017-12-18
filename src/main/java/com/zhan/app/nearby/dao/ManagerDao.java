package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.comm.UserType;

@Repository("managerDao")
public class ManagerDao extends BaseDao {
	public static final String TABLE_USER_DYNAMIC = "t_user_dynamic";
	public static final String TABLE_HOME_FOUND_SELECTED = "t_home_found_selected";
	public static final String TABLE_DYNAMIC_COMMENT = "t_dynamic_comment";
	public static final String TABLE_LIKE_DYNAMIC_STATE = "t_like_dynamic";
	public static final String TABLE_TOPIC = "t_topic";
	@Resource
	private JdbcTemplate jdbcTemplate;
//	private static Logger log = Logger.getLogger(ManagerDao.class);

	public long insertDynamic(UserDynamic dyanmic) {
		return saveObj(jdbcTemplate, TABLE_USER_DYNAMIC, dyanmic);
	}

	public List<UserDynamic> getHomeFoundSelected(int pageIndex, int pageSize) {
		String sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
				+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id  where selected.selected_state=?   order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { ImageStatus.SELECTED.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new DynamicMapper());

	}

	public List<UserDynamic> getDyanmicByState(int pageIndex, int pageSize,DynamicState state) {
		String sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC + " dynamic  left join t_user user on  dynamic.user_id=user.user_id  where dynamic.state=?   order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { state.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new DynamicMapper());

	}

	public List<UserDynamic> getUnSelected(int pageIndex, int pageSize) {
		String sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
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
	public int getPageCountByState(int state) {
		String sql = "select  count(*) from " + TABLE_USER_DYNAMIC + " where state=?";
		return jdbcTemplate.queryForObject(sql, new Object[] {state }, Integer.class);
	}
    //获取未选中的（前提为被审核通过的）
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
	
	public int removeDyanmicByState(long id,DynamicState state) {
		String sql = "delete from " + TABLE_USER_DYNAMIC + " where id=? and state=?";
		return jdbcTemplate.update(sql, new Object[] { id, state.ordinal() });
	}
	
	//修改动态的状态
	public int updateDynamicState(long id,DynamicState state) {
		String sql = "update  t_user_dynamic set state=? where id=? ";
		return jdbcTemplate.update(sql, new Object[] { state.ordinal(),id});
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
		Object[] colVals = { uid, FoundUserRelationship.GONE.ordinal() };
		if (count == 0) {
			String[] columns = { "uid", "state" };
			return saveObj(jdbcTemplate, tableName, columns, colVals);
		} else {
			return jdbcTemplate.update("update " + tableName + " set state=? where uid=?", colVals);
		}
	}

	public int getNewUserCount() {
		String sql = "select count(*) from t_user where type=? and  to_days(create_time) = to_days(now());";
		return jdbcTemplate.queryForObject(sql, new Object[] { UserType.OFFIEC.ordinal() }, int.class);
	}

	public List<ManagerUser> listNewUser(int pageIndex, int pageSize) {
		String sql = "select user.user_id ,user.nick_name,user.avatar,user.sex,user.type,coalesce(ship.state,0) as state from t_user user left join t_found_user_relationship ship on user.user_id=ship.uid where  type=? and  to_days(user.create_time) = to_days(now()) order by user.user_id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { UserType.OFFIEC.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new BeanPropertyRowMapper<ManagerUser>(ManagerUser.class));
	}

}
