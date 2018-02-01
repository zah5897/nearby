package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.CityService;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImportCityUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SpringContextUtil;
import com.zhan.app.nearby.util.TextUtils;

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
	
	@RequestMapping("reset_type")
	public ModelMap reset_type() {
		cityService.reset_type();
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
	public ModelMap gps_city(HttpServletRequest request, String lat, String lng) {
		ModelMap result = ResultUtil.getResultOKMap();
		String[] city_info = null;
		if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {
			city_info = AddressUtil.getAddressByIp(IPUtil.getIpAddress(request));
		} else {
			city_info = AddressUtil.getAddressByLatLng(lat, lng);
		}

		if (city_info == null) {
			return ResultUtil.getResultMap(ERROR.ERR_SYS);
		}
		CityService cityService = ((CityService) SpringContextUtil.getBean("cityService"));
		List<City> provincesAll = cityService.list();
		if (provincesAll != null) {
			for (City city : provincesAll) {
				if (city_info[0].contains(city.getName())) {
					if (city.getChildren() != null) {
						for (City region : city.getChildren()) {
							if (city_info[1].contains(region.getName())) {
								result.put("city", region);
								break;
							}
						}
					}
					break;
				}
			}
		}
		return result;
	}

}
