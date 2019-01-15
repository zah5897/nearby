package com.zhan.app.nearby.controller;

import java.util.Iterator;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.BottleService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.DeviceUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/bottle")
public class BottleController {

	@Resource
	private BottleService bottleService;
	@Resource
	private MainService mainService;
	@Resource
	private UserService userService;

	@RequestMapping("send")
	public ModelMap send(Bottle bottle, String aid, String token, String _ua) {

		if (bottle.getUser_id() <= 0 && !userService.checkLogin(bottle.getUser_id(), token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (!bottleService.checkTime(bottle)) {
			return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		}

		if (bottleService.isBlackUser(bottle.getUser_id())) {
			return ResultUtil.getResultMap(ERROR.ERR_ACCOUNT_BLACKLIST);
		}

		if (bottle.getType() == BottleType.MEET.ordinal()) {
			//
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}

		if (bottle.getType() == BottleType.DRAW_GUESS.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "不支持该类型瓶子");
		}

		if (bottle.getReward() > 0) {

			int coin = userService.loadUserCoins(aid, bottle.getUser_id());
			if (coin < bottle.getReward()) {
				return ResultUtil.getResultMap(ERROR.ERR_COINS_SHORT);
			}
			Object coins = userService.checkOut(bottle.getUser_id(), bottle.getReward(), aid).get("all_coins");
			if (coins == null) {
				return ResultUtil.getResultMap(ERROR.ERR_FAILED);
			}
			int icoin = Integer.parseInt(coins.toString());
			if (icoin < 0) {
				return ResultUtil.getResultMap(ERROR.ERR_COINS_SHORT);
			}
		}

		bottle.set_from(DeviceUtil.getRequestDevice(_ua));

		bottleService.send(bottle, aid);
		return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
	}

	/**
	 * 发现
	 * 
	 * @param user_id
	 * @param lat
	 * @param lng
	 * @param count
	 * @return
	 */
	@RequestMapping("upload")
	public ModelMap upload(DefaultMultipartHttpServletRequest multipartRequest, Bottle bottle, String token, String _ua,
			String aid) {

		if (!userService.checkLogin(bottle.getUser_id(), token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (bottle.getType() != BottleType.DRAW_GUESS.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "瓶子类型不对");
		}

		if (bottle.getReward() > 0) {

			int coin = userService.loadUserCoins(aid, bottle.getUser_id());
			if (coin < bottle.getReward()) {
				return ResultUtil.getResultMap(ERROR.ERR_COINS_SHORT);
			}
			Object coins = userService.checkOut(bottle.getUser_id(), bottle.getReward(), aid).get("all_coins");
			if (coins == null) {
				return ResultUtil.getResultMap(ERROR.ERR_FAILED);
			}
			int icoin = Integer.parseInt(coins.toString());
			if (icoin < 0) {
				return ResultUtil.getResultMap(ERROR.ERR_COINS_SHORT);
			}
		}
		bottle.set_from(DeviceUtil.getRequestDevice(_ua));
		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imagePath = ImageSaveUtils.saveBottleDraw(file);
						ModelMap result = ResultUtil.getResultOKMap();
						bottle.setContent(imagePath);
						bottleService.send(bottle, aid);
						ImagePathUtil.completeBottleDrawPath(bottle);
						result.put("bottle", bottle);
						return result;
					} catch (Exception e) {
						return ResultUtil.getResultMap(ERROR.ERR_FAILED, "图片上传失败");
					}
				}
			}
		}
		return ResultUtil.getResultMap(ERROR.ERR_PARAM, "无图片上传");
	}

	@RequestMapping("list")
	public ModelMap list(Long user_id, Integer count, Integer look_sex, Integer lock_sex, Integer type, Integer state,
			String version, String _ua) {

		if (lock_sex != null && look_sex == null) {
			look_sex = lock_sex;
		}
		return bottleService.getBottles(user_id == null ? 0 : user_id, count == null ? 5 : count, look_sex, type, state,
				version, _ua);
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
	public ModelMap replay(String aid, long user_id, long target, long bottle_id, String msg) {
		return bottleService.replay(aid, user_id, target, msg, bottle_id);
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

	@RequestMapping("answer_to_draw")
	public ModelMap answer_to_draw(Integer count) {
		return ResultUtil.getResultOKMap().addAttribute("answers", bottleService.loadAnswerToDraw(count));
	}

	@RequestMapping("reward_list")
	public ModelMap reward_history(long user_id, Integer page, Integer count) {
		return ResultUtil.getResultOKMap().addAttribute("reward_list",
				bottleService.rewardHistory(user_id, page, count));
	}

}
