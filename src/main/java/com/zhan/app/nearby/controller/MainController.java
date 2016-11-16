package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.LikeDynamicState;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/main")
public class MainController {
	@Resource
	private MainService mainService;
	@Resource
	private UserDynamicService userDynamicService;

	@Resource
	private DynamicMsgService dynamicMsgService;

	/**
	 * 发现
	 * 
	 * @param user_id
	 * @param lat
	 * @param lng
	 * @param count
	 * @return
	 */
	@RequestMapping("found")
	public ModelMap found(Long user_id, Long last_id, Integer count, String lat, String lng, Integer city_id) {

		if (last_id == null || last_id < 0) {
			last_id = 0l;
		}

		int realCount;
		if (count == null || count <= 0) {
			realCount = 20;
		} else {
			realCount = count;
		}

		if (city_id == null || city_id <0) {
			city_id=0;
		}
		if(user_id==null){
			user_id=0l;
		}
		List<UserDynamic> dynamics = mainService.getHomeFoundSelected(user_id,last_id, realCount,city_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("images", dynamics);

		if (dynamics == null || dynamics.size() < realCount) {
			result.put("hasMore", false);
			result.put("last_id", 0);
		} else {
			result.put("hasMore", true);
			result.put("last_id", dynamics.get(realCount - 1).getId());
		}
		return result;
	}

	@RequestMapping("praise")
	public ModelMap praise(Long user_id, Long image_id) {
		int count = userDynamicService.praiseDynamic(image_id);
		ModelMap result;
		if (count > 0) {
			result = ResultUtil.getResultOKMap();
			result.put("praise_count", count);

			long userId = userDynamicService.getUserIdByDynamicId(image_id);
			if (userId > 0) {
				dynamicMsgService.insertActionMsg(DynamicMsgType.TYPE_PRAISE, user_id, image_id, userId, null);
				userDynamicService.updateLikeState(user_id, image_id, LikeDynamicState.LIKE);
			}
		} else {
			result = ResultUtil.getResultMap(ERROR.ERR_FAILED, "图片找不到");
		}
		return result;
	}
}
