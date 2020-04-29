package com.zhan.app.nearby.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

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
import com.zhan.app.nearby.comm.DynamicCommentStatus;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.comm.UserFnStatus;
import com.zhan.app.nearby.comm.VideoStatus;
import com.zhan.app.nearby.dao.ManagerDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.task.HXAsyncTask;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
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

	public ModelMap getHomeFoundSelected(Long user_id, int pageIndex, int pageSize) {
		ModelMap reMap = ResultUtil.getResultOKMap();
		if (pageIndex == 1) {
			int totalSize = managerDao.getHomeFoundSelectedCount(user_id);
			reMap.put("pageCount", getPageCount(totalSize, pageSize));
		}
		List<UserDynamic> dys = managerDao.getHomeFoundSelected(user_id, pageIndex, pageSize);
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

	public ModelMap getUnSelected(Long user_id, String nick_name, int pageIndex, int pageSize) {
		ModelMap reMap = ResultUtil.getResultOKMap();
		if (pageIndex == 1) {
			int total = managerDao.getUnSelectedCount(user_id, nick_name);
			reMap.put("pageCount", getPageCount(total, pageSize));
		}
		List<UserDynamic> dys = managerDao.getUnSelectedDynamic(user_id, nick_name, pageIndex, pageSize);
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

	public int removeDyanmicByIdAndState(long id, DynamicState state) {
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
	public int updateDynamicState(long id, DynamicState state) {
		return managerDao.updateDynamicState(id, state);
	}

	// 动态审核违规
	public int updateDynamicManagerFlag(long id,int flag) {
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

	public ModelMap getBlackUsers(Long user_id, int page, int count) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<BaseUser> users = userService.getBlackUsers(user_id, page, count);
		if (page == 1) {
			int totalSize = userService.getBlackUsersCount(user_id);
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

	public void handleReport(int id, boolean isIgnore) {
		mainService.handleReport(id, isIgnore);
	}

	/**
	 * 获取提现记录
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public ModelMap getReports(int approval_type, int pageSize, int pageIndex) {

		ModelMap r = ResultUtil.getResultOKMap();
		List<Report> exchanges = mainService.listManagerReport(approval_type, pageSize, pageIndex);
		if (pageIndex == 1) {
			int totalSize = mainService.getReportSizeByApproval(approval_type);

			r.put("pageCount", getPageCount(totalSize, pageSize));
		}
		r.put("reports", exchanges);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	public ModelMap listBottleByState(Long user_id, int state, int pageSize, int pageIndex) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<Bottle> exchanges = bottleService.getBottlesByState(user_id, state, pageSize, pageIndex);
		for (Bottle b : exchanges) {
			if (b.getType() == BottleType.MEET.ordinal()) {
				b.setContent(getMeetUserAvatar(b.getContent()));
			} else if (b.getType() == BottleType.DRAW_GUESS.ordinal()) {
				ImagePathUtil.completeBottleDrawPath(b);
			}
		}
		if (pageIndex == 1) {
			int totalSize = bottleService.getBottleCountWithState(user_id, state);
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
	public ModelMap loadAppointMents(int status, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = appointmentService.getCheckCount(status);
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", page);

		List<Appointment> data = appointmentService.listToCheck(status, page, count);
		ImagePathUtil.completePath(data);
		r.addAttribute("data", data);
		return r;
	}

	public ModelMap changeAppointMentsStatus(int id, int status, int page, int count, int newStatus) {

		appointmentService.changeStatus(id, newStatus);
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = appointmentService.getCheckCount(status);
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", page);

		List<Appointment> data = appointmentService.listToCheck(status, page, count);
		ImagePathUtil.completePath(data);
		r.addAttribute("data", data);
		return r;
	}

	// ----------短视频相关--------------------------------------------------------------

	public ModelMap loadShortvideos(int status, int page, int count, boolean isUserCert) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = videoService.getCountByStatus(status, isUserCert);
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", page);

		List<Video> data = videoService.loadByStatus(status, page, count, isUserCert);
		ImagePathUtil.completeVideosPath(data);
		r.addAttribute("data", data);
		return r;
	}

	public ModelMap changeShortvideoStatus(int id, int status, int page, int count, int newStatus) {

		videoService.changeStatus(id, newStatus);
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = videoService.getCountByStatus(status, false);
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", page);

		List<Video> data = videoService.loadByStatus(status, page, count, false);
		ImagePathUtil.completeVideosPath(data);
		r.addAttribute("data", data);
		return r;
	}

	public ModelMap userShortvideoCert(int id, long uid, int isOK, int status, int page, int count) {
		videoService.changeStatus(id, isOK == 1 ? VideoStatus.CHECKED.ordinal() : VideoStatus.DEL.ordinal());
		userService.changeUserCertStatus(uid, isOK == 1 ? 1 : 0);
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = videoService.getCountByStatus(status, true);
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", page);

		List<Video> data = videoService.loadByStatus(status, page, count, true);
		ImagePathUtil.completeVideosPath(data);
		r.addAttribute("data", data);
		return r;
	}
	// ----------动态评论相关--------------------------------------------------------------

	public ModelMap loadDynamicComment(Long user_id, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = userDynamicService.getDynamicCommentCount(user_id);
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", page);

		List<DynamicComment> data = userDynamicService.loadDynamicCommentToCheck(user_id, page, count);
		r.addAttribute("data", data);
		return r;
	}

	public ModelMap change_dynamic_comment_status(Long user_id, int id, int status, int page, int count) {
		userDynamicService.changeCommentStatus(id, status);
		return loadDynamicComment(user_id, page, count);
	}

	// ----------礼物清单-----------------------

	public ModelMap loadGiftHistoryList(Long user_id, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = giftService.getGiftHistoryCount(user_id);
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
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
			int pageCount = totalSize / count;
			if (totalSize % count > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", page);

		r.addAttribute("data", userService.loadSignatureUpdateUsers(user_id, page, count));
		return r;
	}

	public ModelMap deleteUserSignature(long uid, Long user_id, int page, int count) {
		userService.deleteUserSignature(uid);
		return loadSignatureUpdateUsers(user_id, page, count);
	}
}
