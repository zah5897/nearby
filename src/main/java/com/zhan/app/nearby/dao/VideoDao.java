package com.zhan.app.nearby.dao;


import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Video;

@Repository("videoDao")
public class VideoDao extends BaseDao {
	public static final String TABLE_VIDEo = "t_short_video";
	@Resource
	private JdbcTemplate jdbcTemplate;
	// ---------------------------------------bottle-------------------------------------------------
	public void insert(Video video) {
		saveObjSimple(jdbcTemplate, TABLE_VIDEo, video);
	}
}
