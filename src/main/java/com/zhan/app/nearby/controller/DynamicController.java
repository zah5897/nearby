package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.LikeDynamicState;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {
	@Resource
	private UserDynamicService userDynamicService;

	@Resource
	private DynamicMsgService dynamicMsgService;
	@Resource
	private UserService userService;

	@RequestMapping("comment")
	public ModelMap comment(DynamicComment comment) {
		if (comment.getUser_id() < 1L) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户id异常");
		}
		comment.setComment_time(new Date());
		long id = userDynamicService.comment(comment);

		ModelMap result;
		if (id > 0) {
			DynamicComment resultObj = userDynamicService.loadComment(comment.getDynamic_id(), id);
			ImagePathUtil.completeCommentImagePath(resultObj, true);
			result = ResultUtil.getResultOKMap();
			result.put("comment", resultObj);
			long userId = userDynamicService.getUserIdByDynamicId(comment.getDynamic_id());
			if (userId > 0) {
				dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_COMMENT, comment.getUser_id(),
						comment.getDynamic_id(), userId, comment.getContent());
			     
			}
		} else {
			result = ResultUtil.getResultMap(ERROR.ERR_FAILED, "评论失败");
		}

		return result;
	}

	@RequestMapping("comment_list")
	public ModelMap comment_list(Long dynamic_id, Integer count, Long last_comment_id) {
		if (dynamic_id == null || dynamic_id < 1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		if (count == null || count < 1) {
			count = 5;
		}
		List<DynamicComment> comments = userDynamicService.commentList(dynamic_id, count, last_comment_id);
		ModelMap result = ResultUtil.getResultOKMap();
		ImagePathUtil.completeCommentImagePath(comments, true);
		result.put("comments", comments);
		long last_id = 0l;
		if (comments != null && comments.size() > 0 && comments.size() == count) {
			last_id = comments.get(comments.size() - 1).getId();
		}

		result.put("hasMore", last_id > 0);

		result.put("last_id", last_id);
		return result;
	}

	@RequestMapping("detail")
	public ModelMap detail(Long dynamic_id, Long user_id) {
		if (dynamic_id == null || dynamic_id < 1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		UserDynamic dynamic = userDynamicService.detail(dynamic_id, user_id);
		ModelMap result;
		if (dynamic != null) {
			ImagePathUtil.completeDynamicPath(dynamic, true);
			ImagePathUtil.completeAvatarPath(dynamic.getUser(), true);
			userDynamicService.updateBrowserCount(dynamic.getId(), dynamic.getBrowser_count() + 1);
			result = ResultUtil.getResultOKMap();
			result.put("detail", dynamic);
		} else {
			result = ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "该动态不存在或被删除");
			result.put("dynamic_id", dynamic_id);
		}
		return result;
	}

	@RequestMapping("msg_list")
	public ModelMap msg_list(Long user_id, Long last_id,Integer type) {
		if (user_id == null || user_id < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "user_id参数异常：user_id=" + user_id);
		}

		if (last_id == null) {
			last_id = 0l;
		}
		List<DynamicMessage> msgs = dynamicMsgService.msg_list(user_id, last_id,type==null?0:1);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("msgs", msgs);
		return result;
	}

	@RequestMapping("like")
	public ModelMap like(Long user_id, String dynamic_id) {
		if (user_id == null || user_id < 1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定当前用户：user_id=" + user_id);
		}
		if (TextUtils.isEmpty(dynamic_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定您要操作的动态信息");
		}

		String[] dy_ids = dynamic_id.split(",");
		for (String id : dy_ids) {
			try {
				long dy_id = Long.parseLong(id);
				int count = userDynamicService.praiseDynamic(dy_id, true);
				if (count > 0) {
					userDynamicService.updateLikeState(user_id, dy_id, LikeDynamicState.LIKE);
					long userId = userDynamicService.getUserIdByDynamicId(dy_id);
					dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_PRAISE, user_id, dy_id, userId, "有人喜欢了你的动态");
				}

			} catch (NumberFormatException e) {
			}
		}
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("unlike")
	public ModelMap unlike(Long user_id, String dynamic_id) {
		if (user_id == null || user_id < 1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定当前用户：user_id=" + user_id);
		}
		if (TextUtils.isEmpty(dynamic_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定您要操作的动态信息");
		}
		String[] dy_ids = dynamic_id.split(",");
		for (String id : dy_ids) {
			try {
				long dy_id = Long.parseLong(id);
				userDynamicService.praiseDynamic(dy_id, false);
				userDynamicService.updateLikeState(user_id, dy_id, LikeDynamicState.UNLIKE);
			} catch (NumberFormatException e) {
			}
		}
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("delete")
	public ModelMap delete(HttpServletRequest request, Long user_id, String dynamic_id) {
		if (user_id == null || user_id < 1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定当前用户：user_id=" + user_id);
		}
		if (TextUtils.isEmpty(dynamic_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定您要操作的动态信息");
		}
		String id = userDynamicService.delete(request.getServletContext(), user_id, dynamic_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("delete_id", id);
		return result;
	}
	
	/**
	 * 获取用户图片列表
	 * 
	 * @param user_id
	 * @param last_image_id
	 * @param count
	 * @return
	 */
	@RequestMapping("list_image")
	public ModelMap list_image(Long user_id, Long last_id, Integer count) {
		return userDynamicService.getUserImages(user_id, last_id, count);
	}
	

}
