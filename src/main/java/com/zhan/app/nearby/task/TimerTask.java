package com.zhan.app.nearby.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.service.BottleService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.service.VipService;
import com.zhan.app.nearby.util.HttpUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.SpringContextUtil;

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
				Bottle bottle = new Bottle();
				bottle.setCreate_time(new Date());
				bottle.setUser_id(u.getUser_id());
				bottle.setType(BottleType.MEET.ordinal());
				bottle.setContent(String.valueOf(u.getUser_id()));
				bottleService.send(bottle,null);
			}

		}
		//定时自动加入黑名单IP
		autoAddBlackIP();
	}

	@Scheduled(cron = "0 59 23 * * ?") // 每天23：59分执行
	public void meiliRateTask() {
		MainService mainService = SpringContextUtil.getBean("mainService");
		int rateCount = mainService.injectRate();
		System.out.println("inject rate count:" + rateCount);
		deleteIllegalAvatarFile();
	}

	 

	@Scheduled(cron = "0 10 0 * * ?") // 每天12点10分执行1次
	// @Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
	public void clearExpireVip() {
		VipService vipService = SpringContextUtil.getBean("vipService");
		int clearCount = vipService.clearExpireVip();
		System.out.println("clear Expire vip count:" + clearCount);
		
		
		//每天凌晨清理前天数据
		
		UserService userService = SpringContextUtil.getBean("userService");
		userService.clearExpireMeetBottleUser();
		
		
		BottleService bottleService = SpringContextUtil.getBean("bottleService");
		bottleService.clearExpireBottle();
		
		
	}

	@SuppressWarnings("unchecked")
	@Scheduled(cron = "0 0/10 * * * ?") // 每5分钟执行一次
	public void injectTextBottle() {
		BottleService bottleService = SpringContextUtil.getBean("bottleService");
		String url = "http://app.weimobile.com/yuehui/m_lianai!findLove.action?_ua=a|8.1|shiguangliu|1.1|CocoaPods|df67470b7146390726dd8ee072f47413|320|568|0&aid=1044969149&ver=1.1&ln=en&sid=1001&de=2018-04-15%2016:29:15&mod=iPhone%20Simulator&mno=&mos=iPhone%20Simulator&cd=o57aTreul3QUX+4usDu0mA==&sync=1&companyId=89jq3jrsdfu0as98dfh34ho&deviceId=df67470b7146390726dd8ee072f47413&page=1&";
		String result = HttpUtil.sendGet(url);
		Map<String, Object> map = JSONUtil.jsonToMap(result);

		if (map == null) {
			return;
		}
		Map<String, Object> body = (Map<String, Object>) map.get("body");

		if (body == null) {
			return;
		}
		List<Object> resultList = (List<Object>) body.get("result");
		if (resultList == null) {
			return;
		}

		for (Object obj : resultList) {
			Map<String, Object> data = (Map<String, Object>) obj;
			String id = (String) data.get("id");
			String title = (String) data.get("title");
			bottleService.sendAutoBottle(id, title);
		}
	}
	
	
	@Scheduled(cron = "0 0/20 * * * ?") // 每5分钟执行一次
	public void checkUserOnline() {
		UserService userService = SpringContextUtil.getBean("userService");
		userService.removeTimeoutOnlineUsers(4);
	}
	
	
 
	public void autoAddBlackIP() {
		UserService userService = SpringContextUtil.getBean("userService");
		userService.checkRegistIP(10);
	}
	
	private void deleteIllegalAvatarFile() {
		UserService userService = SpringContextUtil.getBean("userService");
		userService.deleteIllegalAvatarFile();
	}
	
}