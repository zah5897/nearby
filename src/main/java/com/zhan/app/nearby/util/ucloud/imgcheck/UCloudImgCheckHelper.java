package com.zhan.app.nearby.util.ucloud.imgcheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.zhan.app.nearby.util.JSONUtil;
import com.zhan.app.nearby.util.PropertiesUtil;
import com.zhan.app.nearby.util.ucloud.UCloudUtil;

import cn.ucloud.censor.client.DefaultCensorClient;
import cn.ucloud.censor.model.CreateUAICensorResourceParam;
import cn.ucloud.censor.model.CreateUAICensorResourceResult;
import cn.ucloud.censor.pojo.CensorConfig;
import cn.ucloud.common.pojo.Account;

public enum UCloudImgCheckHelper {
	instance;

	private static final String TOOL_URL = "http://api.uai.ucloud.cn/v1/image/scan";

	private String keys[];

	public static final String resourceId = "uaicensor-fwh2vv45";

	private void init() {
		if (keys != null) {
			return;
		}
		Properties prop = PropertiesUtil.load("app.properties");
		String PRIVATE_KEY = prop.getProperty("ucloud.sms.privateKey");
		String PUBLIC_KEY = prop.getProperty("ucloud.sms.publicKey");
		keys=new String[] {PUBLIC_KEY,PRIVATE_KEY};
	}
	 
	//创建resourceId;
	private String createResource() {
		init();
		String region = "cn-bj2";
		String zone = "cn-bj2-04";
		List<Integer> ids = new ArrayList<>();
		ids.add(0);
		CreateUAICensorResourceParam param = new CreateUAICensorResourceParam(region, zone, ids);
		param.setResourceMemo("demoMemo");
		param.setResourceName("demoName");
		try {
			DefaultCensorClient client = new DefaultCensorClient(new CensorConfig(new Account(keys[1], keys[0])));
			CreateUAICensorResourceResult result = client.createUAICensorResource(param);
			String resourceId = result.getResourceId();
			System.out.println(resourceId);
			return resourceId;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ucloud 鉴黄
	 * 
	 * @param imageUrl
	 * @return 返回值 RetCode 0 标识正常 其余一律异常 Suggestion 建议， pass-放行， forbid-封禁，
	 *         check-人工审核
	 */
	public String check(String imageUrl) {
		init();
		try {
			RestTemplate rest = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			/**
			 * 生成signature，首字母排序
			 */
			String timestamp = System.currentTimeMillis() + "";
			SortedMap<Object, Object> packageParams = new TreeMap<>();
			packageParams.put("PublicKey", keys[0]);
			packageParams.put("ResourceId", resourceId);
			packageParams.put("Timestamp", timestamp);
			packageParams.put("Url", imageUrl);
			String signature = UCloudUtil.createSign(packageParams, keys[1]);
			/**
			 * 参数
			 */
			MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
			param.add("Scenes", "porn");
			param.add("Method", "url");
			param.add("Url", imageUrl);
			/**
			 * headers 参数
			 */
			headers.setContentType(MediaType.parseMediaType("multipart/form-data; charset=UTF-8"));
			headers.set("PublicKey", keys[0]);
			headers.set("Signature", signature);
			headers.set("ResourceId", resourceId);
			headers.set("Timestamp", timestamp);
			HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(param, headers);
			ResponseEntity<String> responseEntity = rest.exchange(TOOL_URL, HttpMethod.POST, httpEntity, String.class);
			String result= responseEntity.getBody();
			
			//{"RetCode":0,"Message":"","Timestamp":1581309687,"Status":"Success","StartTime":1581309195261,"EndTime":1581309687,"Result":{"Porn":{"Suggestion":"pass","Score":0.00198},"Politician":{"Suggestion":"","Score":0},"Terror":{"Suggestion":"","Score":0}}}

			Map<String, Object> rm=JSONUtil.jsonToMap(result);
			int RetCode=(int) rm.get("RetCode");
			Map<String, Object> Result=(Map<String, Object>) rm.get("Result");
			
			Map<String, Object> Porn=(Map<String, Object>) Result.get("Porn"); //涉黄结果 ，Terror涉恐结果，Politician摄政结果
			String Suggestion=Porn.get("Suggestion").toString(); // pass-放行， forbid-封禁， check-人工审核
			return Suggestion;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] args) {
		String url = "https://upfile-drcn.platform.hicloud.com/FileServer/image/b.0070086000208130906.20181112160522.49093172523974616176334524613917.1000.D42DEF1EB9AE61196FF0CA47E2C19959B972736633965924702A50372135435A.jpg";
		String result=UCloudImgCheckHelper.instance.check(url);
		System.out.println(result);
	}
}