package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.City;

@Repository("cityDao")
public class CityDao extends BaseDao {
	public static final String TABLE_NAME = "t_sys_city";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public void insert(City city) {
		saveObj(jdbcTemplate, TABLE_NAME, city);
	}

	public List<City> list() {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " pre ", new Object[] {},
				new BeanPropertyRowMapper(City.class));
	}
	public List<City> listByType(int type) {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " where type=? ", new Object[] {type},
				new BeanPropertyRowMapper(City.class));
	}

	 

}
