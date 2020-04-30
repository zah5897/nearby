package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VideoComment;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.service.VideoService;
import com.zhan.app.nearby.task.CommAsyncTask;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.DeviceUtil;
import com.zhan.app.nearby.util.IPUtil;
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
	@Autowired
	private UserDynamicService userDynamicService;
	@Autowired 
	private CommAsyncTask commAsyncTask;
	
	@RequestMapping("send")
	@ApiOperation(httpMethod = "POST", value = "发送短视频") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "aid", value = "aid", required = true, paramType = "query"),
			@ApiImplicitParam(name = "title", value = "视频标题", paramType = "query"),
			@ApiImplicitParam(name = "type", value = "视频标题,0为短视频,3为头像认证短视频,默认为0", paramType = "query",dataType = "Integer"),
			@ApiImplicitParam(name = "video_name", value = "视频上传在UCloud上面的文件名称", required = true, paramType = "query"),
			@ApiImplicitParam(name = "thumb_img_name", value = "视频预览图，上传在UCloud上面的文件名称", paramType = "query"),
			@ApiImplicitParam(name = "secret_level", value = "视频等级，0为公开，1为私密，默认公开",dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "duration", value = "视频时长单位秒", required = true, paramType = "query",dataType = "Float") })
	public ModelMap send(HttpServletRequest request, long user_id, String token, String aid, String title, String video_name, String thumb_img_name,
			float duration,Integer type,Integer secret_level,String _ua,String lat,String lng,String addr,String ios_addr) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		
		if(secret_level==null) {
			secret_level=0;
		}
		
		if(type==null) {
			type=0;
		}
		 
		if (TextUtils.isEmpty(video_name)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "video_name is null");
		}
		if (duration < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "duration < 1 error.");
		}
		
		
		if(type==3) {
		   int count=videoService.getTodayConfirmVideCount(user_id);
		   if(count>=3) {//超限了
			   return ResultUtil.getResultMap(ERROR.ERR_CONFIRM_VIDEO_LIMIT);
		   }
		}

		Video video = new Video();
		video.setUid(user_id);
		video.setTitle(title);
		video.setType(type);
		video.setDuration(duration);
		video.setVideo_name(video_name);
		video.setThumb_img_name(thumb_img_name);
		video.setCreate_time(new Date());
		video.setSecret_level(secret_level);
		videoService.save(video);
		
		ImagePathUtil.completeVideoPath(video);
		UserDynamic dynamic=new UserDynamic();
		dynamic.set_from(DeviceUtil.getRequestDevice(_ua));
		dynamic.setIp(IPUtil.getIpAddress(request));
		dynamic.setUser_id(user_id);
		String content = BottleKeyWordUtil.filterContent(title);
		dynamic.setDescription(content);
		dynamic.setState(DynamicState.CREATE.ordinal());
		dynamic.setLocal_image_name(thumb_img_name);
		dynamic.setCreate_time(new Date());
		dynamic.setLat(lat);
		dynamic.setLng(lng);
		dynamic.setAddr(addr);
		
		dynamic.setVideo_file_short_name(video_name);
		dynamic.setDuration(duration);
		dynamic.setType(1);
		dynamic.setSecret_level(secret_level);
		commAsyncTask.getDynamicLocation(IPUtil.getIpAddress(request), dynamic, ios_addr);
		userDynamicService.insertDynamic(dynamic);
		
		return ResultUtil.getResultOKMap().addAttribute("data", video);
	}

	@RequestMapping("mine")
	@ApiOperation(httpMethod = "POST", value = "获取当前账号的短视频列表") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token",   paramType = "query"),
			@ApiImplicitParam(name = "last_id", value = "上一页最后一条的id值",  paramType = "query"),
			@ApiImplicitParam(name = "count", value = "count", required = true, paramType = "query") })
	public ModelMap mine(long user_id, String token, Long last_id, int count) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		List<Video> list = videoService.mine(user_id, last_id, count);
		if(!list.isEmpty()) {
			last_id=list.get(list.size()-1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("data", list).addAttribute("hasMore", list.size() == count).addAttribute("last_id", last_id);
	}

	@RequestMapping("list")
	@ApiOperation(httpMethod = "POST", value = "获取所有已审核通过的短视频") // swagger 当前接口注解
	@ApiImplicitParams({
			@ApiImplicitParam(name = "last_id", value = "上一页最后一条的id值",   paramType = "query"),
			@ApiImplicitParam(name = "type", value = "视频标题,0为普通短视频，1为头像视频，2为发布的动态视频,3为认证短视频",  paramType = "query"),
			@ApiImplicitParam(name = "secret_level", value = "视频等级，0为公开，1为私密，默认公开",  paramType = "query"),
			@ApiImplicitParam(name = "count", value = "count", required = true, paramType = "query") })
	public ModelMap list(long user_id,Long last_id, int count,Integer type,Integer secret_level) {
		List<Video> list = videoService.list(user_id,last_id, count,type,secret_level);
		if(!list.isEmpty()) {
			last_id=list.get(list.size()-1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("data", list).addAttribute("hasMore", list.size() == count).addAttribute("last_id", last_id);
	}
	
	@RequestMapping("load/{uid}")
	@ApiOperation(httpMethod = "POST", value = "获取某人已经通过审核的短视频列表") // swagger 当前接口注解
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uid", value = "路径变量，对应用户的user_id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "last_id", value = "上一页最后一条的id值", required = true, paramType = "query"),
			@ApiImplicitParam(name = "count", value = "count", required = true, paramType = "query") })
	public ModelMap load(@PathVariable Long uid, Long last_id, int count) {
		List<Video> list = videoService.loadByUid(uid, last_id, count);
		if(!list.isEmpty()) {
			last_id=list.get(list.size()-1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("data", list).addAttribute("hasMore", list.size() == count).addAttribute("last_id", last_id);
	}
	

	@RequestMapping("load_latest_confirm_video/{uid}")
	@ApiOperation(httpMethod = "POST", value = "获取某人最新的头像短视频") // swagger 当前接口注解
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uid", value = "路径变量，对应用户的user_id", required = true, paramType = "query") 
		 })
	public ModelMap load_latest_confirm_video(@PathVariable Long uid   ) {
		Video video = videoService.loadLatestConfirmVideo(uid);
		return ResultUtil.getResultOKMap().addAttribute("video", video);
	}
	
	@RequestMapping("load_confirmd_video/{uid}")
	@ApiOperation(httpMethod = "POST", value = "获取某人已审核的最近的一条数据") // swagger 当前接口注解
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uid", value = "路径变量，对应用户的user_id", required = true, paramType = "query") 
		  })
	public ModelMap load_confirmd_video(@PathVariable Long uid) {
		Video video = videoService.loadConfirmdVideo(uid);
		return ResultUtil.getResultOKMap().addAttribute("video", video);
	}
	
	
	@RequestMapping("comment")
	@ApiOperation(httpMethod = "POST", value = "评论视频") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "content", value = "content", required = true, paramType = "query"),
			@ApiImplicitParam(name = "time_point", value = "评论时，当前视频播放的时间点，单位秒，没开始播放为0，播放结束时为-1，默认为0", paramType = "query") })
	public ModelMap comment(long user_id, String token, String id, String content, Integer time_point) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		VideoComment comment=new VideoComment();
		comment.setUid(user_id);
		comment.setTime_point(time_point==null?0:time_point);
		comment.setCreate_time(new Date());
		comment.setVideo_id(id);
		comment.setContent(BottleKeyWordUtil.filterContent(content));
		videoService.comment(comment);
		return ResultUtil.getResultOKMap().addAttribute("data", comment);
	}
	
	
	@RequestMapping("list_comment")
	@ApiOperation(httpMethod = "POST", value = "获取某视频评论列表") // swagger 当前接口注解
	@ApiImplicitParams({
			@ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "last_id", value = "last_id", paramType = "query"),
			@ApiImplicitParam(name = "count", value = "count", required = true, paramType = "query") })
	public ModelMap list_comment(long user_id,String id, Integer last_id, int count) {
		List<VideoComment> list = videoService.listComment(user_id,id, last_id, count);
		if(!list.isEmpty()) {
			last_id=list.get(list.size()-1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("data", list).addAttribute("hasMore", list.size() == count).addAttribute("last_id", last_id);
	}
	
	@RequestMapping("praise")
	@ApiOperation(httpMethod = "POST", value = "视频点赞") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query")
			  })
	public ModelMap praise(long user_id, String token, long id) {
		videoService.praise(user_id, id);
		return ResultUtil.getResultOKMap();
	}
	
	@RequestMapping("cancel_praise")
	@ApiOperation(httpMethod = "POST", value = "取消视频点赞") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query")
			  })
	public ModelMap cancel_praise(long user_id, String token, long id) {
		videoService.cancelPraise(user_id, id);
		return ResultUtil.getResultOKMap();
	}
	
	@RequestMapping("store")
	@ApiOperation(httpMethod = "POST", value = "收藏视频") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query")
			  })
	public ModelMap store(long user_id, String token, long id) {
		videoService.store(user_id, id);
		return ResultUtil.getResultOKMap();
	}
	@RequestMapping("cancel_store")
	@ApiOperation(httpMethod = "POST", value = "取消收藏视频") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query")
			  })
	public ModelMap cancel_store(long user_id, String token, long id) {
		videoService.cancelStore(user_id, id);
		return ResultUtil.getResultOKMap();
	}
	@RequestMapping("share")
	@ApiOperation(httpMethod = "POST", value = "分享视频，通知服务器变动分享次数") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query")
			  })
	public ModelMap share(long user_id, String token, long id) {
		videoService.addShareCount(id);
		return ResultUtil.getResultOKMap();
	}
	@RequestMapping("scan")
	@ApiOperation(httpMethod = "POST", value = "提交浏览，通知服务器变动浏览次数") // swagger 当前接口注解
	@ApiImplicitParams({  
			    @ApiImplicitParam(name = "id", value = "视频id", required = true, paramType = "query")
			  })
	public ModelMap scan(long id) {
		videoService.addScanCount(id);
		return ResultUtil.getResultOKMap();
	}
}
