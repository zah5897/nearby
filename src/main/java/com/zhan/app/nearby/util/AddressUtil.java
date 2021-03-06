package com.zhan.app.nearby.util;

import java.util.List;
import java.util.Map;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.dao.CityDao;
import com.zhan.app.nearby.service.UserDynamicService;

public class AddressUtil {
	private static final String BAIDU_AK = "dZo8pGRmo4X3T0lXx5yuf6r9Xs4ktzpo";

	private static final String GAODE_KEY = "ab07209cb3e51a69e2e2adb8911c0d6c";

	public static void setCity(UserDynamic dynamic, String[] address) {
		if (!TextUtils.isEmpty(address[1])) {
			String addr = address[1] + address[2] + address[3];
			dynamic.setAddr(addr);

			dynamic.setCity(address[1]);
			dynamic.setRegion(address[2]);
			dynamic.setStreet(address[3]);

			dynamic.setLat(address[6]);
			dynamic.setLng(address[7]);
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
						if (city.getParent_id() > 0) {
							if (dynamic.getCity_id() != city.getParent_id()) {
								City parent_city = cityDao.getCityById(city.getParent_id());
								dynamic.setCity_id(parent_city.getId());
								dynamic.setProvince_id(parent_city.getId());
								if (parent_city.getParent_id() > 0) {
									dynamic.setProvince_id(parent_city.getParent_id());
								}
							}
						}
						break;
					}
				}
			}

		}
	}

	 
	@SuppressWarnings("unchecked")
	public static String[] getAddressByLatLng(String lat, String lng) {
		String url = "http://api.map.baidu.com/geocoder/v2/?location=" + lat + "," + lng + "&output=json&pois=0&ak="
				+ BAIDU_AK;
		Map<String, Object> obj = HttpClientUtils.get(url);

		if (obj == null) {
			return null;
		}

		int status = Integer.parseInt(obj.get("status").toString());
		if (status == 0) {
			Map<String, Object> resultObj = (Map<String, Object>) obj.get("result");
			Map<String, Object> addressComponent = (Map<String, Object>) resultObj.get("addressComponent");

			String province = addressComponent.get("province").toString();
			String city = addressComponent.get("city").toString();
			String district = addressComponent.get("district").toString();
			String street = addressComponent.get("street").toString();

			String street_number = addressComponent.get("street_number").toString();
			String city_code = resultObj.get("cityCode").toString();

			return new String[] { province, city, district, street, street_number, city_code, lat, lng };
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static String[] getAddressByIp(String ip) {
		String url = "http://api.map.baidu.com/location/ip?coor=bd09ll&ak=" + BAIDU_AK + "&ip=" + ip;
		Map<String, Object> obj = HttpClientUtils.get(url);
		String addrArray[] = new String[8];

		if (obj == null) {
			return addrArray;
		}
		Map<String, Object> contentObj = (Map<String, Object>) obj.get("content");
		if (contentObj != null) {
			Map<String, Object> address_detail = (Map<String, Object>) contentObj.get("address_detail");
			if (address_detail != null) {
				String province = address_detail.get("province").toString();
				String city = address_detail.get("city").toString();
				String district = address_detail.get("district").toString();
				String street = address_detail.get("street").toString();
				String street_number = address_detail.get("street_number").toString();
				String city_code = address_detail.get("city_code").toString();
				addrArray[0] = province;
				addrArray[1] = city;
				addrArray[2] = district;
				addrArray[3] = street;
				addrArray[4] = street_number;
				addrArray[5] = city_code;
			}
			Map<String, Object> point = (Map<String, Object>) contentObj.get("point");
			if (point != null) {
				String x = point.get("x").toString();
				String y = point.get("y").toString();
				addrArray[6] = x;
				addrArray[7] = y;
			}
		}
		return addrArray;
	}

	public static String[] getLatLngByIP(String ip) {
		String[] result = getAddressByIp(ip);
		return new String[] { result[6], result[7] };
	}

	public static String[] getAddressByIp_GAODE(String ip) {
		String url = "http://restapi.amap.com/v3/ip?ip=" + ip + "&key=" + GAODE_KEY;
		String[] location = null;
		Map<String, Object> obj = HttpClientUtils.get(url);
		if (obj == null) {
			return null;
		}
		location = new String[4];
		location[0] = obj.get("province").toString();
		location[1] = obj.get("city").toString();

		String rectangle = obj.get("rectangle").toString();
		String[] latlng = rectangle.split(";");

		String[] lat_lng = latlng[0].split(",");

		location[2] = lat_lng[1];
		location[3] = lat_lng[0];
		return location;
	}
}
