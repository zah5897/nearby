package com.zhan.app.nearby.util.baidu;

import java.util.HashMap;

import org.json.JSONObject;

import com.baidu.aip.face.AipFace;

public enum FaceCheckHelper {
	instance;
	// 设置APPID/AK/SK
	public static final String APP_ID = "18370320";
	public static final String API_KEY = "kcGfb64hdlrGgUBQP0W6CjU1";
	public static final String SECRET_KEY = "xn8wsDgo2Mh5pKoyyT6agYZV2HoWBpxg";

	
	private AipFace client;

	private void init() {
		if (client == null) {
			client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
			client.setConnectionTimeoutInMillis(10000);
			client.setSocketTimeoutInMillis(600000);
		}
	}

	public int checkFace(String url) {
		init();
		HashMap<String, String> options = new HashMap<String, String>();
//		options.put("face_field", "age");
//		options.put("max_face_num", "2");
//		options.put("face_type", "LIVE");
//		options.put("liveness_control", "LOW");

//		String image = "取决于image_type参数，传入BASE64字符串或URL字符串或FACE_TOKEN字符串";
		String imageType = "URL";

		// 人脸检测
		JSONObject res = client.detect(url, imageType, options);
		JSONObject result=res.optJSONObject("result");
		
		if(result==null) {
			return 0;
		}
		 
		return result.optInt("face_num");
	}
	
	public static void main(String[] args) {
		String url="https://upfile-drcn.platform.hicloud.com/FileServer/image/b.0070086000208130906.20181112160522.49093172523974616176334524613917.1000.D42DEF1EB9AE61196FF0CA47E2C19959B972736633965924702A50372135435A.jpg";
		FaceCheckHelper.instance.checkFace(url);
	}
}