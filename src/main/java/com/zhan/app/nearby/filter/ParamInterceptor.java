package com.zhan.app.nearby.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.TextUtils;
import com.zhan.app.nearby.util.WriteJsonUtil;

public class ParamInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String _ua = request.getParameter("_ua");
		String url = request.getRequestURI();
//		System.out.println(url);
		if (url.contains("nearby/manager")) {
			return true;
		}

		if (url.contains("nearby/js")) {
			return true;
		}
		if (url.contains("nearby/images")) {
			return true;
		}
		if (url.contains("nearby/css")) {
			return true;
		}
		if (url.contains("nearby/img/")) {
			return true;
		}
		if (url.contains("nearby/avatar/")) {
			return true;
		}
		if (url.contains("nearby/gift_img/")) {
			return true;
		}
		if (url.endsWith(".html")) {
			return true;
		}
		 
		if (TextUtils.isEmpty(_ua) || _ua.length() < 10) {
			WriteJsonUtil.write(response, ERROR.ERR_NO_AGREE);
			return false;
		}
		return true;
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

}
