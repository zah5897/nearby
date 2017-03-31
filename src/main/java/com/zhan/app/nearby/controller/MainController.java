package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.service.CityService;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/main")
public class MainController {
	@Resource
	private MainService mainService;
	@Resource
	private UserDynamicService userDynamicService;

	@Resource
	private DynamicMsgService dynamicMsgService;
	@Resource
	private CityService cityService;

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
	public ModelMap found(Long user_id, Long last_id, Integer count, String lat, String lng, Integer city_id) {
		return mainService.getHomeFoundSelected(user_id, last_id, count, city_id);
	}

	@RequestMapping("report")
	public ModelMap report(Long user_id, String token, Long dynamic_id) {
		return ResultUtil.getResultOKMap();
	}
}
