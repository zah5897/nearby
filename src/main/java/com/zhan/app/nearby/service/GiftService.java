package com.zhan.app.nearby.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.InfoCacheService;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.dao.GiftDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.RandomCodeUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SMSHelper;

@Service
public class GiftService {

	public static final int LIMIT_COUNT = 5;
	@Resource
	private GiftDao giftDao;
	private static Logger log = Logger.getLogger(GiftService.class);

	@Resource
	private UserService userService;

	@Resource
	private InfoCacheService infoCacheService;
	@Resource
	private UserCacheService userCacheService;

	@Transactional
	public ModelMap save(Gift gift) {
		if (gift.getId() > 0) {
			giftDao.update(gift);
		} else {
			giftDao.insert(gift);
		}
		ImagePathUtil.completeGiftPath(gift, true);
		return ResultUtil.getResultOKMap().addAttribute("gift", gift);
	}

	public ModelMap list() {
		List<Gift> gifts = giftDao.listGifts();
		ImagePathUtil.completeGiftsPath(gifts, true);
		return ResultUtil.getResultOKMap().addAttribute("gifts", gifts);
	}

	@Transactional
	public ModelMap delete(long id) {
		giftDao.delete(id);
		return ResultUtil.getResultOKMap().addAttribute("id", id);
	}

	public ModelMap loadOwn(long user_id, String aid) {
		List<GiftOwn> gifs = giftDao.getOwnGifts(user_id);
		List<GiftOwn> gifsGroup = new ArrayList<GiftOwn>();
		if (gifs != null) {
			for (GiftOwn gif : gifs) {
				if (gifsGroup.contains(gif)) {
					int index = gifsGroup.indexOf(gif);
					GiftOwn g = gifsGroup.get(index);
					g.setCount(g.getCount() + gif.getCount());
				} else {
					gifsGroup.add(gif);
				}
			}
		}
		ImagePathUtil.completeGiftsOwnPath(gifs, true);
		return ResultUtil.getResultOKMap().addAttribute("gifts", gifsGroup);
	}

	// -----------客户端使用-----------------------
	// 赠送礼物（用户购买后直接赠送）
	public Map<String, Object> give(long user_id, long to_user_id, int gift_id, String aid, int count) {
		if (gift_id == 0 || user_id == 0 || to_user_id == 0 || TextUtils.isEmpty(aid) || count <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		Gift gift = giftDao.load(gift_id);
		if (gift == null) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "该礼物不存在");
		}
		int gift_coins = gift.getPrice() * count;
		Map<String, Object> map = HttpService.buy(user_id, aid, gift_coins, gift_id);
		int code = Integer.parseInt(map.get("code").toString());
		if (code == 0) {
			int i = giftDao.addOwn(to_user_id, gift_id, user_id, count);
			giftDao.addGiftCoins(to_user_id, gift_coins);
			if (i == 1) {
				// 通知对方收到某某的礼物
				Map<String, String> ext = new HashMap<String, String>();
				ext = new HashMap<String, String>();
				// ext.put("nickname", with_user.getNick_name());
				// ext.put("avatar", with_user.getAvatar());
				// ext.put("origin_avatar", with_user.getOrigin_avatar());
				// ext.put("description", "");
				String desc = "赠送了一个礼物给你";
				BaseUser u = userService.getBasicUser(user_id);

				infoCacheService.clear(InfoCacheService.GIFT_SEND_NOTICE);

				// Main.sendCmdMessage("sys", users, ext);
				Main.sendTxtMessage(Main.SYS, new String[] { String.valueOf(to_user_id) }, u.getNick_name() + desc, ext,
						PushMsgType.TYPE_RECEIVER_GIFT);
				Map<String, Object> result = HttpService.queryUserCoins(user_id, aid);
				return result;
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_SYS);
			}
		} else {
			ERROR error = ERROR.ERR_FAILED;
			error.setValue(code);
			error.setErrorMsg(map.get("msg").toString());
			log.error("礼物购买失败 code=" + code);
			return ResultUtil.getResultMap(error);
		}
	}

	public List<GiftOwn> loadGiftGiveList(int page, int count) {
		return giftDao.loadGiftNotice(page, count);
	}

	/**
	 * 魅力榜
	 * 
	 * @param type
	 * @param pageIndex
	 * @param count
	 * @return
	 */
	public List<MeiLi> loadMeiLi(int pageIndex, int count) {
		return giftDao.loadTotalMeiLi(pageIndex, count);
	}

	/**
	 * 土豪榜
	 * 
	 * @param type
	 * @param pageIndex
	 * @param count
	 * @return
	 */
	public List<MeiLi> loadTuHao(int pageIndex, int count) {
		return giftDao.loadTuHao(pageIndex, count);
	}

	// 获取用户魅力值
	public int getUserMeiLiVal(long user_id) {
		return giftDao.getUserMeiLiVal(user_id);
	}

	// 获取用户财富值
	public int getUserCoins(String aid, long user_id) {
		return userService.loadUserCoins(aid, user_id);
	}

	// 获取用户被喜欢
	public int getUserBeLikeVal(long user_id) {
		return giftDao.getUserBeLikeVal(user_id);

	}

	public ModelMap getVal(long user_id, String token, String aid) {
		int val = giftDao.getVal(user_id);
		return ResultUtil.getResultOKMap().addAttribute("value", val);
	}

	public int minusCoins(long user_id, int to_minus_coins) {
		int val = giftDao.getVal(user_id);
		int newCoins = val - to_minus_coins;
		if (newCoins < 0) {
			return -1;
		} else {
			return giftDao.updateGiftCoins(user_id, newCoins);
		}
	}

	// 送礼公告
	public List<GiftOwn> notice(String aid, long user_id, Integer page, Integer count) {
		if (page == null || page < 1) {
			page = 1;
		}
		if (count == null) {
			count = 10;
		}
		List<GiftOwn> owns = null;
		if (page == 1) {
			owns = infoCacheService.getGiftSendNoticeCache();
		}
		if (owns != null && owns.size() == count) {
			return owns;
		}

		owns = giftDao.getGifNotice(user_id, page, count);
		for (GiftOwn own : owns) {
			own.setSender(userService.getBasicUser(own.getGive_uid()));
			ImagePathUtil.completeAvatarPath(own.getSender(), true);
			BaseUser me = userService.getBasicUser(user_id);
			ImagePathUtil.completeAvatarPath(me, true);
			own.setReceiver(me);
			ImagePathUtil.completeGiftPath(own, true);
		}

		if (owns.size() == count && page == 1) {
			String json = JSONUtil.writeValueAsString(owns);
			infoCacheService.cacheGiftSendNotice(json);
		}
		return owns;
	}

	@Transactional
	public ModelMap exchange_diamond(long user_id, String token, String aid, int diamond, String code) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		String mobile = userService.getUserMobileById(user_id);

		if (mobile == null) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (!userCacheService.valideExchageCode(mobile, code)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "验证码错误");
		}
		int val = giftDao.getVal(user_id);
		if (diamond > val) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "钻石数量不足");
		}
		int newVal = val - diamond;
		giftDao.updateGiftCoins(user_id, newVal);

		Exchange exchange = new Exchange();
		exchange.setUser_id(user_id);
		exchange.setAid(aid);
		exchange.setCreate_time(new Date());
		exchange.setDiamond_count(diamond);
		exchange.setState(ExchangeState.IN_EXCHANGE.ordinal());
		giftDao.addExchangeHistory(exchange);
		userService.modifyUserExtra(user_id, aid, (int) (diamond * 0.3), 1);
		return ResultUtil.getResultOKMap("提交成功").addAttribute("value", newVal);
	}

	@Transactional
	public ModelMap get_exchange_validate_code(long user_id, String token, Integer code_type) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		String mobile = userService.getUserMobileById(user_id);

		if (userCacheService.getExchageCodeCacheCount(mobile) >= 5) {
			return ResultUtil.getResultMap(ERROR.ERR_SMS_CODE_LIMIT);
		}

		String code = RandomCodeUtil.randomCode(6);

		if (code_type == null) {
			code_type = 0;
		}
		if (code_type == -1000) {
			userCacheService.cacheExchageValidateCode(mobile, code, code_type);
			return ResultUtil.getResultOKMap().addAttribute("validate_code", code);
		}
		ModelMap data = ResultUtil.getResultOKMap();
		HashMap<String, Object> result = SMSHelper.smsExchangeCode(mobile, code);
		boolean smsOK = SMSHelper.isSuccess(result);
		if (smsOK) {
			userCacheService.cacheExchageValidateCode(mobile, code, code_type);
			data.put("validate_code", code);
		} else {
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "获取验证码次数过多，明天再试。");
		}
		return data;
	}

	public ModelMap exchange_diamond_history(long user_id, String token, int i, int j) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		List<Exchange> data = giftDao.loadExchangeDiamondHistory(user_id, i, j);
		for (Exchange e : data) {
			e.transferToDesc();// 转成描述语句
		}
		return ResultUtil.getResultOKMap().addAttribute("data", data).addAttribute("total_exchange_coin",
				giftDao.getTotalExchangeDiamond(user_id));
	}
}
