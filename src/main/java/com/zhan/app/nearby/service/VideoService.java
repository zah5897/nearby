package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Appointment;
import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VideoComment;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.VideoDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ObjectId;

@Service
public class VideoService {
	@Resource
	private VideoDao videoDao;
	@Resource
	private UserDao userDao;

	public void save(Video video) {
//		video.setId(ObjectId.get().toString());
		long id = videoDao.insert(video);
		video.setId(id);
	}

	public List<Video> mine(long user_id, Long last_id, int count) {
		List<Video> list = videoDao.mine(user_id, last_id, count);
		ImagePathUtil.completeVideosPath(list);
		return list;
	}

	//获取某人的短视频（已审核通过）
	public List<Video> loadByUid(long user_id, Long last_id, int count) {
		List<Video> list = videoDao.loadByUid(user_id, last_id, count);
		ImagePathUtil.completeVideosPath(list);
		return list;
	}
	
	//获取所有人的发布的已经通过审核的视频
	public List<Video> list(Long last_id, int count) {
		List<Video> list = videoDao.listAll(last_id, count);
		ImagePathUtil.completeVideosPath(list);
		return list;
	}

	public List<VideoComment> listComment(long user_id, String vid, Integer last_id, int count) {
		List<VideoComment> list = videoDao.listComment(user_id, vid, last_id, count);
		return list;
	}

	public void comment(VideoComment comment) {
		videoDao.insertObject(comment);
		videoDao.addCommentCount(comment.getVideo_id());
		BaseUser user = userDao.getBaseUserNoToken(comment.getUid());
		ImagePathUtil.completeAvatarPath(user, true);
		comment.setUser(user);
	}

	public void praise(long uid, String video_id) {
		videoDao.addPraiseHistory(uid, video_id);
	}

	public void store(long uid, String video_id) {
		videoDao.addStoreHistory(uid, video_id);
	}

	public void addShareCount(String video_id) {
		videoDao.addShareCount(video_id);
	}

	public int getCountByStatus(int status) {
		return videoDao.getCountByStatus(status);
	}

	public List<Video> loadByStatus(int status, int page, int count) {
		return videoDao.loadByStatus(status, page, count);
	}

	public void changeStatus(int id, int newStatus) {
		videoDao.changeStatus(id,newStatus);
		
	}
}
