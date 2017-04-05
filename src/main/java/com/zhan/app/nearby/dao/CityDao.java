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
				new BeanPropertyRowMapper<City>(City.class));
	}

	public List<City> listByType(int type) {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " where type=? ", new Object[] { type },
				new BeanPropertyRowMapper<City>(City.class));
	}

	public List<City> listByParentId(int parent_id) {
		return jdbcTemplate.query("select *from " + TABLE_NAME + " where parent_id=? ", new Object[] { parent_id },
				new BeanPropertyRowMapper<City>(City.class));
	}

	public City getCityById(int id) {
		try {
			List<City> cities = jdbcTemplate.query("select *from " + TABLE_NAME + " where id=? ", new Object[] { id },
					new BeanPropertyRowMapper<City>(City.class));
			if (cities != null && cities.size() > 0) {
				return cities.get(0);
			}
		} catch (Exception e) {

		}
		return null;
	}

	public int getChildCount(int id) {
		return jdbcTemplate.queryForObject("select count(*) from " + TABLE_NAME + " where parent_id=? ",
				new Object[] { id }, Integer.class);
	}

	public void updateType(int id, int type) {
		jdbcTemplate.update("update " + TABLE_NAME + " set type=? where id=? ", new Object[] { type, id });
	}

}
