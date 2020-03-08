package com.zhan.app.nearby.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.apache.log4j.Logger;

public class DateTimeUtil {
	
	private static Logger log = Logger.getLogger(DateTimeUtil.class);
	
	public static String getYearMonthDay(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return year + "_" + month + "_" + day;
	}

	public static String format(Date result) {
		if(result==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(result);
	}
	
	public static Date parse(String timeStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.parse(timeStr);
	}
	public static Date parse(String timeStr,String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(timeStr);
	}
	public static String parseBirthday(Date result) {
		if (result == null) {
			return new String();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(result);
	}
	
	public static String getDayStr(Date result) {
		if (result == null) {
			return new String();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(result);
	}

	public static String getAge(Date birthday) {
		if (birthday == null) {
			return "-1";
		}
		Calendar c = Calendar.getInstance();
		c.setTime(birthday);
		int birthdayYear = c.get(Calendar.YEAR);
		c.setTimeInMillis(System.currentTimeMillis());
		int nowYear = c.get(Calendar.YEAR);

		if (birthdayYear > nowYear) {
			return "-1";
		}
		return String.valueOf(nowYear - birthdayYear);
	}

	public static Date parseDate(String strDate) {
		if (TextUtils.isEmpty(strDate)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(strDate);
		} catch (ParseException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public static Date getVipEndDate(Date now, int monthCount) {
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		int nowMonth = c.get(Calendar.MONTH);// 当前月份0开始
		int nowYear = c.get(Calendar.YEAR); // 当前年

		int endYear = nowYear;
		int endMonth = nowMonth;
		int endDay = c.get(Calendar.DAY_OF_MONTH);

		if (monthCount == 12) { // 直接跨年
			endYear = nowYear + 1;
		} else if (nowMonth + monthCount > 11) {
			endYear = nowYear + 1;
			endMonth = (nowMonth + monthCount) - 12;
		} else {
			endMonth = nowMonth + monthCount;
		}
		c.clear();
		c.set(endYear, endMonth, endDay);
		return c.getTime();
	}

	
	public static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);
		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}
	
	// public static void main(String[] args) throws ParseException {
	// Calendar c = Calendar.getInstance();
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// Date birthday = sdf.parse("2017-03-08 00:00:00");
	// String age = getAge(birthday);
	// }
}
