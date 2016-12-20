package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.service.ManagerService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	@Resource
	private ManagerService managerService;

	@RequestMapping(value = "/")
	public ModelAndView index(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("account");
		if (obj != null) {
			return new ModelAndView("index");
		}
		return new ModelAndView("login");
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView dologin(HttpServletRequest request, String name, String password) {

		HttpSession session = request.getSession();
		Object obj = session.getAttribute("account");
		if (obj != null) {
			return new ModelAndView("redirect:/manager/");
		}

		if (!"admin".equals(name)) {
			ModelAndView view = new ModelAndView("login");
			view.addObject("error", "登录失败，账号不存在");
			return view;
		}
		if ("123456789".equals(password)) {
			ModelAndView view = new ModelAndView("login");
			view.addObject("error", "登录失败，密码不正确");
			return view;
		}
		session.setAttribute("account", name + "&" + password);
		return new ModelAndView("redirect:/manager/");
	}

	@RequestMapping(value = "/logout")
	public ModelAndView logout(HttpServletRequest request) {

		HttpSession session = request.getSession();
		session.removeAttribute("account");
		return new ModelAndView("redirect:/manager/");
	}

	@RequestMapping(value = "/forword")
	public ModelAndView forword(String path) {
		return new ModelAndView(path);
	}

	// 列出已经选择到首页的动态
	@RequestMapping(value = "/selected_dynamic_list")
	public ModelAndView selected_dynamic_list(int pageIndex) {
		int count = managerService.getHomeFoundSelectedCount();
		ModelAndView view = new ModelAndView("dynamic_1");
		if (pageIndex <= 0) {
			pageIndex = 1;
		}

		List<UserDynamic> dys = managerService.getHomeFoundSelected(pageIndex, 10);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeImagePath(dys, true);
		}
		view.addObject("selecteds", dys);
		view.addObject("count", count);
		int pageCount = count / 10;

		if (count % 10 > 0) {
			pageCount += 1;
		}
		view.addObject("pageCount", pageCount);
		view.addObject("currentPageIndex", pageIndex);
		return view;
	}

	// 未选择出现在首页的列表
	@RequestMapping(value = "/unselected_dynamic_list")
	public ModelAndView unselected_dynamic_list(int pageIndex) {
		int count = managerService.getUnSelectedCount();
		ModelAndView view = new ModelAndView("dynamic_1");
		if (pageIndex <= 0) {
			pageIndex = 1;
		}

		List<UserDynamic> dys = managerService.getUnSelected(pageIndex, 10);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeImagePath(dys, true);
		}
		view.addObject("selecteds", dys);
		view.addObject("count", count);
		int pageCount = count / 10;

		if (count % 10 > 0) {
			pageCount += 1;
		}
		view.addObject("pageCount", pageCount);
		view.addObject("currentPageIndex", pageIndex);
		return view;
	}

	@RequestMapping(value = "/remove_from_selected")
	public @ResponseBody ModelMap remove_from_selected(long id,int currentPage) {
		managerService.removeFromSelected(id);
		ModelMap r=ResultUtil.getResultOKMap();
		
		List<UserDynamic> dys = managerService.getHomeFoundSelected(currentPage, 10);
		
		if(dys!=null){
			UserDynamic dy=dys.get(dys.size()-1);
			ImagePathUtil.completeImagePath(dy, true);
			r.put("pageData", dy); 
		}
		return r;
	}

}
