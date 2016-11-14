package com.zhan.app.nearby.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mchange.v2.sql.SqlUtils;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.mapper.SimpkleUserMapper;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.SQLUtil;

@Repository("userDao")
public class UserDao extends BaseDao {
	@Resource
	private JdbcTemplate jdbcTemplate;

	public User getUser(long id) {
		List<User> list = jdbcTemplate.query("select *from t_user user where user.user_id=?", new Object[] { id },
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

	public List<?> getList() {
		List<User> list = jdbcTemplate.query("select *from t_user user", new SimpkleUserMapper());
		return list;
	}

	public User findUserByMobile(String mobile) {
		List<User> list = jdbcTemplate.query("select *from t_user user where user.mobile=?", new Object[] { mobile },
				new SimpkleUserMapper());
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	public User findUserByDeviceId(String deviceId) {
		List<User> list = jdbcTemplate.query("select *from t_user user where user.mobile=? and type=?", new Object[] { deviceId ,UserType.VISITOR.ordinal()},
				new SimpkleUserMapper());
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	public Serializable insert(User user) {
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

	public int updateToken(long userId, String token, String _ua) {
		return jdbcTemplate.update("update t_user set token=?,_ua=? where user_id=?",
				new Object[] { token, _ua, userId });
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
	public int updateVisitor(long user_id, String device_token,String lat, String lng,String zh_cn) {
		return jdbcTemplate.update("update t_user set device_token=?,zh_cn=?, lat=?,lng=? where user_id=?", new Object[] {device_token,zh_cn ,lat, lng, user_id });
	}
	
	
	public User getUserDetailInfo(long user_id) {
		List<User> list = jdbcTemplate.query("select *from t_user user where user.user_id=?", new Object[] { user_id },
				new BeanPropertyRowMapper(User.class));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public int modify_info(long user_id, String nick_name, String birthday, String job, String height, String weight,
			String signature, String my_tags, String interests, String animals, String musics, String weekday_todo,
			String footsteps, String want_to_where) {

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

		if (values.size() == 0) {
			return 0;
		}
		values.add(user_id);
		sql += names.toString() + " where user_id=?";

		Object[] params = values.toArray();
		return jdbcTemplate.update(sql, params);
	}

	
	
 
	
}
