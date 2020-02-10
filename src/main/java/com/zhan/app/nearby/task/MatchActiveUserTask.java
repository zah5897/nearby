package com.zhan.app.nearby.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.ImagePathUtil;

@Component
public class MatchActiveUserTask {

	@Autowired
	private UserService userService;

	@Async
	public void newRegistMatch(BaseUser newRegisterUser) {
		int sex = 0;
		if ("0".equals(newRegisterUser.getSex())) {
			sex = 1;
		}
		List<BaseUser> users = userService.getActiveUser(sex);
		if (users.size() > 0) {
			BaseUser user = users.get(0);
			HX_SessionUtil.makeChatSession(newRegisterUser, user, 0);
		}
	}

	@Async
	public void longTimeNotOpenMatch(BaseUser curUser) {
		int sex = 0;
		if ("0".equals(curUser.getSex())) {
			sex = 1;
		}
		List<BaseUser> users = userService.getActiveUser(sex);
		if (users.size() > 0) {
			BaseUser user = users.get(0);
			HX_SessionUtil.makeChatSession(curUser, user, 0);
		}
	}

	// 鏈�杩�3涓湀鍐呮墦寮�杩嘺pp鐨勭敤鎴凤紝鍖归厤涓�浜涙鍦ㄦ椿璺冪殑鐢ㄦ埛
	@Async
	public void shortTimeOpenMatch(BaseUser curUser) {
		int sex = 0;
		if ("0".equals(curUser.getSex())) {
			sex = 1;
		}
		List<BaseUser> users = userService.get2daysLoginUser(curUser.getUser_id(),sex, 90, 1);
		ImagePathUtil.completeAvatarPath(curUser, true);
		for (BaseUser u : users) {
			String msg = Main.getRandomMsg();
			userService.saveMatchLog(curUser.getUser_id(), u.getUser_id());
			HX_SessionUtil.matchCopyDraw(curUser, u.getUser_id(), msg);
			HX_SessionUtil.matchCopyDraw(ImagePathUtil.completeAvatarPath(u, true), curUser.getUser_id(), msg);
		}
	}
	@Async
	public void matchActiveUsers() {
		userService.matchActiveUsers();
	}
 
}
