package com.zhan.app.nearby.task;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.service.BottleService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.service.VipService;
import com.zhan.app.nearby.util.HttpClientUtils;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.SpringContextUtil;
import com.zhan.app.nearby.util.TextUtils;

@Component
@EnableScheduling
public class TimerTask {
	@Autowired
	private FaceCheckTask faceCheckTask;
	@Autowired
	private MatchActiveUserTask matchActiveUserTask;
	@Autowired
	private HXAsyncTask hxAsyncTask;
	@Autowired
	private UserCacheService userCacheService;
	
	@Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
	public void injectMeetBottle() {
		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 240);
		if (lock) {
			UserDao userDao = SpringContextUtil.getBean("userDao");
			List<BaseUser> users = userDao.getRandomMeetBottleUser(10);
			BottleService bottleService = SpringContextUtil.getBean("bottleService");
			for (BaseUser u : users) {
				if (bottleService.checkExistMeetBottleAndReUse(u.getUser_id())) {// 说明不存在
					bottleService.sendMeetBottle(u.getUser_id());
				}
			}
			
			
			
		}
	}

	@Scheduled(cron = "0 59 23 * * ?") // 每天23：59分执行
	public void meiliRateTask() {

		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 43200);
		if (lock) {
			UserCacheService userCacheService = SpringContextUtil.getBean("userCacheService");
			userCacheService.clearCacheCount();
			// 删除匹配的用户记录
			UserService userService = SpringContextUtil.getBean("userService");
			userService.clearUserMatchData();
			
			MainService mainService = SpringContextUtil.getBean("mainService");
			mainService.notifyOnlineUserCloseChatPage();
		}
	}

	@Scheduled(cron = "0 10 0 * * ?") // 每天0：10分执行
	public void clearExpireVip() {

		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 43200);
		if (lock) {
			VipService vipService = SpringContextUtil.getBean("vipService");
			vipService.clearExpireVip();
			// 每天凌晨清理前天数据
			UserService userService = SpringContextUtil.getBean("userService");
			userService.clearExpireMeetBottleUser();

			UserDynamicService userDynamicService = SpringContextUtil.getBean("userDynamicService");
			userDynamicService.clearIllegalDynamic();

		}
	}

	@Scheduled(cron = "0 0 0/2 * * ?") // 每2小时执行一次
	public void injectTextBottle() {

		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 7000);
		if (lock) {

			BottleService bottleService = SpringContextUtil.getBean("bottleService");
			bottleService.refreshPool();
			injectTxtBottle(bottleService);

		}
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

		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 900);
		if (lock) {

			UserService userService = SpringContextUtil.getBean("userService");
			userService.removeTimeoutOnlineUsers(30);
		}
	}

	@Scheduled(cron = "0 0/5 * * * ?") // 每10分钟执行一次
	public void doCheckImg() { // 自动审核待审核的图片

		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 250);
		if (lock) {
			faceCheckTask.doCheckImg();
		}
	}

	@Scheduled(cron = "0 0/5 * * * ?") // 每10分钟执行一次
	public void doMatchActiveUser() { //

		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 250);
		if (lock) {
			matchActiveUserTask.matchActiveUsers();
		}
	}

	@Scheduled(cron = "0 0/60 * * * ?") // 每10分钟执行一次
	public void downloadIMChatHistoryMessages() { //
		
		String ip = IPUtil.getLocalAddr();
		String method = Thread.currentThread().getStackTrace()[1].getMethodName();
		boolean lock = userCacheService.tryLock(method, ip, 59*60); //锁定59分钟
		if (lock) {
			hxAsyncTask.exportChatMessages();
		}
		
	}
	
	
	public void autoAddBlackIP() {
		UserService userService = SpringContextUtil.getBean("userService");
		userService.checkRegistIP(10);
	}

}