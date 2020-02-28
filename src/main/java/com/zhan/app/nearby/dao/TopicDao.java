package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.dao.base.BaseDao;

@Repository("topicDao")
public class TopicDao extends BaseDao<Topic> {
	public static final String TABLE_NAME = "t_topic";
	@Resource
	private JdbcTemplate jdbcTemplate;

 
	public List<Topic> list() {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " order by id desc ", new Object[] {},
				new BeanPropertyRowMapper<Topic>(Topic.class));
	}

	public Topic top() {
		List<Topic> topics = jdbcTemplate.query("select *from " + TABLE_NAME + " order by id desc limit 1",
				new Object[] {}, new BeanPropertyRowMapper<Topic>(Topic.class));
		if (topics != null && topics.size() > 0) {
			return topics.get(0);
		}
		return null;
	}

	public List<Topic> history(long current_topic_id) {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " where id<>? order by id desc ", new Object[] {current_topic_id},
				new BeanPropertyRowMapper<Topic>(Topic.class));
	}

}
