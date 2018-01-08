package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.ExchangeState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.dao.ManagerDao;

@Service
@Transactional("transactionManager")
public class ManagerService {
	@Resource
	private ManagerDao managerDao;
	@Resource
	private UserCacheService userCacheService;
	@Resource
	private UserService userService;

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

	public void sendMsgToAll(final String msg) {
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
						Main.sendTxtMessage(Main.SYS, hxIds, msg, null);
					} else if (ids.size() > 0 && ids.size() < page_size) {
						String[] hxIds = new String[ids.size()];
						for (int i = 0; i < hxIds.length; i++) {
							hxIds[i] = ids.get(i).toString();
						}
						Main.sendTxtMessage(Main.SYS, hxIds, msg, null);
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
	public List<BaseUser> getAllUser(int pageSize, int currentPage, int type, String keyword) {
		return userService.getAllUser(pageSize, currentPage, type, keyword);
	}

	/**
	 * 获取用户总数
	 * 
	 * @return
	 */
	public int getUserSize(int type, String keyword) {
		return userService.getUserSize(type, keyword);
	}

	/**
	 * 获取发现黑名单用户
	 * 
	 * @param pageSize
	 * @param pageIndex
	 * @return
	 */
	public List<BaseUser> getFoundBlackUsers(int pageSize, int pageIndex) {
		return userService.getFoundBlackUsers(pageSize, pageIndex);
	}

	/**
	 * 获取黑名单总数
	 * 
	 * @return
	 */
	public int getFoundBlackUsers() {
		return userService.getFoundBlackUsers();
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
	public void editUserFoundBlack(long user_id, int fun) {
		managerDao.editUserFoundBlack(user_id, fun);
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
			return managerDao.updateExchageState(id, ExchangeState.AGREE_WAIT) == 1;
		} else {
			return managerDao.updateExchageState(id, ExchangeState.REJECT) == 1;
		}
	}

}
