package com.zhan.app.nearby.task;

import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.service.BottleService;
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
			if(!bottleService.isExistMeetTypeBottle(u.getUser_id())) {//说明不存在
				
				Bottle bottle=new Bottle();
				bottle.setCreate_time(new Date());
				bottle.setUser_id(u.getUser_id());
				bottle.setType(BottleType.MEET.ordinal());
				bottle.setContent(String.valueOf(u.getUser_id()));
				
				bottleService.insert(bottle);
			}
			
		}
	}
	// @Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
	// public void bottleSpiderTast() {
	// System.out.println("spiderTast");
	// // 从0开始,每次100
	// SpiderManager.getInstance().bottleSpider();
	// }

	// @Scheduled(cron = "0/5 * * * * ? ") // 每5秒执行一次
	// public void test() {
	// System.out.println("timerPull");
	// System.out.println(Thread.currentThread().getName());
	// }
}