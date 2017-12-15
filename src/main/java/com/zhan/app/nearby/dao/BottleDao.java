package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("bottleDao")
public class BottleDao extends BaseDao {
	public static final String TABLE_BOTTLE = "t_bottle";
	public static final String TABLE_BOTTLE_POOL = "t_bottle_pool";
	public static final String TABLE_BOTTLE_COMMENT = "t_bottle_comment";
	@Resource
	private JdbcTemplate jdbcTemplate;

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
		String sql = "insert into " + TABLE_BOTTLE_POOL + " set bottle_id=?,user_id=?,type=?,create_time=?";
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

	public List<Bottle> getBottles(long user_id, long last_id, int limit) {
		String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id,"+getAgeSql()+", u.sex ,c.name as city_name from t_bottle b left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.id>? order by b.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { user_id, last_id, limit },
				new BeanPropertyRowMapper<Bottle>(Bottle.class) {
					@Override
					public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
						Bottle bottle = super.mapRow(rs, rowNumber);
						User user = new User();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setBirthday(rs.getDate("birthday"));
						user.setAvatar(rs.getString("avatar"));
						user.setSex(rs.getString("sex"));
						user.setAge(rs.getString("age"));
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
				});
	}

	public List<Bottle> getBottlesByGender(long user_id, long last_id, int limit, int gender) {
		String sql = "select b.*,u.nick_name,u.avatar,u.birthday,u.birth_city_id, "+getAgeSql()+",u.sex,c.name as city_name from t_bottle b left join t_user u on b.user_id=u.user_id left join t_sys_city c on u.birth_city_id=c.id where b.user_id<>? and b.id>? and u.sex=? order by b.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { user_id, last_id, gender, limit },
				new BeanPropertyRowMapper<Bottle>(Bottle.class) {
					@Override
					public Bottle mapRow(ResultSet rs, int rowNumber) throws SQLException {
						Bottle bottle = super.mapRow(rs, rowNumber);
						User user = new User();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setBirthday(rs.getDate("birthday"));
						user.setAvatar(rs.getString("avatar"));
						user.setSex(rs.getString("sex"));
						user.setAge(rs.getString("age"));
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
				});
	}

	public boolean exist(long bottle_id) {
		String sql = "select count(*) from t_bottle where id=?";
		int count = jdbcTemplate.queryForObject(sql, new Object[] { bottle_id }, Integer.class);
		return count > 0;
	}

	public List<Bottle> getBottleFromPool(long id, int limit) {
		String sql = "select * from " + TABLE_BOTTLE_POOL + " pool left join " + TABLE_BOTTLE
				+ " bottle on pool.bottle_id=bottle.id where pool.bottle_id>? order by pool.bottle_id limit ?";
		try {
			return jdbcTemplate.query(sql, new Object[] { id, limit }, new BeanPropertyRowMapper<Bottle>(Bottle.class));
		} catch (Exception e) {
			return null;
		}
	}

	public int removeFromPool(long id) {
		String sql = "delete from " + TABLE_BOTTLE_POOL + " where bottle_id=?";
		return jdbcTemplate.update(sql, new Object[] { id });
	}

	public boolean existBottles(String content, String img) {
		return jdbcTemplate.queryForObject("select count(*) from t_bottle where content=? and image=?",
				new Object[] { content, img }, Integer.class) > 0;
	}

	private User resultSetToUser(ResultSet rs) throws SQLException {
		User sender = new User();
		sender.setUser_id(rs.getLong("user_id"));
		sender.setNick_name(rs.getString("nick_name"));
		sender.setAvatar(rs.getString("avatar"));
		return sender;
	}

	public List<Bottle> getMineBottles(long user_id, long last_id, int page_size) {
		long real_last_id = last_id <= 0 ? Integer.MAX_VALUE : last_id;
		String sql="select bottle.*,coalesce(bs.view_nums,0) as view_nums from " + TABLE_BOTTLE + " bottle left join t_bottle_scan_nums bs on bottle.id=bs.bottle_id "
				+ "  where bottle.user_id=? and bottle.id<? order by bottle.id desc limit ?";
		return jdbcTemplate.query(sql,
				new Object[] { user_id, real_last_id, page_size }, new BeanPropertyRowMapper<Bottle>(Bottle.class));
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

	public List<User> getScanUserList(long bottle_id) {
		String sql = "select user.user_id,user.nick_name,user.avatar from t_bottle_scan scan left join t_user user on scan.user_id=user.user_id where scan.bottle_id=? order by scan.create_time";
		return jdbcTemplate.query(sql, new Object[] { bottle_id }, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setUser_id(rs.getLong("user_id"));
				user.setNick_name(rs.getString("nick_name"));
				user.setAvatar(rs.getString("avatar"));
				return user;
			}
		});
	}

	public List<User> getRandomScanUserList(int limit) {
		String sql = "select user_id,nick_name,avatar from t_user where avatar<>? order by user_id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { null, limit }, new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setUser_id(rs.getLong("user_id"));
				user.setNick_name(rs.getString("nick_name"));
				user.setAvatar(rs.getString("avatar"));
				return user;
			}
		});
	}
	
	
	
	private String getAgeSql(){
		return " (year(now())-year(u.birthday)-1) + ( DATE_FORMAT(u.birthday, '%m%d') <= DATE_FORMAT(NOW(), '%m%d') ) as age ";
	}
	
}
