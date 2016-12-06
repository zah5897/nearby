package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/msg")
public class MsgController {
	@Resource
	private DynamicMsgService dynamicMsgService;

	@RequestMapping("delete")
	public ModelMap delete(Long user_id, long msg_id) {
		dynamicMsgService.delete(msg_id);
		ModelMap mm = ResultUtil.getResultOKMap();
		mm.put("msg_id", msg_id);
		return mm;
	}
}
