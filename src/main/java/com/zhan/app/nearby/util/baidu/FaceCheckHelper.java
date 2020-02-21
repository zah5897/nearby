package com.zhan.app.nearby.util.baidu;

import java.util.HashMap;

import org.json.JSONObject;

import com.baidu.aip.face.AipFace;

public enum FaceCheckHelper {
	instance;
	// ����APPID/AK/SK
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

//		String image = "ȡ����image_type����������BASE64�ַ�����URL�ַ�����FACE_TOKEN�ַ���";
		String imageType = "URL";

		// �������
		JSONObject res = client.detect(url, imageType, options);
		JSONObject result=res.optJSONObject("result");
		
		if(result==null) {
			return 0;
		}
		 
		return result.optInt("face_num");
	}
	
	public static void main(String[] args) {
		String url="http://nearby-avatar.cn-bj.ufileos.com/nearby/avatar/origin/6416FCA5-7C85-4045-BBE0-2E10429B3720.jpg";
		FaceCheckHelper.instance.checkFace(url);
	}
}