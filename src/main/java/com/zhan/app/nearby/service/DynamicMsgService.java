package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.type.DynamicMsgStatus;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.DynamicMsgDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.task.CommAsyncTask;
import com.zhan.app.nearby.task.HXAsyncTask;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.PushUtils;
import com.zhan.app.nearby.util.ResultUtil;

@Service
public class DynamicMsgService {
	@Resource
	private DynamicMsgDao dynamicMsgDao;
	@Resource
	private UserDao userDao;
	@Resource
	private GiftService giftService;
	@Resource
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private HXAsyncTask hxTask;
	
	@Autowired
	private CommAsyncTask commAsyncTask;

	/**
	 * 
	 * @param type
	 * @param by_user_id 消息触发者
	 * @param dynamic_id
	 * @param user_id    目标用户
	 * @param content
	 * @return
	 */
	public long insertActionMsg(DynamicMsgType type, long by_user_id, long obj_id, long user_id, String content) {

		DynamicMessage msg = new DynamicMessage();
		msg.setUser_id(user_id);
		msg.setBy_user_id(by_user_id);
		msg.setObj_id(obj_id);
		msg.setType(type.ordinal());
		msg.setContent(content);
		msg.setCreate_time(new Date());
		msg.setStatus(DynamicMsgStatus.DEFAULE.ordinal());
		long id = dynamicMsgDao.insert(msg);
		PushUtils.pushActionMsg(redisTemplate, id, type, user_id, obj_id);
		return id;
	}

	public List<DynamicMessage> msg_list(Long user_id, long last_id, int type) {
		List<DynamicMessage> msgs = dynamicMsgDao.loadMsg(user_id, last_id, type);
		if (msgs != null) {
			for (DynamicMessage message : msgs) {
				ImagePathUtil.completeAvatarPath(message.getUser(), true);
			}
		}
		return msgs;
	}

	public void delete(long msg_id) {
		dynamicMsgDao.delete(msg_id);
	}

	public int updateState(long id) {
		return dynamicMsgDao.updateState(id);
	}

	public int updateMeetState(long user_id, long target) {
		return dynamicMsgDao.updateMeetState(user_id, target);
	}

	public ModelMap replay(long user_id, long msg_id) {
		DynamicMessage msg = dynamicMsgDao.loadMsg(msg_id);

		if (msg == null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "消息不存在").addAttribute("id", msg_id);
		}

		dynamicMsgDao.updateState(msg_id);
		// 邂逅或者表白信的回复

		if (msg.getType() == DynamicMsgType.TYPE_LIKE.ordinal() || msg.getType() == DynamicMsgType.TYPE_MEET.ordinal()
				|| msg.getType() == DynamicMsgType.TYPE_EXPRESS.ordinal()) {
			userDao.updateRelationship(user_id, msg.getBy_user_id(), Relationship.LIKE);

			BaseUser me = userDao.getBaseUser(user_id);
			BaseUser he = userDao.getBaseUser(msg.getBy_user_id());
			if (msg.getType() == DynamicMsgType.TYPE_MEET.ordinal()) {
				hxTask.makeChatSession(me, he, msg.getObj_id());
			} else if (msg.getType() == DynamicMsgType.TYPE_LIKE.ordinal()) {
				hxTask.makeChatSession(me, he, 0);
			} else {
				hxTask.makeChatSession(me, he);
			}
		}
		return ResultUtil.getResultOKMap().addAttribute("id", msg_id);
	}

	public ModelMap getMyDynamicMsg(long user_id, boolean isV2) {
		List<DynamicComment> comms = dynamicMsgDao.getMyDynamicCommentLatest(user_id);
		List<DynamicMessage> msgs = dynamicMsgDao.getMyMeetLatest(user_id);
		dynamicMsgDao.updateLatestTipTime(user_id);
		if (isV2) {
			List<DynamicMessage> praise = dynamicMsgDao.getPraseMsg(user_id);
			dynamicMsgDao.updateLatestPraiseTipTime(user_id);
			return ResultUtil.getResultOKMap().addAttribute("comments", comms).addAttribute("xiehou_msgs", msgs)
					.addAttribute("praise_msgs", praise);
		} else {
			return ResultUtil.getResultOKMap().addAttribute("comments", comms).addAttribute("xiehou_msgs", msgs);
		}

	}

	public ModelMap loadMsg(long user_id,String token, Long last_id, int count,boolean noMeet) {
		ModelMap result = ResultUtil.getResultOKMap();
	
		List<DynamicMessage> msgs = dynamicMsgDao.loadMsg(user_id, last_id, count,noMeet);
		 
		result.addAttribute("msgs", msgs);
		
		if(last_id==null) {
			int unread_count = dynamicMsgDao.getUnReadMsgCount(user_id,noMeet);
			result.addAttribute("unread_count", unread_count);
		}

		if(!msgs.isEmpty()) {
			last_id=msgs.get(msgs.size()-1).getId();
		}
		return result.addAttribute("hasMore", msgs.size()==count).addAttribute("last_id", last_id);

	}

	public int clearMeetMsg(long user_id) {
		return dynamicMsgDao.clearMeetMsg(user_id);
	}
	public void clearMsg(long user_id,long last_id) {
		 dynamicMsgDao.clearMsg(user_id, last_id);
	}

	public int delMeetMsg(long user_id, long id) {
		return dynamicMsgDao.delMeetMsg(user_id, id, DynamicMsgType.TYPE_MEET.ordinal());
	}

	public BaseUser getLikeLastOne(long user_id) {
		long id = dynamicMsgDao.getLikeLastOneID(user_id);
		BaseUser user = userDao.getBaseUser(id);
		ImagePathUtil.completeAvatarPath(user, true);
		return user;
	}

	public boolean hasExistDyMsg(DynamicMsgType type, long by_user_id, long dynamic_id, long user_id) {
		int count = dynamicMsgDao.getDymanicMsgCount(DynamicMsgType.TYPE_MEET, by_user_id, dynamic_id, user_id);
		return count > 0;
	}

	public void replayDynamicMsg(Long user_id, long msg_id) {
		dynamicMsgDao.updateMsgStatus(msg_id, DynamicMsgStatus.HAD_Operation);
	}
}
