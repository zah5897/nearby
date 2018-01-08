package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.user.BaseUser;

public class FateUserMapper implements RowMapper<BaseUser> {

	public BaseUser mapRow(ResultSet rs, int rowNum) throws SQLException {
		BaseUser user = new BaseUser();
		user.setUser_id(rs.getLong("user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setAvatar(rs.getString("avatar"));
		user.setSex(rs.getString("sex"));
		user.setType(rs.getShort("type"));
		return user;
	}

}
