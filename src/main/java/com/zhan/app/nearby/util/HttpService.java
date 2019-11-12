package com.zhan.app.nearby.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.util.TextUtils;

import com.zhan.app.nearby.exception.ERROR;

public class HttpService {

	private static String MODULE_RULE_URL;
	private static String MODULE_PAY_URL;
	private static String MODULE_COINS_QUERY_URL;
	private static String MODULE_ORDER_A_EXTRA;
	
	private  static String MODULE_ORDER_URL;

	// ----------------------购买规则管理---------------------------
	public static Map<?, ?> deleteRule(int rule_id) {
		if (TextUtils.isEmpty(MODULE_RULE_URL)) {
			MODULE_RULE_URL = loadProperty("PAY_MODULE_RULE_URL");
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(rule_id));
		Map<String, Object> result = HttpClientUtils.post(MODULE_RULE_URL + "/del", params);
		if (result != null) {
			return result;
		}
		return nullResult(ERROR.ERR_FAILED);
	}

	public static Map<?, ?> listRule(String aid) {
		if (TextUtils.isEmpty(MODULE_RULE_URL)) {
			MODULE_RULE_URL = loadProperty("PAY_MODULE_RULE_URL");
		}
		String url = null;
		if (!TextUtils.isEmpty(aid)) {
			url = MODULE_RULE_URL + "/list";
		} else {
			url = MODULE_RULE_URL + "/list_all";
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("aid", aid);
		Map<String, Object> result = HttpClientUtils.post(url, params);
		if (result != null) {
			return result;
		}
		return nullResult(ERROR.ERR_FAILED);
	}

	public static Map<?, ?> saveRule(int id, String name, int coins, int coins_free, int rmb, String description,
			String aid, String app_name) {
		if (TextUtils.isEmpty(MODULE_RULE_URL)) {
			MODULE_RULE_URL = loadProperty("PAY_MODULE_RULE_URL");
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", String.valueOf(id));
		try {
			params.put("name", URLEncoder.encode(name, "utf-8"));
			params.put("description", URLEncoder.encode(description, "utf-8"));
			params.put("aid", aid);
			params.put("app_name", URLEncoder.encode(app_name, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.put("coins", String.valueOf(coins));
		params.put("coins_free", String.valueOf(coins_free));
		params.put("rmb", String.valueOf(rmb));

		Map<String, Object> result = HttpClientUtils.post(MODULE_RULE_URL + "/save", params);
		if (result != null) {
			return result;
		}
		return nullResult(ERROR.ERR_FAILED);
	}
//-------------------购买-------------------------------

	/**
	 * 购买礼物
	 * 
	 * @param user_id
	 * @param aid
	 * @param int_amount
	 * @param gift_id
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> buy(long user_id, String aid, int int_amount, Object gift_id) {
		return minusCoins(user_id, aid, int_amount, gift_id);
	}

	/**
	 * 购买vip
	 * 
	 * @param user_id
	 * @param aid
	 * @param int_amount
	 * @param gift_id
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> buyVIP(long user_id, String aid, int  rule_id, String  subject,int amount,int type) {
		
		if (TextUtils.isEmpty(MODULE_ORDER_URL)) {
			MODULE_ORDER_URL = loadProperty("PAY_MODULE_ORDER_URL");
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", String.valueOf(user_id));
		params.put("aid", aid);
		params.put("subject", subject);
		params.put("rule_id", String.valueOf(rule_id));
		params.put("amount", String.valueOf(amount));
		params.put("type", String.valueOf(type));

		Map<String, Object> result = HttpClientUtils.post(MODULE_ORDER_URL, params);
		if (result != null) {
			return result;
		}
		return nullResult(ERROR.ERR_FAILED);
	}

	/**
	 * 消耗金币
	 * 
	 * @param user_id
	 * @param aid
	 * @param int_amount
	 * @param ext
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> minusCoins(long user_id, String aid, int int_amount, Object ext) {
		if (TextUtils.isEmpty(MODULE_PAY_URL)) {
			MODULE_PAY_URL = loadProperty("PAY_MODULE_PAY_URL");
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", String.valueOf(user_id));
		params.put("aid", aid);
		params.put("int_amount", String.valueOf(int_amount));
		params.put("ext", String.valueOf(ext));

		Map<String, Object> result = HttpClientUtils.post(MODULE_PAY_URL, params);
		if (result != null) {
			return result;
		}
		return nullResult(ERROR.ERR_FAILED);
	}

	/**
	 * 消耗金币
	 * 
	 * @param user_id
	 * @param aid
	 * @param int_amount
	 * @param ext
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> modifyUserExtra(long user_id, String aid, int count, int addOrMinus) {
		if (TextUtils.isEmpty(MODULE_ORDER_A_EXTRA)) {
			MODULE_ORDER_A_EXTRA = loadProperty("PAY_MODULE_ORDER_A_EXTRA");
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("id_user", String.valueOf(user_id) + "$");
		params.put("aid", aid);
		params.put("extra", String.valueOf(count) + "_");
		params.put("type", String.valueOf(addOrMinus));

		Map<String, Object> result = HttpClientUtils.post(MODULE_ORDER_A_EXTRA, params);
		if (result != null) {
			return result;
		}
		return nullResult(ERROR.ERR_FAILED);
	}

	// ----------------------查询用户coins-------------------------
	public static Map<String, Object> queryUserCoins(long user_id, String aid) {
		if (TextUtils.isEmpty(MODULE_COINS_QUERY_URL)) {
			MODULE_COINS_QUERY_URL = loadProperty("PAY_MODULE_COINS_QUERY_URL");
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", String.valueOf(user_id));
		params.put("aid", aid);
		Map<String, Object> result = HttpClientUtils.post(MODULE_COINS_QUERY_URL, params);
		if (result != null) {
			return result;
		}
		return nullResult(ERROR.ERR_FAILED);

	}

	/**
	 * 加载配置信息
	 * 
	 * @param propertyName
	 * @return
	 */
	private static String loadProperty(String propertyName) {
		Properties prop = PropertiesUtil.load("app.properties");
		return PropertiesUtil.getProperty(prop, propertyName);
	}

	private static Map<String, Object> nullResult(ERROR err) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", err.getValue());
		map.put("msg", err.getErrorMsg());
		return map;
	}
}
