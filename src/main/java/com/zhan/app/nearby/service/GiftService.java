package com.zhan.app.nearby.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.dao.GiftDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@Service
@Transactional("transactionManager")
public class GiftService {

	public static final int LIMIT_COUNT = 5;
	@Resource
	private GiftDao giftDao;
	private static Logger log = Logger.getLogger(GiftService.class);

	
	@Resource
	private UserService userService;
	
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

	public ModelMap loadOwn(long user_id, String aid) {
		List<GiftOwn> gifs=giftDao.getOwnGifts(user_id);
		ImagePathUtil.completeGiftsOwnPath(gifs, true);
		return ResultUtil.getResultOKMap().addAttribute("gifts", gifs);
	}

	// -----------客户端使用-----------------------
	// 赠送礼物（用户购买后直接赠送）
	public ModelMap give(long user_id, long to_user_id, int gift_id, String aid, int count) {
		if (gift_id == 0 || user_id == 0 || to_user_id == 0 || TextUtils.isEmpty(aid) || count <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		Gift gift = giftDao.load(gift_id);
		if (gift == null) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "该礼物不存在");
		}
		Map<?, ?> map = HttpService.buy(user_id, aid, gift.getPrice() * count, gift_id);
		int code = (int) map.get("code");
		if (code == 0) {
			int i = giftDao.addOwn(to_user_id, gift_id, user_id, count);
			if (i == 1) {
				return ResultUtil.getResultOKMap();
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_SYS);
			}
		} else {
			ERROR error = ERROR.ERR_FAILED;
			error.setValue(code);
			error.setErrorMsg(map.get("msg").toString());
			log.error("礼物购买失败 code="+code);
			return ResultUtil.getResultMap(error);
		}
	}

	public List<GiftOwn> loadGiftGiveList(int page, int count) {
		return giftDao.loadGiftNotice(page, count);
	}

	public List<MeiLi> loadMeiLi(int type,int pageIndex,int count) {
		if (type == 0) {
			return giftDao.loadNewRegistUserMeiLi(pageIndex,count);
		} else if (type == 1) {
			return giftDao.loadTotalMeiLi(pageIndex,count);
		} else {
			return giftDao.loadTuHao(pageIndex,count);
		}
	}
	
	//获取用户魅力值
	public int getUserMeiLiVal(long user_id){
		return giftDao.getUserMeiLiVal(user_id);
	}
	
	//获取用户财富值
	public int getUserCoins(String aid,long user_id){
		return userService.loadUserCoins(aid, user_id);
	}
	
	//获取用户被喜欢
	public int getUserBeLikeVal(long user_id){
		return giftDao.getUserBeLikeVal(user_id);
	
	}
	
	
	
}
