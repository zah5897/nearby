package com.zhan.app.nearby.controller;


import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.GameScore;
import com.zhan.app.nearby.service.GameService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/game")
public class GameController {

	@Resource
	private GameService gameService;

	@RequestMapping("commit_game_score")
	public ModelMap commitGameScore(GameScore score,long user_id,String token) {
		score.setUid(user_id);
		gameService.commitGameScore(score);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("rank_list")
	public ModelMap rankList(int gid,Integer page,Integer count) {
		return ResultUtil.getResultOKMap().addAttribute("data", gameService.rankList(gid,page==null?1:page,count==null?20:count));
	} 
}
