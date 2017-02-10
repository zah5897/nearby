package com.zhan.app.nearby.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.cache.InfoCacheService;
import com.zhan.app.nearby.dao.CityDao;

@Service
@Transactional("transactionManager")
public class CityService {
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
			e.printStackTrace();
		}
		if (provinces != null && provinces.size() > 0) {
			return provinces;
		}
		List<City> provincesAll = cityDao.list();
		provinces = new ArrayList<>();
		for (City city : provincesAll) {
			if (city.getType() == 0) {
				if (city.getChildren() == null) {
					city.setChildren(new ArrayList<>());
					City cityPraent = new City();
					cityPraent.setType(city.getType());
					cityPraent.setId(city.getId());
					cityPraent.setName(city.getName());
					cityPraent.setParent_id(city.getParent_id());
					city.getChildren().add(cityPraent);

				}
				for (City city_child : provincesAll) {
					if (city_child.getType() == 1 && city.getId() == city_child.getParent_id()) {
						city.getChildren().add(city_child);
					}
				}
				provinces.add(city);
			}
		}
		try {
			infoCacheService.setCities(CITY_LIST, provinces);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return provinces;
	}

	public List<City> hot() {
		List<City> provinces = null;
		try {
			provinces = infoCacheService.getCities(CITY_HOT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (provinces != null && provinces.size() > 0) {
			return provinces;
		}

		provinces = new ArrayList<>();

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
			e.printStackTrace();
		}
		return provinces;
	}

	public void clearCache() {
		infoCacheService.clear(CITY_HOT);
		infoCacheService.clear(CITY_LIST);
	}

	public City getCity(int birth_city_id) {
		// TODO Auto-generated method stub
		List<City> all=list();
		if(all!=null&&birth_city_id>0){
			for(City c:all){
				if(c.getId()==birth_city_id){
					return c;
				}
				if(c.getChildren()!=null){
					for(City child:c.getChildren()){
						if(child.getId()==birth_city_id){
							return child;
						}
					}
				}
			}
		}
		return null;
	}

}
