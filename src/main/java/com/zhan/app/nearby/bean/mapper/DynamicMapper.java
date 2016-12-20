package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.LikeDynamicState;
import com.zhan.app.nearby.util.DateTimeUtil;

public class DynamicMapper implements RowMapper<UserDynamic> {

	public UserDynamic mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserDynamic dynamic = new UserDynamic();
		dynamic.setId(rs.getLong("id"));
		dynamic.setDescription(rs.getString("description"));
		dynamic.setAddr(rs.getString("addr"));
		dynamic.setCreate_time(rs.getTimestamp("create_time"));
		dynamic.setLocal_image_name(rs.getString("local_image_name"));
		dynamic.setPraise_count(rs.getInt("praise_count"));
		dynamic.setBrowser_count(rs.getInt("browser_count"));
		dynamic.setCan_comment(rs.getString("can_comment"));
		dynamic.setStreet(rs.getString("street"));

		try {
			dynamic.setLike_state(rs.getInt("like_state"));
		} catch (Exception e) {
			dynamic.setLike_state(LikeDynamicState.UNLIKE.ordinal());
		}

		User user = new User();

		user.setUser_id(rs.getLong("user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setAvatar(rs.getString("avatar"));
		user.setSex(rs.getString("sex"));
		user.setType(rs.getShort("type"));
		Date birthday = rs.getDate("birthday");
		user.setAge(DateTimeUtil.getAge(birthday));

		dynamic.setUser(user);
		return dynamic;
	}

}
