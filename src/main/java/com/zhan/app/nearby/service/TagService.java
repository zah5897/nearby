package com.zhan.app.nearby.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.dao.UserInfoDao;

@Service
@Transactional("transactionManager")
public class TagService {
	@Resource
	private UserInfoDao userInfoDao;

	public long saveUserImage(Image image) {
		return userInfoDao.saveImage(image);
	}

	public User getUserInfo(long user_id) {
		User user = userInfoDao.getUserInfo(user_id);
		if (user != null) {

		}
		return user;
	}
}
