package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.property.MsgAttention;
import com.zhan.app.nearby.bean.user.SimpleUser;
import com.zhan.app.nearby.util.DateTimeUtil;

public class DynamicMsgMapper implements RowMapper<DynamicMessage> {

	public DynamicMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
		DynamicMessage dynamicMsg = new DynamicMessage();
		dynamicMsg.setId(rs.getLong("id"));
		dynamicMsg.setContent(rs.getString("content"));
		dynamicMsg.setCreate_time(rs.getTimestamp("create_time"));

		dynamicMsg.setStatus(rs.getInt("status"));
		
		dynamicMsg.setType(rs.getInt("type"));
        int isRead=rs.getInt("isReadNum");
        dynamicMsg.setIsReadNum(isRead);
		long dy_id = rs.getLong("dynamic_id");

		dynamicMsg.setAttention(new MsgAttention());
		dynamicMsg.getAttention().setId(dy_id);
		dynamicMsg.getAttention().setType(dynamicMsg.getType());
		dynamicMsg.setDynamic_id(dy_id);

		SimpleUser user = new SimpleUser();
		user.setUser_id(rs.getLong("by_user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setAvatar(rs.getString("avatar"));
		user.setSex(rs.getString("sex"));
		user.setType(rs.getShort("type"));
		Date birthday = rs.getTimestamp("birthday");
		user.setAge(DateTimeUtil.getAge(birthday));
		dynamicMsg.setUser(user);
		return dynamicMsg;
	}

}
