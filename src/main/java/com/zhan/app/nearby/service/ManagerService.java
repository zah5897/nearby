package com.zhan.app.nearby.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.HXHistoryMsg;
import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.DynamicStatus;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.comm.UserFnStatus;
import com.zhan.app.nearby.comm.VideoStatus;
import com.zhan.app.nearby.dao.ManagerDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.task.HXAsyncTask;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.ResultUtil;

@Service
public class ManagerService {

	@Resource
	private ManagerDao managerDao;
	@Resource
	private UserCacheService userCacheService;
	@Resource
	private UserService userService;
	@Resource
	private UserDao userDao;

	@Resource
	private MainService mainService;
	@Resource
	private BottleService bottleService;

	@Resource
	private UserDynamicService userDynamicService;
	@Resource
	private VipService vipService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private VideoService videoService;

	@Autowired
	private GiftService giftService;

	@Autowired
	private HXAsyncTask hxTask;
	@Autowired
	private HXService hxService;

	public boolean mLogin(HttpServletRequest request, String name, String pwd) {
		Integer i = managerDao.queryM(name, pwd);
		if (i > 0) {
			String ip = IPUtil.getIpAddress(request);
			userCacheService.putManagerAuthData(ip, name);
			return true;
		}
		return false;
	}

	public boolean isLogin(HttpServletRequest request) {
		request.getSession().setMaxInactiveInterval(60 * 60);
		String ip = IPUtil.getIpAddress(request);
		return userCacheService.validateManagerAuthDataActive(ip);
	}

	public void logout(HttpServletRequest request) {
		String ip = IPUtil.getIpAddress(request);
		userCacheService.removeManagerAuthData(ip);
	}

	public String getManagerAuthName(HttpServletRequest request) {
		String ip = IPUtil.getIpAddress(request);
		return userCacheService.getManagerAuthName(ip);
	}

	public ModelMap getHomeFoundSelected(Long user_id, int pageIndex, int pageSize, Long dy_id) {
		ModelMap reMap = ResultUtil.getResultOKMap();
		if (pageIndex == 1) {
			int totalSize = managerDao.getHomeFoundSelectedCount(user_id, dy_id);
			reMap.put("pageCount", getPageCount(totalSize, pageSize));
		}
		List<UserDynamic> dys = managerDao.getHomeFoundSelected(user_id, pageIndex, pageSize, dy_id);
		ImagePathUtil.completeDynamicsPath(dys, true);
		reMap.put("data", dys);
		reMap.put("currentPageIndex", pageIndex);
		return reMap;
	}

	private int getPageCount(int totalSize, int pageSize) {
		int pageCount = totalSize / pageSize;
		if (totalSize % 10 > 0) {
			pageCount += 1;
		}
		if (pageCount == 0) {
			pageCount = 1;
		}
		return pageCount;
	}

	public ModelMap getUnSelected(Long user_id, Long dy_id, String nick_name, int pageIndex, int pageSize) {
		ModelMap reMap = ResultUtil.getResultOKMap();
		if (pageIndex == 1) {
			int total = managerDao.getUnSelectedCount(user_id, dy_id);
			reMap.put("pageCount", getPageCount(total, pageSize));
		}
		List<UserDynamic> dys = managerDao.getUnSelectedDynamic(user_id, dy_id, nick_name, pageIndex, pageSize);
		ImagePathUtil.completeDynamicsPath(dys, true);
		reMap.put("data", dys);
		reMap.put("currentPageIndex", pageIndex);
		return reMap;
	}

	public ModelMap getUnCheckDynamic(int page, int count) {
		ModelMap reMap = ResultUtil.getResultOKMap();
		if (page == 1) {
			int total = managerDao.getUnCheckedDynamicCount();
			reMap.put("pageCount", getPageCount(total, count));
		}
		List<UserDynamic> dys = managerDao.getUnCheckDynamic(page, count);
		ImagePathUtil.completeDynamicsPath(dys, true);
		reMap.put("data", dys);
		reMap.put("currentPageIndex", page);
		return reMap;
	}

	public ModelMap getIllegalDynamic(int page, int count) {
		ModelMap reMap = ResultUtil.getResultOKMap();
		if (page == 1) {
			int total = managerDao.getIllegalDynamicCount();
			reMap.put("pageCount", getPageCount(total, count));
		}
		List<UserDynamic> dys = managerDao.getIllegalDynamic(page, count);
		ImagePathUtil.completeDynamicsPath(dys, true);
		reMap.put("data", dys);
		reMap.put("currentPageIndex", page);
		return reMap;
	}

	public int removeFromSelected(long id) {
		return managerDao.removeFromSelected(id);
	}

	public int removeDyanmicByIdAndState(long id, DynamicStatus state) {
		return managerDao.removeDyanmicByIdAndState(id, state);
	}

	public int removeUserDynamic(long id) {
		return managerDao.removeUserDynamic(id);
	}

	public int addDyToSelected(long id) {
		return managerDao.addToSelected(id);
	}

	public boolean updateWelcome(String welcome) {
		return userCacheService.setWelcome(welcome);
	}

	public String getWelcome() {
		return userCacheService.getWelcome();
	}

	public void addTopic(Topic topic) {
		managerDao.insertObject(topic);
	}

	public List<Topic> loadTopic() {

		return managerDao.loadTopic();
	}

	public void delTopic(long id) {
		managerDao.delTopic(id);
	}

	/**
	 * 根据时间限制获取新增用户数量
	 * 
	 * @param type
	 * @return
	 */
	public int newUserCount(int type) {
		return managerDao.getNewUserCount(type);
	}

	/**
	 * 根据限制获取新增用户
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @param type
	 * @return
	 */
	public List<ManagerUser> listNewUser(int pageIndex, int pageSize, int type) {
		return managerDao.listNewUser(pageIndex, pageSize, type);
	}

	// 动态审核违规
	public int updateDynamicState(long id, DynamicStatus state) {
		return managerDao.updateDynamicState(id, state);
	}

	// 动态审核违规
	public int updateDynamicManagerFlag(long id, int flag) {
		return managerDao.updateDynamicManagerFlag(id, flag);
	}

	/**
	 * 获取所有用户
	 * 
	 * @param pageSize
	 * @param currentPage
	 * @return
	 */
	public ModelMap getAllUser(String keyword, int type, Long user_id, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		List<ManagerUser> users = managerDao.listAllUser(user_id, page, count, type, keyword);

		if (page == 1) {
			int totalSize = managerDao.getAllUserCount(user_id, type, keyword);

			r.put("pageCount", getPageCount(totalSize, count));
		}
		ImagePathUtil.completeManagerUserAvatarsPath(users, true);
		r.put("users", users);
		r.put("currentPageIndex", page);
		return r;
	}

	/**
	 * 获取发现黑名单用户
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public ModelMap getFoundUsers(Long user_id, int page, int count) {
		List<BaseUser> users = userService.getFoundUsers(user_id, page, count);
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = userService.getFoundUsersCount(user_id);
			int pageCount = totalSize / count;
			if (totalSize % 10 > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		r.put("users", users);
		r.put("currentPageIndex", page);
		return r;

	}

	public ModelMap getBlackUsers(String nick_name, String mobile, int page, int count) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<BaseUser> users = userService.getBlackUsers(nick_name, mobile, page, count);
		if (page == 1) {
			int totalSize = userService.getBlackUsersCount(nick_name, mobile);
			int pageCount = totalSize / count;
			if (totalSize % 10 > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			result.put("pageCount", pageCount);
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		result.put("users", users);
		result.put("currentPageIndex", page);
		return result;
	}

	public ModelMap getAllMeetBottleRecommendUser(int pageSize, int pageIndex, String keyword) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<BaseUser> users = userService.getAllMeetBottleRecommendUser(pageSize, pageIndex, keyword);

		if (pageIndex == 1) {
			int totalSize = userService.getMeetBottleRecommendUserSize(keyword);
			r.put("pageCount", getPageCount(totalSize, pageSize));
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		r.put("users", users);
		r.put("currentPageIndex", pageIndex);
		return r;

	}

	/**
	 * 添加到发现用户黑名单
	 * 
	 * @param user_id
	 */
	public void editUserFoundState(long user_id, UserFnStatus fn) {
		userService.setUserFoundFn(user_id, fn);
		if (fn == UserFnStatus.DEFAULT) {
			bottleService.clearPoolBottleByUserId(user_id);
		}
	}

	/**
	 * 添加到邂逅瓶待选用户区
	 * 
	 * @param user_id
	 */
	public void editUserMeetBottle(long user_id, UserFnStatus fun) {
		managerDao.editUserMeetBottle(user_id, fun);
		bottleService.removeMeetBottle(user_id);
	}

	/**
	 * 获取提现记录
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public ModelMap getExchangeHistory(int pageSize, int pageIndex, int type) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<Object> exchanges = managerDao.getExchangeHistory(pageSize, pageIndex, type);
		if (pageIndex == 1) {
			int totalSize = managerDao.getExchangeHistorySize(type);

			r.put("pageCount", getPageCount(totalSize, pageSize));
		}
		r.put("exchanges", exchanges);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	/**
	 * 处理提现申请
	 * 
	 * @param id
	 * @param agreeOrReject
	 * @return
	 */
	public boolean handleExchange(int id, boolean agreeOrReject) {
		if (agreeOrReject) {
			return managerDao.updateExchageState(id, ExchangeState.EXCHANGED) == 1;
		} else {
			mainService.backExchange(id);
			return managerDao.updateExchageState(id, ExchangeState.REJECT) == 1;
		}
	}

	public void deleteReport(int id) {
		mainService.deleteReport(id);
	}

	/**
	 * 获取提现记录
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public ModelMap getReports(int pageSize, int pageIndex) {

		ModelMap r = ResultUtil.getResultOKMap();
		List<Report> exchanges = mainService.listManagerReport(pageSize, pageIndex);
		if (pageIndex == 1) {
			int totalSize = mainService.getReportSizeByApproval();

			r.put("pageCount", getPageCount(totalSize, pageSize));
		}
		r.put("reports", exchanges);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	public ModelMap listBottleByState(Long user_id, String nick_name, int status, int type, int pageSize,
			int pageIndex) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<Bottle> exchanges = bottleService.getBottlesByState(user_id, nick_name, status, type, pageSize, pageIndex);
		for (Bottle b : exchanges) {
			if (b.getType() == BottleType.MEET.ordinal()) {
				b.setContent(getMeetUserAvatar(b.getContent()));
			} else if (b.getType() == BottleType.DRAW_GUESS.ordinal()) {
				ImagePathUtil.completeBottleDrawPath(b);
			}
		}
		if (pageIndex == 1) {
			int totalSize = bottleService.getBottleCountWithState(user_id, status, type);
			r.put("pageCount", getPageCount(totalSize, pageSize));
		}
		r.put("bottles", exchanges);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	public void changeBottleState(int id, int to_state) {
		bottleService.changeBottleState(id, to_state);
	}

	public ModelMap getSpecialUsers(int pageIndex, int pageSize) {
		return mainService.getSpecialUsers(pageIndex, pageSize);
	}

	public int getSpecialUsersCount() {
		return mainService.getSpecialUsersCount();
	}

	public int delSpecialUser(long uid) {
		return mainService.delSpecialUser(uid);
	}

	public int addSpreadUser(long uid) {
		return mainService.addSpreadUser(uid);
	}

	public void editAvatarState(int id, int state) {
		userService.editAvatarState(id, state);
	}

	public void editAvatarStateToIllegal(long uid) {
		userService.editAvatarStateToIllegal(uid);
	}

	public String getMeetUserAvatar(String content) {
		return userService.getUserAvatar(Long.parseLong(content)).get("thumb").toString();
	}

	public boolean checkWordsExist(String word) {
		BottleKeyWordUtil.checkWordsExist(word);
		return false;
	}

	public ModelMap listConfirmAvatars(int pageSize, int pageIndex, Long user_id, int state) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<BaseUser> users = userService.listConfirmAvatars(state, pageSize, pageIndex, user_id);
		ImagePathUtil.completeAvatarsPath(users, true);
		r.addAttribute("users", users);
		if (pageIndex == 1) {
			int totalSize = userService.getCountOfConfirmAvatars(user_id, state);
			r.put("pageCount", getPageCount(totalSize, pageSize));
		}
		r.put("currentPageIndex", pageIndex);
		return r;

	}

	public ModelMap listAvatarsByUid(int pageSize, int pageIndex, Long user_id, String nickName) {

		ModelMap r = ResultUtil.getResultOKMap();
		List<BaseUser> users = userService.listAvatarsByUid(pageSize, pageIndex, user_id, nickName);
		ImagePathUtil.completeAvatarsPath(users, true);
		r.addAttribute("users", users);
		if (pageIndex == 1) {
			int totalSize = userService.getCountOfUserAvatars(user_id, nickName);

			r.put("pageCount", getPageCount(totalSize, pageSize));
		}
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	public void charge_vip(long user_id, int month, String mark) {
		VipUser vip = new VipUser();
		vip.setUser_id(user_id);
		vip.setMark(mark);
		vip.setVip_id(vipService.getVipIdByMonth(month));
		vip.setAid("1178548652");
		vipService.chargeVip(vip, month);

	}

	public Object charge_coin(long user_id, int coin, String mark) {
		return userService.modifyUserExtra(user_id, "1178548652", coin, 1);
	}

	public void change_pwd(String name, String pwd) throws NoSuchAlgorithmException {
		managerDao.updateMPwd(name, MD5Util.getMd5(pwd));
	}

	// ----------------约会相关------------------------------------------------
	public ModelMap loadAppointMents(Long user_id, String nick_name, int status, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = appointmentService.getCheckCount(user_id, nick_name, status);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("currentPageIndex", page);

		List<Appointment> data = appointmentService.listToCheck(user_id, nick_name, status, page, count);
		ImagePathUtil.completePath(data);
		r.addAttribute("data", data);
		return r;
	}

	// ----------短视频相关--------------------------------------------------------------

	public ModelMap loadShortvideos(Long user_id, String nick_name, int status, int page, int count) {
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = userDynamicService.getVideoCountByStatus(user_id, nick_name, status);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("currentPageIndex", page);

		List<UserDynamic> data = userDynamicService.getVideoList(user_id, nick_name, status, page, count);
		r.addAttribute("data", data);
		return r;
	}

	public void changeShortvideoStatus(int id, int newStatus) {
		userDynamicService.changeVideoStatus(id, newStatus);
	}

	public ModelMap loadUserCertVideos(int status, int page, int count) {
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = videoService.getUserCertVideoCount(status);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("currentPageIndex", page);

		List<Video> data = videoService.getUserCertVideo(status, page, count);
		r.addAttribute("data", data);
		return r;
	}

	public ModelMap userShortvideoCert(int id, long uid, int isOK, int status, int page, int count) {
		videoService.changeStatus(id, isOK == 1 ? VideoStatus.CHECKED.ordinal() : VideoStatus.DEL.ordinal());
		userService.changeUserCertStatus(uid, isOK == 1 ? 1 : 0);
		return loadUserCertVideos(status, page, count);
	}
	// ----------动态评论相关--------------------------------------------------------------

	public ModelMap loadDynamicComment(Long user_id, String nick_name, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = userDynamicService.getDynamicCommentToCheckCount(user_id, nick_name);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("currentPageIndex", page);

		List<DynamicComment> data = userDynamicService.loadDynamicCommentToCheck(user_id, nick_name, page, count);
		r.addAttribute("data", data);
		return r;
	}

	public ModelMap change_dynamic_comment_status(Long user_id, String nick_name, int id, int page, int count,
			int toStatus) {
		userDynamicService.changeCommentStatus(id, toStatus);
		return loadDynamicComment(user_id, nick_name, page, count);
	}

	// ----------礼物清单-----------------------

	public ModelMap loadGiftHistoryList(Long user_id, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = giftService.getGiftHistoryCount(user_id);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("currentPageIndex", page);

		List<GiftOwn> data = giftService.getGifNoticeByManager(user_id, page, count);
		ImagePathUtil.completeGiftsOwnPath(data, true);
		r.addAttribute("data", data);
		return r;
	}

	// ----------签名更新相关-----------------------
	public ModelMap loadSignatureUpdateUsers(Long user_id, int page, int count) {
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = userService.getSignatureUpdateUsersCount(user_id);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("currentPageIndex", page);

		r.addAttribute("data", userService.loadSignatureUpdateUsers(user_id, page, count));
		return r;
	}

	public ModelMap loadNicknameUpdateUsers(Long user_id, int page, int count) {
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = userService.getNicknameUpdateUsersCount(user_id);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("currentPageIndex", page);

		r.addAttribute("data", userService.loadNicknameUpdateUsers(user_id, page, count));
		return r;
	}

	public ModelMap deleteUserSignature(long uid, Long user_id, int page, int count) {
		userService.deleteUserSignature(uid);
		return loadSignatureUpdateUsers(user_id, page, count);
	}

	public ModelMap deleteUserNickname(long uid, Long user_id, int page, int count) {
		userService.deleteUserNickname(uid);
		return loadNicknameUpdateUsers(user_id, page, count);
	}
	
	public ModelMap onlyUserNicknameIllegal(long uid) {
		String nick=userService.onlyNicknameIllegal(uid);
		return ResultUtil.getResultOKMap().addAttribute("nick", nick);
	}

	public void addUserToBlackByBottleID(int id) {
		userService.setUserSysStatusTo(bottleService.getBottleById(id).getUser_id(), SysUserStatus.BLACK);
	}

	public void deleteBottle(int id) {
		bottleService.delete(id);
	}

	public ModelMap listHXChatHistoryMsgs(String keywords, String type, int page, int count) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<HXHistoryMsg> msgs = hxService.list(type, keywords, page, count);
		for (HXHistoryMsg msg : msgs) {
			String fromVatar = ImagePathUtil.completeUserVatarPath(msg.getFrom_avatar());
			msg.setFrom_avatar(fromVatar);

			String toVatar = ImagePathUtil.completeUserVatarPath(msg.getTo_avatar());
			msg.setTo_avatar(toVatar);
		}
		if (page == 1) {
			int totalSize = hxService.getCount(type, keywords, page, count);
			r.put("pageCount", getPageCount(totalSize, count));
		}
		r.put("msgs", msgs);
		r.put("currentPageIndex", page);
		return r;
	}

	public ModelMap downloadMsgFile(String msg_id) {
		HXHistoryMsg msg = hxService.getHistoryMsgById(msg_id);
		Map<String, Object> map = JSONUtil.jsonToMap(msg.getContent());
		String secretKey = map.get("secret").toString();
		String remotePath = map.get("url").toString();

		String base64;
		try {
			base64 = HX_SessionUtil.downloadAudioFile(remotePath, secretKey);
			return ResultUtil.getResultOKMap().addAttribute("file", base64).addAttribute("type", msg.getType());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResultUtil.getResultFailed();
	}

	public void downloadMsgImg(HttpServletResponse response, String msg_id) throws IOException {
		HXHistoryMsg msg = hxService.getHistoryMsgById(msg_id);
		Map<String, Object> map = JSONUtil.jsonToMap(msg.getContent());
		String secretKey = map.get("secret").toString();
		String remotePath = map.get("url").toString();

		HX_SessionUtil.downloadImgFile(response, remotePath, secretKey);

	}

}
