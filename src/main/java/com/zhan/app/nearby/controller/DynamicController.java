package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {
	@Resource
	private UserDynamicService userDynamicService;
	 
	@Resource
	private DynamicMsgService dynamicMsgService;
	
	
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
			long userId=userDynamicService.getUserIdByDynamicId(comment.getDynamic_id());
			if(userId>0){
				dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_COMMENT, comment.getUser_id(), comment.getDynamic_id(),userId, comment.getContent());
			}
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
		long last_id=-1l;
		if(comments!=null&&comments.size()>0&&comments.size()==count){
			last_id=comments.get(comments.size()-1).getId();
		}
		
		result.put("hasMore", last_id>0);
		
		result.put("last_comment_id", last_id);
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
			userDynamicService.updateBrowserCount(dynamic.getId(), dynamic.getBrowser_count()+1);
		}
		ModelMap result=ResultUtil.getResultOKMap();
				result.put("detail", dynamic);
				return result;
	}
	@RequestMapping("msg_list")
	public ModelMap msg_list(Long user_id) {
		if (user_id==null||user_id<1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"user_id参数异常：user_id="+user_id);
		}
		List<DynamicMessage> msgs = dynamicMsgService.msg_list(user_id);
		ModelMap result=ResultUtil.getResultOKMap();
		result.put("msgs", msgs);
		return result;
	}
	
	
	
}
