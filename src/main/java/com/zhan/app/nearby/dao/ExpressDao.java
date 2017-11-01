package com.zhan.app.nearby.dao;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Express;

/**
 * 表白
 * 
 * @author zah
 *
 */
@Repository("expressDao")
public class ExpressDao extends BaseDao {
	public static final String TABLE_EXPRESS = "t_express";
	@Resource
	private JdbcTemplate jdbcTemplate;

	// ---------------------------------------bottle-------------------------------------------------
	public long insert(Express express) {
		long id = saveObj(jdbcTemplate, TABLE_EXPRESS, express);
		express.setId(id);
		return id;
	}

}
