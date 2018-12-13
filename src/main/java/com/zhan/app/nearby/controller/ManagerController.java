package com.zhan.app.nearby.controller;

import java.security.NoSuchAlgorithmException;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.ManagerService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.MD5Util;
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
	public ModelAndView dologin(HttpServletRequest request, String name, String password) throws NoSuchAlgorithmException {

		HttpSession session = request.getSession();
		Object obj = session.getAttribute("account");
		if (obj != null) {
			return new ModelAndView("redirect:/manager/");
		}
		boolean mlr=managerService.mLogin(name, MD5Util.getMd5(password));
		if(!mlr) {
			ModelAndView view = new ModelAndView("login");
			view.addObject("error", "登录失败，账号或密码错误");
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

	private int getPageCountByState(int state) {
		int count = managerService.getPageCountByState(state);
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
		List<Long> idlist = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});

		for (long id : idlist) {
			managerService.removeFromSelected(id);
		}
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCount(true));
		List<UserDynamic> dys = managerService.getHomeFoundSelected(currentPage, 10);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeDynamicsPath(dys, true);
			if (idlist.size() >= dys.size()) {
				r.put("pageData", dys);
			} else {
				int from = dys.size() - idlist.size();
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

		List<Long> idList = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});
		int len = idList.size();
		for (long id : idList) {
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
							String imagePath = ImageSaveUtils.saveTopicImages(file);
							topic.setIcon(imagePath);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if ("big_img".equals(name)) {
						try {
							String imagePath = ImageSaveUtils.saveTopicImages(file);
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
	public @ResponseBody ModelMap send_msg_to_all(String msg, String type) {
		if (TextUtils.isEmpty(msg)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		managerService.sendMsgToAll(msg, type);
		return ResultUtil.getResultOKMap();
	}

	// 获取新增用户
	@RequestMapping(value = "/list_new_user")
	public @ResponseBody ModelMap list_new_user(int pageIndex, int pageSize, int type) {
		ModelMap reMap = ResultUtil.getResultOKMap();
		if (pageIndex == 1) {
			int count = managerService.newUserCount(type);
			int pageCount = count / pageSize;
			if (count % pageSize > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			reMap.put("pageCount", pageCount);
			reMap.put("totalCount", count);
		}
		List<ManagerUser> users = managerService.listNewUser(pageIndex, pageSize, type);
		ImagePathUtil.completeManagerUserAvatarsPath(users, true);
		reMap.put("users", users);
		reMap.put("currentPageIndex", pageIndex);
		return reMap;
	}

	// @RequestMapping(value = "/edit_user_from_found_list")
	// public @ResponseBody ModelMap edit_user_from_found_list(String ids, int
	// state, int currentPage) {
	// if (TextUtils.isEmpty(ids)) {
	// return ResultUtil.getResultMap(ERROR.ERR_FAILED);
	// }
	// JSONArray idsArray = JSON.parseArray(ids);
	// int len = idsArray.size();
	// for (int i = 0; i < len; i++) {
	// String strId = idsArray.getString(i);
	// long id = Long.parseLong(strId);
	// managerService.editUserFromFound(id, state);
	// }
	// return list_new_user(currentPage);
	// }

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

	// 未选中首页内执行违规操作
	@RequestMapping(value = "/illegal_unselected")
	public @ResponseBody ModelMap illegal_unselected(long id, int currentPage) {
		managerService.updateDynamicState(id, DynamicState.T_ILLEGAL);
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

	// 动态违规接口
	@RequestMapping(value = "/dy_illegal")
	public @ResponseBody ModelMap dy_illegal(long id, int currentPage) {
		managerService.updateDynamicState(id, DynamicState.T_ILLEGAL);
		ModelMap r = ResultUtil.getResultOKMap();

		List<UserDynamic> dys = managerService.getDyanmicByState(currentPage, 10, DynamicState.T_CREATE);
		r.put("pageCount", getPageCountByState(DynamicState.T_CREATE.ordinal()));
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

	// 根据状态列出动态
	@RequestMapping(value = "/list_dynamic_by_state")
	public @ResponseBody ModelMap list_dynamic_by_state(int pageIndex, int state) {
		int pageCount = getPageCountByState(state);
		if (pageIndex == 0) {
			pageIndex = 1;
		} else if (pageIndex < 0) {
			pageIndex = pageCount;
		}
		List<UserDynamic> dys = managerService.getDyanmicByState(pageIndex, 10, DynamicState.values()[state]);
		if (dys != null && dys.size() > 0) {
			ImagePathUtil.completeDynamicsPath(dys, true);
		}

		ModelMap reMap = ResultUtil.getResultOKMap();
		reMap.put("selecteds", dys);
		reMap.put("pageCount", pageCount);
		reMap.put("currentPageIndex", pageIndex);
		return reMap;
	}

	// 批量审核通过
	@RequestMapping(value = "/verify_batch")
	public @ResponseBody ModelMap verify_batch(String ids, int currentPage) {

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		List<Long> idList = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});

		int len = idList.size();
		for (long id : idList) {
			managerService.updateDynamicState(id, DynamicState.T_FORMAL);
		}
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCountByState(DynamicState.T_CREATE.ordinal()));
		List<UserDynamic> dys = managerService.getDyanmicByState(currentPage, 10, DynamicState.T_CREATE);
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

	// 批量审核通过
	@RequestMapping(value = "/verify_batch_singl")
	public @ResponseBody ModelMap verify_batch_singl(long id, int currentPage) {

		managerService.updateDynamicState(id, DynamicState.T_FORMAL);
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCountByState(DynamicState.T_CREATE.ordinal()));
		List<UserDynamic> dys = managerService.getDyanmicByState(currentPage, 10, DynamicState.T_CREATE);
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

	// 删除违规动态（多个）
	@RequestMapping(value = "/removes_illegal_dynamics")
	public @ResponseBody ModelMap removes_illegal_dynamics(String ids, int currentPage) {

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		List<Long> idList = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});

		int len = idList.size();
		for (long id : idList) {
			managerService.removeDyanmicByState(id, DynamicState.T_ILLEGAL);
		}
		ModelMap r = ResultUtil.getResultOKMap();
		r.put("pageCount", getPageCountByState(DynamicState.T_ILLEGAL.ordinal()));
		List<UserDynamic> dys = managerService.getDyanmicByState(currentPage, 10, DynamicState.T_ILLEGAL);
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

	// 删除违规动态（单个）
	@RequestMapping(value = "/removes_illegal_dynamic")
	public @ResponseBody ModelMap removes_illegal_dynamic(long id, int currentPage) {
		managerService.removeDyanmicByState(id, DynamicState.T_ILLEGAL);
		ModelMap r = ResultUtil.getResultOKMap();

		List<UserDynamic> dys = managerService.getDyanmicByState(currentPage, 10, DynamicState.T_ILLEGAL);
		r.put("pageCount", getPageCountByState(DynamicState.T_ILLEGAL.ordinal()));
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

	// 违规动态恢复到待审核区
	@RequestMapping(value = "/backToCheck")
	public @ResponseBody ModelMap backToCheck(long id, int currentPage) {
		managerService.updateDynamicState(id, DynamicState.T_CREATE);
		ModelMap r = ResultUtil.getResultOKMap();

		List<UserDynamic> dys = managerService.getDyanmicByState(currentPage, 10, DynamicState.T_ILLEGAL);
		r.put("pageCount", getPageCountByState(DynamicState.T_ILLEGAL.ordinal()));
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

	// 获取所有用户
	@RequestMapping(value = "/list_user_all")
	public @ResponseBody ModelMap list_user_all(int pageSize, int pageIndex, int type, String keyword, Long user_id) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<BaseUser> users = managerService.getAllUser(pageSize, pageIndex, type, keyword, user_id);

		if (pageIndex == 1) {
			int totalSize = managerService.getUserSize(type, keyword, user_id);
			int pageCount = totalSize / pageSize;
			if (totalSize % 10 > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		r.put("users", users);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	// 获取所有发现用户黑名单
	@RequestMapping(value = "/list_user_found")
	public @ResponseBody ModelMap list_user_found_by_state(int pageSize, int pageIndex, int state) {
		ModelMap r = ResultUtil.getResultOKMap();
		FoundUserRelationship ship = FoundUserRelationship.values()[state];
		List<BaseUser> users = managerService.getFoundUsersByState(pageSize, pageIndex, ship);

		if (pageIndex == 1) {
			int totalSize = managerService.getFoundUserCountByState(ship);
			int pageCount = totalSize / pageSize;
			if (totalSize % 10 > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		r.put("users", users);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	// 获取所有用户
	@RequestMapping(value = "/list_user_meet_bottle_recommend")
	public @ResponseBody ModelMap list_user_meet_bottle_recommend(int pageSize, int pageIndex, String keyword) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<BaseUser> users = managerService.getAllMeetBottleRecommendUser(pageSize, pageIndex, keyword);

		if (pageIndex == 1) {
			int totalSize = managerService.getMeetBottleRecommendUserSize(keyword);
			int pageCount = totalSize / pageSize;
			if (totalSize % 10 > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		ImagePathUtil.completeAvatarsPath(users, true);
		r.put("users", users);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	// 添加到发现用户黑名单
	@RequestMapping(value = "/edit_user_found_state")
	public @ResponseBody ModelMap addToUserFoundBlack(long user_id, int fun, Integer pageSize, Integer pageIndex,
			int state) {
		FoundUserRelationship ship = FoundUserRelationship.values()[state];
		managerService.editUserFoundState(user_id, ship);
		if (fun == 0) {
			return list_user_found_by_state(pageSize, pageIndex, state);
		}
		return ResultUtil.getResultOKMap();
	}

	// 添加到发现用户黑名单
	@RequestMapping(value = "/remove_user_found_state")
	public @ResponseBody ModelMap remove_user_found_state(long user_id, Integer pageSize, Integer pageIndex,
			int state) {
		managerService.removeUserFoundState(user_id);
		return list_user_found_by_state(pageSize, pageIndex, state);
	}

	// 添加到邂逅瓶待选
	@RequestMapping(value = "/edit_user_meet_bottle_recomend")
	public @ResponseBody ModelMap addToUserMeetBottle(long user_id, int fun, Integer pageSize, Integer pageIndex,
			String keyword) {
		managerService.editUserMeetBottle(user_id, fun);
		if (fun == 0) {
			return list_user_meet_bottle_recommend(pageSize, pageIndex, keyword);
		}
		return ResultUtil.getResultOKMap();
	}

	// 获取提现申请记录
	@RequestMapping(value = "/list_exchange_history")
	public @ResponseBody ModelMap list_exchange_history(int pageSize, int pageIndex, int type) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<Object> exchanges = managerService.getExchangeHistory(pageSize, pageIndex, type);
		if (pageIndex == 1) {
			int totalSize = managerService.getExchangeHistorySize(type);
			int pageCount = totalSize / pageSize;
			if (totalSize % 10 > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("exchanges", exchanges);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	@RequestMapping(value = "/exchange_handle")
	public @ResponseBody ModelMap exchange_handle(int id, boolean agreeOrReject, int pageSize, int pageIndex,
			int type) {
		managerService.handleExchange(id, agreeOrReject);
		return list_exchange_history(pageSize, pageIndex, type);
	}

	// 获取提现申请记录
	@RequestMapping(value = "/list_report_history")
	public @ResponseBody ModelMap list_report_history(int type, int pageSize, int pageIndex) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<Report> exchanges = managerService.getReports(type, pageSize, pageIndex);
		if (pageIndex == 1) {
			int totalSize = managerService.getReportSize(type);
			int pageCount = totalSize / pageSize;
			if (totalSize % 10 > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("reports", exchanges);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	// 获取提现申请记录
	@RequestMapping(value = "/handleReport")
	public @ResponseBody ModelMap handleReport(int id, int type, int pageSize, int pageIndex, Boolean isIgnore) {
		managerService.handleReport(id, isIgnore == null ? false : isIgnore);
		return list_report_history(type, pageSize, pageIndex);
	}

	// 获取提现申请记录
	@RequestMapping(value = "/list_bottle")
	public @ResponseBody ModelMap list_bottle(int type, int pageSize, int pageIndex, Long bottle_id) {
		ModelMap r = ResultUtil.getResultOKMap();
		List<Bottle> exchanges = managerService.listBottleByState(type, pageSize, pageIndex, bottle_id);

		for (Bottle b : exchanges) {
			if (b.getType() == BottleType.MEET.ordinal()) {
				b.setContent(managerService.getMeetUserAvatar(b.getContent()));
			}
		}

		if (pageIndex == 1) {
			int totalSize = managerService.getBottleCountWithState(type, bottle_id);
			int pageCount = totalSize / pageSize;
			if (totalSize % pageSize > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("bottles", exchanges);
		r.put("currentPageIndex", pageIndex);
		return r;
	}

	// 获取提现申请记录
	@RequestMapping(value = "/changeBottleState")
	public @ResponseBody ModelMap changeBottleState(int id, int type, int pageSize, int pageIndex, int to_state,
			Long bottle_id) {
		managerService.changeBottleState(id, to_state);
		return list_bottle(type, pageSize, pageIndex, bottle_id);
	}

	// 获取提现申请记录
	@RequestMapping(value = "/filter_key_word")
	public @ResponseBody ModelMap bottle_txt_key_word(int type, HttpServletRequest request) {

		if (type == 0) {
			if(ImageSaveUtils.getFilterWordsFilePath()!=null) {
				return ResultUtil.getResultOKMap().addAttribute("download_path",ImagePathUtil.getFilterWordsPath());
			}else {
				return ResultUtil.getResultOKMap();
			}
		} else {
			DefaultMultipartHttpServletRequest multipartRequest = (DefaultMultipartHttpServletRequest) request;
			ModelMap result = ResultUtil.getResultMap(ERROR.ERR_FAILED);
			if (multipartRequest != null) {
				Iterator<String> iterator = multipartRequest.getFileNames();
				while (iterator.hasNext()) {
					MultipartFile file = multipartRequest.getFile((String) iterator.next());
					if (!file.isEmpty()) {
						try {
							String imagePath = ImageSaveUtils.saveFile(file);
							result = ResultUtil.getResultOKMap();
							result.put("download_path", imagePath);
							return result;
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
					}
				}
			}
			return result;
		}

	}

	// 获取提现申请记录
	@RequestMapping(value = "/list_spread_user")
	public @ResponseBody ModelMap list_spread_user() {
		return managerService.getSpecialUsers(1, 1000);
	}

	@RequestMapping(value = "/add_spread_user")
	public @ResponseBody ModelMap add_spread_user(long uid) {
		int count = managerService.addSpreadUser(uid);
		if (count == 1) {
			return managerService.getSpecialUsers(1, 1000);
		} else {

			String err_msg;
			if (count == -1) {
				err_msg = "该用户已存在推广列表中";
			} else if (count == -2) {
				err_msg = "该用户ID不存在";
			} else {
				err_msg = "操作失败";
			}
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, err_msg);
		}
	}

	@RequestMapping(value = "/del_spread_user")
	public @ResponseBody ModelMap del_spread_user(long uid) {
		int count = managerService.delSpecialUser(uid);
		if (count == 1) {
			return managerService.getSpecialUsers(1, 1000);
		} else {
			return ResultUtil.getResultOKMap().addAttribute("count", count);
		}
	}

	@RequestMapping(value = "/edit_avatar_state")
	public @ResponseBody ModelMap edit_avatar_state(int id) {
		managerService.editAvatarState(id);
		return ResultUtil.getResultOKMap();
	}
	
	@RequestMapping(value = "/edit_avatar_state_by_user_id")
	public @ResponseBody ModelMap edit_avatar_state_by_user_id(long user_id) {
		managerService.editAvatarStateByUserId(user_id);
		return ResultUtil.getResultOKMap();
	}
	
	//获取需要審核的用戶头像
	@RequestMapping(value = "/list_confirm_avatars")
	public @ResponseBody ModelMap list_confirm_avatars(int pageSize, int pageIndex) {
		ModelMap r=ResultUtil.getResultOKMap();
		r.addAttribute("users", managerService.listConfirmAvatars(pageSize,pageIndex));
		if (pageIndex == 1) {
			int totalSize = managerService.getCountOfConfirmAvatars();
			int pageCount = totalSize / pageSize;
			if (totalSize % pageSize > 0) {
				pageCount += 1;
			}
			if (pageCount == 0) {
				pageCount = 1;
			}
			r.put("pageCount", pageCount);
		}
		r.put("currentPageIndex", pageIndex);
		return r;
	}
	
	
}
