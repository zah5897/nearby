package com.zhan.app.nearby.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.dao.base.BaseDao;

@Repository("exchangeDao")
public class ExchangeDao extends BaseDao<Exchange> {
	public int insert(long uid,int coin,String aid) {

		int count = jdbcTemplate.update(
				"insert into t_coin_exchange (uid,coin_count,create_time,aid) values(?,?,?,?)",
				new Object[] { uid,coin ,new Date(),aid});
		return count;
	}

	 
 
}
