package com.zhan.app.nearby.util;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.zhan.app.nearby.exception.AppException;
import com.zhan.app.nearby.exception.ERROR;

public class WriteJsonUtil {
	public static void write(HttpServletResponse response, Exception ex) {
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			ERROR err;
			if (ex instanceof AppException) {
				err = ((AppException) ex).getError();
			} else {
				err = ERROR.ERR_SYS;
			}
			 
			Map<String, Object> json=ResultUtil.getResultMap(err);
			writer.write(json.toString());
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write(HttpServletResponse response, ERROR error) {
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			Map<String, Object> json=ResultUtil.getResultMap(error);
			writer.write(json.toString());
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
