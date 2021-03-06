package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.user.SimpleUser;

public class DynamicMsgMapper implements RowMapper<DynamicMessage> {

	public DynamicMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
		DynamicMessage dynamicMsg = new DynamicMessage();
		dynamicMsg.setId(rs.getLong("id"));
		dynamicMsg.setContent(rs.getString("content"));
		dynamicMsg.setCreate_time(rs.getTimestamp("create_time"));

		dynamicMsg.setStatus(rs.getInt("status"));

		dynamicMsg.setType(rs.getInt("type"));
		int isRead = rs.getInt("isReadNum");
		dynamicMsg.setIsReadNum(isRead);
		long obj_id = rs.getLong("obj_id");

		dynamicMsg.setObj_id(obj_id);

		SimpleUser user = new SimpleUser();
		user.setUser_id(rs.getLong("by_user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setAvatar(rs.getString("avatar"));
		user.setSex(rs.getString("sex"));
		user.setType(rs.getShort("type"));
		Date birthday = rs.getTimestamp("birthday");
		user.setBirthday(birthday);
		dynamicMsg.setUser(user);
		return dynamicMsg;
	}

}
