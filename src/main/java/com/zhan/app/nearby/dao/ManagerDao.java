package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.comm.ImageStatus;

@Repository("managerDao")
public class ManagerDao extends BaseDao {
	public static final String TABLE_USER_DYNAMIC = "t_user_dynamic";
	public static final String TABLE_HOME_FOUND_SELECTED = "t_home_found_selected";
	public static final String TABLE_DYNAMIC_COMMENT = "t_dynamic_comment";
	public static final String TABLE_LIKE_DYNAMIC_STATE = "t_like_dynamic";
	@Resource
	private JdbcTemplate jdbcTemplate;
	private static Logger log = Logger.getLogger(ManagerDao.class);

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

	public List<UserDynamic> getUnSelected(int pageIndex, int pageSize) {
		String sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC
				+ " dynamic left join t_user user on  dynamic.user_id=user.user_id  where dynamic.id not in(select dynamic_id from "
				+ TABLE_HOME_FOUND_SELECTED + " where selected_state=?)   order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { ImageStatus.SELECTED.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new DynamicMapper());

	}

	public int getHomeFoundSelectedCount() {
		String sql = "select  count(*) from " + TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
				+ " selected on dynamic.id=selected.dynamic_id    where selected.selected_state=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { ImageStatus.SELECTED.ordinal() }, Integer.class);
	}

	public int getUnSelectedCount() {
		String sql = "select count(*) from " + TABLE_USER_DYNAMIC
				+ " dynamic    where dynamic.id not in(select dynamic_id from " + TABLE_HOME_FOUND_SELECTED
				+ " where selected_state=?)";
		return jdbcTemplate.queryForObject(sql, new Object[] { ImageStatus.SELECTED.ordinal() }, Integer.class);
	}

	public int removeFromSelected(long id) {
		String sql = "delete from " + TABLE_HOME_FOUND_SELECTED + " where dynamic_id=? and selected_state=?";
		return jdbcTemplate.update(sql, new Object[] { id, ImageStatus.SELECTED.ordinal() });
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

}
