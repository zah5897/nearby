package com.zhan.app.nearby.util;

public class PushUtils {
	public static final String APP_NAME = "nearby";
	public static final String KEY_NEARBY_PUSH = "nearby_push_queue";

	public static int TYPE = 1; // 开发模式
//	public static void pushActionMsg(final RedisTemplate<String, String> redisTemplate, final long msg_id,
//			final DynamicMsgType type, final long user_id, final long id) {
//
//		if (type == DynamicMsgType.TYPE_COMMENT || type == DynamicMsgType.TYPE_PRAISE) {
//
//			new Thread() {
//				public void run() {
//					UserService userService = SpringContextUtil.getBean("userService");
//					try {
//						String token = userService.getDeviceToken(user_id);
//						if (TextUtils.isEmpty(token)) {
//							return;
//						}
//
//						String title = "系统消息";
//
//						if (type == DynamicMsgType.TYPE_COMMENT) {
//							title = "有人评论了你的动态";
//						} else if (type == DynamicMsgType.TYPE_PRAISE) {
//							title = "有人赞了你的动态";
//						}
//
//						JSONObject object = new JSONObject();
//						object.put("alert", title);
//						object.put("id", id);
//						object.put("app_name", APP_NAME);
//						object.put("token", token);
//						object.put("time", System.currentTimeMillis() / 1000 / 60); // 精度分钟
//						object.put("type", TYPE);
//						object.put("msg_id", msg_id);
//						redisTemplate.opsForList().leftPush(KEY_NEARBY_PUSH, object.toJSONString());
//					} catch (Exception e) {
//
//					}
//				}
//			}.start();
//		}
//	}
}
