package com.zhan.app.nearby.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.fasterxml.jackson.core.type.TypeReference;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.BottleExpress;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.bean.user.MeetListUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.AccountStateType;
import com.zhan.app.nearby.comm.BottleState;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.BottleDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.AppException;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Service
@Transactional("transactionManager")
public class BottleService {

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

	public Bottle getBottleFromPool(long user_id) {

		List<Bottle> bottles = bottleDao.getBottleRandomInPool(user_id, 1);
		if (bottles != null && bottles.size() > 0) {
			return bottles.get(0);
		}
		return null;
	}

	public boolean checkTime(Bottle bottle) {
		long last_time = userCacheService.getLastBottleSendTime(bottle.getUser_id());
		long now = System.currentTimeMillis() / 1000;
		boolean r = now - last_time > 10;
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
	public ModelMap getBottles(long user_id, int page_size, Integer look_sex, Integer type, Integer state_val) {
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

		int realType = type == null ? -1 : type;

		if (look_sex == null) {
			bolltes = bottleDao.getBottles(user_id, page_size, realType, state);
		} else {
			VipUser vip = vipDao.loadUserVip(user_id);
			if (vip == null || vip.getDayDiff() < 0) {
				bolltes = bottleDao.getBottles(user_id, page_size, realType, state);
			} else {
				int sex = (look_sex == null ? -1 : look_sex);
				bolltes = bottleDao.getBottlesByGender(user_id, page_size, sex, realType, state);
			}
		}

		for (Bottle bottle : bolltes) {
			if (bottle.getType() == BottleType.MEET.ordinal()) {
				ImagePathUtil.completeAvatarPath(bottle.getSender(), true);
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

	public void send(Bottle bottle, String aid) {

		
		
		
		
		if (bottle.getType() == BottleType.DM_TXT.ordinal() || bottle.getType() == BottleType.DM_VOICE.ordinal()) {
			Map<String, Object> extraData = userService.modifyExtra(bottle.getUser_id(), aid, 1, -1);
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
			bottle.setContent(newContent);
		}
		//从池中清理掉重复的瓶子
		checkExistAndClear(bottle);
		
		bottle.setCreate_time(new Date());
		bottleDao.insert(bottle);
		if (bottle.getId() > 0) {
			bottleDao.insertToPool(bottle);
		}
	}
	
	private void checkExistAndClear(Bottle bottle) {
        if(bottle.getType() == BottleType.TXT.ordinal()) {
        	List<Long> existIds=bottleDao.getExistTxtBottle(bottle.getUser_id(),bottle.getContent());
        	for(Long id:existIds) {
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
				if (users == null || users.size() < 8) {
					String gender = userService.getUserGenderByID(user_id);
					int gen;
					if ("0".equals(gender)) {
						gen = 1;
					} else {
						gen = 0;
					}
					List<BaseUser> last_user = bottleDao.getRandomScanUserList(users == null ? 8 : 8 - users.size(),
							gen);
					users.addAll(last_user);
					bottle.setView_nums(users.size());
				} else {
					bottle.setView_nums(bottleDao.getScanUserCount(bottle.getId()));
				}
				ImagePathUtil.completeAvatarsPath(users, false);
				bottle.setScan_user_list(users);
			}
		}
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottles", bottles);
		result.put("hasMore", bottles.size() == page_size);
		return result;
	}

	public ModelMap delete(long user_id, long bottle_id) {
		int result = bottleDao.delete(user_id, bottle_id);
		if (result > 0) {
			Bottle bottle = new Bottle();
			bottle.setId(bottle_id);
			return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}

	public ModelMap scan(long user_id, String bottle_id) {
		if (!TextUtils.isEmpty(bottle_id)) {
			String[] bottle_ids = bottle_id.split(",");
			for (String id : bottle_ids) {
				try {
					long bid = Long.parseLong(id);
					bottleDao.logScan(user_id, bid);
				} catch (Exception e) {
				}
			}
		}
		return ResultUtil.getResultOKMap().addAttribute("ids", bottle_id);
	}

	public boolean isExistMeetTypeBottle(long user_id) {
		return bottleDao.isExistMeetTypeBottle(user_id);
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
	public ModelMap express(long user_id, long to_user_id, String content) {
		BottleExpress express = new BottleExpress();
		express.setUid(user_id);
		express.setTo_uid(to_user_id);
		express.setContent(content);
		express.setCreate_time(new Date());
		bottleDao.insertExpress(express);

		// dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_EXPRESS, user_id, -1,
		// to_user_id, content);
		BaseUser user = userDao.getBaseUser(user_id);
		BaseUser to_user = userDao.getBaseUser(to_user_id);
		makeChatSession(user, to_user, content);
		return ResultUtil.getResultOKMap();
	}

	private void makeChatSession(BaseUser user, BaseUser with_user, String expressMsg) {
		ImagePathUtil.completeAvatarPath(with_user, true);
		ImagePathUtil.completeAvatarPath(user, true);
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		Main.sendTxtMessage(String.valueOf(user.getUser_id()), new String[] { String.valueOf(with_user.getUser_id()) },
				expressMsg, ext, PushMsgType.TYPE_NEW_CONVERSATION);

		// 发送给自己
		// ext = new HashMap<String, String>();
		// ext.put("nickname", with_user.getNick_name());
		// ext.put("avatar", with_user.getAvatar());
		// ext.put("origin_avatar", with_user.getOrigin_avatar());
		// Main.sendTxtMessage(String.valueOf(with_user.getUser_id()), new String[] {
		// String.valueOf(user.getUser_id()) },
		// expressMsg, ext);

	}

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
						send(bottle, null);
						bottleID = bottle.getId();
					}
				}
				userDao.updateRelationship(user_id, withUserID, Relationship.LIKE);
				dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_MEET, user_id, bottleID, withUserID, "");

				// BaseUser u = userDao.getBaseUser(user_id);
				// BaseUser withUser = userDao.getBaseUser(withUserID);
				// makeChatSession(u, withUser, bottleID);
				user_ids.add(withUserID);
				// replay(user_id,withUserID,"有人喜欢了你",bottleID);
			}
		}
		return ResultUtil.getResultOKMap().addAttribute("with_user_id", user_ids);
	}

	public ModelMap replay(long user_id, long target, String msg, long bottle_id) {
		BaseUser user = userDao.getBaseUser(user_id);
		ImagePathUtil.completeAvatarPath(user, true);
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		ext.put("bottle_id", String.valueOf(bottle_id));
		Main.sendTxtMessage(String.valueOf(user.getUser_id()), new String[] { String.valueOf(target) }, msg, ext,
				PushMsgType.TYPE_NEW_CONVERSATION);
		return ResultUtil.getResultOKMap();
	}

	/**
	 * 清理掉过期的语音瓶子 ,根据分钟数
	 * 
	 * @param maxValidate
	 * @return
	 */
	public int clearExpireBottle(int maxValidate) {
		return bottleDao.clearExpireBottle(maxValidate);
	}

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

	public void clearExpireAudioBottle() {
		bottleDao.clearExpireAudioBottle();
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

		BaseUser u1 = userDao.getBaseUser(user_id);
		BaseUser u2 = userDao.getBaseUser(target);
		mainService.makeChatSession(u1, u2);
		u2.setToken(null);
		return ResultUtil.getResultOKMap().addAttribute("user", u2);
	}

	public void clearIllegalMeetBottle(long uid) {
		bottleDao.clearIllegalMeetBottle(uid);
		userDao.removeMeetBottleUserByUserId(uid);
	}
	public int clearPoolBottleByUserId(long uid) {
		return bottleDao.clearPoolBottleByUserId(uid);	
	}
	
	public void clearUserBottle(long uid) {
		clearPoolBottleByUserId(uid);
		bottleDao.clearBottleByUserId(uid);
	}
	
}
