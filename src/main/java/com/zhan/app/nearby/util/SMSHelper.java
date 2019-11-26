package com.zhan.app.nearby.util;

import java.util.HashMap;
import java.util.Set;

import com.cloopen.rest.sdk.CCPRestSmsSDK;

public class SMSHelper {

	public static void main(String[] args) {
		smsRegist("13262510792", "1234");
	}

	public static boolean smsRegist(String mobile, String code) {
		HashMap<String, Object> result = sms("241010", mobile, code);
		return isSuccess(result);
	}

	public static boolean smsResetPwd(String mobile, String code) {
		HashMap<String, Object> result = sms("241014", mobile, code);
		return isSuccess(result);
	}
	
	public static boolean smsGameRegist(String mobile, String code) {
		HashMap<String, Object> result = sms("241010", mobile, code);
		return isSuccess(result);
	}
	
	public static boolean smsResetGamePwd(String mobile, String code) {
		HashMap<String, Object> result = sms("241014", mobile, code);
		return isSuccess(result);
	}

	public static boolean smsBindZHiFuBao(String mobile, String code) {
		HashMap<String, Object> result = sms("240624", mobile, code);
		return isSuccess(result);
	}

	public static HashMap<String, Object> smsExchangeCode(String mobile, String code) {
		return sms("240623", mobile, code);
	}

	private static HashMap<String, Object> sms(String tempId, String mobile, String code) {
		HashMap<String, Object> result = null;
		CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
		restAPI.init("app.cloopen.com", "8883");
		// 初始化服务器地址和端口，生产环境配置成app.cloopen.com，端口是8883.
		restAPI.setAccount("8a216da85982d9da015986d386eb0186", "5c5ade785c794e968eec8aacc894fb68");
		// 初始化主账号名称和主账号令牌，登陆云通讯网站后，可在控制首页中看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN。
		restAPI.setAppId("8a216da85982d9da015986d3887b018d");
		// 请使用管理控制台中已创建应用的APPID。
		result = restAPI.sendTemplateSMS(mobile, tempId, new String[] { code });
		return result;
	}

	@SuppressWarnings("unchecked")
	public static boolean isSuccess(HashMap<String, Object> result) {
		if (result == null) {
			return false;
		}
		if ("000000".equals(result.get("statusCode"))) {
			// 正常返回输出data包体信息（map）
			HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for (String key : keySet) {
				data.get(key);
			}
			return true;
		}
		return false;
	}
}
