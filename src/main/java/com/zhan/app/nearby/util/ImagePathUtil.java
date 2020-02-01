package com.zhan.app.nearby.util;

import java.util.List;

import com.zhan.app.nearby.bean.Avatar;
import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.bean.GiftOwn;
import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.bean.ManagerUser;
import com.zhan.app.nearby.bean.Topic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.bean.user.BaseUser;

public class ImagePathUtil {

	public static String HOST_PROFIX_FILE = "http://app.weimobile.com/";
	public static String HOST_PROFIX_AVATAR = "http://nearby-avatar.cn-bj.ufileos.com/";
	public static String HOST_PROFIX_IMAGES = "http://nearby-images.cn-bj.ufileos.com/";
	public static String HOST_PROFIX_GIFT = "http://nearby-gift-img.cn-bj.ufileos.com/";
	public static String HOST_PROFIX_TOPIC = "http://nearby-topic-img.cn-bj.ufileos.com/";
	public static String HOST_PROFIX_BOTTLE_DRAW = "http://nearby-bottle-draw.cn-bj.ufileos.com/";
	public static String HOST_PROFIX_VIDEO = "http://nearby-video.cn-bj.ufileos.com/";
	public static String HOST_PROFIX_VIDEO_THUMB = "http://nearby-video-thumb.cn-bj.ufileos.com/";

	public static BaseUser completeAvatarPath(BaseUser user, boolean thumbAndOrigin) {
		
		if(user==null) {
			return user;
		}
		
		String avatar = user.getAvatar();
		if (TextUtils.isEmpty(avatar)) {
			return user;
		}
		
		if(avatar.startsWith("http")) {
			user.setOrigin_avatar(avatar);
			return user;
		}
		
		String path = HOST_PROFIX_AVATAR + ImageSaveUtils.FILE_AVATAR + avatar;
		user.setOrigin_avatar(path);
		user.setAvatar(user.getOrigin_avatar());
		return user;
	}

	public static List<? extends BaseUser> completeAvatarsPath(List<? extends BaseUser> users, boolean thumbAndOrigin) {
		if (users == null || users.size() == 0) {
			return users;
		}
		for (BaseUser user : users) {
			completeAvatarPath(user, true);
		}
		return users;
	}

	public static Avatar completeAvatarPath(Avatar avatarModel) {
		
		if(avatarModel==null) {
			return avatarModel;
		}
		
		
		String avatar = avatarModel.getAvatar();
		if (TextUtils.isEmpty(avatar)) {
			return avatarModel;
		}
		
		if(avatar.startsWith("http")) {
			avatarModel.setOrigin_avatar(avatar);
			return avatarModel;
		}
		
		String path = HOST_PROFIX_AVATAR + ImageSaveUtils.FILE_AVATAR + avatar;
		avatarModel.setAvatar(path);
		avatarModel.setOrigin_avatar(path);
		return avatarModel;
	}

	public static List<Avatar> completeAvatarsPath(List<Avatar> avatars) {
		if (avatars == null || avatars.size() == 0) {
			return avatars;
		}
		for (Avatar avatar : avatars) {
			completeAvatarPath(avatar);
		}
		return avatars;
	}

	public static void completeManagerUserAvatarsPath(List<ManagerUser> users, boolean thumbAndOrigin) {
		if (users == null || users.size() == 0) {
			return;
		}
		for (ManagerUser user : users) {
			String[] avatars = completeAvatarPath(user.getAvatar());
			user.setAvatar(avatars[0]);
			user.setOrigin_avatar(avatars[1]);
		}
	}

	public static String[] completeAvatarPath(String avatar) {
		String avatars[] = new String[2];
		if (TextUtils.isEmpty(avatar)) {
			return avatars;
		}
		
		avatars[0] = HOST_PROFIX_AVATAR + ImageSaveUtils.FILE_AVATAR + avatar;
		avatars[1] = avatars[0];
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

		
		if(dynamic==null) {
			return;
		}
		
		String shortName = dynamic.getLocal_image_name();
		if (!TextUtils.isEmpty(shortName)) {
			dynamic.setThumb(HOST_PROFIX_IMAGES + ImageSaveUtils.FILE_IMAGES + shortName);
			dynamic.setOrigin(dynamic.getThumb());
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

		if(image==null) {
            return;			
 		}
		String shortName = image.getLocal_image_name();
		if (!TextUtils.isEmpty(shortName)) {
			image.setThumb(HOST_PROFIX_IMAGES + ImageSaveUtils.FILE_IMAGES + shortName);
			image.setOrigin(image.getThumb());
		}
	}

	public static void completeBottleDrawPath(Bottle b) {

		if(b==null) {
			return;
		}
		
		if (b.getType() != BottleType.DRAW_GUESS.ordinal()) {
			return;
		}
		String shortName = b.getContent();
		if (!TextUtils.isEmpty(shortName)) {
			b.setContent(HOST_PROFIX_BOTTLE_DRAW + ImageSaveUtils.FILE_BOTTLE_DRAW + shortName);
		}
	}
	
	public static void completeVideoPath(Video video) {
		if(video==null) {
			return;
		}
		if(TextUtils.isEmpty(video.getId())) {
			return;
		}
        video.setUrl(HOST_PROFIX_VIDEO+video.getId());
        video.setThumb(HOST_PROFIX_VIDEO_THUMB+video.getId());
	}
	
	
	public static String getFilterWordsPath() {
		return HOST_PROFIX_FILE + ImageSaveUtils.FILE_ROOT_FILES + ImageSaveUtils.FILTER_WORDS_FILE_NAME;
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

	}

	public static void completeTopicImagePath(Topic topic, boolean thumbAndOrigin) {
		
		if(topic==null) {
			return;
		}
		
		String small = topic.getIcon();
		String big = topic.getBig_icon();
		if (!TextUtils.isEmpty(small)) {
			topic.setIcon(HOST_PROFIX_TOPIC + ImageSaveUtils.FILE_TOPIC_ORIGIN + small);
			if (thumbAndOrigin) {
				topic.setIcon_origin(topic.getIcon());
			}
		}
		if (!TextUtils.isEmpty(big)) {
			topic.setBig_icon(topic.getIcon());
			if (thumbAndOrigin) {
				topic.setBig_icon_origin(topic.getIcon());
			}
		}

	}

	public static void completeGiftPath(Gift gift, boolean thumbAndOrigin) {
		
		if(gift==null) {
			return;
		}
		
		String shortName = gift.getImage_url();
		if (!TextUtils.isEmpty(shortName)) {
			gift.setImage_url(HOST_PROFIX_GIFT + ImageSaveUtils.FILE_GIFT_ORIGIN + shortName);
			if (thumbAndOrigin) {
				gift.setOrigin_image_url(gift.getImage_url());
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
}
