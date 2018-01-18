package com.zhan.app.nearby.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.util.HttpService;

@Service
public class RuleService {

	public Map<?, ?> save(int id, String name, int coins, int coins_free, int rmb, String description, String aid,
			String app_name) {
		return HttpService.saveRule(id, name, coins, coins_free, rmb, description, aid, app_name);
	}

	public Map<?, ?> list(String aid) {
		return HttpService.listRule(aid);
	}

	public Map<?, ?> delete(int id) {
		return HttpService.deleteRule(id);
	}
}
