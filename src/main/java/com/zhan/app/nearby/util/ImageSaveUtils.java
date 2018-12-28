package com.zhan.app.nearby.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

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

	public static final String FILE_ROOT_GIFT_ORIGIN = "/gift_img/origin/";
	public static final String FILE_ROOT_GIFT_THUMB = "/gift_img/thumb/";

	public static final String FILE_ROOT_BOTTLE_ORIGIN = "/bottle_img/origin/";
	public static final String FILE_ROOT_BOTTLE_THUMB = "/bottle_img/thumb/";

	public static final String FILE_ROOT_FILES = "/files/";
	
	
	
	public static final String FILTER_WORDS_FILE_NAME="filter_words.txt";

	private static String getRootPath() {

		if (IMAGE_ROOT_PATH == null) {
			InputStream in = ImageSaveUtils.class.getClassLoader().getResourceAsStream("config.properties");
			Properties props = new Properties();
			try {
				props.load(in);
			} catch (IOException e) {
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

	public static String getOriginAvatarPath() {
		return getRootPath() + FILE_ROOT_AVATAR_ORIGIN;
	}

	public static String getThumbAvatarPath() {
		return getRootPath() + FILE_ROOT_AVATAR_THUMB;
	}

	public static String getOriginImagesPath() {
		return getRootPath() + FILE_ROOT_IMAGES_ORIGIN;
	}

	public static String getThumbImagesPath() {
		return getRootPath() + FILE_ROOT_IMAGES_THUMB;
	}

	public static String getTopicOriginImagesPath() {
		return getRootPath() + FILE_ROOT_TOPIC_ORIGIN;
	}

	public static String getTopicThumbImagesPath() {
		return getRootPath() + FILE_ROOT_TOPIC_THUMB;
	}

	public static String getGiftOriginImagesPath() {
		return getRootPath() + FILE_ROOT_GIFT_ORIGIN;
	}

	public static String getGiftThumbImagesPath() {
		return getRootPath() + FILE_ROOT_GIFT_THUMB;
	}

	public static String saveAvatar(MultipartFile file) throws IllegalStateException, IOException {
		String filePath = getOriginAvatarPath();
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

			String thumbFile = getThumbAvatarPath() + fileShortName;
			pressImageByWidth(uploadFile.getAbsolutePath(), PRESS_AVATAR_WIDTH, thumbFile);
			return fileShortName;
		}
		return null;
	}

	public static String saveUserImages(MultipartFile file) throws IllegalStateException, IOException {
		String filePath = getOriginImagesPath();
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			boolean isGif = false;
			if (shortName.contains(".")) {
				String endWith = shortName.split("\\.")[1];
				if (endWith.equalsIgnoreCase("gif")) {
					isGif = true;
				}
				fileShortName = UUID.randomUUID() + "." + endWith;
			} else {
				fileShortName = UUID.randomUUID().toString() + ".jpg";
			}
			File uploadFile = new File(filePath + fileShortName);
			uploadFile.mkdirs();
			file.transferTo(uploadFile);// 保存到一个目标文件中。

			String thumbFile = getThumbImagesPath() + fileShortName;
			if (isGif) {
				copyfile(uploadFile, new File(thumbFile), false);
			} else {
				pressImageByWidth(uploadFile.getAbsolutePath(), PRESS_IMAGE_WIDTH, thumbFile);
			}
			return fileShortName;
		}
		return null;
	}

	public static String saveFile(MultipartFile file) throws IllegalStateException, IOException {
		String filePath = getRootPath() + FILE_ROOT_FILES;
		String fileShortName = "filter_words.txt";
		File uploadFile = new File(filePath + fileShortName);
		uploadFile.mkdirs();
		file.transferTo(uploadFile);// 保存到一个目标文件中。
		BottleKeyWordUtil.loadFilterWords();
		return uploadFile.getAbsolutePath();
	}

	public static String getFilterWordsFilePath() {
		String filePath = getRootPath() + FILE_ROOT_FILES + FILTER_WORDS_FILE_NAME;
		if (new File(filePath).exists()) {
			return filePath;
		} else {
			return null;
		}
	}

	public static String saveTopicImages(MultipartFile file) throws IllegalStateException, IOException {
		String filePath = getTopicOriginImagesPath();
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

			String thumbFile = getTopicThumbImagesPath() + fileShortName;
			pressImageByWidth(uploadFile.getAbsolutePath(), PRESS_IMAGE_WIDTH, thumbFile);
			return fileShortName;
		}
		return null;
	}

	public static String saveBottleImages(MultipartFile file) throws IllegalStateException, IOException {
		String filePath = getTopicOriginImagesPath();
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

			String thumbFile = getTopicThumbImagesPath() + fileShortName;
			pressImageByWidth(uploadFile.getAbsolutePath(), PRESS_IMAGE_WIDTH, thumbFile);
			return fileShortName;
		}
		return null;
	}

	public static String saveGiftImages(MultipartFile file) throws IllegalStateException, IOException {
		String filePath = getGiftOriginImagesPath();
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

			String thumbFile = getGiftThumbImagesPath() + fileShortName;
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
	public static void removeAcatar(String oldFileName) {
		// 删除大图

		if (TextUtils.isEmpty(oldFileName)) {
			return;
		}

		if (oldFileName.contains("illegal")) {
			return;
		}
		String filePath = getOriginAvatarPath();
		File uploadFile = new File(filePath + oldFileName);
		if (uploadFile.exists()) {
			uploadFile.delete();
		}

		// 删除小图
		String smallPath = getThumbAvatarPath();
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
	public static void removeUserImages(String oldFileName) {
		// 删除大图

		String filePath = getOriginImagesPath();
		File uploadFile = new File(filePath + oldFileName);
		if (uploadFile.exists()) {
			uploadFile.delete();
		}

		// 删除小图
		String smallPath = getThumbImagesPath();
		File uploadSmallFile = new File(smallPath + oldFileName);
		if (uploadSmallFile.exists()) {
			uploadSmallFile.delete();
		}
	}

	public static void pressImageByWidth(String origin, int minWidth, String thumb) throws IOException {
		ImageCompressUtil.resizeByWidth(origin, minWidth, thumb);
	}

	public static void copyfile(File fromFile, File toFile, Boolean rewrite) {
		if (!fromFile.exists()) {
			return;
		}
		if (!fromFile.isFile()) {
			return;
		}
		if (!fromFile.canRead()) {
			return;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);

			byte[] bt = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			// 关闭输入、输出流
			fosfrom.close();
			fosto.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
