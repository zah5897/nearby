package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.BottleService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/bottle")
public class BottleController {

	@Resource
	private BottleService bottleService;
	@Resource
	private MainService mainService;

	@RequestMapping("send")
	public ModelMap send(Bottle bottle, String aid) {

		if (bottle.getUser_id() <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (!bottleService.checkTime(bottle)) {
			return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		}

		if (bottleService.isBlackUser(bottle.getUser_id())) {
			return ResultUtil.getResultMap(ERROR.ERR_ACCOUNT_BLACKLIST);
		}
		bottleService.send(bottle, aid);
		return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
	}

	@RequestMapping("list")
	public ModelMap list(Long user_id, Integer count, Integer look_sex, Integer lock_sex, Integer type, Integer state) {
		
		if(lock_sex!=null&&look_sex==null) {
			look_sex=lock_sex;
		}	
		return bottleService.getBottles(user_id == null ? 0 : user_id, count == null ? 5 : count, look_sex, type,
				state);
	}

	@RequestMapping("list_dm")
	public ModelMap list_dm(Long user_id, Integer count, Integer look_sex, Integer type, Integer state) {
		return bottleService.getDMBottles(user_id == null ? 0 : user_id, count == null ? 5 : count, type, state);
	}

	@RequestMapping("load")
	public ModelMap load(long user_id, long bottle_id) {
		Bottle bottle = bottleService.getBottleDetial(bottle_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottle", bottle);
		return result;
	}

	@RequestMapping("mine")
	public ModelMap mine(long user_id, Integer page, Integer page_size) {
		return bottleService.getMineBottles(user_id, page == null ? 1 : page, page_size == null ? 10 : page_size);
	}

	@RequestMapping("scan")
	public ModelMap scan(long user_id, String bottle_id) {
		return bottleService.scan(user_id, bottle_id);
	}

	@RequestMapping("delete")
	public ModelMap delete(long user_id, long bottle_id) {
		return bottleService.delete(user_id, bottle_id);
	}

	@RequestMapping("like")
	public ModelMap like(long user_id, String token, String with_user_id) {
		return bottleService.like(user_id, with_user_id);
	}

	@RequestMapping("ignore")
	public ModelMap ignore() {
		// long user_id, String token, String with_user_id
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("replay")
	public ModelMap replay(long user_id, long target, long bottle_id, String msg) {
		return bottleService.replay(user_id, target, msg, bottle_id);
	}

	
	@RequestMapping("replay_meet")
	public ModelMap replay(long user_id, long target) {
		return bottleService.replay_meet(user_id, target);
	}
	
	@RequestMapping("express/{to_user_id}")
	public ModelMap like(@PathVariable long to_user_id, long user_id, String content) {
		return bottleService.express(user_id, to_user_id, content);
	}

	@RequestMapping("meet_list/{user_id}")
	public ModelMap like(@PathVariable long user_id, Integer page, Integer count) {
		return bottleService.meetList(user_id, page, count);
	}
}
