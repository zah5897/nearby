package com.zhan.app.nearby.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.zhan.app.nearby.util.IPUtil;

public class AppExceptionHandler implements HandlerExceptionResolver {

	private static Logger log = Logger.getLogger(AppExceptionHandler.class);

	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object obj,
			Exception ex) {
		String url = request.getRequestURI();
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		String msg = getErrorMessage(ex);
		String _ua=request.getParameter("_ua");
		String ip = IPUtil.getIpAddress(request);
		log.error("_ua="+_ua+"\nip="+ip+"\n"+url + "\n" + msg);

		ERROR err;
		if (ex instanceof AppException) {
			err = ((AppException) ex).getError();
		} else {
			err = ERROR.ERR_SYS;
		}

		view.addStaticAttribute("msg", err.getErrorMsg());
		view.addStaticAttribute("code", err.getValue());

		ModelAndView mv = new ModelAndView();
		mv.setView(view);
		return mv;
	}

	public static String getErrorMessage(Exception e) {
		if (null == e) {
			return "";
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
