package com.zhan.app.nearby.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.SpringContextUtil;
import com.zhan.app.nearby.util.TextUtils;
import com.zhan.app.nearby.util.baidu.FaceCheckHelper;

@Component
public class FaceCheckTask {
	
	@Async
	public void doCheck(BaseUser baseUser) {
		if(baseUser==null||TextUtils.isEmpty(baseUser.getAvatar())||!baseUser.getAvatar().startsWith("http")) {
			return;
		}
		int faceCount=FaceCheckHelper.instance.checkFace(baseUser.getAvatar());
		if(faceCount>0) {
			UserService userService =SpringContextUtil.getBean("userService");
			userService.updateAvatarIsFace(baseUser.getUser_id(), 1);
			userService.addRecommendAndMeetBottle(baseUser.getUser_id());
		}
	}
}
