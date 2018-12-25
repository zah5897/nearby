package com.zhan.app.nearby.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.easemob.server.example.comm.wrapper.ResponseWrapper;
import com.zhan.app.nearby.bean.Avatar;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.Tag;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.bean.user.LocationUser;
import com.zhan.app.nearby.bean.user.SimpleUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.AvatarIMGStatus;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.dao.TagDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.AppException;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.HttpsUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.PropertiesUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
@Transactional("transactionManager")
public class UserService {
	@Resource
	private UserDao userDao;
	@Resource
	private TagDao tagDao;
	@Resource
	private UserCacheService userCacheService;
	@Resource
	private CityService cityService;
	@Resource
	private UserDynamicService userDynamicService;

	@Resource
	private GiftService giftService;
	@Resource
	private VipDao vipDao;
	@Resource
	private DynamicMsgService dynamicMsgService;

	@Resource
	private BottleService bottleService;

	public BaseUser getBasicUser(long id) {
		return userDao.getBaseUser(id);
	}

	public BaseUser findBaseUserByMobile(String mobile) {
		return userDao.findBaseUserByMobile(mobile);
	}

	public LocationUser findLocationUserByMobile(String mobile) {
		return userDao.findLocationUserByMobile(mobile);
	}

	public String getUserToken(long user_id) {
		return userDao.getUserToken(user_id);
	}

	public BaseUser findUserByDeviceId(String deviceId) {
		return userDao.findUserByDeviceId(deviceId);
	}

	public LocationUser findLocationUserByDeviceId(String deviceId) {
		return userDao.findLocationUserByDeviceId(deviceId);
	}

	// @Transactional(readOnly = true)
	// public User findUserByName(String name) {
	// return userDao.findUserByName(name);
	// }

	public void delete(long id) {
		userDao.delete(id);
	}

	public long insertUser(BaseUser user) {
		user.setCreate_time(new Date());
		int count = userDao.getUserCountByMobile(user.getMobile());
		if (count > 0) {
			return -1l;
		}
		long id = (Long) userDao.insert(user);
		user.setUser_id(id);
		if (id > 0 && user.getType() == UserType.OFFIEC.ordinal()) {
			saveUserOnline(user.getUser_id());
			registHX(user);
		}
		return id;
	}

	public int getUserCountByMobile(String mobile) {
		return userDao.getUserCountByMobile(mobile);
	}

	public int updateToken(BaseUser user) {
		// 更新登陆时间
		return userDao.updateToken(user.getUser_id(), user.getToken(), new Date());
	}

	public void pushLongTimeNoLoginMsg(long user_id, Date lastLoginTime) {

		long now = System.currentTimeMillis() / 1000 / 60 / 60 / 24;
		long loginTime;
		if (lastLoginTime == null) {
			loginTime = 0;
		} else {
			loginTime = lastLoginTime.getTime() / 1000 / 60 / 60 / 24;
		}
		if (now - loginTime >= 3) {
			Map<String, String> ext = new HashMap<String, String>();
			String msg = userCacheService.getWelcome();
			ext.put("msg", msg);
			Main.sendTxtMessage(Main.SYS, new String[] { String.valueOf(user_id) }, msg, ext, PushMsgType.TYPE_WELCOME);
		}
	}

	public int updatePassword(String mobile, String password) {
		return userDao.updatePassword(mobile, password);
	}

	public int updateAvatar(long user_id, String newAcatar) {
		return userDao.updateAvatar(user_id, newAcatar);
	}

	public int saveAvatar(long user_id, String avatar) {
		return userDao.saveAvatar(user_id, avatar);
	}

	public String deleteAvatar(long user_id, String avatar_id) {
		int count = userDao.getAvatarCount(user_id);
		if (count > 1) {
			String delAvatar = userDao.deleteAvatar(user_id, avatar_id);
			ImageSaveUtils.removeAcatar(delAvatar);
			String topAvatar = userDao.getLastAvatar(user_id);
			updateAvatar(user_id, topAvatar);
			return delAvatar;
		} else {
			return null;
		}
	}

	public int updateLocation(long user_id, String lat, String lng) {
		int count = userDao.updateLocation(user_id, lat, lng);
		// userCacheService.cacheValidateCode(mobile, code);
		return count;
	}

	public int updateVisitor(long user_id, String app_id, String device_token, String lat, String lng, String zh_cn) {
		int count = userDao.updateVisitor(user_id, app_id, device_token, lat, lng, zh_cn);
		return count;
	}

	public DetailUser getUserDetailInfo(long user_id_for) {
		DetailUser user = userDao.getUserDetailInfo(user_id_for);
		if (user != null) {
			if (user.getCity_id() > 0) {
				user.setCity(cityService.getSimpleCity(user.getCity_id()));
			}
			if (user.getBirth_city_id() > 0) {
				user.setBirth_city(cityService.getSimpleCity(user.getBirth_city_id()));
			}

			ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址
			user.hideSysInfo();
		}
		return user;
	}

	public int modify_info(long user_id, String nick_name, String birthday, String job, String height, String weight,
			String signature, String my_tags, String interests, String animals, String musics, String weekday_todo,
			String footsteps, String want_to_where, boolean isNick_modify, Integer birth_city_id, String contact) {
		return userDao.modify_info(user_id, nick_name, birthday, job, height, weight, signature, my_tags, interests,
				animals, musics, weekday_todo, footsteps, want_to_where, birth_city_id, contact);
	}

	public int visitorToNormal(SimpleUser user) {
		int count = userDao.visitorToNormal(user.getUser_id(), user.getMobile(), user.getPassword(), user.getToken(),
				user.getNick_name(), user.getBirthday(), user.getSex(), user.getAvatar(), user.getLast_login_time());
		if (count > 0) {
			registHX(user);
		}
		return count;
	}

	public void uploadLocation(final String ip, final Long user_id, String lat, String lng) {

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

	public void setCity(Long user_id, Integer city_id) {

		if (user_id == null || user_id > 0) {
			return;
		}

		if (city_id == null) {
			return;
		}

		userDao.setCity(user_id, city_id);
	}

	public void updateRelationship(long user_id, long with_user_id, Relationship relation) {
		userDao.updateRelationship(user_id, with_user_id, relation);
	}

	public List<Long> getAllUserIds(long last_id, int page) {
		return userDao.getAllUserIds(last_id, page);
	}

	public ModelMap getUserCenterData(String token, String aid, Long user_id_for, Long uid) {
		if (user_id_for == null || user_id_for <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "对应用户不存在");
		}
		DetailUser user = userDao.getUserDetailInfo(user_id_for);
		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "对应ID不存在");
		}

		user.setAge(DateTimeUtil.getAge(user.getBirthday()));

		List<UserDynamic> dys = userDynamicService.getUserDynamic(user_id_for, 1, 5, true);
		user.setImages(dys);
		// //
		// Map<String, Object> userJson = new HashMap<>();
		// userJson.put("about_me", user);
		// Map<String, Object> secret_me = new HashMap<String, Object>();
		// userJson.put("secret_me", secret_me);
		// userJson.put("my_tags", new HashMap<>());
		// ModelMap result = ResultUtil.getResultOKMap();
		// result.put("user", userJson);

		ImagePathUtil.completeAvatarPath(user, true);
		setTagByIds(user);

		City bc = cityService.getSimpleCity(user.getBirth_city_id());
		user.setBirth_city(bc);

		City cc = cityService.getSimpleCity(user.getCity_id());
		user.setCity(cc);

		ModelMap r = ResultUtil.getResultOKMap();
		user.setIs_vip(vipDao.isVip(user_id_for));
		user.setAvatars(getUserAvatars(user_id_for));

		if (!TextUtils.isEmpty(user.getContact()) && uid != user_id_for) {
			if (!userDao.hadGetContact(uid == null ? 0 : uid, user_id_for == null ? 0 : user_id_for)) {
				user.setContact("花费金币查看");
			}
		}
		// JSONUtil.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		// Map<String, Object> map=JSONUtil.jsonToMap(user);

		r.put("user", user);
		// Object w=map.get("want_to_where");
		// System.out.println(w);
		Relationship iWithHim = getRelationShip(uid == null ? 0 : uid, user_id_for);
		Relationship heWithMe = getRelationShip(user_id_for, uid == null ? 0 : uid);
		int relationShip = 0;
		if (iWithHim == Relationship.LIKE && heWithMe == Relationship.LIKE) {
			relationShip = 4;
		} else if (iWithHim == Relationship.LIKE && heWithMe != Relationship.LIKE) {
			relationShip = 5;
		} else if (iWithHim != Relationship.LIKE && heWithMe == Relationship.LIKE) {
			relationShip = 6;
		} else {
			relationShip = 7;
		}
		r.put("relationship", relationShip);
		r.addAttribute("meili", giftService.getUserMeiLiVal(user_id_for));
		r.addAttribute("coins", giftService.getUserCoins(aid, user_id_for));
		r.addAttribute("like_count", giftService.getUserBeLikeVal(user_id_for));
		return r;
	}

	public List<Avatar> getUserAvatars(long user_id) {
		return ImagePathUtil.completeAvatarsPath(userDao.getUserAvatars(user_id));
	}

	public List<Tag> getTagsByType(int type) {
		return tagDao.getTagsByType(type);
	}

	public void setTagByIds(DetailUser user) {

		String ids[];
		List<Tag> tags = tagDao.getTags();
		if (tags == null || tags.size() == 0) {
			return;
		}

		// 补全 职属性
		if (!TextUtils.isEmpty(user.getJob_ids())) {
			ids = user.getJob_ids().split(",");
			List<Tag> jobs = new ArrayList<Tag>();
			for (String id : ids) {
				try {
					int tag_id = Integer.parseInt(id);
					for (Tag tag : tags) {
						if (tag.getType() == Tag.TYPE_JOB && tag.getId() == tag_id) {
							jobs.add(tag);
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
			user.setJobs(jobs);
		}
		// // 补全我的标签
		if (!TextUtils.isEmpty(user.getMy_tag_ids())) {
			ids = user.getMy_tag_ids().split(",");
			List<Tag> myTags = new ArrayList<Tag>();
			for (String id : ids) {
				try {
					int tag_id = Integer.parseInt(id);
					for (Tag tag : tags) {
						if (tag.getType() == Tag.TYPE_TAG && tag.getId() == tag_id) {
							myTags.add(tag);
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
			user.setMy_tags(myTags);
		}
		// 补全我的兴趣爱好
		if (!TextUtils.isEmpty(user.getInterest_ids())) {
			ids = user.getInterest_ids().split(",");
			List<Tag> myInterest = new ArrayList<Tag>();
			for (String id : ids) {
				try {
					int tag_id = Integer.parseInt(id);
					for (Tag tag : tags) {
						if (tag.getType() == Tag.TYPE_INTEREST && tag.getId() == tag_id) {
							myInterest.add(tag);
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
			user.setInterest(myInterest);
		}
		// 补全我喜欢的动物
		if (!TextUtils.isEmpty(user.getAnimal_ids())) {
			ids = user.getAnimal_ids().split(",");
			List<Tag> myAnimal = new ArrayList<Tag>();
			for (String id : ids) {
				try {
					int tag_id = Integer.parseInt(id);
					for (Tag tag : tags) {
						if (tag.getType() == Tag.TYPE_LIKE_ANIMAL && tag.getId() == tag_id) {
							myAnimal.add(tag);
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
			user.setFavourite_animal(myAnimal);
		}
		// 补全我喜欢的音乐
		if (!TextUtils.isEmpty(user.getMusic_ids())) {
			ids = user.getMusic_ids().split(",");
			List<Tag> musics = new ArrayList<Tag>();
			for (String id : ids) {
				try {
					int tag_id = Integer.parseInt(id);
					for (Tag tag : tags) {
						if (tag.getType() == Tag.TYPE_LIKE_MUSIC && tag.getId() == tag_id) {
							musics.add(tag);
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
			user.setFavourite_music(musics);
		}
		// 补全周末想去干嘛
		if (!TextUtils.isEmpty(user.getWeekday_todo_ids())) {
			ids = user.getWeekday_todo_ids().split(",");
			List<Tag> weekday = new ArrayList<Tag>();
			for (String id : ids) {
				try {
					int tag_id = Integer.parseInt(id);
					for (Tag tag : tags) {
						if (tag.getType() == Tag.TYPE_WEEKDAY && tag.getId() == tag_id) {
							weekday.add(tag);
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
			}
			user.setWeekday_todo(weekday);
		}
		// 补全足迹
		if (!TextUtils.isEmpty(user.getFootstep_ids()))

		{
			ids = user.getFootstep_ids().split(",");
			List<Tag> footstep = new ArrayList<Tag>();
			for (String id : ids) {
				try {
					int tag_id = Integer.parseInt(id);
					for (Tag tag : tags) {
						if (tag.getType() == Tag.TYPE_FOOTSTEPS && tag.getId() == tag_id) {
							footstep.add(tag);
						}
					}
				} catch (NumberFormatException e) {
					continue;
				}
				user.setFootsteps(footstep);
			}
		}
	}

	public ModelMap getUserAvatar(long user_id) {
		String avatar = userDao.getUserAvatar(user_id);
		String[] avatars = ImagePathUtil.completeAvatarPath(avatar);
		return ResultUtil.getResultOKMap().addAttribute("thumb", avatars[0]).addAttribute("origin", avatars[1]);
	}

	public ModelMap getUserSimple(long user_id) {
		BaseUser user = userDao.getBaseUser(user_id);
		if (user != null) {
			ImagePathUtil.completeAvatarPath(user, true);
			return ResultUtil.getResultOKMap().addAttribute("user", user);
		}
		return ResultUtil.getResultOKMap().addAttribute("user", null);
	}

	public ModelMap getUserProperty(long user_id, String aid) {
		return null;
	}

	public int loadUserCoins(String aid, long user_id) {
		Map<?, ?> map = HttpService.queryUserCoins(user_id, aid);
		if (map == null) {
			return 0;
		}
		int code = (int) map.get("code");
		if (code == 0) {
			return (int) map.get("all_coins");
		} else {
			return 0;
		}
	}

	public VipUser loadUserVipInfo(String aid, long user_id) {
		return vipDao.loadUserVip(user_id);
	}

	/**
	 * 获取用户列表
	 * 
	 * @param pageSize
	 * @param currentPage
	 * @return
	 */
	public List<BaseUser> getAllUser(int pageSize, int currentPage, int type, String keyword, Long user_id) {
		if (type == -1) {
			return userDao.getUsers(pageSize, currentPage, keyword, user_id == null ? 0 : user_id);
		} else {
			return userDao.getUsers(pageSize, currentPage, type, keyword, user_id == null ? 0 : user_id);
		}
	}

	/**
	 * 获取用户总数
	 * 
	 * @return
	 */
	public int getUserSize(int type, String keyword, Long user_id) {
		if (type == -1) {
			return userDao.getUserSize(keyword, user_id == null ? 0 : user_id);
		} else {
			return userDao.getUserSize(type, keyword, user_id == null ? 0 : user_id);
		}
	}

	public String getUserGenderByID(long user_id) {
		return userDao.getUserGenderByID(user_id);
	}

	/**
	 * 获取发现黑名单用户
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<BaseUser> getFoundUsersByState(int pageSize, int pageIndex, FoundUserRelationship ship) {
		return userDao.getFoundUsersByState(pageSize, pageIndex, ship);
	}

	/**
	 * 获取黑名单总数
	 * 
	 * @return
	 */
	public int getFoundUsersCountByState(FoundUserRelationship ship) {
		return userDao.getFoundUsersCountByState(ship);
	}

	public List<BaseUser> getAllMeetBottleRecommendUser(int pageSize, int pageIndex, String keyword) {
		return userDao.getAllMeetBottleRecommendUser(pageSize, pageIndex, keyword);
	}

	public int getMeetBottleRecommendUserSize(String keyword) {
		return userDao.getMeetBottleRecommendUserSize(keyword);
	}

	public ModelMap likeList(long user_id, Integer page_index, Integer count) {

		if (page_index == null || page_index < 1) {
			page_index = 1;
		}

		if (count == null || count < 1) {
			count = 10;
		}
		ModelMap r = ResultUtil.getResultOKMap();
		List<BaseUser> likeMeList = userDao.getLikeList(user_id, page_index, count);
		r.addAttribute("users", likeMeList);
		ImagePathUtil.completeAvatarsPath(likeMeList, true);
		if (page_index == 1) {
			if (!likeMeList.isEmpty()) {
				r.addAttribute("last_one", likeMeList.get(0));
			} else {
				r.addAttribute("last_one", null);
			}
		}
		r.addAttribute("has_more", likeMeList.size() >= count);
		return r;
	}

	public boolean checkLogin(long user_id, String token) {
		int count = userDao.getUserCountByIDToken(user_id, token);
		return count > 0;
	}

	public Relationship getRelationShip(long user_id, long user_id_for) {
		return userDao.getRelationShip(user_id, user_id_for);
	}

	private void registHX(BaseUser user) {
		try {
			String id = String.valueOf(user.getUser_id());
			String password = MD5Util.getMd5_16(id);
			Object resutl = Main.registUser(id, password, user.getNick_name());
			if (resutl != null) {
				if (resutl instanceof ResponseWrapper) {
					ResponseWrapper response = (ResponseWrapper) resutl;
					if (response.getResponseStatus() != 200) {
						throw new AppException(ERROR.ERR_SYS, new RuntimeException("环信注册失败"));
					}
				}
			}
		} catch (Exception e) {
			throw new AppException(ERROR.ERR_SYS, new RuntimeException("环信注册失败"));
		}
	}

	public int addSpecialUser(long uid) {
		return userDao.addSpecialUser(uid);
	}

	public int delSpecialUser(long uid) {
		return userDao.delSpecialUser(uid);
	}

	public List<BaseUser> loadSpecialUsers(Integer pageIndex, Integer limit) {
		return userDao.loadSpecialUsers(pageIndex == null ? 1 : pageIndex, limit == null ? 5 : limit);
	}

	public int getSpecialUsersCount() {
		return userDao.getSpecialUsersCount();
	}

	public Map<String, Object> checkIn(long user_id, String token, String aid) {
		if (checkLogin(user_id, token)) {
			int count = userDao.todayCheckInCount(user_id);
			if (count == 0) {
				userDao.todayCheckIn(user_id);
				return modifyExtra(user_id, aid, 1, 1);
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_FAILED, "已经签过到");
			}
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
	}
	
	public Map<String, Object> checkOut(long user_id,int coin, String aid) {
		return modifyExtra(user_id, aid,coin, -1);	 
	}

	
	public Map<String, Object> rewardCoin(long user_id,int coin, String aid) {
		return modifyExtra(user_id, aid,coin, 1);	 
	}
	
	private String MODULE_ORDER_A_EXTRA;

	public Map<String, Object> modifyExtra(long user_id, String aid, int count, int type) {
		if (TextUtils.isEmpty(MODULE_ORDER_A_EXTRA)) {
			Properties prop = PropertiesUtil.load("config.properties");
			String value = PropertiesUtil.getProperty(prop, "MODULE_ORDER_A_EXTRA");
			MODULE_ORDER_A_EXTRA = value;
		}
		String result = null;
		try {
			result = HttpsUtil.sendHttpsPost(MODULE_ORDER_A_EXTRA + "?id_user=" + user_id + "$&aid=" + aid + "&extra="
					+ count + "_&type=" + type);
			if (!TextUtils.isEmpty(result)) {
				Map<String, Object> resultData = JSONUtil.jsonToMap(result);
				if (resultData != null) {
					resultData.put("coins_checkin", count);
				}
				return resultData;
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}

	public void editAvatarState(int id) {
		long uid = userDao.editAvatarState(id, AvatarIMGStatus.ILLEGAL.ordinal());
		bottleService.clearIllegalMeetBottle(uid);
	}

	public void editAvatarStateByUserId(long uid) {
		String avatarName = userDao.getCurrentAvatar(uid);
		try {
			Integer id = userDao.getAvatarIdByName(avatarName);
			if (id != null) {
				userDao.editAvatarState(id, AvatarIMGStatus.ILLEGAL.ordinal());
			}
		} catch (Exception e) {
			userDao.editAvatarStateByUserId(uid, AvatarIMGStatus.ILLEGAL.ordinal());
		}
		bottleService.clearIllegalMeetBottle(uid);
	}

	public void deleteIllegalAvatarFile() {
		List<String> avatars = userDao.loadIllegalAvatar();
		for (String avatar : avatars) {
			ImageSaveUtils.removeAcatar(avatar);
		}
	}
	
	public void deleteIllegalAvatarFileRightNow(long uid) {
		List<String> avatars = userDao.loadAvatarByUid(uid);
		for (String avatar : avatars) {
			ImageSaveUtils.removeAcatar(avatar);
		}
	}
	
	
	

	public ModelMap openApp(long user_id, String token, String aid) {
		if (checkLogin(user_id, token)) {
			saveUserOnline(user_id);

			userDao.uploadLastLoginTime(user_id);
			return ResultUtil.getResultOKMap();
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
	}

	public ModelMap getContact(long by_user_id, long user_id, String token, String aid) {
		if (!checkLogin(user_id, token)) {
			if (TextUtils.isEmpty(token)) {
				return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN, "Token过期");
			}

		}

		String weixin = userDao.getContact(by_user_id);
		if (TextUtils.isEmpty(weixin)) {
			return ResultUtil.getResultOKMap().addAttribute("contact", weixin);
		}

		if (userDao.hadGetContact(user_id, by_user_id)) {
			return ResultUtil.getResultOKMap().addAttribute("contact", weixin);
		}
		// 金币减1
		Map<String, Object> extraData = modifyExtra(user_id, aid, 1, -1);
		if (extraData != null && extraData.containsKey("all_coins")) {
			int all_coins = (int) extraData.get("all_coins");
			if (all_coins < 0) {
				throw new AppException(ERROR.ERR_COINS_SHORT);
			} else {
				modifyExtra(by_user_id, aid, 1, 1);
				try {
					userDao.markContactRel(user_id, by_user_id);
				} catch (Exception e) {
					// 主键约束导致插入语失败
				}
			}
		}
		return ResultUtil.getResultOKMap().addAttribute("contact", weixin).addAttribute("contact_cost_coins", 1);
	}

	public ModelMap autoLogin(long user_id, String md5_pwd, String aid) {
		boolean exist = userDao.checkExistByIdAndPwd(user_id, md5_pwd);
		if (exist) {
			String token = UUID.randomUUID().toString();
			userDao.updateToken(user_id, token, new Date());

			saveUserOnline(user_id);

			ModelMap result = ResultUtil.getResultOKMap();
			DetailUser user = userDao.getUserDetailInfo(user_id);
			user.setToken(token);
			ImagePathUtil.completeAvatarPath(user, true); // 补全图片链接地址

			VipUser vip = loadUserVipInfo(aid, user.getUser_id());

			if (vip != null && vip.getDayDiff() >= 0) {
				user.setVip(true);
				user.setIs_vip(true);
			}

			result.put("user", user);
			result.put("all_coins", loadUserCoins(aid, user.getUser_id()));
			result.put("vip", vip);
			return result;
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "登录失败");
		}
	}

	public void saveUserOnline(long uid) {
		try {
			userDao.saveUserOnline(uid);
		} catch (Exception e) {
			userDao.updateOnlineCheckTime(uid);
		}
	}

	public List<BaseUser> getOnlineUsers(int page, int count) {
		List<BaseUser> users = userDao.getOnlineUsers(page, count);
		ImagePathUtil.completeAvatarsPath(users, true);
		return users;
	}

	public void removeOnline(long uid) {
		userDao.removeOnline(uid);
	}

	public void removeTimeoutOnlineUsers(int timeoutMaxMinute) {
		userDao.removeTimeoutOnlineUsers(timeoutMaxMinute);
	}
	
	public void checkRegistIP(int limitCount) {
		List<String> list=userDao.checkRegistIP(limitCount);
		for(String ip:list) {
			boolean r=IPUtil.addIPBlack(ip);
			if(r) {
				clearIllegalUserAndCreate();
			}
		}
	}
	
	public void clearIllegalUserAndCreate() {
		List<String> ips=IPUtil.getIpBlackList();
		for(String ip:ips) {
			List<Long> uids=userDao.loadIllegalRegistUids(ip);
			for(long uid:uids) {
				editAvatarStateByUserId(uid);
				bottleService.clearUserBottle(uid);
				deleteIllegalAvatarFileRightNow(uid);
				userDao.deleteIllegalUser(uid);
			}
		}
	}
	
	
	public List<BaseUser> listConfirmAvatars(int state, int pageSize, int pageIndex) {
		return userDao.listConfirmAvatars(state, pageSize, pageIndex);
	}

	public int getCountOfConfirmAvatars() {
		return userDao.getCountOfConfirmAvatars();
	}

	public int getUserState(long uid) {
		return userDao.getUserState(uid);
	}

	
	public void clearExpireMeetBottleUser() {
		userDao.clearExpireMeetBottleUser();
	}
}
