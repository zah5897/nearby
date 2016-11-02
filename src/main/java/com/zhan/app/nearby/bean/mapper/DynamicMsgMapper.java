package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.util.DateTimeUtil;

public class DynamicMsgMapper implements RowMapper<DynamicMessage> {

	public DynamicMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
		DynamicMessage dynamicMsg = new DynamicMessage();
		dynamicMsg.setId(rs.getLong("id"));
		dynamicMsg.setContent(rs.getString("content"));
dynamicMsg.setCreate_time(rs.getDate("create_time"));
dynamicMsg.setDynamic_id(rs.getLong("dynamic_id"));
dynamicMsg.setType(rs.getInt("type"));
		User user = new User();
		user.setUser_id(rs.getLong("user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setAvatar(rs.getString("avatar"));
		user.setSex(rs.getString("sex"));
		Date birthday=rs.getDate("birthday");
		user.setAge(DateTimeUtil.getAge(birthday));
		dynamicMsg.setUser(user);
		return dynamicMsg;
	}

}