package com.zhan.app.nearby.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.dao.CityDao;
import com.zhan.app.nearby.service.UserDynamicService;

public class AddressUtil {
	private static final String AK = "dZo8pGRmo4X3T0lXx5yuf6r9Xs4ktzpo";

	private static final String GAODE_KEY = "a2af1297f36e2aa7fecea63ef5dfa4d7";

	public static void praseAddress(final String ip, final UserDynamic dynamic, final String ios_addr) {

		dynamic.setIp(ip);
		new Thread() {
			@Override
			public void run() {
				String[] address = null;
				if (!TextUtils.isEmpty(ios_addr)) {
					try {
						JSONObject obj = JSON.parseObject(ios_addr);
						address = new String[6];
						String city = obj.getString("City");
						String SubLocality = obj.getString("SubLocality");
						String Street = obj.getString("Street");
						address[0] = city + SubLocality + Street;
						address[1] = Street;
						address[2] = city;
						address[3] = SubLocality;
						address[4] = dynamic.getLat();
						address[5] = dynamic.getLng();
					} catch (Exception e) {
						address = null;
					}

				}
				if (address == null && !TextUtils.isEmpty(dynamic.getLat())) {
					address = getAddressByLatLng(dynamic.getLat(), dynamic.getLng());
				}
				if (address == null || TextUtils.isEmpty(address[2])) {
					address = getAddressByIp(ip);
				}
				if (address == null || TextUtils.isEmpty(address[2])) {
					String[] city = ipLocation(ip);
					if (city != null && !TextUtils.isEmpty(city[1])) {
						if (address == null) {
							address = new String[6];
						}
						address[2] = city[1];
					}

				}

				if (!TextUtils.isEmpty(address[2])) {
					dynamic.setAddr(address[0]);
					dynamic.setStreet(address[1]);
					dynamic.setCity(address[2]);
					dynamic.setRegion(address[3]);
					dynamic.setLat(address[4]);
					dynamic.setLng(address[5]);
					CityDao cityDao = ((CityDao) SpringContextUtil.getBean("cityDao"));
					List<City> provincesAll = cityDao.list();
					if (provincesAll != null) {
						for (City city : provincesAll) {
							if (dynamic.getCity().contains(city.getName())) {
								dynamic.setCity_id(city.getId());
								dynamic.setProvince_id(city.getId());
								if (city.getParent_id() > 0) {
									dynamic.setProvince_id(city.getParent_id());
								}
								break;
							}
						}
						for (City city : provincesAll) {
							if (city.getType() == 1 && dynamic.getRegion().contains(city.getName())) {
								dynamic.setDistrict_id(city.getId());
								if(city.getParent_id()>0){
									if(dynamic.getCity_id()!=city.getParent_id()){
										City parent_city=cityDao.getCityById(city.getParent_id());
										dynamic.setCity_id(parent_city.getId());
										dynamic.setProvince_id(parent_city.getId());
										if(parent_city.getParent_id()>0){
											dynamic.setProvince_id(parent_city.getParent_id());
										}
									}
								}
								break;
							}
						}
					}

				}
				UserDynamicService userDynamicService = ((UserDynamicService) SpringContextUtil
						.getBean("userDynamicService"));
				userDynamicService.updateAddress(dynamic);

			}
		}.start();
	}

	public static String[] getLatLngByIP(String ip) {
		return getLatLng(ip);
	}

	public static String[] getAddressByLatLng(String lat, String lng) {
		String url = "http://api.map.baidu.com/geocoder/v2/?ak=" + AK + "&location=" + lat + "," + lng + "&output=json";
		String result = HttpUtil.sendGet(url, null);
		if (!TextUtils.isEmpty(result)) {
			JSONObject obj = JSON.parseObject(result);
			int status = obj.getIntValue("status");
			if (status == 0) {
				JSONObject resultObj = obj.getJSONObject("result");

				JSONObject addressComponent = resultObj.getJSONObject("addressComponent");
				String address = resultObj.getString("formatted_address");
				String street = null;
				if (addressComponent != null) {
					street = addressComponent.getString("street") + addressComponent.getString("street_number");
				}
				String district = addressComponent.getString("district");
				String city = addressComponent.getString("city");
				// String province = addressComponent.getString("province");
				return new String[] { address, street, city, district, lat, lng };
			}
		}
		return null;
	}

	private static String[] getLatLng(String ip) {
		String[] latLng = new String[2];
		// 高精度定位
		String url = "http://api.map.baidu.com/highacciploc/v1?qcip=" + ip + "&qterm=pc&ak=" + AK + "&coord=bd09ll";
		String result = HttpUtil.sendGet(url, null);
		if (!TextUtils.isEmpty(result)) {
			JSONObject obj = JSON.parseObject(result);
			JSONObject contentObj = obj.getJSONObject("content");
			if (contentObj != null) {
				JSONObject location = contentObj.getJSONObject("location");
				if (location != null) {
					latLng[0] = location.getString("lat");
					latLng[1] = location.getString("lng");
					return latLng;
				}
			}
		}

		// 低精度ip定位
		url = "http://api.map.baidu.com/location/ip?ak=" + AK + "&coor=bd09ll&ip=" + ip;
		result = HttpUtil.sendGet(url, null);
		if (!TextUtils.isEmpty(result)) {
			JSONObject obj = JSON.parseObject(result);
			JSONObject contentObj = obj.getJSONObject("content");
			if (contentObj != null) {
				JSONObject xy = contentObj.getJSONObject("point");
				if (xy != null) {
					latLng[0] = xy.getString("x");
					latLng[1] = xy.getString("y");
					return latLng;
				}
			}
		}
		return null;
	}

	public static String[] getAddressByIp(String ip) {
		String[] lat_lng = getLatLng(ip);
		if (lat_lng != null && !TextUtils.isEmpty(lat_lng[0]) && !TextUtils.isEmpty(lat_lng[1])) {
			return getAddressByLatLng(lat_lng[0], lat_lng[1]);
		}
		// 高精度定位
		return null;
	}

	public static String[] ipLocation(String ip) {
		String url = "http://restapi.amap.com/v3/ip?ip=" + ip + "&key=" + GAODE_KEY;
		String result = HttpUtil.sendGet(url, null);
		String[] location = null;
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject obj = JSON.parseObject(result);
				location = new String[2];
				location[0] = obj.getString("province");
				location[1] = obj.getString("city");
				return location;
			} catch (Exception e) {

			}
		}

		return location;
	}

	public static void main(String[] args) {
		getAddressByIp("117.143.221.190");
	}
}
