package com.zhan.app.nearby.controller;

import static java.net.URLDecoder.decode;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.Tag;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.bean.user.LoginUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.AccountStateType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.CityService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.RandomCodeUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SMSHelper;
import com.zhan.app.nearby.util.TextUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(value = "用户相关")
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
	 * @param mobile  手机号码
	 * @return
	 */
	@RequestMapping("code")
	public ModelMap code(HttpServletRequest request, String mobile, Integer code_type) {
		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空");
		}
		int count = userService.getUserCountByMobile(mobile);
		if (count > 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "该手机号码已注册");
		}

		if (userCacheService.getUserCodeCacheCount(mobile) >= 3) {
			return ResultUtil.getResultMap(ERROR.ERR_SMS_CODE_LIMIT);
		}

		String code = RandomCodeUtil.randomCode(6);

		if (code_type == null) {
			code_type = 0;
		}

		if (code_type == -1000) {
			userCacheService.cacheRegistValidateCode(mobile, code, code_type);
			return ResultUtil.getResultOKMap().addAttribute("validate_code", code);
		}

//		String cache = userCacheService.getCachevalideCode(mobile);
//		if (cache != null) {
//			// 已经在一分钟内发过，还没过期
//		}
		// if (now - lastTime <= 60) {
		// return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		// }
		ModelMap data = ResultUtil.getResultOKMap();
		boolean smsOK = SMSHelper.smsRegist(mobile, code);
		if (smsOK) {
			userCacheService.cacheRegistValidateCode(mobile, code, code_type == null ? 0 : code_type);
			data.put("validate_code", code);
		} else {
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送过于频繁");
		}

		return data;
	}

	/**
	 * 获取注册用的短信验证码
	 * 
	 * @param request
	 * @param mobile  手机号码
	 * @return
	 */
	@RequestMapping("code_for_game")
	
	public ModelMap code_for_game(HttpServletRequest request, String mobile, Integer code_type) {
		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空");
		}
		int count = userService.getUserCountByMobile(mobile);
		if (count > 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "该手机号码已注册");
		}

		if (userCacheService.getUserCodeCacheCount(mobile) >= 3) {
			return ResultUtil.getResultMap(ERROR.ERR_SMS_CODE_LIMIT);
		}

		String code = RandomCodeUtil.randomCode(6);

		if (code_type == null) {
			code_type = 0;
		}

		if (code_type == -1000) {
			userCacheService.cacheRegistValidateCode(mobile, code, code_type);
			return ResultUtil.getResultOKMap().addAttribute("validate_code", code);
		}

//		String cache = userCacheService.getCachevalideCode(mobile);
//		if (cache != null) {
//			// 已经在一分钟内发过，还没过期
//		}
		// if (now - lastTime <= 60) {
		// return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		// }
		ModelMap data = ResultUtil.getResultOKMap();
		boolean smsOK = SMSHelper.smsGameRegist(mobile, code);
		if (smsOK) {
			userCacheService.cacheRegistValidateCode(mobile, code, code_type == null ? 0 : code_type);
			data.put("validate_code", code);
		} else {
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送过于频繁");
		}

		return data;
	}

	/**
	 * 注册
	 * 
	 * @param multipartRequest 关于图片的request
	 * @param user             用户对象
	 * @param code             验证码
	 * @return
	 */

	@RequestMapping("regist")
	
	public ModelMap regist(HttpServletRequest request, LoginUser user, String code, String aid, Integer city_id,
			String bGenderOK, String _ua, String bDeleteIM) {

		if (TextUtils.isEmpty(user.getMobile())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空!");
		}

		if (TextUtils.isTrimEmpty(user.getNick_name())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "昵称不能为空");
		}
		// 验证code合法性
		if (TextUtils.isEmpty(code) || !userCacheService.valideRegistCode(user.getMobile(), code)) {
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

		if (TextUtils.isEmpty(bGenderOK)) {
			if ("0".equals(user.getSex())) {
				user.setSex("0");
			} else if ("2".equals(user.getSex())) {
				user.setSex("0");
			}
		}
		user.setIp(IPUtil.getIpAddress(request));
		user.setNick_name(BottleKeyWordUtil.filterContent(user.getNick_name()));

		if (user.getBirth_city_id() == 0 && city_id != null) {
			user.setBirth_city_id(city_id);
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
						String newAcatar = ImageSaveUtils.saveAvatar(file);
						user.setAvatar(newAcatar);
						break;
					} catch (Exception e) {
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
		user.setCreate_time(new Date());
		long id = user.getUser_id();

		boolean isNeedHx = false;
		if (TextUtils.isEmpty(bDeleteIM) || !"1".equals(bDeleteIM)) {
			isNeedHx = true;
		}

		if (id > 0) {
			int count = userService.visitorToNormal(user, isNeedHx);
			if (count == 0) {
				return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "无法找到该游客账号");
			}
		} else {
			if (_ua.contains("\\|")) {
				user.set_ua(_ua);
			} else {
				user.set_ua(decode(_ua));
			}
			userService.insertUser(user, isNeedHx);
			id = user.getUser_id();
		}
		if (id == -1l) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "该手机号码已经注册过");
		}
		userService.saveAvatar(id, user.getAvatar());
		ModelMap result = ResultUtil.getResultOKMap();
		user.setUser_id(id);
		ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址
		if (city_id != null) {
			City city = cityService.getFullCity(city_id);
			user.setBirth_city(city);
		}

		user.setAvatars(userService.getUserAvatars(user.getUser_id()));
		result.put("user", user);
		// 注册完毕，则可以清理掉redis关于code缓存了
		userCacheService.clearCode(user.getMobile());
		return result;
	}

	@SuppressWarnings("deprecation")
	@ApiOperation(httpMethod = "POST", value = "regist_v2") // swagger 当前接口注
	@RequestMapping("regist_v2")
	public ModelMap regist_v2(HttpServletRequest request, LoginUser user, String code, String aid, Integer city_id,
			String bGenderOK, String _ua, String image_names, String bDeleteIM) {

		if (TextUtils.isEmpty(user.getMobile())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空!");
		}

		if (TextUtils.isTrimEmpty(user.getNick_name())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "昵称不能为空");
		}
		// 验证code合法性
		if (TextUtils.isEmpty(code) || !userCacheService.valideRegistCode(user.getMobile(), code)) {
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

		if (TextUtils.isEmpty(bGenderOK)) {
			if ("0".equals(user.getSex())) {
				user.setSex("0");
			} else if ("2".equals(user.getSex())) {
				user.setSex("0");
			}
		}
		user.setIp(IPUtil.getIpAddress(request));
		user.setNick_name(BottleKeyWordUtil.filterContent(user.getNick_name()));

		if (user.getBirth_city_id() == 0 && city_id != null) {
			user.setBirth_city_id(city_id);
		}
		user.setAvatar(image_names);
		String token = UUID.randomUUID().toString();
		user.setToken(token);
		user.setType((short) UserType.OFFIEC.ordinal());
		user.setLast_login_time(new Date());
		user.setCreate_time(new Date());

		boolean isNeedHx = false;
		if (TextUtils.isEmpty(bDeleteIM) || !"1".equals(bDeleteIM)) {
			isNeedHx = true;
		}

		long id = user.getUser_id();
		if (id > 0) {
			int count = userService.visitorToNormal(user, isNeedHx);
			if (count == 0) {
				return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "无法找到该游客账号");
			}
		} else {
			if (_ua.contains("\\|")) {
				user.set_ua(_ua);
			} else {
				user.set_ua(decode(_ua));
			}
			userService.insertUser(user, isNeedHx);
			id = user.getUser_id();
		}
		if (id == -1l) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "该手机号码已经注册过");
		}
		userService.saveAvatar(id, user.getAvatar());
		ModelMap result = ResultUtil.getResultOKMap();
		user.setUser_id(id);
		ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址
		if (city_id != null) {
			City city = cityService.getFullCity(city_id);
			user.setBirth_city(city);
		}

		user.setAvatars(userService.getUserAvatars(user.getUser_id()));
		result.put("user", user);
		// 注册完毕，则可以清理掉redis关于code缓存了
		userCacheService.clearCode(user.getMobile());

		// 触发随机匹配会话
		userService.checkHowLongNotOpenApp(user);
		return result;
	}

	@RequestMapping("login_thrid_channel")
	@ApiOperation(httpMethod = "POST", value = "第三方渠道账号登陆") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "aid", value = "aid", required = true, paramType = "query"),
			@ApiImplicitParam(name = "city_id", value = "city_id", paramType = "query"),
			@ApiImplicitParam(name = "_ua", value = "_ua", required = true, paramType = "query"),
			@ApiImplicitParam(name = "login_channel", value = "login_channel", required = true, paramType = "query"), })
	public ModelMap login_thrid_channel(HttpServletRequest request, LoginUser user, String aid, Integer city_id,
			String _ua, String login_channel, String image_names, String city_name, String province_name) {

		if (TextUtils.isEmpty(user.getOpenid())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "此为第三方账号登录，openid不能为空");
		}
		if (_ua.contains("\\|")) {
			user.set_ua(_ua);
		} else {
			user.set_ua(decode(_ua));
		}

		if (userService.getUserCountByOpenid(user.getOpenid()) >= 1) {
			return userService.doLogin(user, user.get_ua(), aid, getDefaultCityId());
		}
		if (TextUtils.isTrimEmpty(user.getNick_name())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "昵称不能为空");
		}

		if (!"0".equals(user.getSex()) && !"1".equals(user.getSex())) {
			user.setSex("0");
		}

		user.setOpenid(login_channel + "#" + user.getOpenid());
		user.setIp(IPUtil.getIpAddress(request));
		user.setToken(UUID.randomUUID().toString());
		user.setAvatar(image_names);
		user.setNick_name(BottleKeyWordUtil.filterContent(user.getNick_name()));
		if (user.getBirth_city_id() == 0 && city_id != null) {
			user.setBirth_city_id(city_id);
		}
		user.setType((short) UserType.THRID_CHANNEL.ordinal());
		user.setLast_login_time(new Date());
		user.setCreate_time(new Date());

		if (user.getBirthday() == null) {
			user.setBirthday(DateTimeUtil.parseDate("2002-01-01"));
		}
		long id = userService.insertUserThridChannel(user, false);
		if (id<1) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "注册失败");
		}
		ModelMap result = ResultUtil.getResultOKMap();
		user.setUser_id(id);
		userService.saveAvatar(id, user.getAvatar());
		ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址

		if (city_id != null) {
			City city = cityService.getFullCity(city_id);
			user.setBirth_city(city);
		}
		user.setAvatars(userService.getUserAvatars(user.getUser_id()));
		result.put("user", user);
		result.put("isFirstLogin", true);

		// 触发随机匹配会话
		userService.checkHowLongNotOpenApp(user);
		return result;
	}

	/**
	 * 游戏用到的用户注册
	 * 
	 * @param request
	 * @param user
	 * @param code
	 * @param aid
	 * @param city_id
	 * @param bGenderOK
	 * @param _ua
	 * @param image_names
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "regist_for_game") // swagger 当前接口注
	@RequestMapping("regist_for_game")
	public ModelMap regist_for_game(HttpServletRequest request, LoginUser user, String code, String aid,
			Integer city_id, String bGenderOK, String _ua, String image_names) {

		if (TextUtils.isEmpty(user.getMobile())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空!");
		}

		if (TextUtils.isTrimEmpty(user.getNick_name())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "昵称不能为空");
		}
		// 验证code合法性
		if (!TextUtils.isEmpty(code)) {
			if (!userCacheService.valideRegistCode(user.getMobile(), code)) {
				return ResultUtil.getResultMap(ERROR.ERR_PARAM, "验证码错误");
			}
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

		if (TextUtils.isEmpty(bGenderOK)) {
			if ("0".equals(user.getSex())) {
				user.setSex("0");
			} else if ("2".equals(user.getSex())) {
				user.setSex("0");
			}
		}
		user.setIp(IPUtil.getIpAddress(request));
		user.setNick_name(BottleKeyWordUtil.filterContent(user.getNick_name()));

		if (user.getBirth_city_id() == 0 && city_id != null) {
			user.setBirth_city_id(city_id);
		}
		user.setAvatar(image_names);
		String token = UUID.randomUUID().toString();
		user.setToken(token);
		user.setType((short) UserType.TEMP_MOBILE.ordinal());
		user.setLast_login_time(new Date());
		user.setCreate_time(new Date());
		if (_ua.contains("\\|")) {
			user.set_ua(_ua);
		} else {
			user.set_ua(decode(_ua));
		}
		
		userService.insertUser(user, false);
		long id = user.getUser_id();
		if (id < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_EXIST, "该手机号码已经注册过");
		}
		userService.saveAvatar(id, user.getAvatar());
		ModelMap result = ResultUtil.getResultOKMap();
		user.setUser_id(id);
		ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址

		if (city_id != null) {
			City city = cityService.getFullCity(city_id);
			user.setBirth_city(city);
		}

		user.setAvatars(userService.getUserAvatars(user.getUser_id()));
		result.put("user", user);
		// 注册完毕，则可以清理掉redis关于code缓存了
		userCacheService.clearCode(user.getMobile());
		return result;
	}

	/**
	 * 登录
	 * 
	 * @param mobile   手机号码
	 * @param password 密码
	 * @param _ua      系统参数
	 * @return
	 */
	@RequestMapping("login")
	@ApiOperation(httpMethod = "POST", value = "login") // swagger 当前接口注
	public ModelMap loginByMobile(String mobile, String password, String openid, String _ua, String aid,
			String bDeleteIM, String device_token) {

		LoginUser user = null;
		if (TextUtils.isEmpty(openid)) {
			if (TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password)) {
				return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码或密码不能为空");
			}
			user = userService.findLocationUserByMobile(mobile);
		} else {
			// login by openid;
			user = userService.findLocationUserByOpenId(openid);
		}

		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该账号不存在");
		}

		if (user.getAccount_state() == AccountStateType.CLOSE.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_CLOSE, "该账号已经注销");
		}

		if (userService.getUserState(user.getUser_id()) == SysUserStatus.BLACK.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该账号因举报而无法登录");
		}

		// 注册环信
		if (TextUtils.isEmpty(bDeleteIM) || !"1".equals(bDeleteIM)) {
			userService.registHXNoException(user);
		}

		try {
			String md5 = MD5Util.getMd5(password);
			if (md5.equals(user.getPassword())) {
				ModelMap result = ResultUtil.getResultOKMap("登录成功");
				user.setToken(UUID.randomUUID().toString());
				user.set_ua(_ua);
				userService.pushLongTimeNoLoginMsg(user.getUser_id(), user.getLast_login_time());
				userService.saveUserOnline(user.getUser_id());
				userService.updateToken(user, device_token); // 更新token，弱登录
				user.set_ua(null);

				ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址
				if (user.getCity() == null) {
					user.setCity(getDefaultCityId());
				}

				if (user.getBirth_city_id() > 0) {
					user.setBirth_city(cityService.getSimpleCity(user.getBirth_city_id()));
				}
				VipUser vip = userService.loadUserVipInfo(aid, user.getUser_id());
				result.put("user", user);
				// result.put("all_coins", userService.loadUserCoins(aid, user.getUser_id()));
				result.put("vip", vip);
				// 触发随机匹配会话
				userService.checkHowLongNotOpenApp(user);
				return result;
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_PASSWORD);
			}
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
			return ResultUtil.getResultMap(ERROR.ERR_PASSWORD);
		}

	}
	@ApiOperation(httpMethod = "POST", value = "logout") // swagger 当前接口注
	@RequestMapping("logout")
	public ModelMap logout(String token, long user_id) {
		if (TextUtils.isEmpty(token) || user_id < 1) {
			return ResultUtil.getResultOKMap();
		}
		BaseUser user = userService.getBasicUser(user_id);
		if (user != null) {
			if (token.equals(user.getToken())) {
				userService.removeOnline(user_id);
				userService.updateToken(new BaseUser(user_id));
			}
		}
		return ResultUtil.getResultOKMap();
	}

	/**
	 * 获取重置密码的短信验证码
	 * 
	 * @param mobile 手机号码
	 * @return
	 */
	@RequestMapping("reset_password_code")
	@ApiOperation(httpMethod = "POST", value = "reset_password_code") // swagger 当前接口注
	public ModelMap reset_password_code(String mobile) {
		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空");
		}
		int count = userService.getUserCountByMobile(mobile);
		if (count < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该手机号码未注册");
		}

		if (userCacheService.getUserCodeCacheCount(mobile) >= 3) {
			return ResultUtil.getResultMap(ERROR.ERR_SMS_CODE_LIMIT);
		}

		ModelMap data = ResultUtil.getResultOKMap();
		String code = RandomCodeUtil.randomCode(6);

		boolean smsOK = SMSHelper.smsResetPwd(mobile, code);
		if (smsOK) {
			userCacheService.cacheRegistValidateCode(mobile, code, 0);
			data.put("validate_code", code);
		} else {
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送失败");
		}
		return data;
	}

	/**
	 * 获取重置密码的短信验证码
	 * 
	 * @param mobile 手机号码
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "reset_game_password_code") // swagger 当前接口注
	@RequestMapping("reset_game_password_code")
	public ModelMap reset_game_password_code(String mobile) {
		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空");
		}
		int count = userService.getUserCountByMobile(mobile);
		if (count < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该手机号码未注册");
		}

		if (userCacheService.getUserCodeCacheCount(mobile) >= 3) {
			return ResultUtil.getResultMap(ERROR.ERR_SMS_CODE_LIMIT);
		}

		ModelMap data = ResultUtil.getResultOKMap();
		String code = RandomCodeUtil.randomCode(6);

		boolean smsOK = SMSHelper.smsResetGamePwd(mobile, code);
		if (smsOK) {
			userCacheService.cacheRegistValidateCode(mobile, code, 0);
			data.put("validate_code", code);
		} else {
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送失败");
		}
		return data;
	}

	/**
	 * 重置密码
	 * 
	 * @param mobile   手机号码
	 * @param password 新密码
	 * @param code     验证码
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "reset_password") // swagger 当前接口注
	@RequestMapping("reset_password")
	public ModelMap reset_password(String mobile, String password, String code, String _ua, String aid,
			String device_token) {

		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码为空");
		}
		boolean validate = userCacheService.valideRegistCode(mobile, code);

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
			return loginByMobile(mobile, password, null, _ua, aid, "1", device_token);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
			return ResultUtil.getResultMap(ERROR.ERR_SYS, "新密码设置异常");
		}

	}

	/**
	 * 获取对应id的用户信息
	 * 
	 * @param user_id 用户id
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "info") // swagger 当前接口注
	@RequestMapping("info")
	public ModelMap info(long user_id, long user_id_for) {
		DetailUser u = userService.getUserDetailInfo(user_id_for);
		if (u == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		} else {
			ModelMap result = ResultUtil.getResultOKMap();
			ImagePathUtil.completeAvatarPath(u, true); // 补全图片链接地址
			Map<String, Object> juser = JSONUtil.jsonToMap(u);
			juser.put("relationship", userService.getRelationShip(user_id, user_id_for).ordinal());
			result.put("user", juser);
			return result;
		}
	}

	/**
	 * 修改头像
	 * 
	 * @param multipartRequest
	 * @param user_id          要修改的用户id
	 * @param token            token
	 * @return
	 */
	@RequestMapping("modify_avatar")
	@ApiOperation(httpMethod = "POST", value = "modify_avatar") // swagger 当前接口注解
	public ModelMap modify_avatar(DefaultMultipartHttpServletRequest multipartRequest, long user_id, String aid,
			String token) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		BaseUser user = userService.getBasicUser(user_id);

		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		} else if (!token.equals(user.getToken())) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String newAcatar = ImageSaveUtils.saveAvatar(file);
						user.setAvatar(newAcatar);
						userService.updateAvatar(user_id, newAcatar);
						userService.saveAvatar(user_id, newAcatar);
						break;
					} catch (Exception e) {
						log.error(e.getMessage());
						return ResultUtil.getResultMap(ERROR.ERR_FAILED);
					}
				}
			}
		}

		return userService.getUserCenterData("", aid, user_id, user_id);
	}

	/**
	 * 修改头像
	 * 
	 * @param multipartRequest
	 * @param user_id          要修改的用户id
	 * @param token            token
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "modify_avatar_v2") // swagger 当前接口注解
	@RequestMapping("modify_avatar_v2")
	public ModelMap modify_avatar_v2(long user_id, String aid, String token, String image_names) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		BaseUser user = userService.getBasicUser(user_id);

		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		} else if (!token.equals(user.getToken())) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(image_names)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		user.setAvatar(image_names);
		userService.updateAvatar(user_id, image_names);
		userService.saveAvatar(user_id, image_names);
		userService.doCheckIsFace(user);
		return userService.getUserCenterData("", aid, user_id, user_id);
	}

	/**
	 * 修改用户基本信息
	 * 
	 * @param multipartRequest
	 * @param user_id          要修改的用户id
	 * @param token            token
	 * @return
	 */
	@RequestMapping("update_base_info")

	@ApiOperation(httpMethod = "POST", value = "修改用户基本信息") //
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "nick_name", value = "昵称", paramType = "query"),
			@ApiImplicitParam(name = "avatar", value = "头像文件名称", paramType = "query"),
			@ApiImplicitParam(name = "birthday", value = "生日", paramType = "query"),
			@ApiImplicitParam(name = "city_id", value = "城市id", paramType = "query") })

	public ModelMap update_base_info(long user_id, String token, String nick_name, String avatar, String birthday,
			Integer city_id, String aid) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		Date birthdayDate = DateTimeUtil.parseDate(birthday);
		if (birthdayDate != null) {
			int age = Integer.parseInt(DateTimeUtil.getAge(birthdayDate));
			if (age < 18) {
				return ResultUtil.getResultMap(ERROR.ERR_FAILED, "年龄必须大于18周岁");
			}
		}
		City city = null;
		if (city_id != null) {
			city = cityService.getSimpleCity(city_id);
		}
		userService.updateBaseInfo(user_id, nick_name, avatar, birthdayDate, city);
		return userService.getUserDetailResult(user_id, token, aid);
	}
	@ApiOperation(httpMethod = "POST", value = "upload_avatars") // swagger 当前接口注解
	@RequestMapping("upload_avatars")
	public ModelMap upload_avatars(DefaultMultipartHttpServletRequest multipartRequest, long user_id, String aid,
			String token) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			List<String> avatarFiles = new ArrayList<String>();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String avatarName = ImageSaveUtils.saveAvatar(file);
						avatarFiles.add(avatarName);
					} catch (Exception e) {
					}
				}
			}
			// 文件保存完毕

			for (String name : avatarFiles) {
				userService.saveAvatar(user_id, name);
			}
			// 更新头像
			if (avatarFiles.size() > 0) {
				userService.updateAvatar(user_id, avatarFiles.get(avatarFiles.size() - 1));
			}

			return userService.getUserCenterData("", aid, user_id, user_id);
			// return ResultUtil.getResultOKMap().addAttribute("avatars",
			// userService.getUserAvatars(user_id));
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED, "no files.");
	}
	@ApiOperation(httpMethod = "POST", value = "upload_avatars_v2") // swagger 当前接口注解
	@RequestMapping("upload_avatars_v2")
	public ModelMap upload_avatars_v2(DefaultMultipartHttpServletRequest multipartRequest, long user_id, String aid,
			String token, String image_names) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		// 文件保存完毕

		String[] avatars = image_names.split(",");
		for (String name : avatars) {
			userService.saveAvatar(user_id, name);
		}
		// 更新头像
		if (avatars.length > 0) {
			userService.updateAvatar(user_id, avatars[avatars.length - 1]);
		}
		return userService.getUserCenterData("", aid, user_id, user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "delete_avatar") // swagger 当前接口注解
	@RequestMapping("delete_avatar")
	public ModelMap delete_avatar(HttpServletRequest request, long user_id, String token, String aid,
			String avatar_ids) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		String[] ids = avatar_ids.split(",");

		for (String sid : ids) {
			userService.deleteAvatar(user_id, sid);
		}
		return userService.getUserCenterData("", aid, user_id, user_id);
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
	@ApiOperation(httpMethod = "POST", value = "modify_info") // swagger 当前接口注解
	@RequestMapping("modify_info")
	public ModelMap modify_info(long user_id, String aid, String token, String nick_name, String birthday, String jobs,
			String height, String weight, String signature, String my_tags, String interest, String favourite_animal,
			String favourite_music, String weekday_todo, String footsteps, String want_to_where, Integer birth_city_id,
			String contact, String sex) {

		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		BaseUser user = userService.getBasicUser(user_id);
		String newSex = null;
		if (!TextUtils.isEmpty(sex) && ("0".equals(sex) || "1".equals(sex))) {
			if (!sex.equals(user.getSex())) {
				boolean canModify = userService.canModifySex(user_id);
				if (!canModify) {
					return ResultUtil.getResultMap(ERROR.ERR_MODIFY_SEX_LIMIT);
				} else {
					newSex = sex;
				}
			}
		}

		boolean isNick_modify = false;
		if (!TextUtils.isTrimEmpty(nick_name)) {
			nick_name = BottleKeyWordUtil.filterContent(nick_name);
			nick_name = nick_name.trim();
			if (!nick_name.equals(user.getNick_name())) {
				if (!user.getNick_name().equals(nick_name)) {
					isNick_modify = true;
				}
			}
		}
		
		if(!TextUtils.isEmpty(signature)) {
			signature=BottleKeyWordUtil.filterContent(signature);
		}
		if(!TextUtils.isEmpty(want_to_where)) {
			want_to_where=BottleKeyWordUtil.filterContent(want_to_where);
		}
		if(!TextUtils.isEmpty(contact)) {
			contact=BottleKeyWordUtil.filterContent(contact);
		}
		
		userService.modify_info(user_id, nick_name, birthday, jobs, height, weight, signature, my_tags, interest,
				favourite_animal, favourite_music, weekday_todo, footsteps, want_to_where, isNick_modify, birth_city_id,
				contact, newSex);
		userService.updateModifySexTimes(user_id);
		return userService.getUserCenterData(token, aid, user_id, user_id);
	}

	/**
	 * 获取自己的详情或者别人的详细信息
	 * 
	 * @param user_id
	 * @param user_id_for
	 * @param countz'z
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "detial_info") // swagger 当前接口注解
	@RequestMapping("detial_info")
	public ModelMap detial_info(Long user_id, String aid, Long user_id_for, Integer count) {
		if (user_id_for != null && user_id_for > 0) {
			user_id = user_id_for;
		}
		if (count == null || count <= 0) {
			count = 4;
		}
		return userService.getUserCenterData("", aid, user_id, user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "dynamic") // swagger 当前接口注解
	@RequestMapping("dynamic")
	public ModelMap dynamic(long user_id, Long user_id_for, Integer page, Integer count,String version,String _ua) {

		if (user_id_for == null || user_id_for < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定用户ID");
		}
		if (count == null) {
			count = 10;
		}
		if (page == null) {
			page = 1;
		}
		ModelMap result = ResultUtil.getResultOKMap();
		List<UserDynamic> dynamics;
		
		boolean canLoadVideoData=version.compareTo("2.1.0")>=0;//2.1.0及以上版本才支持加载视频动态
		
		if (user_id == user_id_for) {
			dynamics = userDynamicService.getMyDynamic(user_id_for, page, count,canLoadVideoData);
		} else {
			dynamics = userDynamicService.getUserDynamic(user_id_for, page, count,canLoadVideoData);
		}
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
	@ApiOperation(httpMethod = "POST", value = "更新用户位置信息") // swagger 当前接口注解
	@RequestMapping("update_location")
	public ModelMap update_location(HttpServletRequest request, Long user_id, String lat, String lng,
			String ios_address) {
		userService.uploadLocation(IPUtil.getIpAddress(request), user_id == null ? 0 : user_id, lat, lng);
		return ResultUtil.getResultOKMap();
	}
	@ApiOperation(httpMethod = "POST", value = "add_app_user") // swagger 当前接口注解
	@RequestMapping("add_token")
	public ModelMap add_token(HttpServletRequest request, Long user_id, String device_token, String zh_cn) {
		userService.uploadToken(user_id == null ? 0 : user_id, device_token, zh_cn);
		return ResultUtil.getResultOKMap();
	}
	@ApiOperation(httpMethod = "POST", value = "add_app_user") // swagger 当前接口注解
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

		LoginUser user = userService.findLocationUserByDeviceId(device_id);
		if (user == null) {

			user = new LoginUser();
			user.setMobile(device_id);
			user.setDevice_token(device_token);
			user.setLat(lat);
			user.setLng(lng);
			user.setAid(aid);
			user.setZh_cn(zh_cn);
			user.setType((short) UserType.VISITOR.ordinal());
			user.setCreate_time(new Date());
			userService.insertUser(user, false);
		} else {
			userService.updateVisitor(user.getUser_id(), aid, device_token, lat, lng, zh_cn);
			user.setDevice_token(device_token);
		}
		ModelMap result = ResultUtil.getResultOKMap();

		user.setCity(getDefaultCityId());
		result.put("user", user);
		return result;
	}
	@ApiOperation(httpMethod = "POST", value = "set_city") // swagger 当前接口注解
	@RequestMapping("set_city")
	public ModelMap set_city(Long user_id, Integer city_id) {
		userService.setCity(user_id, city_id);
		ModelMap result = ResultUtil.getResultOKMap();
		return result;
	}
	@ApiOperation(httpMethod = "POST", value = "add_block") // swagger 当前接口注解
	@RequestMapping("add_block")
	public ModelMap set_city(Long user_id, String token, Long block_user_id) {
		ModelMap result = ResultUtil.getResultOKMap();
		if (user_id != null && user_id > 0 && block_user_id != null && block_user_id > 0) {
			userService.updateRelationship(user_id, block_user_id, Relationship.BLACK);
		}
		return result;
	}

	@Deprecated
	@RequestMapping("center_page")
	public ModelMap center_page(Long user_id_for, String token, String aid, long user_id) {
		return userService.getUserCenterData(token, aid, user_id_for, user_id);
	}

	@Deprecated
	@RequestMapping("center_page/{user_id_for}")
	public ModelMap center_page_path(@PathVariable Long user_id_for, String token, String aid, Long user_id) {
		return userService.getUserCenterData(token, aid, user_id_for, user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "center_page_v2") // swagger 当前接口注解
	@RequestMapping("center_page_v2")
	public ModelMap center_page_v2(Long user_id_for, String token, String aid, long user_id) {
		return userService.getUserCenterDataV2(token, aid, user_id_for, user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "center_page_v2") // swagger 当前接口注解
	@RequestMapping("center_page_v2/{user_id_for}")
	public ModelMap center_page_v2(@PathVariable Long user_id_for, String token, String aid, Long user_id) {
		return userService.getUserCenterDataV2(token, aid, user_id_for, user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "avatar") // swagger 当前接口注解
	@RequestMapping("avatar/{user_id}")
	public ModelMap getUserAvatar(@PathVariable long user_id) {
		return userService.getUserAvatar(user_id);
	}

	/**
	 * 获取系统标签
	 * 
	 * @param type
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "simple") // swagger 当前接口注解
	@RequestMapping("simple/{user_id}")
	public ModelMap getSimpleUserInfo(@PathVariable long user_id) {
		return userService.getUserSimple(user_id);
	}

	/**
	 * 获取系统标签
	 * 
	 * @param type
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "property") // swagger 当前接口注解
	@RequestMapping("property")
	public ModelMap getUserProperty(long user_id, String aid) {
		return userService.getUserProperty(user_id, aid);
	}

	/**
	 * 获取系统标签
	 * 
	 * @param type
	 * @return
	 */
	@ApiOperation(httpMethod = "POST", value = "tags") // swagger 当前接口注解
	@RequestMapping("tags")
	public ModelMap getTags(int type) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<Tag> tags = userService.getTagsByType(type);
		if (tags != null) {
			result.put("tags", tags);
		}
		return result;
	}
	@ApiOperation(httpMethod = "POST", value = "like_list") // swagger 当前接口注解
	@RequestMapping("like_list/{user_id}")
	public ModelMap like_list(@PathVariable long user_id, Integer page_index, Integer count) {
		return userService.likeList(user_id, page_index, count);
	}

	@ApiOperation(httpMethod = "POST", value = "check_in") // swagger 当前接口注解
	@RequestMapping("check_in")
	public Map<String, Object> check_in(Long user_id, String token, String aid) {
		return userService.checkIn(user_id, token, aid);
	}
	@ApiOperation(httpMethod = "POST", value = "getContact") // swagger 当前接口注解
	@RequestMapping("getContact/{by_user_id}")
	public ModelMap getContact(@PathVariable long by_user_id, long user_id, String token, String aid) {
		return userService.getContact(by_user_id, user_id, token, aid);
	}
	@ApiOperation(httpMethod = "POST", value = "autoLogin") // swagger 当前接口注解
	@RequestMapping("autoLogin")
	public ModelMap autoLogin(long user_id, String md5pwd, String aid, String device_token) {

		if (userService.getUserState(user_id) == SysUserStatus.BLACK.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该账号因举报而无法登录");
		}

		return userService.autoLogin(user_id, md5pwd, aid, device_token);
	}
	@ApiOperation(httpMethod = "POST", value = "online_list") // swagger 当前接口注解
	@RequestMapping("online_list")
	public ModelMap online_list(Integer page, Integer count) {

		if (page == null || page < 0) {
			page = 1;
		}
		if (count == null || count <= 0) {
			count = 10;
		}

		List<LoginUser> users = userService.getOnlineUsers(page, count);
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == count);

	}
	@ApiOperation(httpMethod = "POST", value = "关注某人") // swagger 当前接口注解
	@RequestMapping("follow")
	public ModelMap follow(long user_id, String token, long target_id) {

		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		userService.follow(user_id, target_id, false);
		return ResultUtil.getResultOKMap().addAttribute("target_id", target_id);
	}
	@ApiOperation(httpMethod = "POST", value = "取消关注某人") // swagger 当前接口注解
	@RequestMapping("cancel_follow")
	public ModelMap cancelFollow(long user_id, String token, long target_id) {

		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		userService.follow(user_id, target_id, true);
		return ResultUtil.getResultOKMap().addAttribute("target_id", target_id);
	}

	@ApiOperation(httpMethod = "POST", value = "cost_coin") // swagger 当前接口注解
	@RequestMapping("cost_coin")
	public Map<String, Object> cost_coin(long user_id, String token, String aid, int coin) {
		return userService.cost_coin(user_id, token, aid, coin);
	}

	@ApiOperation(httpMethod = "POST", value = "add_extra") // swagger 当前接口注解
	@RequestMapping("add_extra")
	public Map<String, Object> add_extra(long user_id, String token, String content) throws Exception {
		return userService.addCoin(user_id, token, content);
	}
	
	@ApiOperation(httpMethod = "POST", value = "获取某用户关注的人列表") // swagger 当前接口注解
	@RequestMapping("follow/{uid}")
	public ModelMap my_follow(@PathVariable long uid, Integer page, Integer count)
			throws Exception {
		return userService.followUsers(uid, false, page, count);
	}
	@ApiOperation(httpMethod = "POST", value = "获取某用户关注的人列表") // swagger 当前接口注解
	@RequestMapping("follow_v2/{uid}")
	public ModelMap follow_v2(@PathVariable long uid, Integer page, Integer count)
			throws Exception {
		return userService.getFollowUsers(uid, false, page, count);
	}
	
	@ApiOperation(httpMethod = "POST", value = "获取某人认证视频信息") // swagger 当前接口注解
	@RequestMapping("get_confirm_video/{uid}")
	public ModelMap confirm_video(@PathVariable long uid)
			throws Exception {
		return userService.loadConfirmdVideo(uid);
	}


	@ApiOperation(httpMethod = "POST", value = "获取某用户的粉丝列表") // swagger 当前接口注解
	@RequestMapping("fans/{uid}")
	public ModelMap follow_me(@PathVariable long uid, long user_id, String token, Integer page, Integer count)
			throws Exception {
		return userService.followUsers(uid, true, page, count);
	}
	
	@ApiOperation(httpMethod = "POST", value = "获取某用户的粉丝列表") // swagger 当前接口注解
	@RequestMapping("fans_v2/{uid}")
	public ModelMap getfansByUid(@PathVariable long uid,Integer page, Integer count)
			throws Exception {
		return userService.getUserFans(uid,page, count);
	}
	
	

	@ApiOperation(httpMethod = "POST", value = "关闭app") // swagger 当前接口注解
	@RequestMapping("exit_app")
	public ModelMap exit_app(long user_id, String token) throws Exception {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		  userService.exitApp(user_id);
		  return ResultUtil.getResultOKMap();
	}
	
	@ApiOperation(httpMethod = "POST", value = "关闭该账号") // swagger 当前接口注解
	@RequestMapping("close")
	public ModelMap close(long user_id, String token) throws Exception {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return userService.close(user_id);
	}

	@RequestMapping("match")
	@ApiOperation(httpMethod = "POST", value = "通知服务器视频通话正常进行中") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "exclude_uids", value = "当前已经存在的会话用户id,以,分割", paramType = "query"),
			@ApiImplicitParam(name = "percent", value = "匹配的概率，最低1%，默认30%", paramType = "query"),
			@ApiImplicitParam(name = "days", value = "匹配n天内的登录异性账号", paramType = "query"),
			@ApiImplicitParam(name = "count", value = "每次匹配的数量，默认1", paramType = "query") })
	public ModelMap match(long user_id, String exclude_uids, Integer percent, Integer days, Integer count) {
//		userService.match(user_id, exclude_uids, percent, days, count);
		return ResultUtil.getResultOKMap();
	}
 
	@ApiOperation(httpMethod = "POST", value = "获取某人的简单信息") // swagger 当前接口注解
	@RequestMapping("simple_info/{uid}")
	public ModelMap getUserSimpleInfo(@PathVariable long uid) {
		return ResultUtil.getResultOKMap().addAttribute("user", ImagePathUtil.completeAvatarPath(userService.getBaseUserNoToken(uid),true));
	}
	
	@RequestMapping("unlock_chat")
	@ApiOperation(httpMethod = "POST", value = "解锁聊天功能") // swagger 当前接口注解
	@ApiImplicitParams({
		@ApiImplicitParam(name = "target_uid", value = "解锁聊条功能", paramType = "query", dataType = "Integer") })
	public ModelMap unlock_chat(long user_id,String token,String aid,long target_uid) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return userService.unlockChat(user_id, aid, target_uid);
	}
	@RequestMapping("chat_is_unlock")
	@ApiOperation(httpMethod = "POST", value = "查询是否解锁") // swagger 当前接口注解
	@ApiImplicitParams({
		@ApiImplicitParam(name = "target_uid", value = "解锁对方id", paramType = "query", dataType = "Integer") })
	public ModelMap chat_is_unlock(long user_id,String token,String aid,long target_uid) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return ResultUtil.getResultOKMap().addAttribute("is_unlock", userService.isUnlock(user_id, target_uid));
	}
	@RequestMapping("reply_unlock")
	@ApiOperation(httpMethod = "POST", value = "客户端主动标记解锁") // swagger 当前接口注解
	@ApiImplicitParams({
		@ApiImplicitParam(name = "target_uid", value = "解锁对方id", paramType = "query", dataType = "Integer") })
	public ModelMap reply_unlock(long user_id,String token,String aid,long target_uid) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		userService.markUnlock(user_id, target_uid);
		return ResultUtil.getResultOKMap();
	}
	private City getDefaultCityId() {

		City city = new City();
		city.setId(2);
		city.setName("上海");
		city.setType(0);
		return city;
	}

}
