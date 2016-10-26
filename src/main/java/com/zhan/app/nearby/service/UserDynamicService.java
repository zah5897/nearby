package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.util.ImagePathUtil;

@Service
@Transactional("transactionManager")
public class UserDynamicService {
	@Resource
	private UserDynamicDao userDynamicDao;

	public long insertDynamic(UserDynamic dynamic) {
		return userDynamicDao.insertDynamic(dynamic);
	}

	public void addHomeFoundSelected(long dynamic_id) {
		userDynamicDao.addHomeFoundSelected(dynamic_id);
	}

	public int praiseDynamic(long dynamic_id) {
		return userDynamicDao.praiseDynamic(dynamic_id);
	}

	public List<UserDynamic> getUserDynamic(long user_id, long last_id, int count) {
		List<UserDynamic> dynamics = userDynamicDao.getUserDynamic(user_id, last_id, count);
		ImagePathUtil.completeImagePath(dynamics, true);
		return dynamics;
	}

	public long comment(DynamicComment comment) {
		return userDynamicDao.comment(comment);
	}
	
	public DynamicComment loadComment(long dynamic_id,long comment_id) {
		DynamicComment comment=userDynamicDao.loadComment(dynamic_id, comment_id);
		ImagePathUtil.completeAvatarPath(comment.getUser(), true);
		return comment;
	}
	
	
	
	public List<DynamicComment> commentList(long dynamic_id,int count,Long last_comment_id) {
		return userDynamicDao.commentList(dynamic_id, count,last_comment_id==null?0:last_comment_id);
	}
	public UserDynamic detail(long dynamic_id) {
		return userDynamicDao.detail(dynamic_id);
	}
}
