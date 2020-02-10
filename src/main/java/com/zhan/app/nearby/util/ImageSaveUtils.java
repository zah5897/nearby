package com.zhan.app.nearby.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import com.zhan.app.nearby.util.ucloud.ufile.UFileUtil;

import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;

public class ImageSaveUtils {

	private static String FILE_ROOT_PATH;

	private static Logger log = Logger.getLogger(ImageSaveUtils.class);
	// 用户头像图片路径
	public static final String FILE_AVATAR = "nearby/avatar/origin/";
	// 用户上传的图片路径
	public static final String FILE_IMAGES = "nearby/images/origin/";

	public static final String FILE_BOTTLE_DRAW = "nearby/bottle/draw/";

	// 用户头像图片路径
	public static final String FILE_TOPIC_ORIGIN = "nearby/topic_img/origin/";

	public static final String FILE_GIFT_ORIGIN = "nearby/gift_img/origin/";


	public static final String FILE_ROOT_FILES = "/files/";

	public static final String FILTER_WORDS_FILE_NAME = "filter_words.txt";

	private static String getRootPath() {
		if (FILE_ROOT_PATH == null) {
			InputStream in = ImageSaveUtils.class.getClassLoader().getResourceAsStream("app.properties");
			Properties props = new Properties();
			try {
				props.load(in);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
			FILE_ROOT_PATH = props.getProperty("LOCAL_IMAGE_SAVE_PATH");
			if (!TextUtils.isEmpty(FILE_ROOT_PATH)) {
				File file = new File(FILE_ROOT_PATH);
				file.mkdirs();
			}
		}
		return FILE_ROOT_PATH;
	}

	public static String saveAvatar(MultipartFile file)
			throws IllegalStateException, IOException, UfileClientException, UfileServerException {
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				fileShortName = UUID.randomUUID() + "." + shortName.split("\\.")[1];
			} else {
				fileShortName = UUID.randomUUID().toString();
			}
//			File uploadFile = new File(filePath + fileShortName);
//			uploadFile.mkdirs();
//			file.transferTo(uploadFile);// 保存到一个目标文件中。
			String mimeType = MimeTypeUtil.getType(fileShortName);
			UFileUtil.putStream(file.getInputStream(), mimeType, FILE_AVATAR + fileShortName, UFileUtil.BUCKET_AVATAR);
			return fileShortName;
		}
		return null;
	}

	public static String saveUserImages(MultipartFile file)
			throws IllegalStateException, IOException, UfileClientException, UfileServerException {
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				String endWith = shortName.split("\\.")[1];
				fileShortName = UUID.randomUUID() + "." + endWith;
			} else {
				fileShortName = UUID.randomUUID().toString() + ".jpg";
			}
			String mimeType = MimeTypeUtil.getType(fileShortName);
			UFileUtil.putStream(file.getInputStream(), mimeType, FILE_IMAGES + fileShortName, UFileUtil.BUCKET_IMAGES);
			return fileShortName;
		}
		return null;
	}

	
	public static String saveBottleDraw(MultipartFile file)
			throws IllegalStateException, IOException, UfileClientException, UfileServerException {
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				String endWith = shortName.split("\\.")[1];
				fileShortName = UUID.randomUUID() + "." + endWith;
			} else {
				fileShortName = UUID.randomUUID().toString() + ".jpg";
			}
			String mimeType = MimeTypeUtil.getType(fileShortName);
			UFileUtil.putStream(file.getInputStream(), mimeType, FILE_BOTTLE_DRAW + fileShortName,
					UFileUtil.BUCKET_BOTTLE_DRAW);
			return fileShortName;
		}
		return null;
	}

	public static String saveTopicImages(MultipartFile file)
			throws IllegalStateException, IOException, UfileClientException, UfileServerException {
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				fileShortName = UUID.randomUUID() + "." + shortName.split("\\.")[1];
			} else {
				fileShortName = UUID.randomUUID().toString() + ".jpg";
			}
			String mimeType = MimeTypeUtil.getType(fileShortName);
			UFileUtil.putStream(file.getInputStream(), mimeType, FILE_TOPIC_ORIGIN + fileShortName,
					UFileUtil.BUCKET_TOPIC_IMG);
			return fileShortName;
		}
		return null;
	}

	public static String saveGiftImages(MultipartFile file)
			throws IllegalStateException, IOException, UfileClientException, UfileServerException {
		String shortName = file.getOriginalFilename();
		if (!TextUtils.isEmpty(shortName)) {
			String fileShortName = null;
			if (shortName.contains(".")) {
				fileShortName = UUID.randomUUID() + "." + shortName.split("\\.")[1];
			} else {
				fileShortName = UUID.randomUUID().toString() + ".jpg";
			}
			String mimeType = MimeTypeUtil.getType(fileShortName);
			UFileUtil.putStream(file.getInputStream(), mimeType, FILE_GIFT_ORIGIN + fileShortName,
					UFileUtil.BUCKET_GIFT_IMG);
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

	/**
	 * 删除用户旧头像
	 * 
	 * @param servletContext
	 * @param oldFileName    旧头像名称
	 */
	public static void removeAcatar(String oldFileName) {

		UFileUtil.delFileexecuteAsync(FILE_AVATAR+oldFileName, UFileUtil.BUCKET_AVATAR);

	}

	/**
	 * 删除用户上传的图片
	 * 
	 * @param servletContext
	 * @param oldFileName    要删除的图片名称
	 */
	public static void removeUserImages(String oldFileName) {
		UFileUtil.delFileexecuteAsync(FILE_IMAGES+oldFileName, UFileUtil.BUCKET_IMAGES);
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
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}
