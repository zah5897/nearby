package com.zhan.app.nearby.controller;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.CityService;
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
	private CityService cityService;
	@Resource
	private UserCacheService userCacheService;
	
	private static Logger log = Logger.getLogger(MainController.class);
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
		ModelMap re = mainService.getHomeFoundSelected(user_id, last_id, count, city_id);
		return re;
	}
	
	
	@RequestMapping("reset_city")
	public ModelMap reset_city() {
		
		long last_time = userCacheService.getLastUploadTime(41);
		long cur_time = System.currentTimeMillis() / 1000;
		
		
		if(cur_time-last_time<60){
			return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		}
		userCacheService.setLastUploadTime(41);
		log.error("user_id="+41+",upload img log.");
		
		
		ModelMap re = mainService.reset_city();
		return re;
	}
	

	@RequestMapping("foud_users")
	public ModelMap foud_users(Long user_id, Integer count, Integer gender) {
		return mainService.foud_users(user_id, count, gender);
	}

	@RequestMapping("report")
	public ModelMap report(Long user_id, String token, Long dynamic_id) {
		return ResultUtil.getResultOKMap();
	}

	/**
	 * ϲ��ĳ��
	 * 
	 * @param user_id
	 * @param token
	 * @param with_user_id
	 *            ��ϲ����ĳ��
	 * @return
	 */
	@RequestMapping("like")
	public ModelMap like(long user_id, String token, String with_user_id,String content) {
		return mainService.changeRelationShip(user_id, token, with_user_id, Relationship.LIKE,content);
	}

	@RequestMapping("add_block")
	public ModelMap add_block(long user_id, String token, String with_user_id) {
		return mainService.changeRelationShip(user_id, token, with_user_id, Relationship.BLACK,null);
	}

	/**
	 * ��� X ����
	 * 
	 * @param user_id
	 * @param token
	 * @param with_user_id
	 *            �����Ե��û�id
	 * @return
	 */
	@RequestMapping("ignore")
	public ModelMap ignore(long user_id, String token, String with_user_id) {
		return mainService.changeRelationShip(user_id, token, with_user_id, Relationship.IGNORE,null);
	}
}
