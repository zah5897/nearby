package com.zhan.app.nearby.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.CityService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.RandomCodeUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SMSHelper;
import com.zhan.app.nearby.util.TextUtils;
import com.zhan.app.nearby.util.UserDetailInfoUtil;

@RestController
@RequestMapping("/user")
public class UserController {
	private static Logger log = Logger.getLogger(UserController.class);
	@Resource
	private UserService userService;

	@Resource
	private UserCacheService userCacheService;

	@Resource
	private UserDynamicService userDynamicService;

	@Resource
	private CityService cityService;

	/**
	 * 获取注册用的短信验证码
	 * 
	 * @param request
	 * @param mobile
	 *            手机号码
	 * @return
	 */
	@RequestMapping("code")
	public ModelMap code(HttpServletRequest request, String mobile) {
		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空");
		}
		int count = userService.getUserCountByMobile(mobile);
		if (count > 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "该手机号码已注册");
		}

		String cache = userCacheService.getCachevalideCode(mobile);
		if (cache != null) {
			// 已经在一分钟内发过，还没过期
			System.out.println("已经在一分钟内发过，还没过期");
		}
		// if (now - lastTime <= 60) {
		// return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		// }
		ModelMap data = ResultUtil.getResultOKMap();
		String code = RandomCodeUtil.randomCode(6);

		boolean smsOK = SMSHelper.smsRegist(mobile, code);
		if (smsOK) {
			userCacheService.cacheValidateCode(mobile, code);
			data.put("validate_code", code);
		} else {
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送失败");
		}

		return data;
	}

	/**
	 * 注册
	 * 
	 * @param multipartRequest
	 *            关于图片的request
	 * @param user
	 *            用户对象
	 * @param code
	 *            验证码
	 * @return
	 */

	@RequestMapping("regist")
	public ModelMap regist(HttpServletRequest request, User user, String code) {

		if (TextUtils.isEmpty(user.getMobile())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空!");
		}

		// 验证code合法性
		if (TextUtils.isEmpty(code) || !userCacheService.valideCode(user.getMobile(), code)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "验证码错误");
		}

		if (TextUtils.isEmpty(user.getPassword())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "密码不能为空!");
		}
		try {
			user.setPassword(MD5Util.getMd5(user.getPassword()));
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			return ResultUtil.getResultMap(ERROR.ERR_PASSWORD);
		}

		DefaultMultipartHttpServletRequest multipartRequest = null;

		if (request instanceof MultipartHttpServletRequest) {
			multipartRequest = (DefaultMultipartHttpServletRequest) request;
		}
		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String newAcatar = ImageSaveUtils.saveAvatar(file, multipartRequest.getServletContext());
						user.setAvatar(newAcatar);
						break;
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
						break;
					}
				}
			}
		}
		String token = UUID.randomUUID().toString();
		user.setToken(token);
		user.setType((short) UserType.OFFIEC.ordinal());
		user.setLast_login_time(new Date());
		long id = user.getUser_id();
		if (id > 0) {
			int count = userService.visitorToNormal(user);
			if (count == 0) {
				return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "无法找到该游客账号");
			}
		} else {
			id = userService.insertUser(user);
		}
		if (id == -1l) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "该手机号码已经注册过");
		}
		// userService.updateToken(user); // 更新token，弱登录
		userCacheService.cacheLoginToken(user); // 缓存token，缓解检查登陆查询

		ModelMap result = ResultUtil.getResultOKMap();
		user.setUser_id(id);
		user.setAge(DateTimeUtil.getAge(user.getBirthday()));
		ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址

		if (user.getCity() == null) {
			user.setCity(getDefaultCityId());
		}
		result.put("user", user);
		// 注册完毕，则可以清理掉redis关于code缓存了
		userCacheService.clearCode(user.getMobile());
		return result;
	}

	/**
	 * 登录
	 * 
	 * @param mobile
	 *            手机号码
	 * @param password
	 *            密码
	 * @param _ua
	 *            系统参数
	 * @return
	 */
	@RequestMapping("login")
	public ModelMap loginByMobile(String mobile, String password, String _ua) {

		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空!");
		}
		if (TextUtils.isEmpty(password)) {
			return ResultUtil.getResultMap(ERROR.ERR_PASSWORD);
		}

		User user = userService.findUserByMobile(mobile);
		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该账号不存在");
		}
		try {
			String md5 = MD5Util.getMd5(password);
			if (md5.equals(user.getPassword())) {
				ModelMap result = ResultUtil.getResultOKMap("登录成功");
				user.setToken(UUID.randomUUID().toString());
				user.set_ua(_ua);
				userService.pushLongTimeNoLoginMsg(user.getUser_id(), user.getLast_login_time());
				userService.updateToken(user); // 更新token，弱登录
				user.set_ua(null);
				user.setAge(DateTimeUtil.getAge(user.getBirthday()));
				userCacheService.cacheLoginToken(user); // 缓存token，缓解检查登陆查询

				ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址
				if (user.getCity() == null) {
					user.setCity(getDefaultCityId());
				}

				if (user.getBirth_city_id() > 0) {
					user.setBirth_city(cityService.getCity(user.getBirth_city_id()));
				}

				result.put("user", user);
				return result;
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_PASSWORD);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return ResultUtil.getResultMap(ERROR.ERR_PASSWORD);
		}

	}

	@RequestMapping("logout")
	public ModelMap logout(String token, long user_id) {
		if (TextUtils.isEmpty(token) || user_id < 1) {
			return ResultUtil.getResultOKMap();
		}
		User user = userService.getBasicUser(user_id);
		if (user != null) {
			if (token.equals(user.getToken())) {
				userService.updateToken(new User(user_id));
				userCacheService.clearLoginUser(token, user_id);
			}
		}
		return ResultUtil.getResultOKMap();
	}

	/**
	 * 获取重置密码的短信验证码
	 * 
	 * @param mobile
	 *            手机号码
	 * @return
	 */
	@RequestMapping("reset_password_code")
	public ModelMap reset_password_code(String mobile) {
		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空");
		}
		int count = userService.getUserCountByMobile(mobile);
		if (count < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该手机号码未注册");
		}

		String cache = userCacheService.getCachevalideCode(mobile);
		if (cache != null) {
			// 已经在一分钟内发过，还没过期
			System.out.println("已经在一分钟内发过，还没过期");
		}
		// if (now - lastTime <= 60) {
		// return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		// }
		ModelMap data = ResultUtil.getResultOKMap();
		String code = RandomCodeUtil.randomCode(6);

		boolean smsOK = SMSHelper.smsResetPwd(mobile, code);
		if (smsOK) {
			userCacheService.cacheValidateCode(mobile, code);
			data.put("validate_code", code);
		} else {
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送失败");
		}
		return data;
	}

	/**
	 * 重置密码
	 * 
	 * @param mobile
	 *            手机号码
	 * @param password
	 *            新密码
	 * @param code
	 *            验证码
	 * @return
	 */
	@RequestMapping("reset_password")
	public ModelMap reset_password(String mobile, String password, String code, String _ua) {

		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码为空");
		}
		boolean validate = userCacheService.valideCode(mobile, code);

		if (TextUtils.isEmpty(code) || !validate) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "验证码错误");
		}

		if (TextUtils.isEmpty(password)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "新密码不能为空");
		}

		String md5Pwd;
		try {
			md5Pwd = MD5Util.getMd5(password);
			userService.updatePassword(mobile, md5Pwd);
			userCacheService.clearCode(mobile); // 清理缓存
			return loginByMobile(mobile, password, _ua);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return ResultUtil.getResultMap(ERROR.ERR_SYS, "新密码设置异常");
		}

	}

	/**
	 * 获取对应id的用户信息
	 * 
	 * @param user_id
	 *            用户id
	 * @return
	 */
	@RequestMapping("info")
	public ModelMap info(long user_id_for) {
		User u = userService.getBasicUser(user_id_for);
		if (u == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		} else {
			ModelMap result = ResultUtil.getResultOKMap();
			u.hideSysInfo();
			ImagePathUtil.completeAvatarPath(u, true); // 补全图片链接地址
			result.put("user", u);
			return result;
		}
	}

	/**
	 * 修改头像
	 * 
	 * @param multipartRequest
	 * @param user_id
	 *            要修改的用户id
	 * @param token
	 *            token
	 * @return
	 */
	@RequestMapping("modify_avatar")
	public ModelMap modify_avatar(DefaultMultipartHttpServletRequest multipartRequest, long user_id, String token) {
		if (user_id < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户ID异常");
		}

		if (TextUtils.isEmpty(token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		User user = userService.getBasicUser(user_id);

		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		} else if (!token.equals(user.getToken())) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		} else {
			userCacheService.cacheLoginToken(user);
		}
		String newAcatar = null;
		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						newAcatar = ImageSaveUtils.saveAvatar(file, multipartRequest.getServletContext());
						String old = user.getAvatar();
						if (!TextUtils.isEmpty(old)) {
							ImageSaveUtils.removeAcatar(multipartRequest.getServletContext(), old);
						}
						user.setAvatar(newAcatar);
						break;
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
						return ResultUtil.getResultMap(ERROR.ERR_FAILED);
					}
				}
			}
		}

		int code = userService.updateAvatar(user_id, newAcatar);
		ModelMap result = ResultUtil.getResultOKMap();
		user.set_ua(null);
		ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址
		result.put("user", user);
		return result;
	}

	/**
	 * 修改信息
	 * 
	 * @param user_id
	 * @param token
	 * @param nick_name
	 * @param age
	 * @param jobs
	 * @param height
	 * @param weight
	 * @param signature
	 * @param my_tags
	 * @param interest
	 * @param favourite_animal
	 * @param favourite_music
	 * @param weekday_todo
	 * @param footsteps
	 * @param want_to_where
	 * @return
	 */
	@RequestMapping("modify_info")
	public ModelMap modify_info(long user_id, String token, String nick_name, String birthday, String jobs,
			String height, String weight, String signature, String my_tags, String interest, String favourite_animal,
			String favourite_music, String weekday_todo, String footsteps, String want_to_where,
			Integer birth_city_id) {
		if (user_id < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户ID异常");
		}

		User user = userService.getBasicUser(user_id);
		//
		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		}
		boolean isNick_modify = false;
		if (user.getNick_name() != null) {
			if (!user.getNick_name().equals(nick_name)) {
				isNick_modify = true;
			}
		} else if (!TextUtils.isEmpty(nick_name)) {
			isNick_modify = true;
		}

		userService.modify_info(user_id, nick_name, birthday, jobs, height, weight, signature, my_tags, interest,
				favourite_animal, favourite_music, weekday_todo, footsteps, want_to_where, isNick_modify,
				birth_city_id);
		return detial_info(user_id, null, null);
	}

	/**
	 * 获取自己的详情或者别人的详细信息
	 * 
	 * @param user_id
	 * @param user_id_for
	 * @param countz'z
	 * @return
	 */
	@RequestMapping("detial_info")
	public ModelMap detial_info(Long user_id, Long user_id_for, Integer count) {
		if (user_id_for != null && user_id_for > 0) {
			user_id = user_id_for;
		}
		if (count == null || count <= 0) {
			count = 4;
		}
		return UserDetailInfoUtil.getDetailInfo(userService, user_id, count);
	}

	@RequestMapping("dynamic")
	public ModelMap dynamic(Long user_id_for, Long last_id, Integer count) {

		if (user_id_for == null || user_id_for < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定用户ID");
		}

		if (last_id == null) {
			last_id = 0l;
		}

		if (count == null) {
			count = 10;
		}
		ModelMap result = ResultUtil.getResultOKMap();
		List<UserDynamic> dynamics = userDynamicService.getUserDynamic(user_id_for, last_id, count);
		result.put("dynamics", dynamics);

		if (dynamics == null || dynamics.size() < count) {
			result.put("hasMore", false);
			result.put("last_id", 0);
		} else {
			result.put("hasMore", true);
			result.put("last_id", dynamics.get(dynamics.size() - 1).getId());
		}
		return result;
	}

	@RequestMapping("update_location")
	public ModelMap update_location(HttpServletRequest request, long user_id, String lat, String lng,
			String ios_address) {
		userService.uploadLocation(IPUtil.getIpAddress(request), user_id, lat, lng);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("add_token")
	public ModelMap add_token(HttpServletRequest request, long user_id, String device_token, String zh_cn) {
		userService.uploadToken(user_id, device_token, zh_cn);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("visitor_regist")
	public ModelMap visitor_regist(String device_id, String aid, String device_token, String zh_cn, String lat,
			String lng) {

		if (!TextUtils.isEmpty(zh_cn)) {
			if (zh_cn.length() > 2) {
				return ResultUtil.getResultMap(ERROR.ERR_PARAM, "zh_cn has too long,max &lt 2");
			}
		}
		if (TextUtils.isEmpty(device_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "deviceId is empty");
		}

		User user = userService.findUserByDeviceId(device_id);
		if (user == null) {

			user = new User();
			user.setMobile(device_id);
			user.setDevice_token(device_token);
			user.setApp_id(aid);
			user.setLat(lat);
			user.setLng(lng);
			user.setZh_cn(zh_cn);
			user.setType((short) UserType.VISITOR.ordinal());
			long user_id = userService.insertUser(user);
			user.setUser_id(user_id);
		} else {
			userService.updateVisitor(user.getUser_id(), aid, device_token, lat, lng, zh_cn);
			user.setDevice_token(device_token);
		}
		ModelMap result = ResultUtil.getResultOKMap();

		user.setCity(getDefaultCityId());
		result.put("user", user);
		return result;
	}

	@RequestMapping("add_app_user")
	public ModelMap add_app_user(String device_id, String aid, String device_token, String zh_cn, String lat,
			String lng) {

		if (!TextUtils.isEmpty(zh_cn)) {
			if (zh_cn.length() > 2) {
				return ResultUtil.getResultMap(ERROR.ERR_PARAM, "zh_cn has too long,max &lt 2");
			}
		}
		if (TextUtils.isEmpty(device_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "deviceId is empty");
		}

		User user = userService.findUserByDeviceId(device_id);
		if (user == null) {

			user = new User();
			user.setMobile(device_id);
			user.setDevice_token(device_token);
			user.setApp_id(aid);
			user.setLat(lat);
			user.setLng(lng);
			user.setZh_cn(zh_cn);
			user.setType((short) UserType.VISITOR.ordinal());
			long user_id = userService.insertUser(user);
			user.setUser_id(user_id);
		} else {
			userService.updateVisitor(user.getUser_id(), aid, device_token, lat, lng, zh_cn);
			user.setDevice_token(device_token);
		}
		ModelMap result = ResultUtil.getResultOKMap();

		user.setCity(getDefaultCityId());
		result.put("user", user);
		return result;
	}

	@RequestMapping("set_city")
	public ModelMap set_city(Long user_id, Integer city_id) {
		userService.setCity(user_id, city_id);
		ModelMap result = ResultUtil.getResultOKMap();
		return result;
	}

	@RequestMapping("add_block")
	public ModelMap set_city(Long user_id, String token, Long block_user_id) {
		ModelMap result = ResultUtil.getResultOKMap();
		if(user_id!=null&&user_id>0&&block_user_id!=null&&block_user_id>0){
			userService.updateRelationship(user_id, block_user_id, Relationship.BLACK);
		}
		return result;
	}

	private City getDefaultCityId() {

		City city = new City();
		city.setId(2);
		city.setName("上海");
		city.setType(0);
		return city;
	}
}
