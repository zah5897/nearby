package com.zhan.app.nearby.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.PushMsgType;



public class HX_SessionUtil {

	public static String[] meet_msg = { "你好，很高兴遇见你",
			"你在吗？",
			"遇见你是缘分。",
			"你是我等待的那个朋友哦～",
			"瓶友，你好",
			"好久不见～",
			"亲爱，你好！",
			"Hi,你在吗？",
			"Hello!我在这",
			"很高兴成为网友～",
			"春风十里，不如见你",
			"亲，在线等你～",
			"比心！你在吗？",
			"你就是我等的人",
			"等你好久了",
			"在线的话，回我下",
			"在？聊一聊",
			"在茫茫大海中找到你，想和你聊天",
			"在的话，回复1",
			"不做朋友，做网友？",
			"很开心，你也在！" };
	
	public static String getRandomMsg() {
		int r = new Random().nextInt(meet_msg.length);
		return meet_msg[r];
	}
	
	public static void makeChatSession(BaseUser user, BaseUser with_user, long bottle_id) {
		ImagePathUtil.completeAvatarPath(with_user, true);
		ImagePathUtil.completeAvatarPath(user, true);
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		if (bottle_id > 0) {
			ext.put("bottle_id", String.valueOf(bottle_id));
		}

		String msg = getRandomMsg();
		Main.sendTxtMessage(user, new String[] { String.valueOf(with_user.getUser_id()) },
				msg, ext, PushMsgType.TYPE_NEW_CONVERSATION);
		// 发送给自己
		ext = new HashMap<String, String>();
		ext.put("nickname", with_user.getNick_name());
		ext.put("avatar", with_user.getAvatar());
		ext.put("origin_avatar", with_user.getOrigin_avatar());
		if (bottle_id > 0) {
			ext.put("bottle_id", String.valueOf(bottle_id));
		}
		Main.sendTxtMessage(with_user, new String[] { String.valueOf(user.getUser_id()) },
				msg, ext, PushMsgType.TYPE_NEW_CONVERSATION);
	}

	public static void makeChatSessionSingle(BaseUser user, BaseUser with_user, String expressMsg) {
		ImagePathUtil.completeAvatarPath(with_user, true);
		ImagePathUtil.completeAvatarPath(user, true);
		// 发送给对方
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		Main.sendTxtMessage(user, new String[] { String.valueOf(with_user.getUser_id()) },
				expressMsg, ext, PushMsgType.TYPE_NEW_CONVERSATION);
	}

	public static void makeChatSession(BaseUser user, BaseUser with_user) {
		makeChatSession(user, with_user, 0);
	}

	public static void matchCopyDraw(BaseUser fromU, long toU, String msg) {
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", fromU.getNick_name());
		ext.put("avatar", fromU.getAvatar());
		ext.put("origin_avatar", fromU.getOrigin_avatar());
		Main.sendTxtMessage(fromU, new String[] { String.valueOf(toU) }, msg, ext,
				PushMsgType.TYPE_NEW_CONVERSATION);
	}

	public static void pushPraise(long toUid,long dynamic_id) {
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("dynamic_id", String.valueOf(dynamic_id));

		String msg = "有人赞了你的图片！";
		ext.put("msg", msg);
		ext.put("dynamic_id", String.valueOf(dynamic_id));
		ext.put("type", PushMsgType.TYPE_RECEIVE_PRAISE);
		Main.sendCmdMessage(new String[] { String.valueOf(toUid) },ext);
	}

	public static void pushComment(long toUid,DynamicComment comment) {
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
		Object obj=Main.sendCmdMessage(new String[] { String.valueOf(toUid) }, ext);
		System.out.print(obj);
	}
//
	public static void pushGift(String from_nick_name,long to_user_id) {
		// 通知对方收到某某的礼物
		Map<String, String> ext = new HashMap<String, String>();
		ext = new HashMap<String, String>();
		ext.put("msg", from_nick_name+"赠送了一个礼物给你");
		ext.put("type",PushMsgType.TYPE_RECEIVE_GIFT);
		Main.sendCmdMessage(new String[] { String.valueOf(to_user_id) }, ext);
	}

	
	public static void main(String[] args) {
		pushLike(133258);
	}
}
