package com.zhan.app.nearby.util;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

import org.apache.http.util.TextUtils;
import org.apache.log4j.Logger;

import com.zhan.app.nearby.exception.ERROR;

public class HttpService {

	private static String MODULE_RULE_URL;
	private static String MODULE_PAY_URL;
	private static String MODULE_COINS_QUERY_URL;
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
		}
		if (!TextUtils.isEmpty(result)) {
			Map<?, ?> map = JSONUtil.jsonToMap(result);
			return map;
		}
		return ResultUtil.getResultOKMap();
	}

	public static Map<?, ?> listRule(String aid) {
		if (TextUtils.isEmpty(MODULE_RULE_URL)) {
			MODULE_RULE_URL = loadProperty("MODULE_RULE_URL");
		}
		String result = null;
		try {
			String url=null;
			if(!TextUtils.isEmpty(aid)) {
				url=MODULE_RULE_URL + "/list?aid="+aid;
			}else {
				url=MODULE_RULE_URL + "/list_all";
			}
			result = HttpUtil.sendHttpsPost(url);
		} catch (Exception e) {
			log.error("获取列表失败" + e.getMessage());
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
		}
		return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	}
//-------------------购买-------------------------------
	
    /**
     * 购买礼物	
     * @param user_id
     * @param aid
     * @param int_amount
     * @param gift_id
     * @return
     */
 	public static Map<?, ?> buy(long user_id,String aid,int int_amount,int gift_id) {
	   return minusCoins(user_id,aid,int_amount,gift_id);
	}
 	
 	/**
 	 * 消耗金币
 	 * @param user_id
 	 * @param aid
 	 * @param int_amount
 	 * @param ext
 	 * @return
 	 */
	public static Map<?, ?> minusCoins(long user_id,String aid,int int_amount,Object ext) {
		if (TextUtils.isEmpty(MODULE_PAY_URL)) {
			MODULE_PAY_URL = loadProperty("MODULE_PAY_URL");
		}
		String result = null;
		try {
			result = HttpUtil.sendHttpsPost(MODULE_PAY_URL + "?user_id=" + user_id + "&aid=" + aid + "&int_amount="
					+ int_amount + "&ext=" + ext);
		} catch (Exception e) {
			log.error("购买失败" + e.getMessage());
		}
		if (!TextUtils.isEmpty(result)) {
			return JSONUtil.jsonToMap(result);
		}
		return null;
	}
	
	
	
	
	//----------------------查询用户coins-------------------------
	public static Map<?, ?> queryUserCoins(long user_id,String aid) {
		if (TextUtils.isEmpty(MODULE_COINS_QUERY_URL)) {
			MODULE_COINS_QUERY_URL = loadProperty("MODULE_COINS_QUERY_URL");
		}
		String result = null;
		try {
			result = HttpUtil.sendHttpsPost(MODULE_COINS_QUERY_URL + "?user_id=" + user_id + "&aid=" + aid);
		} catch (Exception e) {
			log.error("查询用户金币失败" + e.getMessage());
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
