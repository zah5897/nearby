package com.zhan.app.nearby.util;

import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.service.UserService;

public class PushUtils {
	public static final String APP_NAME = "nearby";
	public static final String KEY_NEARBY_PUSH = "nearby_push_queue";

	public static int TYPE = 1; // 开发模式

	public static void commentMsg(RedisTemplate<String, String> redisTemplate,long msg_id, DynamicMsgType type, long user_id,
			long id) {

		new Thread() {
			public void run() {
				UserService userService = SpringContextUtil.getBean("userService");
				try {
					String token = userService.getDeviceToken(user_id);
					if (TextUtils.isEmpty(token)) {
						return;
					}

					String title = "系统消息";

					if (type == DynamicMsgType.TYPE_COMMENT) {
						title = "有人评论了你的动态";
					} else if (type == DynamicMsgType.TYPE_PRAISE) {
						title = "有人赞了你的动态";
					}

					JSONObject object = new JSONObject();
					object.put("alert", title);
					object.put("id", id);
					object.put("app_name", APP_NAME);
					object.put("token", token);
					object.put("time", System.currentTimeMillis() / 1000 / 60); // 精度分钟
					object.put("type", TYPE);
					object.put("msg_id", msg_id);
					long id = redisTemplate.opsForList().leftPush(KEY_NEARBY_PUSH, object.toJSONString());
				} catch (Exception e) {

				}
			}
		}.start();

	}
}
