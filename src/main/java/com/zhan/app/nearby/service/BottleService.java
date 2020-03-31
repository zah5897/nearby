package com.zhan.app.nearby.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.BottleExpress;
import com.zhan.app.nearby.bean.RedPackageGetHistory;
import com.zhan.app.nearby.bean.Reward;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.MeetListUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.AccountStateType;
import com.zhan.app.nearby.comm.AndroidChannel;
import com.zhan.app.nearby.comm.BottleAnswerState;
import com.zhan.app.nearby.comm.BottleState;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.BottleDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.AppException;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.task.CommAsyncTask;
import com.zhan.app.nearby.task.HXAsyncTask;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.RandomCodeUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service

public class BottleService {
	private static Logger log = Logger.getLogger(BottleService.class);
	public static final int LIMIT_COUNT = 5;

	@Resource
	private BottleDao bottleDao;
	@Resource
	private VipDao vipDao;
	@Resource
	private UserDao userDao;
	@Resource
	private DynamicMsgService dynamicMsgService;

	@Resource
	private UserService userService;
	@Resource
	private MainService mainService;

	@Resource
	private UserCacheService userCacheService;

	@Autowired
	private HXAsyncTask hxTask;
	@Autowired
	private CommAsyncTask commAsyncTask;

	public Bottle getBottleFromPool(long user_id) {

		List<Bottle> bottles = bottleDao.getBottleRandomInPool(user_id, 1);
		if (bottles != null && bottles.size() > 0) {
			return bottles.get(0);
		}
		return null;
	}

	public Bottle getBottleById(long bid) {
		return bottleDao.getBottleById(bid);
	}

	public boolean checkTime(Bottle bottle) {
		long last_time = userCacheService.getLastBottleSendTime(bottle.getUser_id());
		long now = System.currentTimeMillis() / 1000;
		boolean r = now - last_time > 1;
		userCacheService.setLastBottleSendTime(bottle.getUser_id());
		return r;
	}

	public boolean isBlockUser(long user_id) {
		int state = userDao.getUserState(user_id);
		return state == AccountStateType.LOCK.ordinal();
	}

	public boolean isBlackUser(long user_id) {
		int state = userDao.getUserState(user_id);
		return state == FoundUserRelationship.GONE.ordinal();
	}

	/**
	 * 获取非弹幕瓶子
	 * 
	 * @param user_id
	 * @param page_size
	 * @param look_sex
	 * @param type
	 * @param state_val
	 * @return
	 */
	public ModelMap getBottles(long user_id, int page_size, Integer look_sex, Integer type, Integer state_val,
			String version, String _ua, String channel) {
		ModelMap result = ResultUtil.getResultOKMap();
		BottleState state = BottleState.NORMAL;
		if (state_val == null) {
			state = BottleState.NORMAL;
			state_val = 0;
		}
		if (state_val == 1) {
			state = BottleState.BLACK;
		} else if (state_val == 2) {
			state = BottleState.IOS_REVIEW;
		} else {
			state = BottleState.NORMAL;
		}
		List<Bottle> bolltes = null;
		int realType = type;

		if (_ua.startsWith("g")) {
			if (AndroidChannel.dev.name().equals(channel)) {
				if (look_sex == null) {
					bolltes = bottleDao.getBottlesLeastVersion(user_id, look_sex, page_size, realType);
				} else {
					VipUser vip = vipDao.loadUserVip(user_id);
					if (vip == null || vip.getDayDiff() < 0) {
						bolltes = bottleDao.getBottlesLeastVersion(user_id, null, page_size, realType);
					} else {
						int sex = (look_sex == null ? -1 : look_sex);
						bolltes = bottleDao.getBottlesLeastVersion(user_id, sex, page_size, realType);
					}
				}
			} else { // 其他渠道
				if (look_sex == null) {
					bolltes = bottleDao.getBottlesLeastVersion(user_id, look_sex, page_size, realType);
				} else {
					VipUser vip = vipDao.loadUserVip(user_id);
					if (vip == null || vip.getDayDiff() < 0) {
						bolltes = bottleDao.getBottlesLeastVersion(user_id, null, page_size, realType);
					} else {
						int sex = (look_sex == null ? -1 : look_sex);
						bolltes = bottleDao.getBottlesLeastVersion(user_id, sex, page_size, realType);
					}
				}
			}
		} else {// ios 手机
			if (state != BottleState.IOS_REVIEW) {
				if (look_sex == null) {
					bolltes = bottleDao.getBottlesLeastVersion(user_id, null, page_size, realType);
				} else {
					VipUser vip = vipDao.loadUserVip(user_id);
					if (vip == null || vip.getDayDiff() < 0) {
						bolltes = bottleDao.getBottlesLeastVersion(user_id, null, page_size, realType);
					} else {
						int sex = (look_sex == null ? -1 : look_sex);
						bolltes = bottleDao.getBottlesLeastVersion(user_id, sex, page_size, realType);
					}
				}
			} else { // 审核期间的瓶子
				if (look_sex == null) {
					bolltes = bottleDao.getBottlesIOS_REVIEW(user_id, null, page_size, realType);
				} else {
					VipUser vip = vipDao.loadUserVip(user_id);
					if (vip == null || vip.getDayDiff() < 0) {
						bolltes = bottleDao.getBottlesIOS_REVIEW(user_id, null, page_size, realType);
					} else {
						int sex = (look_sex == null ? -1 : look_sex);
						bolltes = bottleDao.getBottlesIOS_REVIEW(user_id, sex, page_size, realType);
					}
				}
			}
		}
		result.addAttribute("bottles", bolltes);
		return result;
	}

	/**
	 * 获取弹幕瓶子
	 * 
	 * @param user_id
	 * @param page_size
	 * @param look_sex
	 * @param type
	 * @param state_val
	 * @return
	 */
	public ModelMap getDMBottles(long user_id, int page_size, Integer type, Integer state_val) {
		ModelMap result = ResultUtil.getResultOKMap();
		BottleState state = BottleState.NORMAL;

		if (state_val == null) {
			state = BottleState.NORMAL;
			state_val = 0;
		}
		if (state_val == 1) {
			state = BottleState.BLACK;
		} else if (state_val == 2) {
			state = BottleState.IOS_REVIEW;
		} else {
			state = BottleState.NORMAL;
		}
		List<Bottle> bottles = null;
		int realType = type == null ? -1 : type;

		int timeType = 0;
		bottles = bottleDao.getLatestDMBottles(user_id, page_size, realType, state, timeType);

		if (bottles == null || bottles.size() == 0) {
			timeType = 1;
			bottles = bottleDao.getLatestDMBottles(user_id, page_size, realType, state, timeType);
		}

		for (Bottle b : bottles) {
			bottleDao.markDMBottleHadGet(user_id, b.getId());
		}

		result.addAttribute("bottles", bottles);
		return result;
	}

	public boolean existBottles(String content, String img) {
		return bottleDao.existBottles(content, img);
	}

	public List<Bottle> getBottlesFromPool(long user_id) {
		return bottleDao.getBottleRandomInPool(user_id, LIMIT_COUNT);
	}

	@Transactional
	public void send(Bottle bottle, String aid) {

		if (bottle.getType() == BottleType.DM_TXT.ordinal() || bottle.getType() == BottleType.DM_VOICE.ordinal()) {
			Map<String, Object> extraData = userService.modifyUserExtra(bottle.getUser_id(), aid, 1, -1);
			if (extraData != null && extraData.containsKey("all_coins")) {
				int all_coins = (int) extraData.get("all_coins");
				if (all_coins < 0) {
					throw new AppException(ERROR.ERR_COINS_SHORT);
				}
			}
		}
		if (bottle.getType() == BottleType.TXT.ordinal() || bottle.getType() == BottleType.DM_TXT.ordinal()) {
			// 敏感词过滤
			String newContent = BottleKeyWordUtil.filterContent(bottle.getContent());
			if (!newContent.equals(bottle.getContent())) {
				bottle.setState(BottleState.BLACK.ordinal());
			}
			bottle.setContent(newContent);
		}
		// 从池中清理掉重复的瓶子
		checkExistAndClear(bottle);
		bottle.setCreate_time(new Date());

		bottleDao.insert(bottle);

		if (bottle.getId() > 0 && bottle.getState() != BottleState.BLACK.ordinal()) {
			bottleDao.insertToPool(bottle);
		}
	}

	@Transactional
	public void sendMeetBottle(long user_id) {
		Bottle bottle = new Bottle();
		bottle.setCreate_time(new Date());
		bottle.setUser_id(user_id);
		bottle.setType(BottleType.MEET.ordinal());
		bottle.setContent(String.valueOf(user_id));
		send(bottle, null);
	}

	private void checkExistAndClear(Bottle bottle) {
		if (bottle.getType() == BottleType.TXT.ordinal()) {
			List<Long> existIds = bottleDao.getExistTxtBottle(bottle.getUser_id(), bottle.getContent());
			for (Long id : existIds) {
				bottleDao.deleteFromPool(id);
			}
		}
	}

	public Bottle getBottleDetial(long bottle_id) {
		return bottleDao.getBottleById(bottle_id);
	}

	public ModelMap getMineBottles(long user_id, int page, int page_size) {
		List<Bottle> bottles = bottleDao.getMineBottles(user_id, page, page_size);
		if (bottles != null) {
			for (Bottle bottle : bottles) {
				List<BaseUser> users = bottleDao.getScanUserList(bottle.getId(), 8);
				bottle.setView_nums(bottleDao.getScanUserCount(bottle.getId()));
				ImagePathUtil.completeAvatarsPath(users, false);
				ImagePathUtil.completeBottleDrawPath(bottle);
				bottle.setScan_user_list(users);
			}
		}
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottles", bottles);
		result.put("hasMore", bottles.size() == page_size);
		return result;
	}

	@Transactional
	public ModelMap delete(long user_id, long bottle_id) {
		int result = bottleDao.delete(user_id, bottle_id);
		if (result > 0) {
			Bottle bottle = new Bottle();
			bottle.setId(bottle_id);
			return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}

	public ModelMap scan(long user_id, String bottle_id, int percent) {
		if (!TextUtils.isEmpty(bottle_id)) {
			String[] bottle_ids = bottle_id.split(",");
			for (String id : bottle_ids) {
				try {
					long bid = Long.parseLong(id);
					bottleDao.logScan(user_id, bid);
					Long uid = bottleDao.getBottleSenderId(bid);

					BaseUser u1 = userDao.getBaseUserNoToken(user_id);
					BaseUser u2 = userDao.getBaseUserNoToken(uid);

					if (u1.getSex().equals(u2.getSex())) { // 同性的话
						continue;
					}
					// 概率
					if (RandomCodeUtil.randomPercentOK(percent)) {
						hxTask.replayBottle(u1, u2, bottleDao.getBottleById(bid));
					}
				} catch (Exception e) {
				}
			}
		}
		return ResultUtil.getResultOKMap().addAttribute("ids", bottle_id);
	}

	public boolean checkExistMeetBottleAndReUse(long user_id) {
		return bottleDao.checkExistMeetBottleAndReUse(user_id);
	}

	/**
	 * 表白邂逅瓶
	 * 
	 * @param user_id
	 * @param to_user_id
	 * @param bottle_id
	 * @param content
	 * @return
	 */
	@Transactional
	public ModelMap express(long user_id, long to_user_id, String content) {
		BottleExpress express = new BottleExpress();
		express.setUid(user_id);
		express.setTo_uid(to_user_id);
		express.setContent(content);
		express.setCreate_time(new Date());
		bottleDao.insertObject(express);

		// dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_EXPRESS, user_id, -1,
		// to_user_id, content);
		BaseUser user = userDao.getBaseUser(user_id);
		BaseUser to_user = userDao.getBaseUser(to_user_id);
		hxTask.createExpressSession(user, to_user, content);
		return ResultUtil.getResultOKMap();
	}

	@Transactional
	public ModelMap like(long user_id, String with_user_id) {
		List<Long> user_ids = new ArrayList<Long>();
		if (!TextUtils.isEmpty(with_user_id)) {
			@SuppressWarnings("rawtypes")
			List<Map> idList = JSONUtil.jsonToList(with_user_id, new TypeReference<List<Map>>() {
			});
			for (Map<?, ?> u_b : idList) {
				long withUserID = Long.parseLong(u_b.get("uid").toString());
				long bottleID = Long.parseLong(u_b.get("bottle_id").toString());

				// 判断瓶子是否存在，不存在的话要新建
				if (bottleID < 1) {
					List<Long> ids = bottleDao.getMeetBottleIDByUser(withUserID);
					if (ids.size() > 0) {
						bottleID = ids.get(0);
					} else {
						Bottle bottle = new Bottle();
						bottle.setCreate_time(new Date());
						bottle.setUser_id(withUserID);
						bottle.setType(BottleType.MEET.ordinal());
						bottle.setContent(String.valueOf(withUserID));
						bottleID = bottleDao.insert(bottle);
					}
				}
				userDao.updateRelationship(user_id, withUserID, Relationship.LIKE);
				if (bottleID > 0) {
					dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_MEET, user_id, bottleID, withUserID, "");
					hxTask.pushLike(withUserID);
				}

				// BaseUser u = userDao.getBaseUser(user_id);
				// BaseUser withUser = userDao.getBaseUser(withUserID);
				// makeChatSession(u, withUser, bottleID);
				user_ids.add(withUserID);
				// replay(user_id,withUserID,"有人喜欢了你",bottleID);
			}
		}
		return ResultUtil.getResultOKMap().addAttribute("with_user_id", user_ids);
	}

	@Transactional
	public ModelMap replay(String aid, long user_id, long target, String msg, long bottle_id) {
		BaseUser user = userDao.getBaseUser(user_id);
		BaseUser targetU = userDao.getBaseUser(target);

		Bottle bottle = bottleDao.getBottleById(bottle_id);
		if (bottle == null) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST);
		}
		// 发送给对方
		if (bottle.getType() != BottleType.RED_PACKAGE.ordinal()) {
			hxTask.replayBottleSingle(user, targetU, bottle, msg);
		}

		if (bottle.getType() == BottleType.DRAW_GUESS.ordinal()) {

			if (!TextUtils.isEmpty(msg) && msg.trim().equals(bottle.getAnswer())) {
				// 更新状态，并奖励
				if (bottle.getAnswer_state() == BottleAnswerState.NORMAL.ordinal()) {

					updateAnswerState(bottle_id, BottleAnswerState.ANSWERED);
					userService.rewardCoin(user_id, bottle.getReward(), aid);
					saveRewardHistory(bottle, user_id);
					return ResultUtil.getResultOKMap().addAttribute("result", 1);
				} else {
					return ResultUtil.getResultOKMap().addAttribute("result", 2);
				}
			} else {
				return ResultUtil.getResultOKMap().addAttribute("result", 0);
			}
		} else if (bottle.getType() == BottleType.RED_PACKAGE.ordinal()) {
			int count = bottle.getRed_package_count();
			int restCoin = bottle.getRed_package_coin_rest();
			if (count > 0 && restCoin > 0) {
				String content = bottle.getAnswer();
//				String curCount = content.substring(0, content.indexOf(","));
//				String restCount = content.substring(content.indexOf(",") + 1);
//				int coinCount = Integer.parseInt(curCount);
				int restCount; // 剩余红包金币数量
				int curCoin; // 当次得到的金币数量
				if (count == 1) {
					restCount = 0;
					curCoin = restCoin;
					count = 0;
					content = "";
				} else {
					curCoin = Integer.parseInt(content.substring(0, content.indexOf(",")));
					count -= 1;
					restCount = restCoin - curCoin;
					content = content.substring(content.indexOf(",") + 1);
				}

				Map<String, Object> map = userService.modifyUserExtra(user_id, aid, curCoin, 1); // 增加金币
				int code = Integer.parseInt(map.get("code").toString());
				if (code == 0) {
					bottleDao.updateRedPackage(content, count, restCount, bottle_id);// 修改该瓶子的剩余数量等信息
					addGetRedPackageHistory(user_id, bottle_id, curCoin); // 记录历史
					hxTask.replayRedPackageBottleSingle(user, target, getBottleById(bottle_id), curCoin);
					return ResultUtil.getResultOKMap().addAttribute("red_package_get_coin", curCoin);
				} else {
					return ResultUtil.getResultOKMap().addAttribute("red_package_get_coin", 0);
				}
			} else {
				return ResultUtil.getResultOKMap().addAttribute("red_package_get_coin", "-1");
			}
		}
		return ResultUtil.getResultOKMap();
	}

	@Transactional
	public int sendAutoBottle(String id, String content) {
		boolean hasSend = bottleDao.hasSend(id) > 0;
		if (!hasSend) {
			long uid = bottleDao.getRandomUidToSendAutoBottle();
			if (uid > 0) {
				bottleDao.insertAutoSendTextBottle(id);
				Bottle bottle = new Bottle();
				bottle.setCreate_time(new Date());
				bottle.setUser_id(uid);
				bottle.setType(BottleType.TXT.ordinal());
				bottle.setContent(content);
				send(bottle, null);
			}
			return 1;
		}
		return 0;
	}

	public List<Bottle> getBottlesByState(int state, int pageSize, int pageIndex, long bottle_id) {
		return bottleDao.getBottlesByState(state, pageSize, pageIndex, bottle_id);
	}

	public int getBottleCountWithState(int state) {
		return bottleDao.getBottleCountWithState(state);
	}

	public void changeBottleState(int id, int to_state) {
		bottleDao.changeBottleState(id, to_state);
	}

	public ModelMap meetList(long user_id, Integer page, Integer count) {

		if (page == null || page < 1) {
			page = 1;
		}

		if (count == null || count <= 0) {
			count = 10;
		}

		ModelMap r = ResultUtil.getResultOKMap();
		List<MeetListUser> meetList = bottleDao.getMeetList(user_id, page, count);
		r.addAttribute("users", meetList);
		ImagePathUtil.completeAvatarsPath(meetList, true);

		if (!meetList.isEmpty()) {
			r.addAttribute("last_one", meetList.get(0));
		}
		r.addAttribute("has_more", meetList.size() >= count);
		return r;
	}

	public ModelMap replay_meet(long user_id, long target) {
		dynamicMsgService.updateMeetState(user_id, target);
		BaseUser u1 = userDao.getBaseUserNoToken(user_id);
		BaseUser u2 = userDao.getBaseUserNoToken(target);
		Bottle meet_bottle = bottleDao.getMeetBottleByUserId(target);
		if (meet_bottle != null) {
			hxTask.replayBottle(u1, u2, meet_bottle);
		}
		return ResultUtil.getResultOKMap().addAttribute("user", u2);
	}

	@Transactional
	public void clearIllegalMeetBottle(long uid) {
		bottleDao.clearIllegalMeetBottle(uid);
		userDao.removeMeetBottleUserByUserId(uid);
	}

	@Transactional
	public int clearPoolBottleByUserId(long uid) {
		return bottleDao.clearPoolBottleByUserId(uid);
	}

	@Transactional
	public void clearUserBottle(long uid) {
		clearPoolBottleByUserId(uid);
		bottleDao.clearBottleByUserId(uid);
	}

	public List<String> loadAnswerToDraw(Integer count) {
		return bottleDao.loadAnswerToDraw(count == null || count < 1 ? 4 : count);
	}

	@Transactional
	public void updateAnswerState(long bottle_id, BottleAnswerState state) {
		bottleDao.updateAnswerState(bottle_id, state.ordinal());
	}

	@Transactional
	public void saveRewardHistory(Bottle b, long uid) {
		Reward reward = new Reward();
		reward.setBottle_id(b.getId());
		reward.setAnswer(b.getAnswer());
		reward.setCreate_time(new Date());
		reward.setUid(uid);
		reward.setReward(b.getReward());
		bottleDao.insertObject(reward);
	}

	@Transactional
	public List<Reward> rewardHistoryGroup(long user_id) {
		return bottleDao.rewardHistoryGroup(user_id);
	}

	public List<Reward> rewardHistory(long user_id, int page, int count) {
		return bottleDao.rewardHistory(user_id, page, count);
	}

	@Transactional
	public void refreshPool() {
		int limit = 100;
		for (BottleType type : BottleType.values()) {
			if (type == BottleType.TXT) {
				limit = 200;
			} else if (type == BottleType.DRAW_GUESS) {
				limit = 20;
			} else if (type == BottleType.MEET) {
				limit = 300;
			} else if (type == BottleType.VOICE) {
				bottleDao.keepVoiceByDay(3);
				continue;
			} else if (type == BottleType.RED_PACKAGE) {
				bottleDao.keepRedPackageByDay(7);
				continue;
			}
			int size = bottleDao.getSizeByType(type.ordinal());
			if (size < limit) {
				continue;
			}
			long id = bottleDao.getLimitId(type.ordinal(), limit);
			bottleDao.removePoolBottleKeepSize(type.ordinal(), id);
		}
	}

	@Transactional
	public void removeMeetBottle(long user_id) {
		bottleDao.removeMeetBottle(user_id);
	}

	private void addGetRedPackageHistory(long uid, long bid, int coin) {
		bottleDao.saveRedPackageHistory(uid, bid, coin);
	}

	public List<RedPackageGetHistory> getRedPackageHistory(long bid) {
		return bottleDao.getRedPackageHistoryByBid(bid);
	}
}
