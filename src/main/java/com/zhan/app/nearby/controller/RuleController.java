package com.zhan.app.nearby.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.service.RuleService;

@RestController
@RequestMapping("rule")
public class RuleController {

	@Resource
	private RuleService ruleService;

	@RequestMapping("list")
	public Map<?, ?> list(String aid) {
		return ruleService.list(aid);
	}

	@RequestMapping("del")
	public Map<?, ?> del(int id) {
		return ruleService.delete(id);
	}

	@RequestMapping("save")
	public Map<?, ?> save(int rule_id,String name, int coins, int coins_free, int rmb, String description,String aid, String app_name) {
		return ruleService.save(rule_id,name, coins, coins_free, rmb, description,aid, app_name);
	}
}
