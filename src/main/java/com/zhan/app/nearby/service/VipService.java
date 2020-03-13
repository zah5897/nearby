package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Vip;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
public class VipService {

	public static final int LIMIT_COUNT = 5;

	@Resource
	private VipDao vipDao;

	
	@Autowired
	private UserService userService;
	
	@Transactional
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
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "该VIP不存在");
		}
		if (TextUtils.isEmpty(vipUser.getAid())) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "app not exist");
		}
		return HttpService.buyVIP(vipUser.getUser_id(), vipUser.getAid(), vipUser.getVip_id(), vip.getName(),
				vip.getAmount(), 1);
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
		if (userVip == null || userVip.getDayDiff() <= 0) {// 以天为精度的过期
			vipDao.delUserVip(vipUser.getUser_id());
			vipUser.setStart_time(now);
			vipUser.setEnd_time(DateTimeUtil.getVipEndDate(now, vip.getTerm_mount()));
			vipDao.insertObject(vipUser);
			userService.updateUserVipVal(vipUser.getUser_id(),true);
			return "success";
		} else {
			Date newEndDate = DateTimeUtil.getVipEndDate(userVip.getEnd_time(), vip.getTerm_mount());
			userVip.setEnd_time(newEndDate);
			userVip.setLast_order_no(vipUser.getLast_order_no());
			vipDao.updateUserVip(userVip);
			userService.updateUserVipVal(vipUser.getUser_id(),true);
			return "success";
		}
		// 还不是vip
	}

	public String chargeVip(VipUser vipUser, int month) {

		// 当前vip类型
		Date now = new Date();
		// 获取已购买的vip数据
		VipUser userVip = vipDao.loadUserVip(vipUser.getUser_id());
		// 已经是vip了，计算复杂
		if (userVip == null || userVip.getDayDiff() <= 0) {// 以天为精度的过期
			vipDao.delUserVip(vipUser.getUser_id());
			vipUser.setStart_time(now);
			vipUser.setLast_order_no(DateTimeUtil.getOutTradeNo());
			vipUser.setEnd_time(DateTimeUtil.getVipEndDate(now, month));
			vipDao.insertObject(vipUser);
			return "success";
		} else {
			Date newEndDate = DateTimeUtil.getVipEndDate(userVip.getEnd_time(), month);
			userVip.setEnd_time(newEndDate);
			userVip.setLast_order_no(DateTimeUtil.getOutTradeNo());
			vipDao.updateUserVip(userVip);
			return "success";
		}
		// 还不是vip
	}

	public Map<?, ?> load(long user_id) {
		VipUser userVip = vipDao.loadUserVip(user_id);
		return ResultUtil.getResultOKMap().addAttribute("vip_info", userVip);
	}

	@Transactional
	public int clearExpireVip() {
		List<Long> vips = vipDao.loadExpireVip();
		for (Long vipUid : vips) {
			if(vipUid!=null) {
				vipDao.delUserVip(vipUid);
				userService.updateUserVipVal(vipUid, false);
			}
			
		}
		return vips.size();
	}

	public int getVipIdByMonth(int month) {
		List<Integer> ids = vipDao.getVipIdByMonth(month);
		if (ids.isEmpty()) {
			return 2;
		} else {
			return ids.get(0);
		}
	}
	
	public boolean isVip(long user_id) {
		return vipDao.isVip(user_id);
	}
}
