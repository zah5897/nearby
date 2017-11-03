package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.GiftService;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@RestController
@RequestMapping("/msg")
public class MsgController {
	@Resource
	private DynamicMsgService dynamicMsgService;

	@Resource
	private GiftService giftService;
	
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
				e.printStackTrace();
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
	public ModelMap notice(Long last_id) {
		giftService.loadGiftGiveList(last_id);
		return dynamicMsgService.noticeList(last_id);
	}
	
}
