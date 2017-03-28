package com.zhan.app.nearby.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.springframework.web.multipart.MultipartFile;

public class ImageSaveUtils {

	// 头像压缩 按此宽度
	public static final int PRESS_AVATAR_WIDTH = 240;
	// 用户上传按此宽度
	public static final int PRESS_IMAGE_WIDTH = 320;

	private static String IMAGE_ROOT_PATH;
	// 用户上传的图片路径
	public static final String FILE_ROOT_IMAGES_ORIGIN = "/images/origin/";
	public static final String FILE_ROOT_IMAGES_THUMB = "/images/thumb/";

	// 用户头像图片路径
	public static final String FILE_ROOT_AVATAR_ORIGIN = "/avatar/origin/";
	public static final String FILE_ROOT_AVATAR_THUMB = "/avatar/thumb/";
	// 用户头像图片路径
	public static final String FILE_ROOT_TOPIC_ORIGIN = "/topic_img/origin/";
	public static final String FILE_ROOT_TOPIC_THUMB = "/topic_img/thumb/";

	private static String getRootPath() {

		if (IMAGE_ROOT_PATH == null) {
			InputStream in = ImageSaveUtils.class.getClassLoader().getResourceAsStream("config.properties");
			Properties props = new Properties();
			try {
				props.load(in);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IMAGE_ROOT_PATH = props.getProperty("IMAGE_SAVE_PATH");
			if (!TextUtils.isEmpty(IMAGE_ROOT_PATH)) {
				File file = new File(IMAGE_ROOT_PATH);
				file.mkdirs();
			}
		}
		return IMAGE_ROOT_PATH;
	}

	public static String getOriginAvatarPath(ServletContext servletContext) {
		return getRootPath() + FILE_ROOT_AVATAR_ORIGIN;
	}

	public static String getThumbAvatarPath(ServletContext servletContext) {
		return getRootPath() + FILE_ROOT_AVATAR_THUMB;
	}

	public static String getOriginImagesPath(ServletContext servletContext) {
		return getRootPath() + FILE_ROOT_IMAGES_ORIGIN;
	}

	public static String getThumbImagesPath(ServletContext servletContext) {
		return getRootPath() + FILE_ROOT_IMAGES_THUMB;
	}
	public static String getTopicOriginImagesPath(ServletContext servletContext) {
		return getRootPath() + FILE_ROOT_TOPIC_ORIGIN;
	}
	
	public static String getTopicThumbImagesPath(ServletContext servletContext) {
		return getRootPath() + FILE_ROOT_TOPIC_THUMB;
	}

	public static String saveAvatar(MultipartFile file, ServletContext servletContext)
			throws IllegalStateException, IOException {
		String filePath = getOriginAvatarPath(servletContext);
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				fileShortName = UUID.randomUUID() + "." + shortName.split("\\.")[1];
			} else {
				fileShortName = UUID.randomUUID().toString();
			}
			File uploadFile = new File(filePath + fileShortName);
			uploadFile.mkdirs();
			file.transferTo(uploadFile);// 保存到一个目标文件中。

			String thumbFile = getThumbAvatarPath(servletContext) + fileShortName;
			pressImageByWidth(uploadFile.getAbsolutePath(), PRESS_AVATAR_WIDTH, thumbFile);
			return fileShortName;
		}
		return null;
	}

	public static String saveUserImages(MultipartFile file, ServletContext servletContext)
			throws IllegalStateException, IOException {
		String filePath = getOriginImagesPath(servletContext);
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				fileShortName = UUID.randomUUID() + "." + shortName.split("\\.")[1];
			} else {
				fileShortName = UUID.randomUUID().toString() + ".jpg";
			}
			File uploadFile = new File(filePath + fileShortName);
			uploadFile.mkdirs();
			file.transferTo(uploadFile);// 保存到一个目标文件中。

			String thumbFile = getThumbImagesPath(servletContext) + fileShortName;
			pressImageByWidth(uploadFile.getAbsolutePath(), PRESS_IMAGE_WIDTH, thumbFile);
			return fileShortName;
		}
		return null;
	}
	public static String saveTopicImages(MultipartFile file, ServletContext servletContext)
			throws IllegalStateException, IOException {
		String filePath = getTopicOriginImagesPath(servletContext);
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				fileShortName = UUID.randomUUID() + "." + shortName.split("\\.")[1];
			} else {
				fileShortName = UUID.randomUUID().toString() + ".jpg";
			}
			File uploadFile = new File(filePath + fileShortName);
			uploadFile.mkdirs();
			file.transferTo(uploadFile);// 保存到一个目标文件中。
			
			String thumbFile = getTopicThumbImagesPath(servletContext) + fileShortName;
			pressImageByWidth(uploadFile.getAbsolutePath(), PRESS_IMAGE_WIDTH, thumbFile);
			return fileShortName;
		}
		return null;
	}

	/**
	 * 删除用户旧头像
	 * 
	 * @param servletContext
	 * @param oldFileName
	 *            旧头像名称
	 */
	public static void removeAcatar(ServletContext servletContext, String oldFileName) {
		// 删除大图

		String filePath = getOriginAvatarPath(servletContext);
		File uploadFile = new File(filePath + oldFileName);
		if (uploadFile.exists()) {
			uploadFile.delete();
		}

		// 删除小图
		String smallPath = getThumbAvatarPath(servletContext);
		File uploadSmallFile = new File(smallPath + oldFileName);
		if (uploadSmallFile.exists()) {
			uploadSmallFile.delete();
		}
	}

	/**
	 * 删除用户上传的图片
	 * 
	 * @param servletContext
	 * @param oldFileName
	 *            要删除的图片名称
	 */
	public static void removeUserImages(ServletContext servletContext, String oldFileName) {
		// 删除大图

		String filePath = getOriginImagesPath(servletContext);
		File uploadFile = new File(filePath + oldFileName);
		if (uploadFile.exists()) {
			uploadFile.delete();
		}

		// 删除小图
		String smallPath = getThumbImagesPath(servletContext);
		File uploadSmallFile = new File(smallPath + oldFileName);
		if (uploadSmallFile.exists()) {
			uploadSmallFile.delete();
		}
	}

	public static void pressImageByWidth(String origin, int minWidth, String thumb) throws IOException {
		ImageCompressUtil.resizeByWidth(origin, minWidth, thumb);
	}
}
