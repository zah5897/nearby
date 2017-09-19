package com.zhan.app.nearby.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.comm.MessageAction;
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
		ImagePathUtil.completeAvatarsPath(users, true);
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
		List<UserDynamic> dynamics = userDynamicDao.getHomeFoundSelected(user_id, last_id, realCount, city);

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
		ImagePathUtil.completeDynamicsPath(dynamics, true);
		result.put("images", dynamics);
		return result;
	}

	// public int getCityImageCount(long user_id, int city_id) {
	// return userDynamicDao.getCityImageCount(user_id,city_id);
	// }

	public int getMostByCity() {
		return userDynamicDao.getMostCityID();
	}

	public ModelMap changeRelationShip(long user_id, String token, String with_user_id, Relationship ship) {
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
				changeRelationShip(user_id, withUser, ship);
			} catch (NumberFormatException e) {
			}
		}
		return ResultUtil.getResultOKMap();
	}

	public ModelMap changeRelationShip(long user_id, User with_user, Relationship ship) {
		userDao.updateRelationship(user_id, with_user.getUser_id(), ship);
		// 判断对方是否也已经喜欢我了
		if (ship == Relationship.LIKE) {
			User user = userDao.getUserSimple(user_id).get(0);
			int count = userDao.isLikeMe(user_id, with_user.getUser_id());
			if (count > 0) { // 对方喜欢我了，这个时候我也喜欢对方了，需要互相发消息
				ImagePathUtil.completeAvatarPath(with_user, true);
				ImagePathUtil.completeAvatarPath(user, true);

				// 发送给对方
				Map<String, String> ext = new HashMap<String, String>();
				ext.put("nickname", user.getNick_name());
				ext.put("avatar", user.getAvatar());
				ext.put("origin_avatar", user.getOrigin_avatar());
				Object result = Main.sendTxtMessage(String.valueOf(user.getUser_id()),
						new String[] { String.valueOf(with_user.getUser_id()) }, "很高兴遇见你", ext);
				if (result != null) {
					System.out.println(result);
				}

				// 发送给自己

				ext = new HashMap<String, String>();
				ext.put("nickname", with_user.getNick_name());
				ext.put("avatar", with_user.getAvatar());
				ext.put("origin_avatar", with_user.getOrigin_avatar());
				result = Main.sendTxtMessage(String.valueOf(with_user.getUser_id()),
						new String[] { String.valueOf(user.getUser_id()) }, "很高兴遇见你", ext);
				if (result != null) {
					System.out.println(result);
				}

				// 系统推"附近有人喜欢了你"给对方
				String msg = "附近有人喜欢了你！";
				ext.put("msg", msg);

				result = Main.sendTxtMessage(Main.SYS, new String[] { String.valueOf(with_user.getUser_id()) }, msg,
						ext);
				if (result != null) {
					System.out.println(result);
				}

			}
			// else {
			// // 发现对方没喜欢我
			// // 需要申请添加好友
			// Object result = Main.addFriend(String.valueOf(user.getUser_id()),
			// String.valueOf(with_user.getUser_id()));
			// if (result != null) {
			// System.out.println(result);
			// }
			//
			// Map<String, String> ext = new HashMap<String, String>();
			// ext.put("action",
			// String.valueOf(MessageAction.ACTION_SOMEONE_LIKE_ME_TIP.ordinal()));
			//
			// result = Main.sendCmdMessage(Main.SYS, new String[] {
			// String.valueOf(with_user.getUser_id()) }, ext);
			// if (result != null) {
			// System.out.println(result);
			// }
			//
			// }
		}

		return ResultUtil.getResultOKMap();
	}

}
