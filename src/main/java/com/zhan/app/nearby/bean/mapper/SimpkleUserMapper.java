package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.util.TextUtils;

public class SimpkleUserMapper implements RowMapper<BaseUser> {
	private static Logger log = Logger.getLogger(SimpkleUserMapper.class);
	public BaseUser mapRow(ResultSet rs, int rowNum) throws SQLException {
		DetailUser user = new DetailUser();
		user.setUser_id(rs.getLong("user_id"));
		user.setMobile(rs.getString("mobile"));
		user.setPassword(rs.getString("password"));
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
			log.error(e.getMessage());
		}

		return user;
	}

}
