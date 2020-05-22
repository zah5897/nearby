package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.AppointmentTheme;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.AppointmentStatus;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("appointmentDao")
public class AppointmentDao extends BaseDao<Appointment> {

	public List<Appointment> listRecommend(long user_id, Integer last_id, int count, Integer theme_id, Integer time_stage,
			String appointment_time, Integer city_id, String keyword) {
		last_id = (last_id == null ? Integer.MAX_VALUE : last_id);

		List<Object> params = new ArrayList<Object>();

		StringBuilder sql = new StringBuilder(
				"select a.*,u.user_id,u.nick_name,u.sex,u.avatar,u.birthday,u.lat,u.lng,u.isvip,u.video_cert_status,c.name as city_name,th.id as tid,th.name as thname  from "
						+ getTableName() + " a left join t_user u on a.uid=u.user_id "
						+ "left join t_sys_city c on a.city_id=c.id "
						+ "left join t_appointment_theme th on a.theme_id=th.id   where   (a.uid=? or a.status=? ) ");

		params.add(user_id);
		params.add(AppointmentStatus.RECOMMEND.ordinal());

		if (theme_id != null) {
			sql.append(" and a.theme_id=? ");
			params.add(theme_id);
		}

		if (time_stage != null) {
			sql.append(" and a.time_stage=? ");
			params.add(time_stage);
		}

		if (appointment_time != null) {
			sql.append(" and date(a.appointment_time)=?  ");
			params.add(appointment_time);
		}
		if (city_id != null) {
			sql.append(" and a.city_id=?  ");
			params.add(city_id);
		}

		if (keyword != null && !TextUtils.isEmpty(keyword.trim())) {
			sql.append(" and (a.description like ? or a.street like ?) ");
			params.add("%" + keyword + "%");
			params.add("%" + keyword + "%");
		}

		sql.append(" and a.id<? order by a.id desc limit ?");
		params.add(last_id);
		params.add(count);

		return jdbcTemplate.query(sql.toString(), params.toArray(), appointmentMapper);
	}

	public List<Appointment> queryMine(long user_id, Integer last_id, int count) {
		last_id = (last_id == null ? Integer.MAX_VALUE : last_id);

		String sql = "select a.*,u.user_id,u.nick_name,u.sex,u.avatar,u.lat,u.lng,u.birthday,u.isvip,c.name as city_name,th.id as tid,th.name as thname  from "
				+ getTableName() + " a left join t_user u on a.uid=u.user_id "
				+ "left join t_sys_city c on a.city_id=c.id "
				+ "left join t_appointment_theme th on a.theme_id=th.id   where a.uid=? and  a.id<? order by a.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count }, appointmentMapper);
	}

	public List<Appointment> queryAllToCheck(Long uid,String nick_name,int status, int page, int count) {
		
		if(uid!=null) {
			String sql = "select a.*,u.user_id,u.nick_name,u.sex,u.avatar,u.lat,u.lng,u.isvip,u.birthday,c.name as city_name,th.id as tid,th.name as thname  from "
					+ getTableName() + " a left join t_user u on a.uid=u.user_id "
					+ "left join t_sys_city c on a.city_id=c.id "
					+ "left join t_appointment_theme th on a.theme_id=th.id where a.uid=? and a.status=? order by a.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] {uid, status, (page - 1) * count, count }, appointmentMapper);
		}
		
		if(TextUtils.isNotEmpty(nick_name)) {
			String sql = "select a.*,u.user_id,u.nick_name,u.sex,u.avatar,u.lat,u.lng,u.isvip,u.birthday,c.name as city_name,th.id as tid,th.name as thname  from "
					+ getTableName() + " a left join t_user u on a.uid=u.user_id "
					+ "left join t_sys_city c on a.city_id=c.id "
					+ "left join t_appointment_theme th on a.theme_id=th.id where a.status=? and a.uid in (select user_id from t_user where nick_name like ?) order by a.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { status,"%"+nick_name+"%", (page - 1) * count, count }, appointmentMapper);
		}
		
		
		String sql = "select a.*,u.user_id,u.nick_name,u.sex,u.avatar,u.lat,u.lng,u.isvip,u.birthday,c.name as city_name,th.id as tid,th.name as thname  from "
				+ getTableName() + " a left join t_user u on a.uid=u.user_id "
				+ "left join t_sys_city c on a.city_id=c.id "
				+ "left join t_appointment_theme th on a.theme_id=th.id where a.status=? order by a.id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { status, (page - 1) * count, count }, appointmentMapper);
		
		
	}

	public List<Appointment> loadUserAppointments(long uid, Integer last_id, int count) {
		if (last_id == null) {
			last_id = Integer.MAX_VALUE;
		}
		String sql = "select a.*,u.user_id,u.nick_name,u.sex,u.avatar,u.lat,u.lng,u.isvip,u.birthday,c.name as city_name,th.id as tid,th.name as thname  from "
				+ getTableName() + " a left join t_user u on a.uid=u.user_id "
				+ "left join t_sys_city c on a.city_id=c.id "
				+ "left join t_appointment_theme th on a.theme_id=th.id where a.uid=? and (a.status=? or a.status=?) and a.id<? order by a.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { uid, AppointmentStatus.CHECKED.ordinal(),AppointmentStatus.RECOMMEND.ordinal(), last_id, count },
				appointmentMapper);
	}

	public int deleteById(long user_id, Integer id) {
		return jdbcTemplate.update("delete from " + getTableName() + " where uid=" + user_id + " and id=" + id);
	}

	public List<AppointmentTheme> listTheme() {
		return jdbcTemplate.query("select *from t_appointment_theme",
				new BeanPropertyRowMapper<AppointmentTheme>(AppointmentTheme.class));
	}

	public int getCheckCount(Long uid,String nick_name,int status) {
		
		if(uid!=null) {
			return jdbcTemplate.queryForObject("select count(*) from " + getTableName() + " where  uid="+uid+" and status=" + status,
					Integer.class);
		}
		
		if(TextUtils.isNotEmpty(nick_name)) {
			return jdbcTemplate.queryForObject("select count(*) from " + getTableName() + " where  uid in (select user_id from t_user where nick_name like ?) and status=?",new Object[] {"%"+nick_name+"%",status},
					Integer.class);
		}
		

		return jdbcTemplate.queryForObject("select count(*) from " + getTableName() + " where   status=" + status,
				Integer.class);
		
		
	}

	public void changeStatus(int id, int newStatus) {
		jdbcTemplate.update("update " + getTableName() + " set status=? where id=?", new Object[] { newStatus, id });
	}

	private static BeanPropertyRowMapper<Appointment> appointmentMapper = new BeanPropertyRowMapper<Appointment>(
			Appointment.class) {
		@Override
		public Appointment mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Appointment app = super.mapRow(rs, rowNumber);
			BaseUser user = new BaseUser();
			user.setUser_id(rs.getLong("user_id"));
			user.setNick_name(rs.getString("nick_name"));
			user.setAvatar(rs.getString("avatar"));
			user.setSex(rs.getString("sex"));
			user.setLat(rs.getString("lat"));
			user.setLng(rs.getString("lng"));
			user.setIsvip(rs.getInt("isvip"));
			user.setBirthday(rs.getDate("birthday"));
			try {
				user.setVideo_cert_status(rs.getInt("video_cert_status"));
			} catch (Exception e) {
                    
			}
			ImagePathUtil.completeAvatarPath(user, true);
			AppointmentTheme theme = new AppointmentTheme();
			theme.setId(rs.getInt("tid"));
			theme.setName(rs.getString("thname"));
			app.setTheme(theme);

			String city_name = rs.getString("city_name");
			if (!TextUtils.isEmpty(city_name)) {
				City city = new City();
				city.setId(rs.getInt("city_id"));
				city.setName(city_name);
				app.setCity(city);
			}

			app.setUser(user);
			return app;
		}
	};

	public int getAppointMentUnlockCount(long user_id, int id) {
		String sql = "select count(*) from t_appointment_unlock where uid=? and id=?";
		int count = jdbcTemplate.queryForObject(sql, new Object[] { user_id, id }, Integer.class);
		return count;
	}

	public int getAppointMentTodayCount(long user_id) {
		String sql = "select count(*) from t_appointment_unlock where uid=? and to_days(create_time)=to_days(now())";
		int count = jdbcTemplate.queryForObject(sql, new Object[] { user_id }, Integer.class);
		return count;
	}

	public void unlock(long user_id, int id) {
		jdbcTemplate.update("insert ignore into t_appointment_unlock (uid,id,create_time) values(?,?,?)",
				new Object[] { user_id, id, new Date() });
	}

	public Appointment loadById(int id) {
		String sql = "select a.*,u.user_id,u.nick_name,u.sex,u.avatar,u.lat,u.lng,u.birthday,u.isvip,c.name as city_name,th.id as tid,th.name as thname  from "
				+ getTableName() + " a left join t_user u on a.uid=u.user_id "
				+ "left join t_sys_city c on a.city_id=c.id "
				+ "left join t_appointment_theme th on a.theme_id=th.id   where  a.id=?";
		List<Appointment> apps = jdbcTemplate.query(sql, new Object[] { id }, appointmentMapper);
		if (!apps.isEmpty()) {
			return apps.get(0);
		}

		return null;

	}

	public void markDataBlack(long uid) {
		jdbcTemplate.update("update "+getTableName()+" set status=? where uid=?",AppointmentStatus.ILLEGAL.ordinal(),uid);
	}

}
