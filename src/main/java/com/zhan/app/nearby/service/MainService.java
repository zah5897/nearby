package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.UserDao;
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
	private UserDao userDao;

	@Resource
	CityService cityService;

	public ModelMap foud_users(Long user_id, Integer page_size, Integer gender) {

		int realCount;
		if (page_size == null || page_size <= 0) {
			realCount = 5;
		} else {
			realCount = page_size;
		}
		if (user_id == null) {
			user_id = 0l;
		}

		if (gender == null) {
			gender = -1;
		}
		ModelMap result = ResultUtil.getResultOKMap();
		List<User> users = userDao.getRandomUser(user_id, realCount, gender);
		if (users != null) {
			for (User u : users) {
				ImagePathUtil.completeAvatarPath(u, true);
			}
		}
		result.put("users", users);
		return result;
	}

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
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "city_id err");
		}
		City city = cityService.getFullCity(city_id);
		if (city == null) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "city not found");
		}

		List<UserDynamic> dynamics;

		if (city.getParent_id() == 0) {
			dynamics = userDynamicDao.getHomeFoundSelected(user_id, last_id, realCount, city_id, false);
		} else {
			dynamics = userDynamicDao.getHomeFoundSelected(user_id, last_id, realCount, city_id, true);
		}

		ModelMap result = ResultUtil.getResultOKMap();
		if (dynamics == null || dynamics.size() < realCount) {
			result.put("hasMore", false);
			result.put("last_id", 0);
			if (last_id <= 0 && dynamics.size() < realCount) {
				List<UserDynamic> others = userDynamicDao.getHomeFoundSelectedRandom(user_id,
						realCount - dynamics.size());
				dynamics.addAll(others);
			}
		} else {
			result.put("hasMore", true);
			result.put("last_id", dynamics.get(realCount - 1).getId());
		}

		if (dynamics != null) {
			for (UserDynamic dy : dynamics) {
				ImagePathUtil.completeAvatarPath(dy.getUser(), true);
			}
		}
		ImagePathUtil.completeImagePath(dynamics, true);
		result.put("images", dynamics);
		return result;
	}

	// public int getCityImageCount(long user_id, int city_id) {
	// return userDynamicDao.getCityImageCount(user_id,city_id);
	// }

	public int getMostByCity() {
		return userDynamicDao.getMostCityID();
	}

	public ModelMap changeRelationShip(long user_id, String token, String with_user_id,Relationship ship) {
		if (user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST);
		}
		String[] with_ids = with_user_id.split(",");
		for (String id : with_ids) {
			try {
				long with_user = Long.parseLong(id);
				User withUser = userDao.getUser(with_user);
				if (user_id == with_user || withUser == null) {
					continue;
				}
				userDao.updateRelationship(user_id, with_user, ship);
			} catch (NumberFormatException e) {
			}
		}
		return ResultUtil.getResultOKMap();
	}

}
