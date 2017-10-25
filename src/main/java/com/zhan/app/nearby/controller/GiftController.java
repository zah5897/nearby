package com.zhan.app.nearby.controller;

import java.util.Iterator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import com.zhan.app.nearby.bean.Gift;
import com.zhan.app.nearby.service.GiftService;
import com.zhan.app.nearby.util.ImageSaveUtils;

@RestController
@RequestMapping("/gift")
public class GiftController {

	@Resource
	private GiftService giftService;

	@RequestMapping("list")
	public ModelMap list() {
		return giftService.list();
	}

	@RequestMapping("del")
	public ModelMap del(long id) {
		return giftService.delete(id);
	}

	@RequestMapping("add")
	public ModelMap add(Gift gift, HttpServletRequest request) {
		if (request instanceof DefaultMultipartHttpServletRequest) {
			DefaultMultipartHttpServletRequest multiRequest = (DefaultMultipartHttpServletRequest) request;
			Iterator<String> iterator = multiRequest.getFileNames();
			if (iterator.hasNext()) {
				MultipartFile file = multiRequest.getFile((String) iterator.next());
				if (!file.isEmpty()) {
					try {
						String imageName = ImageSaveUtils.saveGiftImages(file, request.getServletContext());
						gift.setImage_url(imageName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return giftService.save(gift);
	}

	@RequestMapping("buy")
	public ModelMap buy(int gift_id, long user_id,String aid) {
		return giftService.buy(gift_id,user_id,aid);
	}
	
	
	@RequestMapping("own")
	public ModelMap own(long user_id,String aid) {
		return giftService.own(user_id,aid);
	}
}
