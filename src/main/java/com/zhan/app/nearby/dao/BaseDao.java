package com.zhan.app.nearby.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.zhan.app.nearby.util.ObjectUtil;

public class BaseDao {

	protected long saveObj(JdbcTemplate jdbcTemplate, String tableName, Object obj) {
		try {
			String sql = " insert into " + tableName + " (";
			Map<String, String> map = ObjectUtil.getProperty(obj);

			String filedStr = "";
			Set<String> set = map.keySet();
			for (String key : set) {
				filedStr += (key + ",");
			}
			filedStr = filedStr.substring(0, filedStr.length() - 1);
			filedStr += " ) ";

			String values = " values ( ";
			for (String key : set) {
				values += ("'" + map.get(key) + "',");
			}
			values = values.substring(0, values.length() - 1);
			values += " ) ";

			sql += (filedStr + values);

			final String s = sql;
			KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
					return ps;
				}

			}, keyHolder);
			return keyHolder.getKey().longValue();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	protected int saveObj(JdbcTemplate jdbcTemplate, String tableName, String[] columns, Object[] colVals) {
		String sql = " insert into " + tableName + " (";
		String filedStr = "";
		for (String key : columns) {
			filedStr += (key + ",");
		}
		filedStr = filedStr.substring(0, filedStr.length() - 1);
		filedStr += " ) ";

		String values = " values ( ";
		for (Object object : colVals) {
			values += ("'" + object + "',");
		}
		values = values.substring(0, values.length() - 1);
		values += " ) ";

		sql += (filedStr + values);
		return jdbcTemplate.update(sql);
	}

}
