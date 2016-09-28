package com.zhan.app.nearby.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/manager_controller")
public class ManagerController {
	@RequestMapping(value = "/forword", method = RequestMethod.GET)
	public ModelAndView forword(String path) {

		System.out.println(path);
		return new ModelAndView(path);
	}
}
