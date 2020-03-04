package com.zhan.app.nearby.controller;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
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
			@ApiImplicitParam(name = "description", value = "约会描述", paramType = "query"),
			@ApiImplicitParam(name = "theme", value = "主题", paramType = "query", dataType = "Integer"),
			@ApiImplicitParam(name = "city_id", value = "约会城市id", paramType = "query"),
			@ApiImplicitParam(name = "appointment_time", value = "约会的具体时间，yyyy-MM-dd hh:mm:ss", paramType = "query"),
			@ApiImplicitParam(name = "addr", value = "约会地址", paramType = "query"),
			@ApiImplicitParam(name = "ii", value = "零时参数，忽略", paramType = "query"),
			@ApiImplicitParam(name = "image", value = "图片", paramType = "query") })
	public ModelMap publish(long user_id, String token, String description, String theme, Integer city_id,
			String appointment_time, String addr, String image) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}

		if (TextUtils.isEmpty(theme)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "主题不能为空");
		}

		if (TextUtils.isEmpty(appointment_time)) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "约会时间不能为空");
		}

		Appointment appointment = new Appointment();
		appointment.setUid(user_id);
		appointment.setDescription(description);
		appointment.setTheme(theme);
		appointment.setAddr(addr);
		try {
			appointment.setAppointment_time(DateTimeUtil.parse(appointment_time));
		} catch (ParseException e) {
			e.printStackTrace();
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "约会时间参数格式错误");
		}
		if (city_id != null) {
			appointment.setCity_id(city_id);
		}
		appointment.setImage(image);
		appointmentService.save(appointment);
		ImagePathUtil.completePath(appointment);
		return ResultUtil.getResultOKMap().addAttribute("data", appointment);
	}

	@RequestMapping("list")
	@ApiOperation(httpMethod = "POST", value = "获取最新的约会列表") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "last_id", value = "分页id", dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "count", value = "主题", paramType = "query", dataType = "Integer") })
	public ModelMap list(long user_id, Integer last_id, int count) {
		List<Appointment> apps = appointmentService.list(user_id, last_id, count);
		ImagePathUtil.completePath(apps);
		boolean hasMore = apps.size() == count;
		if(!apps.isEmpty()) {
			last_id = apps.get(apps.size() - 1).getId();
		}
		return ResultUtil.getResultOKMap().addAttribute("data", apps).addAttribute("hasMore", hasMore)
				.addAttribute("last_id", last_id);
	}

	@RequestMapping("del")
	@ApiOperation(httpMethod = "POST", value = "删除约会信息") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "用户id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "用户登录token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "id", value = "对应id", paramType = "query", dataType = "Integer") })
	public ModelMap del(long user_id, String token, Integer id) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		appointmentService.del(user_id, id);
		return ResultUtil.getResultOKMap();
	}

}
