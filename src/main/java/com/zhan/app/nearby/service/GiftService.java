package com.zhan.app.nearby.service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.controller.ImageController;
import com.zhan.app.nearby.dao.GiftDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.HttpsUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.PropertiesUtil;
import com.zhan.app.nearby.util.ResultUtil;

@Service
@Transactional("transactionManager")
public class GiftService {

	public static final int LIMIT_COUNT = 5;
	private String MODULE_PAY_URL;
	@Resource
	private GiftDao giftDao;
	private static Logger log = Logger.getLogger(GiftService.class);

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

	public ModelMap delete(long id) {
		giftDao.delete(id);
		return ResultUtil.getResultOKMap().addAttribute("id", id);
	}

	public ModelMap buy(int gift_id, long user_id, String aid) {

		if (gift_id == 0 || user_id == 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		Gift gift = giftDao.load(gift_id);
		if (gift == null) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "该礼物不存在");
		}
		if (TextUtils.isEmpty(aid)) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "app not exist");
		}
		if (TextUtils.isEmpty(MODULE_PAY_URL)) {
			Properties prop = PropertiesUtil.load("config.properties");
			String value = PropertiesUtil.getProperty(prop, "MODULE_PAY_URL");
			MODULE_PAY_URL = value;
		}
		String result = null;
		try {
			result = HttpsUtil.sendHttpsPost(MODULE_PAY_URL + "?user_id=" + user_id + "&aid=" + aid + "&int_amount="
					+ gift.getPrice() + "&ext=" + gift_id);
		} catch (Exception e) {
			log.error("购买失败" + e.getMessage());
		}
		if (!TextUtils.isEmpty(result)) {
			Map<?, ?> map = JSONUtil.jsonToMap(result);
			if (map.get("code").toString().equals("0")) {
				GiftOwn ownGift = giftDao.getOwnGift(user_id, gift_id);
				if (ownGift != null) {
					giftDao.updateOwnCount(user_id, gift_id, ownGift.getCount() + 1);
				} else {
					giftDao.addOwn(user_id, gift_id);
				}
			}
			return ResultUtil.getResultOKMap();
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}

	public ModelMap own(long user_id, String aid) {
		return ResultUtil.getResultOKMap().addAttribute("gifts", giftDao.getOwnGifts(user_id));
	}
}
