package com.zhan.app.nearby.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.UserDynamicRelationShip;
import com.zhan.app.nearby.comm.LikeDynamicState;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;

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

	public int praiseDynamic(long dynamic_id, boolean praise) {
		return userDynamicDao.praiseDynamic(dynamic_id, praise);
	}

	public List<UserDynamic> getUserDynamic(long user_id, long last_id, int count) {
		List<UserDynamic> dynamics = userDynamicDao.getUserDynamic(user_id, last_id, count);
		ImagePathUtil.completeImagePath(dynamics, true);
		return dynamics;
	}

	public long comment(DynamicComment comment) {
		return userDynamicDao.comment(comment);
	}

	public DynamicComment loadComment(long dynamic_id, long comment_id) {
		DynamicComment comment = userDynamicDao.loadComment(dynamic_id, comment_id);
		ImagePathUtil.completeAvatarPath(comment.getUser(), true);
		return comment;
	}

	public List<DynamicComment> commentList(long dynamic_id, int count, Long last_comment_id) {
		List<DynamicComment> comments = userDynamicDao.commentList(dynamic_id, count,
				last_comment_id == null ? 0 : last_comment_id);
		if (comments != null && comments.size() > 0) {
			for (DynamicComment comment : comments) {
				ImagePathUtil.completeAvatarPath(comment.getUser(), true);
			}
		}
		return comments;
	}

	public UserDynamic detail(long dynamic_id, Long user_id) {
		UserDynamic dynamic = userDynamicDao.detail(dynamic_id);
		if (user_id == null || user_id < 1) {
			dynamic.setLike_state(LikeDynamicState.UNLIKE.ordinal());
		}
		int likeState = userDynamicDao.getLikeState(user_id, dynamic_id);
		dynamic.setLike_state(likeState);
		return dynamic;
	}

	public void updateAddress(UserDynamic dynamic) {
		userDynamicDao.updateAddress(dynamic);
	}

	public void updateBrowserCount(long dynamic_id, int browser_count) {
		userDynamicDao.updateBrowserCount(dynamic_id, browser_count);
	}

	public long getUserIdByDynamicId(long dynamic_id) {
		return userDynamicDao.getUserIdByDynamicId(dynamic_id);
	}

	public long updateLikeState(Long user_id, long dynamic_id, LikeDynamicState like) {
		UserDynamicRelationShip dynamicRelationShip = new UserDynamicRelationShip();
		dynamicRelationShip.setDynamic_id(dynamic_id);
		dynamicRelationShip.setUser_id(user_id);
		dynamicRelationShip.setRelationship(like.ordinal());
		return userDynamicDao.updateLikeState(dynamicRelationShip);
	}

	public String delete(ServletContext servletContext, Long user_id, String dynamic_ids) {
		String[] dy_ids = dynamic_ids.split(",");
		String successid = null;
		for (String id : dy_ids) {
			try {
				long dy_id = Long.parseLong(id);
				UserDynamic dy = userDynamicDao.basic(dy_id);
				if (dy != null && dy.getUser_id() == user_id) {
					userDynamicDao.delete(user_id, dy_id);
					ImageSaveUtils.removeUserImages(servletContext, dy.getLocal_image_name());
					if (successid == null) {
						successid = id;
					} else {
						successid += "," + id;
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return successid;
	}
}
