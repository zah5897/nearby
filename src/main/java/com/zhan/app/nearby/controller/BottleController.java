package com.zhan.app.nearby.controller;

import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.Bottle;
import com.zhan.app.nearby.bean.type.BottleType;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.service.BottleService;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@RestController
@RequestMapping("/bottle")
public class BottleController {

	@Resource
	private BottleService bottleService;

	@RequestMapping("send")
	public ModelMap send(Bottle bottle, HttpServletRequest request) {

		if (bottle.getUser_id() <= 0) {
			return ResultUtil.getResultMap(ERROR.ERR_NO_LOGIN);
		}
		if (bottle.getType() == BottleType.IMG.ordinal() && (request instanceof DefaultMultipartHttpServletRequest)) {
			DefaultMultipartHttpServletRequest multiRequest = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = multiRequest.getFileNames();
			if (iterator.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imageName = ImageSaveUtils.saveBottleImages(file, request.getServletContext());
						bottle.setContent(imageName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		bottleService.insert(bottle);
		return ResultUtil.getResultOKMap().addAttribute("bottle", bottle);
	}

	@RequestMapping("list")
	public ModelMap list(Long user_id, Long last_id, Integer page_size, Integer look_sex) {
		return bottleService.getBottles(user_id == null ? 0 : user_id, last_id == null ? 0 : last_id,
				page_size == null ? 5 : page_size, look_sex);
	}

	@RequestMapping("load")
	public ModelMap load(long user_id, long bottle_id) {
		Bottle bottle = bottleService.getBottleDetial(bottle_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottle", bottle);
		return result;
	}

	@RequestMapping("mine")
	public ModelMap mine(long user_id, Long last_id, Integer page_size) {
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottles", bottleService.getMineBottles(user_id, last_id == null ? 0 : last_id,
				page_size == null ? 20 : page_size));
		return result;
	}

	@RequestMapping("delete")
	public ModelMap delete(long user_id, long bottle_id) {
		return bottleService.delete(user_id, bottle_id);
	}
}
