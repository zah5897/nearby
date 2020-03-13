package com.zhan.app.nearby.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.BGM;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.Exchange;
import com.zhan.app.nearby.bean.MeiLi;
import com.zhan.app.nearby.bean.PersonalInfo;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.RankUser;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.AccountStateType;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.GiftDao;
import com.zhan.app.nearby.dao.SystemDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.task.CommAsyncTask;
import com.zhan.app.nearby.task.HXAsyncTask;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.RandomCodeUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SMSHelper;
import com.zhan.app.nearby.util.TextUtils;

@Service
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

	@Autowired
	private HXAsyncTask hxTask;
	@Autowired
	private CommAsyncTask commAsyncTask;

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

	public ModelMap getHomeFoundSelected(long user_id, Long last_id, Integer count, Integer city_id) {
		if (count == null) {
			count = 20;
		}
		City city = null;
		if (city_id != null) {
			city = cityService.getFullCity(city_id); // 获取城市
		}
		List<UserDynamic> dynamics = userDynamicDao.getHomeFoundSelected(user_id, last_id, count, city);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("hasMore", dynamics.size() == count);
		result.put("last_id", dynamics.get(dynamics.size() - 1).getId());
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
			long tuid = Long.parseLong(with_ids[i]);
			changeRelationShip(user_id, tuid, Relationship.LIKE);
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
			long tuid = Long.parseLong(with_ids[i]);
			changeRelationShip(user_id, tuid, Relationship.BLACK);
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
			long tuid = Long.parseLong(with_ids[i]);
			changeRelationShip(user_id, tuid, Relationship.IGNORE);
			commAsyncTask.updateMeiLiValByLike(tuid);
		}
		return ResultUtil.getResultOKMap();
	}

	private ModelMap changeRelationShip(long user_id, long with_user, Relationship ship) {
		try {
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
							hxTask.createChatSessionRandMsg(user, withUser);
						}
					}
				}
			}
		} catch (NumberFormatException e) {
		}
		return ResultUtil.getResultOKMap();
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
		if (type == 0) {
			List<MeiLi> users = userDao.getNewRegistUsers(pageIndex, count);
			return ResultUtil.getResultOKMap().addAttribute("users", users);
		} else if (type == 1) {
			List<MeiLi> meili = giftService.loadMeiLi(pageIndex, count);
			return ResultUtil.getResultOKMap().addAttribute("users", meili);
		} else if (type == 2) {
			List<MeiLi> meili = giftService.loadTuHao(pageIndex, count);
			return ResultUtil.getResultOKMap().addAttribute("users", meili);
		} else if (type == 3) {
			return ResultUtil.getResultOKMap().addAttribute("users", userDao.getVipRankUsers(pageIndex, count));
		}
		return ResultUtil.getResultOKMap();
	}

	public ModelMap rank_list_v2(long user_id,int type, Integer pageIndex, Integer count) {
		if (pageIndex == null || pageIndex <= 0) {
			pageIndex = 1;
		}
		if (count == null) {
			count = 20;
		}
		
		
		ModelMap r=	ResultUtil.getResultOKMap();
		
		List<? extends BaseUser> users = null;
		if(type==-2) {
			users = userDao.getIWatching(user_id,pageIndex, count);
		}else if(type==-1) {
			users = userDao.getRankOnlineUsers(pageIndex, count);
		}else if (type == 0) {
			users = userDao.getNewRegistUsersV2(pageIndex, count);
		} else if (type == 1) {
			users = giftService.loadMeiLiV2(pageIndex, count);
		} else if (type == 2) {
			users = giftService.loadTuHaoV2(pageIndex, count);
		} else if (type == 3) {
			users=userDao.getVipRankUsersV2(pageIndex, count);
		}
		r.addAttribute("hasMore", users.size()==count);
		ImagePathUtil.completeAvatarsPath(users, true);
		return r.addAttribute("users", users);
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

	public ModelMap getTopUsers(Long fix_user_id, Integer page, Integer count) {
		if (page == null || page < 1) {
			page = 1;
		}
		if (count == null || count < 1) {
			count = 6;
		}
		BaseUser fix_user = null;
		if (fix_user_id != null && fix_user_id > 0) {
			fix_user = userDao.getBaseUser(fix_user_id);
		}
		List<BaseUser> users;
		if (fix_user != null) {
			users = systemDao.getTouTiaoUser((page - 1) * count, count - 1);
			users.add(0, fix_user);
		} else {
			users = systemDao.getTouTiaoUser((page - 1) * count, count);
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		for (BaseUser u : users) {
			u.setBirth_city(cityService.getSimpleCity(u.getBirth_city_id()));
			u.setCity(cityService.getSimpleCity(u.getCity_id()));
		}
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == count);
	}

	public ModelMap getmomoUsers(String gender, Long fix_user_id, Integer count) {
		if (count == null || count < 1) {
			count = 6;
		}
		BaseUser fix_user = null;
		if (fix_user_id != null && fix_user_id > 0) {
			fix_user = userDao.getBaseUser(fix_user_id);
		}
		int startIndex = 0;
		List<BaseUser> users;
		if (fix_user != null) {
			List<Map<String, Object>> index_userids = systemDao.getTouTiaoUserIndexVal();
			for (Map<String, Object> idMap : index_userids) {
				long uid = (long) idMap.get("uid");
				if (uid == fix_user.getUser_id()) {
					double i = (double) idMap.get("i");
					startIndex = (int) i;
					break;
				}
			}
			users = systemDao.getTouTiaoUser(startIndex - 1, count);
		} else {
			users = systemDao.loadMaxRateMeiLiRandom(fix_user_id, gender, count);
		}

		ImagePathUtil.completeAvatarsPath(users, true);
		for (BaseUser u : users) {
			u.setBirth_city(cityService.getSimpleCity(u.getBirth_city_id()));
			u.setCity(cityService.getSimpleCity(u.getCity_id()));
		}
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == count);
	}

	public ModelMap getHotUsers(String gender, Long fix_user_id, Integer page, Integer count) {
		if (page == null || page < 1) {
			page = 1;
		}
		if (count == null || count < 1) {
			count = 6;
		}
		List<BaseUser> users;
		if (page == 1 && fix_user_id == null) {
			users = systemDao.getTouTiaoUser(0, count);
		} else if (page == 1 && fix_user_id != null) {
			users = getAfterFixUsers(fix_user_id, count);
		} else {
			users = systemDao.loadMaxRateMeiLiRandom(fix_user_id, gender, count);
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		for (BaseUser u : users) {
			u.setBirth_city(cityService.getSimpleCity(u.getBirth_city_id()));
			u.setCity(cityService.getSimpleCity(u.getCity_id()));
		}
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == count);
	}

	private List<BaseUser> getAfterFixUsers(long fix_user_id, int count) {
		int startIndex = 0;
		List<Map<String, Object>> index_userids = systemDao.getTouTiaoUserIndexVal();
		for (Map<String, Object> idMap : index_userids) {
			long uid = (long) idMap.get("uid");
			if (uid == fix_user_id) {
				double i = (double) idMap.get("i");
				startIndex = (int) i;
				break;
			}
		}
		return systemDao.getTouTiaoUser(startIndex - 1, count);
	}

//	public int injectRate() {
//		return systemDao.injectRate();
//	}

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
		systemDao.insertObject(personal);
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
		ModelMap data = ResultUtil.getResultOKMap();
		boolean smsOK = SMSHelper.smsExchangeCode(mobile, code);
		if (smsOK) {
			userCacheService.cacheRegistValidateCode(mobile, code, code_type);
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
					hxTask.disconnectUser(String.valueOf(report.getTarget_id()));
					userDao.updateAccountState(report.getTarget_id(), AccountStateType.LOCK.ordinal());
				} else {
					userDynamicDao.delete(report.getUser_id(), report.getTarget_id());
				}
			}
			systemDao.updateReportState(id, isIgnore ? -1 : 1);
		}
	}

	public void saveBGM(BGM bgm) {
		bgm.setCreate_time(new Date());
		if (systemDao.isExist(bgm) > 0) {
			systemDao.updateBGM(bgm);
			return;
		}
		systemDao.insertObject(bgm);
	}

	public List<BGM> loadBGM(Integer count, Integer test) {
		if (test != null && test == 1) { // test=1表示客户端调试，一定返回
			return systemDao.loadBGM(count == null || count <= 0 ? 1 : count);
		} else {
			int rand = new Random().nextInt(10);
			if (rand < 2) {
				return systemDao.loadBGM(count == null || count <= 0 ? 1 : count);
			} else {
				return new ArrayList<BGM>();
			}
		}
	}

	public ModelMap goods_id_list(int type) {
		ModelMap goodsList = ResultUtil.getResultOKMap();
		if (type == 0) {
			List<String> vips = new ArrayList<String>();
			vips.add("vip_2");
			vips.add("vip_3");
			vips.add("vip_4");
			vips.add("vip_5");
			List<String> coins = new ArrayList<String>();
			coins.add("sb_60");
			coins.add("sb_310");
			coins.add("sb_520");
			coins.add("sb_1340");
			coins.add("sb_3460");
			coins.add("sb_7580");
			goodsList.addAttribute("vip_goods_ids", vips);
			goodsList.addAttribute("coin_goods_ids", coins);
		}
		return goodsList;
	}

	public ModelMap test_redis() {
		userCacheService.test();
		return ResultUtil.getResultOKMap().addAttribute("test", userCacheService.getTest());
	}

	public Map<String, Object> buy_first_position(long user_id, String aid) {
		long id = systemDao.getTouTiaoFirstUserId();
		if (user_id == id) {
			return ResultUtil.getResultOKMap("您已经在头条了");
		}
		Map<String, Object> map = HttpService.buy(user_id, aid, 1, "buy_first_position");
		if (map != null) {
			int code = Integer.parseInt(map.get("code").toString());
			if (code == 0) {
				systemDao.insertTouTiaoUser(user_id);
			}
			return map;
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
	}

	public List<String> loadBlackWords() {
		return systemDao.loadBlackWords();
	}

	public void addBlackWord(String word) {
		if (word == null || TextUtils.isEmpty(word.trim())) {
			return;
		}
		systemDao.insertBlackWord(word);
	}

	public void removeBlackWord(String word) {
		systemDao.delBlackWord(word);
	}
}
