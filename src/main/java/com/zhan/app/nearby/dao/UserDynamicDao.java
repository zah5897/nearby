package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.Image;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.UserDynamicRelationShip;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.bean.user.BaseVipUser;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.FoundUserRelationship;
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

	public List<UserDynamic> getHomeFoundSelected(long user_id, long last_id, int page_size, City city) {
		String sql = "select dy.*,"
				+ " coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=dy.id and t_like.user_id=?), '0') as like_state ,"
				+ " u.user_id , u.nick_name ,u.avatar,u.sex ,u.birthday ,u.type " + "from " + TABLE_USER_DYNAMIC
				+ " dy left join " + TABLE_HOME_FOUND_SELECTED + " hs " + " on dy.id=hs.dynamic_id "
				+ " left join t_user u on  dy.user_id=u.user_id "
				+ " left join t_user_relationship ur on dy.user_id=ur.with_user_id "
				+ " where hs.selected_state=? and dy.id<?   " + cityIn(city)
				+ " and ur.user_id<>? and ur.relationship=?  order by dy.id desc limit ?";

		long lastID = (last_id <= 0 ? Long.MAX_VALUE : last_id);
		Object[] param = new Object[] { user_id, ImageStatus.SELECTED.ordinal(), lastID, user_id,
				Relationship.BLACK.ordinal(), page_size };
		return jdbcTemplate.query(sql, param, new DynamicMapper());
//		} else {
//			if (city != null) {
//				sql = "select dynamic.* ,"
//						+ "user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
//						+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
//						+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id "
//						+ "where selected.selected_state=? and dynamic.id<? and " + cityIn(city)
//						+ " order by dynamic.id desc limit ?";
//
//				param = new Object[] { ImageStatus.SELECTED.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id,
//						city.getId(), page_size };
//			} else {
//				sql = "select dynamic.* ,"
//						+ "user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
//						+ TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
//						+ " selected on dynamic.id=selected.dynamic_id left join t_user user on  dynamic.user_id=user.user_id "
//						+ "where selected.selected_state=? and dynamic.id<?  order by dynamic.id desc limit ?";
//
//				param = new Object[] { ImageStatus.SELECTED.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id,
//						page_size };
//			}
//
//			return jdbcTemplate.query(sql, param, new DynamicMapper());
//		}
	}

	public void addFloverCount(long dy_id) {
		String sql = "update " + TABLE_USER_DYNAMIC + " set flover_count=flover_count+1 where id=" + dy_id;
		jdbcTemplate.update(sql);
	}

	public void addCommentCount(long dy_id) {
		String sql = "update " + TABLE_USER_DYNAMIC + " set comment_count=comment_count+1 where id=" + dy_id;
		jdbcTemplate.update(sql);
	}

	public int praiseDynamic(long dynamic_id, boolean praise) {

		if (praise) {
			return jdbcTemplate
					.update("update " + TABLE_USER_DYNAMIC + " set praise_count=praise_count+1 where id=" + dynamic_id);
		} else {
			return jdbcTemplate.update("update " + TABLE_USER_DYNAMIC
					+ " set praise_count=praise_count-1 where praise_count>0 and  id=" + dynamic_id);
		}
	}

	public List<UserDynamic> getHomeFoundSelectedRandom(long user_id, int size) {
		String sql = "select dynamic.*,"
				+ "coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=dynamic.id and t_like.user_id=?), '0') as like_state ,"
				+ "user.user_id  ," + "user.nick_name ," + "user.avatar," + "user.sex ," + "user.birthday ,"
				+ "user.type " + "from " + TABLE_USER_DYNAMIC + " dynamic left join " + TABLE_HOME_FOUND_SELECTED
				+ " selected on dynamic.id=selected.dynamic_id "
				+ " left join t_user user on  dynamic.user_id=user.user_id  "
				+ " left join t_user_relationship ur on dynamic.user_id=ur.with_user_id "
				+ "where selected.selected_state=?  ur.user_id<>? and ur.relationship=?   order by RAND() limit ?";

		Object[] param = new Object[] { user_id, ImageStatus.SELECTED.ordinal(), user_id, Relationship.BLACK.ordinal(),
				size };
		return jdbcTemplate.query(sql, param, new DynamicMapper());
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

//	private String fiflterBlock() {
//		return " and dynamic.user_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) ";
//	}

	private String cityIn(City city) {
		if (city == null) {
			return "";
		}
		if (city.getParent_id() == 0) {
			return " and   dy.province_id=" + city.getId();
		} else {
			return " and   dy.city_id=" + city.getId();
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

	public List<UserDynamic> getUserDynamic(long user_id, int page, int count, boolean filterBlock) {
		String sql = null;
		if (filterBlock) {
			sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state,u.nick_name,u.avatar,u.sex,u.type,u.birthday from "
					+ TABLE_USER_DYNAMIC
					+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id left join t_user u on dy.user_id=u.user_id  where dy.user_id=? and dy.state<>"
					+ DynamicState.T_ILLEGAL.ordinal() + " order by dy.id desc limit ?,?";
		} else {
			sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state,u.nick_name,u.avatar,u.sex,u.type,u.birthday from "
					+ TABLE_USER_DYNAMIC
					+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id left join t_user u on dy.user_id=u.user_id  where dy.user_id=? order by dy.id desc limit ?,?";
		}

		return jdbcTemplate.query(sql, new Object[] { user_id, (page - 1) * count, count }, new DynamicMapper());
	}

	public List<UserDynamic> getAllDynamic() {
		String sql = "select * from " + TABLE_USER_DYNAMIC;
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
	}

	public long comment(DynamicComment comment) {
		return saveObj(jdbcTemplate, TABLE_DYNAMIC_COMMENT, comment);
	}

	public DynamicComment loadComment(long dynamic_id, long comment_id) {

		String atSql = "select cc.id as at_commtent_id,cc.content as at_content, cc.comment_time as at_comment_time,cc.user_id as at_u_id ,au.nick_name as at_nick_name ,au.avatar as at_avatar,au.sex as at_sex ,cc.comment_time as at_create_time ,atv.vip_id as at_vip_id "
				+ "from t_dynamic_comment cc left join t_user au on cc.user_id=au.user_id left join t_user_vip atv on au.user_id=atv.user_id";
		String sql = "select c.*,u.nick_name,u.avatar,u.sex,v.vip_id, "
				+ "at_c.* from t_dynamic_comment c  left join t_user u on c.user_id=u.user_id " + "left join ( " + atSql
				+ " ) as at_c on c.at_comment_id=at_c.at_commtent_id  left join t_user_vip v on c.user_id=v.user_id  where c.dynamic_id=? and c.id=?";
		List<DynamicComment> comments = jdbcTemplate.query(sql, new Object[] { dynamic_id, comment_id },
				new DynamicCommentMapper());
		return comments.get(0);
	}

	public List<DynamicComment> commentList(long dynamic_id, int count, long last_comment_id) {
		String sql = "select c.*,u.nick_name,u.avatar,u.sex,v.vip_id from t_dynamic_comment c "
				+ "left join t_user u on c.user_id=u.user_id  " + "left join t_user_vip v on c.user_id=v.user_id  "
				+ " where c.status<>? and  c.dynamic_id=? and c.id<? and  c.pid=0 order by c.id desc limit ?";
		return jdbcTemplate
				.query(sql,
						new Object[] { FoundUserRelationship.GONE.ordinal(), dynamic_id,
								last_comment_id <= 0 ? Long.MAX_VALUE : last_comment_id, count },
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
		jdbcTemplate.update("update " + TABLE_USER_DYNAMIC
				+ " set addr=? ,street=?,city=?,region=? ,province_id=?,city_id=?,district_id=?, ip=? where id=?",
				new Object[] { dynamic.getAddr(), dynamic.getStreet(), dynamic.getCity(), dynamic.getRegion(),
						dynamic.getProvince_id(), dynamic.getCity_id(), dynamic.getDistrict_id(), dynamic.getIp(),
						dynamic.getId() });
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
		int count = jdbcTemplate.queryForObject(sql,
				new Object[] { dynamicRelationShip.getUser_id(), dynamicRelationShip.getDynamic_id() }, Integer.class);
		if (count == 0) {
			saveObj(jdbcTemplate, TABLE_LIKE_DYNAMIC_STATE, dynamicRelationShip);
			return 0;
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
		try {
			String sql = "select c.city_id from (select count(*) as count, gb.* from t_user_dynamic gb group by gb.city_id) as c order by c.count desc limit 1";
			return jdbcTemplate.queryForObject(sql, Integer.class);
		} catch (Exception e) {
		}
		return 1;
	}

	// 获取用户发布的动态里面的图片
	public List<Image> getUserImages(long user_id, long last_image_id, int count) {
		return jdbcTemplate.query(
				"select *from t_user_dynamic  where user_id=? and local_image_name<>? and id<? order by id desc limit ?",
				new Object[] { user_id, "", last_image_id, count }, new BeanPropertyRowMapper<Image>(Image.class));
	}

	public int updateCityId(long dy_id, int province_id, int city_id, int district_id) {
		return jdbcTemplate.update("update t_user_dynamic  set province_id=?,city_id=?,district_id=? where id=?",
				new Object[] { province_id, city_id, district_id, dy_id });
	}

	public void updateCommentStatus(long user_id, FoundUserRelationship ship) {
		jdbcTemplate.update("update t_dynamic_comment set status=? where user_id=?",
				new Object[] { ship.ordinal(), user_id });
	}

	public List<UserDynamic> loadFollow(long user_id, long last_id, int count) {
		String sql = "select d.*,"
				+ "coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=d.id and t_like.user_id=?), '0') as like_state ,"
				+ "u.user_id ,u.nick_name ,u.avatar,u.sex ,u.birthday ,u.type "
				+ "from t_user_follow f left join  t_user_dynamic d on f.target_id=d.user_id "
				+ "left join t_user u on  d.user_id=u.user_id "
				+ "where f.uid=? and  d.id<? and f.target_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) "
				+ "  order by d.id desc limit ?";
		Object[] param = new Object[] { user_id, user_id, last_id, user_id, Relationship.BLACK.ordinal(), count };
		return jdbcTemplate.query(sql, param, new DynamicMapper());
	}

	public List<UserDynamic> getDyanmicByState(int pageIndex, int pageSize, DynamicState state) {
		String sql = "select dynamic.*,(select count(*) from t_dynamic_comment where dynamic_id=dynamic.id) as commentCount ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ TABLE_USER_DYNAMIC
				+ " dynamic  left join t_user user on  dynamic.user_id=user.user_id  where dynamic.state=?   order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { state.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				new DynamicMapper());

	}

	public List<UserDynamic> getIllegalDyanmic() {
		String sql = "select  id,local_image_name from " + TABLE_USER_DYNAMIC
				+ "   where state=?  and local_image_name<>'illegal.jpg'";
		return jdbcTemplate.query(sql, new Object[] { DynamicState.T_ILLEGAL.ordinal() },
				new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
	}

	public int updateDynamicImgToIllegal(long id) {
		String sql = "update    " + TABLE_USER_DYNAMIC + "  set local_image_name=?  where state=?  and id=?";
		return jdbcTemplate.update(sql, new Object[] { "illegal.jpg", DynamicState.T_ILLEGAL.ordinal(), id });
	}

	public int getPageCountByState(int state) {
		String sql = "select  count(*) from " + TABLE_USER_DYNAMIC + " where state=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { state }, Integer.class);
	}

	public List<DynamicComment> loadSubComm(long pid, long did, int count, long last_id) {
		String sql = "select c.*,u.nick_name,u.avatar,u.sex,v.vip_id " + "from t_dynamic_comment c  "
				+ "left join t_user u on c.user_id=u.user_id  left join t_user_vip v on c.user_id=v.user_id   where c.status<>? and  c.dynamic_id=? and c.pid=? and c.id>? order by c.id limit ?";

		return jdbcTemplate.query(sql, new Object[] { FoundUserRelationship.GONE.ordinal(), did, pid, last_id, count },
				new BeanPropertyRowMapper<DynamicComment>(DynamicComment.class) {
					@Override
					public DynamicComment mapRow(ResultSet rs, int rowNumber) throws SQLException {
						DynamicComment dc = super.mapRow(rs, rowNumber);

						BaseVipUser user = new BaseVipUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						user.setSex(rs.getString("sex"));
						Object vipObj = rs.getObject("vip_id");
						if (vipObj != null && !"null".equals(vipObj.toString())) {
							user.setVip(true);
						}
						dc.setUser(user);
						return dc;
					}
				});

	}

	class DynamicCommentMapper implements RowMapper<DynamicComment> {

		public DynamicComment mapRow(ResultSet rs, int rowNum) throws SQLException {
			DynamicComment comment = new DynamicComment();
			comment.setId(rs.getLong("id"));
			comment.setDynamic_id(rs.getLong("dynamic_id"));
			comment.setContent(rs.getString("content"));
			comment.setComment_time(rs.getTimestamp("comment_time"));

			BaseVipUser user = new BaseVipUser();
			user.setUser_id(rs.getLong("user_id"));
			user.setNick_name(rs.getString("nick_name"));
			user.setAvatar(rs.getString("avatar"));
			user.setSex(rs.getString("sex"));
			Object vipObj = rs.getObject("vip_id");
			if (vipObj != null && !"null".equals(vipObj.toString())) {
				user.setVip(true);
			}
			comment.setUser(user);
			comment.setSub_comm(loadSubComm(comment.getId(), comment.getDynamic_id(), 10, 0));
			return comment;
		}

	}

	public int getDynamicCount(long id) {
		String sql = "select count(*) from " + TABLE_USER_DYNAMIC + " where id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
	}

	public void sendFlover(long user_id, long dynamic_id, int gif_id) {
		String sql = "insert into t_send_flover (uid,dy_id,create_time,gift_id) values (?, ?,?,?)";
		jdbcTemplate.update(sql, new Object[] { user_id, dynamic_id, new Date(), gif_id });
	}

}
