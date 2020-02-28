package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.dao.base.BaseDao;

@Repository("cityDao")
public class CityDao extends BaseDao<City> {

	public List<City> list() {
		return jdbcTemplate.query("select *from " + getTableName() + " pre ", new Object[] {},
				new BeanPropertyRowMapper<City>(City.class));
	}

	public List<City> listByType(int type) {
		return jdbcTemplate.query("select *from " + getTableName() + " where type=? ", new Object[] { type },
				new BeanPropertyRowMapper<City>(City.class));
	}

	public List<City> listByParentId(int parent_id) {
		return jdbcTemplate.query("select *from " + getTableName() + " where parent_id=? ", new Object[] { parent_id },
				new BeanPropertyRowMapper<City>(City.class));
	}

	public City getCityById(int id) {
		try {
			List<City> cities = jdbcTemplate.query("select *from " + getTableName() + " where id=? ", new Object[] { id },
					new BeanPropertyRowMapper<City>(City.class));
			if (cities != null && cities.size() > 0) {
				return cities.get(0);
			}
		} catch (Exception e) {

		}
		return null;
	}

	public int getChildCount(int id) {
		return jdbcTemplate.queryForObject("select count(*) from " + getTableName() + " where parent_id=? ",
				new Object[] { id }, Integer.class);
	}

	public void updateType(int id, int type) {
		jdbcTemplate.update("update " + getTableName() + " set type=? where id=? ", new Object[] { type, id });
	}

}
