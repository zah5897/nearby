package com.zhan.app.nearby.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zhan.app.nearby.bean.PersonalInfo;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.CityService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ResultUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/main")
@Api(value = "主页接口", description = "主页接口")
public class MainController {
	@Resource
	private MainService mainService;
	@Resource
	private UserDynamicService userDynamicService;
	@Resource
	private CityService cityService;
	@Resource
	private UserCacheService userCacheService;
	@Resource
	private UserService userService;
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
	@ApiOperation(httpMethod = "POST", value = "found") // swagger 当前接口注解
	public ModelMap found(Long user_id, Long last_id, Integer count, String lat, String lng, Integer city_id) {
		return mainService.getHomeFoundSelected(user_id == null ? 0 : user_id, last_id, count, city_id);
	}

	@RequestMapping("reset_city")
	@ApiOperation(httpMethod = "POST", value = "reset_city") // swagger 当前接口注解
	public ModelMap reset_city() {

		long last_time = userCacheService.getLastUploadTime(41);
		long cur_time = System.currentTimeMillis() / 1000;

		if (cur_time - last_time < 60) {
			return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		}
		userCacheService.setLastUploadTime(41);
		log.error("user_id=" + 41 + ",upload img log.");

		ModelMap re = mainService.reset_city();
		return re;
	}

	// 是用于第三方的app里面，用于推广我们app用的

	@RequestMapping("foud_users")
	@ApiOperation(httpMethod = "POST", value = "foud_users 接口名字有误，请使用found_users接口") // swagger 当前接口注解
	public ModelMap foud_users(Long user_id, Integer count, Integer gender) {
		return mainService.found_users(user_id, count, gender);
	}

	@RequestMapping("found_users")
	@ApiOperation(httpMethod = "POST", value = "found_users") // swagger 当前接口注解
	public ModelMap found_users(Long user_id, Integer count, Integer gender) {
		return mainService.found_users(user_id, count, gender);
	}

	@RequestMapping("buy_first_position")
	@ApiOperation(httpMethod = "POST", value = "buy_first_position") // swagger 当前接口注解
	public Map<String, Object> buy_first_position(long user_id, String token, String aid) {
		if (!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		return mainService.buy_first_position(user_id, aid);
	}

	@RequestMapping("new_regist_users")
	@ApiOperation(httpMethod = "POST", value = "new_regist_users") // swagger 当前接口注解
	public ModelMap new_regist_users(Integer page, Integer count) {
		return mainService.newRegistUsers(page, count);
	}

	@RequestMapping("report")
	@ApiOperation(httpMethod = "POST", value = "report") // swagger 当前接口注解
	public ModelMap report(Long user_id, String token, Long dynamic_id) {
		return ResultUtil.getResultOKMap();
	}

	/**
	 * ϲ��ĳ��
	 * 
	 * @param user_id
	 * @param token
	 * @param with_user_id ��ϲ����ĳ��
	 * @return
	 */
	@RequestMapping("like")
	@ApiOperation(httpMethod = "POST", value = "like") // swagger 当前接口注解
	public ModelMap like(long user_id, String token, String with_user_id) {
		return mainService.like(user_id, with_user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "add_block") // swagger 当前接口注解
	@RequestMapping("add_block")
	public ModelMap add_block(long user_id, String token, String with_user_id) {
		return mainService.addBlock(user_id, with_user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "ignore") // swagger 当前接口注解
	@RequestMapping("ignore")
	public ModelMap ignore(long user_id, String token, String with_user_id) {
		return mainService.ignore(user_id, with_user_id);
	}
	@ApiOperation(httpMethod = "POST", value = "排行榜") // swagger 当前接口注解
	@RequestMapping("rank_list")
	public ModelMap meili(int type, Integer page, Integer count) {
		return mainService.meiliList(type, page, count);
	}
	@ApiOperation(httpMethod = "POST", value = "排行榜") // swagger 当前接口注解
	@ApiImplicitParams({
		@ApiImplicitParam(name = "type", value = "-2我关注的人，-1在线用户，0 新人榜，1魅力榜，2土豪榜，3vip榜", paramType = "query", dataType = "Integer"),
		@ApiImplicitParam(name = "time_point", value = "该参数只针对在线排行榜，把最后一条用户的last_login_time传过来即可，格式yyyy-MM-dd HH:mm:ss", paramType = "query", dataType = "Integer")})
	@RequestMapping("rank_list_v2")
	public ModelMap rank_list_v2(long user_id,int type, Integer page, Integer count,String time_point) {
		return mainService.rank_list_v2(user_id,type, page, count,time_point);
	}
	
	
	// 提现历史记录
	@ApiOperation(httpMethod = "POST", value = "exchange_history") // swagger 当前接口注解
	@RequestMapping("exchange_history")
	public ModelMap exchange_history(long user_id, String token, String aid, Integer page, Integer count) {
		return mainService.exchange_history(user_id, aid, page, count);
	}

//	//钻石兑换RMB
//	@RequestMapping("exchange_rmb")
//	public ModelMap exchange_rmb(long user_id,String token,String aid,int diamond,String zhifubao,String mobile,String code) {
//		return mainService.exchange_rmb(user_id,token,aid,diamond,zhifubao,mobile,code);
//	}

	// 获取成长率最高的用户
	@RequestMapping("hot_users")
	@ApiOperation(httpMethod = "POST", value = "hot_users") // swagger 当前接口注解

	public ModelMap hot_users(String gender, Long fix_user_id, Integer page,Integer count) {
		return mainService.getHotUsers(gender, fix_user_id, page,count);
	}

	@ApiOperation( value = "获取顶部购买头条的用户",httpMethod = "GET") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "fix_user_id", value = "固定显示的用户id", required = false, paramType = "query"),
			@ApiImplicitParam(name = "page", value = "页码，从1开始", required = false, paramType = "query"),
			@ApiImplicitParam(name = "count", value = "每页条数", required = false, paramType = "query")  })
	@RequestMapping(value="top_users",method = {RequestMethod.POST,RequestMethod.GET})
	public ModelMap top_users(Long fix_user_id, Integer page, Integer count) {
		return mainService.getTopUsers(fix_user_id, page, count);
	}
	@ApiOperation(value = "随机获取推荐用户",httpMethod = "GET") // swagger 当前接口注解
	@ApiImplicitParams({ @ApiImplicitParam(name = "gender", value = "性别", required = false, paramType = "query"),
		    @ApiImplicitParam(name = "fix_user_id", value = "固定显示的用户id", required = false, paramType = "query"),
			@ApiImplicitParam(name = "count", value = "每次条数", required = false, paramType = "query")  })
	@RequestMapping(value="momo_users",method = {RequestMethod.POST,RequestMethod.GET})
	public ModelMap momo_users(String gender, Long fix_user_id,  Integer count) {
		return mainService.getmomoUsers(gender, fix_user_id, count);
	}

	// 提交个人身份证
	@RequestMapping("check_submit_personal_id")
	@ApiOperation(httpMethod = "POST", value = "check_submit_personal_id") // swagger 当前接口注解
	public ModelMap check_submit_personal_id(PersonalInfo personal) {
		return mainService.check_submit_personal_id(personal);
	}

	// 提交支付宝信息
	@RequestMapping("check_submit_personal_zhifubao")
	@ApiOperation(httpMethod = "POST", value = "check_submit_personal_zhifubao") // swagger 当前接口注解
	public ModelMap check_submit_zhifubao(PersonalInfo personal, String code) {
		return mainService.check_submit_zhifubao(personal, code);
	}

	// 修改个人绑定的信息
	@RequestMapping("modify_bind_personal_info")
	@ApiOperation(httpMethod = "POST", value = "modify_bind_personal_info") // swagger 当前接口注解
	public ModelMap modify_bind_personal_info(PersonalInfo personal, String code) {
		return mainService.modify_bind_personal_info(personal, code);
	}

	// 获取个人绑定的信息
	@RequestMapping("load_personal_info")
	@ApiOperation(httpMethod = "POST", value = "load_personal_info") // swagger 当前接口注解
	public ModelMap load_personal_info(long user_id, String token, String aid) {
		return mainService.load_personal_info(user_id, token, aid);
	}

	// 获取验证码
	@RequestMapping("get_personal_validate_code")
	@ApiOperation(httpMethod = "POST", value = "get_personal_validate_code") // swagger 当前接口注解
	public ModelMap get_personal_validate_code(long user_id, String token, String aid, String mobile,
			Integer code_type) {
		return mainService.get_personal_validate_code(user_id, token, mobile, code_type);
	}

	@RequestMapping("load_special_users")
	@ApiOperation(httpMethod = "POST", value = "load_special_users") // swagger 当前接口注解
	public ModelMap special_users(Integer count) {
		return mainService.getSpecialUsers(1, count == null ? 5 : count);
	}
}
