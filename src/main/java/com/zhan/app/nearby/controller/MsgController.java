package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.GiftService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@RestController
@RequestMapping("/msg")
public class MsgController {
	private static Logger log = Logger.getLogger(MsgController.class);
	@Resource
	private DynamicMsgService dynamicMsgService;

	@Resource
	private GiftService giftService;

	
	@Resource
	private UserService userService;
	
	@RequestMapping("delete")
	public ModelMap delete(Long user_id, long msg_id) {
		dynamicMsgService.delete(msg_id);
		ModelMap mm = ResultUtil.getResultOKMap();
		mm.put("msg_id", msg_id);
		return mm;
	}

	@RequestMapping("update_state")
	public ModelMap update_state(Long user_id, String msg_ids) {
		if (user_id == null || TextUtils.isEmpty(msg_ids)) {
			return ResultUtil.getResultOKMap();
		}
		String[] ids = msg_ids.split(",");
		for (String id : ids) {
			try {
				dynamicMsgService.updateState(Long.parseLong(id));
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("msg_list")
	public ModelMap msg_list(Long user_id, Long last_id, Integer type) {
		if (user_id == null || user_id < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "user_id参数异常：user_id=" + user_id);
		}

		if (last_id == null) {
			last_id = 0l;
		}
		List<DynamicMessage> msgs = dynamicMsgService.msg_list(user_id, last_id, type == null ? 0 : 1);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("msgs", msgs);
		return result;
	}

	@RequestMapping("replay")
	public ModelMap replay(long user_id, long msg_id) {
		return dynamicMsgService.replay(user_id, msg_id);
	}

	@RequestMapping("notice")
	public ModelMap notice(Integer page, Integer count) {
		if (page == null || page <=0) {
			page = 1;
		}
		if (count == null || count <= 0) {
			count = 20;
		}
		List<GiftOwn> notices = giftService.loadGiftGiveList(page, count);

		ModelMap result = ResultUtil.getResultOKMap();
		if (notices == null || notices.size() < count) {
			result.addAttribute("hasMore", false);
		} else {
			result.addAttribute("hasMore", true);
		}
		return result.addAttribute("notice", notices);
	}

	@RequestMapping("clear_meet_msg")
	public ModelMap clear_meet_msg(long user_id,String token) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		  dynamicMsgService.clearMeetMsg(user_id);
		return ResultUtil.getResultOKMap();
	}
	
	@RequestMapping("del_meet_msg")
	public ModelMap del_meet_msg(long user_id,long msg_id,String token) {
		
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		
		 dynamicMsgService.delMeetMsg(user_id, msg_id);
		return ResultUtil.getResultOKMap();
	}
	
	
	@RequestMapping("latest_msg_tip")
	public ModelMap latest_dynamic_msg_tip(long user_id) {
		return dynamicMsgService.getMyDynamicMsg(user_id,false);
	}
	
	@RequestMapping("latest_msg_tip_v2")
	public ModelMap latest_msg_tip_v2(long user_id) {
		return dynamicMsgService.getMyDynamicMsg(user_id,true);
	}
}
