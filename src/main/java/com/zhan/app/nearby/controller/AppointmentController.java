package com.zhan.app.nearby.controller;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.AppointmentService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.DateTimeUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.TextUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/appointment")
@Api(value = "约会相关", description = "约会相关api")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;
	@Resource
	private UserService userService;

	@RequestMapping("publish")
	@ApiOperation(httpMethod = "POST", value = "发布约会信息") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "description", value = "约会描述",required = true, paramType = "query"),
			@ApiImplicitParam(name = "theme_id", value = "主题id",required = true, paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "city_id", value = "约会城市id", paramType = "query"),
			@ApiImplicitParam(name = "appointment_time", value = "约会的具体时间，yyyy-MM-dd",required = true, paramType = "query"),
			@ApiImplicitParam(name = "time_stage", value = "时间段，上午=1，下午=2，晚上=3，",required = true, paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "Street", value = "街道信息", paramType = "query"),
			@ApiImplicitParam(name = "ios_addr", value = "ios定位信息", paramType = "query"),
			@ApiImplicitParam(name = "android_addr", value = "android定位信息", paramType = "query"),
			@ApiImplicitParam(name = "channel", value = "发布渠道",required = true, paramType = "query"),
			@ApiImplicitParam(name = "lat", value = "纬度",required = true, paramType = "query"),
			@ApiImplicitParam(name = "lng", value = "经度", required = true,paramType = "query"),
			@ApiImplicitParam(name = "image_names", value = "图片(多张用逗号隔开))", paramType = "query") })
	public ModelMap publish(long user_id, String token, String description, int theme_id, Integer city_id,
			String appointment_time, int time_stage, String Street, String ios_addr, String image_names, String channel,
			String lat, String lng, String android_addr) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(appointment_time)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "约会时间不能为空");
		}

		Appointment appointment = new Appointment();
		appointment.setUid(user_id);
		appointment.setDescription(description);
		appointment.setTime_stage(time_stage);
		
	    appointment.setTheme_id(theme_id);
		appointment.setStreet(Street);
		appointment.setLat(lat);
		appointment.setLng(lng);
		appointment.setChannel(channel);
		try {
			appointment.setAppointment_time(DateTimeUtil.parse(appointment_time, "yyyy-MM-dd"));
		} catch (ParseException e) {
			e.printStackTrace();
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "约会时间参数格式错误");
		}
		if (city_id != null) {
			appointment.setCity_id(city_id);
		}
		appointment.setImage(image_names);
		appointmentService.save(appointment, ios_addr, android_addr);
		ImagePathUtil.completePath(appointment);
		userService.updateUserLOcation(user_id,lat,lng);
		return ResultUtil.getResultOKMap().addAttribute("dating", appointment);
	}

	@RequestMapping("list")
	@ApiOperation(httpMethod = "POST", value = "获取最新的约会列表") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "last_id", value = "分页id", dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "count", value = "每页数量",required = true, paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "theme_id", value = "主题id", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "appointment_time", value = "约会时间", paramType = "query"),
			@ApiImplicitParam(name = "ii", value = "test", paramType = "query"),
			@ApiImplicitParam(name = "keyword", value = "关键字搜索（描述和street里面的关键字）)", paramType = "query"),
			@ApiImplicitParam(name = "city_id", value = "约会城市", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "time_stage", value = "时间段", paramType = "query", dataType = "Integer") })
	public ModelMap list(long user_id, Integer last_id, int count, Integer theme_id, Integer time_stage,
			String appointment_time, Integer city_id, String keyword) {
		List<Appointment> apps = appointmentService.list(user_id, last_id, count, theme_id, time_stage,
				appointment_time, city_id, keyword);
		ImagePathUtil.completePath(apps);
		boolean hasMore = apps.size() == count;
		if (!apps.isEmpty()) {
			last_id = apps.get(apps.size() - 1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("dating", apps).addAttribute("hasMore", hasMore)
				.addAttribute("last_id", last_id);
	}

	@RequestMapping("mine")
	@ApiOperation(httpMethod = "POST", value = "获取本人发布的约会信息") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "last_id", value = "分页id", dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "count", value = "每页数量",required = true, paramType = "query", dataType = "Integer") })
	public ModelMap mine(long user_id, Integer last_id, int count) {
		List<Appointment> apps = appointmentService.mine(user_id, last_id, count);
		ImagePathUtil.completePath(apps);
		boolean hasMore = apps.size() == count;
		if (!apps.isEmpty()) {
			last_id = apps.get(apps.size() - 1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("dating", apps).addAttribute("hasMore", hasMore)
				.addAttribute("last_id", last_id);
	}
	
	@RequestMapping("load")
	@ApiOperation(httpMethod = "POST", value = "根据id获取约会的详细信息") // swagger 当前接口注解
	@ApiImplicitParams({  
			@ApiImplicitParam(name = "id", value = "对应id",required = true, paramType = "query", dataType = "Integer") })
	public ModelMap load(int id) {
		 
		Appointment apps=appointmentService.load(id);
		ImagePathUtil.completePath(apps);
		return ResultUtil.getResultOKMap().addAttribute("dating", apps);
	}
	
	@RequestMapping("load_user_appointments/{uid}")
	@ApiOperation(httpMethod = "POST", value = "获取某人已审核的约会列表") // swagger 当前接口注解
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uid", value = "路径变量，对应用户的user_id", required = true, paramType = "query") ,
			@ApiImplicitParam(name = "last_id", value = "分页id",   paramType = "query") ,
			@ApiImplicitParam(name = "count", value = "每页数量", required = true , paramType = "query") 
		 })
	public ModelMap load_user_appointments(@PathVariable long uid,Integer last_id,int count) {
		List<Appointment> apps = appointmentService.loadUserAppointments(uid, last_id, count);
		ImagePathUtil.completePath(apps);
		boolean hasMore = apps.size() == count;
		if (!apps.isEmpty()) {
			last_id = apps.get(apps.size() - 1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("dating", apps).addAttribute("hasMore", hasMore)
				.addAttribute("last_id", last_id);
	}

	
	
	@RequestMapping("del")
	@ApiOperation(httpMethod = "POST", value = "删除约会信息") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id",required = true, value = "对应id", paramType = "query", dataType = "Integer") })
	public ModelMap del(long user_id, String token, Integer id) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		appointmentService.del(user_id, id);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("load_theme_data")
	@ApiOperation(httpMethod = "POST", value = "获取约会主题") // swagger 当前接口注解
	public ModelMap load_theme_data() {
		return ResultUtil.getResultOKMap().addAttribute("themes", appointmentService.listTheme());
	}

//	@RequestMapping("unlock")
//	@ApiOperation(httpMethod = "POST", value = "解锁应约") // swagger 当前接口注解
//	@ApiImplicitParams({
//		@ApiImplicitParam(name = "id", value = "对应约会id", paramType = "query", dataType = "Integer") })
//	public ModelMap unlock(long user_id,String token,String aid,int id) {
//		if(!userService.checkLogin(user_id, token)) {
//			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
//		}
//		int count= appointmentService.getAppointMentUnlockCount(user_id,id);
//		if(count>0) {
//			return ResultUtil.getResultOKMap();
//		}
//		if(!userService.isVip(user_id)) {
//			int todayCount= appointmentService.getAppointMentTodayCount(user_id);
//			if(todayCount>=5) {
//                return ResultUtil.getResultMap(ERROR.ERR_FAILED,"非vip每日只能应约5次");				
//			}
//		}
//		return ResultUtil.getResultOKMap();
//		Map<String, Object> r=userService.costCoin(user_id,aid,1);
//		if(r!=null) {
//          Object all_coinsObj=r.get("all_coins");
//          if(all_coinsObj==null) {
//        	  return ResultUtil.getResultFailed();
//          }
//          int all_coins=(int) all_coinsObj;
//          if(all_coins>=0) {
//        	  appointmentService.unlock(user_id,id);
//        	  return ResultUtil.getResultOKMap();
//          }else {
//        	  return ResultUtil.getResultMap(ERROR.ERR_COINS_SHORT);
//          }
//		}else {
//			 return ResultUtil.getResultFailed();
//		}
//	}

}
