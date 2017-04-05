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
import com.zhan.app.nearby.comm.Relationship;

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

	public int getSelectedCityCount(int city_id) {
		String sql = "select count(*) from " + TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
				+ " selected on dynamic.id=selected.dynamic_id  where selected.selected_state=?   and (dynamic.city_id=? or  dynamic.district_id=?)";
		return jdbcTemplate.queryForObject(sql, new Object[] { ImageStatus.SELECTED.ordinal(), city_id, city_id },
				Integer.class);
	}

	public List<UserDynamic> getHomeFoundSelected(long user_id, long last_id, int page_size, int city_id,
			boolean isSub) {
		String sql;
		if (user_id > 0) {
			sql = "select dynamic.*,"
					+ "coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=dynamic.id and t_like.user_id=?), '0') as like_state ,"
					+ "user.user_id  ," + "user.nick_name ," + "user.avatar," + "user.sex ," + "user.birthday ,"
					+ "user.type " + "from " + TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
					+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id  "
					+ "where selected.selected_state=? and dynamic.id<? and " + cityIn(isSub) + fiflterBlock()
					+ "  order by dynamic.id desc limit ?";

			Object[] param;
			if (isSub) {
				param = new Object[] { user_id, ImageStatus.SELECTED.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id,
						city_id, city_id, user_id, Relationship.BLACK.ordinal(), page_size };
			} else {
				param = new Object[] { user_id, ImageStatus.SELECTED.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id,
						city_id, city_id, city_id, city_id, user_id, Relationship.BLACK.ordinal(), page_size };
			}
			return jdbcTemplate.query(sql, param, new DynamicMapper());
		} else {

			sql = "select dynamic.* ,"
					+ "user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
					+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
					+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id "
					+ "where selected.selected_state=? and dynamic.id<? and " + cityIn(isSub)
					+ " order by dynamic.id desc limit ?";

			Object[] param;
			if (isSub) {
				param = new Object[] { ImageStatus.SELECTED.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id, city_id,
						city_id, page_size };
			} else {
				param = new Object[] { ImageStatus.SELECTED.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id, city_id,
						city_id, city_id, city_id, page_size };
			}

			return jdbcTemplate.query(sql, param, new DynamicMapper());
		}

	}

	public List<UserDynamic> getSelectedDynamicByTopic(long topic_id, ImageStatus status, long last_id, int page_size) {
		String sql;

		sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
				+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id where selected.selected_state=? and dynamic.id<? and dynamic.topic_id=? order by dynamic.id desc limit ?";
		return jdbcTemplate.query(sql,
				new Object[] { status.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id, topic_id, page_size },
				new DynamicMapper());
	}

	private String fiflterBlock() {
		return " and dynamic.user_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) ";
	}

	private String cityIn(boolean isSub) {
		if (isSub) {
			return "  (dynamic.city_id =?  or dynamic.district_id =? ) ";
		} else {
			return "  (dynamic.city_id =?  or dynamic.district_id =? or dynamic.district_id in (select id from t_sys_city where parent_id=?) or dynamic.city_id in (select id from t_sys_city where parent_id=?) )";
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

		String sql = "select c.*,u.nick_name,u.avatar,at_c.at_content," + "at_c.at_content," + "at_c.at_comment_time,"
				+ "at_c.at_u_id," + "at_c.at_nick_name," + "at_c.at_avatar,"
				+ "at_c.at_create_time from t_dynamic_comment c  left join t_user u on c.user_id=u.user_id "
				+ "left join (select cc.id as at_commtent_id, " + "cc.content as at_content,"
				+ "cc.comment_time as at_comment_time," + "cc.user_id as at_u_id ," + "au.nick_name as at_nick_name ,"
				+ "au.avatar as at_avatar ," + "cc.comment_time as at_create_time "
				+ "from t_dynamic_comment cc left join t_user au on cc.user_id=au.user_id  ) as at_c on c.at_comment_id=at_c.at_commtent_id   where c.dynamic_id=? and c.id=?";
		List<DynamicComment> comments = jdbcTemplate.query(sql, new Object[] { dynamic_id, comment_id },
				new DynamicCommentMapper());
		return comments.get(0);
	}

	public List<DynamicComment> commentList(long dynamic_id, int count, long last_comment_id) {

		String sql = "select c.*,u.nick_name,u.avatar,at_c.at_content," + "at_c.at_content," + "at_c.at_comment_time,"
				+ "at_c.at_u_id," + "at_c.at_nick_name," + "at_c.at_avatar,"
				+ "at_c.at_create_time from t_dynamic_comment c  left join t_user u on c.user_id=u.user_id "
				+ "left join (select cc.id as at_commtent_id, " + "cc.content as at_content,"
				+ "cc.comment_time as at_comment_time," + "cc.user_id as at_u_id ," + "au.nick_name as at_nick_name ,"
				+ "au.avatar as at_avatar ," + "cc.comment_time as at_create_time "
				+ "from t_dynamic_comment cc left join t_user au on cc.user_id=au.user_id  ) as at_c on c.at_comment_id=at_c.at_commtent_id   where c.dynamic_id=? and c.id<? order by c.id desc limit ?";

		return jdbcTemplate.query(sql,
				new Object[] { dynamic_id, last_comment_id <= 0 ? Long.MAX_VALUE : last_comment_id, count },
				new DynamicCommentMapper());

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
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		} catch (Exception e) {
			return null;
		}
	}

	public void updateAddress(UserDynamic dynamic) {
		jdbcTemplate.update(
				"update " + TABLE_USER_DYNAMIC
						+ " set addr=? ,street=?,city=?,region=? ,city_id=?,district_id=?, ip=? where id=?",
				new Object[] { dynamic.getAddr(), dynamic.getStreet(), dynamic.getCity(), dynamic.getRegion(),
						dynamic.getCity_id(), dynamic.getDistrict_id(), dynamic.getIp(), dynamic.getId() });
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

	public int getMostCityID() {
		String sql = "select c.city_id from (select count(*) as count, gb.* from t_user_dynamic gb group by gb.city_id) as c order by c.count desc limit 1";
		return jdbcTemplate.queryForObject(sql, Integer.class);

	}

	// public int getCityImageCount(long user_id, int city_id) {
	// String sql = "select count(*) from " + TABLE_USER_DYNAMIC + " dynamic
	// left join " + TABLE_HOME_FOUND_SELECTED
	// + " selected on dynamic.id=selected.dynamic_id "
	// + " where selected.selected_state=? and (dynamic.city_id=? or
	// dynamic.district_id=?) " + fiflterBlock();
	// return jdbcTemplate.queryForObject(sql, new Object[] {
	// ImageStatus.SELECTED.ordinal(), city_id, city_id,
	// user_id, Relationship.BLACK.ordinal() }, Integer.class);
	// }

}
