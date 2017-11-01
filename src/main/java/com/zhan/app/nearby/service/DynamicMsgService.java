package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.dao.DynamicMsgDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.PushUtils;

@Service
@Transactional("transactionManager")
public class DynamicMsgService {
	@Resource
	private DynamicMsgDao dynamicMsgDao;
	@Resource
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 
	 * @param type
	 * @param by_user_id 消息触发者
	 * @param dynamic_id
	 * @param user_id 目标用户
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

	public List<DynamicMessage> msg_list(Long user_id, long last_id,int type) {
		List<DynamicMessage> msgs = dynamicMsgDao.loadMsg(user_id, last_id,type);
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
}
