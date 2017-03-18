package com.zhan.app.nearby.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.UserDynamic;
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

		if (city_id == null || city_id < 0) {
			city_id = 0;
		}
		if(user_id==null){
			user_id=0l;
		}
		
		
		int imageCount=mainService.getCityImageCount(user_id, city_id);
		List<UserDynamic> dynamics = mainService.getHomeFoundSelected(user_id, last_id, realCount, city_id); // 不需要like_state

		ModelMap result = ResultUtil.getResultOKMap();

		if (last_id <= 0) {
			if (imageCount<1) {
				int recommend_city_id = mainService.getMostByCity();
				dynamics = mainService.getHomeFoundSelected(user_id, last_id, realCount, recommend_city_id); // 不需要like_state
				result.put("recommend_city_id", recommend_city_id);
			}else{
				dynamics = mainService.getHomeFoundSelected(user_id, last_id, realCount, city_id); // 不需要like_state
			}
		}else{
			dynamics = mainService.getHomeFoundSelected(user_id, last_id, realCount, city_id); // 不需要like_state
		}

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

	@RequestMapping("report")
	public ModelMap report(Long user_id, String token, Long dynamic_id) {
		return ResultUtil.getResultOKMap();
	}
}
