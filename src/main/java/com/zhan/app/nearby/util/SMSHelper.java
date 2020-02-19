package com.zhan.app.nearby.util;

import com.zhan.app.nearby.util.ucloud.usms.UCloudSMSHelper;

public class SMSHelper {

	public static void main(String[] args) {
		smsRegist("13262510792", "1234");
	}

	public static boolean smsRegist(String mobile, String code) {
//		HashMap<String, Object> result = sms("241010", mobile, code);
		return UCloudSMSHelper.smsRegist(mobile, code);
//		return isSuccess(result);
	}

	public static boolean smsResetPwd(String mobile, String code) {
		return UCloudSMSHelper.smsResetPwd(mobile, code);
//		H?ashMap<String, Object> result = sms("241014", mobile, code);
//		return isS?uccess(result);
	}
	
	public static boolean smsGameRegist(String mobile, String code) {
//		HashMap<String, Object> result = sms("241010", mobile, code);
//		return isSuccess(result);
		return UCloudSMSHelper.smsRegist(mobile, code);
	}
	
	public static boolean smsResetGamePwd(String mobile, String code) {
//		HashMap<String, Object> result = sms("241014", mobile, code);
//		return isSuccess(result);
		return UCloudSMSHelper.smsResetGamePwd(mobile, code);
	}

	public static boolean smsBindZHiFuBao(String mobile, String code) {
		return UCloudSMSHelper.smsComm(mobile, code);
	}

	public static boolean smsExchangeCode(String mobile, String code) {
//		return sms("240623", mobile, code);
		return UCloudSMSHelper.smsExchangeCode(mobile, code);
	}
	 
}
