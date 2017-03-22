package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Topic;

@Repository("topicDao")
public class TopicDao extends BaseDao {
	public static final String TABLE_NAME = "t_topic";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public long insert(Topic topic) {
		return saveObj(jdbcTemplate, TABLE_NAME, topic);
	}

	public List<Topic> list() {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " pre ", new Object[] {},
				new BeanPropertyRowMapper<Topic>(Topic.class));
	}

}
