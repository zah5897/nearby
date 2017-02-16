package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
@Transactional("transactionManager")
public class UserService {
	@Resource
	private UserDao userDao;
	@Resource
	private UserCacheService userCacheService;
	@Resource
	private CityService cityService;

	public User getBasicUser(long id) {
		return userDao.getUser(id);
	}

	public User findUserByMobile(String mobile) {
		return userDao.findUserByMobile(mobile);
	}

	public User findUserByDeviceId(String deviceId) {
		return userDao.findUserByDeviceId(deviceId);
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
		// if (id > 0) {
		// try {
		// String password = MD5Util.getMd5_16(String.valueOf(id));
		// Object resutl = Main.registUser(String.valueOf(id), password,
		// user.getNick_name());
		// if (resutl != null) {
		// System.out.println(resutl);
		// }
		// } catch (Exception e) {
		// throw new AppException(ERROR.ERR_SYS.setNewText(" by 环信"),new
		// RuntimeException("环信注册失败"));
		// }
		// }
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

	public int updateVisitor(long user_id, String device_token, String lat, String lng, String zh_cn) {
		int count = userDao.updateVisitor(user_id, device_token, lat, lng, zh_cn);
		return count;
	}

	public User getUserDetailInfo(long user_id, int count) {
		User user = userDao.getUserDetailInfo(user_id);
		if (user != null) {

			if(user.getCity_id()>0){
				user.setCity(cityService.getCity(user.getCity_id()));
			}
			if(user.getBirth_city_id()>0){
				 user.setBirth_city(cityService.getCity(user.getBirth_city_id()));
			}
			
			
			
			ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址
			// 隐藏系统安全信息
			user.hideSysInfo();
			// // 补全 tag 属性
			// setTagByIds(user);
			// 补全images属性

			if (count <= 0) {
				count = 4;
			}
			// List<Image> userImages = userInfoDao.getUserImages(user_id, 0,
			// count);
			// ImagePathUtil.completeImagePath(userImages, true); // 补全图片路径
			// user.setImages(userImages);
		}
		return user;
	}

	public int modify_info(long user_id, String nick_name, String birthday, String job, String height, String weight,
			String signature, String my_tags, String interests, String animals, String musics, String weekday_todo,
			String footsteps, String want_to_where, boolean isNick_modify,Integer birth_city_id) {
		return userDao.modify_info(user_id, nick_name, birthday, job, height, weight, signature, my_tags, interests,
				animals, musics, weekday_todo, footsteps, want_to_where,birth_city_id);
	}

	public int visitorToNormal(User user) {
		return userDao.visitorToNormal(user.getUser_id(), user.getMobile(), user.getPassword(), user.getToken(),
				user.getNick_name(), user.getBirthday(), user.getSex(), user.getAvatar());
	}

	public void uploadLocation(final String ip, final long user_id, String lat, String lng) {
		if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {
			new Thread() {
				@Override
				public void run() {
					String[] lat_lng = AddressUtil.getLatLngByIP(ip);
					if (lat_lng == null) {
						return;
					}
					if (TextUtils.isEmpty(lat_lng[0]) || TextUtils.isEmpty(lat_lng[1])) {
						return;
					}
					userDao.updateLocation(user_id, lat_lng[0], lat_lng[1]);
				}
			}.start();
		} else {
			userDao.updateLocation(user_id, lat, lng);
		}
	}

	public void uploadToken(long user_id, String token, String zh_cn) {
		userDao.uploadToken(user_id, token, zh_cn);
	}

	public String getDeviceToken(long user_id) {
		return userDao.getDeviceToken(user_id);
	}

	public void setCity(Long user_id, Integer city_id) {
		
		if(user_id==null||user_id>0){
			return;
		}
		
		if(city_id==null){
			return;
		}
		
		  userDao.setCity(user_id,city_id);
	}

	
	
}
