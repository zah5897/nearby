package com.zhan.app.nearby.filter;

import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zhan.app.nearby.service.ManagerService;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.TextUtils;

public class ParamInterceptor implements HandlerInterceptor {

	public static final String ANDROID = "g";
	public static final String IOS = "a";

	@Resource
	private ManagerService managerService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws NoSuchAlgorithmException {

		String url = request.getRequestURI();

		if (IPUtil.doBlackIPFilter(request)) {
			return false;
		}
		if (url.contains("nearby/manager")) {
			String ip = IPUtil.getIpAddress(request);
			return managerService.isAllowed(ip);
		}
		if (url.contains("nearby/exchange")) {
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
		if (url.contains("nearby/bottle/draw/")) {
			return true;
		}
		if (url.contains("nearby/files/")) {
			return true;
		}
		 boolean r=isSupportSwagger(url);
		 if(r) {
			 return r;
		 }
		
		if (url.endsWith(".html")) {
			return true;
		}
		 
		String _ua = request.getParameter("_ua");
		String version = request.getParameter("version");
		String timestamp = request.getParameter("timestamp");
		String aid = request.getParameter("aid");
		return checkSecret(request,_ua, aid, version, timestamp);
	}

	
	public boolean isSupportSwagger(String url) {
		if(url.contains("swagger")) {
			return true;
		}
		if(url.contains("nearby/v2/")) {
			return true;
		}
		System.out.println(url);
		return true;
	}
	@SuppressWarnings("deprecation")
	private boolean checkSecret(HttpServletRequest request,String _ua, String aid, String version, String timestamp) throws NoSuchAlgorithmException {
//		
		if(TextUtils.isEmpty(_ua)) {
			return false;
		}
		String[] _uas = _ua.split("\\|");
		if (_uas.length < 10) {
			String deUA= URLDecoder.decode(_ua);
			_uas = deUA.split("\\|");
		}
		
		if (IOS.equals(_uas[0])) {
			if (version.compareTo("1.8.1") <= 0) {
				return true;
			}
		}
		byte[] md5 = MD5Util.getMd5Byte((aid + version + timestamp));
		byte[] base64byte = Base64.encodeBase64Chunked(md5);
		String cd = new String(base64byte);
		String paramS = _uas[_uas.length - 1];
		if (!cd.trim().equalsIgnoreCase(paramS.trim())) {
			 return false;
		}
		return true;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		String t = "1544595059984";
		String v = "1.8.2";
		String aid = "1178548652";
		byte[] md5 = MD5Util.getMd5Byte((aid + v + t));

		for (byte b : md5) {
			System.out.print(b);
			System.out.print(",");
		}
		System.out.println();
		byte[] base64byte = Base64.encodeBase64Chunked(md5);
		String cd = new String(base64byte);
		System.out.println(cd);
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

}
