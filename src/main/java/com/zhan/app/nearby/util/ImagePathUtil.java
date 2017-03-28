package com.zhan.app.nearby.util;

import java.util.List;

import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.bean.UserDynamic;

public class ImagePathUtil {

	public static String HOST_PROFIX = "http://app.weimobile.com/nearby";
	// public static String HOST_PROFIX = "http://139.196.111.132:8080/love";

	public static void completeAvatarPath(User user, boolean thumbAndOrigin) {
		String avatar = user.getAvatar();
		if (TextUtils.isEmpty(avatar)) {
			return;
		}
		if (avatar.startsWith("http://")) {
			if (thumbAndOrigin) {
				user.setOrigin_avatar(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_ORIGIN
						+ avatar.substring(avatar.lastIndexOf("/") + 1, avatar.length()));
			}
			return;
		}
		user.setAvatar(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_THUMB + avatar);
		if (thumbAndOrigin) {
			user.setOrigin_avatar(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_ORIGIN + avatar);
		}
	}

	public static void completeImagePath(List<UserDynamic> dynamics, boolean thumbAndOrigin) {

		if (dynamics != null && dynamics.size() > 0) {
			for (UserDynamic dynamic : dynamics) {
				completeImagePath(dynamic, thumbAndOrigin);
			}
		}
	}

	public static void completeImagePath(UserDynamic dynamic, boolean thumbAndOrigin) {

		String shortName = dynamic.getLocal_image_name();
		if (!TextUtils.isEmpty(shortName)) {
			dynamic.setThumb(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_IMAGES_THUMB + shortName);
			if (thumbAndOrigin) {
				dynamic.setOrigin(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_IMAGES_ORIGIN + shortName);
			}
		}
	}

	public static void completeTopicImagePath(List<Topic> topics, boolean thumbAndOrigin) {

		if (topics != null && topics.size() > 0) {
			for (Topic topic : topics) {
				completeTopicImagePath(topic,thumbAndOrigin);
			}
		}

	}
	public static void completeTopicImagePath(Topic topic, boolean thumbAndOrigin) {
		String small = topic.getIcon();
		String big = topic.getBig_icon();
		if (!TextUtils.isEmpty(small)) {
			topic.setIcon(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_THUMB + small);
            if(thumbAndOrigin){
            	topic.setIcon_origin(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_ORIGIN + small);
            }
		}
		if (!TextUtils.isEmpty(big)) {
			topic.setBig_icon(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_THUMB + small);
			if(thumbAndOrigin){
				topic.setBig_icon_origin(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_ORIGIN + small);
			}
		}
		
	}

	// public static void main(String args[]){
	// String url="http://139.196.111.132:8080/love/images/1234.png";
	// String r=url.substring(url.lastIndexOf("/")+1, url.length());
	// System.out.println(r);
	// }
}
