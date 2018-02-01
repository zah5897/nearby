package com.zhan.app.nearby.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.dao.CityDao;
import com.zhan.app.nearby.service.UserDynamicService;

public class AddressUtil {
	private static final String BAIDU_AK = "dZo8pGRmo4X3T0lXx5yuf6r9Xs4ktzpo";

	private static final String GAODE_KEY = "ab07209cb3e51a69e2e2adb8911c0d6c";

	public static void praseAddress(final String ip, final UserDynamic dynamic, final String ios_addr) {
		dynamic.setIp(ip);
		new Thread() {
			@Override
			public void run() {
				String[] address = null;
				if (!TextUtils.isEmpty(ios_addr)) {
					try {
						JSONObject obj = JSON.parseObject(ios_addr);
						address = new String[8];
						String city = obj.getString("City");
						String SubLocality = obj.getString("SubLocality");
						String Street = obj.getString("Street");
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
					address = getAddressByLatLng(dynamic.getLat(), dynamic.getLng());
				}
				if (address == null || TextUtils.isEmpty(address[1])) {
					address = getAddressByIp(ip);
				}
				if (address == null || TextUtils.isEmpty(address[1])) {
					String[] city = getAddressByIp_GAODE(ip);
					if (city != null && !TextUtils.isEmpty(city[1])) {
						if (address == null) {
							address = new String[8];
						}
						address[0] = city[0];
						address[1] = city[1];
						address[6]=city[2];
						address[7]=city[3];
					}
				}
				setCity(dynamic,address);
				UserDynamicService userDynamicService = ((UserDynamicService) SpringContextUtil
						.getBean("userDynamicService"));
				userDynamicService.updateAddress(dynamic);

			}
		}.start();
	}
	
	private static void setCity(UserDynamic dynamic,String[] address) {
		if (!TextUtils.isEmpty(address[1])) {
			String addr=address[1]+address[2]+address[3];
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
	}
	

	public static String[] getAddressByLatLng(String lat, String lng) {
		String url = "http://api.map.baidu.com/geocoder/v2/?location="+lat+","+lng+"&output=json&pois=0&ak="+BAIDU_AK;
		String result = HttpUtil.sendGet(url, null);
		if (!TextUtils.isEmpty(result)) {
			JSONObject obj = JSON.parseObject(result);
			int status = obj.getIntValue("status");
			if (status == 0) {
				JSONObject resultObj = obj.getJSONObject("result");
				JSONObject addressComponent = resultObj.getJSONObject("addressComponent");
				
				
				
				String province = addressComponent.getString("province");
				String city = addressComponent.getString("city");
				String district = addressComponent.getString("district");
				String street = addressComponent.getString("street");
				
				
				String street_number=addressComponent.getString("street_number");
				String city_code=resultObj.getString("cityCode");
			 
				return new String[] { province, city, district, street, street_number, city_code,lat,lng };
			}
		}
		return null;
	}

	public static String[] getAddressByIp(String ip) {
	    String	url = "http://api.map.baidu.com/location/ip?coor=bd09ll&ak=" + BAIDU_AK + "&ip=" + ip;
		String result = HttpUtil.sendGet(url, null);
		String addrArray[]=new String[8];
		if (!TextUtils.isEmpty(result)) {
			JSONObject obj = JSON.parseObject(result);
			JSONObject contentObj = obj.getJSONObject("content");
			if (contentObj != null) {
				JSONObject address_detail = contentObj.getJSONObject("address_detail");
				if(address_detail!=null) {
					String province=address_detail.getString("province");
					String city=address_detail.getString("city");
					String district=address_detail.getString("district");
					String street=address_detail.getString("street");
					String street_number=address_detail.getString("street_number");
					String city_code=address_detail.getString("city_code");
					addrArray[0]=province;
					addrArray[1]=city;
					addrArray[2]=district;
					addrArray[3]=street;
					addrArray[4]=street_number;
					addrArray[5]=city_code;
				}
				JSONObject point = contentObj.getJSONObject("point");
				if(point!=null) {
					String x=point.getString("x");
					String y=point.getString("y");
					addrArray[6]=x;
					addrArray[7]=y;
				}
			}
		}
		return addrArray;
	}
	
	public static String[] getLatLngByIP(String ip) {
	    String[] result=getAddressByIp(ip);
		return new String[] {result[6],result[7]};
	}

	public static String[] getAddressByIp_GAODE(String ip) {
		String url = "http://restapi.amap.com/v3/ip?ip=" + ip + "&key=" + GAODE_KEY;
		String result = HttpUtil.sendGet(url, null);
		String[] location = null;
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject obj = JSON.parseObject(result);
				location = new String[4];
				location[0] = obj.getString("province");
				location[1] = obj.getString("city");
				
				String rectangle=obj.getString("rectangle");
				String[] latlng=rectangle.split(";");
				
			    String[] lat_lng=latlng[0].split(",");
				
			    location[2]=lat_lng[1];
			    location[3]=lat_lng[0];
				return location;
			} catch (Exception e) {

			}
		}
		return location;
	}

	public static void main(String[] args) {
		getAddressByIp("117.143.221.190");//	  http://117.143.221.190:8899/nearby/bottle/send
	}
}
