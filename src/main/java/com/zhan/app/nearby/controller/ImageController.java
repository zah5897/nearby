package com.zhan.app.nearby.controller;

import java.util.Date;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/image")
public class ImageController {
	@Resource
	private UserService userService;

	@Resource
	private UserDynamicService userDynamicService;

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
	public ModelMap upload(DefaultMultipartHttpServletRequest multipartRequest, Long user_id, UserDynamic dynamic,String ios_addr) {

		if (user_id == null || user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"用户id异常");
		}
		
		
		if(userService.getBasicUser(user_id)==null){
			return ResultUtil.getResultMap(ERROR.ERR_PARAM,"用户不存在");
		}
		
		
		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imagePath = ImageSaveUtils.saveUserImages(file, multipartRequest.getServletContext());
						dynamic.setUser_id(user_id);
						dynamic.setLocal_image_name(imagePath);
						dynamic.setCreate_time(new Date());
						long id = userDynamicService.insertDynamic(dynamic);
						dynamic.setId(id);
						AddressUtil.praseAddress(IPUtil.getIpAddress(multipartRequest),dynamic,ios_addr);
						
						//预先放在首页推荐
//						if (id > 0) {
//							userDynamicService.addHomeFoundSelected(id);
//						}
						
						ModelMap result=ResultUtil.getResultOKMap();
						
						UserDynamic dy = userDynamicService.detail(id,user_id);
						if(dy!=null){
							ImagePathUtil.completeImagePath(dy, true);
							ImagePathUtil.completeAvatarPath(dy.getUser(), true);
						}
						result.put("detail", dy);
						return result;
					} catch (Exception e) {
						e.printStackTrace();
						log.error(e.getMessage());
						break;
					}
				}
			}

		}
		return ResultUtil.getResultMap(ERROR.ERR_PARAM,"无图片上传");
	}
	@RequestMapping("test")
	public ModelMap test(HttpServletRequest request,String lat,String lng) {
		AddressUtil.getAddressByIp(IPUtil.getIpAddress(request));
		return ResultUtil.getResultOKMap();
	}
}
