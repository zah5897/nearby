package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.dao.ManagerDao;
import com.zhan.app.nearby.dao.UserDao;

@Service
@Transactional("transactionManager")
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

	public int getHomeFoundSelectedCount() {
		return managerDao.getHomeFoundSelectedCount();
	}

	public int getPageCountByState(int state) {
		return managerDao.getPageCountByState(state);
	}

	public List<UserDynamic> getHomeFoundSelected(int pageIndex, int pageSize) {
		return managerDao.getHomeFoundSelected(pageIndex, pageSize);
	}

	// 根据状态获取动态
	public List<UserDynamic> getDyanmicByState(int pageIndex, int pageSize, DynamicState state) {
		return managerDao.getDyanmicByState(pageIndex, pageSize, state);
	}

	public int getUnSelectedCount() {
		return managerDao.getUnSelectedCount();
	}

	public List<UserDynamic> getUnSelected(int pageIndex, int pageSize) {
		return managerDao.getUnSelected(pageIndex, pageSize);
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

	public long addTopic(Topic topic) {
		long id = managerDao.insertTopic(topic);
		topic.setId(id);
		return id;
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

	public void sendMsgToAll(final String msg, final String type) {
		new java.lang.Thread() {
			public void run() {
				int page_size = 20;
				long last_id = 0;
				while (true) {
					List<Long> ids = userService.getAllUserIds(last_id, page_size);
					if (ids == null) {
						return;
					}
					if (ids.size() == page_size) {
						String[] hxIds = new String[page_size];
						for (int i = 0; i < page_size; i++) {
							hxIds[i] = ids.get(i).toString();
						}
						last_id = ids.get(page_size - 1);
						Main.sendTxtMessage(Main.SYS, hxIds, msg, null, type);
					} else if (ids.size() > 0 && ids.size() < page_size) {
						String[] hxIds = new String[ids.size()];
						for (int i = 0; i < hxIds.length; i++) {
							hxIds[i] = ids.get(i).toString();
						}
						Main.sendTxtMessage(Main.SYS, hxIds, msg, null, type);
						break;
					} else {
						break;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
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
	public List<BaseUser> getAllUser(int pageSize, int currentPage, int type, String keyword,Long user_id) {
		return userService.getAllUser(pageSize, currentPage, type, keyword,user_id);
	}

	/**
	 * 获取用户总数
	 * 
	 * @return
	 */
	public int getUserSize(int type, String keyword,Long user_id) {
		return userService.getUserSize(type, keyword,user_id);
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
	public void editUserMeetBottle(long user_id, int fun) {
		managerDao.editUserMeetBottle(user_id, fun);
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

	public void handleReport(int id,boolean isIgnore) {
		mainService.handleReport(id,isIgnore);
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

	public List<Bottle> listBottleByState(int state, int pageSize, int pageIndex,Long bottle_id) {
		long realId;
		if(bottle_id==null||bottle_id<1) {
			realId=0;
		}else {
			realId=bottle_id;
		}
		return bottleService.getBottlesByState(state, pageSize, pageIndex,realId);
	}
	
	public int getBottleCountWithState(int state,Long bottle_id) {
		if(bottle_id!=null&&bottle_id>0) {
			return 1;
		}
		return bottleService.getBottleCountWithState(state);
	}

	public void changeBottleState(int id, int to_state) {
		bottleService.changeBottleState(id,to_state);
	}

	public ModelMap getSpecialUsers(int pageIndex,int pageSize) {
		return mainService.getSpecialUsers(pageIndex,pageSize);
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
}
