package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.BGM;
import com.zhan.app.nearby.bean.Report;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.task.HXAsyncTask;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.baidu.ImgCheckHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/system")
@Api(value = "系统相关")
public class SystemController {
	@Resource
	private UserService userService;
	@Autowired
	private MainService mainService;

	@Autowired
	private HXAsyncTask hxAsyncTask;

	@Deprecated
	@RequestMapping("report")
	public ModelMap report(Report report) {
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("new_report")
	public ModelMap new_report(Report report) {
		mainService.saveReport(report);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("list_report")
	public ModelMap list_report(int page, Integer count, int type) {
		count = (count == null ? 10 : count);
		List<Report> list = mainService.listReport(type, page, count);
		ModelMap r = ResultUtil.getResultOKMap().addAttribute("users", list);
		if (list.size() == count) {
			r.addAttribute("hasMore", true);
		} else {
			r.addAttribute("hasMore", false);
		}
		return r;
	}

	@RequestMapping("prootl")
	public String prootl() {
		return "prootl";
	}

	@RequestMapping("test_keyword")
	public ModelMap test_keyword(String word) {
		return ResultUtil.getResultOKMap().addAttribute("过滤结果", BottleKeyWordUtil.filterContent(word));
	}

	@RequestMapping("add_new_black_ip")
	public ModelMap addNewBlackIP(String ip) {
		IPUtil.addIPBlack(ip);
		return ResultUtil.getResultOKMap().addAttribute("black_ips", IPUtil.getIpBlackList());
	}

	@RequestMapping("remove_black_ip")
	public ModelMap removeBlackIP(String ip) {
		IPUtil.removeBlackIP(ip);
		return ResultUtil.getResultOKMap().addAttribute("black_ips", IPUtil.getIpBlackList());
	}

	@RequestMapping("bgm")
	public ModelMap bgm(Integer count, Integer test) {
		return ResultUtil.getResultOKMap().addAttribute("bgms", mainService.loadBGM(count, test));
	}

	@RequestMapping("bgm_like/{id}")
	public ModelMap bgmLike(@PathVariable int id) {
		return ResultUtil.getResultOKMap().addAttribute("id", id);
	}

	@RequestMapping("bgm_add")
	public ModelMap bgmAdd(BGM bgm) {
		mainService.saveBGM(bgm);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("goods_id_list")
	public ModelMap goods_id_list(int type) {
		return mainService.goods_id_list(type);
	}

	@RequestMapping("resetCheckImgClient")
	public ModelMap resetCheckImgClient(String app_id, String api_key, String sccret_key) {
		ImgCheckHelper.instance.resetClient(app_id, api_key, sccret_key);
		return ResultUtil.getResultOKMap();
	}

	@ApiOperation(httpMethod = "POST", value = "获取窗口隐藏设置")
	@ApiImplicitParams({ @ApiImplicitParam(name = "user_id", value = "user_id", required = true, paramType = "query"),
			@ApiImplicitParam(name = "token", value = "token", required = true, paramType = "query"),
			@ApiImplicitParam(name = "channel", value = "channel", required = true, paramType = "query") })
	@RequestMapping("show_chat_info")
	public ModelMap showChatTabInfo(long user_id, String token, String channel) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return mainService.chatStrategy(channel);
	}

	@RequestMapping("test_redis")
	public ModelMap test_redis() {
		return mainService.test_redis();
	}

	@RequestMapping("test_black_words")
	public String test_black_words(String w) {
		return BottleKeyWordUtil.filterContent(w);
	}

	@RequestMapping("test")
	public ModelMap test() {
		hxAsyncTask.exportChatMessages();
		return ResultUtil.getResultOKMap();
	}

}
