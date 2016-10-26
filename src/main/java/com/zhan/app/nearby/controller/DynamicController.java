package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {
	@Resource
	private UserDynamicService userDynamicService;
	 
	
	
	
	@RequestMapping("comment")
	public ModelMap comment(DynamicComment comment) {
		if (comment.getUser_id() < 1L) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户id异常");
		}
		comment.setComment_time(new Date());
		long id = userDynamicService.comment(comment);
		
		ModelMap result;
		if (id > 0) {
			DynamicComment resultObj=userDynamicService.loadComment(comment.getDynamic_id(), id);
			result =ResultUtil.getResultOKMap();
			result.put("comment", resultObj);
		}else{
			result= ResultUtil.getResultMap(ERROR.ERR_FAILED,"评论失败");
		}
		
		return result;
	}
	@RequestMapping("comment_list")
	public ModelMap comment_list(Long dynamic_id,Integer count,Long last_comment_id) {
		if (dynamic_id==null||dynamic_id<1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		if(count==null||count<1){
			count=5;
		}
		List<DynamicComment> comments = userDynamicService.commentList(dynamic_id, count,last_comment_id);
		ModelMap result=ResultUtil.getResultOKMap();
		result.put("comments", comments);
		return result;
	}
	@RequestMapping("detail")
	public ModelMap detail(Long dynamic_id) {
		if (dynamic_id==null||dynamic_id<1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		UserDynamic dynamic = userDynamicService.detail(dynamic_id);
		if(dynamic!=null){
			ImagePathUtil.completeImagePath(dynamic, true);
			ImagePathUtil.completeAvatarPath(dynamic.getUser(), true);
		}
		ModelMap result=ResultUtil.getResultOKMap();
		
				result.put("detail", dynamic);
				return result;
	}
}
