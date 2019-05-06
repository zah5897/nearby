package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.zhan.app.nearby.bean.GameScore;
import com.zhan.app.nearby.dao.GameDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ResultUtil;

@Service
public class GameService {

	public static final int LIMIT_COUNT = 5;

	@Resource
	private GameDao gameDao;

	@Resource
	private UserService userService;

	public void commitGameScore(GameScore score) {
		if (gameDao.isExist(score)) {
			gameDao.updateScore(score);
			return;
		}
		score.setCreate_time(new Date());
		gameDao.insert(score);
	}

	public List<GameScore> rankList(String gid, int page, int count) {
		int start = (page - 1) * count;
		return gameDao.rankList(gid, start, count);
	}

	public List<Map<String, Object>> loadGames() {
		return gameDao.getGames();
	}

	public ModelMap startGame(long user_id, String token, String gid) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		gameDao.clearStep(user_id, gid);
		return ResultUtil.getResultOKMap();
	}

	public Map<String, Object> disCube(long user_id, String token, String aid, String gid) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		int step = gameDao.getGameStep(user_id, gid) + 1;
		int coinCount = step * 6;
		Map<String, Object> result = userService.modifyUserExtra(user_id, aid, coinCount, -1);
		if ((int) result.get("code") == 0) {
			gameDao.updateStep(user_id, gid, step);
			return ResultUtil.getResultOKMap();
		} else {
			return result;
		}
	}

}
