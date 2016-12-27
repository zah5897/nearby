package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.util.ImagePathUtil;

@Service
@Transactional("transactionManager")
public class MainService {
	@Resource
	private UserDynamicDao userDynamicDao;

	@Resource
	CityService cityService;

	public List<UserDynamic> getHomeFoundSelected(long user_id, long last_img_id, int page_size, int city_id) {
		List<UserDynamic> dynamics = userDynamicDao.getHomeFoundSelected(user_id, ImageStatus.SELECTED, last_img_id,
				page_size, city_id);
		if (dynamics != null) {
			for (UserDynamic dy : dynamics) {
				ImagePathUtil.completeAvatarPath(dy.getUser(), true);
			}
		}
		ImagePathUtil.completeImagePath(dynamics, true);
		return dynamics;
	}

	public int getMostByCity() {
		return userDynamicDao.getMostCityID();
	}

}
