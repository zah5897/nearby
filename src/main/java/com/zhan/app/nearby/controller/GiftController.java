package com.zhan.app.nearby.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.service.GiftService;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/gift")
public class GiftController {

	private static Logger log = Logger.getLogger(GiftController.class);
	// -------------管理后台使用----------------------
	@Resource
	private GiftService giftService;

	@RequestMapping("list")
	public ModelMap list() {
		return giftService.list();
	}

	@RequestMapping("del")
	public ModelMap del(long id) {
		return giftService.delete(id);
	}

	@RequestMapping("add")
	public ModelMap add(Gift gift, HttpServletRequest request) {
		if (request instanceof DefaultMultipartHttpServletRequest) {
			DefaultMultipartHttpServletRequest multiRequest = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = multiRequest.getFileNames();
			if (iterator.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imageName = ImageSaveUtils.saveGiftImages(file);
						gift.setImage_url(imageName);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}
		return giftService.save(gift);
	}

	// 获取送到的礼物列表
	@RequestMapping("own")
	public ModelMap own(long user_id, Long target_user_id, String aid) {
		if (target_user_id == null || target_user_id < 1) {
			target_user_id = user_id;
		}
		return giftService.loadOwn(target_user_id, aid);
	}

	@RequestMapping("received")
	public ModelMap received(long user_id, String aid) {
		return giftService.loadOwn(user_id, aid);
	}

	// -----------------客户端使用---------------
	@RequestMapping("send")
	public Map<?, ?> send(long user_id, long to_user_id, int gift_id, String aid, int count) {
		return giftService.give(user_id, to_user_id, gift_id, aid, count);
	}

	@RequestMapping("val")
	public ModelMap allVal(long user_id, String token, String aid) {
		return giftService.getVal(user_id, token, aid);
	}

	
	
	//获取验证码
	@RequestMapping("get_exchange_validate_code")
	public ModelMap get_exchange_validate_code(long user_id,String token,String aid,Integer code_type) {
		return giftService.get_exchange_validate_code(user_id,token,code_type);
	}
	// 礼物扇贝值兑换钻石
	@RequestMapping("exchange_diamond")
	public ModelMap exchange_diamond(long user_id, String token, String aid, int diamond,String code) {
		return giftService.exchange_diamond(user_id, token, aid, diamond,code);
	}

	// 礼物扇贝值兑换钻石
	@RequestMapping("exchange_diamond_history")
	public ModelMap exchange_diamond_history(long user_id, String token, Integer page,Integer count) {
		return giftService.exchange_diamond_history(user_id, token, page==null?1:page, count==null?20:count);
	}
	// 送礼公告
	@RequestMapping("notice")
	public ModelMap notice(String aid, long user_id, Integer page, Integer count) {
		List<GiftOwn> owns = giftService.notice(aid, user_id, page, count);
		ModelMap r = ResultUtil.getResultOKMap().addAttribute("notice", owns);
		if (owns.isEmpty()) {
			r.addAttribute("hasMore", false);
		} else if (count == null) {
			if (owns.size() == 10) {
				r.addAttribute("hasMore", true);
			} else {
				r.addAttribute("hasMore", false);
			}
		} else if (owns.size() == count) {
			r.addAttribute("hasMore", true);
		} else {
			r.addAttribute("hasMore", false);
		}
		return r;
	}

}
