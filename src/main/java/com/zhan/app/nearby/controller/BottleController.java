package com.zhan.app.nearby.controller;

import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/bottle")
public class BottleController {

	@Resource
	private BottleService bottleService;
	@Resource
	private MainService mainService;

	@RequestMapping("send")
	public ModelMap send(Bottle bottle, HttpServletRequest request) {

		if (bottle.getUser_id() <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (!bottleService.checkTime(bottle)) {
			return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		}
		
		if(bottleService.isBlockUser(bottle.getUser_id())) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED,"该帐号状态异常");
		}
		
		if (bottle.getType() == BottleType.IMG.ordinal() && (request instanceof DefaultMultipartHttpServletRequest)) {
			DefaultMultipartHttpServletRequest multiRequest = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = multiRequest.getFileNames();
			if (iterator.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imageName = ImageSaveUtils.saveBottleImages(file);
						bottle.setContent(imageName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}else if(bottle.getType() == BottleType.TXT.ordinal()) {
			//敏感词过滤
			String newContent=BottleKeyWordUtil.filterContent(bottle.getContent());
			bottle.setContent(newContent);
		}
		bottleService.insert(bottle);
		return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
	}

	@RequestMapping("list")
	public ModelMap list(Long user_id, Integer count, Integer look_sex, Integer type,Integer state) {
		return bottleService.getBottles(user_id == null ? 0 : user_id, count == null ? 5 : count, look_sex, type,state);
	}

	@RequestMapping("load")
	public ModelMap load(long user_id, long bottle_id) {
		Bottle bottle = bottleService.getBottleDetial(bottle_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottle", bottle);
		return result;
	}

	@RequestMapping("mine")
	public ModelMap mine(long user_id, Long last_id, Integer page_size) {
		return bottleService.getMineBottles(user_id, last_id == null ? 0 : last_id, page_size == null ? 20 : page_size);
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

	@RequestMapping("express/{to_user_id}")
	public ModelMap like(@PathVariable long to_user_id, long user_id, String content) {
		return bottleService.express(user_id, to_user_id, content);
	}
}
