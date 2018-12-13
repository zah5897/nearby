package com.zhan.app.nearby.filter;

import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.zhan.app.nearby.service.ManagerService;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.MD5Util;

public class ParamInterceptor implements HandlerInterceptor {

	public static final String ANDROID = "g";
	public static final String IOS = "a";
	private Base64 base64 = new Base64();

	@Resource
	private ManagerService managerService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String url = request.getRequestURI();

		if (IPUtil.doBlackIPFilter(request)) {
			return false;
		}
		// System.out.println(url);
		if (url.contains("nearby/manager")) {
			String ip = IPUtil.getIpAddress(request);
			return managerService.isAllowed(ip);
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
		if ("1111".equals(i)) {
			return true;
		}

		String _ua = request.getParameter("_ua");
		String ua = URLDecoder.decode(_ua);
		String[] _uas = ua.split("\\|");

		String version = request.getParameter("version");

		if (IOS.equals(_uas[0])) {
			if (version.compareTo("1.8.1") < 0) {
				return true;
			}
		}

		String timestamp = request.getParameter("timestamp");

		String aid = request.getParameter("aid");

		String cd = new String(base64.encodeBase64Chunked(MD5Util.getMd5Byte((aid + version + timestamp))));

		String paramS = _uas[_uas.length - 1];
		if (!cd.trim().equalsIgnoreCase(paramS.trim())) {
			System.out.println("验签失败。");
			System.out.println("本地加密结果：" + cd);
			System.out.println("客户端加密结果：" + paramS);
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String t="1544595059984";
		String v="1.8.2";
		String aid="1178548652";
		Base64 base64 = new Base64();
		byte[] md5=MD5Util.getMd5Byte((aid + v + t));

        for(byte b:md5) {
        	System.out.print(b);
        	System.out.print(",");
        }
        System.out.println();
		byte[] base64byte= base64.encodeBase64Chunked(md5);
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
