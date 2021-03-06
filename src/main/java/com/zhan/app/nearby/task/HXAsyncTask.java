package com.zhan.app.nearby.task;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.easemob.server.example.HXHistoryMsg;
import com.easemob.server.example.comm.wrapper.ResponseWrapper;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.exception.AppException;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.HXService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.SpringContextUtil;

@Component
public class HXAsyncTask {

	@Autowired
	private HXService hxService;

	@Async
	public void replayBottle(BaseUser user, BaseUser with_user, Bottle bottle) {
		HX_SessionUtil.replayBottle(user, with_user, bottle);
	}

	@Async
	public void replayBottleSingle(BaseUser user, BaseUser with_user, Bottle bottle, String msg) {
		HX_SessionUtil.replayBottleSingle(user, with_user, bottle, msg);
	}

	@Async
	public void replayRedPackageBottleSingle(BaseUser user, long to, Bottle bottle, int coin) {
		HX_SessionUtil.replayRedPackageBottleSingle(user, to, bottle, coin);
	}

	@Async
	public void createChatSessionRandMsg(BaseUser user, BaseUser with_user) {
		HX_SessionUtil.createChatSession(user, with_user, null);
	}

	@Async
	public void createChatSession(BaseUser user, BaseUser with_user, String msg) {
		HX_SessionUtil.createChatSession(user, with_user, msg);
	}

	@Async
	public void createExpressSession(BaseUser user, BaseUser with_user, String msg) {
		HX_SessionUtil.createExpressSession(user, with_user, msg);
	}

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

	@Async
	public void disconnectUser(String valueOf) {
		HX_SessionUtil.disconnectUser(valueOf);
	}

	@Async
	public void disconnect(String uid) {
		HX_SessionUtil.disconnect(uid);
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
						throw new AppException(ERROR.ERR_SYS, new RuntimeException("环信注册失败"));
					}
				}
			}
		} catch (Exception e) {
			throw new AppException(ERROR.ERR_SYS, new RuntimeException("环信注册失败"));
		}
	}

	@Async
	public void exportChatMessages() {
		String timePoint = DateTimeUtil.getMessageHistoryTimePoint();
		List<HXHistoryMsg> msgs = HX_SessionUtil.exportChatMessages(timePoint);
		for (HXHistoryMsg msg : msgs) {
			try {
				hxService.insert(msg);
			} catch (Exception e) {
			}
		}
	}

	@Async
	public void notifyOnlineUserCloseChatPage() {
		UserService userService = SpringContextUtil.getBean("userService");
		List<String> ids = userService.getLatestLoginUserIds();
		handleCloseChatPageNotify(ids);

	}

	private void handleCloseChatPageNotify(List<String> ids) {
		if (ids.isEmpty()) {
			return;
		}
		List<String> subIds = ids;
		if (ids.size() >= 1000) {
			subIds = ids.subList(0, 999);
		}
		String[] idArray = new String[subIds.size()];
		subIds.toArray(idArray);
		HX_SessionUtil.pushCloseChatPage(idArray);
		ids.removeAll(subIds);
		handleCloseChatPageNotify(ids);
	}

}
