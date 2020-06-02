package com.zhan.app.nearby.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import com.easemob.server.example.HXHistoryMsg;
import com.easemob.server.example.HXHistoryMsgDownloadHelper;
import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.ChatConversationType;
import com.zhan.app.nearby.comm.PushMsgType;

public class HX_SessionUtil {

	public static String[] meet_msg = { "你好，很高兴遇见你", "你在吗？", "遇见你是缘分。", "你是我等待的那个朋友哦～", "瓶友，你好", "好久不见～", "亲爱，你好！",
			"Hi,你在吗？", "Hello!我在这", "很高兴成为网友～", "春风十里，不如见你", "亲，在线等你～", "比心！你在吗？", "你就是我等的人", "等你好久了", "在线的话，回我下",
			"在？聊一聊", "在茫茫大海中找到你，想和你聊天", "在的话，回复1", "不做朋友，做网友？", "很开心，你也在！" };

	public static String getRandomMsg() {
		int r = new Random().nextInt(meet_msg.length);
		return meet_msg[r];
	}

	public static void replayBottle(BaseUser user, BaseUser with_user, Bottle bottle) {
		if (bottle == null) {
			return;
		}
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		putUserInfo(ext, user);
		ext.put("bottle_id", String.valueOf(bottle.getId()));
		putBottleInfo(ext, bottle);

		String msg = getRandomMsg();
		Main.sendTxtMessage(user, new String[] { String.valueOf(with_user.getUser_id()) }, msg, ext);
		// 发送给自己
		ext = new HashMap<String, String>();
		putUserInfo(ext, with_user);
		ext.put("bottle_id", String.valueOf(bottle.getId()));
		putBottleInfo(ext, bottle);
		Main.sendTxtMessage(with_user, new String[] { String.valueOf(user.getUser_id()) }, msg, ext);
	}

	public static void replayBottleSingle(BaseUser user, BaseUser with_user, Bottle bottle, String msg) {
		if (bottle == null) {
			return;
		}
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		putUserInfo(ext, user);
		ext.put("bottle_id", String.valueOf(bottle.getId()));
		putBottleInfo(ext, bottle);
		Main.sendTxtMessage(user, new String[] { String.valueOf(with_user.getUser_id()) }, msg, ext);
	}

	public static void replayRedPackageBottleSingle(BaseUser user, long to, Bottle bottle, int coin) {
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("bottle_id", String.valueOf(bottle.getId()));
		putUserInfo(ext, user);

		Map<String, String> data = new HashMap<String, String>();
		data.put("content", user.getNick_name() + "领取了你的扇贝");
		data.put("type", ChatConversationType.CHAT_TYPE_RED_PACKET_BOTTLE);

		putDataInfo(data, user);
		ext.put("data", JSONUtil.writeValueAsString(data));

		Main.sendTxtMessage(user, new String[] { String.valueOf(to) }, user.getNick_name() + "领取了你的扇贝", ext);
	}

	public static void createChatSession(BaseUser user, BaseUser with_user) {
		createChatSession(user, with_user, null);
	}

	public static void createChatSession(BaseUser user, BaseUser with_user, String msg) {
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		putUserInfo(ext, user);

		Map<String, String> data = new HashMap<String, String>();
		data.put("content", msg);
		data.put("type", ChatConversationType.CHAT_TYPE_SYSTEM_MATCH);
		putDataInfo(data, user);

		String dataStr = JSONUtil.writeValueAsString(data);
		ext.put("data", dataStr);
		if (TextUtils.isEmpty(msg)) {
			msg = getRandomMsg();
		}
		Main.sendTxtMessage(user, new String[] { String.valueOf(with_user.getUser_id()) }, msg, ext);
		// 发送给自己
		ext = new HashMap<String, String>();
		putUserInfo(ext, with_user);
		putDataInfo(data, with_user);
		dataStr = JSONUtil.writeValueAsString(data);
		ext.put("data", dataStr);
		Main.sendTxtMessage(with_user, new String[] { String.valueOf(user.getUser_id()) }, msg, ext);
	}

	public static void createExpressSession(BaseUser user, BaseUser with_user, String msg) {
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		putUserInfo(ext, user);

		Map<String, String> data = new HashMap<String, String>();
		data.put("content", msg);
		data.put("type", ChatConversationType.CHAT_TYPE_EXPRESS);
		putDataInfo(data, user);
		ext.put("data", JSONUtil.writeValueAsString(data));
		Main.sendTxtMessage(user, new String[] { String.valueOf(with_user.getUser_id()) }, msg, ext);
	}

	public static void pushPraise(long toUid, long dynamic_id) {
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("dynamic_id", String.valueOf(dynamic_id));

		String msg = "有人赞了你的图片！";
		ext.put("msg", msg);
		ext.put("dynamic_id", String.valueOf(dynamic_id));
		ext.put("type", PushMsgType.TYPE_RECEIVE_PRAISE);
		Main.sendCmdMessage(new String[] { String.valueOf(toUid) }, ext);
	}

	public static void pushComment(long toUid, DynamicComment comment) {
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("comment_id", String.valueOf(comment.getId()));
		ext.put("dynamic_id", String.valueOf(comment.getDynamic_id()));
		String user_id_str = String.valueOf(toUid);

		String msg = "有人评论了你的图片，快去看看！";
		ext.put("msg", msg);
		ext.put("type", PushMsgType.TYPE_RECEIVE_COMMENT);
		Main.sendCmdMessage(new String[] { user_id_str }, ext);
	}

//
	public static void pushLike(long toUid) {
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("type", PushMsgType.TYPE_RECEIVE_LIKE);
		ext.put("msg", "有人点击了喜欢你");
		Object obj = Main.sendCmdMessage(new String[] { String.valueOf(toUid) }, ext);
		System.out.print(obj);
	}

//
	public static void pushGift(String from_nick_name, long to_user_id) {
		// 通知对方收到某某的礼物
		Map<String, String> ext = new HashMap<String, String>();
		ext = new HashMap<String, String>();
		ext.put("msg", from_nick_name + "赠送了一个礼物给你");
		ext.put("type", PushMsgType.TYPE_RECEIVE_GIFT);
		Main.sendCmdMessage(new String[] { String.valueOf(to_user_id) }, ext);
	}

	public static void pushVip(long to_user_id) {
		// 通知对方收到某某的礼物
		Map<String, String> ext = new HashMap<String, String>();
		ext = new HashMap<String, String>();
		ext.put("msg", "vip 到账通知");
		ext.put("type", ChatConversationType.TYPE_BUY_VIP);
		Main.sendCmdMessageVipInfo(new String[] { String.valueOf(to_user_id) }, ext);
	}

	public static void pushOnLine(BaseUser user, String[] to_user_ids) {
		// 通知对方收到某某的礼物
		Map<String, String> ext = new HashMap<String, String>();
		ext = new HashMap<String, String>();
		putUserInfo(ext, user);
		ext.put("msg", "用户上线通知");
		ext.put("type", ChatConversationType.TYPE_ONLINE_USER);

		Map<String, String> data = new HashMap<String, String>();
		data.put("content", user.getNick_name() + "用户上线通知");
		data.put("type", ChatConversationType.TYPE_ONLINE_USER);
		Map<String, String> online_user = new HashMap<String, String>();
		putDataInfo(online_user, user);
		data.put("online_user", JSONUtil.writeValueAsString(online_user));
		putDataInfo(data, user);
		ext.put("data", JSONUtil.writeValueAsString(data));
		Main.sendCmdMessageOnlineMsg(to_user_ids, ext, user);
	}
 

	public static void pushCloseChatPage(String[] to_user_ids) {
		// 通知对方收到某某的礼物
		Map<String, String> ext = new HashMap<String, String>();
		ext = new HashMap<String, String>();
		ext.put("msg", "关闭聊天页面通知");
		ext.put("type", ChatConversationType.TYPE_CLOSE_CHAT_PAGE);

		Map<String, String> data = new HashMap<String, String>();
		data.put("content","关闭聊天页面通知");
		data.put("type", ChatConversationType.TYPE_CLOSE_CHAT_PAGE);
		ext.put("data", JSONUtil.writeValueAsString(data));
		Main.sendCmdMessage(to_user_ids, ext);
	}
	
	
//	public static void sendReplayBottle(BaseUser user, long target, String msg, Map<String, String> ext,
//			String typeNewConversation) {
//		Main.sendTxtMessage(user, new String[] { String.valueOf(target) }, msg, ext, typeNewConversation);
//	}

	public static void disconnectUser(String valueOf) {
		Main.disconnectUser(valueOf);
	}

	public static void disconnect(String valueOf) {
		Main.disconnect(valueOf);
	}

	public static void sendWelcome(String[] users, String msgTxt, Map<String, String> ext, String TYPE) {
		Main.sendTxtMessageByAdmin(users, msgTxt, ext, PushMsgType.TYPE_WELCOME);
	}

	public static Object registUser(String userName, String password, String nickname) {
		return Main.registUser(userName, password, nickname);
	}

	private static void putUserInfo(Map<String, String> ext, BaseUser from) {
		String avatar = ImagePathUtil.completeStrAvatarPath(from.getAvatar());
		ext.put("nickname", from.getNick_name());
		ext.put("avatar", avatar);
		ext.put("origin_avatar", avatar);
	}

	public static void putDataInfo(Map<String, String> data, BaseUser by) {
		data.put("sender_user_id", String.valueOf(by.getUser_id()));
		data.put("sender_nickname", by.getNick_name());
		data.put("sender_avatar", ImagePathUtil.completeStrAvatarPath(by.getAvatar()));
	}

	private static void putBottleInfo(Map<String, String> ext, Bottle bottle) {
		Map<String, String> bottleInfo = new HashMap<String, String>();

		bottleInfo.put("sender_user_id", String.valueOf(bottle.getUser_id()));
		bottleInfo.put("sender_nickname", bottle.getSender().getNick_name());
		bottleInfo.put("sender_avatar", ImagePathUtil.completeStrAvatarPath(bottle.getSender().getAvatar()));
		bottleInfo.put("bottle_type", String.valueOf(bottle.getType()));

		int type = bottle.getType();
		switch (BottleType.values()[type]) {
		case TXT:
			bottleInfo.put("content", bottle.getContent());
			bottleInfo.put("type", ChatConversationType.CHAT_TYPE_TEXT_BOTTLE);
			break;
		case VOICE:
			Map<String, Object> voiceMsg = JSONUtil.jsonToMap(bottle.getContent());
			bottleInfo.put("voice_id", voiceMsg.get("messageId").toString());
			bottleInfo.put("voice_length", voiceMsg.get("duration").toString());
			bottleInfo.put("voice_url", voiceMsg.get("remotePath").toString());
			bottleInfo.put("type", ChatConversationType.CHAT_TYPE_VOICE_BOTTLE);
			break;
		case MEET:
			bottleInfo.put("type", ChatConversationType.CHAT_TYPE_MEET_BOTTLE);
			break;
		case DM_TXT:
			bottleInfo.put("content", bottle.getContent());
			bottleInfo.put("type", ChatConversationType.CHAT_TYPE_BARRAGE_TEXT_BOTTLE);
			break;
		case DM_VOICE:
			Map<String, Object> dmvoiceMsg = JSONUtil.jsonToMap(bottle.getContent());
			bottleInfo.put("voice_id", dmvoiceMsg.get("messageId").toString());
			bottleInfo.put("voice_length", dmvoiceMsg.get("duration").toString());
			bottleInfo.put("voice_url", dmvoiceMsg.get("remotePath").toString());

			bottleInfo.put("type", ChatConversationType.CHAT_TYPE_BARRAGE_VOICE_BOTTLE);
			break;
		case DRAW_GUESS:
			bottleInfo.put("content", bottle.getAnswer());
			bottleInfo.put("type", ChatConversationType.CHAT_TYPE_DRAW_BOTTLE);
			break;
		case RED_PACKAGE:
			bottleInfo.put("content", bottle.getContent());
			bottleInfo.put("type", ChatConversationType.CHAT_TYPE_RED_PACKET_BOTTLE);
			break;
		default:
			break;
		}
		ext.put("data", JSONUtil.writeValueAsString(bottleInfo));
	}

	public static List<HXHistoryMsg> exportChatMessages(String timePoint) {
		return HXHistoryMsgDownloadHelper.downloadHistoryMessage(timePoint);
	}

	public static String downloadAudioFile( String remoteUrl, String secretKey) throws IOException {
		return Main.downloadAudioFile( remoteUrl, secretKey);
	}
	public static void downloadImgFile(HttpServletResponse response,String remoteUrl, String secretKey) throws IOException {
		  Main.downloadImgFile( response,remoteUrl, secretKey);
	}
}
