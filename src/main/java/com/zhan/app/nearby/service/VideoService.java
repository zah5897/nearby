package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zhan.app.nearby.bean.Video;
import com.zhan.app.nearby.bean.VideoComment;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.VideoDao;
import com.zhan.app.nearby.util.ImagePathUtil;

@Service
public class VideoService {
	@Resource
	private VideoDao videoDao;
	@Resource
	private UserDao userDao;

	public void save(Video video) {
//		video.setId(ObjectId.get().toString());
	    videoDao.insert(video);
		if(video.getType()==3) {//发布了短视频认证，修改其认证状态
			userDao.changeUserCertStatus(video.getUid(), -1); //审核中
		}
	}

	public List<Video> mine(long user_id, Long last_id, int count) {
		List<Video> list = videoDao.mine(user_id, last_id, count);
		ImagePathUtil.completeVideosPath(list);
		return list;
	}

	// 获取某人的短视频（已审核通过）
	public List<Video> loadByUid(long user_id, Long last_id, int count) {
		List<Video> list = videoDao.loadByUid(user_id, last_id, count);
		ImagePathUtil.completeVideosPath(list);
		return list;
	}
	public Video loadByid(long id) {
		List<Video> list = videoDao.loadByid(id);
		ImagePathUtil.completeVideosPath(list);
		if(list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	// 获取所有人的发布的已经通过审核的视频
	public List<Video> list(long uid,Long last_id, int count, Integer type, Integer secret_level) {
		List<Video> list = videoDao.listAll(uid,last_id, count, type, secret_level);
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

	public void praise(long uid, long video_id) {
		videoDao.addPraiseHistory(uid, video_id);
	}

	public void cancelPraise(long uid, long video_id) {
		videoDao.cancelPraise(uid, video_id);
	}
	
	public void store(long uid, long video_id) {
		videoDao.addStoreHistory(uid, video_id);
	}

	public void cancelStore(long uid, long video_id) {
		videoDao.cancelStore(uid, video_id);
	}
	public void addShareCount(long video_id) {
		videoDao.addShareCount(video_id);
	}

	public void addScanCount(long video_id) {
		videoDao.addScanCount(video_id);
	}

	public int getCountByStatus(int status,boolean isUserCert) {
		return videoDao.getCountByStatus(status,isUserCert);
	}
	 
	public List<Video> loadByStatus(int status, int page, int count,boolean isUserCert) {
		return videoDao.loadByStatus(status, page, count,isUserCert);
	}

	public void changeStatus(int id, int newStatus) {
		videoDao.changeStatus(id, newStatus);

	}
	public int getTodayConfirmVideCount(long uid) {
		return videoDao.getTodayConfirmVideCount(uid);
	}
	public Video loadConfirmdVideo(long uid) {
		List<Video> vs= videoDao.loadConfirmdVideo(uid);
		if(vs.isEmpty()) {
			return null;
		}
		Video v=vs.get(0);
		ImagePathUtil.completeVideoPath(v);
		return v;
		
	}
	public Video loadLatestConfirmVideo(long uid) {
		List<Video> vs= videoDao.loadLatestConfirmVideo(uid);
		if(vs.isEmpty()) {
			return null;
		}
		Video v=vs.get(0);
		ImagePathUtil.completeVideoPath(v);
		return v;
		
	}
}
