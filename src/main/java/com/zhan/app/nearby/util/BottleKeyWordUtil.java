package com.zhan.app.nearby.util;

import java.util.Arrays;
import java.util.List;

import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.dao.SystemDao;

public class BottleKeyWordUtil {


	public static String loadKeyWold() {
		String keywords = null;
		keywords = loadKeyWoldFromCache();
		if (TextUtils.isNotBlank(keywords)) {
			return keywords;
		}
		SystemDao dao=SpringContextUtil.getBean("systemDao");
		//获取瓶子敏感词
		keywords = dao.loadFilterKeyword(0);
		if(keywords==null) {
			keywords="";
		}
		return keywords;
	}

	public static List<String> loadKeyWolds() {
		String keywords = loadKeyWold();
		if (TextUtils.isEmpty(keywords)) {
			return null;
		}
		return Arrays.asList(keywords.split(","));
	}

	public static String filterContent(String content) {
		if (TextUtils.isEmpty(content)) {
			return content;
		}
		List<String> bottleKeyWords = loadKeyWolds();
		if (bottleKeyWords != null && bottleKeyWords.size() > 0) {
			for (String key : bottleKeyWords) {
				content = content.replace(key, getStar(key.length()));
			}
		}
		return content;
	}
	
	
	public static boolean isContainsIllegalKey(String content) {
		if (TextUtils.isEmpty(content)) {
			return false;
		}
		List<String> bottleKeyWords = loadKeyWolds();
		if (bottleKeyWords != null && bottleKeyWords.size() > 0) {
			for (String key : bottleKeyWords) {
				if(content.contains(key)) {
					return true;
				}
			}
		}
		return false;
	}
	
	

	public static void saveKeyWord(String keywords) {
		if (keywords == null) {
			keywords = "";
		}
		keywords = keywords.trim();
		if (TextUtils.isNotEmpty(keywords)) {
			keywords = keywords.replace("，", ",");
			String[] keys = keywords.split(",");
			StringBuilder sb = new StringBuilder();
			for (String key : keys) {
				if (TextUtils.isNotEmpty(key.trim())) {
					sb.append(key.trim());
					sb.append(",");
				}
			}
			keywords = sb.toString();
			keywords = keywords.substring(0, keywords.length() - 1);
		}

		
		SystemDao dao=SpringContextUtil.getBean("systemDao");
		dao.updateFilterKeywords(0, keywords);
		toCache(keywords);
	}

	public static String loadKeyWoldFromCache() {
		UserCacheService cache = SpringContextUtil.getBean("userCacheService");
		return cache.getBottleKeyWord();

	}

	public static void toCache(String keys) {
		UserCacheService cache = SpringContextUtil.getBean("userCacheService");
		cache.setBottleKeyWord(keys);
	}

	public static String getStar(int len) {
		char[] stars = new char[len];
		for (int i = 0; i < len; i++) {
			stars[i] = '*';
		}
		return new String(stars, 0, len);
	}

}
