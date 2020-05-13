package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.DynamicCommentStatus;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.LikeDynamicState;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.GiftService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {
	private static Logger log = Logger.getLogger(DynamicController.class);
	@Resource
	private UserDynamicService userDynamicService;

	@Resource
	private DynamicMsgService dynamicMsgService;
	@Resource
	private UserService userService;

	@Resource
	private GiftService giftService;
	@Autowired
	private UserCacheService userCacheService;
	@RequestMapping("comment")
	public ModelMap comment(DynamicComment comment,String token) {
		
		if (comment.getUser_id() <= 0 && !userService.checkLogin(comment.getUser_id(), token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		
		long lastTIme=userCacheService.getCommentLastTime(comment.getUser_id());
		if(System.currentTimeMillis()/1000-lastTIme<10) {//每次评论不能少于10秒
			 return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		}
		userCacheService.putCommentLastTime(comment.getUser_id());
		
		
		comment.setComment_time(new Date());

		// 敏感词过滤
		String newContent = BottleKeyWordUtil.filterContent(comment.getContent());
		comment.setContent(newContent);
		comment.setStatus(DynamicCommentStatus.CREATE.ordinal());
		long id = userDynamicService.comment(comment);
		ModelMap result;
		if (id > 0) {
			DynamicComment resultObj = userDynamicService.loadComment(comment.getDynamic_id(), id);
			ImagePathUtil.completeCommentImagePath(resultObj, true);
			result = ResultUtil.getResultOKMap();
			result.put("comment", resultObj);
			long userId = userDynamicService.getUserIdByDynamicId(comment.getDynamic_id());
			if (userId > 0) {
				dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_COMMENT, comment.getUser_id(), id, userId,
						comment.getContent());

			}
		} else {
			result = ResultUtil.getResultMap(ERROR.ERR_FAILED, "评论失败");
		}

		return result;
	}

	@RequestMapping("list_video")
	@ApiOperation(httpMethod = "POST", value = "获取短视频动态") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "last_id", value = "上一页最后一条的id值", paramType = "query"),
			@ApiImplicitParam(name = "secret_level", value = "视频等级，0为公开，1为私密", paramType = "query"),
			@ApiImplicitParam(name = "count", value = "count", required = true, paramType = "query") })
	public ModelMap list(long user_id, Long last_id, int count, Integer type, Integer secret_level) {
		List<UserDynamic> dys = userDynamicService.loadVideoDynamic(user_id, last_id, count, secret_level);

		if (!dys.isEmpty()) {
			last_id = dys.get(dys.size() - 1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("data", dys).addAttribute("hasMore", dys.size() == count)
				.addAttribute("last_id", last_id);
	}

	@RequestMapping("scan")
	@ApiOperation(httpMethod = "POST", value = "提交浏览，通知服务器变动浏览次数") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", value = "动态id", required = true, paramType = "query") })
	public ModelMap scan(long id) {
		userDynamicService.updateBrowserCount(id);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("comment_list")
	public ModelMap comment_list(long user_id, Long dynamic_id, Integer count, Long last_id) {
		if (dynamic_id == null || dynamic_id < 1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM);
		}
		if (count == null || count < 1) {
			count = 5;
		}
		List<DynamicComment> comments = userDynamicService.commentList(user_id, dynamic_id, count, last_id);
		ModelMap result = ResultUtil.getResultOKMap();
		ImagePathUtil.completeCommentImagePath(comments, true);
		result.put("comments", comments);
		long lastId = 0l;
		if (comments != null && comments.size() > 0 && comments.size() == count) {
			lastId = comments.get(comments.size() - 1).getId();
		}
		result.put("hasMore", lastId > 0);
		result.put("last_id", lastId);
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
			dynamic.setBrowser_count(dynamic.getBrowser_count() + 1);
			userDynamicService.updateBrowserCount(dynamic.getId());
			result = ResultUtil.getResultOKMap();
			result.put("detail", dynamic);
		} else {
			result = ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST, "该动态不存在或被删除");
			result.put("dynamic_id", dynamic_id);
		}
		return result;
	}
	
	@RequestMapping("detail_by_comment_id")
	public ModelMap detail_by_comment_id(long comment_id,Long user_id) {
	    long dynamic_id=userDynamicService.getDynamicId(comment_id);
		 return detail(dynamic_id, user_id);
	}

	@RequestMapping("comment_sub_list")
	public ModelMap comment_sub_list(long pid, long dynamic_id, Long last_id, int count) {

		List<DynamicComment> comments = userDynamicService.loadSubComm(pid, dynamic_id, count,
				last_id == null ? 0 : last_id);
		ModelMap result = ResultUtil.getResultOKMap();
		ImagePathUtil.completeCommentImagePath(comments, true);
		result.put("comments", comments);
		long lastId = 0l;
		if (comments != null && comments.size() > 0 && comments.size() == count) {
			lastId = comments.get(comments.size() - 1).getId();
		}
		result.put("hasMore", lastId > 0);
		result.put("last_id", lastId);
		return result;
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
				userDynamicService.praiseDynamic(dy_id, true);
				userDynamicService.updateLikeState(user_id, dy_id, LikeDynamicState.LIKE);
				long userId = userDynamicService.getUserIdByDynamicId(dy_id);
				int type=userDynamicService.getDynamicType(dy_id);
				if(type==1) { //视频类型
					dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_PRAISE_VIDEO, user_id, dy_id, userId, "有人喜欢了你的视频");
				}else {
					dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_PRAISE, user_id, dy_id, userId, "有人喜欢了你的动态");
				}

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("send_flower")
	public Map<String, Object> send_flower(long user_id, String token, String aid, long dynamic_id, int count) {

		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (!userDynamicService.isDynamicExist(dynamic_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_NOT_EXIST);
		}
		long userId = userDynamicService.getUserIdByDynamicId(dynamic_id);

		int gift_id = 44;

		if (count < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "赠送数量不能小于1");
		}
		Map<String, Object> giveResult = giftService.give(user_id, userId, gift_id, aid, count);

		if (((int) giveResult.get("code")) != 0) {
			return giveResult;
		}
		userDynamicService.sendFlower(user_id, dynamic_id, gift_id, count);
		dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_SEND_FLOWER, user_id, dynamic_id, userId, "有人为你的动态送花了");
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
	public ModelMap delete(Long user_id, String dynamic_id) {
		if (user_id == null || user_id < 1l) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定当前用户：user_id=" + user_id);
		}
		if (TextUtils.isEmpty(dynamic_id)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "请确定您要操作的动态信息");
		}
		String id = userDynamicService.delete(user_id, dynamic_id);
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
	public ModelMap list_image(Long user_id, Long last_id, Integer count, String version) {
		return userDynamicService.getUserImages(user_id, last_id, count, version);
	}

	@RequestMapping("replay_dynamic_msg")
	public ModelMap replay_dynamic_msg(Long user_id, long msg_id) {
		dynamicMsgService.replayDynamicMsg(user_id, msg_id);
		return ResultUtil.getResultOKMap().addAttribute("msg_id", msg_id);
	}

	@RequestMapping("follow")
	public ModelMap follow(long user_id, Long last_id, Integer count) {
		int c = count == null ? 20 : count;
		List<UserDynamic> dys = userDynamicService.loadFollow(user_id, last_id == null ? Long.MAX_VALUE : last_id, c);
		ModelMap r = ResultUtil.getResultOKMap().addAttribute("images", dys);

		if (!dys.isEmpty()) {
			r.addAttribute("last_id", dys.get(dys.size() - 1).getId());
		}
		if (c == dys.size()) {
			r.addAttribute("hasMore", true);
		} else {
			r.addAttribute("hasMore", false);
		}
		return r;
	}

}
