package com.zhan.app.nearby.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.cache.InfoCacheService;
import com.zhan.app.nearby.dao.CityDao;

@Service
public class CityService {
	
	private static Logger log = Logger.getLogger(CityService.class);
	
	private static final String CITY_LIST = "city_list";
	private static final String CITY_HOT = "city_hot";
	@Resource
	private CityDao cityDao;

	@Resource
	private InfoCacheService infoCacheService;

	public void insert(City city) {
		cityDao.insert(city);
	}

	public List<City> list() {

 		List<City> provinces = null;
		try {
			provinces = infoCacheService.getCities(CITY_LIST);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if (provinces != null && provinces.size() > 0) {
			return provinces;
		}
		List<City> provincesAll = cityDao.list();
		provinces = new ArrayList<City>();
		for (City city : provincesAll) {
			if (city.getType() == 0) {
				if (city.getChildren() == null) {
					city.setChildren(new ArrayList<City>());
					City cityPraent = new City();
					cityPraent.setType(city.getType());
					cityPraent.setId(city.getId());
					cityPraent.setName(city.getName());
					cityPraent.setParent_id(city.getParent_id());
					city.getChildren().add(cityPraent);

				}
				provinces.add(city);
			}else{
				for (City province : provinces) {
					if (city.getParent_id()==province.getId()) {
						province.getChildren().add(city);
					}
				}
			}
		}
		try {
			infoCacheService.setCities(CITY_LIST, provinces);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return provinces;
	}

	public List<City> hot() {
		List<City> provinces = null;
		try {
			provinces = infoCacheService.getCities(CITY_HOT);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if (provinces != null && provinces.size() > 0) {
			return provinces;
		}

		provinces = new ArrayList<City>();

		String[] hotTag = { "北京", "上海", "深圳", "杭州", "南京", "成都", "武汉", "长沙", "重庆" };

		List<City> provincesAll = cityDao.list();

		for (String tag : hotTag) {
			for (City city : provincesAll) {
				if (city.getName().contains(tag)) {
					provinces.add(city);
					break;
				}
			}
		}
		try {
			infoCacheService.setCities(CITY_HOT, provinces);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return provinces;
	}

	public void clearCache() {
		infoCacheService.clear(CITY_HOT);
		infoCacheService.clear(CITY_LIST);
	}

	public City getSimpleCity(int birth_city_id) {
		return cityDao.getCityById(birth_city_id);
	}

	public City getFullCity(int city_id) {
		return cityDao.getCityById(city_id);
	}
	public int getChildCount(int city_id) {
		return cityDao.getChildCount(city_id);
	}

	public void reset_type() {
		// 省
		List<City> provincesAll = cityDao.listByParentId(0);
		for (City city : provincesAll) {
			// 市
			List<City> cities = cityDao.listByParentId(city.getId());
			for (City cy : cities) {
				// 去
				List<City> cs = cityDao.listByParentId(cy.getId());
				for (City c : cs) {
					cityDao.updateType(c.getId(), 2);
				}
			}

		}
	}

	public City getCityByName(String city) {
		return cityDao.getCityByName(city);
	}

}
