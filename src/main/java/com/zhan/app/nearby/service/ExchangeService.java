package com.zhan.app.nearby.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.PersonalInfo;
import com.zhan.app.nearby.dao.ExchangeDao;
import com.zhan.app.nearby.dao.SystemDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
public class ExchangeService {

	public static final int LIMIT_COUNT = 5;
	@Autowired
	private ExchangeDao exchangeDao;
	@Autowired
	private SystemDao systemDao;
	private static Logger log = Logger.getLogger(ExchangeService.class);

	@Transactional
	public Map<String, Object> exchangeCoin(long uid, int coinCount, String aid) {

		Map<String, Object> result = HttpService.minusCoins(uid, aid, coinCount, "金币提现");
		int code = (int) result.get("code");
		if (code != 0) {
			return result;
		}
		int v = exchangeDao.insert(uid, coinCount, aid);

		if (v == 1) {
			return ResultUtil.getResultOKMap();
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_SYS);
		}

	}

	public boolean isBindZhiFuBao(long uid, String aid) {
		PersonalInfo pInfo = systemDao.loadPersonalInfo(uid, aid);
		if (pInfo==null||TextUtils.isEmpty(pInfo.getZhifubao_access_number())) {
			return false;
		}
		return true;
	}
	public int savePersonalInfo(long user_id,String token,String mobile,String zhifubao,String aid) {
		PersonalInfo pInfo = new PersonalInfo();
		pInfo.setAid(aid);
		pInfo.setMobile(mobile);
		pInfo.setUser_id(user_id);
		pInfo.setZhifubao_access_number(zhifubao);
		return systemDao.insertObject(pInfo);
	}

}
