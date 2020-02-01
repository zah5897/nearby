package com.zhan.app.nearby.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.TelVideo;
import com.zhan.app.nearby.dao.TelVideoDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ResultUtil;

@Service
public class TelVideoService {
	@Resource
	private TelVideoDao telVideoDao;
	@Resource
	private UserService userService;

	private final int ONE_MINUTE_COST=10;
	public Map<String, Object> live(String token, String aid, TelVideo video) {
		if (!userService.checkLogin(video.getUid(), token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		TelVideo last = telVideoDao.getLastHandlerVideo(video.getClient_uuid()); // 获取上次扣费的最后一条记录
		if (last == null) {
			video.setState(1);
			telVideoDao.insert(video);
			return userService.cost_coin(video.getUid(), token, aid, 1 * ONE_MINUTE_COST);
		} else {
			int lastMinute=  last.getTime_value()/60;
			int curMinute=  video.getTime_value()/60;
			if (lastMinute ==curMinute) {
				video.setState(0);
				telVideoDao.insert(video);
				int all_coins=userService.loadUserCoins(aid, video.getUid());
				return ResultUtil.getResultOKMap().addAttribute("cost_coins", "0").addAttribute("all_coins", all_coins);
			} else {
				video.setState(1);
				telVideoDao.insert(video);
				return userService.cost_coin(video.getUid(), token, aid, (curMinute -lastMinute)* ONE_MINUTE_COST);
			}
		}
	}
	
//	public static void main(String[] args) {
//		int t1=1;
//		int t30=30;
//		int t59=59;
//		int t60=60;
//		int t61=61;
//		int t70=70;
//		System.out.println(t1/60);
//		System.out.println(t30/60);
//		System.out.println(t59/60);
//		System.out.println(t60/60);
//		System.out.println(119/60);
//		System.out.println(120/60);
//		
//	}
	
}
