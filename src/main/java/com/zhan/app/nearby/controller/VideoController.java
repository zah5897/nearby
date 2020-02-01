package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

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
@RequestMapping("/video")
@Api(value = "短视频", description = "短视频api")
public class VideoController {

	@Resource
	private VideoService videoService;
	@Resource
	private UserService userService;

	@RequestMapping("send")
	@ApiOperation(httpMethod = "POST", value = "通知服务器视频通话正常进行中") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "aid", value = "aid", required = true, paramType = "query")
			  })
	public Map<String, Object> send(long user_id, String token, String aid, Video video) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if(TextUtils.isEmpty(video.getId())) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"id不能为空");
		}
		
		video.setCreate_time(new Date());
		video.setUid(user_id);
		videoService.save(video);
		ImagePathUtil.completeVideoPath(video);
		return ResultUtil.getResultOKMap().addAttribute("data", video);

	}

}
