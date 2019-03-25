package com.zhan.app.nearby.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.easemob.server.example.Main;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.UserDynamicRelationShip;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
import com.zhan.app.nearby.comm.LikeDynamicState;
import com.zhan.app.nearby.comm.PushMsgType;
import com.zhan.app.nearby.dao.UserDao;
import com.zhan.app.nearby.dao.UserDynamicDao;
import com.zhan.app.nearby.exception.ERROR;
import com.zhan.app.nearby.util.ImagePathUtil;
import com.zhan.app.nearby.util.ImageSaveUtils;
import com.zhan.app.nearby.util.ResultUtil;

@Service
public class UserDynamicService {

	private static Logger log = Logger.getLogger(UserDynamicService.class);

	@Resource
	private UserDynamicDao userDynamicDao;
	@Resource
	private UserDao userDao;

	public long insertDynamic(UserDynamic dynamic) {
		long id = userDynamicDao.insertDynamic(dynamic);
		dynamic.setId(id);
		return id;
	}

	public void addHomeFoundSelected(long dynamic_id) {
		userDynamicDao.addHomeFoundSelected(dynamic_id);
	}

	public int praiseDynamic(long dynamic_id, boolean praise) {
		int result = userDynamicDao.praiseDynamic(dynamic_id, praise);
		if (result > 0) {
			if (praise) {
				long user_id = userDynamicDao.getUserIdByDynamicId(dynamic_id);
				Map<String, String> ext = new HashMap<String, String>();
				ext.put("dynamic_id", String.valueOf(dynamic_id));

				String msg = "有人赞了你的图片！";
				ext.put("msg", msg);
				Main.sendTxtMessage(Main.SYS, new String[] { String.valueOf(user_id) }, msg, ext,
						PushMsgType.TYPE_NEW_PRAISE);
			}
		}
		return result;
	}

	public List<UserDynamic> getUserDynamic(long user_id, int page, int count, boolean filterBlock) {
		List<UserDynamic> dynamics = userDynamicDao.getUserDynamic(user_id, page, count, filterBlock);
		ImagePathUtil.completeDynamicsPath(dynamics, true);
		return dynamics;
	}

	public List<UserDynamic> getUserDynamic(long user_id, int page, int count) {
		return getUserDynamic(user_id, page, count, true);
	}

	public long comment(DynamicComment comment) {
		long id = userDynamicDao.comment(comment);
		if (id > 0) {
			long user_id = userDynamicDao.getUserIdByDynamicId(comment.getDynamic_id());

			Map<String, String> ext = new HashMap<String, String>();
			ext.put("comment_id", String.valueOf(comment.getId()));
			ext.put("dynamic_id", String.valueOf(comment.getDynamic_id()));
			String user_id_str = String.valueOf(user_id);

			String msg = "有人评论了你的图片，快去看看！";
			ext.put("msg", msg);
			Main.sendTxtMessage(Main.SYS, new String[] { user_id_str }, msg, ext, PushMsgType.TYPE_NEW_COMMENT);
		}
		return id;
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
		if (dynamic != null) {
			if (user_id == null || user_id < 1) {
				dynamic.setLike_state(LikeDynamicState.UNLIKE.ordinal());
			}
			int likeState = userDynamicDao.getLikeState(user_id, dynamic_id);
			dynamic.setLike_state(likeState);
		}
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

	public String delete(Long user_id, String dynamic_ids) {
		String[] dy_ids = dynamic_ids.split(",");
		String successid = null;
		for (String id : dy_ids) {
			try {
				long dy_id = Long.parseLong(id);
				UserDynamic dy = userDynamicDao.basic(dy_id);
				if (dy != null && dy.getUser_id() == user_id) {
					userDynamicDao.delete(user_id, dy_id);
					ImageSaveUtils.removeUserImages(dy.getLocal_image_name());
				}
				if (successid == null) {
					successid = id;
				} else {
					successid += "," + id;
				}
			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return successid;
	}

	public ModelMap getUserImages(Long user_id, Long last_id, Integer count) {
		if (user_id == null || user_id < 1) {
			return ResultUtil.getResultMap(ERROR.ERR_PARAM, "用户ID异常");
		}
		if (last_id == null || last_id <= 0) {
			last_id = Long.MAX_VALUE;
		}
		if (count == null || count <= 0) {
			count = 5;
		}
		List<Image> userImages = userDynamicDao.getUserImages(user_id, last_id, count);
		ImagePathUtil.completeImagesPath(userImages, true); // 补全图片路径
		boolean hasMore = true;
		if (userImages == null || userImages.size() < count) {
			hasMore = false;
		}
		return ResultUtil.getResultOKMap().addAttribute("images", userImages).addAttribute("hasMore", hasMore);
	}

	public void updateCommentStatus(long user_id, FoundUserRelationship ship) {
		userDynamicDao.updateCommentStatus(user_id, ship);
	}

	public List<UserDynamic> loadFollow(long user_id, long l, int i) {
		List<UserDynamic> dy = userDynamicDao.loadFollow(user_id, l, i);
		for (UserDynamic d : dy) {
			ImagePathUtil.completeAvatarPath(d.getUser(), true);
			d.getUser().setHas_followed(1);
			ImagePathUtil.completeDynamicPath(d, true);
		}
		return dy;
	}

	public List<DynamicComment> loadSubComm(long pid, long did, int count, long last_id) {
		return userDynamicDao.loadSubComm(pid, did, count, last_id);
	}
	
	
	public List<UserDynamic> getDyanmicByState(int pageIndex, int pageSize, DynamicState state){
	    return userDynamicDao.getDyanmicByState(pageIndex, pageSize, state);
	    		
	}

	public int getPageCountByState(int state) {
		return userDynamicDao.getPageCountByState(state);
	}

	public void clearIllegalDynamic() {
		List<UserDynamic> dys=userDynamicDao.getIllegalDyanmic();
		for(UserDynamic dy:dys) {
			ImageSaveUtils.removeUserImages(dy.getLocal_image_name());
			userDynamicDao.updateDynamicImgToIllegal(dy.getId());
		}
	}
	
}
