package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.BGM;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.bean.PersonalInfo;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.BaseVipUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.AccountStateType;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.GiftDao;
import com.zhan.app.nearby.dao.SystemDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.RandomCodeUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SMSHelper;
import com.zhan.app.nearby.util.TextUtils;

@Service
@Transactional("transactionManager")
public class MainService {
	@Resource
	private UserDynamicDao userDynamicDao;
	@Resource
	private UserDao userDao;
	@Resource
	private GiftDao giftDao;
	@Resource
	private SystemDao systemDao;
	@Resource
	private VipDao vipDao;
	@Resource
	private DynamicMsgService dynamicMsgService;

	@Resource
	private GiftService giftService;

	@Resource
	CityService cityService;
	@Resource
	UserService userService;
	@Resource
	UserCacheService userCacheService;

	public ModelMap found_users(Long user_id, Integer page_size, Integer gender) {
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
		List<BaseUser> users = userDao.getFoundUserRandom(user_id, realCount, gender);
		ImagePathUtil.completeAvatarsPath(users, true);
		result.put("users", users);
		return result;
	}

	public ModelMap newRegistUsers(Integer page, Integer count) {
		if (page == null || page < 0) {
			page = 1;
		}
		if (count == null || count < 1) {
			count = 10;
		}
		ModelMap result = ResultUtil.getResultOKMap();
		List<MeiLi> users = userDao.getNewRegistUsers(page, count);
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
			city_id = 0;
		}
		City city = cityService.getFullCity(city_id);
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

	public int getMostByCity() {
		return userDynamicDao.getMostCityID();
	}

	public ModelMap like(long user_id, String with_user_id) {
		if (user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST);
		}
		String[] with_ids = with_user_id.split(",");
		int len = with_ids.length;
		for (int i = 0; i < len; i++) {
			changeRelationShip(user_id, with_ids[i], Relationship.LIKE);
		}
		return ResultUtil.getResultOKMap();
	}

	public ModelMap addBlock(long user_id, String with_user_id) {
		if (user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST);
		}
		String[] with_ids = with_user_id.split(",");
		int len = with_ids.length;
		for (int i = 0; i < len; i++) {
			changeRelationShip(user_id, with_ids[i], Relationship.BLACK);
		}
		return ResultUtil.getResultOKMap();
	}

	public ModelMap ignore(long user_id, String with_user_id) {
		if (user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST);
		}
		String[] with_ids = with_user_id.split(",");
		int len = with_ids.length;
		for (int i = 0; i < len; i++) {
			changeRelationShip(user_id, with_ids[i], Relationship.IGNORE);
		}
		return ResultUtil.getResultOKMap();
	}

	private ModelMap changeRelationShip(long user_id, String with_user_id, Relationship ship) {
		try {
			long with_user = Long.parseLong(with_user_id);
			if (with_user != user_id) {
				BaseUser withUser = userDao.getBaseUser(with_user);
				if (withUser != null) {
					userDao.updateRelationship(user_id, with_user, ship);
					if (ship == Relationship.LIKE) {
						BaseUser user = userDao.getBaseUser(user_id);
						dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_LIKE, user_id, with_user,
								withUser.getUser_id(), "");
						int count = userDao.isLikeMe(user_id, with_user);
						if (count > 0) { // 对方喜欢我了，这个时候我也喜欢对方了，需要互相发消息
							makeChatSession(user, withUser);
						}
					}
				}
			}
		} catch (NumberFormatException e) {
		}
		return ResultUtil.getResultOKMap();
	}

	public void makeChatSession(BaseUser user, BaseUser with_user) {
		ImagePathUtil.completeAvatarPath(with_user, true);
		ImagePathUtil.completeAvatarPath(user, true);

		String chatSessionTxt = "很高兴遇见你";

		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		Main.sendTxtMessage(String.valueOf(user.getUser_id()), new String[] { String.valueOf(with_user.getUser_id()) },
				chatSessionTxt, ext, PushMsgType.TYPE_NEW_CONVERSATION);

		// 发送给自己
		ext = new HashMap<String, String>();
		ext.put("nickname", with_user.getNick_name());
		ext.put("avatar", with_user.getAvatar());
		ext.put("origin_avatar", with_user.getOrigin_avatar());
		Main.sendTxtMessage(String.valueOf(with_user.getUser_id()), new String[] { String.valueOf(user.getUser_id()) },
				chatSessionTxt, ext, PushMsgType.TYPE_NEW_CONVERSATION);

	}

	public ModelMap reset_city() {
		List<UserDynamic> dynamics = userDynamicDao.getAllDynamic();
		if (dynamics != null) {
			for (UserDynamic dynamic : dynamics) {
				int province_id = 0;
				int city_id = 0;
				int district_id = 0;
				if (dynamic.getDistrict_id() > 0) {
					district_id = dynamic.getDistrict_id();
					City curCity = cityService.getFullCity(dynamic.getDistrict_id());
					if (curCity.getParent_id() > 0) {
						City city = cityService.getFullCity(curCity.getParent_id());
						city_id = city.getId();
						if (city.getParent_id() > 0) {
							City parent = cityService.getFullCity(city.getParent_id());
							province_id = parent.getId();
						} else {
							province_id = city_id;
						}
					}
				} else if (dynamic.getCity_id() > 0) {
					City city = cityService.getFullCity(dynamic.getCity_id());
					city_id = city.getId();
					if (city.getParent_id() > 0) {
						City parent = cityService.getFullCity(city.getParent_id());
						province_id = parent.getId();
					} else {
						province_id = city_id;
					}
				}
				userDynamicDao.updateCityId(dynamic.getId(), province_id, city_id, district_id);
			}
		}
		return ResultUtil.getResultOKMap();
	}

	public ModelMap meiliList(int type, Integer pageIndex, Integer count) {

		if (pageIndex == null || pageIndex <= 0) {
			pageIndex = 1;
		}

		if (count == null) {
			count = 20;
		}

		if (type != 3) {
			List<MeiLi> meili = giftService.loadMeiLi(type, pageIndex, count);
			return ResultUtil.getResultOKMap().addAttribute("users", meili);
		} else {
			return newRegistUsers(pageIndex, count);
		}

		// 这个地方的rank_list字段用 users

		// return ResultUtil.getResultOKMap().addAttribute("rank_list", meili);
	}

	public ModelMap exchange_history(long user_id, String aid, Integer page_index, Integer count) {
		if (page_index == null) {
			page_index = 1;
		}

		if (page_index < 1) {
			page_index = 1;
		}
		if (count == null) {
			count = 20;
		}

		ModelMap result = ResultUtil.getResultOKMap();
		if (page_index == 1) {
			Integer totalRMB = systemDao.getTotalExchangeRmmbByState(user_id, aid, ExchangeState.EXCHANGED);
			result.put("total_exchange_rmb_fen", totalRMB != null ? totalRMB : 0);
		}
		return result.addAttribute("exchange_histories",
				systemDao.loadExchangeHistory(user_id, aid, page_index, count));
	}

//	public ModelMap exchange_rmb(long user_id, String token, String aid, int diamond, String zhifubao_access_number,
//			String mobile, String code) {
//
//		if (TextUtils.isEmpty(zhifubao_access_number)) {
//			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "请输入支付宝帐号");
//		}
//
//		boolean isLogin = userService.checkLogin(user_id, token);
//		if (!isLogin) {
//			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
//		}
//		// 验证code合法性
//		if (TextUtils.isEmpty(code) || !userCacheService.valideCode(mobile, code)) {
//			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "验证码错误");
//		} else {
//			// userCacheService.clearCode(mobile);
//		}
//
//		PersonalInfo info = systemDao.loadPersonalInfo(user_id, aid);
//		if (info == null) {
//			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "请先绑定个人信息");
//		} else if (!zhifubao_access_number.equals(info.getZhifubao_access_number())) {
//			return ResultUtil.getResultMap(ERROR.ERR_ZHIFUBAO_ACCOUNT_NOT_MATCH).addAttribute("personal_info", info);
//		}
//
//		int val = giftDao.getVal(user_id);
//		if (diamond > val) {
//			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "钻石数量不足");
//		}
//		int newVal = val - diamond;
//		giftDao.updateGiftCoins(user_id, newVal);
//
//		Exchange exchange = new Exchange();
//		exchange.setUser_id(user_id);
//		exchange.setAid(aid);
//		exchange.setCreate_time(new Date());
//		exchange.setDiamond_count(diamond);
//		exchange.setRmb_fen(diamond * 3);
//		exchange.setState(ExchangeState.IN_EXCHANGE.ordinal());
//		systemDao.addExchangeHistory(exchange);
//		return ResultUtil.getResultOKMap("提交成功").addAttribute("value", newVal);
//	}

	public ModelMap getHotUsers(String gender, Long fix_user_id, Integer page_index) {
		int limit = 6;
		BaseVipUser fix_user = null;

		if (page_index == null || page_index < 1) {
			page_index = 1;
		}

		if (page_index == 1 && fix_user_id != null && fix_user_id > 0) {
			limit = 5;
			fix_user = userDao.getBaseVipUser(fix_user_id);
		}

		List<BaseVipUser> users = systemDao.loadMaxRateMeiLiRandom(fix_user_id, gender, page_index, limit);
		// List<BaseUser> users = systemDao.loadMaxRateMeiLi(fix_user_id, gender,
		// page_index, limit);
		if (users.size() < limit) {
			users = systemDao.loadMaxMeiLi(fix_user_id, gender, page_index, limit);
		}

		if (fix_user != null) {

			for (BaseVipUser user : users) {
				user.setVip(vipDao.isVip(user.getUser_id()));
			}
			users.add(0, fix_user);
		}
		ImagePathUtil.completeAvatarsPath(users, true);

		for (BaseVipUser u : users) {
			u.setBirth_city(cityService.getSimpleCity(u.getBirth_city_id()));
			u.setCity(cityService.getSimpleCity(u.getCity_id()));
		}
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == 6);
	}

	public int injectRate() {
		return systemDao.injectRate();
	}

	public ModelMap check_submit_personal_id(PersonalInfo personal) {
		String token = userService.getUserToken(personal.getUser_id());
		if (TextUtils.isEmpty(token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (!token.equals(personal.getToken())) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		PersonalInfo info = systemDao.loadPersonalInfo(personal.getUser_id(), personal.getAid());
		if (info != null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "当前帐号已绑定身份证");
		}
		systemDao.savePersonalInfo(personal);
		return ResultUtil.getResultOKMap().addAttribute("personal_info", personal);
	}

	public ModelMap check_submit_zhifubao(PersonalInfo personal, String code) {
		if (TextUtils.isEmpty(personal.getZhifubao_access_number())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "支付宝帐号不能为空");
		}
		// 验证code合法性
		if (TextUtils.isEmpty(code) || !userCacheService.valideRegistCode(personal.getMobile(), code)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "验证码错误");
		}
		PersonalInfo info = systemDao.loadPersonalInfo(personal.getUser_id(), personal.getAid());
		if (info == null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "个人身份证未绑定");
		}
		systemDao.updatePersonalInfo(personal.getUser_id(), personal.getAid(), personal.getZhifubao_access_number(),
				personal.getMobile());
		info = systemDao.loadPersonalInfo(personal.getUser_id(), personal.getAid());
		return ResultUtil.getResultOKMap().addAttribute("personal_info", info);
	}

	public ModelMap load_personal_info(long user_id, String token, String aid) {
		PersonalInfo info = systemDao.loadPersonalInfo(user_id, aid);
		return ResultUtil.getResultOKMap().addAttribute("personal_info", info);
	}

	// 修改个人绑定的信息
	public ModelMap modify_bind_personal_info(PersonalInfo personal, String code) {
		if (TextUtils.isEmpty(personal.getZhifubao_access_number())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "支付宝帐号不能为空");
		}
		// 验证code合法性
		if (TextUtils.isEmpty(code) || !userCacheService.valideRegistCode(personal.getMobile(), code)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "验证码错误");
		}

		if (TextUtils.isEmpty(personal.getPersonal_name())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "绑定的姓名不能为空");
		}

		if (TextUtils.isEmpty(personal.getPersonal_id())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "身份证号不能为空");
		}

		PersonalInfo info = systemDao.loadPersonalInfo(personal.getUser_id(), personal.getAid());
		if (info == null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "个人身份证未绑定,无法修改");
		}
		systemDao.updatePersonalInfo(personal);
		return ResultUtil.getResultOKMap().addAttribute("personal_info", personal);
	}

	public ModelMap get_personal_validate_code(long user_id, String token, String mobile, Integer code_type) {
		boolean isLogin = userService.checkLogin(user_id, token);
		if (!isLogin) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "手机号码不能为空");
		}

		String code = RandomCodeUtil.randomCode(6);
		
		if(code_type==null) {
			code_type=0;
		}
		
		if (code_type == -1000) {
			userCacheService.cacheRegistValidateCode(mobile, code,code_type);
			return ResultUtil.getResultOKMap().addAttribute("validate_code", code);
		}
//		String cache = userCacheService.getCachevalideCode(mobile);
//		if (cache != null) {
//			// 已经在一分钟内发过，还没过期
//		}
		ModelMap data = ResultUtil.getResultOKMap();
		HashMap<String, Object> result = SMSHelper.smsExchangeCode(mobile, code);
		boolean smsOK = SMSHelper.isSuccess(result);
		if (smsOK) {
			userCacheService.cacheRegistValidateCode(mobile, code,code_type);
			data.put("validate_code", code);
		} else {
			// String errorMsg = "错误码=" + result.get("statusCode") + " 错误信息= " +
			// result.get("statusMsg");
			// data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送失败:"+errorMsg);
			data = ResultUtil.getResultMap(ERROR.ERR_FAILED, "获取验证码次数过多，明天再试。");
		}
		return data;
	}

	public ModelMap getSpecialUsers(int page, int limit) {
		List<BaseUser> users = userService.loadSpecialUsers(page, limit);
		ImagePathUtil.completeAvatarsPath(users, true);
		return ResultUtil.getResultOKMap().addAttribute("users", users);
	}

	public int getSpecialUsersCount() {
		return userService.getSpecialUsersCount();
	}

	public int delSpecialUser(long uid) {
		return userService.delSpecialUser(uid);
	}

	public int addSpreadUser(long uid) {
		return userService.addSpecialUser(uid);
	}

	public boolean backExchange(int id) {
		Exchange ex = systemDao.loadExchange(id);
		int i = giftDao.addGiftCoins(ex.getUser_id(), ex.getDiamond_count());
		return i > 0;
	}

	public void saveReport(Report report) {
		if (!systemDao.existReport(report)) {
			report.setCreate_time(new Date());
			systemDao.insertReport(report);
		}
	}

	public List<Report> listReport(int type, int page, int count) {
		List<Report> list = systemDao.listReport(type, page, count);
		return list;
	}

	public List<Report> listManagerReport(int approval_type, int count, int page) {
		List<Report> reports = systemDao.listManagerReport(approval_type, page, count);
		if (reports != null) {
			for (Report report : reports) {
				if (report.getType() == 0) {
					BaseUser user = userDao.getBaseUser(report.getTarget_id());
					ImagePathUtil.completeAvatarPath(user, false);
					report.setUser(user);
				}
			}
		}
		return reports;
	}

	public int getReportSizeByApproval(int approval_type) {
		return systemDao.getReportSizeByApproval(approval_type);
	}

	public void handleReport(int id, boolean isIgnore) {
		Report report = systemDao.getReport(id);
		if (report != null) {
			if (!isIgnore) {
				if (report.getType() == 0) {
					Main.disconnectUser(String.valueOf(report.getTarget_id()));
					userDao.updateAccountState(report.getTarget_id(), AccountStateType.LOCK.ordinal());
				} else {
					userDynamicDao.delete(report.getUser_id(), report.getTarget_id());
				}
			}
			systemDao.updateReportState(id, isIgnore ? -1 : 1);
		}
	}

	public void saveBGM(BGM bgm) {
		systemDao.insertBGM(bgm);
	}

	public List<BGM> loadBGM(Integer count) {
		return systemDao.loadBGM(count == null || count <= 0 ? 1 : count);
	}
}
