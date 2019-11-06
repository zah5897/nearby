package com.zhan.app.nearby.controller;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zhan.app.nearby.bean.user.LocationUser;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.MD5Util;

@Controller
@RequestMapping("/exchange")
public class WebExchageController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/")
	public ModelAndView index(HttpServletRequest request, long user_id, String token) {
		if (userService.checkLogin(user_id, token)) {
			return new ModelAndView("exchange/index");
		} else {
			return new ModelAndView("exchange/user_login");
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView dologin(HttpServletRequest request, String mobile, String password) {

		LocationUser user = userService.findLocationUserByMobile(mobile);
		if (user == null) {
			return new ModelAndView().addObject("err_info", "该账号不存在");
		}
		if (userService.getUserState(user.getUser_id()) == FoundUserRelationship.GONE.ordinal()) {
			return new ModelAndView().addObject("err_info", "该账号应被举报而被锁定，无法登录");
		}

		String md5;
		try {
			md5 = MD5Util.getMd5(password);
			if (!md5.equals(user.getPassword())) {
				return new ModelAndView().addObject("err_info", "密码错误");
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new ModelAndView().addObject("err_info", "密码校验异常");
		}
		return new ModelAndView("exchange/index").addObject("user_id", user.getUser_id()).addObject("token", user.getToken());
	}

}
