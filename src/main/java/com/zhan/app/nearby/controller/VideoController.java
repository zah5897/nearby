package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.service.VideoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/video")
@Api(value = "视频通话", description = "视频通话 api")
public class VideoController {

	@Resource
	private VideoService videoService;

	@RequestMapping("live")
	@ApiOperation(httpMethod = "POST", value = "通知服务器视频通话正常进行中") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "aid", value = "aid", required = true, paramType = "query"),
			@ApiImplicitParam(name = "client_uuid", value = "客户端发起会话时唯一标识", required = true, paramType = "query"),
			@ApiImplicitParam(name = "time", value = "客户端通话计时 ，单位s", required = true, paramType = "query") })
	public Map<String, Object> live(long user_id, String token, String aid, String client_uuid, int time) {
		Video video = new Video();
		video.setId(UUID.randomUUID().toString());
		video.setUid(user_id);
		video.setType(0);
		video.setClient_uuid(client_uuid);
		video.setCreate_time(new Date());
		video.setTime_value(time);
		return videoService.live(token, aid, video);
	}

}
