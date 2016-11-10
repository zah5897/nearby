package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.util.ImagePathUtil;

@Service
@Transactional("transactionManager")
public class MainService {
	@Resource
	private UserDynamicDao userDynamicDao;

	public List<UserDynamic> getHomeFoundSelected(long last_img_id, int page_size) {
		List<UserDynamic> dynamics = getHomeFoundSelected(ImageStatus.SELECTED, last_img_id, page_size);
		if (dynamics != null) {
			for (UserDynamic dy : dynamics) {
				ImagePathUtil.completeAvatarPath(dy.getUser(), true);
			}
		}
		ImagePathUtil.completeImagePath(dynamics, true);
		return dynamics;
	}

	public List<UserDynamic> getHomeFoundSelected(ImageStatus imageStatus, long last_img_id, int page_size) {
		try {
			return userDynamicDao.getHomeFoundSelected(imageStatus, last_img_id, page_size);
		} catch (Exception e) {
			return null;
		}
	}

}
