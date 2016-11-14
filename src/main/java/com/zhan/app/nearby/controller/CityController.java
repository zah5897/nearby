package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.service.CityService;
import com.zhan.app.nearby.util.ImportCityUtil;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/city")
public class CityController {

	@Resource
	private CityService cityService;

	@RequestMapping("init")
	public ModelMap init() {
		ImportCityUtil.importCity(cityService);
		return ResultUtil.getResultOKMap();
	}
	@RequestMapping("clear_cache")
	public ModelMap clear_cache() {
		cityService.clearCache();
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("hot")
	public ModelMap hot() {
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("hot_cities", cityService.hot());
		return result;
	}

	@RequestMapping("list")
	public ModelMap list() {
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("cities", cityService.list());
		return result;
	}
}
