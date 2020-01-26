package com.zhan.app.nearby.task;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.service.BottleService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.service.VipService;
import com.zhan.app.nearby.util.HttpClientUtils;
import com.zhan.app.nearby.util.SpringContextUtil;
import com.zhan.app.nearby.util.TextUtils;

@Component
@EnableScheduling
public class TimerTask {

	// @Scheduled(cron = "0 0 0/1 * * ?") // 每小時
	@Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
	public void injectMeetBottle() {
		UserDao userDao = SpringContextUtil.getBean("userDao");
		List<BaseUser> users = userDao.getRandomMeetBottleUser(10);
		BottleService bottleService = SpringContextUtil.getBean("bottleService");
		for (BaseUser u : users) {
			if (bottleService.checkExistMeetBottleAndReUse(u.getUser_id())) {// 说明不存在
				bottleService.sendMeetBottle(u.getUser_id());
			}
		}
		// 定时自动加入黑名单IP
		autoAddBlackIP();
	}

	@Scheduled(cron = "0 59 23 * * ?") // 每天23：59分执行
	public void meiliRateTask() {
		UserCacheService userCacheService = SpringContextUtil.getBean("userCacheService");
		userCacheService.clearCacheCount();
	}

	@Scheduled(cron = "0 10 0 * * ?") // 每天0：10分执行
	public void clearExpireVip() {
		VipService vipService = SpringContextUtil.getBean("vipService");
		vipService.clearExpireVip();
		// 每天凌晨清理前天数据
		UserService userService = SpringContextUtil.getBean("userService");
		userService.clearExpireMeetBottleUser();

		UserDynamicService userDynamicService = SpringContextUtil.getBean("userDynamicService");
		userDynamicService.clearIllegalDynamic();
	}

	@Scheduled(cron = "0 0 0/2 * * ?") // 每2小时执行一次
	public void injectTextBottle() {
		BottleService bottleService = SpringContextUtil.getBean("bottleService");
		bottleService.refreshPool();
		injectTxtBottle(bottleService);
	}

	private void injectTxtBottle(BottleService bottleService) {
		String url_1 = "http://api.qqsuu.net/api/index";
		String url_2 = "https://v1.hitokoto.cn/";
		int r = new Random().nextInt(2);

		String txt = null;
		if (r == 0) {
			txt = HttpClientUtils.getStringResult(url_1);
		} else {
			Map<String, Object> map = HttpClientUtils.get(url_2);
			if (map != null && map.containsKey("hitokoto")) {
				Object obj = map.get("hitokoto");
				if (obj != null) {
					txt = obj.toString();
				}
			}
		}
		if (!TextUtils.isEmpty(txt)) {
			bottleService.sendAutoBottle(txt, txt);
		}
	}

	@Scheduled(cron = "0 0/20 * * * ?") // 每20分钟执行一次
	public void checkUserOnline() {
		UserService userService = SpringContextUtil.getBean("userService");
		userService.removeTimeoutOnlineUsers(4);
	}
	
	@Scheduled(cron = "0 0/10 * * * ?") // 每10分钟执行一次
	public void timerMatchActiveUser() {
		UserService userService = SpringContextUtil.getBean("userService");
		userService.matchActiveUsers();
	}
	

	public void autoAddBlackIP() {
		UserService userService = SpringContextUtil.getBean("userService");
		userService.checkRegistIP(10);
	}

}