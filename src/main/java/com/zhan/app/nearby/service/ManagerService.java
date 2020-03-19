package com.zhan.app.nearby.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.dao.ManagerDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.exception.ERROR;
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
	private HXAsyncTask hxTask;

	public int getHomeFoundSelectedCount() {
		return managerDao.getHomeFoundSelectedCount();
	}

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

	public int getPageCountByState(int state) {
		return userDynamicService.getPageCountByState(state);
	}

	public List<UserDynamic> getHomeFoundSelected(int pageIndex, int pageSize) {
		return managerDao.getHomeFoundSelected(pageIndex, pageSize);
	}

	// 根据状态获取动态
	public List<UserDynamic> getDyanmicByState(int pageIndex, int pageSize, DynamicState state) {
		return userDynamicService.getDyanmicByState(pageIndex, pageSize, state);
	}

	public int getUnSelectedCount(String nick_name) {
		return managerDao.getUnSelectedCount(nick_name);
	}

	public List<UserDynamic> getUnSelected(int pageIndex, int pageSize) {
		return managerDao.getUnSelected(pageIndex, pageSize, null);
	}

	public List<UserDynamic> getUnSelected(int pageIndex, int pageSize, String nick_name) {
		return managerDao.getUnSelected(pageIndex, pageSize, nick_name);
	}

	public int removeFromSelected(long id) {
		return managerDao.removeFromSelected(id);
	}

	public int removeDyanmicByState(long id, DynamicState state) {
		return managerDao.removeDyanmicByState(id, state);
	}

	public int removeUserDynamic(long id) {
		return managerDao.removeUserDynamic(id);
	}

	public int addToSelected(long id) {
		return managerDao.addToSelected(id);
	}

	public int ignore(long id) {
		return managerDao.ignore(id);
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

	public int editUserFromFound(long uid, int state) {
		return managerDao.setUserFoundRelationshipState(uid, FoundUserRelationship.values()[state]);
	}

	// 动态审核违规
	public int updateDynamicState(long id, DynamicState state) {
		return managerDao.updateDynamicState(id, state);
	}

	/**
	 * 获取所有用户
	 * 
	 * @param pageSize
	 * @param currentPage
	 * @return
	 */
	public List<BaseUser> getAllUser(int pageSize, int currentPage, int type, String keyword, Long user_id) {
		return userService.getAllUser(pageSize, currentPage, type, keyword, user_id);
	}

	/**
	 * 获取用户总数
	 * 
	 * @return
	 */
	public int getUserSize(int type, String keyword, Long user_id) {
		return userService.getUserSize(type, keyword, user_id);
	}

	/**
	 * 获取发现黑名单用户
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<BaseUser> getFoundUsersByState(int pageSize, int pageIndex, FoundUserRelationship ship) {
		return userService.getFoundUsersByState(pageSize, pageIndex, ship);
	}

	/**
	 * 获取黑名单总数
	 * 
	 * @return
	 */
	public int getFoundUserCountByState(FoundUserRelationship ship) {
		return userService.getFoundUsersCountByState(ship);
	}

	public List<BaseUser> getAllMeetBottleRecommendUser(int pageSize, int pageIndex, String keyword) {
		return userService.getAllMeetBottleRecommendUser(pageSize, pageIndex, keyword);
	}

	public int getMeetBottleRecommendUserSize(String keyword) {
		return userService.getMeetBottleRecommendUserSize(keyword);
	}

	/**
	 * 添加到发现用户黑名单
	 * 
	 * @param user_id
	 */
	public void editUserFoundState(long user_id, FoundUserRelationship ship) {
		managerDao.editUserFoundState(user_id, ship);
		if (ship == FoundUserRelationship.GONE) {
			bottleService.clearPoolBottleByUserId(user_id);
			userDynamicService.updateCommentStatus(user_id, ship);
			hxTask.disconnect(String.valueOf(user_id));
		}
	}

	/**
	 * 移除状态
	 * 
	 * @param user_id
	 */
	public void removeUserFoundState(long user_id) {
		managerDao.removeUserFoundState(user_id);
	}

	/**
	 * 添加到邂逅瓶待选用户区
	 * 
	 * @param user_id
	 */
	public void editUserMeetBottle(long user_id, int fun, String ip, String by) {
		managerDao.editUserMeetBottle(user_id, fun, ip, by);
		bottleService.removeMeetBottle(user_id);
	}

	/**
	 * 获取提现记录
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<Object> getExchangeHistory(int pageSize, int pageIndex, int type) {
		return managerDao.getExchangeHistory(pageSize, pageIndex, type);
	}

	/**
	 * 获取提现总记录数
	 * 
	 * @return
	 */
	public int getExchangeHistorySize(int type) {
		return managerDao.getExchangeHistorySize(type);
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
	public List<Report> getReports(int approval_type, int pageSize, int pageIndex) {
		return mainService.listManagerReport(approval_type, pageSize, pageIndex);
	}

	public int getReportSize(int approval_type) {
		return mainService.getReportSizeByApproval(approval_type);
	}

	public List<Bottle> listBottleByState(int state, int pageSize, int pageIndex, Long bottle_id) {
		long realId;
		if (bottle_id == null || bottle_id < 1) {
			realId = 0;
		} else {
			realId = bottle_id;
		}
		return bottleService.getBottlesByState(state, pageSize, pageIndex, realId);
	}

	public int getBottleCountWithState(int state, Long bottle_id) {
		if (bottle_id != null && bottle_id > 0) {
			return 1;
		}
		return bottleService.getBottleCountWithState(state);
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

	public void editAvatarStateByUserId(long uid) {
		userService.editAvatarStateByUserId(uid);
	}

	public String getMeetUserAvatar(String content) {
		return userService.getUserAvatar(Long.parseLong(content)).get("thumb").toString();
	}

	public boolean checkWordsExist(String word) {
		BottleKeyWordUtil.checkWordsExist(word);
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<BaseUser> listConfirmAvatars(int pageSize, int pageIndex, Long user_id, int state) {
		return (List<BaseUser>) ImagePathUtil
				.completeAvatarsPath(userService.listConfirmAvatars(state, pageSize, pageIndex, user_id), false); // state=0为变动，1为
	}

	@SuppressWarnings("unchecked")
	public List<BaseUser> listAvatarsByUid(int pageSize, int pageIndex, Long user_id, String nickName) {
		return (List<BaseUser>) ImagePathUtil
				.completeAvatarsPath(userService.listAvatarsByUid(pageSize, pageIndex, user_id, nickName), false); // state=0为变动，1为
	}

	public int getCountOfConfirmAvatars(Long user_id, int state) {
		return userService.getCountOfConfirmAvatars(user_id, state);
	}

	public int getCountOfUserAvatars(Long user_id, String nickName) {
		return userService.getCountOfUserAvatars(user_id, nickName);
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

	//----------------约会相关------------------------------------------------
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

		
		
		List<Appointment> data=appointmentService.listToCheck(status, page, count);
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

		List<Appointment> data=appointmentService.listToCheck(status, page, count);
		ImagePathUtil.completePath(data);
		r.addAttribute("data", data);
		return r;
	}

	
	//----------短视频相关--------------------------------------------------------------
	
	
	public ModelMap loadShortvideos(int status, int page, int count) {

		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = videoService.getCountByStatus(status);
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
		
		List<Video> data=videoService.loadByStatus(status, page, count);
		ImagePathUtil.completeVideosPath(data);
		r.addAttribute("data", data);
		return r;
	}

	public ModelMap changeShortvideoStatus(int id, int status, int page, int count, int newStatus) {

		videoService.changeStatus(id, newStatus);
		ModelMap r = ResultUtil.getResultOKMap();
		if (page == 1) {
			int totalSize = videoService.getCountByStatus(status);
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

		List<Video> data=videoService.loadByStatus(status, page, count);
		ImagePathUtil.completeVideosPath(data);
		r.addAttribute("data", data);
		return r;
	}
	
}
