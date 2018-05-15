package com.zhan.app.nearby.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.zhan.app.nearby.cache.UserCacheService;

public class BottleKeyWordUtil {

	public static final String NAME = "bottle_key_word.properties";
	public static final String KEY = "bottle_key_word";

	public static String loadKeyWold() {
		String keywords = null;
		keywords = loadKeyWoldFromCache();
		if (TextUtils.isNotBlank(keywords)) {
			return keywords;
		}
		Properties prop = PropertiesUtil.load(NAME);
		keywords = PropertiesUtil.getProperty(prop, KEY);
		return keywords;
	}

	public static List<String> loadKeyWolds() {
		String keywords = loadKeyWold();
		if (TextUtils.isEmpty(keywords)) {
			return null;
		}
		return Arrays.asList(keywords.split(","));
	}

	public static void saveKeyWord(String keywords) {
		if (keywords == null) {
			keywords = "";
		}
		keywords=keywords.trim();
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
		
		Properties prop = PropertiesUtil.load(NAME);
		prop.setProperty(KEY, keywords);

		String file = ImageSaveUtils.class.getClassLoader().getResource(NAME).getFile();
		try {
			prop.store(new FileOutputStream(new File(file)), "");
			toCache(keywords);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		char[] stars=new char[len];
		for(int i=0;i<len;i++) {
			stars[i]='*';
		}
		return new String(stars, 0, len);
	}
	
}
