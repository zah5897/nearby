package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.dao.BottleDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ResultUtil;

@Service
@Transactional("transactionManager")
public class BottleService {

	public static final int LIMIT_COUNT = 5;

	@Resource
	private BottleDao bottleDao;
	@Resource
	private VipDao vipDao;

	public Bottle getBottleFromPool(long user_id) {

		List<Bottle> bottles = bottleDao.getBottleRandomInPool(user_id, 1);
		if (bottles != null && bottles.size() > 0) {
			return bottles.get(0);
		}
		return null;
	}

	public ModelMap getBottles(long user_id, long last_id, int page_size, Integer look_sex) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<Bottle> bolltes = null;
		if (look_sex == null) {
			bolltes = bottleDao.getBottles(user_id, last_id, page_size);
		} else {
			VipUser vip = vipDao.loadUserVip(user_id);
			if (vip == null) {
				return ResultUtil.getResultMap(ERROR.ERR_NOT_VIP);
			} else if (vip.getDayDiff() < 0) {
				return ResultUtil.getResultMap(ERROR.ERR_VIP_EXPIRE);
			}
			bolltes = bottleDao.getBottlesByGender(user_id, last_id, page_size, look_sex == null ? -1 : look_sex);
		}
		result.addAttribute("bottles", bolltes);
		return result;
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

	public List<Bottle> getMineBottles(long user_id, long last_id, int page_size) {
		return bottleDao.getMineBottles(user_id, last_id, page_size);
	}

	public ModelMap delete(long user_id, long bottle_id) {
		int result = bottleDao.delete(user_id, bottle_id);
		if (result > 0) {
			Bottle bottle = new Bottle();
			bottle.setId(bottle_id);
			return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}
}
