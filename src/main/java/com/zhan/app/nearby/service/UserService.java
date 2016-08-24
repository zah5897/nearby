package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.dao.UserDao;


@Service
@Transactional("transactionManager")
public class UserService {
	@Resource
	private UserDao userDao;
	@Resource
	private UserCacheService userCacheService;

	public User getUser(long id) {
		return userDao.getUser(id);
	}

	public User findUserByMobile(String mobile) {
		return userDao.findUserByMobile(mobile);
	}

	// @Transactional(readOnly = true)
	// public User findUserByName(String name) {
	// return userDao.findUserByName(name);
	// }

	public void delete(long id) {
		userDao.delete(id);
	}

	public long insertUser(User user) {
		int count = userDao.getUserCountByMobile(user.getMobile());
		if (count > 0) {
			return -1l;
		}
		long id = (Long) userDao.insert(user);
//		if (id > 0) {
//			try {
//				String password = MD5Util.getMd5_16(String.valueOf(id));
//				Object resutl = Main.registUser(String.valueOf(id), password, user.getNick_name());
//				if (resutl != null) {
//					System.out.println(resutl);
//				}
//			} catch (Exception e) {
//				throw new AppException(ERROR.ERR_SYS.setNewText(" by 环信"),new RuntimeException("环信注册失败"));
//			}
//		}
		return id;
	}

	public List<?> getList() {
		return userDao.getList();
	}

	public int getUserCountByMobile(String mobile) {
		return userDao.getUserCountByMobile(mobile);
	}

	public int updateToken(User user) {
		return userDao.updateToken(user.getUser_id(), user.getToken(), user.get_ua());
	}

	public int updatePassword(String mobile, String password) {
		return userDao.updatePassword(mobile, password);
	}

	public int updateAvatar(long user_id, String newAcatar) {
		return userDao.updateAvatar(user_id, newAcatar);
	}

	public int updateLocation(long user_id, String lat, String lng) {
		int count = userDao.updateLocation(user_id, lat, lng);
		// userCacheService.cacheValidateCode(mobile, code);
		return count;
	}
	
	public int updateVisitor(long user_id, String device_token,String lat, String lng,String zh_cn) {
		int count = userDao.updateVisitor(user_id, device_token, lat, lng, zh_cn);
		return count;
	}
	
	
}
