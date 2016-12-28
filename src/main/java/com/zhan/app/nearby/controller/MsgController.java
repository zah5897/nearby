package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

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
	@RequestMapping("update_state")
	public ModelMap delete(Long user_id, String msg_ids) {
		if(user_id==null||TextUtils.isEmpty(msg_ids)){
			return ResultUtil.getResultOKMap();
		}
		String[] ids=msg_ids.split(",");
		for(String id:ids){
			try{
			    dynamicMsgService.updateState(Long.parseLong(id));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return ResultUtil.getResultOKMap();
	}
}
