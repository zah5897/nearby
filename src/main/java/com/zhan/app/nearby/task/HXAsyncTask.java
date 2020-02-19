package com.zhan.app.nearby.task;

import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.easemob.server.example.Main;
import com.easemob.server.example.comm.wrapper.ResponseWrapper;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.exception.AppException;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.MD5Util;

@Component
public class HXAsyncTask {

	 
	@Async
	public   void makeChatSession(BaseUser user, BaseUser with_user, long bottle_id) {
		HX_SessionUtil.makeChatSession(user, with_user,bottle_id);
	}

	@Async
	public   void makeChatSessionSingle(BaseUser user, BaseUser with_user, String expressMsg) {
		HX_SessionUtil.makeChatSessionSingle(user, with_user, expressMsg);
	}
	@Async
	public   void makeChatSession(BaseUser user, BaseUser with_user) {
		HX_SessionUtil.makeChatSession(user, with_user, 0);
	}
	@Async
	public   void matchCopyDraw(BaseUser fromU, long toU, String msg) {
		HX_SessionUtil.matchCopyDraw(fromU, toU, msg);
	}
	@Async
	public   void pushPraise(long toUid,long dynamic_id) {
		HX_SessionUtil.pushPraise(toUid, dynamic_id);
	}
	@Async
	public   void pushComment(long toUid,DynamicComment comment) {
		HX_SessionUtil.pushComment(toUid, comment);
	}
	@Async
	public   void pushLike(long toUid) {
		HX_SessionUtil.pushLike(toUid);
	}
	@Async
	public   void pushGift(String from_nick_name,long to_user_id) {
		// 通知对方收到某某的礼物
		HX_SessionUtil.pushGift(from_nick_name, to_user_id);
	}
	
	@Async
	public void sendReplayBottle(BaseUser user, long target, String msg, Map<String, String> ext,
			String typeNewConversation) {
		Main.sendTxtMessage(user, new String[] { String.valueOf(target) }, msg, ext,
				typeNewConversation);
	}
	@Async
	public void getRedPackageSendMsg(BaseUser user, long bottle_id, long target, int coin) {
		Map<String, String> ext = new HashMap<String, String>();
		ext.put("nickname", user.getNick_name());
		ext.put("avatar", user.getAvatar());
		ext.put("origin_avatar", user.getOrigin_avatar());
		ext.put("bottle_id", String.valueOf(bottle_id));
		Main.sendTxtMessage(user, new String[] { String.valueOf(target) },
				user.getNick_name() + "领取了你的扇贝", ext, PushMsgType.TYPE_NEW_CONVERSATION);
	}
	@Async
	public void disconnectUser(String valueOf) {
		Main.disconnectUser(valueOf);	
	}
	@Async
	public void disconnect(String valueOf) {
		// TODO Auto-generated method stub
		Main.disconnect(valueOf);	
	}
	@Async
	public void sendWelcome(String[] users, String msgTxt, Map<String, String> ext,
			String TYPE) {
		Main.sendTxtMessageByAdmin(users, msgTxt, ext, PushMsgType.TYPE_WELCOME);
	}
	@Async
	public void registHXUser(BaseUser user) {
		try {
			String id = String.valueOf(user.getUser_id());
			String password = MD5Util.getMd5_16(id);
			Object resutl = Main.registUser(id, password, user.getNick_name());
			if (resutl != null) {
				if (resutl instanceof ResponseWrapper) {
					ResponseWrapper response = (ResponseWrapper) resutl;
					if (response.getResponseStatus() != 200) {
						//环信接口调用失败
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Async
	public void registHXThrowException(BaseUser user) {
		try {
			String id = String.valueOf(user.getUser_id());
			String password = MD5Util.getMd5_16(id);
			Object resutl = Main.registUser(id, password, user.getNick_name());
			if (resutl != null) {
				if (resutl instanceof ResponseWrapper) {
					ResponseWrapper response = (ResponseWrapper) resutl;
					if (response.getResponseStatus() != 200) {
						throw new AppException(ERROR.ERR_SYS, new RuntimeException("鐜俊娉ㄥ唽澶辫触"));
					}
				}
			}
		} catch (Exception e) {
			throw new AppException(ERROR.ERR_SYS, new RuntimeException("鐜俊娉ㄥ唽澶辫触"));
		}
	}
	
	@Async
	public void sendMessage(BaseUser user,long to, String msgTxt) {
		Main.sendTxtMessage(user, new String[] {String.valueOf(to)}, msgTxt, null, PushMsgType.TYPE_NEW_CONVERSATION);
	}
	 
}
