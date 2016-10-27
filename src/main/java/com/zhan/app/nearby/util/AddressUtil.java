package com.zhan.app.nearby.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.service.UserDynamicService;

public class AddressUtil {
   private static final String AK="dZo8pGRmo4X3T0lXx5yuf6r9Xs4ktzpo";
   public static void praseAddress(String ip,UserDynamic dynamic){
	   
	   new Thread(){
		   @Override
		public void run() {
			   String address=getAddressByLatLng(dynamic.getLat(),dynamic.getLng());
			   if(TextUtils.isEmpty(address)){
				   address=getAddressByIp(ip);
			   }
			   if(!TextUtils.isEmpty(address)){
				   UserDynamicService  userDynamicService= ((UserDynamicService)SpringContextUtil.getBean("userDynamicService"));
				   dynamic.setAddr(address);
				   userDynamicService.updateAddress(dynamic);
			   }
		}
	   }.start();
   }
   
   public static String getAddressByLatLng(String lat,String lng){
	   String url="http://api.map.baidu.com/geocoder/v2/?ak="+AK+"&location="+lat+","+lng+"&output=json";
	   String result= HttpUtil.sendGet(url, null);
	   if(!TextUtils.isEmpty(result)){
	      JSONObject obj=JSON.parseObject(result);
	      int status=obj.getIntValue("status");
	      if(status==0){
	    	  JSONObject resultObj=obj.getJSONObject("result");
	    	  String address=resultObj.getString("formatted_address");
	    	  return address;
	      }
	   }
	   return null;
   }
   
   public static String getAddressByIp(String ip){
	   String lat = null;
	   String lng = null;
	   //高精度定位
	   String url="http://api.map.baidu.com/highacciploc/v1?qcip="+ip+"&qterm=pc&ak="+AK+"&coord=bd09ll";
	   String result= HttpUtil.sendGet(url, null);
	   if(!TextUtils.isEmpty(result)){
		   JSONObject obj=JSON.parseObject(result);
		   JSONObject contentObj=obj.getJSONObject("content");
		   if(contentObj!=null){
			   JSONObject location=contentObj.getJSONObject("location");
			   if(location!=null){
				   lat=location.getString("lat");
				   lng=location.getString("lng");
			   }
		   }
	   }
	   if(!TextUtils.isEmpty(lat)&&!TextUtils.isEmpty(lng)){
		   return getAddressByLatLng(lat,lng);
	   }
	   
	   //低精度ip定位
	   url="http://api.map.baidu.com/location/ip?ak="+AK+"&coor=bd09ll&ip="+ip;
	   result= HttpUtil.sendGet(url, null);
	   if(!TextUtils.isEmpty(result)){
		      JSONObject obj=JSON.parseObject(result);
		      JSONObject contentObj=obj.getJSONObject("content");
		      if(contentObj!=null){
		    	  JSONObject xy=contentObj.getJSONObject("point");
		    	  if(xy!=null){
		    		  lng=xy.getString("x");
		    		  lat=xy.getString("y");
		    		  if(!TextUtils.isEmpty(lat)&&!TextUtils.isEmpty(lng)){
		    			   return getAddressByLatLng(lat,lng);
		    		   }
		    	  }
		      }
		   }
	   return null;
   }
}
