package com.zhan.app.nearby.util;

public class DeviceUtil {

	public static int getRequestDevice(String _ua) {
		if (_ua.startsWith("a")) {
			return 1; // ios
		} else if (_ua.startsWith("g")) {
			return 2;// android
		} else {
			return 0;
		}
	}
}
