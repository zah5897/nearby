package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Express;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.ExpressService;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@RestController
@RequestMapping("/express")
public class ExpressController {

	@Resource
	private ExpressService expressService;

	@RequestMapping("send")
	public ModelMap send(Express express) {

		if (express.getUser_id() <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (express.getTo_user_id() <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "对方不存在");
		}

		if (TextUtils.isEmpty(express.getContent()) || express.getContent().length() < 5) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "表白内容太短");
		}

		return expressService.insert(express);
	}

}
