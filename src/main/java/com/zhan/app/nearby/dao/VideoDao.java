package com.zhan.app.nearby.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.util.ImagePathUtil;

@Repository("videoDao")
public class VideoDao extends BaseDao {
	public static final String TABLE_VIDEo = "t_short_video";
	@Resource
	private JdbcTemplate jdbcTemplate;
	// ---------------------------------------bottle-------------------------------------------------
	public void insert(Video video) {
		saveObjSimple(jdbcTemplate, TABLE_VIDEo, video);
	}
	public List<Video> mine(long user_id,int page,int count) {
    String sql="select v.* ,u.user_id,u.nick_name ,u.avatar,u.sex from "+TABLE_VIDEo+" v left join t_user u on v.uid=u.user_id where v.uid=?  order by create_time desc limit ?,?";
		return jdbcTemplate.query(sql,new Object[] {user_id,(page-1)*count,count}, new BeanPropertyRowMapper<Video>(Video.class) {
			@Override
			public Video mapRow(ResultSet rs, int rowNumber) throws SQLException {
				Video v= super.mapRow(rs, rowNumber);
				BaseUser user=new BaseUser();
				user.setUser_id(rs.getLong("user_id"));
				user.setNick_name(rs.getString("nick_name"));
				user.setAvatar(rs.getString("avatar"));
				user.setSex(rs.getString("sex"));
				ImagePathUtil.completeAvatarPath(user, true);
				v.setSender(user);
				return v;
			}
		});
	}
}
