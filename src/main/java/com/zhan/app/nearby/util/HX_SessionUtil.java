package com.zhan.app.nearby.util;

import java.util.HashMap;
import java.util.Map;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.PushMsgType;

public class HX_SessionUtil {
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

		String msg = Main.getRandomMsg();
		Main.sendTxtMessage(String.valueOf(user.getUser_id()), new String[] { String.valueOf(with_user.getUser_id()) },
				msg, ext, PushMsgType.TYPE_NEW_CONVERSATION);
		// 发送给自己
		ext = new HashMap<String, String>();
		ext.put("nickname", with_user.getNick_name());
		ext.put("avatar", with_user.getAvatar());
		ext.put("origin_avatar", with_user.getOrigin_avatar());
		if (bottle_id > 0) {
			ext.put("bottle_id", String.valueOf(bottle_id));
		}
		Main.sendTxtMessage(String.valueOf(with_user.getUser_id()), new String[] { String.valueOf(user.getUser_id()) },
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
		Main.sendTxtMessage(String.valueOf(user.getUser_id()), new String[] { String.valueOf(with_user.getUser_id()) },
				expressMsg, ext, PushMsgType.TYPE_NEW_CONVERSATION);

	}

	public static void makeChatSession(BaseUser user, BaseUser with_user) {
		makeChatSession(user, with_user, 0);
	}
	
	
	public static void matchCopyDraw(BaseUser fromU,long toU,String msg) {
			Map<String, String> ext = new HashMap<String, String>();
			ext.put("nickname", fromU.getNick_name());
			ext.put("avatar", fromU.getAvatar());
			ext.put("origin_avatar", fromU.getOrigin_avatar());
			Main.sendTxtMessage(String.valueOf(fromU.getUser_id()), new String[] { String.valueOf(toU) }, msg, ext,
					PushMsgType.TYPE_NEW_CONVERSATION);
		}
	

}
