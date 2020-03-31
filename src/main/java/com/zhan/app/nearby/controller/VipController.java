package com.zhan.app.nearby.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.Vip;
import com.zhan.app.nearby.bean.VipUser;
import com.zhan.app.nearby.service.VipService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("vip")
@Api(value = "vip相关", description = "vip相关")
public class VipController {

	@Resource
	private VipService vipService;

	@RequestMapping("list")
	public ModelMap list() {
		return vipService.list();
	}

	@RequestMapping("del")
	public ModelMap del(long id) {
		return vipService.delete(id);
	}

	@RequestMapping("add")
	public ModelMap add(Vip vip) {
		return vipService.save(vip);
	}

	@RequestMapping("save")
	public ModelMap save(Vip vip) {
		return vipService.save(vip);
	}

	@RequestMapping("global_info")
	@ApiOperation(httpMethod = "POST", value = "获取vip的总数和最近4条数据") // swagger 当前接口注解
	public ModelMap global_info() {
		return vipService.globalInfo();
	}
	
	@RequestMapping("buy")
	public Map<?, ?> buy(VipUser vipUser) {
		return vipService.buy(vipUser);
	}

	@RequestMapping("buy_notify")
	public String buy_notify(VipUser vipUser) {
		return vipService.buy_notify(vipUser);
	}

	/**
	 * 获取用户会员信息
	 * @param vipUser
	 * @return
	 */
	@RequestMapping("load")
	public Map<?, ?> load(long user_id) {
		return vipService.load(user_id);
	}
}
