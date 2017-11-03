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
import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.dao.DynamicMsgDao;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.PushUtils;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

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
		DynamicMessage msg = new DynamicMessage();
		msg.setUser_id(user_id);
		msg.setBy_user_id(by_user_id);
		msg.setDynamic_id(dynamic_id);
		msg.setType(type.ordinal());
		msg.setContent(content);
		msg.setCreate_time(new Date());
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

	public ModelMap replay(long user_id, long msg_id) {
		DynamicMessage msg = dynamicMsgDao.loadMsg(msg_id);
		dynamicMsgDao.updateState(msg_id);
		// 邂逅或者表白信的回复
		if (msg.getType() == DynamicMsgType.TYPE_MEET.ordinal()
				|| msg.getType() == DynamicMsgType.TYPE_EXPRESS.ordinal()) {
			userDao.updateRelationship(user_id, msg.getBy_user_id(), Relationship.LIKE);
			
			User me=userDao.getUserSimple(user_id).get(0);
			// 发送给对方
			Map<String, String> ext = new HashMap<String, String>();
			ext.put("nickname", me.getNick_name());
			ext.put("avatar", me.getAvatar());
			ext.put("origin_avatar", me.getOrigin_avatar());
			Object result = Main.sendTxtMessage(String.valueOf(me.getUser_id()),
					new String[] { String.valueOf(msg.getBy_user_id()) }, "很高兴遇见你", ext);
			if (result != null) {
				System.out.println(result);
			}

			// 发送给自己
			User he=userDao.getUserSimple(msg.getBy_user_id()).get(0);
			ext = new HashMap<String, String>();
			ext.put("nickname", he.getNick_name());
			ext.put("avatar", he.getAvatar());
			ext.put("origin_avatar", he.getOrigin_avatar());
			result = Main.sendTxtMessage(String.valueOf(he.getUser_id()),
					new String[] { String.valueOf(user_id) }, "很高兴遇见你", ext);
			if (result != null) {
				System.out.println(result);
			}
		}
		return ResultUtil.getResultOKMap();
	}

	public ModelMap noticeList(Long last_id) {
		return ResultUtil.getResultOKMap().addAttribute("notice", giftService.loadGiftGiveList(last_id==null?0:last_id));
	}

}
