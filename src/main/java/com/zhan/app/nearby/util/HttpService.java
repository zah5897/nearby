package com.zhan.app.nearby.util;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.RuleService;

public class HttpService {

	private static String MODULE_RULE_URL;
	private static String MODULE_PAY_URL;
	private static Logger log = Logger.getLogger(HttpService.class);

	
	//----------------------购买规则管理---------------------------
	public static Map<?, ?> deleteRule(int rule_id) {
		if (TextUtils.isEmpty(MODULE_RULE_URL)) {
			MODULE_RULE_URL = loadProperty("MODULE_RULE_URL");
		}
		String result = null;
		try {
			result = HttpUtil.sendHttpsPost(MODULE_RULE_URL + "/del?id=" + rule_id);
		} catch (Exception e) {
			log.error("删除失败" + e.getMessage());
			System.out.println("删除失败");
		}
		if (!TextUtils.isEmpty(result)) {
			Map<?, ?> map = JSONUtil.jsonToMap(result);
			return map;
		}
		return ResultUtil.getResultOKMap();
	}

	public static Map<?, ?> listRule() {
		if (TextUtils.isEmpty(MODULE_RULE_URL)) {
			MODULE_RULE_URL = loadProperty("MODULE_RULE_URL");
		}
		String result = null;
		try {
			result = HttpUtil.sendHttpsPost(MODULE_RULE_URL + "/list_all");
		} catch (Exception e) {
			log.error("获取列表失败" + e.getMessage());
			System.out.println("获取列表失败");
		}
		if (!TextUtils.isEmpty(result)) {
			Map<?, ?> map = JSONUtil.jsonToMap(result);
			return map;
		}
		return ResultUtil.getResultOKMap();
	}

	public static Map<?, ?> saveRule(int id, String name, int coins, int coins_free, int rmb, String description,
			String aid, String app_name) {
		if (TextUtils.isEmpty(MODULE_RULE_URL)) {
			MODULE_RULE_URL = loadProperty("MODULE_RULE_URL");
		}
		String result = null;
		try {
			result = HttpUtil.sendHttpsPost(MODULE_RULE_URL + "/save?id=" + id + "&name="
					+ URLEncoder.encode(name, "utf-8") + "&coins=" + coins + "&coins_free=" + coins_free + "&rmb=" + rmb
					+ "&description=" + URLEncoder.encode(description, "utf-8") + "&aid=" + aid + "&app_name="
					+ URLEncoder.encode(app_name, "utf-8"));
			return JSONUtil.jsonToMap(result);
		} catch (Exception e) {
			log.error("添加失败" + e.getMessage());
			System.out.println("添加失败");
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}
//-------------------购买-------------------------------
	
	
	public static Map<?, ?> buy(long user_id,String aid,int int_amount,int gift_id) {
		if (TextUtils.isEmpty(MODULE_PAY_URL)) {
			MODULE_PAY_URL = loadProperty("MODULE_PAY_URL");
		}
		String result = null;
		try {
			result = HttpUtil.sendHttpsPost(MODULE_PAY_URL + "?user_id=" + user_id + "&aid=" + aid + "&int_amount="
					+ int_amount + "&ext=" + gift_id);
		} catch (Exception e) {
			log.error("购买失败" + e.getMessage());
		}
		if (!TextUtils.isEmpty(result)) {
			return JSONUtil.jsonToMap(result);
		}
	   return null;
	}
	
	/**
	 * 加载配置信息
	 * @param propertyName
	 * @return
	 */
	private static String loadProperty(String propertyName) {
		Properties prop = PropertiesUtil.load("config.properties");
		return PropertiesUtil.getProperty(prop, propertyName);
	}
}
