package com.zhan.app.nearby.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.apache.log4j.Logger;

public class BottleKeyWordUtil {
	private static Logger log = Logger.getLogger(BottleKeyWordUtil.class);
	private static TextFilter textFilter;

	public static String filterContent(String content) {
		if (TextUtils.isEmpty(content)) {
			return "";
		}
		Set<String> bottleKeyWords;
		try {
			bottleKeyWords = checkFilterWord(content);
			if (bottleKeyWords != null && bottleKeyWords.size() > 0) {
				for (String key : bottleKeyWords) {
					content = content.replace(key, getStar(key.length()));
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return content;
	}

	
	
	public static Set<String> loadFilterWords() throws IOException {
		
		if(isWindows()) {
			Set<String> set= new HashSet<String>();
			set.add("敏感词");
			return set;
		}
		
		String filePath = ImageSaveUtils.getFilterWordsFilePath();
		
		if (filePath != null) {
			InputStream in = new FileInputStream(new File(filePath));
			
			
//			BufferedReader br = new BufferedReader(new UnicodeReader(in,"GBK"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
			in.close();
			
			String keyes=sb.toString();
			String[] words=keyes.split(",");
			List<String> listwords=new ArrayList<String>();
			checkWordsExist(listwords,words);
			Set<String> staffsSet = new HashSet<String>(listwords);
			return staffsSet;
		} else {
			throw new RuntimeErrorException(null, "敏感词文件为空");
		}
	}
	private static void checkWordsExist(List<String> listWords,String[] words) {
		for(String w:words) {
			if(TextUtils.isEmpty(w)) {
				continue;
			}
			if(w.contains("，")) {
				String[] errSplit=w.split("，");
				checkWordsExist(listWords,errSplit);
				continue;
			}
			
			if(!listWords.contains(w)) {
				listWords.add(w);
			}
		}
	}
	
	
	private static void initTextFilter() throws IOException {
		if (textFilter == null) {
			textFilter = new TextFilter();
			Set<String> sensitiveWords = loadFilterWords();
			textFilter.initSensitiveWordsMap(sensitiveWords);
		}
	}

	public static void refreshFilterWords() {
		try {
			if (textFilter == null) {
				initTextFilter();
			} else {
				Set<String> sensitiveWords = loadFilterWords();
				textFilter.initSensitiveWordsMap(sensitiveWords);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	private static Set<String> checkFilterWord(String txt) throws IOException {
		initTextFilter();
		return textFilter.getSensitiveWords(txt);
	}

	public static boolean isContainsIllegalKey(String content) throws IOException {
		if (TextUtils.isEmpty(content)) {
			return false;
		}
		initTextFilter();
		if (textFilter.checkContainsFilterWord(content).size() > 0) {
			return true;
		}
		return false;
	}

	public static String getStar(int len) {
		char[] stars = new char[len];
		for (int i = 0; i < len; i++) {
			stars[i] = '*';
		}
		return new String(stars, 0, len);
	}

	public static void checkWordsExist(String word) {
		textFilter.checkWordsExist(word);
	}
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
	}
}
