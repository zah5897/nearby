package com.zhan.app.nearby.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.GameScore;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.GameService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/game")
public class GameController {

	@Resource
	private GameService gameService;

	@Resource
	private UserService userService;

	@RequestMapping("list")
	public ModelMap list() {
		return ResultUtil.getResultOKMap().addAttribute("games", gameService.loadGames());
	}

	@RequestMapping("commit_game_score")
	public ModelMap commitGameScore(GameScore score, long user_id, String token) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		score.setUid(user_id);
		gameService.commitGameScore(score);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("rank_list")
	public ModelMap rankList(String gid, Integer page, Integer count,Long user_id) {
		int pi=page == null ? 1 : page;
		int ps=count == null ? 20 : count;
		List<GameScore> list=gameService.rankList(gid,pi,ps );
		ModelMap data= ResultUtil.getResultOKMap().addAttribute("users",list);
		data.put("hasMore", list.size()==ps);
		if(page==null||page==1) {
			data.put("me", gameService.getCurrentPosition(gid,user_id==null?0:user_id));
		}
		return data;
	}
	
	@RequestMapping("start")
	public ModelMap start(long user_id,String token,String gid) {
		gameService.startGame(  user_id,  token,  gid);
		return ResultUtil.getResultOKMap();
	}
	
	@RequestMapping("skip")
	public Map<String, Object> discube(long user_id,String token,String aid,String gid) {
		return gameService.disCube(user_id,token,aid,gid);
	}
}
