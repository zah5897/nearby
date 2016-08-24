package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.bean.User;
import com.zhan.app.nearby.cache.UserCacheService;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.dao.ImageDao;
import com.zhan.app.nearby.dao.UserDao;

@Service
@Transactional("transactionManager")
public class MainService {
	@Resource
	private ImageDao imageDao;

	public List<Image> getSelectedImages(long last_img_id, int page_size) {
		return getImagesBySelectedState(ImageStatus.SELECTED, last_img_id, page_size);
	}

	public List<Image> getImagesBySelectedState(ImageStatus imageStatus, long last_img_id, int page_size) {
		return imageDao.getImagesBySelectedState(imageStatus, last_img_id, page_size);
	}

}
