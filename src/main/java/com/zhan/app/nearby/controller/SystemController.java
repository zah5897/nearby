package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/system")
public class SystemController {
	@Resource
	private UserService userService;
	@Autowired
	private MainService mainService;
	
	
    @Deprecated
	@RequestMapping("report")
	public ModelMap report(Report report) {
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("new_report")
	public ModelMap new_report(Report report) {
		mainService.saveReport(report);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("list_report")
	public ModelMap list_report(int page, Integer count, int type) {
		List<Report> list=mainService.listReport(type, page, count==null?10:count);
		return ResultUtil.getResultOKMap().addAttribute("users",list );
	}

	@RequestMapping("prootl")
	public String prootl() {
		return "prootl";
	}
	
}
