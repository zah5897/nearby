package com.zhan.app.nearby.util.baidu;

import org.json.JSONArray;
import org.json.JSONObject;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import com.zhan.app.nearby.util.TextUtils;

public enum ImgCheckHelper {
	instance;
	// ����APPID/AK/SK
	public static final String APP_ID = "18422246";
	public static final String API_KEY = "oIAnB9WKkuqKalWCprXv3IXN";
	public static final String SECRET_KEY = "efnoGbyFTIfUBkPrHbG6ooNYYqZdGW2T";

	
	private AipContentCensor client;

	private void init() {
		if (client == null) {
			client = new AipContentCensor(APP_ID, API_KEY, SECRET_KEY);
			client.setConnectionTimeoutInMillis(10000);
			client.setSocketTimeoutInMillis(600000);
		}
	}

	public void resetClient(String app_id,String api_key,String sccret_key) {
		client = new AipContentCensor(app_id, api_key, sccret_key);
		client.setConnectionTimeoutInMillis(10000);
		client.setSocketTimeoutInMillis(600000);
	}
	public int checkImg(String url) {
		init();
		// 参数为url
		JSONObject response=client.imageCensorUserDefined(url, EImgType.URL, null);
		if(!TextUtils.isEmpty(response.optString("error_msg"))) {
		   return  -1;
		}
		JSONArray data=response.optJSONArray("data");
		if(data==null||data.length()==0) {
			return 1; 
		}
		for(int i=0,len=data.length();i<len;i++) {
			 JSONObject msg=data.optJSONObject(i);
			 int type=msg.optInt("type");
			 if(type==1||type==3) { //色情，暴力
				 return 0;
			 }
		}
		return 1;
		
		/**
		 * "data": [
        {
            "msg": "存在色情内容",
            "probability": 0.94308,
            "type": 1
        },
        {
            "msg": "存在性感内容",
            "probability": 0.94308,
            "type": 2
        },
        {
            "msg": "存在暴恐内容",
            "probability": 0.94308,
            "type": 3
        },
        {
        "msg": "存在恶心内容",
        "probability": 0.9688154,
        "type": 4
    },
        {
            "msg": "存在政治敏感内容",
            "stars": [
                {
                    "probability": 0.94308,
                    "name": "习近平"
                },
                {
                    "probability": 0.44308,
                    "name": "彭丽媛"
                }
            ],
            "type": 8
        },
        {
            "msg": "存在二维码内容",
            "probability": 0.94308,
            "type": 6
        },
        {
            "msg": "存在水印码内容",
            "probability": 0.94308,
            "type": 5
        },
        {
            "msg": "存在条形码内容",
            "probability": 0.94308,
            "type": 7
        },
        {
            "msg": "包含联系方式",
            "probability": 0.94308,
            "words": "包含联系方式",
            "type": 8
        }
    ]
		 */
	}
	
	public static void main(String[] args) {
		String url="https://upfile-drcn.platform.hicloud.com/FileServer/image/b.0070086000208130906.20181112160522.49093172523974616176334524613917.1000.D42DEF1EB9AE61196FF0CA47E2C19959B972736633965924702A50372135435A.jpg";
		ImgCheckHelper.instance.checkImg(url);
	}
}