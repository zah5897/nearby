package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.TelVideo;

@Repository("telVideoDao")
public class TelVideoDao extends BaseDao {
	public static final String TABLE_VIDEo = "t_video";
	@Resource
	private JdbcTemplate jdbcTemplate;

	@Resource
	private VipDao vipDao;

	@Resource
	private CityDao cityDao;
	@Resource
	private UserDao userDao;

	public static final int BOTTLE_LIMIT_COUNT = 150;

	// ---------------------------------------bottle-------------------------------------------------
	public void insert(TelVideo video) {
		saveObjSimple(jdbcTemplate, TABLE_VIDEo, video);
	}

	public TelVideo getLastHandlerVideo(String client_uuid, int state) {
		String sql = "select *from " + TABLE_VIDEo
				+ " where client_uuid=? and state=? order by create_time desc limit 1";
		List<TelVideo> video = jdbcTemplate.query(sql, new Object[] { client_uuid, state },
				new BeanPropertyRowMapper<TelVideo>(TelVideo.class));
		if (video.isEmpty()) {
			return null;
		} else {
			return video.get(0);
		}
	}

	public TelVideo getLastHandlerVideo(String client_uuid) {
		String sql = "select *from " + TABLE_VIDEo + " where client_uuid=?  order by create_time desc limit 1";
		List<TelVideo> video = jdbcTemplate.query(sql, new Object[] { client_uuid },
				new BeanPropertyRowMapper<TelVideo>(TelVideo.class));
		if (video.isEmpty()) {
			return null;
		} else {
			return video.get(0);
		}
	}

}