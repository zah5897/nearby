package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.dao.BottleDao;

@Service
@Transactional("transactionManager")
public class BottleService {

	public static final int LIMIT_COUNT = 5;

	@Resource
	private BottleDao bottleDao;

	public Bottle getBottleFromPool(long user_id) {

		List<Bottle> bottles = bottleDao.getBottleRandomInPool(user_id, 1);
		if (bottles != null && bottles.size() > 0) {
			return bottles.get(0);
		}
		return null;
	}

	public List<Bottle> getBottles(long user_id, long last_id, int page_size) {
		return bottleDao.getBottles(user_id, last_id, page_size);
	}

	public boolean existBottles(String content, String img) {
		return bottleDao.existBottles(content, img);
	}

	public List<Bottle> getBottlesFromPool(long user_id) {
		return bottleDao.getBottleRandomInPool(user_id, LIMIT_COUNT);
	}

	public void insert(Bottle bottle) {
		bottle.setCreate_time(new Date());
		bottleDao.insert(bottle);
		if (bottle.getUser_id() > 0) {
			bottleDao.insertToPool(bottle);
		}
	}

	public Bottle getBottleDetial(long bottle_id) {
		return bottleDao.getBottleById(bottle_id);
	}
}
