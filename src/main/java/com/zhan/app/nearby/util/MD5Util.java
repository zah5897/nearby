package com.zhan.app.nearby.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MD5Util {

	// 静态方法，便于作为工具类
	public static String getMd5(String plainText) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(plainText.getBytes());
		byte b[] = md.digest();

		int i;

		StringBuffer buf = new StringBuffer("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		// 32位加密
		return buf.toString();
		// 16位的加密
		// return buf.toString().substring(8, 24);
	}

	public static String getMd5_16(String plainText) throws NoSuchAlgorithmException {
		return getMd5(plainText).substring(8, 24);
	}

	public static byte[] getMd5Byte(String plainText) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(plainText.getBytes());
		byte b[] = md.digest();
		return b;
	}

	public static String sign(Map<String, String> map, String key) {
		ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> mapping1, Map.Entry<String, String> mapping2) {
				return mapping1.getKey().compareTo(mapping2.getKey());
			}
		});
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> mapping : list) {
			if ("sign".equals(mapping.getKey())) {
				continue;
			}
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(mapping.getKey());
			sb.append("=");
			sb.append(mapping.getValue());
		}
		return AESUtil.encrypt(sb.toString(), key);
	}

	public static void main(String[] args) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("user_id", String.valueOf(41));
		param.put("token", "123");
		param.put("aid", "123");
		param.put("task_id", "123");
		param.put("extra", String.valueOf(1));
		param.put("uuid", "123");
		
		String content=sign(param, "123");
		System.out.println(content);
		System.out.println(AESUtil.decrypt(content, "123"));
		
		
	}
}
