package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.user.LocationUser;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("appointmentDao")
public class AppointmentDao extends BaseDao<Appointment> {

	public List<Appointment> queryAll(long user_id, Integer last_id, int count) {
		last_id = (last_id == null ? Integer.MAX_VALUE : last_id);

		String sql = "select a.*,u.user_id,u.nick_name,u.sex,u.lat,u.lng,c.name as city_name,u.city_id as user_city_id,uc.name as user_city_name from "
				+ getTableName()
				+ " a left join t_user u on a.uid=u.user_id left join t_sys_city c on a.city_id=c.id left join t_sys_city uc on u.city_id=uc.id   where a.id<? order by a.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { last_id, count }, appointmentMapper);
	}

	public int deleteById(long user_id, Integer id) {
		return jdbcTemplate.update("delete from " + getTableName() + " where uid=" + user_id + " and id=" + id);
	}

	private static BeanPropertyRowMapper<Appointment> appointmentMapper = new BeanPropertyRowMapper<Appointment>(
			Appointment.class) {
		@Override
		public Appointment mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Appointment app = super.mapRow(rs, rowNumber);
			LocationUser user = new LocationUser();
			user.setUser_id(rs.getLong("user_id"));
			user.setNick_name(rs.getString("nick_name"));
			user.setSex(rs.getString("sex"));
			user.setLat(rs.getString("lat"));
			user.setLng(rs.getString("lng"));
			ImagePathUtil.completeAvatarPath(user, true);

			String city_name = rs.getString("city_name");
			if (!TextUtils.isEmpty(city_name)) {
				City city = new City();
				city.setId(rs.getInt("city_id"));
				city.setName(city_name);
				app.setCity(city);
			}
			
			String user_city_name = rs.getString("user_city_name");
			if (!TextUtils.isEmpty(user_city_name)) {
				City city = new City();
				city.setId(rs.getInt("user_city_id"));
				city.setName(user_city_name);
				user.setCity(city);
			}
			app.setPublisher(user);
			return app;
		}
	};
}
