package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.UserDynamicRelationShip;
import com.zhan.app.nearby.bean.mapper.DynamicCommentMapper;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.comm.ImageStatus;
import com.zhan.app.nearby.comm.LikeDynamicState;

@Repository("userDynamicDao")
public class UserDynamicDao extends BaseDao {
	public static final String TABLE_USER_DYNAMIC = "t_user_dynamic";
	public static final String TABLE_HOME_FOUND_SELECTED = "t_home_found_selected";
	public static final String TABLE_DYNAMIC_COMMENT = "t_dynamic_comment";
	public static final String TABLE_LIKE_DYNAMIC_STATE = "t_like_dynamic";
	@Resource
	private JdbcTemplate jdbcTemplate;
	private static Logger log = Logger.getLogger(UserDynamicDao.class);

	public long insertDynamic(UserDynamic dyanmic) {
		return saveObj(jdbcTemplate, TABLE_USER_DYNAMIC, dyanmic);
	}

	public List<UserDynamic> getHomeFoundSelected(long user_id, ImageStatus status, long last_id, int page_size,
			int city_id) {
		String sql;
		if (user_id > 0) {
			if (last_id < 1) {
				sql = "select dynamic.*,coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=dynamic.id and t_like.user_id=?), '0') as like_state ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday ,user.type from "
						+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
						+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id    where selected.selected_state=? and (dynamic.city_id=? or dynamic.district_id=?)   order by dynamic.id desc limit ?";
				return jdbcTemplate.query(sql, new Object[] { user_id, status.ordinal(), city_id, city_id, page_size },
						new DynamicMapper());
			} else {
				sql = "select dynamic.*,coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=dynamic.id and t_like.user_id=?), '0') as like_state ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday ,user.type from "
						+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
						+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id   where selected.selected_state=? and dynamic.id<? and (dynamic.city_id=? or dynamic.district_id=?)   order by dynamic.id desc limit ?";
				return jdbcTemplate.query(sql,
						new Object[] { user_id, status.ordinal(), last_id, city_id, city_id, page_size },
						new DynamicMapper());
			}
		} else {
			if (last_id < 1) {
				sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
						+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
						+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id  where selected.selected_state=? and (dynamic.city_id=? or dynamic.district_id=?)  order by dynamic.id desc limit ?";
				return jdbcTemplate.query(sql, new Object[] { status.ordinal(), city_id, city_id, page_size },
						new DynamicMapper());
			} else {
				sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
						+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
						+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id where selected.selected_state=? and dynamic.id<? and (dynamic.city_id=? or dynamic.district_id=?) order by dynamic.id desc limit ?";
				return jdbcTemplate.query(sql, new Object[] { status.ordinal(), last_id, city_id, city_id, page_size },
						new DynamicMapper());
			}
		}

	}

	public int addHomeFoundSelected(long dynamic_id) {

		String checkHas = "select count(*) from " + TABLE_HOME_FOUND_SELECTED
				+ " where dynamic_id=? and selected_state=?";
		int count = jdbcTemplate.queryForObject(checkHas, new Object[] { dynamic_id, ImageStatus.SELECTED.ordinal() },
				Integer.class);

		if (count > 0) {
			return 0;
		}
		String sql = "insert into " + TABLE_HOME_FOUND_SELECTED + " values (?, ?)";
		return jdbcTemplate.update(sql, new Object[] { dynamic_id, ImageStatus.SELECTED.ordinal() });
	}

	public List<Integer> getPraiseCount(long dynamic_id) {
		String sql = "select praise_count from " + TABLE_USER_DYNAMIC + " where id=?";
		return jdbcTemplate.queryForList(sql, new Object[] { dynamic_id }, Integer.class);
	}

	public int praiseDynamic(long dynamic_id, boolean praise) {
		List<Integer> result = getPraiseCount(dynamic_id);
		if (result == null || result.size() == 0) {
			return -1;
		}
		Integer hasPraiseCount = result.get(0);
		if (hasPraiseCount == null || hasPraiseCount < 0) {
			hasPraiseCount = 0;
		}
		if (praise) {
			hasPraiseCount += 1;
		} else {
			hasPraiseCount -= 1;
			if (hasPraiseCount < 0) {
				hasPraiseCount = 0;
			}
		}
		return jdbcTemplate.update("update " + TABLE_USER_DYNAMIC + " set praise_count=? where id=?",
				new Object[] { hasPraiseCount, dynamic_id });
	}

	public List<UserDynamic> getUserDynamic(long user_id, long last_id, int count) {
		if (last_id < 1) {
			String sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state from " + TABLE_USER_DYNAMIC
					+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id where dy.user_id=? order by dy.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, count },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		} else {
			String sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state from " + TABLE_USER_DYNAMIC
					+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id where dy.user_id=? and dy.id<? order by dy.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		}
	}

	public long comment(DynamicComment comment) {
		return saveObj(jdbcTemplate, TABLE_DYNAMIC_COMMENT, comment);
	}

	public DynamicComment loadComment(long dynamic_id, long comment_id) {

		String sql = "select comment.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.type from "
				+ TABLE_DYNAMIC_COMMENT
				+ " comment left join t_user user on comment.user_id=user.user_id where comment.dynamic_id=? and comment.id=?";

		return jdbcTemplate.queryForObject(sql, new Object[] { dynamic_id, comment_id }, new DynamicCommentMapper());
	}

	public List<DynamicComment> commentList(long dynamic_id, int count, long last_comment_id) {

		if (last_comment_id > 0) {
			String sql = "select comment.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday ,user.type from "
					+ TABLE_DYNAMIC_COMMENT
					+ " comment left join t_user user on comment.user_id=user.user_id where comment.dynamic_id=? and comment.id<? order by comment.id desc limit ?";

			return jdbcTemplate.query(sql, new Object[] { dynamic_id, last_comment_id, count },
					new DynamicCommentMapper());
		} else {
			String sql = "select comment.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday ,user.type from "
					+ TABLE_DYNAMIC_COMMENT
					+ " comment left join t_user user on comment.user_id=user.user_id where comment.dynamic_id=?  order by comment.id desc limit ?";

			return jdbcTemplate.query(sql, new Object[] { dynamic_id, count }, new DynamicCommentMapper());
		}

	}

	public UserDynamic detail(long dynamic_id) {
		try {
			String sql = "select dy.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type  from "
					+ TABLE_USER_DYNAMIC + " dy left join t_user user on dy.user_id=user.user_id where dy.id=?";
			return jdbcTemplate.queryForObject(sql, new Object[] { dynamic_id }, new DynamicMapper());
		} catch (Exception e) {
			return null;
		}
	}

	public UserDynamic basic(long dynamic_id) {
		try {
			String sql = "select  *   from " + TABLE_USER_DYNAMIC + "   where id=?";
			return jdbcTemplate.queryForObject(sql, new Object[] { dynamic_id },
					new BeanPropertyRowMapper<>(UserDynamic.class));
		} catch (Exception e) {
			return null;
		}
	}

	public void updateAddress(UserDynamic dynamic) {
		jdbcTemplate.update(
				"update " + TABLE_USER_DYNAMIC
						+ " set addr=? ,street=?,city=?,region=? ,city_id=?,district_id=? where id=?",
				new Object[] { dynamic.getAddr(), dynamic.getStreet(), dynamic.getCity(), dynamic.getRegion(),
						dynamic.getCity_id(), dynamic.getDistrict_id(), dynamic.getId() });
	}

	public void updateBrowserCount(long dynamic_id, int browser_count) {
		jdbcTemplate.update("update " + TABLE_USER_DYNAMIC + " set browser_count=? where id=?",
				new Object[] { browser_count, dynamic_id });
	}

	public long getUserIdByDynamicId(long dynamic_id) {
		try {
			String sql = "select user_id  from " + TABLE_USER_DYNAMIC + " where id=?";
			return jdbcTemplate.queryForObject(sql, new Object[] { dynamic_id }, long.class);
		} catch (Exception e) {
			return 0l;
		}
	}

	public long updateLikeState(UserDynamicRelationShip dynamicRelationShip) {
		String sql = "select count(*) from " + TABLE_LIKE_DYNAMIC_STATE + " where user_id=? and dynamic_id=?";
		int count = 0;
		try {
			count = jdbcTemplate.queryForObject(sql,
					new Object[] { dynamicRelationShip.getUser_id(), dynamicRelationShip.getDynamic_id() },
					Integer.class);
		} catch (Exception e) {
			log.error(e);
		}
		if (count == 0) {
			return saveObj(jdbcTemplate, TABLE_LIKE_DYNAMIC_STATE, dynamicRelationShip);
		} else {
			return jdbcTemplate.update(
					"update " + TABLE_LIKE_DYNAMIC_STATE + " set relationship=? where user_id=? and dynamic_id=?",
					new Object[] { dynamicRelationShip.getRelationship(), dynamicRelationShip.getUser_id(),
							dynamicRelationShip.getDynamic_id() });
		}
	}

	public int getLikeState(Long user_id, long dynamic_id) {
		String sql = "select relationship from " + TABLE_LIKE_DYNAMIC_STATE + " where user_id=? and dynamic_id=?";
		try {
			return jdbcTemplate.queryForObject(sql, new Object[] { user_id, dynamic_id }, Integer.class);
		} catch (Exception e) {
			log.error(e);
		}
		return LikeDynamicState.UNLIKE.ordinal();
	}

	public int delete(Long user_id, long dynamic_id) {
		// 删除本体
		String sql = "delete from " + TABLE_USER_DYNAMIC + " where user_id=? and id=?";
		jdbcTemplate.update(sql, new Object[] { user_id, dynamic_id });
		// 删除喜欢记录
		sql = "delete from t_like_dynamic where user_id=? and dynamic_id=?";
		jdbcTemplate.update(sql, new Object[] { user_id, dynamic_id });

		// 删除首页选中
		sql = "delete from t_home_found_selected where  dynamic_id=?";
		jdbcTemplate.update(sql, new Object[] { dynamic_id });

		return 0;
	}

}
