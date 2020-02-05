package com.zhan.app.nearby.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.SpringContextUtil;
import com.zhan.app.nearby.util.TextUtils;
import com.zhan.app.nearby.util.baidu.FaceCheckHelper;
import com.zhan.app.nearby.util.baidu.ImgCheckHelper;

@Component
public class FaceCheckTask {
	@Autowired
	private UserDynamicService userDynamicService;
	@Autowired
	private UserService userService;

	@Async
	public void doCheckFace(BaseUser baseUser) {
		if (baseUser == null || TextUtils.isEmpty(baseUser.getAvatar()) || !baseUser.getAvatar().startsWith("http")) {
			return;
		}
		int faceCount = FaceCheckHelper.instance.checkFace(baseUser.getAvatar());
		if (faceCount > 0) {
			UserService userService = SpringContextUtil.getBean("userService");
			userService.updateAvatarIsFace(baseUser.getUser_id(), 1);
			userService.addRecommendAndMeetBottle(baseUser.getUser_id());
		}
	}

	@Async
	public void doCheckImg() {
		//检查用户发布的动态图片
//		List<UserDynamic> dys = userDynamicService.getDyanmicByState(1, 200, DynamicState.T_CREATE);
//		for (UserDynamic dy : dys) {
//			String localName=dy.getLocal_image_name();
//			ImagePathUtil.completeDynamicPath(dy, false);
//			int result  = ImgCheckHelper.instance.checkImg(dy.getThumb()); //-1 为接口异常，0为违规图片  ，1正常图片
//			if (result==1) {
//				userDynamicService.updateDynamicState(dy.getId(), DynamicState.T_FORMAL);
//			} else  if(result==0){
//				userDynamicService.updateDynamicImgToIllegal(dy.getId());
//				//删除ufile 文件
//				UFileUtil.delFileexecuteAsync(localName, UFileUtil.BUCKET_IMAGES);
//			}
//		}
		//检查用户相册
		List<BaseUser> users=userService.listConfirmAvatars(0, 100, 1, null);
		for(BaseUser u:users) {
			String localName=u.getAvatar();
			ImagePathUtil.completeAvatarPath(u, false);
			int result  = ImgCheckHelper.instance.checkImg(u.getAvatar()); //-1 为接口异常，0为违规图片  ，1正常图片
			if (result==0) {
				 userService.editAvatarStateToIllegal(u.getUser_id(), localName);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}