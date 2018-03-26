package com.zhan.app.nearby.util;

import java.util.List;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.user.BaseUser;

public class ImagePathUtil {

	public static String HOST_PROFIX = "http://app.weimobile.com/nearby";
//	public static String HOST_PROFIX = "http://127.0.0.1:8899/nearby";

	public static BaseUser completeAvatarPath(BaseUser user, boolean thumbAndOrigin) {
		String avatar = user.getAvatar();
		if (TextUtils.isEmpty(avatar)) {
			return user;
		}
		if (avatar.startsWith("http://")) {
			if (thumbAndOrigin) {
				user.setOrigin_avatar(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_ORIGIN
						+ avatar.substring(avatar.lastIndexOf("/") + 1, avatar.length()));
			}
			return user;
		}
		user.setAvatar(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_THUMB + avatar);
		if (thumbAndOrigin) {
			user.setOrigin_avatar(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_ORIGIN + avatar);
		}
		return user;
	}

	public static void completeAvatarsPath(List<? extends BaseUser> users, boolean thumbAndOrigin) {
		if (users == null || users.size() == 0) {
			return;
		}
		for (BaseUser user : users) {
			completeAvatarPath(user, true);
		}
	}

	public static void completeManagerUserAvatarsPath(List<ManagerUser> users, boolean thumbAndOrigin) {
		if (users == null || users.size() == 0) {
			return;
		}
		for (ManagerUser user : users) {
			String[] avatars=completeAvatarPath(user.getAvatar());
			user.setAvatar(avatars[0]);
			user.setOrigin_avatar(avatars[1]);
		}
	}

	public static String[] completeAvatarPath(String avatar) {
		String avatars[] = new String[2];
		if (TextUtils.isEmpty(avatar)) {
			return avatars;
		}
		avatars[0] = HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_THUMB + avatar;
		avatars[1] = HOST_PROFIX + ImageSaveUtils.FILE_ROOT_AVATAR_ORIGIN + avatar;
		return avatars;
	}

	public static void completeDynamicsPath(List<UserDynamic> dynamics, boolean thumbAndOrigin) {

		if (dynamics != null && dynamics.size() > 0) {
			for (UserDynamic dynamic : dynamics) {
				completeDynamicPath(dynamic, thumbAndOrigin);
			}
		}
	}

	public static void completeDynamicPath(UserDynamic dynamic, boolean thumbAndOrigin) {

		String shortName = dynamic.getLocal_image_name();
		if (!TextUtils.isEmpty(shortName)) {
			dynamic.setThumb(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_IMAGES_THUMB + shortName);
			if (thumbAndOrigin) {
				dynamic.setOrigin(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_IMAGES_ORIGIN + shortName);
			}
		}
	}

	public static void completeImagesPath(List<Image> images, boolean thumbAndOrigin) {
		if (images != null && images.size() > 0) {
			for (Image img : images) {
				completeImagePath(img, thumbAndOrigin);
			}
		}
	}

	public static void completeImagePath(Image image, boolean thumbAndOrigin) {

		String shortName = image.getLocal_image_name();
		if (!TextUtils.isEmpty(shortName)) {
			image.setThumb(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_IMAGES_THUMB + shortName);
			if (thumbAndOrigin) {
				image.setOrigin(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_IMAGES_ORIGIN + shortName);
			}
		}
	}

	public static void completeTopicImagePath(List<Topic> topics, boolean thumbAndOrigin) {

		if (topics != null && topics.size() > 0) {
			for (Topic topic : topics) {
				completeTopicImagePath(topic, thumbAndOrigin);
			}
		}

	}

	public static void completeCommentImagePath(List<DynamicComment> comments, boolean thumbAndOrigin) {

		if (comments != null && comments.size() > 0) {
			for (DynamicComment comment : comments) {
				completeCommentImagePath(comment, thumbAndOrigin);
			}
		}
	}

	public static void completeCommentImagePath(DynamicComment comment, boolean thumbAndOrigin) {
		completeAvatarPath(comment.getUser(), thumbAndOrigin);
		if (comment.getAtComment() != null) {
			completeAvatarPath(comment.getAtComment().getUser(), thumbAndOrigin);
		}

	}

	public static void completeTopicImagePath(Topic topic, boolean thumbAndOrigin) {
		String small = topic.getIcon();
		String big = topic.getBig_icon();
		if (!TextUtils.isEmpty(small)) {
			topic.setIcon(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_THUMB + small);
			if (thumbAndOrigin) {
				topic.setIcon_origin(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_ORIGIN + small);
			}
		}
		if (!TextUtils.isEmpty(big)) {
			topic.setBig_icon(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_THUMB + big);
			if (thumbAndOrigin) {
				topic.setBig_icon_origin(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_TOPIC_ORIGIN + big);
			}
		}

	}

	
	public static void completeGiftPath(Gift gift, boolean thumbAndOrigin) {
		String shortName = gift.getImage_url();
		if (!TextUtils.isEmpty(shortName)) {
			gift.setImage_url(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_GIFT_THUMB + shortName);
			if (thumbAndOrigin) {
				gift.setOrigin_image_url(HOST_PROFIX + ImageSaveUtils.FILE_ROOT_GIFT_ORIGIN + shortName);
			}
		}
	}
	public static void completeGiftsPath(List<Gift> gifts, boolean thumbAndOrigin) {
		if (gifts != null && gifts.size() > 0) {
			for (Gift gift : gifts) {
				completeGiftPath(gift, thumbAndOrigin);
			}
		}
	}
	public static void completeGiftsOwnPath(List<GiftOwn> gifts, boolean thumbAndOrigin) {
		if (gifts != null && gifts.size() > 0) {
			for (Gift gift : gifts) {
				completeGiftPath(gift, thumbAndOrigin);
			}
		}
	}
	// public static void main(String args[]){
	// String url="http://139.196.111.132:8080/love/images/1234.png";
	// String r=url.substring(url.lastIndexOf("/")+1, url.length());
	// System.out.println(r);
	// }
}
