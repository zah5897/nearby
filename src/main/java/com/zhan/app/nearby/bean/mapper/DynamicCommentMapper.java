package com.zhan.app.nearby.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.user.BaseVipUser;

public class DynamicCommentMapper implements RowMapper<DynamicComment> {
	private static Logger log = Logger.getLogger(DynamicCommentMapper.class);
	public DynamicComment mapRow(ResultSet rs, int rowNum) throws SQLException {
		DynamicComment comment = new DynamicComment();
		comment.setId(rs.getLong("id"));
		comment.setDynamic_id(rs.getLong("dynamic_id"));
		comment.setContent(rs.getString("content"));
		comment.setComment_time(rs.getTimestamp("comment_time"));

		BaseVipUser user = new BaseVipUser();
		user.setUser_id(rs.getLong("user_id"));
		user.setNick_name(rs.getString("nick_name"));
		user.setAvatar(rs.getString("avatar"));
		user.setSex(rs.getString("sex"));
		Object vipObj = rs.getObject("vip_id");
		if (vipObj != null && !"null".equals(vipObj.toString())) {
			user.setVip(true);
		}
		comment.setUser(user);
		long at_commentId = rs.getLong("at_comment_id");
		if (at_commentId > 0) {
			try {
				DynamicComment atComment = new DynamicComment();
				atComment.setId(rs.getLong("at_comment_id"));
				atComment.setDynamic_id(rs.getLong("dynamic_id"));
				atComment.setContent(rs.getString("at_content"));
				atComment.setComment_time(rs.getTimestamp("at_comment_time"));

				BaseVipUser at_user = new BaseVipUser();
				at_user.setUser_id(rs.getLong("at_u_id"));
				at_user.setNick_name(rs.getString("at_nick_name"));
				at_user.setAvatar(rs.getString("at_avatar"));
				at_user.setSex(rs.getString("at_sex"));
				Object atVipObj = rs.getObject("at_vip_id");
				if (atVipObj != null && !"null".equals(atVipObj.toString())) {
					at_user.setVip(true);
				}
				atComment.setUser(at_user);
				comment.setAtComment(atComment);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

		return comment;
	}

}
