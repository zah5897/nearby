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
import com.zhan.app.nearby.comm.BottleState;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.BottleService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.DeviceUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.RedPacketUtils;
import com.zhan.app.nearby.util.ResultUtil;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/bottle")
@Api(value = "瓶子相关")
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

		if (bottle.getType() == BottleType.MEET.ordinal()) {
			//
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}

		if (bottle.getType() == BottleType.DRAW_GUESS.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "不支持该类型瓶子");
		}

		bottle.set_from(DeviceUtil.getRequestDevice(_ua));
		if (bottle.getType() == BottleType.RED_PACKAGE.ordinal()) {
			
			if (bottle.getRed_package_count()<1) {
				return ResultUtil.getResultMap(ERROR.ERR_PARAM);
			}
			if (bottle.getRed_package_coin_total() < bottle.getRed_package_count()) {
				return ResultUtil.getResultMap(ERROR.ERR_PARAM, "每个红包不得少于1个扇贝");
			}
			
			Object coins = userService.checkOut(bottle.getUser_id(), bottle.getRed_package_coin_total(), aid).get("all_coins");
			if (coins == null) {
				return ResultUtil.getResultMap(ERROR.ERR_FAILED);
			}
			int icoin = Integer.parseInt(coins.toString());
			if (icoin < 0) {
				return ResultUtil.getResultMap(ERROR.ERR_COINS_SHORT);
			}
			
			bottle.setAnswer(String.join(",",
					RedPacketUtils.splitRedPackets(bottle.getRed_package_coin_total(), bottle.getRed_package_count())));
			bottle.setRed_package_coin_rest(bottle.getRed_package_coin_total());
		}
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

	/**
	 * 发现
	 * 
	 * @param user_id
	 * @param lat
	 * @param lng
	 * @param count
	 * @return
	 */
	@RequestMapping("upload_v2")
	public ModelMap upload_v2(Bottle bottle, String token, String _ua, String aid, String image_names) {

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

		bottle.setContent(image_names);
		bottleService.send(bottle, aid);
		ImagePathUtil.completeBottleDrawPath(bottle);
		return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
	}
	
	
	@RequestMapping("list")
	public ModelMap list(Long user_id, Integer count, Integer look_sex, Integer lock_sex, Integer type, Integer state,
			String version, String _ua,String channel) {

		if (lock_sex != null && look_sex == null) {
			look_sex = lock_sex;
		}

		if (type == null) {
			type = -1;
		}
		if (type == BottleType.DM_TXT.ordinal() || type == BottleType.DM_VOICE.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "不支持弹幕瓶子");
		}
		if (_ua.startsWith("a")) {
			if (version.compareTo(review_version)>=0) { // ios审核临界版本号
				state = BottleState.IOS_REVIEW.ordinal();
			}
		}

		user_id = user_id == null ? 0 : user_id;
		count=count == null ? 5 : count;
		return bottleService.getBottles(user_id, count, look_sex, type, state,version, _ua,channel);
	}
	private String review_version="2.0.8";
	
	@RequestMapping("set_ios_review_version")
	public ModelMap set_ios_review(String review_version) {
		 this.review_version=review_version;
		 return ResultUtil.getResultOKMap().addAttribute("review_version", review_version);
	}
	
	
	
	@RequestMapping("list_dm")
	public ModelMap list_dm(Long user_id, Integer count, Integer look_sex, Integer type, Integer state) {
		return bottleService.getDMBottles(user_id == null ? 0 : user_id, count == null ? 5 : count, type, state);
	}

	@RequestMapping("load")
	public ModelMap load(long user_id, long bottle_id) {
		Bottle bottle = bottleService.getBottleDetial(bottle_id);
		ImagePathUtil.completeBottleDrawPath(bottle);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottle", bottle);
		return result;
	}

	@RequestMapping("mine")
	public ModelMap mine(Long user_id, Integer page, Integer page_size) {
		return bottleService.getMineBottles(user_id == null ? 41 : user_id, page == null ? 1 : page,
				page_size == null ? 10 : page_size);
	}

	@RequestMapping("setProperty")
	public ModelMap setProperty(int percent) {
		this.percent = percent;
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("getProperty")
	public ModelMap getProperty() {
		return ResultUtil.getResultOKMap().addAttribute("percent", percent);
	}

	private int percent = 10;

	@RequestMapping("scan")
	public ModelMap scan(long user_id, String bottle_id) {
		return bottleService.scan(user_id, bottle_id, percent);
	}

	@RequestMapping("delete")
	public ModelMap delete(long user_id,String token, long bottle_id) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return bottleService.delete(user_id, bottle_id);
	}

	@RequestMapping("like")
	public ModelMap like(long user_id, String token, String with_user_id) {
		return bottleService.like(user_id, with_user_id);
	}

	@RequestMapping("ignore")
	public ModelMap ignore() {
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

	@RequestMapping("reward_group")
	public ModelMap reward_history(long user_id) {
		return ResultUtil.getResultOKMap().addAttribute("reward_list", bottleService.rewardHistoryGroup(user_id));
	}

	@RequestMapping("reward_list")
	public ModelMap reward_list(long user_id, Integer page, Integer count) {
		return ResultUtil.getResultOKMap().addAttribute("reward_list",
				bottleService.rewardHistory(user_id, page == null ? 1 : page, count == null ? 20 : count));
	}

	@RequestMapping("get_red_package_history")
	public ModelMap get_red_package_history(long user_id, long bottle_id) {
		return ResultUtil.getResultOKMap().addAttribute("red_package_gets",
				bottleService.getRedPackageHistory(bottle_id));
	}
}
