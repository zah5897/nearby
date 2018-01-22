package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.BottleExpress;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.BottleDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.VipDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ImagePathUtil;
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

	public Bottle getBottleFromPool(long user_id) {

		List<Bottle> bottles = bottleDao.getBottleRandomInPool(user_id, 1);
		if (bottles != null && bottles.size() > 0) {
			return bottles.get(0);
		}
		return null;
	}

	public ModelMap getBottles(long user_id, long last_id, int page_size, Integer look_sex) {
		ModelMap result = ResultUtil.getResultOKMap();
		List<Bottle> bolltes = null;
		if (look_sex == null) {
			bolltes = bottleDao.getBottles(user_id, last_id, page_size);
		} else {
			VipUser vip = vipDao.loadUserVip(user_id);
			if (vip == null) {
				return ResultUtil.getResultMap(ERROR.ERR_NOT_VIP, "普通用户获取瓶子");
			} else if (vip.getDayDiff() < 0) {
				return ResultUtil.getResultMap(ERROR.ERR_VIP_EXPIRE);
			}
			bolltes = bottleDao.getBottlesByGender(user_id, last_id, page_size, look_sex == null ? -1 : look_sex);
		}

		for (Bottle bottle : bolltes) {
			if (bottle.getType() == BottleType.MEET.ordinal()) {
				ImagePathUtil.completeAvatarPath(bottle.getSender(), true);
			}
		}

		result.addAttribute("bottles", bolltes);
		return result;
	}

	public boolean existBottles(String content, String img) {
		return bottleDao.existBottles(content, img);
	}

	public List<Bottle> getBottlesFromPool(long user_id) {
		return bottleDao.getBottleRandomInPool(user_id, LIMIT_COUNT);
	}

	public void insert(Bottle bottle) {
		bottle.setCreate_time(new Date());
		bottleDao.insert(bottle);
		if (bottle.getId() > 0) {
			bottleDao.insertToPool(bottle);
		}
	}

	public Bottle getBottleDetial(long bottle_id) {
		return bottleDao.getBottleById(bottle_id);
	}

	public ModelMap getMineBottles(long user_id, long last_id, int page_size) {
		List<Bottle> bottles = bottleDao.getMineBottles(user_id, last_id, page_size);
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

		dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_EXPRESS, user_id, -1, to_user_id, content);
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
		Object result = Main.sendTxtMessage(String.valueOf(user.getUser_id()),
				new String[] { String.valueOf(with_user.getUser_id()) }, expressMsg, ext);
		if (result != null) {
			System.out.println(result);
		}
		// 发送给自己
		ext = new HashMap<String, String>();
		ext.put("nickname", with_user.getNick_name());
		ext.put("avatar", with_user.getAvatar());
		ext.put("origin_avatar", with_user.getOrigin_avatar());
		result = Main.sendTxtMessage(String.valueOf(with_user.getUser_id()),
				new String[] { String.valueOf(user.getUser_id()) }, expressMsg, ext);
		if (result != null) {
			System.out.println(result);
		}

		// 系统推"附近有人喜欢了你"给对方
		String msg = "附近有人向你表白了！";
		ext.put("msg", msg);
		result = Main.sendTxtMessage(Main.SYS, new String[] { String.valueOf(with_user.getUser_id()) }, msg, ext);
		if (result != null) {
			System.out.println(result);
		}
	}

	public ModelMap like(long user_id, String with_user_id) {
		if (!TextUtils.isEmpty(with_user_id)) {
			JSONArray array = JSON.parseArray(with_user_id);
			int len = array.size();
			for (int i = 0; i < len; i++) {
				JSONObject u_b = array.getJSONObject(i);
				long withUserID = u_b.getLongValue("uid");
				long bottleID = u_b.getLongValue("bottle_id");
				//判断瓶子是否存在，不存在的话要新建
				if (bottleID < 1) {
					List<Long> ids = bottleDao.getMeetBottleIDByUser(withUserID);
					if(ids.size()>0) {
						bottleID=ids.get(0);
					}else {
						Bottle bottle=new Bottle();
						bottle.setCreate_time(new Date());
						bottle.setUser_id(withUserID);
						bottle.setType(BottleType.MEET.ordinal());
						bottle.setContent(String.valueOf(withUserID));
						insert(bottle);
						bottleID=bottle.getId();
					}
				}
				userDao.updateRelationship(user_id, withUserID, Relationship.LIKE);
				dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_MEET, user_id, bottleID, withUserID, "");
				BaseUser u = userDao.getBaseUser(user_id);
				BaseUser withUser = userDao.getBaseUser(withUserID);
				makeChatSession(u, withUser, bottleID);
			}
		}
		return null;
	}

	private void makeChatSession(BaseUser user, BaseUser with_user, long bottle_id) {
		ImagePathUtil.completeAvatarPath(with_user, true);
		ImagePathUtil.completeAvatarPath(user, true);
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		ext.put("bottle_id", String.valueOf(bottle_id));
		Main.sendCmdMessage(String.valueOf(user.getUser_id()), new String[] { String.valueOf(with_user.getUser_id()) },
				ext);

		// 发送给自己
		ext = new HashMap<String, String>();
		ext.put("nickname", with_user.getNick_name());
		ext.put("avatar", with_user.getAvatar());
		ext.put("origin_avatar", with_user.getOrigin_avatar());
		ext.put("bottle_id", String.valueOf(bottle_id));
		Main.sendCmdMessage(String.valueOf(with_user.getUser_id()), new String[] { String.valueOf(user.getUser_id()) },
				ext);
	}
}
