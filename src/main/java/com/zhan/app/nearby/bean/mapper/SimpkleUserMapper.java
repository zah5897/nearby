package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.util.TextUtils;

public class SimpkleUserMapper implements RowMapper<User> {

	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setUser_id(rs.getLong("user_id"));
		user.setMobile(rs.getString("mobile"));
		user.setPassword(rs.getString("password"));
		user.setName(rs.getString("name"));
		user.setNick_name(rs.getString("nick_name"));
		user.setBirthday(rs.getTimestamp("birthday"));
		user.setSex(rs.getString("sex"));
		user.setAvatar(rs.getString("avatar"));
		user.setSignature(rs.getString("signature"));
		user.setToken(rs.getString("token"));
		user.set_ua(rs.getString("_ua"));
		user.setLat(rs.getString("lat"));
		user.setLng(rs.getString("lng"));
		user.setType(rs.getShort("type"));
		user.setBirth_city_id(rs.getInt("birth_city_id"));
		user.setLast_login_time(rs.getTimestamp("last_login_time"));

		try {
			int cityId = rs.getInt("city_id");
			String cityName = rs.getString("city_name");

			if (cityId > 0 && TextUtils.isNotEmpty(cityName)) {
				City city = new City();
				city.setId(cityId);
				city.setName(cityName);
				user.setCity(city);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}

		return user;
	}

	// public Funny mapRow(ResultSet rs, int rowNum) throws SQLException {
	// // Funny user = new User();
	// // user.setId(rs.getInt("id"));
	// // user.setUsername(rs.getString("username"));
	// // user.setPassword(rs.getString("password"));
	//
	// Funny f = null;
	// try {
	// f = (Funny) PropertyMapperUtil.prase(Funny.class, rs);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return f;
	// }

}
