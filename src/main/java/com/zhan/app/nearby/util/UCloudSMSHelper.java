package com.zhan.app.nearby.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.zhan.app.nearby.util.ucloud.usms.client.DefaultUSMSClient;
import com.zhan.app.nearby.util.ucloud.usms.client.USMSClient;
import com.zhan.app.nearby.util.ucloud.usms.model.SendUSMSMessageParam;
import com.zhan.app.nearby.util.ucloud.usms.model.SendUSMSMessageResult;
import com.zhan.app.nearby.util.ucloud.usms.pojo.USMSConfig;

import cn.ucloud.common.pojo.Account;
import cn.ucloud.common.pojo.BaseResponseResult;

public class UCloudSMSHelper {
	private static String PrivateKey;
	private static String PublicKey;
	private static String sig;
	private static String projectID;
	static USMSClient client;

	private static void initClient() {
		if (client == null) {
			 // 加载配置信息
			Properties prop = PropertiesUtil.load("app.properties");
			PrivateKey=prop.getProperty("ucloud.sms.privateKey");
			PublicKey=prop.getProperty("ucloud.sms.publicKey");
			sig=prop.getProperty("ucloud.sms.sig");
			projectID=prop.getProperty("ucloud.sms.projectID");
			client = new DefaultUSMSClient(new USMSConfig(new Account(PrivateKey, PublicKey)));
		}
	}

	private static boolean sendSMS(String phoneNumber, String templateId, String smsContent) {
		initClient();

		List<String> phoneNumbers = new ArrayList<>();
		phoneNumbers.add(phoneNumber);
		SendUSMSMessageParam param = new SendUSMSMessageParam(phoneNumbers, templateId);
		param.setSigContent(sig);
		param.setProjectId(projectID);
		List<String> templateParams = new ArrayList<>();
		templateParams.add(smsContent);
		param.setTemplateParams(templateParams);
		SendUSMSMessageResult result;
		try {
			result = client.sendUSMSMessage(param);
			return handleResult(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean handleResult(BaseResponseResult result) {
		// result example :{"Action":"SendUSMSMessageResponse","Message":"Send
		// success","RetCode":0,"SessionNo":"fbcb8979-ec63-4db6-87b9-b40dd32b570f"}
		String RetCode = JSONUtil.jsonToMap(result.getResponseContent()).get("RetCode").toString();
		return "0".equals(RetCode);
	}

	public static void main(String[] args) {
		smsRegist("13262510792", "123456");
	}

	public static boolean smsRegist(String mobile, String code) {
		return sendSMS(mobile, "UTA19111264DD52", code);
	}

	public static boolean smsResetPwd(String mobile, String code) {
		return sendSMS(mobile, "UTA191112D6E3A4", code);
	}

	public static boolean smsGameRegist(String mobile, String code) {
		return sendSMS(mobile, "UTA1911126F832B", code);
	}

	public static boolean smsResetGamePwd(String mobile, String code) {
		return smsResetPwd(mobile, code);
	}

	public static boolean smsExchangeCode(String mobile, String code) {
		return sendSMS(mobile, "UTA19111243A287", code);
	}

}
