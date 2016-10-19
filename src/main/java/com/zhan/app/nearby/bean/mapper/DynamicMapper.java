package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.UserDynamic;

public class DynamicMapper implements RowMapper<UserDynamic> {

	public UserDynamic mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserDynamic dynamic = new UserDynamic();
		dynamic.setId(rs.getLong("id"));
		dynamic.setDescription(rs.getString("description"));
		dynamic.setAddr(rs.getString("addr"));
		dynamic.setCreate_time(rs.getDate("create_time"));
		dynamic.setLocal_image_name(rs.getString("local_image_name"));
		dynamic.setPraise_count(rs.getInt("praise_count"));
		dynamic.setCan_comment(rs.getString("can_comment"));

		User user = new User();
		dynamic.setUser(user);

		dynamic.getUser().setUser_id(rs.getLong("user_id"));
		dynamic.getUser().setNick_name(rs.getString("nick_name"));
		return dynamic;
	}

}
