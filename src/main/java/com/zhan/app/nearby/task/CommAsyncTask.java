package com.zhan.app.nearby.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.UserType;
import com.zhan.app.nearby.dao.CityDao;
import com.zhan.app.nearby.service.DynamicMsgService;
import com.zhan.app.nearby.service.MainService;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.HX_SessionUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.SpringContextUtil;
import com.zhan.app.nearby.util.TextUtils;

@Component
public class CommAsyncTask {

	@Autowired
	private UserService userService;

	@Async
	public void getDynamicLocation(final String ip, final UserDynamic dynamic, final String ios_addr) {
		dynamic.setIp(ip);
		String[] address = null;
		if (!TextUtils.isEmpty(ios_addr)) {
			try {
				Map<String, Object> obj = JSONUtil.jsonToMap(ios_addr);
				address = new String[8];
				String city = obj.get("City").toString();
				String SubLocality = obj.get("SubLocality").toString();
				String Street = obj.get("Street").toString();
				address[1] = city;
				address[2] = SubLocality;
				address[3] = Street;

				address[6] = dynamic.getLat();
				address[7] = dynamic.getLng();
			} catch (Exception e) {
				address = null;
			}

		}
		if (address == null && !TextUtils.isEmpty(dynamic.getLat())) {
			address = AddressUtil.getAddressByLatLng(dynamic.getLat(), dynamic.getLng());
		}
		if (address == null || TextUtils.isEmpty(address[1])) {
			address = AddressUtil.getAddressByIp(ip);
		}
		if (address == null || TextUtils.isEmpty(address[1])) {
			String[] city = AddressUtil.getAddressByIp_GAODE(ip);
			if (city != null && !TextUtils.isEmpty(city[1])) {
				if (address == null) {
					address = new String[8];
				}
				address[0] = city[0];
				address[1] = city[1];
				address[6] = city[2];
				address[7] = city[3];
			}
		}
		AddressUtil.setCity(dynamic, address);
		UserDynamicService userDynamicService = ((UserDynamicService) SpringContextUtil.getBean("userDynamicService"));
		userDynamicService.updateAddress(dynamic);
		userService.updateUserLOcation(dynamic.getUser_id(), dynamic.getLat(), dynamic.getLng());
	}

	@Async
	public void getUserLocationByIP(BaseUser user, String ip) {

		String[] city = AddressUtil.getAddressByIp_GAODE(ip);
		String[] address = new String[8];
		if (city != null && !TextUtils.isEmpty(city[1])) {
			address[0] = city[0];
			address[1] = city[1];
			address[6] = city[2];
			address[7] = city[3];
		}
		City userLocation = null;
		if (!TextUtils.isEmpty(address[1])) {
			CityDao cityDao = ((CityDao) SpringContextUtil.getBean("cityDao"));
			userLocation = cityDao.getCityByName(address[1]);
		}

		if (userLocation == null) {
			return;
		}

		userService.updateUserBirthCity(user.getUser_id(), userLocation);
	}

	@Async
	public void clearMsg(DynamicMsgService service, long uid, long last_id) {
		service.clearMsg(uid, last_id);
	}

	@Async
	public void userOnLineNotify(long uid) {
		Date date = userService.getLastSendTime(uid);
		if (date != null) {
			long time = date.getTime() / 1000;
			long now = System.currentTimeMillis() / 1000;
			if (now - time < 120) { // 2分钟内，不能发送频繁
				return;
			}
		}
		BaseUser user = userService.getBaseUserNoToken(uid);
		if(user.getType()==UserType.TEMP_MOBILE.ordinal()||user.getType()==UserType.VISITOR.ordinal()) { //只推送正式用户和第三方登录的用户
			return;
		}
		
		if(user.getIsFace()==0) {
			return;
		}
		
		ImagePathUtil.completeAvatarPath(user, true);
		List<String> uids = userService.getOnlineUidLastetByLimit(900);

		uids.remove(String.valueOf(uid));
		
		String[] toUid = new String[uids.size()];
		uids.toArray(toUid);
		HX_SessionUtil.pushOnLine(user, toUid);
		userService.updateLastSendTime(uid);
		userService.updateNotifyTime(uids);
	}

	@Async // 添加敏感词
	public void addMGC(String words) {
		if (TextUtils.isEmpty(words)) {
			return;
		}
		words.replace("，", ",");
		String[] wordArray = words.split(",");
		MainService mainService = SpringContextUtil.getBean("mainService");
		for (String word : wordArray) {
			mainService.addBlackWord(word.trim());
		}
		
		BottleKeyWordUtil.refreshFilterWords();
		
	}
	@Async // 添加敏感词
	public void delMGC(String words) {
		if (TextUtils.isEmpty(words)) {
			return;
		}
		words.replace("，", ",");
		String[] wordArray = words.split(",");
		MainService mainService = SpringContextUtil.getBean("mainService");
		for (String word : wordArray) {
		   mainService.deleteBlackWord(word.trim());
		}
		BottleKeyWordUtil.refreshFilterWords();
	}
}
