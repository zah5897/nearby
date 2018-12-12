package com.zhan.app.nearby.filter;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.MD5Util;

public class ParamInterceptor implements HandlerInterceptor {

	
	public static final String ANDROID="g";
	public static final String IOS="a";
	private Base64 base64 = new Base64();
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
	
		String url = request.getRequestURI();

		if (IPUtil.doBlackIPFilter(request)) {
			return false;
		}
		// System.out.println(url);
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

		if (url.contains("nearby/files/")) {
			return true;
		}

		if (url.endsWith(".html")) {
			return true;
		}

		
		String i = request.getParameter("i");
		if("1111".equals(i)) {
			return true;
		}
		
		
		
		String _ua = request.getParameter("_ua");
		String ua = URLDecoder.decode(_ua);
 		String[] _uas = ua.split("\\|");
 		
 		
 		String version = request.getParameter("version");
 		
 		if(IOS.equals(_uas[0])) {
 			if(version.compareTo("1.8.1")<0) {
 				return true;
 			}
 		}
 		
 		String timestamp = request.getParameter("timestamp");
 		
		String aid = request.getParameter("aid");
		
		String cd = new String(base64.encodeBase64Chunked(MD5Util.getMd5Byte((aid + version + timestamp))));
		
		String paramS=_uas[_uas.length - 1];
		if (!cd.trim().equalsIgnoreCase(paramS.trim())) {
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
