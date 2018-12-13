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

public class BottleKeyWordUtil {

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
			e.printStackTrace();
		}

		return content;
	}

	
	
	public static Set<String> loadFilterWords() throws IOException {
		
		String filePath = ImageSaveUtils.getFilterWordsFilePath();
		
		if (filePath != null) {
			InputStream in = new FileInputStream(new File(filePath));
			
			
//			BufferedReader br = new BufferedReader(new UnicodeReader(in,"GBK"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				//System.out.println(temp);
				sb.append(temp);
			}
			br.close();
			in.close();
			
			String keyes=sb.toString();
			System.out.println(keyes);
			String[] words=keyes.split(",");
			List<String> listwords=new ArrayList<String>();
			checkWordsExist(listwords,words);
			System.out.println("总敏感词数："+listwords.size());
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
			}else {
				//System.out.println("重复关键词："+w);
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
			e.printStackTrace();
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

}
