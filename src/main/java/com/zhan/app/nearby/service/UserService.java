package com.zhan.app.nearby.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.Avatar;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.Tag;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.bean.user.LoginUser;
import com.zhan.app.nearby.bean.user.RankUser;
import com.zhan.app.nearby.bean.user.SimpleUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.AccountStateType;
import com.zhan.app.nearby.comm.AvatarIMGStatus;
import com.zhan.app.nearby.comm.DynamicCommentStatus;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.comm.UserFnStatus;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.dao.TagDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.task.CommAsyncTask;
import com.zhan.app.nearby.task.FaceCheckTask;
import com.zhan.app.nearby.task.HXAsyncTask;
import com.zhan.app.nearby.task.MatchActiveUserTask;
import com.zhan.app.nearby.util.AESUtil;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
public class UserService {

	private static String[] ignoreKeyword = { "微信", "微", "v", "weixin", "wx", "微xin", "QQ", "扣扣", "Q", "手机", "手机号",
			"手机号码", "微信号", "qq号" };
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
	@Resource
	private ManagerService managerService;
	@Autowired
	private MatchActiveUserTask matchActiveUserTask;
	@Autowired
	private FaceCheckTask faceCheckTask;
	@Autowired
	private CommAsyncTask commAsyncTask;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private HXAsyncTask hxTask;

	@Autowired
	private VideoService videoService;

	public BaseUser getBasicUser(long id) {
		return userDao.getBaseUser(id);
	}

	public BaseUser getBaseVipUser(long id) {
		return userDao.getBaseVipUser(id);
	}

	public BaseUser findBaseUserByMobile(String mobile) {
		return userDao.findBaseUserByMobile(mobile);
	}

	public LoginUser findLocationUserByMobile(String mobile) {
		return userDao.findLocationUserByMobile(mobile);
	}

	public LoginUser findLocationUserByOpenId(String openid) {
		return userDao.findLocationUserByOpenid(openid);
	}

	public String getUserMobileById(long user_id) {
		return userDao.getUserMobileById(user_id);
	}

	public String getUserToken(long user_id) {
		return userDao.getUserToken(user_id);
	}

	public BaseUser findUserByDeviceId(String deviceId) {
		return userDao.findUserByDeviceId(deviceId);
	}

	public LoginUser findLocationUserByDeviceId(String deviceId) {
		return userDao.findLocationUserByDeviceId(deviceId);
	}

	@Transactional
	public void insertUser(LoginUser user, boolean isNeedHX) {
		user.setCreate_time(new Date());
		int count = userDao.getUserCountByMobile(user.getMobile());
		if (count > 0) {
			return;
		}
		user.setLast_login_time(new Date());
		userDao.insert(user);
		if (user.getUser_id() > 0 && user.getType() == UserType.OFFIEC.ordinal()) {
			if (user.getIsFace() == 1) {
				addRecommendAndMeetBottle(user.getUser_id());
			}
			saveUserOnline(user.getUser_id());
			if (isNeedHX) {
				registHXThrowException(user);
				matchActiveUserTask.newRegistMatch(user);
			}
		}
		return;
	}

	@Transactional
	public long insertUserThridChannel(LoginUser user, boolean isNeedHX) {
		user.setCreate_time(new Date());
		int count = userDao.getUserCountByOpenId(user.getOpenid());
		if (count > 0) {
			return -1l;
		}
		user.setLast_login_time(new Date());
		userDao.insert(user);
		long id = user.getUser_id();

		if (id > 0 && user.getType() == UserType.OFFIEC.ordinal()
				|| user.getType() == UserType.THRID_CHANNEL.ordinal()) {
			saveUserOnline(user.getUser_id());
			if (isNeedHX) {
				registHXThrowException(user);
				matchActiveUserTask.newRegistMatch(user);
			}
		}
		doCheckIsFace(user);
		commAsyncTask.getUserLocationByIP(user, user.getIp());
		return id;
	}

	public void userOnLineNotify(long uid) {
		commAsyncTask.userOnLineNotify(uid);
	}

	public void doCheckIsFace(BaseUser user) {
		faceCheckTask.doCheckFace(user);
	}

	public int getUserCountByMobile(String mobile) {
		return userDao.getUserCountByMobile(mobile);
	}

	public int getUserCountByOpenid(String openid) {
		return userDao.getUserCountByOpenId(openid);
	}

	@Transactional
	public int updateToken(BaseUser user) {
		// 更新登陆时间
		return userDao.updateToken(user.getUser_id(), user.getToken(), new Date());
	}

	@Transactional
	public int updateToken(BaseUser user, String device_token) {
		// 更新登陆时间
		return userDao.updateToken(user.getUser_id(), user.getToken(), new Date(), device_token);
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
			hxTask.sendWelcome(new String[] { String.valueOf(user_id) }, msg, ext, PushMsgType.TYPE_WELCOME);
		}
	}

	@Transactional
	public int updatePassword(String mobile, String password) {
		return userDao.updatePassword(mobile, password);
	}

	@Transactional
	public int updateAvatar(long user_id, String newAcatar) {
		return userDao.updateAvatar(user_id, newAcatar);
	}

	@Transactional
	public int saveAvatar(long user_id, String avatar) {
		return userDao.saveAvatar(user_id, avatar);
	}

	@Transactional
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

	public void deleteAvatar(int id) {
		userDao.deleteAvatar(id);
	}

	@Transactional
	public int updateLocation(long user_id, String lat, String lng) {
		int count = userDao.updateLocation(user_id, lat, lng);
		// userCacheService.cacheValidateCode(mobile, code);
		return count;
	}

	@Transactional
	public int updateVisitor(long user_id, String aid, String device_token, String lat, String lng, String zh_cn) {
		int count = userDao.updateVisitor(user_id, aid, device_token, lat, lng, zh_cn);
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
		}
		return user;
	}

	@Transactional
	public int modify_info(long user_id, String nick_name, String birthday, String job, String height, String weight,
			String signature, String my_tags, String interests, String animals, String musics, String weekday_todo,
			String footsteps, String want_to_where, boolean isNick_modify, Integer birth_city_id, String contact,
			String newSex) {

		if (!TextUtils.isEmpty(contact)) {
			contact = BottleKeyWordUtil.filterContactContent(contact, Arrays.asList(ignoreKeyword));
		}

		int c = userDao.modify_info(user_id, nick_name, birthday, job, height, weight, signature, my_tags, interests,
				animals, musics, weekday_todo, footsteps, want_to_where, birth_city_id, contact, newSex);
		if (!TextUtils.isEmpty(signature)) {
			userDao.updateSignatureRecord(user_id, signature);
		}
		if (!TextUtils.isEmpty(nick_name)) {
			userDao.updateNicknameRecord(user_id, nick_name);;
		}
		return c;
	}

	@Transactional
	public int visitorToNormal(LoginUser user, boolean isNeedHX) {
		int count = userDao.visitorToNormal(user.getUser_id(), user.getMobile(), user.getPassword(), user.getToken(),
				user.getNick_name(), user.getBirthday(), user.getSex(), user.getAvatar(), user.getLast_login_time());
		if (count > 0) {
			addRecommendAndMeetBottle(user.getUser_id());
			if (isNeedHX) {
				registHXThrowException(user);
				matchActiveUserTask.newRegistMatch(user); // 匹配活跃用户
			}

		}
		return count;
	}

	public void checkHowLongNotOpenApp(long uid) {
		checkHowLongNotOpenApp(getBasicUser(uid));
	}

	public void checkHowLongNotOpenApp(BaseUser user) {
//		Date date = userDao.getUserLastLoginTime(user.getUser_id());
//		if (date == null) {
//			matchActiveUserTask.longTimeNotOpenMatch(user);
//		}
//		int newPercent = percent;
//		if ("0".equals(user.getSex())) {
//			newPercent = (int) (percent * 2.56);
//		}
//		if (RandomCodeUtil.randomPercentOK(newPercent)) { // 5%鐨勬鐜�
//			matchActiveUserTask.shortTimeOpenMatch(user);
//		}
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

	@Transactional
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

	@Transactional
	public void updateRelationship(long user_id, long with_user_id, Relationship relation) {
		userDao.updateRelationship(user_id, with_user_id, relation);
	}

	public List<Long> getAllUserIds(long last_id, int page) {
		return userDao.getAllUserIds(last_id, page);
	}

	public ModelMap getUserCenterData(String token, String aid, Long user_id_for, Long uid, String version) {
		if (user_id_for == null || user_id_for <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "对应用户不存在");
		}
		DetailUser user = userDao.getUserDetailInfo(user_id_for);
		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "对应ID不存在");
		}
		List<UserDynamic> dys = userDynamicService.getUserDynamic(user_id_for, 1, 5, version.compareTo("2.1.0") >= 0);
		user.setImages(dys);

		ImagePathUtil.completeAvatarPath(user, true);
		setTagByIds(user);

		City bc = cityService.getSimpleCity(user.getBirth_city_id());
		user.setBirth_city(bc);

		City cc = cityService.getSimpleCity(user.getCity_id());
		user.setCity(cc);

		ModelMap r = ResultUtil.getResultOKMap();
		user.setAvatars(getUserAvatars(user_id_for));

		if (!TextUtils.isEmpty(user.getContact()) && uid != user_id_for) {
			if (!isVip(uid)) {
				user.setContact("vip可看");
			}
		}

		user.setFans_count(userDao.getFansCount(user.getUser_id()));
		user.setMy_follow_count(userDao.getFollowCount(user.getUser_id()));
		user.setHas_followed(userDao.isFollowed(uid, user_id_for) ? 1 : 0);

		r.put("user", user);
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
//		r.addAttribute("meili", giftService.getUserMeiLiVal(user_id_for));
		r.addAttribute("meili", user.getMeili());
		r.addAttribute("coins", giftService.getUserCoins(aid, user_id_for));
		r.addAttribute("like_count", giftService.getUserBeLikeVal(user_id_for));
		return r;
	}

	public ModelMap getUserCenterDataV2(String token, String aid, long user_id_for, long uid) {
		DetailUser user = userDao.getUserDetailInfo(user_id_for);
		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}

		ImagePathUtil.completeAvatarPath(user, true);
		setTagByIds(user);

		City bc = cityService.getSimpleCity(user.getBirth_city_id());
		user.setBirth_city(bc);

		City cc = cityService.getSimpleCity(user.getCity_id());
		user.setCity(cc);

		ModelMap r = ResultUtil.getResultOKMap();
		user.setAvatars(getUserAvatars(user_id_for));

		if (!TextUtils.isEmpty(user.getContact()) && uid != user_id_for) {
			if (!isVip(uid)) {
				user.setContact("vip可看");
			}
		}

		user.setFans_count(userDao.getFansCount(user.getUser_id()));
		user.setMy_follow_count(userDao.getFollowCount(user.getUser_id()));

		int relationShip = 0;

		String ut = String.valueOf(user_id_for);
		String uu = String.valueOf(uid);
		if (ut.equals(uu)) { // 说明为本人自己
			user.setHas_followed(1);
			relationShip = 4;
			r.addAttribute("coins", giftService.getUserCoins(aid, user_id_for));
			Video v = videoService.loadLatestConfirmVideo(user_id_for);
			if (v != null) {
				v.setUser(null);
			}
			user.setHead_video(v);

		} else {
			user.setHas_followed(userDao.isFollowed(uid, user_id_for) ? 1 : 0);
			Relationship iWithHim = getRelationShip(uid, user_id_for);
			Relationship heWithMe = getRelationShip(user_id_for, uid);
			if (iWithHim == Relationship.LIKE && heWithMe == Relationship.LIKE) {
				relationShip = 4;
			} else if (iWithHim == Relationship.LIKE && heWithMe != Relationship.LIKE) {
				relationShip = 5;
			} else if (iWithHim != Relationship.LIKE && heWithMe == Relationship.LIKE) {
				relationShip = 6;
			} else {
				relationShip = 7;
			}
			r.addAttribute("coins", giftService.getUserCoins(aid, user_id_for));

			Video v = videoService.loadConfirmdVideo(user_id_for);
			if (v != null) {
				v.setUser(null);
			}
			user.setHead_video(v);
		}

		r.put("user", user);
		r.put("relationship", relationShip);
		r.addAttribute("meili", user.getMeili());
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

		// 琛ュ叏 鑱屽睘鎬�
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
		// // 琛ュ叏鎴戠殑鏍囩
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
		// 琛ュ叏鎴戠殑鍏磋叮鐖卞ソ
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
		// 琛ュ叏鎴戝枩娆㈢殑鍔ㄧ墿
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
		// 琛ュ叏鎴戝枩娆㈢殑闊充箰
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
		// 琛ュ叏鍛ㄦ湯鎯冲幓骞插槢
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
		// 琛ュ叏瓒宠抗
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
	 * 鑾峰彇鐢ㄦ埛鍒楄〃
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
	 * 鑾峰彇鐢ㄦ埛鎬绘暟
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
	 * 鑾峰彇鍙戠幇榛戝悕鍗曠敤鎴�
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<BaseUser> getFoundUsers(Long user_id, int page, int count) {
		return userDao.getFoundUsers(user_id, page, count);
	}

	/**
	 * 鑾峰彇榛戝悕鍗曟�绘暟
	 * 
	 * @return
	 */
	public int getFoundUsersCount(Long user_id) {
		return userDao.getFoundUsersCount(user_id);
	}

	/**
	 * 鑾峰彇鍙戠幇榛戝悕鍗曠敤鎴�
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<BaseUser> getBlackUsers(String nick_name, String mobile, int page, int count) {
		return userDao.getBlackUsers(nick_name, mobile, page, count);
	}

	/**
	 * 鑾峰彇榛戝悕鍗曟�绘暟
	 * 
	 * @return
	 */
	public int getBlackUsersCount(String nick_name, String mobile) {
		return userDao.getBlackUsersCount(nick_name, mobile);
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

	public void registHXNoException(BaseUser user) {
		hxTask.registHXUser(user);
	}

	public void registHXThrowException(BaseUser user) {
		hxTask.registHXThrowException(user);
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

	public Map<String, Object> checkIn(Long user_id, String token, String aid) {
		if (user_id == null) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (checkLogin(user_id, token)) {
			int count = userDao.todayCheckInCount(user_id);
			if (count == 0) {
				userDao.todayCheckIn(user_id);
				int checkINCoin = 1;
				Map<String, Object> result = modifyUserExtra(user_id, aid, checkINCoin, 1);
				result.put("coins_checkin", checkINCoin);
				return result;
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_FAILED, "已经签过到");
			}
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
	}

	public Map<String, Object> checkOut(long user_id, int coin, String aid) {
		return modifyUserExtra(user_id, aid, coin, -1);
	}

	public Map<String, Object> rewardCoin(long user_id, int coin, String aid) {
		return modifyUserExtra(user_id, aid, coin, 1);
	}

	public Map<String, Object> modifyUserExtra(long user_id, String aid, int count, int type) {
		return HttpService.modifyUserExtra(user_id, aid, count, type);

	}

	@Transactional
	public void editAvatarState(int id, int state) {
		if (state == AvatarIMGStatus.ILLEGAL.ordinal()) {
			long uid = userDao.editAvatarState(id, AvatarIMGStatus.ILLEGAL.ordinal());
			userDao.clearUserMeetAndFound(uid);
			bottleService.clearIllegalMeetBottle(uid);
		} else {
			userDao.updateAvatarState(id, state);
		}

	}

//	@Transactional
//	public void editAvatarStateByUserId(long uid) {
//		String avatarName = userDao.getCurrentAvatar(uid);
//		try {
//			Integer id = userDao.getAvatarIdByName(avatarName);
//			if (id != null) {
//				userDao.editAvatarState(id, AvatarIMGStatus.ILLEGAL.ordinal());
//			}
//		} catch (Exception e) {
//			userDao.editAvatarStateByUserId(uid, AvatarIMGStatus.ILLEGAL.ordinal());
//		}
//		userDao.setUserSysStatusToNormal(uid);
//		bottleService.clearIllegalMeetBottle(uid);
//	}
	@Transactional
	public void editAvatarStateToIllegal(long uid) {
		String avatarName = userDao.getCurrentAvatar(uid);
		try {
			Integer id = userDao.getAvatarIdByName(avatarName);
			if (id != null) {
				userDao.editAvatarState(id, AvatarIMGStatus.ILLEGAL.ordinal());
			}
		} catch (Exception e) {
			userDao.editAvatarStateByUserId(uid, AvatarIMGStatus.ILLEGAL.ordinal());
		}
		userDao.clearUserMeetAndFound(uid);
		bottleService.clearIllegalMeetBottle(uid);
	}

	@Transactional
	public void deleteIllegalAvatarFileRightNow(long uid) {
		List<String> avatars = userDao.loadAvatarByUid(uid);
		for (String avatar : avatars) {
			ImageSaveUtils.removeAcatar(avatar);
		}
	}

	public ModelMap getContact(long by_user_id, long user_id, String token, String aid) {
		if (!checkLogin(user_id, token)) {
			if (TextUtils.isEmpty(token)) {
				return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN, "Token杩囨湡");
			}
		}
		String weixin = userDao.getContact(by_user_id);
		if (TextUtils.isEmpty(weixin)) {
			return ResultUtil.getResultOKMap().addAttribute("contact", weixin);
		}
		if (user_id == by_user_id || vipDao.isVip(user_id)) {
			return ResultUtil.getResultOKMap().addAttribute("contact", weixin);
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_VIP);
		}
	}

	public ModelMap autoLogin(long user_id, String md5_pwd, String aid, String device_token) {
		boolean exist = userDao.checkExistByIdAndPwd(user_id, md5_pwd);
		if (exist) {
			userDao.uploadLastLoginTime(user_id);
			String token = UUID.randomUUID().toString();
			if (TextUtils.isEmpty(device_token)) {
				userDao.updateToken(user_id, token, new Date());
			} else {
				userDao.updateToken(user_id, token, new Date(), device_token);
			}
			saveUserOnline(user_id);
			return getUserDetailResult(user_id, token, aid);
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
	}

	public ModelMap getUserDetailResult(long user_id, String token, String aid) {
		ModelMap result = ResultUtil.getResultOKMap();
		DetailUser user = userDao.getUserDetailInfo(user_id);
		user.setToken(token);
		ImagePathUtil.completeAvatarPath(user, true); // 琛ュ叏鍥剧墖閾炬帴鍦板潃

		VipUser vip = loadUserVipInfo(aid, user.getUser_id());
		if (vip != null && vip.getDayDiff() >= 0) {
			user.setIsvip(1);
		}

		result.put("user", user);
		result.put("all_coins", loadUserCoins(aid, user.getUser_id()));
		result.put("vip", vip);
		// 触发随机匹配会话
		checkHowLongNotOpenApp(user);
		return result;
	}

	public void saveUserOnline(long uid) {
		userDao.saveUserOnline(uid);
		userOnLineNotify(uid);
	}

	public List<LoginUser> getOnlineUsers(int page, int count) {
		List<LoginUser> users = userDao.getOnlineUsers(page, count);
		ImagePathUtil.completeAvatarsPath(users, true);
		return users;
	}

	public int getOnlineUserCountLastetByMinute(int minuts) {
		return userDao.getOnlineUserCountLastetByMinute(minuts);
	}

	public List<Long> getOnlineUidLastetByMinute(int minuts) {
		return userDao.getOnlineUidLastetByMinute(minuts);
	}

	public List<String> getOnlineUidLastetByLimit(int limit) {
		return userDao.getOnlineUidLastetByLimit(limit);
	}

	@Transactional
	public void removeOnline(long uid) {
		userDao.removeOnline(uid);
	}

	@Transactional
	public void removeTimeoutOnlineUsers(int timeoutMaxMinute) {
		userDao.removeTimeoutOnlineUsers(timeoutMaxMinute);
	}

	public void checkRegistIP(int limitCount) {
		List<String> list = userDao.checkRegistIP(limitCount);
		for (String ip : list) {
			boolean r = IPUtil.addIPBlack(ip);
			if (r) {
				clearIllegalUserAndCreate();
			}
		}
	}

	@Transactional
	public void clearIllegalUserAndCreate() {
		List<String> ips = IPUtil.getIpBlackList();
		for (String ip : ips) {
			List<Long> uids = userDao.loadIllegalRegistUids(ip);
			for (long uid : uids) {
				editAvatarStateToIllegal(uid);
				bottleService.clearUserBottle(uid);
				deleteIllegalAvatarFileRightNow(uid);
				userDao.deleteIllegalUser(uid);
			}
		}
	}

	public void addRecommendAndMeetBottle(long user_id) {
		userDao.setUserFnStatusEnable(user_id);
	}

	public List<Avatar> listNotCheckedAvatars(int count) {
		return userDao.listNotCheckedAvatars(count);
	}

	public List<BaseUser> listConfirmAvatars(int state, int pageSize, int pageIndex, Long user_id) {
		return userDao.listConfirmAvatars(state, pageSize, pageIndex, user_id);
	}

	public List<BaseUser> listAvatarsByUid(int pageSize, int pageIndex, Long user_id, String nickName) {
		return userDao.listAvatarsByUid(pageSize, pageIndex, user_id, nickName);
	}

	public int getCountOfConfirmAvatars(Long user_id, int state) {
		return userDao.getCountOfConfirmAvatars(user_id, state);
	}

	public int getCountOfUserAvatars(Long user_id, String nickName) {
		return userDao.getCountOfUserAvatars(user_id, nickName);
	}

	public int getUserSysStatus(long uid) {
		return userDao.getUserSysStatus(uid);
	}

	public void clearExpireMeetBottleUser() {
		userDao.clearExpireMeetBottleUser();
	}

	public void follow(long user_id, long target_id, boolean cancel) {
		if (cancel) {
			userDao.cancelFollow(user_id, target_id);
		} else {
			if (!userDao.isFollowed(user_id, target_id)) {
				userDao.follow(user_id, target_id);
			}
		}
	}

	public BaseUser getBaseUserNoToken(long user_id) {
		return userDao.getBaseUserNoToken(user_id);
	}

	/**
	 * 鍒ゆ柇鏄惁鍏虫敞浜嗚鐢ㄦ埛
	 * 
	 * @param user_id
	 * @param targetUid
	 * @return
	 */
	public boolean isFollowed(long user_id, long targetUid) {
		return userDao.isFollowed(user_id, targetUid);
	}

	/**
	 * 娑堣�楅噾甯�
	 * 
	 * @param user_id
	 * @param token
	 * @param aid
	 * @param coin
	 * @return
	 */
	public Map<String, Object> cost_coin(long user_id, String token, String aid, int coin) {
		if (coin < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		if (coin == 0) {
			return ResultUtil.getResultOKMap();
		}
		if (!checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		Map<String, Object> result = modifyUserExtra(user_id, aid, coin, -1);
		result.put("cost_coins", coin);
		return result;

	}

	public Map<String, Object> costCoin(long user_id, String aid, int coin) {
		if (coin < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		if (coin == 0) {
			return ResultUtil.getResultOKMap();
		}
		Map<String, Object> result = modifyUserExtra(user_id, aid, coin, -1);
		result.put("cost_coins", coin);
		return result;

	}

	/**
	 * 澧炲姞閲戝竵
	 * 
	 * @param user_id
	 * @param token
	 * @param aid
	 * @param coin
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	public Map<String, Object> addCoin(long user_id, String token, String content) throws Exception {
//		if (!checkLogin(user_id, token)) {
//			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
//		}
		if (TextUtils.isEmpty(content)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		String discontent = AESUtil.aesDecryptString(content, token);
		String[] params = discontent.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String s : params) {
			String[] p = s.split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}

		String aid = map.get("aid");
		int extra = Integer.parseInt(map.get("extra"));
		String task_id = map.get("task_id");
		String uuid = map.get("uuid");

		int count = userDao.getTaskCount(user_id, aid, task_id, uuid);
		if (count > 0) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		userDao.savaTaskHistory(user_id, aid, task_id, uuid, extra);
		Map<String, Object> result = modifyUserExtra(user_id, aid, extra, 1);
		result.put("reward_coins", extra);
		return result;

	}

	/**
	 * 鍑忓皯閲戝竵
	 * 
	 * @param user_id
	 * @param token
	 * @param aid
	 * @param coin
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	public Map<String, Object> minusCoin(long user_id, String token, String content) throws Exception {
//		if (!checkLogin(user_id, token)) {
//			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
//		}
		if (TextUtils.isEmpty(content)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		String discontent = AESUtil.aesDecryptString(content, token);
		String[] params = discontent.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String s : params) {
			String[] p = s.split("=");
			if (p.length == 2) {
				map.put(p[0], p[1]);
			}
		}

		String aid = map.get("aid");
		int extra = Integer.parseInt(map.get("extra"));
		Map<String, Object> result = modifyUserExtra(user_id, aid, extra, -1);
		result.put("minus_coins", extra);
		return result;

	}

	public ModelMap followUsers(long user_id, boolean isFollowMe, Integer page, Integer count) {
		int c = count == null ? 20 : count;
		List<BaseUser> users = userDao.followUsers(user_id, isFollowMe, page == null ? 1 : page, c);
		ImagePathUtil.completeAvatarsPath(users, true);
		long last_id = users.size() == 0 ? 0 : users.get(users.size() - 1).getUser_id();
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == c)
				.addAttribute("last_id", last_id);
	}

	public ModelMap getFollowUsers(long user_id, boolean isFollowMe, Integer page, Integer count) {
		int c = count == null ? 20 : count;
		List<RankUser> users = userDao.getFollowUsersByUid(user_id, page == null ? 1 : page,
				count == null ? 10 : count);
		ImagePathUtil.completeAvatarsPath(users, true);
		long last_id = users.size() == 0 ? 0 : users.get(users.size() - 1).getUser_id();
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == c)
				.addAttribute("last_id", last_id);
	}

	public ModelMap getUserFans(long user_id, Integer page, Integer count) {
		int c = count == null ? 20 : count;
		List<RankUser> users = userDao.getFansByUid(user_id, page == null ? 1 : page, count == null ? 10 : count);
		ImagePathUtil.completeAvatarsPath(users, true);
		long last_id = users.size() == 0 ? 0 : users.get(users.size() - 1).getUser_id();
		return ResultUtil.getResultOKMap().addAttribute("users", users).addAttribute("hasMore", users.size() == c)
				.addAttribute("last_id", last_id);
	}

	// 璇ユ柟娉曚负
	public List<BaseUser> getActiveUser(int sex) {
		return userDao.getActiveUsers(sex);
	}

	public ModelMap test_new_user_regist(long uid, long other_uid) {
		BaseUser user = userDao.getBaseUser(uid);
		System.out.println(Thread.currentThread().getName());
		matchActiveUserTask.newRegistMatch(user);
		return ResultUtil.getResultOKMap();
	}

	public void testLongTimeNoLogin(long user_id, long target_id) {
		BaseUser i = userDao.getBaseUser(user_id);
		BaseUser he = userDao.getBaseUser(target_id);
		hxTask.createChatSessionRandMsg(i, he);
	}

	/**
	 * 涓婁紶鎵嬫満鐨刣evice_token
	 * 
	 * @param user_id
	 * @param token
	 */
	public void addDeviceToken(long user_id, String device_token) {
		userDao.addDeviceToken(user_id, device_token);
	}

	public ModelMap close(long uid) {
		userDao.close(uid);
		return ResultUtil.getResultOKMap();
	}

	public ModelMap testMakeSession(long f, long to) {
		BaseUser fu = userDao.getBaseUser(f);
		BaseUser toU = userDao.getBaseUser(to);
		hxTask.createChatSessionRandMsg(fu, toU);
		return ResultUtil.getResultOKMap();
	}

	public List<BaseUser> get2daysLoginUserWithOutIds(long uid, int sex, int day, int count, long[] withOutids) {
		return userDao.get2daysLoginUserWithOutIds(uid, sex, day, count, withOutids);
	}

	public List<BaseUser> get2daysLoginUser(long uid, int sex, int day, int count) {
		return userDao.get2daysLoginUser(uid, sex, day, count);
	}

	public void saveMatchLog(long uid, long tuid) {
		userDao.saveMatchLog(uid, tuid);
	}

//	public void match(long user_id, String exclude_uids, Integer percent, Integer days, Integer count) {
//		String[] uids = null;
//		long[] eids = null;
//		if (!TextUtils.isEmpty(exclude_uids)) {
//			uids = exclude_uids.split(",");
//			eids = new long[uids.length];
//			for (int i = 0; i < uids.length; i++) {
//				eids[i] = Long.parseLong(uids[i]);
//			}
//		}
//
//		BaseUser cur = getBasicUser(user_id);
//		if (percent == null || percent < 1) {
//			percent = this.percent;
//		}
//		int sex = 0;
//		if ("0".equals(cur.getSex())) {
//			percent = (int) (percent * 2.56);
//			sex = 1;
//		}
//
//		if (days == null || days < 1) {
//			days = 90;
//		}
//
//		if (count == null || count < 1) {
//			count = 1;
//		}
//
//		if (RandomCodeUtil.randomPercentOK(percent)) { //
//			matchActiveUserTask.newMatch(cur, eids, days, count, sex);
//		}
//
//	}

	public ModelMap doLogin(LoginUser tempUser, String _ua, String aid, City defaultCity) {

		// login by openid;
		LoginUser user = userDao.findLocationUserByOpenid(tempUser.getOpenid());

		if (user == null)

		{
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "璇ヨ处鍙蜂笉瀛樺湪");
		}

		if (user.getAccount_state() == AccountStateType.CLOSE.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_CLOSE, "璇ヨ处鍙峰凡缁忔敞閿�");
		}

		if (getUserSysStatus(user.getUser_id()) == SysUserStatus.BLACK.ordinal()) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "璇ヨ处鍙峰洜涓炬姤鑰屾棤娉曠櫥褰�");
		}
		// 妫�鏌ヨ鐢ㄦ埛澶氫箙娌＄櫥闄嗕簡
//		checkHowLongNotOpenApp(user);
		ModelMap result = ResultUtil.getResultOKMap("鐧诲綍鎴愬姛");
		user.setToken(UUID.randomUUID().toString());
		user.set_ua(_ua);
		pushLongTimeNoLoginMsg(user.getUser_id(), user.getLast_login_time());
		saveUserOnline(user.getUser_id());
		updateToken(user); // 鏇存柊token锛屽急鐧诲綍
		user.set_ua(null);

		ImagePathUtil.completeAvatarPath(user, true); // 琛ュ叏鍥剧墖閾炬帴鍦板潃
		if (user.getCity() == null) {
			user.setCity(defaultCity);
		}

		if (user.getBirth_city_id() > 0) {
			user.setBirth_city(cityService.getSimpleCity(user.getBirth_city_id()));
		}
		VipUser vip = loadUserVipInfo(aid, user.getUser_id());
		if (vip != null && vip.getDayDiff() >= 0) {
			user.setIsvip(1);
		}
		result.put("user", user);
		// result.put("all_coins", userService.loadUserCoins(aid, user.getUser_id()));
		result.put("vip", vip);
		result.put("isFirstLogin", false);
		return result;

	}

	public int updateAvatarIsFace(long user_id, int isFace) {
		return userDao.updateAvatarIsFace(user_id, isFace);
	}

	public int updateAvatarState(int id, int state) {
		return userDao.updateAvatarState(id, state);
	}

	public void editAvatarStateToIllegal(long user_id, String avatarName) {
		userDao.editAvatarStateToIllegal(user_id, avatarName);
		userDao.setUserSysStatusToNormal(user_id);
		bottleService.clearIllegalMeetBottle(user_id);
	}

	public int updateUserBirthCity(long user_id, City city) {
		return userDao.updateUserBirthCity(user_id, city.getId());
	}

	/**
	 * 是否能修改性别，只能修改1次
	 * 
	 * @param uid
	 * @return
	 */
	public boolean canModifySex(long uid) {
		int times = userDao.getSexModifyTimes(uid);
		return times <= 0;
	}

	public boolean matchActiveUsers() {

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());

		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		if (hour < 5) { // 5点开始执行
			return false;
		}

		int days = 90; // 3个月的内登录的男性账号
		int count = userDao.getNeedMatchManCount(days);

		if (count == 0) {

			return false;
		}
		int everyTimesCount = count / ((19 * 60) / 5); // 19小时，每5分钟执行一次
		if (hour == 23) {// 当天的最后一次匹配
			if (minute >= 55) {
				everyTimesCount = count;
			}
		}

		List<Long> womenUids = userDao.getActiveWomenUserNotDoMatchUids(count);
		if (womenUids.isEmpty()) { //
			return false;
		}

		do {
			int startIndex = new Random().nextInt(count);
			List<BaseUser> mans = userDao.getActiveManToMatch(days, startIndex, 1);
			if (mans.isEmpty()) {
				continue;
			}
			BaseUser man = mans.get(0);

			int womanstartIndex = new Random().nextInt(womenUids.size());
			BaseUser woman = userDao.getBaseUserNoToken(womenUids.get(womanstartIndex));
			womenUids.remove(womanstartIndex);// 移除，防止下次被重新拿到

			String msg = HX_SessionUtil.getRandomMsg();
			ImagePathUtil.completeAvatarPath(woman, true);
			ImagePathUtil.completeAvatarPath(man, true);

			saveMatchLog(man.getUser_id(), woman.getUser_id());

			hxTask.createChatSession(woman, man, msg);

			everyTimesCount--;
			count--;
		} while (everyTimesCount > 0);
		return true;
	}

	public void clearUserMatchData() {
		userDao.clearUserMatchData();
	}

	public void updateModifySexTimes(long user_id) {
		userDao.updateModifySexTimes(user_id);
	}

	public String getOpenIdByUid(long user_id) {
		return userDao.getOpenIdByUid(user_id);
	}

	public void updateBaseInfo(long user_id, String nick_name, String avatar, Date birthday, City city) {
		userDao.updateBaseInfo(user_id, nick_name, avatar, birthday, city);
		if(TextUtils.isNotEmpty(nick_name)) {
			userDao.updateNicknameRecord(user_id, nick_name);
		}
		if (!TextUtils.isEmpty(avatar)) {
			saveAvatar(user_id, avatar);
			faceCheckTask.doCheckFace(getBaseUserNoToken(user_id));
		}
	}

	public ModelMap unlockChat(long user_id, String aid, long target_uid) {
		int count = userDao.getUnlockChatCount(user_id, target_uid);
		if (count > 0) {
			return ResultUtil.getResultOKMap();
		}
		// 解锁聊天，消耗一个金币
		Map<String, Object> r = costCoin(user_id, aid, 1);
		if (r != null) {
			Object all_coinsObj = r.get("all_coins");
			if (all_coinsObj == null) {
				return ResultUtil.getResultFailed();
			}
			int all_coins = (int) all_coinsObj;
			if (all_coins >= 0) {
				userDao.unlockChat(user_id, target_uid);
				return ResultUtil.getResultOKMap();
			} else {
				return ResultUtil.getResultMap(ERROR.ERR_COINS_SHORT);
			}
		} else {
			return ResultUtil.getResultFailed();
		}
	}

	public boolean isUnlock(long user_id, long target_uid) {
		return userDao.getUnlockChatCount(user_id, target_uid) > 0;
	}

	public void markUnlock(long user_id, long target_uid) {
		userDao.unlockChat(user_id, target_uid);
	}

	public boolean isVip(long user_id) {
		return vipDao.isVip(user_id);
	}

	public void updateMeiLiValue(long user_id, int addMeiLi) {
		userDao.addMeili(user_id, addMeiLi);
	}

	public void updateUserVipVal(long user_id, boolean isVip) {
		userDao.updateUserVipVal(user_id, isVip);
	}

	public void updateUserLOcation(long uid, String lat, String lng) {
		userDao.updateUserLocation(uid, lat, lng);
	}

	public void changeUserCertStatus(long uid, int certStatus) {
		userDao.changeUserCertStatus(uid, certStatus);
	}

	public ModelMap loadConfirmdVideo(long uid) {
		return ResultUtil.getResultOKMap().addAttribute("video", videoService.loadConfirmdVideo(uid));
	}

	public ModelMap loadConfirmVideo(long uid) {
		return ResultUtil.getResultOKMap().addAttribute("video", videoService.loadConfirmdVideo(uid));
	}

	public Date getLastSendTime(long uid) {
		return userDao.getLastSendTime(uid);
	}

	public void updateLastSendTime(long uid) {
		userDao.updateLastSendTime(uid);
	}

	public void updateNotifyTime(List<String> uids) {
		for (String id : uids) {
			userDao.updateNotifyTime(Long.parseLong(id));
		}
	}

	public void exitApp(long user_id) {
		userDao.cleanOnline(user_id);
	}

	public int getSignatureUpdateUsersCount(Long user_id) {
		return userDao.getSignatureUpdateUsersCount(user_id);
	}
	public int getNicknameUpdateUsersCount(Long user_id) {
		return userDao.getNicknameUpdateUsersCount(user_id);
	}

	public List<SimpleUser> loadSignatureUpdateUsers(Long user_id, int page, int count) {
		List<SimpleUser> users = userDao.loadSignatureUpdateUsers(user_id, page, count);
		ImagePathUtil.completeAvatarsPath(users, true);
		return users;
	}
	public List<SimpleUser> loadNicknameUpdateUsers(Long user_id, int page, int count) {
		List<SimpleUser> users = userDao.loadNicknameUpdateUsers(user_id, page, count);
		ImagePathUtil.completeAvatarsPath(users, true);
		return users;
	}

	public void deleteUserSignature(Long uid) {
		userDao.deleteUserSignature(uid);
	}
	public void deleteUserNickname(Long uid) {
		userDao.deleteUserNickname(uid);
		nickNameIllegal(uid);
	}

	public void setUserSysStatusToNormal(long uid) {
		userDao.setUserSysStatusToNormal(uid);
	}

	public void setUserSysStatusTo(long uid, SysUserStatus status) {
		if (status == SysUserStatus.BLACK) {
			userDao.clearUserMeetAndFound(uid); // 清理用户的推荐邂逅瓶状态，首页推荐状态，
			bottleService.clearIllegalMeetBottleAndMarkBlack(uid); // 清理用户的邂逅瓶子，标记用户的瓶子为黑名单状态
			userDao.markDynamicIllegal(uid);// 标记用户发的动态为黑名单状态
			userDynamicService.updateCommentStatus(uid, DynamicCommentStatus.ILLEGAL); // 标记评论为黑名单
			appointmentService.markDataBlack(uid); // 标记约会为黑名单
			userDao.removetSignatureUpdateRecord(uid);
			userDao.clearToken(uid);
			hxTask.disconnectUser(String.valueOf(uid)); //强制用户下线
		}
		userDao.setUserSysStatusTo(uid, status);
	}

	public void setUserFoundFn(long uid, UserFnStatus fn) {
		userDao.setUserFoundFn(uid, fn);
	}

	public void clearUserMeetAndFound(long uid) {
		userDao.clearUserMeetAndFound(uid);
	}

	public String nickNameIllegal(long user_id) {
		String nickName = userDao.getNickNameById(user_id).get(0);
		char[] arr = nickName.toCharArray();
		Arrays.fill(arr, 0, arr.length, '*');
		String starName = new String(arr);
		userDao.updateNickName(user_id, starName);
		return starName;
	}
}
