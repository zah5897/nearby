package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;

@Repository("userDynamicDao")
public class UserDynamicDao extends BaseDao {
	public static final String TABLE_USER_DYNAMIC = "t_user_dynamic";
	public static final String TABLE_HOME_FOUND_SELECTED = "t_home_found_selected";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public long insertDynamic(UserDynamic dyanmic) {
		return saveObj(jdbcTemplate, TABLE_USER_DYNAMIC, dyanmic);
	}

	public List<UserDynamic> getHomeFoundSelected(ImageStatus status, long last_id, int page_size) {
		{
			String sql = "select *from " + TABLE_USER_DYNAMIC + " dyanmic right join " + TABLE_HOME_FOUND_SELECTED
					+ " selected on dyanmic.id=selected.dynamic_id and selected.selected_state=? and selected.dynamic_id>? order by selected.dynamic_id desc limit ?";

			return jdbcTemplate.query(sql, new Object[] { status.ordinal(), last_id, page_size },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		}
	}

	public void addHomeFoundSelected(long dynamic_id) {
		String sql = "insert into " + TABLE_HOME_FOUND_SELECTED + " values (?, ?)";
		jdbcTemplate.update(sql, new Object[] { dynamic_id, ImageStatus.SELECTED.ordinal() });
	}

	public List<Integer> getPraiseCount(long dynamic_id) {
		String sql = "select praise_count from " + TABLE_USER_DYNAMIC + " where id=?";
		return jdbcTemplate.queryForList(sql, new Object[] { dynamic_id }, Integer.class);
	}

	public int praiseDynamic(long dynamic_id) {
		List<Integer> result = getPraiseCount(dynamic_id);

		if (result == null || result.size() == 0) {
			return -1;
		}
		Integer hasPraiseCount = result.get(0);

		if (hasPraiseCount < 0) {
			hasPraiseCount = 0;
		}
		jdbcTemplate.update("update " + TABLE_USER_DYNAMIC + " set praise_count=? where id=?",
				new Object[] { hasPraiseCount += 1, dynamic_id });
		return hasPraiseCount;
	}

	public List<UserDynamic> getUserDynamic(long user_id, long last_id, int count) {
		if (last_id < 1) {
			String sql = "select * from " + TABLE_USER_DYNAMIC + " where user_id=? order by id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, count },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		} else {
			String sql = "select * from " + TABLE_USER_DYNAMIC + " where user_id=? and id<? order by id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		}
	}
}
