package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.user.DetailUser;

public class FoundUserMapper implements RowMapper<DetailUser> {

	public DetailUser mapRow(ResultSet rs, int rowNum) throws SQLException {
		DetailUser user = new DetailUser();
		user.setUser_id(rs.getLong("user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setBirthday(rs.getTimestamp("birthday"));
		user.setAvatar(rs.getString("avatar"));
		user.setLat(rs.getString("lat"));
		user.setLng(rs.getString("lng"));
		user.setJob_ids(rs.getString("job_ids"));
		user.setDisc(rs.getString("juli"));
		user.setInterest_ids(rs.getString("interest_ids"));
		user.setType(rs.getShort("type"));
		return user;
	}

}
