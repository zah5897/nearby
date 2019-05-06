package com.zhan.app.nearby.util;

import java.util.Random;

public class RandomCodeUtil {
	public static String randomCode(int len) {
		Random ramRandom = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(ramRandom.nextInt(10));
		}
		return sb.toString();
	}

	public static boolean randomPercentOK(int percent) {
		Random ramRandom = new Random();
		int r = ramRandom.nextInt(100);
		return r < percent;
	}
	
	public static int getRandom(int len) {
		Random ramRandom = new Random();
		return ramRandom.nextInt(len);
	}
}
