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
			last_id = Long.MAX_VALUE;
		}
		String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip from " + TABLE_VIDEO
				+ " v left join t_user u on v.uid=u.user_id where v.uid=? and v.id<?  order by v.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count }, videoMapper);
	}

	public List<Video> loadByUid(long user_id, Long last_id, int count) {
		if (last_id == null) {
			last_id = Long.MAX_VALUE;
		}
		String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip,p.create_time as pt ,s.create_time as st  from "
				+ TABLE_VIDEO + " v left join t_user u on v.uid=u.user_id" + " left join " + TABLE_VIDEO_PRAISE_HISTORY
				+ " p on p.video_id=v.id " + " left join " + TABLE_VIDEO_STORE_HISTORY + " s on s.video_id=v.id "
				+ " where v.uid=? and v.id<? and v.status=?  order by v.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] { user_id, last_id, VideoStatus.CHECKED.ordinal(), count },
				videoMapper);
	}

	public List<Video> listAll(Long last_id, int count, Integer type, Integer secret_level) {

		String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex,u.isvip,o.check_time,p.create_time as pt ,s.create_time as st from "
				+ TABLE_VIDEO + " v left join t_user u on v.uid=u.user_id  left join " + TABLE_VIDEO_PRAISE_HISTORY
				+ " p on p.video_id=v.id  left join " + TABLE_VIDEO_STORE_HISTORY + " s on s.video_id=v.id "
				+ " left join t_user_online o on o.uid=v.uid  where v.status=? ";

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

	public void addPraiseHistory(long uid, long video_id) {
		jdbcTemplate.update(
				"insert  ignore into " + TABLE_VIDEO_PRAISE_HISTORY + " (uid,video_id,create_time) values(?,?,?)",
				new Object[] { uid, video_id, new Date() });
		jdbcTemplate.update("update  " + TABLE_VIDEO + " set praise_count=praise_count+1 where id=?", video_id);
	}

	public void cancelPraise(long uid, long video_id) {
		int count = jdbcTemplate.update("delete from " + TABLE_VIDEO_PRAISE_HISTORY + " where uid=? and video_id=?",
				uid, video_id);

		if (count > 0) {
			jdbcTemplate.update(
					"update  " + TABLE_VIDEO + " set praise_count=praise_count-1  where id=? and praise_count>0",
					video_id);
		}
	}

	public void addStoreHistory(long uid, long video_id) {
		jdbcTemplate.update(
				"insert ignore into " + TABLE_VIDEO_STORE_HISTORY + " (uid,video_id,create_time) values(?,?,?)",
				new Object[] { uid, video_id, new Date() });
		jdbcTemplate.update("update  " + TABLE_VIDEO + " set store_count=store_count+1 where id=?", video_id);
	}

	public void cancelStore(long uid, long video_id) {

		int count = jdbcTemplate.update("delete from " + TABLE_VIDEO_STORE_HISTORY + " where uid=? and video_id=?", uid,
				video_id);

		if (count > 0) {
			jdbcTemplate.update(
					"update  " + TABLE_VIDEO + " set store_count=store_count-1  where id=? and store_count>0",
					video_id);
		}
	}

	public void addShareCount(long video_id) {
		jdbcTemplate.update("update  " + TABLE_VIDEO + " set share_count=share_count+1 where id=?", video_id);
	}

	public void addScanCount(long video_id) {
		jdbcTemplate.update("update  " + TABLE_VIDEO + " set scan_count=scan_count+1 where id=?", video_id);
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
				} else {
					user.setOnline_status(0);
				}
			} catch (Exception e) {
				user.setOnline_status(-1);
			}

			try {
				Object pt = rs.getObject("pt");
				if (pt != null) {
					v.setHasPraised(true);
				} else {
					v.setHasPraised(false);
				}
			} catch (Exception e) {
				v.setHasPraised(false);
			}
			try {
				Object st = rs.getObject("st");
				if (st != null) {
					v.setHasStored(true);
				} else {
					v.setHasStored(false);
				}
			} catch (Exception e) {
				v.setHasStored(false);
			}
			v.setUser(user);
			return v;
		}
	};

	public int getCountByStatus(int status, boolean isUserCert) {
		if(isUserCert) {
			return jdbcTemplate.queryForObject("select count(*) from " + getTableName() + " where type=3 and status=" + status,
					Integer.class);
		}else {
			return jdbcTemplate.queryForObject("select count(*) from " + getTableName() + " where type<>3 and status=" + status,
					Integer.class);
		}
		
		
	}

	public List<Video> loadByStatus(int status, int page, int count,boolean isUserCert) {
		if(isUserCert) {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where  v.type=3 and  v.status=?  order by v.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { status, (page - 1) * count, count }, videoMapper);
		}else {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where  v.type<>3 and  v.status=?  order by v.id desc limit ?,?";
			return jdbcTemplate.query(sql, new Object[] { status, (page - 1) * count, count }, videoMapper);
		}
	}
	public List<Video> loadConfirmVideo(long uid) {
			String sql = "select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex ,u.isvip from " + TABLE_VIDEO
					+ " v left join t_user u on v.uid=u.user_id where  v.type=3 and  v.uid=? and  v.status=?  order by v.id desc limit 1";
			return jdbcTemplate.query(sql, new Object[] {uid, VideoStatus.CHECKED.ordinal()}, videoMapper);
		 
	}
	public void changeStatus(int id, int newStatus) {
		jdbcTemplate.update("update " + getTableName() + " set status=? where id=?",newStatus, id );
	}

	public int getTodayConfirmVideCount(long uid) {
		return jdbcTemplate.queryForObject("select count(*) from "+getTableName()+" where uid=? and type=3 and to_days(create_time)=to_days(now())",new Object[] {uid}, Integer.class);
	}
}
