package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VideoComment;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.VideoStatus;
import com.zhan.app.nearby.dao.base.BaseDao;
import com.zhan.app.nearby.util.ImagePathUtil;

@Repository("videoDao")
public class VideoDao extends BaseDao<Video> {
	public static final String TABLE_VIDEO = "t_short_video";
	public static final String TABLE_VIDEO_COMMENT = "t_video_comment";
	public static final String TABLE_VIDEO_PRAISE_HISTORY = "t_video_praise_history";
	public static final String TABLE_VIDEO_STORE_HISTORY = "t_video_store_history";

	public List<Video> mine(long user_id, Long last_id, int count) {
		if (last_id == null) {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex,u.isvip from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where v.uid=?  order by v.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, count }, videoMapper);
		} else {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where v.uid=? and v.id<?  order by v.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count }, videoMapper);
		}
	}

	public List<Video> loadByUid(long user_id, Long last_id, int count) {
		if (last_id == null) {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex,u.isvip from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where v.uid=? and v.status=?  order by v.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, VideoStatus.CHECKED.ordinal(), count }, videoMapper);
		} else {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where v.uid=? and v.id<? and v.status=?  order by v.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, VideoStatus.CHECKED.ordinal(), count },
					videoMapper);
		}
	}

	public List<Video> listAll(Long last_id, int count, Integer type, Integer secret_level) {

		String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex,u.isvip,o.check_time from " + TABLE_VIDEO
				+ " v left join t_user u on v.uid=u.user_id  left join t_user_online o on o.uid=v.uid  where v.status=? ";

		List<Object> params = new ArrayList<Object>();
		params.add(VideoStatus.CHECKED.ordinal());
		if (last_id != null) {
			sql += " and v.id<? ";
			params.add(last_id);
		}

		if (type != null) {
			sql += " and v.type=?";
			params.add(type);
		}

		if (secret_level != null) {
			sql += " and v.secret_level=?";
			params.add(secret_level);
		}
		sql += " order by v.id desc limit ?";
		params.add(count);
		return jdbcTemplate.query(sql, params.toArray(), videoMapper);
	}

	public List<VideoComment> listComment(long user_id, String vid, Integer last_id, int count) {

		if (last_id == null) {
			String sql = "select vc.*,u.nick_name,u.avatar,u.sex from " + TABLE_VIDEO_COMMENT
					+ " vc left join t_user u on vc.uid=u.user_id where vc.video_id=? order by vc.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { vid, count },
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
		} else {
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

	private static BeanPropertyRowMapper<Video> videoMapper = new BeanPropertyRowMapper<Video>(Video.class) {
		@Override
		public Video mapRow(ResultSet rs, int rowNumber) throws SQLException {
			Video v = super.mapRow(rs, rowNumber);
			BaseUser user = new BaseUser();
			user.setUser_id(rs.getLong("user_id"));
			user.setNick_name(rs.getString("nick_name"));
			user.setAvatar(rs.getString("avatar"));
			user.setSex(rs.getString("sex"));
			user.setIsvip(rs.getInt("isvip"));
			ImagePathUtil.completeAvatarPath(user, true);
			try {
				Object loginTime = rs.getObject("check_time");
				if (loginTime != null) {
					user.setOnline_status(1);
				}else {
					user.setOnline_status(0);
				}
			} catch (Exception e) {
				user.setOnline_status(-1);
			}
			v.setUser(user);
			return v;
		}
	};

	public int getCountByStatus(int status) {
		return jdbcTemplate.queryForObject("select count(*) from " + getTableName() + " where status=" + status,
				Integer.class);
	}

	public List<Video> loadByStatus(int status, int page, int count) {
		String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip from " + TABLE_VIDEO
				+ " v left join t_user u on v.uid=u.user_id where  v.status=?  order by v.id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { status, (page - 1) * count, count }, videoMapper);
	}

	public void changeStatus(int id, int newStatus) {
		jdbcTemplate.update("update " + getTableName() + " set status=? where id=?", new Object[] { newStatus, id });
	}
}
