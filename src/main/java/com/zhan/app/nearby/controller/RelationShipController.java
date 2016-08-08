package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.UserInfoService;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@RestController
@RequestMapping("/relationship")
public class RelationShipController {
	private static Logger log = Logger.getLogger(RelationShipController.class);
	@Resource
	private UserCacheService userCacheService;
	@Resource
	private UserInfoService userInfoService;

	@RequestMapping("black")
	public ModelMap found(long user_id, String token, String with_user_id) {
		if (user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST);
		}
		if (TextUtils.isEmpty(with_user_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		if (TextUtils.isEmpty(token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (!token.equals(userCacheService.getCacheToken(user_id))) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		String[] with_ids = with_user_id.split(",");
		for (String id : with_ids) {
			try {
				long with_user = Long.parseLong(id);
				if (user_id == with_user) {
					continue;
				}
				userInfoService.updateRelationshipNOHX(new User(user_id), with_user, Relationship.BLACK.ordinal());
			} catch (NumberFormatException e) {
			}
		}
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("unblack")
	public ModelMap unblack(long user_id, String token, String with_user_id) {
		if (user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST);
		}
		if (TextUtils.isEmpty(with_user_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		if (TextUtils.isEmpty(token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (!token.equals(userCacheService.getCacheToken(user_id))) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		String[] with_ids = with_user_id.split(",");
		for (String id : with_ids) {
			try {
				long with_user = Long.parseLong(id);
				if (user_id == with_user) {
					continue;
				}
				userInfoService.updateRelationshipNOHX(new User(user_id), with_user, Relationship.DEFAULT.ordinal());
			} catch (NumberFormatException e) {
			}
		}
		return ResultUtil.getResultOKMap();
	}
}
