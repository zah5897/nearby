package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/main")
public class MainController {
	@Resource
	private MainService mainService;
	@Resource
	private UserDynamicService imageService;

	/**
	 * 发现
	 * 
	 * @param user_id
	 * @param lat
	 * @param lng
	 * @param count
	 * @return
	 */
	@RequestMapping("found")
	public ModelMap found(Long user_id, Long last_image_id, Integer count, String lat, String lng) {

		if (user_id == null || user_id < 0l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户id异常");
		}

		if (last_image_id == null || last_image_id < 0) {
			last_image_id = 0l;
		}

		int realCount;
		if (count == null || count <= 0) {
			realCount = 20;
		} else {
			realCount = count;
		}

		List<UserDynamic> dynamics = mainService.getHomeFoundSelected(last_image_id, realCount);
		ImagePathUtil.completeImagePath(dynamics, true);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("images", dynamics);
		return result;
	}

	@RequestMapping("praise")
	public ModelMap praise(Long user_id, Long image_id) {
		int count = imageService.praiseDynamic(image_id);
		ModelMap result;
		if (count > 0) {
			result = ResultUtil.getResultOKMap();
			result.put("praise_count", count);
		} else {
			result = ResultUtil.getResultMap(ERROR.ERR_FAILED, "图片找不到");
		}
		return result;
	}
}
