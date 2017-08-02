package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/system")
public class SystemController {
	@Resource
	private UserService userService;
	// @Resource
	// private SystemService systemService;

	@RequestMapping("report")
	public ModelMap report(long user_id, String token, long report_to_user_id, String report_tag_id, String content) {

		// if (user_id < 1) {
		// return ResultUtil.getResultMap(ERROR.ERR_PARAM,"用户ID异常");
		// }
		// if (report_to_user_id < 1) {
		// return ResultUtil.getResultMap(ERROR.ERR_PARAM,"被举报用户ID异常");
		// }
		//
		// if (user_id == report_to_user_id) {
		// return ResultUtil.getResultMap(ERROR.ERR_PARAM,"不能自己举报自己!");
		// }
		// //
		// if (TextUtils.isEmpty(token)) {
		// return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		// }
		// User user = userService.getUser(user_id);
		// //
		// if (user == null) {
		// return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST, "该用户不存在！");
		// }
		// // else if (!token.equals(user.getToken())) {
		// // return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		// // }
		// int count = systemService.report(user_id, report_to_user_id,
		// report_tag_id, content);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("prootl")
	public String prootl() {
		return "prootl";
	}

}
