package com.zhan.app.nearby.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.dao.BottleDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.UserDynamicService;
import com.zhan.app.nearby.service.UserService;
import com.zhan.app.nearby.util.AddressUtil;
import com.zhan.app.nearby.util.BottleKeyWordUtil;
import com.zhan.app.nearby.util.IPUtil;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;
import com.zhan.app.nearby.util.SpringContextUtil;

@RestController
@RequestMapping("/image")
public class ImageController {
	@Resource
	private UserService userService;

	@Resource
	private UserDynamicService userDynamicService;

	@Resource
	private UserCacheService userCacheService;

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
	public ModelMap upload(DefaultMultipartHttpServletRequest multipartRequest, Long user_id, UserDynamic dynamic,
			String ios_addr) {

		if (user_id == null || user_id < 0) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户id异常");
		}

		if (userService.getBasicUser(user_id) == null) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户不存在");
		}

		long last_time = userCacheService.getLastUploadTime(user_id);
		long cur_time = System.currentTimeMillis() / 1000;

		if (cur_time - last_time < 60) {
			return ResultUtil.getResultMap(ERROR.ERR_FREUENT);
		}
		userCacheService.setLastUploadTime(user_id);
		log.error("user_id=" + user_id + ",upload img log.");

		if (multipartRequest != null) {
			Iterator<String> iterator = multipartRequest.getFileNames();
			while (iterator.hasNext()) {
				MultipartFile file = multipartRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imagePath = ImageSaveUtils.saveUserImages(file);
						dynamic.setUser_id(user_id);
						String content = BottleKeyWordUtil.filterContent(dynamic.getDescription());
						dynamic.setDescription(content);

						dynamic.setState(DynamicState.T_CREATE.ordinal());
						dynamic.setLocal_image_name(imagePath);
						dynamic.setCreate_time(new Date());
						long id = userDynamicService.insertDynamic(dynamic);
						dynamic.setId(id);
						AddressUtil.praseAddress(IPUtil.getIpAddress(multipartRequest), dynamic, ios_addr);

						// 预先放在首页推荐
						// if (id > 0) {
						// userDynamicService.addHomeFoundSelected(id);
						// }

						ModelMap result = ResultUtil.getResultOKMap();

						UserDynamic dy = userDynamicService.detail(id, user_id);
						if (dy != null) {
							ImagePathUtil.completeDynamicPath(dy, true);
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
		return ResultUtil.getResultMap(ERROR.ERR_PARAM, "无图片上传");
	}

	@RequestMapping("test")
	public ModelMap test(HttpServletRequest request, String lat, String lng) {
		String ipResult = IPUtil.getIpAddress(request);
		System.out.println(ipResult);
		String[] result = AddressUtil.getAddressByIp(ipResult);
		System.out.println(result);
		return ResultUtil.getResultOKMap();
	}

	@RequestMapping("load_word")
	public ModelMap load_word(HttpServletRequest request, String lat, String lng) {
		String s=getfileinfo();
		String[] words=s.replace("，", ",").split(",");
		Set<String> wordSet=new HashSet<String>();
		for(String c:words) {
			wordSet.add(c);
		}
		BottleDao dao=SpringContextUtil.getBean("bottleDao");
		for(String c:wordSet) {
			dao.insertAnswer(c);
		}
		return ResultUtil.getResultOKMap();
	}

	public String getfileinfo() {
		StringBuilder rstr = new StringBuilder();
		try {
			InputStream in   = new FileInputStream(new File("C:\\Users\\zah\\Desktop\\word.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				rstr.append(str);
			}
			br.close();
		} catch (IOException e) {
			// todo loginfo
		}
		return rstr.toString();
	}
}
