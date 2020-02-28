package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VideoComment;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

@Repository("videoDao")
public class VideoDao extends BaseDao<Video> {
	public static final String TABLE_VIDEO = "t_short_video";
	public static final String TABLE_VIDEO_COMMENT = "t_video_comment";
	public static final String TABLE_VIDEO_PRAISE_HISTORY = "t_video_praise_history";
	public static final String TABLE_VIDEO_STORE_HISTORY = "t_video_store_history";


	public List<Video> mine(long user_id, Long last_id, int count) {
		if (last_id==null) {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where v.uid=?  order by v.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, count },
					new BeanPropertyRowMapper<Video>(Video.class) {
						@Override
						public Video mapRow(ResultSet rs, int rowNumber) throws SQLException {
							Video v = super.mapRow(rs, rowNumber);
							BaseUser user = new BaseUser();
							user.setUser_id(rs.getLong("user_id"));
							user.setNick_name(rs.getString("nick_name"));
							user.setAvatar(rs.getString("avatar"));
							user.setSex(rs.getString("sex"));
							ImagePathUtil.completeAvatarPath(user, true);
							v.setUser(user);
							return v;
						}
					});
		} else {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where v.uid=? and v.id<?  order by v.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count },
					new BeanPropertyRowMapper<Video>(Video.class) {
						@Override
						public Video mapRow(ResultSet rs, int rowNumber) throws SQLException {
							Video v = super.mapRow(rs, rowNumber);
							BaseUser user = new BaseUser();
							user.setUser_id(rs.getLong("user_id"));
							user.setNick_name(rs.getString("nick_name"));
							user.setAvatar(rs.getString("avatar"));
							user.setSex(rs.getString("sex"));
							ImagePathUtil.completeAvatarPath(user, true);
							v.setUser(user);
							return v;
						}
					});
		}
	}

	public List<VideoComment> listComment(long user_id, String vid, Integer last_id, int count) {
		
		if(last_id==null) {
			String sql = "select vc.*,u.nick_name,u.avatar,u.sex from " + TABLE_VIDEO_COMMENT
					+ " vc left join t_user u on vc.uid=u.user_id where vc.video_id=? order by vc.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { vid,count },
					new BeanPropertyRowMapper<VideoComment>(VideoComment.class) {
						@Override
						public VideoComment mapRow(ResultSet rs, int rowNumber) throws SQLException {
							VideoComment vc = super.mapRow(rs, rowNumber);
							BaseUser user = new BaseUser();
							user.setUser_id(rs.getLong("user_id"));
							user.setNick_name(rs.getString("nick_name"));
							user.setAvatar(rs.getString("avatar"));
							user.setSex(rs.getString("sex"));
							ImagePathUtil.completeAvatarPath(user, true);
							vc.setUser(user);
							return vc;
						}
					});
		}else {
			String sql = "select vc.*,u.nick_name,u.avatar,u.sex from " + TABLE_VIDEO_COMMENT
					+ " vc left join t_user u on vc.uid=u.user_id where vc.video_id=?  and vc.id<? order by vc.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { vid, last_id, count },
					new BeanPropertyRowMapper<VideoComment>(VideoComment.class) {
						@Override
						public VideoComment mapRow(ResultSet rs, int rowNumber) throws SQLException {
							VideoComment vc = super.mapRow(rs, rowNumber);
							BaseUser user = new BaseUser();
							user.setUser_id(rs.getLong("user_id"));
							user.setNick_name(rs.getString("nick_name"));
							user.setAvatar(rs.getString("avatar"));
							user.setSex(rs.getString("sex"));
							ImagePathUtil.completeAvatarPath(user, true);
							vc.setUser(user);
							return vc;
						}
					});
		}
	}

	public void addCommentCount(String video_id) {
		int count = jdbcTemplate.queryForObject("select comment_count from " + TABLE_VIDEO + " where id=?",
				new Object[] { video_id }, Integer.class);
		jdbcTemplate.update("update  " + TABLE_VIDEO + " set comment_count=? where id=?",
				new Object[] { count + 1, video_id });
	}

	public void addPraiseHistory(long uid, String video_id) {
		try {
			jdbcTemplate.update(
					"insert into " + TABLE_VIDEO_PRAISE_HISTORY + " (uid,video_id,create_time) values(?,?,?)",
					new Object[] { uid, video_id, new Date() });

			int count = jdbcTemplate.queryForObject("select praise_count from " + TABLE_VIDEO + " where id=?",
					new Object[] { video_id }, Integer.class);
			jdbcTemplate.update("update  " + TABLE_VIDEO + " set praise_count=? where id=?",
					new Object[] { count + 1, video_id });
		} catch (Exception e) {

		}
	}

	public void addStoreHistory(long uid, String video_id) {
		try {
			jdbcTemplate.update(
					"insert into " + TABLE_VIDEO_STORE_HISTORY + " (uid,video_id,create_time) values(?,?,?)",
					new Object[] { uid, video_id, new Date() });

			int count = jdbcTemplate.queryForObject("select store_count from " + TABLE_VIDEO + " where id=?",
					new Object[] { video_id }, Integer.class);
			jdbcTemplate.update("update  " + TABLE_VIDEO + " set store_count=? where id=?",
					new Object[] { count + 1, video_id });
		} catch (Exception e) {

		}
	}

	public void addShareCount(String video_id) {
		int count = jdbcTemplate.queryForObject("select share_count from " + TABLE_VIDEO + " where id=?",
				new Object[] { video_id }, Integer.class);
		jdbcTemplate.update("update  " + TABLE_VIDEO + " set share_count=? where id=?",
				new Object[] { count + 1, video_id });
	}

}
