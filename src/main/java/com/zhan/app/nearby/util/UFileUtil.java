package com.zhan.app.nearby.util;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.zhan.app.nearby.controller.ImageController;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.ApiError;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.bean.UfileErrorBean;
import cn.ucloud.ufile.bean.base.BaseResponseBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import cn.ucloud.ufile.http.OnProgressListener;
import cn.ucloud.ufile.http.UfileCallback;
import cn.ucloud.ufile.util.MimeTypeUtil;
public class UFileUtil {
	private static String PUBLIC_KEY;
	private static String PRIVATE_KEY;

	public static final String BUCKET_AVATAR = "nearby-avatar";
	public static final String BUCKET_IMAGES = "nearby-images";
	public static final String BUCKET_TOPIC_IMG = "nearby-topic-img";
	public static final String BUCKET_GIFT_IMG = "nearby-gift-img";
	public static final String BUCKET_BOTTLE_DRAW = "nearby-bottle-draw";

	static ObjectAuthorization OBJECT_AUTHORIZER = null;
	static ObjectConfig config;
	private static Logger log = Logger.getLogger(ImageController.class);

	private static void init() {
		// 对象相关API的授权器
		if (OBJECT_AUTHORIZER == null) {
			Properties prop = PropertiesUtil.load("app.properties");
			PRIVATE_KEY=prop.getProperty("ucloud.ufile.privateKey");
			PUBLIC_KEY=prop.getProperty("ucloud.ufile.publicKey");
			OBJECT_AUTHORIZER = new UfileObjectLocalAuthorization(PUBLIC_KEY, PRIVATE_KEY);
			config = new ObjectConfig("cn-bj", "ufileos.com");
		}
	}

	public static void main(String[] args) {

//		String path = "C:\\Users\\zah\\Desktop\\test.jpg";
//		File f = new File(path);
//		try {
//			putFile(f, "nearby/avatar/origin/test.jpg", "nearby-avatar");
//		} catch (UfileClientException | UfileServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		delFileexecuteAsync("nearby/avatar/thumb/5dfe5beb-e093-4f38-9fe6-eeaf1c6ea2f7.jpg", "nearby-avatar");

	}

	public static void putFile(File file, String nameAs, String toBucket)
			throws UfileClientException, UfileServerException {
		init();
		String mimeType = MimeTypeUtil.getMimeType(file);
		PutObjectResultBean response = UfileClient.object(OBJECT_AUTHORIZER, config).putObject(file, mimeType)
				.nameAs(nameAs).toBucket(toBucket).execute();
		System.out.println(response);
	}

	public static void delFileexecuteAsync(String keyName, String bucketName) {
		init();
		UfileClient.object(OBJECT_AUTHORIZER, config).deleteObject(keyName, bucketName)
				.executeAsync(new UfileCallback<BaseResponseBean>() {
					@Override
					public void onResponse(BaseResponseBean response) {
						System.out.print(response.getMessage());
					}

					@Override
					public void onError(okhttp3.Request request, ApiError error, UfileErrorBean response) {
						log.error(error.getMessage());
						System.out.print(error.getMessage());
					}
				});
	}

	public static void putStream(InputStream stream, String mimeType, String nameAs, String toBucket)
			throws UfileClientException, UfileServerException {
		init();
		UfileClient.object(OBJECT_AUTHORIZER, config).putObject(stream, mimeType).nameAs(nameAs).toBucket(toBucket)
				.setOnProgressListener(new OnProgressListener() {
					@Override
					public void onProgress(long bytesWritten, long contentLength) {
					}
				}).execute();
	}
}