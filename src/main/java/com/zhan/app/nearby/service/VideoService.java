package com.zhan.app.nearby.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.dao.VideoDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ResultUtil;

@Service
public class VideoService {
	@Resource
	private VideoDao videoDao;
	@Resource
	private UserService userService;

	public Map<String, Object> live(String token, String aid, Video video) {
		if (!userService.checkLogin(video.getUid(), token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		Video last = videoDao.getLastHandlerVideo(video.getClient_uuid(), 1); // 获取上次扣费的最后一条记录
		if (last == null) {
			video.setState(1);
			videoDao.insert(video);
			return userService.cost_coin(video.getUid(), token, aid, 1 * 1);
		} else {
			int val = video.getTime_value() - last.getTime_value();
			if (val > 60) {
				int minute = (int) Math.ceil(val / 60.0)-1; //向上 取整了，所以要减一
				video.setState(1);
				videoDao.insert(video);
				return userService.cost_coin(video.getUid(), token, aid, minute * 1);
			} else {
				video.setState(0);
				videoDao.insert(video);
				int all_coins=userService.loadUserCoins(aid, video.getUid());
				return ResultUtil.getResultOKMap().addAttribute("cost_coins", "0").addAttribute("all_coins", all_coins);
			}
		}

	}
}
