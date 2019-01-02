package com.zhan.app.nearby.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.zhan.app.nearby.dao.SystemDao;


public class IPUtil {
	private static Logger log = Logger.getLogger(IPUtil.class);
	public static List<String> ipBlackList;
	
	public static String getIpAddress(HttpServletRequest request) {
		String ipAddress = null;
		// ipAddress = this.getRequest().getRemoteAddr();
		ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if (ipAddress.equals("127.0.0.1")) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					log.error(e.getMessage());
				}
				ipAddress = inet.getHostAddress();
			}

		}

		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
	
	
	public static boolean addIPBlack(String ip) {
		SystemDao dao=SpringContextUtil.getBean("systemDao");
		if(ipBlackList==null) {
			ipBlackList=dao.loadBlackIPs();
		}
		if(!ipBlackList.contains(ip)) {
			ipBlackList.add(ip);
			dao.insertIpToBlack(ip);
			return true;
		}
		return false;
	}

	
	public static void removeBlackIP(String ip) {
		SystemDao dao=SpringContextUtil.getBean("systemDao");
		if(ipBlackList==null) {
			ipBlackList=dao.loadBlackIPs();
		}
		ipBlackList.remove(ip);
		dao.deleteFromBlackIps(ip);
	}
	
	public static List<String> getIpBlackList() {
		if(ipBlackList==null) {
			SystemDao dao=SpringContextUtil.getBean("systemDao");
			if(ipBlackList==null) {
				ipBlackList=dao.loadBlackIPs();
			}
		}
		return ipBlackList;
	}
	
	public static boolean doBlackIPFilter(HttpServletRequest request) {
		String ip=getIpAddress(request);
		if(getIpBlackList().contains(ip)) {
			return true;
		}
		return false;
	}
	
	
	
	public static String getCityName(String ip) {
		String city_name_url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;

		String result = HttpUtil.sendGet(city_name_url, null);
		String cityName = null;
		if (!TextUtils.isEmpty(result)) {
			Map<String, Object> address = JSONUtil.jsonToMap(result);
			if (address != null) {
				cityName = address.get("city").toString();
			}
		}
		return cityName;
	}

}
