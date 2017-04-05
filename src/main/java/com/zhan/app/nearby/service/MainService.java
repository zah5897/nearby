package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@Service
@Transactional("transactionManager")
public class MainService {
	@Resource
	private UserDynamicDao userDynamicDao;

	@Resource
	CityService cityService;

	public ModelMap getHomeFoundSelected(Long user_id, Long last_id, Integer page_size, Integer city_id) {
		
		if (last_id == null || last_id < 0) {
			last_id = 0l;
		}

		int realCount;
		if (page_size == null || page_size <= 0) {
			realCount = 20;
		} else {
			realCount = page_size;
		}
		if (user_id == null) {
			user_id = 0l;
		}
		if (city_id == null || city_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"city_id err");
		}
		City city=cityService.getFullCity(city_id);
		if(city==null){
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"city not found");
		}
		
		System.out.println("city name="+city.getName());
		System.out.println("city parent_id="+city.getParent_id());
		List<UserDynamic> dynamics;
		if(city.getParent_id()>0){
			
			int hasCount=userDynamicDao.getSelectedCityCount(city_id);
			if(hasCount>=realCount){
				dynamics=userDynamicDao.getHomeFoundSelected(user_id, last_id,realCount, city_id,true);
			}else{
				dynamics=userDynamicDao.getHomeFoundSelected(user_id, last_id,realCount, city.getParent_id(),false);
			}
		}else{
			dynamics=userDynamicDao.getHomeFoundSelected(user_id, last_id,realCount, city.getId(),false);
		}
		if (dynamics != null) {
			for (UserDynamic dy : dynamics) {
				ImagePathUtil.completeAvatarPath(dy.getUser(), true);
			}
		}
		ImagePathUtil.completeImagePath(dynamics, true);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("images", dynamics);
		if (dynamics == null || dynamics.size() < realCount) {
			result.put("hasMore", false);
			result.put("last_id", 0);
		} else {
			result.put("hasMore", true);
			result.put("last_id", dynamics.get(realCount - 1).getId());
		}
		return result;
	}

//	public int getCityImageCount(long user_id, int city_id) {
//		return userDynamicDao.getCityImageCount(user_id,city_id);
//	}

	public int getMostByCity() {
		return userDynamicDao.getMostCityID();
	}

}
