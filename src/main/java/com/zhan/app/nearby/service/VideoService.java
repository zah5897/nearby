package com.zhan.app.nearby.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.dao.VideoDao;

@Service
public class VideoService {
	@Resource
	private VideoDao videoDao;
	public void save(Video video) {
		videoDao.insert(video);
	}
}
