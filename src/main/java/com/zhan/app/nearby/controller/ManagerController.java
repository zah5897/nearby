package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.ManagerService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

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

	// 列出已经选择到首页的动态 返回json
	@RequestMapping(value = "/selected_dynamic_list")
	public @ResponseBody ModelMap selected_dynamic_list(int pageIndex) {
		int pageCount = getPageCount(true);
		if (pageIndex == 0) {
			pageIndex = 1;
		} else if (pageIndex < 0) {
			pageIndex = pageCount;
		}
		List<UserDynamic> dys = managerService.getHomeFoundSelected(pageIndex, 10);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeDynamicsPath(dys, true);
		}

		ModelMap reMap = ResultUtil.getResultOKMap();
		reMap.put("selecteds", dys);
		reMap.put("pageCount", pageCount);
		reMap.put("currentPageIndex", pageIndex);
		return reMap;
	}

	// 未选择出现在首页的列表
	@RequestMapping(value = "/unselected_dynamic_list")
	public @ResponseBody ModelMap unselected_dynamic_list(int pageIndex) {
		int pageCount = getPageCount(false);
		if (pageIndex == 0) {
			pageIndex = 1;
		} else if (pageIndex < 0) {
			pageIndex = pageCount;
		} else if (pageIndex > pageCount) {
			pageIndex = pageCount;
		}

		List<UserDynamic> dys = managerService.getUnSelected(pageIndex, 10);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeDynamicsPath(dys, true);
		}
		ModelMap reMap = ResultUtil.getResultOKMap();
		reMap.put("selecteds", dys);
		reMap.put("pageCount", pageCount);
		reMap.put("currentPageIndex", pageIndex);
		return reMap;
	}

	private int getPageCount(boolean isSelected) {
		int count;
		if (isSelected) {
			count = managerService.getHomeFoundSelectedCount();
		} else {
			count = managerService.getUnSelectedCount();
		}
		int pageCount = count / 10;
		if (count % 10 > 0) {
			pageCount += 1;
		}

		return pageCount == 0 ? 1 : pageCount;
	}

	
	
	
	@RequestMapping(value = "/remove_from_selected")
	public @ResponseBody ModelMap remove_from_selected(long id, int currentPage) {
		managerService.removeFromSelected(id);
		ModelMap r = ResultUtil.getResultOKMap();

		List<UserDynamic> dys = managerService.getHomeFoundSelected(currentPage, 10);
		r.put("pageCount", getPageCount(true));
		if (dys != null && dys.size() > 0) {
			UserDynamic dy = dys.get(dys.size() - 1);
			ImagePathUtil.completeDynamicPath(dy, true);
			r.put("pageData", dy);
			r.put("currentPageIndex", currentPage);
		} else {
			r.put("currentPageIndex", currentPage - 1 > 0 ? currentPage - 1 : 1);
		}
		return r;
	}

	// 删除多个
	@RequestMapping(value = "/removes_from_selected")
	public @ResponseBody ModelMap removes_from_selected(String ids, int currentPage) {

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		JSONArray idsArray = JSON.parseArray(ids);

		int len = idsArray.size();
		for (int i = 0; i < len; i++) {
			String strId = idsArray.getString(i);
			long id = Long.parseLong(strId);
			managerService.removeFromSelected(id);
		}
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCount(true));
		List<UserDynamic> dys = managerService.getHomeFoundSelected(currentPage, 10);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeDynamicsPath(dys, true);
			if (len >= dys.size()) {
				r.put("pageData", dys);
			} else {
				int from = dys.size() - len;
				List<UserDynamic> subList = dys.subList(from, dys.size());
				r.put("pageData", subList);
			}
			r.put("currentPageIndex", currentPage);
		} else {
			r.put("currentPageIndex", currentPage - 1 > 0 ? currentPage - 1 : 1);
		}
		return r;
	}

	// 添加到首页推荐
	@RequestMapping(value = "/add_to_selected")
	public @ResponseBody ModelMap add_to_selected(long id, int currentPage) {
		managerService.addToSelected(id);
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCount(false));

		List<UserDynamic> dys = managerService.getUnSelected(currentPage, 10);
		if (dys != null && dys.size() > 0) {
			UserDynamic dy = dys.get(dys.size() - 1);
			if (dy.getId() < id) {
				ImagePathUtil.completeDynamicPath(dy, true);
				r.put("pageData", dy);
			}
			r.put("currentPageIndex", currentPage);
		} else {
			r.put("currentPageIndex", currentPage - 1 > 0 ? currentPage - 1 : 1);
		}

		return r;
	}

	// 忽略
	@RequestMapping(value = "/ignore")
	public @ResponseBody ModelMap ignore(long id, int currentPage) {
		managerService.ignore(id);
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCount(false));

		List<UserDynamic> dys = managerService.getUnSelected(currentPage, 10);
		if (dys != null && dys.size() > 0) {
			UserDynamic dy = dys.get(dys.size() - 1);
			if (dy.getId() < id) {
				ImagePathUtil.completeDynamicPath(dy, true);
				r.put("pageData", dy);
			}
			r.put("currentPageIndex", currentPage);
		} else {
			r.put("currentPageIndex", currentPage - 1 > 0 ? currentPage - 1 : 1);
		}

		return r;
	}

	// 添加多个到首页推荐
	@RequestMapping(value = "/add_batch_to_selected")
	public @ResponseBody ModelMap add_batch_to_selected(String ids, int currentPage) {

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		JSONArray idsArray = JSON.parseArray(ids);

		int len = idsArray.size();
		for (int i = 0; i < len; i++) {
			String strId = idsArray.getString(i);
			long id = Long.parseLong(strId);
			managerService.addToSelected(id);
		}
		ModelMap r = ResultUtil.getResultOKMap();
		// List<UserDynamic> dys = managerService.getUnSelected(currentPage,
		// 10);
		// if (dys != null) {
		// ImagePathUtil.completeImagePath(dys, true);
		// if (len >= dys.size()) {
		// r.put("pageData", dys);
		// } else {
		// int from = dys.size() - len;
		// List<UserDynamic> subList = dys.subList(from, dys.size());
		// r.put("pageData", subList);
		// }
		//
		// }

		r.put("pageCount", getPageCount(false));
		List<UserDynamic> dys = managerService.getUnSelected(currentPage, 10);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeDynamicsPath(dys, true);
			if (len >= dys.size()) {
				r.put("pageData", dys);
			} else {
				int from = dys.size() - len;
				List<UserDynamic> subList = dys.subList(from, dys.size());
				r.put("pageData", subList);
			}
			r.put("currentPageIndex", currentPage);
		} else {
			r.put("currentPageIndex", currentPage - 1 > 0 ? currentPage - 1 : 1);
		}
		return r;
	}

	@RequestMapping(value = "/get_welcome")
	public @ResponseBody ModelMap get_welcome() {
		ModelMap reMap = ResultUtil.getResultOKMap();
		reMap.put("welcome", managerService.getWelcome());
		return reMap;
	}

	@RequestMapping(value = "/set_welcome")
	public @ResponseBody ModelMap set_welcome(String welcome) {
		if (managerService.updateWelcome(welcome)) {
			ModelMap result = ResultUtil.getResultOKMap();
			result.put("welcome", welcome);
			return result;
		} else {
			return ResultUtil.getResultMap(ERROR.ERR_SYS);
		}
	}

	@RequestMapping(value = "/add_topic")
	public @ResponseBody ModelMap add_topic(HttpServletRequest request, Topic topic) {
		if (request instanceof DefaultMultipartHttpServletRequest) {
			DefaultMultipartHttpServletRequest multipartRequest = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {

					String name = file.getName();

					if ("small_img".equals(name)) {
						try {
							String imagePath = ImageSaveUtils.saveTopicImages(file,
									multipartRequest.getServletContext());
							topic.setIcon(imagePath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if ("big_img".equals(name)) {
						try {
							String imagePath = ImageSaveUtils.saveTopicImages(file,
									multipartRequest.getServletContext());
							topic.setBig_icon(imagePath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
		}
		topic.setCreate_time(new Date());
		managerService.addTopic(topic);
		ImagePathUtil.completeTopicImagePath(topic, true);
		return ResultUtil.getResultOKMap().addAttribute("topic", topic);
	}

	@RequestMapping(value = "/load_topic")
	public @ResponseBody ModelMap load_topic() {
		List<Topic> topics = managerService.loadTopic();
		ImagePathUtil.completeTopicImagePath(topics, true);
		return ResultUtil.getResultOKMap().addAttribute("topics", topics);
	}

	@RequestMapping(value = "/del_topic")
	public @ResponseBody ModelMap del_topic(long id) {
		managerService.delTopic(id);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping(value = "/send_msg_to_all")
	public @ResponseBody ModelMap send_msg_to_all(String msg) {
		if (TextUtils.isEmpty(msg)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		managerService.sendMsgToAll(msg);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping(value = "/new_user_count")
	public @ResponseBody ModelMap new_user_count() {
		int count = managerService.newUserCount();
		return ResultUtil.getResultOKMap().addAttribute("count", count);
	}

	@RequestMapping(value = "/list_new_user")
	public @ResponseBody ModelMap list_new_user(int pageIndex) {

		int count = managerService.newUserCount();

		int pageCount = count / 10;
		if (count % 10 > 0) {
			pageCount += 1;
		}

		if (pageCount == 0) {
			pageCount = 1;
		}

		if (pageIndex == 0) {
			pageIndex = 1;
		} else if (pageIndex < 0) {
			pageIndex = pageCount;
		} else if (pageIndex > pageCount) {
			pageIndex = pageCount;
		}
		List<ManagerUser> users = managerService.listNewUser(pageIndex, 10);
		ImagePathUtil.completeManagerUserAvatarsPath(users, true);
		ModelMap reMap = ResultUtil.getResultOKMap();
		reMap.put("users", users);
		reMap.put("pageCount", pageCount);
		reMap.put("currentPageIndex", pageIndex);
		return reMap;
	}

	@RequestMapping(value = "/edit_user_from_found_list")
	public @ResponseBody ModelMap edit_user_from_found_list(String ids,int state, int currentPage) {
		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		JSONArray idsArray = JSON.parseArray(ids);
		int len = idsArray.size();
		for (int i = 0; i < len; i++) {
			String strId = idsArray.getString(i);
			long id = Long.parseLong(strId);
			managerService.editUserFromFound(id,state);
		}
		return list_new_user(currentPage);
	}
	@RequestMapping(value = "/dynamic_del")
	public @ResponseBody ModelMap dynamic_del(long id, int currentPage) {
		managerService.removeUserDynamic(id);
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCount(false));
		List<UserDynamic> dys = managerService.getUnSelected(currentPage, 10);
		if (dys != null && dys.size() > 0) {
			UserDynamic dy = dys.get(dys.size() - 1);
			if (dy.getId() < id) {
				ImagePathUtil.completeDynamicPath(dy, true);
				r.put("pageData", dy);
			}
			r.put("currentPageIndex", currentPage);
		} else {
			r.put("currentPageIndex", currentPage - 1 > 0 ? currentPage - 1 : 1);
		}

		return r;
	}
}
