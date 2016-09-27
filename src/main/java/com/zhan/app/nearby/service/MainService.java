package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.dao.UserDynamicDao;

@Service
@Transactional("transactionManager")
public class MainService {
	@Resource
	private UserDynamicDao userDynamicDao;

	public List<UserDynamic> getHomeFoundSelected(long last_img_id, int page_size) {
		return getHomeFoundSelected(ImageStatus.SELECTED, last_img_id, page_size);
	}

	public List<UserDynamic> getHomeFoundSelected(ImageStatus imageStatus, long last_img_id, int page_size) {
		return userDynamicDao.getHomeFoundSelected(imageStatus, last_img_id, page_size);
	}

}
