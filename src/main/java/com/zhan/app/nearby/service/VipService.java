package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Vip;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.HttpsUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.PropertiesUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
@Transactional("transactionManager")
public class VipService {

	public static final int LIMIT_COUNT = 5;
	private String MODULE_ORDER_URL;
	@Resource
	private VipDao vipDao;
	private static Logger log = Logger.getLogger(VipService.class);

	public ModelMap save(Vip vip) {
		if (vip.getId() > 0) {
			vipDao.update(vip);
		} else {
			vipDao.insert(vip);
		}
		return ResultUtil.getResultOKMap().addAttribute("vip", vip);
	}

	public ModelMap list() {
		List<Vip> vips = vipDao.listVipData();
		return ResultUtil.getResultOKMap().addAttribute("vips", vips);
	}

	public ModelMap delete(long id) {
		vipDao.delete(id);
		return ResultUtil.getResultOKMap().addAttribute("id", id);
	}

	public Map<?, ?> buy(VipUser vipUser) {

		if (vipUser.getVip_id() == 0 || vipUser.getUser_id() == 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		Vip vip = vipDao.load(vipUser.getVip_id());
		if (vip == null) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "该礼物不存在");
		}
		if (TextUtils.isEmpty(vipUser.getAid())) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "app not exist");
		}
		if (TextUtils.isEmpty(MODULE_ORDER_URL)) {
			Properties prop = PropertiesUtil.load("config.properties");
			String value = PropertiesUtil.getProperty(prop, "MODULE_ORDER_URL");
			MODULE_ORDER_URL = value;
		}
		String result = null;
		try {
			result = HttpsUtil.sendHttpsPost(MODULE_ORDER_URL + "?user_id=" + vipUser.getUser_id() + "&aid="
					+ vipUser.getAid() + "&rule_id=" + vipUser.getVip_id() + "&subject=" + vip.getName() + "&amount="
					+ vip.getAmount() + "&type=1");
		} catch (Exception e) {
			log.error("购买失败" + e.getMessage());
		}
		if (!TextUtils.isEmpty(result)) {
			return JSONUtil.jsonToMap(result);
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}

	public String buy_notify(VipUser vipUser) {
		if (vipUser.getUser_id() < 1 || vipUser.getVip_id() < 0) {
			return "failed";
		}

		// 当前vip类型
		Vip vip = vipDao.load(vipUser.getVip_id());
		Date now = new Date();
		// 获取已购买的vip数据
		VipUser userVip = vipDao.loadUserVip(vipUser.getUser_id());
		// 已经是vip了，计算复杂
		if (userVip != null) {
			if (userVip.getDayDiff()<=0) {//以天为精度的过期
				vipDao.delUserVip(vipUser.getUser_id());
			}else{
				Date newEndDate=DateTimeUtil.getVipEndDate(userVip.getEnd_time(), vip.getTerm_mount());
				userVip.setEnd_time(newEndDate);
				userVip.setLast_order_no(vipUser.getLast_order_no());
				vipDao.updateUserVip(userVip);
				return "success";
			}
		}
		// 还不是vip
		vipUser.setStart_time(now);
		vipUser.setEnd_time(DateTimeUtil.getVipEndDate(now, vip.getTerm_mount()));
		vipDao.insert(vipUser);
		return "success";
	}

	public Map<?, ?> load(long user_id) {
		VipUser userVip =vipDao.loadUserVip(user_id);
		return ResultUtil.getResultOKMap().addAttribute("vip_info", userVip);
	}
}
