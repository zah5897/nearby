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
		comment.setComment_time(rs.getTimestamp("comment_time"));

		User user = new User();
		user.setUser_id(rs.getLong("user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setAvatar(rs.getString("avatar"));
		comment.setUser(user);
		long at_commentId = rs.getLong("at_comment_id");
		if (at_commentId > 0) {
			try {
				DynamicComment atComment = new DynamicComment();
				atComment.setId(rs.getLong("at_comment_id"));
				atComment.setDynamic_id(rs.getLong("dynamic_id"));
				atComment.setContent(rs.getString("at_content"));
				atComment.setComment_time(rs.getTimestamp("at_comment_time"));

				User at_user = new User();
				at_user.setUser_id(rs.getLong("at_u_id"));
				at_user.setNick_name(rs.getString("at_nick_name"));
				at_user.setAvatar(rs.getString("at_avatar"));
				atComment.setUser(at_user);
				comment.setAtComment(atComment);
			} catch (Exception e) {

			}
		}

		return comment;
	}

}
