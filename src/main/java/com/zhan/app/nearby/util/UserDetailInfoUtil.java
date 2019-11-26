package com.zhan.app.nearby.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.user.DetailUser;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.UserService;

public class UserDetailInfoUtil {
	public static ModelMap getDetailInfo(UserService userService, Long user_id, int count) {
		if (user_id == null || user_id < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户ID异常");
		}
		DetailUser user = userService.getUserDetailInfo(user_id);
		if (user == null) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		}
		Map<String, Object> secret_me = new HashMap<String, Object>();
		 
		//
		Map<String, Object> userJson = new HashMap<String, Object>();
		userJson.put("about_me", user.getBasicUserInfoMap());
		userJson.put("secret_me", secret_me);
		// userJson.put("my_tags", user.getMy_tags() != null ? user.getMy_tags()
		// : new ArrayList<Tag>());
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("user", userJson);
		return result;
	}
}
