package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.Iterator;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.ImageService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/image")
public class ImageController {
	@Resource
	private UserService userService;

	@Resource
	private ImageService imageService;

	private static Logger log = Logger.getLogger(ImageController.class);

	/**
	 * 发现
	 * 
	 * @param user_id
	 * @param lat
	 * @param lng
	 * @param count
	 * @return
	 */
	@RequestMapping("upload")
	public ModelMap upload(DefaultMultipartHttpServletRequest multipartRequest, Long user_id, Image image) {

		if (user_id == null && user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM.setNewText("用户id异常"));
		}
		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imagePath = ImageSaveUtils.saveUserImages(file, multipartRequest.getServletContext());
						image.setUser_id(user_id);
						image.setName(imagePath);
						image.setCreate_time(new Date());
						long id = imageService.insertImage(image);

						if (id > 0) {
							imageService.addSelectedImage(id);
						}

						return ResultUtil.getResultOKMap();
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
						break;
					}
				}
			}

		}
		return ResultUtil.getResultMap(ERROR.ERR_SYS.setNewText("无图片上传"));
	}

}
