package com.zhan.app.nearby.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.dao.ImageDao;

@Service
@Transactional("transactionManager")
public class ImageService {
	@Resource
	private ImageDao imageDao;

	public long insertImage(Image image) {
		return imageDao.insertImage(image);
	}

	public void addSelectedImage(long image_id) {
		imageDao.addSelectedImage(image_id);
	}
}
