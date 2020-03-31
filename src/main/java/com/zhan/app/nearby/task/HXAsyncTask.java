package com.zhan.app.nearby.task;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.easemob.server.example.comm.wrapper.ResponseWrapper;
import com.zhan.app.nearby.bean.Bottle;
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
	public void replayBottle(BaseUser user, BaseUser with_user, Bottle bottle) {
		HX_SessionUtil.replayBottle(user, with_user, bottle);
	}
	@Async
	public void replayBottleSingle(BaseUser user, BaseUser with_user, Bottle bottle,String msg) {
		HX_SessionUtil.replayBottleSingle(user, with_user, bottle,msg);
	}
	@Async
	public void replayRedPackageBottleSingle(BaseUser user, long to,Bottle bottle,int coin) {
		HX_SessionUtil.replayRedPackageBottleSingle(user, to, bottle, coin);
	}
	@Async
	public void createChatSessionRandMsg(BaseUser user, BaseUser with_user) {
		HX_SessionUtil.createChatSession(user, with_user,null);
	}

	@Async
	public void createChatSession(BaseUser user, BaseUser with_user,String msg) {
		HX_SessionUtil.createChatSession(user, with_user,msg);
	}
	
	@Async
	public void createExpressSession(BaseUser user, BaseUser with_user,String msg) {
		HX_SessionUtil.createExpressSession(user, with_user,msg);
	}
	
//
//	@Async
//	public void makeChatSession(BaseUser user, BaseUser with_user, long bottle_id) {
//		HX_SessionUtil.makeChatSession(user, with_user, bottle_id);
//	}

//	@Async
//	public void makeChatSessionSingle(BaseUser user, BaseUser with_user, String expressMsg) {
//		HX_SessionUtil.makeChatSessionSingle(user, with_user, expressMsg);
//	}

//	@Async
//	public void makeChatSession(BaseUser user, BaseUser with_user) {
//		HX_SessionUtil.makeChatSession(user, with_user, 0);
//	}


	@Async
	public void pushPraise(long toUid, long dynamic_id) {
		HX_SessionUtil.pushPraise(toUid, dynamic_id);
	}

	@Async
	public void pushComment(long toUid, DynamicComment comment) {
		HX_SessionUtil.pushComment(toUid, comment);
	}

	@Async
	public void pushLike(long toUid) {
		HX_SessionUtil.pushLike(toUid);
	}

	@Async
	public void pushGift(String from_nick_name, long to_user_id) {
		// 通知对方收到某某的礼物
		HX_SessionUtil.pushGift(from_nick_name, to_user_id);
	}

//	@Async
//	public void sendReplayBottle(BaseUser user, long target, String msg, Map<String, String> ext,
//			String typeNewConversation) {
//		HX_SessionUtil.sendReplayBottle(user, target, msg, ext, typeNewConversation);
//	}

	

	@Async
	public void disconnectUser(String valueOf) {
		HX_SessionUtil.disconnectUser(valueOf);
	}

	@Async
	public void disconnect(String valueOf) {
		HX_SessionUtil.disconnect(valueOf);
	}

	@Async
	public void sendWelcome(String[] users, String msgTxt, Map<String, String> ext, String TYPE) {
		HX_SessionUtil.sendWelcome(users, msgTxt, ext, PushMsgType.TYPE_WELCOME);
	}

	@Async
	public void registHXUser(BaseUser user) {
		try {
			String id = String.valueOf(user.getUser_id());
			String password = MD5Util.getMd5_16(id);
			Object resutl = HX_SessionUtil.registUser(id, password, user.getNick_name());

			if (resutl != null) {
				if (resutl instanceof ResponseWrapper) {
					ResponseWrapper response = (ResponseWrapper) resutl;
					if (response.getResponseStatus() != 200) {
						// 环信接口调用失败
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
			Object resutl = HX_SessionUtil.registUser(id, password, user.getNick_name());
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


//	@Async
//	public void sendMessage(BaseUser user,long to, String msgTxt) {
//		Main.sendTxtMessage(user, new String[] {String.valueOf(to)}, msgTxt, null, PushMsgType.TYPE_NEW_CONVERSATION);
//	}

}
