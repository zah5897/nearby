package com.zhan.app.nearby.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.comm.BottleState;
import com.zhan.app.nearby.comm.DynamicStatus;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.comm.UserFnStatus;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.AppointmentService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.ManagerService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.task.CommAsyncTask;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@Controller
@RequestMapping("/manager")
public class WebManagerController {
	private static Logger log = Logger.getLogger(WebManagerController.class);
	@Resource
	private ManagerService managerService;
	@Resource
	private MainService mainService;
	@Resource
	private UserService userService;
	@Autowired
	private AppointmentService appointmentService;
	@Autowired
	private CommAsyncTask commAsyncTask;

	@RequestMapping(value = "/")
	public ModelAndView index(HttpServletRequest request) {
		if (managerService.isLogin(request)) {
			return new ModelAndView("index");
		}
		return new ModelAndView("login");
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ModelAndView dologin(HttpServletRequest request, String name, String password)
			throws NoSuchAlgorithmException {
		if (managerService.isLogin(request)) {
			return new ModelAndView("redirect:/manager/");
		}
		boolean mlr = managerService.mLogin(request, name, MD5Util.getMd5(password));
		if (!mlr) {
			ModelAndView view = new ModelAndView("login");
			view.addObject("error", "登录失败，账号或密码错误");
			return view;
		}
		return new ModelAndView("redirect:/manager/");
	}

	@RequestMapping(value = "/logout")
	public ModelAndView logout(HttpServletRequest request) {
		if (!managerService.isLogin(request)) {
			return new ModelAndView("login");
		}
		managerService.logout(request);
		return new ModelAndView("redirect:/manager/");
	}

	@RequestMapping(value = "/forword")
	public ModelAndView forword(HttpServletRequest request, String path) {
		if (path.startsWith("play_video")) {

			ModelAndView mv = new ModelAndView("play_video");
			mv.addObject("thumb", request.getParameter("thumb"));
			mv.addObject("url", request.getParameter("vu"));
			return mv;
		}

		if (!managerService.isLogin(request)) {
			return new ModelAndView("redirect:/manager/");
		}
		return new ModelAndView(path);
	}

	@RequestMapping(value = "/selected_dynamic_list")
	public @ResponseBody ModelMap selected_dynamic_list(HttpServletRequest request, Long user_id, int pageIndex,Long dy_id) {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.getHomeFoundSelected(user_id, pageIndex, 10,dy_id);
	}

	// 未选择出现在首页的列表
	@RequestMapping(value = "/unselected_dynamic_list")
	public @ResponseBody ModelMap unselected_dynamic_list(HttpServletRequest request, Long user_id, int pageIndex,
			String nick_name) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return  managerService.getUnSelected(user_id, nick_name, pageIndex, 10);
	}

	@RequestMapping(value = "/remove_from_selected")
	public @ResponseBody ModelMap remove_from_selected(HttpServletRequest request, Long user_id, long id,
			int currentPage,Long dy_id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		managerService.removeFromSelected(id);
		return selected_dynamic_list(request,user_id,currentPage,dy_id);
	}

	// 删除多个
	@RequestMapping(value = "/removes_from_selected")
	public @ResponseBody ModelMap removes_from_selected(HttpServletRequest request, Long user_id, String ids,
			int currentPage,Long dy_id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		List<Long> idlist = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});

		for (long id : idlist) {
			managerService.removeFromSelected(id);
		}
		return selected_dynamic_list(request,user_id,currentPage,dy_id);
	}

	// 添加到首页推荐
	@RequestMapping(value = "/add_dy_to_selected")
	public @ResponseBody ModelMap add_dy_to_selected(HttpServletRequest request, Long user_id, String nick_name, long id,
			int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		managerService.addDyToSelected(id);
		return unselected_dynamic_list(request,user_id,currentPage,nick_name);
	}
	// 添加多个到首页推荐
	@RequestMapping(value = "/add_batch_to_selected")
	public @ResponseBody ModelMap add_batch_to_selected(HttpServletRequest request, Long user_id, String nick_name,
			String ids, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}

		List<Long> idList = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});
		for (long id : idList) {
			managerService.addDyToSelected(id);
		}
		return unselected_dynamic_list(request,user_id,currentPage,nick_name);
	}

	@RequestMapping(value = "/get_welcome")
	public @ResponseBody ModelMap get_welcome(HttpServletRequest request) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		ModelMap reMap = ResultUtil.getResultOKMap();
		reMap.put("welcome", managerService.getWelcome());
		return reMap;
	}

	@RequestMapping(value = "/set_welcome")
	public @ResponseBody ModelMap set_welcome(HttpServletRequest request, String welcome) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

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

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

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
							log.error(e.getMessage());
						}
					} else if ("big_img".equals(name)) {
						try {
							String imagePath = ImageSaveUtils.saveTopicImages(file);
							topic.setBig_icon(imagePath);
						} catch (Exception e) {
							log.error(e.getMessage());
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
	public @ResponseBody ModelMap load_topic(HttpServletRequest request) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		List<Topic> topics = managerService.loadTopic();
		ImagePathUtil.completeTopicImagePath(topics, true);
		return ResultUtil.getResultOKMap().addAttribute("topics", topics);
	}

	@RequestMapping(value = "/del_topic")
	public @ResponseBody ModelMap del_topic(HttpServletRequest request, long id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.delTopic(id);
		return ResultUtil.getResultOKMap();
	}

	// 获取新增用户
	@RequestMapping(value = "/list_new_user")
	public @ResponseBody ModelMap list_new_user(HttpServletRequest request, int pageIndex, int pageSize, int type) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

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
	@RequestMapping(value = "/dynamic_del")
	public @ResponseBody ModelMap dynamic_del(HttpServletRequest request, Long user_id, String nick_name, long id,
			int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.removeUserDynamic(id);
		return unselected_dynamic_list(request,user_id,currentPage,nick_name);
	}

	// 未选中首页内执行违规操作
	@RequestMapping(value = "/illegal_unselected")
	public @ResponseBody ModelMap illegal_unselected(HttpServletRequest request, Long user_id, String nick_name,
			long id, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.updateDynamicState(id, DynamicStatus.ILLEGAL);
		return unselected_dynamic_list(request,user_id,currentPage,nick_name);
	}
	
	
	// 未选中首页内执行违规操作
		@RequestMapping(value = "/ignore_unselected")
		public @ResponseBody ModelMap ignore_unselected(HttpServletRequest request, Long user_id, String nick_name,
				long id, int currentPage) {

			if (!managerService.isLogin(request)) {
				return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
			}
			managerService.updateDynamicManagerFlag(id, 1);
			return unselected_dynamic_list(request,user_id,currentPage,nick_name);
		}
	

	// 动态违规接口
	@RequestMapping(value = "/dy_illegal")
	public @ResponseBody ModelMap dy_illegal(HttpServletRequest request, long id, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		managerService.updateDynamicState(id, DynamicStatus.ILLEGAL);
		return managerService.getUnCheckDynamic(currentPage,10);
	}

	// 根据状态列出动态
	@RequestMapping(value = "/list_unchecked_dynamic")
	public @ResponseBody ModelMap list_unchecked_dynamic(HttpServletRequest request, int pageIndex) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.getUnCheckDynamic(pageIndex,10);
	}

	// 批量审核通过
	@RequestMapping(value = "/checked_batch")
	public @ResponseBody ModelMap checked_batch(HttpServletRequest request, String ids, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		List<Long> idList = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});

		for (long id : idList) {
			managerService.updateDynamicState(id, DynamicStatus.CHECKED);
		}
		return managerService.getUnCheckDynamic(currentPage,10);
	}

	// 批量审核通过
	@RequestMapping(value = "/dy_checked_ok")
	public @ResponseBody ModelMap verify_batch_singl(HttpServletRequest request, long id, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.updateDynamicState(id, DynamicStatus.CHECKED);
		return managerService.getUnCheckDynamic(currentPage,10);
	}
	@RequestMapping(value = "/list_illegal_dynamic")
	public @ResponseBody ModelMap list_illegal_dynamic(HttpServletRequest request, int pageIndex) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.getIllegalDynamic(pageIndex,10);
	}
	// 删除违规动态（多个）
	@RequestMapping(value = "/removes_illegal_dynamics")
	public @ResponseBody ModelMap removes_illegal_dynamics(HttpServletRequest request, String ids, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(ids)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED);
		}
		List<Long> idList = JSONUtil.jsonToList(ids, new TypeReference<List<Long>>() {
		});
		for (long id : idList) {
			managerService.removeDyanmicByIdAndState(id, DynamicStatus.ILLEGAL);
		}
		return managerService.getIllegalDynamic(currentPage,10);
	}

	// 删除违规动态（单个）
	@RequestMapping(value = "/removes_illegal_dynamic")
	public @ResponseBody ModelMap removes_illegal_dynamic(HttpServletRequest request, long id, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		managerService.removeDyanmicByIdAndState(id, DynamicStatus.ILLEGAL);
		return managerService.getIllegalDynamic(currentPage,10);
	}

	// 违规动态恢复到待审核区
	@RequestMapping(value = "/backToCheck")
	public @ResponseBody ModelMap backToCheck(HttpServletRequest request, long id, int currentPage) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		managerService.updateDynamicState(id, DynamicStatus.CREATE);
		return managerService.getIllegalDynamic(currentPage,10);
	}

	// 获取所有用户
	@RequestMapping(value = "/list_user_all")
	public @ResponseBody ModelMap list_user_all(HttpServletRequest request, int pageSize, int pageIndex, int type,
			String keyword, Long user_id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.getAllUser(keyword,type,user_id,pageIndex, pageSize);
	}

	// 获取所有发现用户黑名单
	@RequestMapping(value = "/list_user_black")
	public @ResponseBody ModelMap list_user_black(HttpServletRequest request,String nick_name,String mobile, int pageSize, int pageIndex) {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return  managerService.getBlackUsers(nick_name,mobile,pageIndex, pageSize);
	}
	 
	@RequestMapping(value = "/list_user_found")
	public @ResponseBody ModelMap list_user_found(HttpServletRequest request,Long user_id, int pageSize, int pageIndex) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		 return managerService.getFoundUsers(user_id, pageIndex, pageSize);
	}

	// 获取所有用户
	@RequestMapping(value = "/list_user_meet_bottle_recommend")
	public @ResponseBody ModelMap list_user_meet_bottle_recommend(HttpServletRequest request, int pageSize,
			int pageIndex, String keyword) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return  managerService.getAllMeetBottleRecommendUser(pageSize, pageIndex, keyword);
	}

	@RequestMapping(value = "/add_user_black")
	public @ResponseBody ModelMap addToUserBlack(HttpServletRequest request, long user_id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		userService.setUserSysStatusTo(user_id, SysUserStatus.BLACK);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping(value = "/nick_name_illegal")
	public @ResponseBody ModelMap add_user_found(HttpServletRequest request, long user_id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return ResultUtil.getResultOKMap().addAttribute("nick_name", userService.nickNameIllegal(user_id));
	}

	// 添加到发现用户黑名单
	@RequestMapping(value = "/remove_user_black")
	public @ResponseBody ModelMap remove_user_black(HttpServletRequest request,String nick_name,String mobile, long uid, Integer pageSize,
			Integer pageIndex) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		userService.setUserSysStatusTo(uid, SysUserStatus.NORMAL);
		return list_user_black(request,nick_name,mobile, pageSize, pageIndex);
	}

	@RequestMapping(value = "/remove_user_found")
	public @ResponseBody ModelMap remove_user_found(HttpServletRequest request, Long user_id,long uid, Integer pageSize,
			Integer pageIndex) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		userService.setUserFoundFn(uid, UserFnStatus.DEFAULT);
		return  list_user_found(request, user_id, pageSize, pageIndex);
	}

	// 获取提现申请记录
	@RequestMapping(value = "/list_exchange_history")
	public @ResponseBody ModelMap list_exchange_history(HttpServletRequest request, int pageSize, int pageIndex,
			int type) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		return  managerService.getExchangeHistory(pageSize, pageIndex, type);
	}

	@RequestMapping(value = "/exchange_handle")
	public @ResponseBody ModelMap exchange_handle(HttpServletRequest request, int id, boolean agreeOrReject,
			int pageSize, int pageIndex, int type) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.handleExchange(id, agreeOrReject);
		return list_exchange_history(request, pageSize, pageIndex, type);
	}

	// 获取提现申请记录
	@RequestMapping(value = "/list_report_history")
	public @ResponseBody ModelMap list_report_history(HttpServletRequest request, int pageSize,
			int pageIndex) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return  managerService.getReports(pageSize, pageIndex);
	}

	// 获取提现申请记录
	@RequestMapping(value = "/report_to_black")
	public @ResponseBody ModelMap handleReport(HttpServletRequest request, int id,long uid, int pageSize,
			int pageIndex) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		userService.setUserSysStatusTo(uid, SysUserStatus.BLACK);
		managerService.deleteReport(id);
		return list_report_history(request, pageSize, pageIndex);
	}
	@RequestMapping(value = "/report_to_normal")
	public @ResponseBody ModelMap report_to_normal(HttpServletRequest request, int id,int pageSize,
			int pageIndex) {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		managerService.deleteReport(id);
		return list_report_history(request, pageSize, pageIndex);
	}

	@RequestMapping(value = "/list_bottle")
	public @ResponseBody ModelMap list_bottle(HttpServletRequest request, Long user_id,String nick_name,int status, int type, int pageSize,
			int pageIndex) {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		 return  managerService.listBottleByState(user_id,nick_name,status, type, pageSize, pageIndex);
	}
	
	@RequestMapping(value = "/list_bottle_black")
	public @ResponseBody ModelMap list_bottle_black(HttpServletRequest request, Long user_id,String nick_name, int pageSize,
			int pageIndex) {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		 return  managerService.listBottleByState(user_id,nick_name, BottleState.BLACK.ordinal(),-1, pageSize, pageIndex);
	}
	

	// 获取提现申请记录
	@RequestMapping(value = "/changeBottleStatus")
	public @ResponseBody ModelMap changeBottleState(HttpServletRequest request, Long user_id,String nick_name, int id,int status, int type,
			int pageSize, int pageIndex, int to_state) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if(to_state>=0) {
			managerService.changeBottleState(id, to_state);
		}
		if(to_state==-1) { //删除瓶子
			managerService.deleteBottle(id);
		}
		if(to_state==-2) { //该用户加入黑名单
			managerService.addUserToBlackByBottleID(id);
		}
		return list_bottle(request, user_id,nick_name,status, type, pageSize, pageIndex);
	}

	@RequestMapping(value = "/edit_key_words")
	public @ResponseBody ModelMap add_black_words(int type, HttpServletRequest request) throws IOException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (request instanceof MultipartHttpServletRequest) {
			DefaultMultipartHttpServletRequest dRequest = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = dRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = dRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					String mgc = new String(file.getBytes(), "UTF-8");
					if (type == 1) {
						commAsyncTask.addMGC(mgc);
					} else {
						commAsyncTask.delMGC(mgc);
					}

				}
			}
		}

		return ResultUtil.getResultOKMap();
	}

	// 获取提现申请记录
	@RequestMapping(value = "/list_spread_user")
	public @ResponseBody ModelMap list_spread_user(HttpServletRequest request) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		return managerService.getSpecialUsers(1, 1000);
	}

	@RequestMapping(value = "/add_spread_user")
	public @ResponseBody ModelMap add_spread_user(HttpServletRequest request, long uid) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

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
	public @ResponseBody ModelMap del_spread_user(HttpServletRequest request, long uid) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		int count = managerService.delSpecialUser(uid);
		if (count == 1) {
			return managerService.getSpecialUsers(1, 1000);
		} else {
			return ResultUtil.getResultOKMap().addAttribute("count", count);
		}
	}

	@RequestMapping(value = "/edit_avatar_state")
	public @ResponseBody ModelMap edit_avatar_state(HttpServletRequest request, int id, int state) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.editAvatarState(id, state);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping(value = "/edit_avatar_state_by_user_id")
	public @ResponseBody ModelMap edit_avatar_state_by_user_id(HttpServletRequest request, long user_id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.editAvatarStateToIllegal(user_id);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping(value = "/edit_avatar_state_to_illegal")
	public @ResponseBody ModelMap edit_avatar_state_to_illegal(HttpServletRequest request, long user_id) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.editAvatarStateToIllegal(user_id);
		return ResultUtil.getResultOKMap();
	}

	// 获取需要審核的用戶头像
	@RequestMapping(value = "/list_confirm_avatars")
	public @ResponseBody ModelMap list_confirm_avatars(HttpServletRequest request, int pageSize, int pageIndex,
			Long user_id, int state) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.listConfirmAvatars(pageSize, pageIndex, user_id, state);
	}
	// 获取需要審核的用戶头像
	@RequestMapping(value = "/list_avatars_by_uid")
	public @ResponseBody ModelMap list_avatars_by_uid(HttpServletRequest request, int pageSize, int pageIndex,
			Long user_id, String nick_name) {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		 return managerService.listAvatarsByUid(pageSize, pageIndex, user_id, nick_name);
	}

	// 充值会员
	@RequestMapping(value = "/charge_vip")
	public @ResponseBody ModelMap charge_vip(HttpServletRequest request, long user_id, int month, String mark) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		managerService.charge_vip(user_id, month, mark);
		return ResultUtil.getResultOKMap();
	}

	// 充值会员
	@RequestMapping(value = "/charge_coin")
	public @ResponseBody Object charge_coin(HttpServletRequest request, long user_id, int coin, String mark) {

		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		return managerService.charge_coin(user_id, coin, mark);
	}

	// 修改管理员密码
	@RequestMapping(value = "/modify_pwd")
	public @ResponseBody ModelMap modify_pwd(HttpServletRequest request, String old_pwd, String new_pwd,
			String confirm_pwd) throws NoSuchAlgorithmException {

		String currentManagerName = managerService.getManagerAuthName(request);

		if (currentManagerName == null || !managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(old_pwd)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "旧密码不能为空");
		}

		boolean r = managerService.mLogin(request, currentManagerName, MD5Util.getMd5(old_pwd));
		if (!r) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "旧密码不正确");
		}

		if (old_pwd.equals(new_pwd)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "新密码不能跟旧密码一样");
		}

		if (!new_pwd.equals(confirm_pwd)) {
			return ResultUtil.getResultMap(ERROR.ERR_FAILED, "新密码两次不一致");
		}
		managerService.change_pwd(currentManagerName, new_pwd);
		return ResultUtil.getResultOKMap();
	}

	// -------------------------------约会相关--------------------------------------------------
	@RequestMapping(value = "/appointment_list")
	public @ResponseBody ModelMap appointment_list(HttpServletRequest request,Long user_id,String nick_name,int status , int page, int count)
			throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		
		return managerService.loadAppointMents(user_id,nick_name,status, page, count);
	}

	@RequestMapping(value = "/changeAppointMentStatus")
	public @ResponseBody ModelMap changeAppointMentStatus(HttpServletRequest request,Long user_id,String nick_name, int id, int status, int page,
			int count, int to_state) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		appointmentService.changeStatus(id, to_state);
		return appointment_list(request,user_id,nick_name, status, page, count);
	}

	// ------------------------------短视频相关--------------------------------------------------
	@RequestMapping(value = "/shortvideo_list")
	public @ResponseBody ModelMap shortvideo_list(HttpServletRequest request,Long user_id,String nick_name, int status, int page, int count) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.loadShortvideos(user_id,nick_name,status, page, count);
	}

	@RequestMapping(value = "/changeShortvideoStatus")
	public @ResponseBody ModelMap changeShortvideoStatus(HttpServletRequest request,Long user_id,String nick_name, int id, int status, int page,
			int count, int to_state) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		managerService.changeShortvideoStatus(id, to_state);
		return managerService.loadShortvideos(user_id,nick_name,status, page, count);
	}

	@RequestMapping(value = "/shortvideo_list_cert")
	public @ResponseBody ModelMap shortvideo_list_cert(HttpServletRequest request, int status, int page, int count,
			int isUserCert) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.loadUserCertVideos(status, page, count);
	}

	@RequestMapping(value = "/user_shortvideo_cert_ok")
	public @ResponseBody ModelMap user_shortvideo_cert_ok(HttpServletRequest request, int id, long uid, int isOK,
			int status, int page, int count) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.userShortvideoCert(id, uid, isOK, status, page, count);
	}

	@RequestMapping(value = "/dynamic_comment_list")
	public @ResponseBody ModelMap dynamic_comment_list(HttpServletRequest request, Long user_id, String nick_name, int page,
			int count) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.loadDynamicComment(user_id,nick_name, page, count);
	}

	@RequestMapping(value = "/change_dynamic_comment_status")
	public @ResponseBody ModelMap change_dynamic_comment_status(HttpServletRequest request, Long user_id,String nick_name, int id,
			int toStatus, int page, int count) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.change_dynamic_comment_status(user_id,nick_name, id,  page, count,toStatus);
	}

	@RequestMapping(value = "/gift_history_list")
	public @ResponseBody ModelMap gift_history_list(HttpServletRequest request, Long user_id, int page, int count)
			throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.loadGiftHistoryList(user_id, page, count);
	}

	@RequestMapping(value = "/load_signature_update_users")
	public @ResponseBody ModelMap loadSignatureUpdateUsers(HttpServletRequest request, Long user_id, int page,
			int count) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.loadSignatureUpdateUsers(user_id, page, count);
	}

	@RequestMapping(value = "/delete_user_signature")
	public @ResponseBody ModelMap deleteUserSignature(HttpServletRequest request, long uid, Long user_id, int page,
			int count) throws NoSuchAlgorithmException {
		if (!managerService.isLogin(request)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return managerService.deleteUserSignature(uid, user_id, page, count);
	}
}
