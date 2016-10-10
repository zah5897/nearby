package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.User;

public class DynamicCommentMapper implements RowMapper<DynamicComment> {

	public DynamicComment mapRow(ResultSet rs, int rowNum) throws SQLException {
		DynamicComment comment = new DynamicComment();
		comment.setId(rs.getLong("id"));
		comment.setDynamic_id(rs.getLong("dynamic_id"));
		comment.setContent(rs.getString("content"));
		comment.setComment_time(rs.getDate("comment_time"));
		
		
		User user=new User();
		user.setUser_id(rs.getLong("user_id"));
		user.setNick_name(rs.getString("nick_name"));
		
		comment.setComment_user(user);
		return comment;
	}

	 
}
