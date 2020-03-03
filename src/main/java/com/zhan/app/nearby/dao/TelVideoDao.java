package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.TelVideo;
import com.zhan.app.nearby.dao.base.BaseDao;

@Repository("telVideoDao")
public class TelVideoDao extends BaseDao<TelVideo> {
	@Resource
	private VipDao vipDao;

	@Resource
	private CityDao cityDao;
	@Resource
	private UserDao userDao;

	public static final int BOTTLE_LIMIT_COUNT = 150;

	public TelVideo getLastHandlerVideo(String client_uuid, int state) {
		String sql = "select *from " + getTableName()
				+ " where client_uuid=? and state=? order by create_time desc limit 1";
		List<TelVideo> video = jdbcTemplate.query(sql, new Object[] { client_uuid, state }, getEntityMapper());
		if (video.isEmpty()) {
			return null;
		} else {
			return video.get(0);
		}
	}

	public TelVideo getLastHandlerVideo(String client_uuid) {
		String sql = "select *from " + getTableName() + " where client_uuid=?  order by create_time desc limit 1";
		List<TelVideo> video = jdbcTemplate.query(sql, new Object[] { client_uuid }, getEntityMapper());
		if (video.isEmpty()) {
			return null;
		} else {
			return video.get(0);
		}
	}

}
