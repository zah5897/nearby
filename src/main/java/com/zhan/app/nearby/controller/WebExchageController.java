package com.zhan.app.nearby.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.zhan.app.nearby.bean.user.LoginUser;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.SysUserStatus;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.ExchangeService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.HttpService;
import com.zhan.app.nearby.util.MD5Util;
import com.zhan.app.nearby.util.RandomCodeUtil;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SMSHelper;
import com.zhan.app.nearby.util.TextUtils;

@Controller
@RequestMapping("/exchange")
public class WebExchageController {

	@Autowired
	private UserService userService;
	@Autowired
	private ExchangeService exchangeService;
	@Autowired
	private UserCacheService userCacheService;
	// 1178548652 漂流瓶
	String aid = "1178548652";

	@RequestMapping(value = "/")
	public ModelAndView index(HttpServletRequest request, Long user_id, String token) {
		if (user_id == null || TextUtils.isEmpty(token)) {
			return new ModelAndView("exchange_views/login");
		}
		if (userService.checkLogin(user_id, token)) {
			return new ModelAndView(new RedirectView("index")).addObject("user_id",user_id).addObject("token", token);
		} else {
			return new ModelAndView("exchange_views/login");
		}
	}

	@RequestMapping(value = "/login")
	public ModelAndView login() {
		return new ModelAndView("exchange_views/login");
	}

	@RequestMapping(value = "/success")
	public ModelAndView toSuccess(int coins) {
		return new ModelAndView("exchange_views/success").addObject("coins", coins);
	}

	@RequestMapping(value = "/error")
	public ModelAndView toError(int code,String msg) {
		return new ModelAndView("exchange_views/error").addObject("code", code).addObject("msg", msg);
	}

	@RequestMapping(value = "/index")
	public ModelAndView index(long user_id,String token) {
		Map<String, Object> coinsResult = HttpService.queryUserCoins(user_id, aid);
		if(coinsResult.containsKey("all_coins")) {
			return new ModelAndView("exchange_views/index").addObject("user_id",user_id).addObject("token",token).addObject("coins", coinsResult.get("all_coins"));
		}else {
			return new ModelAndView("exchange_views/error").addAllObjects(ResultUtil.getResultMap(ERROR.ERR_SYS));
		}
	}

	
	@RequestMapping(value = "/bind_zfb")
	public ModelAndView bind_zfb(long user_id,String token) {
		return new ModelAndView("exchange_views/bing_zhifubao").addObject("user_id", user_id).addObject("token", token);
	}
	
	
	@RequestMapping(value = "/sned_bind_zhifubao_sms")
	public @ResponseBody ModelMap send_sms(long user_id,String token) {
		if(!userService.checkLogin(user_id, token)) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		String mobile=userService.getUserMobileById(user_id);
		if(TextUtils.isEmpty(mobile)) {
			return ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST);
		}
		
		if (userCacheService.getUserCodeCacheCount(mobile) >= 3) {
			return ResultUtil.getResultMap(ERROR.ERR_SMS_CODE_LIMIT);
		}
		
		
		String code = RandomCodeUtil.randomCode(6);
		boolean smsOK = SMSHelper.smsBindZHiFuBao(mobile, code);
		smsOK=true;
		if (smsOK) {
			userCacheService.cacheBindZhiFuBaoCode(mobile, code);
		} else {
			return  ResultUtil.getResultMap(ERROR.ERR_FAILED, "验证码发送过于频繁");
		}
		return ResultUtil.getResultOKMap().addAttribute("mobile", mobile);
	}
	
	@RequestMapping(value = "/do_bind_zhifubao")
	public ModelAndView bindZhiFuBao(long user_id,String token,String mobile,String code,String zhifubao) {
		
		
		if(TextUtils.isEmpty(zhifubao)) {
			return new ModelAndView(new RedirectView("error"),ResultUtil.getResultMap( ERROR.ERR_PARAM,"支付宝帐号不能为空"));	
		}
		
		if(TextUtils.isEmpty(code)) {
			return new ModelAndView(new RedirectView("error"),ResultUtil.getResultMap( ERROR.ERR_PARAM,"验证码为空"));	
		}
		
		if(!userService.checkLogin(user_id, token)) {
			return new ModelAndView(new RedirectView("login"));
		}
		
		if(!userCacheService.validateBindZhiFuBaoCode(mobile, code)) {
			return new ModelAndView(new RedirectView("error"),ResultUtil.getResultMap( ERROR.ERR_FAILED,"验证码错误"));	
		}
		exchangeService.savePersonalInfo(user_id, token, mobile, zhifubao,aid);
		return new ModelAndView(new RedirectView("index")).addObject("user_id", user_id).addObject("token", token);
	}
	
	
	@RequestMapping(value = "/do_login", method = RequestMethod.POST)
	public ModelAndView dologin(HttpServletRequest request, String mobile, String password, RedirectAttributes attr) {
		RedirectView redirectView = new RedirectView("error");
		if(TextUtils.isEmpty(mobile)) {
			return new ModelAndView(redirectView,ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST));
		}
		LoginUser user = userService.findLocationUserByMobile(mobile);
		if (user == null) {
			return new ModelAndView(redirectView,ResultUtil.getResultMap(ERROR.ERR_USER_NOT_EXIST));
		}
		if (userService.getUserState(user.getUser_id()) == SysUserStatus.BLACK.ordinal()) {
			return new ModelAndView(redirectView,ResultUtil.getResultMap(ERROR.ERR_ACCOUNT_BLACKLIST));
		}
		String md5;
		try {
			md5 = MD5Util.getMd5(password);
			if (!md5.equals(user.getPassword())) {
				return new ModelAndView(redirectView,ResultUtil.getResultMap(ERROR.ERR_PASSWORD));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new ModelAndView(redirectView,ResultUtil.getResultMap(ERROR.ERR_PASSWORD));
		}
		redirectView = new RedirectView("index");
		return new ModelAndView(redirectView).addObject("user_id", user.getUser_id()).addObject("token", user.getToken());
	}

	@RequestMapping(value = "/do_submit_exchange", method = RequestMethod.POST)
	public ModelAndView loadExchangeCoins(long user_id, String token, int count) {
		if (!userService.checkLogin(user_id, token)) {
			return new ModelAndView(new RedirectView("login"));
		}

		// TODO 检查用户是否绑定支付宝
		if (!exchangeService.isBindZhiFuBao(user_id, aid)) {
			return new ModelAndView(new RedirectView("bind_zfb")).addObject("user_id", user_id).addObject("token", token);
		}
		// 检查用户金币是否足够

		Map<String, Object> coinsResult = HttpService.queryUserCoins(user_id, aid);
		int all_coins = (int) coinsResult.get("all_coins");
		if (count > all_coins) {
			return new ModelAndView(new RedirectView("error"),ResultUtil.getResultMap( ERROR.ERR_COINS_SHORT));
		}
		Map<String, Object> r = exchangeService.exchangeCoin(user_id, count, aid);
		int code = (int) r.get("code");
		if (code == 0) {
			return new ModelAndView(new RedirectView("success")).addObject("coins", count);
		} else {
			return new ModelAndView(new RedirectView("error"),ResultUtil.getResultMap( ERROR.ERR_COINS_SHORT));
		}
	}

	
	
}
