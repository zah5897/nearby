package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.service.VideoService;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/short_video")
@Api(value = "短视频", description = "短视频api")
public class VideoController {

	@Resource
	private VideoService videoService;
	@Resource
	private UserService userService;

	@RequestMapping("send")
	@ApiOperation(httpMethod = "POST", value = "发送短视频") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "aid", value = "aid", required = true, paramType = "query"),
			@ApiImplicitParam(name = "title", value = "视频标题", required = true, paramType = "query"),
			@ApiImplicitParam(name = "video_name", value = "视频上传在UCloud上面的文件名称", required = true, paramType = "query"),
			@ApiImplicitParam(name = "thumb_img_name", value = "视频预览图，上传在UCloud上面的文件名称", paramType = "query"),
			@ApiImplicitParam(name = "duration", value = "视频时长单位秒",required = true, paramType = "query") })
	public ModelMap send(long user_id, String token, String aid, String title, String video_name, String thumb_img_name,
			int duration) {
//		if(!userService.checkLogin(user_id, token)) {
//			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
//		}
		
		if(TextUtils.isEmpty(title)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"title is null");
		}
		if(TextUtils.isEmpty(video_name)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"video_name is null");
		}
		if(duration<1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"duration < 1 error.");
		}
		
		Video video = new Video();
		video.setUid(user_id);
		video.setTitle(title);
		video.setDuration(duration);
		video.setVideo_name(video_name);
		video.setThumb_img_name(thumb_img_name);
		video.setCreate_time(new Date());
		videoService.save(video);
		ImagePathUtil.completeVideoPath(video);
		return ResultUtil.getResultOKMap().addAttribute("data", video);
	}

	@RequestMapping("mine")
	@ApiOperation(httpMethod = "POST", value = "获取当前账号的短视频列表") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "page", value = "page", required = true, paramType = "query"),
			@ApiImplicitParam(name = "count", value = "count", required = true, paramType = "query") })
	public ModelMap mine(long user_id, String token, int page, int count) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		List<Video> list = videoService.mine(user_id, page, count);
		return ResultUtil.getResultOKMap().addAttribute("data", list).addAttribute("hasMore", list.size()==count);
	}

	@RequestMapping("list")
	@ApiOperation(httpMethod = "POST", value = "获取他人短视频列表") // swagger 当前接口注解
	@ApiImplicitParams({
			@ApiImplicitParam(name = "target_user_id", value = "对应用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "page", value = "page", required = true, paramType = "query"),
			@ApiImplicitParam(name = "count", value = "count", required = true, paramType = "query") })
	public ModelMap list(long target_user_id, int page, int count) {
		List<Video> list = videoService.list(target_user_id, page, count);
		return ResultUtil.getResultOKMap().addAttribute("data", list).addAttribute("hasMore", list.size()==count);
	}
}
