package com.zhan.app.nearby.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.zhan.app.nearby.util.WriteJsonUtil;

public class AppExceptionHandler implements HandlerExceptionResolver {

	private static Logger log = Logger.getLogger(AppExceptionHandler.class);

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object obj,
			Exception ex) {
		String url = request.getRequestURI();
		WriteJsonUtil.write(response, ex);
		log.error(url+"\n"+ex.getMessage());
		ex.printStackTrace();
		return new ModelAndView();
	}

}
