package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.type.DynamicMsgStatus;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.DynamicMsgDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.PushUtils;
import com.zhan.app.nearby.util.ResultUtil;

@Service
@Transactional("transactionManager")
public class DynamicMsgService {
	@Resource
	private DynamicMsgDao dynamicMsgDao;
	@Resource
	private UserDao userDao;
	@Resource
	private GiftService giftService;
	@Resource
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 
	 * @param type
	 * @param by_user_id
	 *            消息触发者
	 * @param dynamic_id
	 * @param user_id
	 *            目标用户
	 * @param content
	 * @return
	 */
	public long insertActionMsg(DynamicMsgType type, long by_user_id, long dynamic_id, long user_id, String content) {
		
		
		if (hasExistDyMsg(DynamicMsgType.TYPE_MEET, user_id, dynamic_id, user_id)) {
			return 0;
		}
		
		DynamicMessage msg = new DynamicMessage();
		msg.setUser_id(user_id);
		msg.setBy_user_id(by_user_id);
		msg.setDynamic_id(dynamic_id);
		msg.setType(type.ordinal());
		msg.setContent(content);
		msg.setCreate_time(new Date());
		msg.setStatus(DynamicMsgStatus.DEFAULE.ordinal());
		long id = dynamicMsgDao.insert(msg);
		PushUtils.pushActionMsg(redisTemplate, id, type, user_id, dynamic_id);
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

	
	public int updateMeetState(long user_id,long target) {
		return dynamicMsgDao.updateMeetState(user_id,target);
	}
	
	public ModelMap replay(long user_id, long msg_id) {
		DynamicMessage msg = dynamicMsgDao.loadMsg(msg_id);
		 
		if(msg==null) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED,"消息不存在").addAttribute("id", msg_id);
		}
		
		dynamicMsgDao.updateState(msg_id);
		// 邂逅或者表白信的回复
		
		
		if (msg.getType() == DynamicMsgType.TYPE_LIKE.ordinal()||msg.getType() == DynamicMsgType.TYPE_MEET.ordinal()
				|| msg.getType() == DynamicMsgType.TYPE_EXPRESS.ordinal()) {
			userDao.updateRelationship(user_id, msg.getBy_user_id(), Relationship.LIKE);
			
			BaseUser me = userDao.getBaseUser(user_id);
			BaseUser he = userDao.getBaseUser(msg.getBy_user_id());
			String niceToMeet="很高兴遇见你";
			if(msg.getType() == DynamicMsgType.TYPE_MEET.ordinal()) {
				makeChatSession(me,he,msg.getDynamic_id(),niceToMeet);
			}else if(msg.getType() == DynamicMsgType.TYPE_LIKE.ordinal()){
				makeChatSession(me,he,0,niceToMeet);
			}else {
				makeChatSession(me,he.getUser_id(),"");
			}
		}
		return ResultUtil.getResultOKMap().addAttribute("id", msg_id);
	}

	
	private void makeChatSession(BaseUser user, BaseUser with_user, long bottle_id,String msg) {
		ImagePathUtil.completeAvatarPath(with_user, true);
		ImagePathUtil.completeAvatarPath(user, true);
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		if(bottle_id>0) {
			ext.put("bottle_id", String.valueOf(bottle_id));
		}
		Main.sendTxtMessage(String.valueOf(user.getUser_id()), new String[] { String.valueOf(with_user.getUser_id()) },
				msg,ext,PushMsgType.TYPE_NEW_CONVERSATION);
		// 发送给自己
		ext = new HashMap<String, String>();
		ext.put("nickname", with_user.getNick_name());
		ext.put("avatar", with_user.getAvatar());
		ext.put("origin_avatar", with_user.getOrigin_avatar());
		if(bottle_id>0) {
			ext.put("bottle_id", String.valueOf(bottle_id));
		}
		Main.sendTxtMessage(String.valueOf(with_user.getUser_id()), new String[] { String.valueOf(user.getUser_id()) },
				msg,ext,PushMsgType.TYPE_NEW_CONVERSATION);
	}
	
	
	private void makeChatSession(BaseUser from,long target,String msg) {
		ImagePathUtil.completeAvatarPath(from, true);
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", from.getNick_name());
		ext.put("avatar", from.getAvatar());
		ext.put("origin_avatar", from.getOrigin_avatar());
		 
		Main.sendTxtMessage(String.valueOf(from.getUser_id()), new String[] { String.valueOf(target) },
				msg,ext,PushMsgType.TYPE_NEW_CONVERSATION);
	 
	}
	
	
	public ModelMap getMyDynamicMsg(long user_id){
		List<DynamicComment> comms= dynamicMsgDao.getMyDynamicCommentLatest(user_id);
		List<DynamicMessage> msgs= dynamicMsgDao.getMyMeetLatest(user_id);
		dynamicMsgDao.updateLatestTipTime(user_id);
		return ResultUtil.getResultOKMap().addAttribute("comments", comms).addAttribute("xiehou_msgs", msgs);
	}
	

	public int clearMeetMsg(long user_id) {
		return dynamicMsgDao.clearMeetMsg(user_id);
	}
	
	public int delMeetMsg(long user_id,long id) {
		return dynamicMsgDao.delMeetMsg(user_id, id, DynamicMsgType.TYPE_MEET.ordinal());
	}

	public BaseUser getLikeLastOne(long user_id) {
		long id= dynamicMsgDao.getLikeLastOneID(user_id);
		BaseUser user=	userDao.getBaseUser(id);
		ImagePathUtil.completeAvatarPath(user, true);
		return user;
	}
	
	public boolean hasExistDyMsg(DynamicMsgType type, long by_user_id, long dynamic_id, long user_id) {
	  int count= dynamicMsgDao.getDymanicMsgCount(DynamicMsgType.TYPE_MEET, by_user_id, dynamic_id, user_id);
	  return count>0;
	}

	public void replayDynamicMsg(Long user_id, long msg_id) {
		// TODO Auto-generated method stub
		  dynamicMsgDao.updateMsgStatus(msg_id,DynamicMsgStatus.HAD_Operation);
	}
}
