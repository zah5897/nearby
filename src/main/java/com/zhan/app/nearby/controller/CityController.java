package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.dao.CityDao;
import com.zhan.app.nearby.service.CityService;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.ImportCityUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SpringContextUtil;

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

	@RequestMapping("set_user_city")
	public ModelMap set_user_city(Long user_id, Integer city_id) {
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("cities", cityService.list());
		return result;
	}

	@RequestMapping("gps_city")
	public ModelMap gps_city(String lat, String lng) {
		ModelMap result = ResultUtil.getResultOKMap();
		String[] city_info = AddressUtil.getAddressByLatLng(lat, lng);

		CityService cityService = ((CityService) SpringContextUtil.getBean("cityService"));
		List<City> provincesAll = cityService.list();
		if (provincesAll != null) {
			for (City city : provincesAll) {
				if (city_info[2].contains(city.getName())) {
					result.put("city", city);
					break;
				}
			}
		}
		return result;
	}

}
