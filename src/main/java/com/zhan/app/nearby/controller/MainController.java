package com.zhan.app.nearby.controller;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.PersonalInfo;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.CityService;
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
		return mainService.found_users(user_id, count, gender);
	}
	@RequestMapping("found_users")
	public ModelMap found_users(Long user_id, Integer count, Integer gender) {
		return mainService.found_users(user_id, count, gender);
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
	public ModelMap like(long user_id, String token, String with_user_id) {
		return mainService.like(user_id, with_user_id);
	}

	
	@RequestMapping("add_block")
	public ModelMap add_block(long user_id, String token, String with_user_id) {
		return mainService.addBlock(user_id, with_user_id);
	}
	 
	@RequestMapping("ignore")
	public ModelMap ignore(long user_id, String token, String with_user_id) {
		return mainService.ignore(user_id, with_user_id);
	}
	
	
	@RequestMapping("rank_list")
	public ModelMap meili(int type,Integer page,Integer count) {
		return mainService.meiliList(type,page,count);
	}
	 
	//提现历史记录
	@RequestMapping("exchange_history")
	public ModelMap exchange_history(long user_id,String token,String aid,Integer page,Integer count) {
		return mainService.exchange_history(user_id,aid,page,count);
	}
	
	//钻石兑换RMB
	@RequestMapping("exchange_rmb")
	public ModelMap exchange_rmb(long user_id,String token,String aid,int diamond,String zhifubao,String mobile,String code) {
		return mainService.exchange_rmb(user_id,token,aid,diamond,zhifubao,mobile,code);
	}
	
	
	//获取成长率最高的用户
	@RequestMapping("hot_users")
	public ModelMap hot_users(String gender,Long fix_user_id,Integer page) {
		return mainService.getHotUsers(gender,fix_user_id,page);
	}
	//提交个人身份证
	@RequestMapping("check_submit_personal_id")
	public ModelMap check_submit_personal_id(PersonalInfo personal) {
		return mainService.check_submit_personal_id(personal);
	}
	//提交支付宝信息
	@RequestMapping("check_submit_personal_zhifubao")
	public ModelMap check_submit_zhifubao(PersonalInfo personal,String code) {
		return mainService.check_submit_zhifubao(personal,code);
	}
	//修改个人绑定的信息
	@RequestMapping("modify_bind_personal_info")
	public ModelMap modify_bind_personal_info(PersonalInfo personal,String code) {
		return mainService.modify_bind_personal_info(personal,code);
	}
	//获取个人绑定的信息
	@RequestMapping("load_personal_info")
	public ModelMap load_personal_info(long user_id,String token,String aid) {
		return mainService.load_personal_info(user_id,token,aid);
	}
	
	//获取验证码
	@RequestMapping("get_personal_validate_code")
	public ModelMap get_personal_validate_code(long user_id,String token,String aid,String mobile,Integer code_type) {
		return mainService.get_personal_validate_code(user_id,token,mobile,code_type);
	}
	
	@RequestMapping("load_special_users")
	public ModelMap special_users(Integer count) {
		return mainService.getSpecialUsers(1,count==null?5:count);
	}
 
	@RequestMapping("getContact/{by_user_id}")
	public ModelMap getWeixin(@PathVariable long by_user_id, long user_id,String token,String aid) {
		return mainService.getContact(by_user_id,user_id,token,aid);
	}
	
}
