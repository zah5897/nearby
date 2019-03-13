package com.zhan.app.nearby.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.GameScore;
import com.zhan.app.nearby.dao.GameDao;

@Service
public class GameService {

	public static final int LIMIT_COUNT = 5;

	@Resource
	private GameDao gameDao;

	public void commitGameScore(GameScore score) {
		if(gameDao.isExist(score)) {
			gameDao.updateScore(score);
			return;
		}
		score.setCreate_time(new Date());
		gameDao.insert(score);
	}
	public List<GameScore> rankList(int gid, int page, int count) {
		int start=(page-1)*count;
		return gameDao.rankList(gid,start,count);
	}

}
