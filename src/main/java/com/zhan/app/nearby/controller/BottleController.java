package com.zhan.app.nearby.controller;

import java.util.Iterator;
import java.util.List;

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

	@RequestMapping("get_one")
	public ModelMap get_one(Long user_id) {
		Bottle bottle = bottleService.getBottleFromPool(user_id == null ? 0 : user_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottle", bottle);
		return result;
	}

	@RequestMapping("catch")
	public ModelMap catchBottl(Long user_id) {
		Bottle bottle = bottleService.getBottleFromPool(user_id == null ? 0 : user_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottle", bottle);
		return result;
	}

	@RequestMapping("main_refresh")
	public ModelMap main_refresh(Long user_id) {
		List<Bottle> bottles = bottleService.getBottlesFromPool(user_id == null ? 0 : user_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottles", bottles);
		return result;
	}

	@RequestMapping("list")
	public ModelMap list(Long user_id, Long last_id, Integer page_size) {
		List<Bottle> bottles = bottleService.getBottles(user_id==null?0:user_id, last_id==null?0:last_id, page_size==null?5:page_size);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottles", bottles);
		return result;
	}

	@RequestMapping("load")
	public ModelMap load(long user_id, long bottle_id) {
		Bottle bottle = bottleService.getBottleDetial(bottle_id);
		ModelMap result = ResultUtil.getResultOKMap();
		result.put("bottle", bottle);
		return result;
	}
}
