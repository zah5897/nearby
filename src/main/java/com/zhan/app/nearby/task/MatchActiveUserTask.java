package com.zhan.app.nearby.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.HX_SessionUtil;

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
}
