package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.dao.VideoDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ObjectId;

@Service
public class VideoService {
	@Resource
	private VideoDao videoDao;

	public void save(Video video) {
		video.setId(ObjectId.get().toString());
		videoDao.insert(video);
	}

	public List<Video> mine(long user_id, int page, int count) {
		List<Video> list = videoDao.mine(user_id, page, count);
		ImagePathUtil.completeVideosPath(list);
		return list;
	}
	public List<Video> list(long user_id, int page, int count) {
		List<Video> list = videoDao.mine(user_id, page, count);
		ImagePathUtil.completeVideosPath(list);
		return list;
	}
}
