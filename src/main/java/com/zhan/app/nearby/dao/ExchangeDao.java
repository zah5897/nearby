package com.zhan.app.nearby.dao;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("exchangeDao")
public class ExchangeDao extends BaseDao {
	@Resource
	private JdbcTemplate jdbcTemplate;
	public int insert(long uid,int coin,String aid) {

		int count = jdbcTemplate.update(
				"insert into t_coin_exchange (uid,coin_count,create_time,aid) values(?,?,?,?)",
				new Object[] { uid,coin ,new Date(),aid});
		return count;
	}

	 
 
}
