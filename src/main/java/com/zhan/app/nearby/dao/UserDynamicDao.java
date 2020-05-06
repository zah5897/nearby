package com.zhan.app.nearby.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.City;
import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.SimpleDynamic;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.UserDynamicRelationShip;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.bean.user.BaseUser;
import com.zhan.app.nearby.comm.DynamicCommentStatus;
import com.zhan.app.nearby.comm.DynamicState;
import com.zhan.app.nearby.comm.LikeDynamicState;
import com.zhan.app.nearby.comm.Relationship;
import com.zhan.app.nearby.comm.UserFnStatus;
import com.zhan.app.nearby.dao.base.BaseDao;

@Repository("userDynamicDao")
public class UserDynamicDao extends BaseDao<UserDynamic> {
	public static final String TABLE_LIKE_DYNAMIC_STATE = "t_like_dynamic";
	@Resource
	private JdbcTemplate jdbcTemplate;
	private static DynamicMapper dynamicMapper = new DynamicMapper();


	public List<UserDynamic> getHomeFoundSelected(long user_id, Long last_id, int page_size, City city) {
		
		String likeStateSql="coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=dy.id and t_like.user_id=?), '0') as like_state";
		
		if(last_id==null) {
			String sql = "select dy.*,"+likeStateSql+" ,u.user_id , u.nick_name ,u.avatar,u.sex ,u.birthday,u.video_cert_status ,u.type, u.isvip  from "
					+ getTableName() + " dy   left join t_user u on  dy.user_id=u.user_id "
					+ " where (dy.user_id=? or dy.found_status=?)  " + cityIn(city)
					+ " and dy.user_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) order by dy.id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] {user_id,user_id,UserFnStatus.ENABLE.ordinal(),user_id,Relationship.BLACK.ordinal(),page_size}, dynamicMapper);
		}
		String sql = "select dy.*,"+likeStateSql+" ,u.user_id , u.nick_name ,u.avatar,u.sex ,u.birthday,u.video_cert_status ,u.type, u.isvip  from "
				+ getTableName() + " dy   left join t_user u on  dy.user_id=u.user_id "
				+ " where (dy.user_id=? or dy.found_status=?)  " + cityIn(city)
				+ "  and dy.id<? and  dy.user_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) order by dy.id desc limit ?";
		return jdbcTemplate.query(sql, new Object[] {user_id,user_id,UserFnStatus.ENABLE.ordinal(),last_id,user_id,Relationship.BLACK.ordinal(),page_size}, dynamicMapper);
	}

	public void addFlowerCount(long dy_id, int count) {
		String sql = "update " + getTableName() + " set flower_count=flower_count+? where id=?";
		jdbcTemplate.update(sql, new Object[] { count, dy_id });
	}

	public void updateCommentCount(long dy_id) {
		int commentCount = jdbcTemplate
				.queryForObject("select count(*) from t_dynamic_comment where dynamic_id=" + dy_id, Integer.class);
		String sql = "update " + getTableName() + " set comment_count=" + commentCount + " where id=" + dy_id;
		jdbcTemplate.update(sql);
	}

	public int praiseDynamic(long dynamic_id, boolean praise) {

		if (praise) {
			return jdbcTemplate
					.update("update " + getTableName() + " set praise_count=praise_count+1 where id=" + dynamic_id);
		} else {
			return jdbcTemplate.update("update " + getTableName()
					+ " set praise_count=praise_count-1 where praise_count>0 and  id=" + dynamic_id);
		}
	}

	public List<UserDynamic> getHomeFoundSelectedRandom(long user_id, int size) {
		String sql = "select dynamic.*,"
				+ "coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=dynamic.id and t_like.user_id=?), '0') as like_state ,"
				+ "user.user_id  ," + "user.nick_name ," + "user.avatar," + "user.sex ," + "user.birthday ,"
				+ "user.type , u.isvip   from " + getTableName() + " dynamic  left join t_user user on  dynamic.user_id=user.user_id  "
				+ "where  dynamic.found_status=? and dynamic.user_id<>? and dynamic.user_id not in(select with_user_id from t_user_relationship where user_id=? and relationship=?)   order by RAND() limit ?";

		Object[] param = new Object[] { user_id, UserFnStatus.ENABLE.ordinal(), user_id, user_id,
				Relationship.BLACK.ordinal(), size };
		return jdbcTemplate.query(sql, param, dynamicMapper);
	}

	public List<UserDynamic> getSelectedDynamicByTopic(long topic_id, long last_id, int page_size) {
		String sql;
		sql = "select dynamic.* ,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type,u.isvip  from "
				+ getTableName() + " dynamic   left join t_user user on  dynamic.user_id=user.user_id where dynamic.found_status=? and dynamic.id<? and dynamic.topic_id=? order by dynamic.id desc limit ?";
		return jdbcTemplate.query(sql,
				new Object[] { UserFnStatus.ENABLE.ordinal(), last_id <= 0 ? Long.MAX_VALUE : last_id, topic_id, page_size },
				dynamicMapper);
	}

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

		String sql = "update from " + getTableName() + " set found_status=? where  id=?";
		return jdbcTemplate.update(sql, new Object[] {UserFnStatus.ENABLE.ordinal(), dynamic_id    });
	}

	public List<UserDynamic> getUserDynamic(long user_id, int page, int count,boolean canLoadVideoData) {
		
		if(canLoadVideoData) {
			String sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state,u.nick_name,u.avatar,u.sex,u.type,u.birthday,u.isvip  from "
					+ getTableName()
					+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id "
					+ " left join t_user u on dy.user_id=u.user_id  where dy.state=? and dy.user_id=?  order by dy.id desc limit ?,?";
			return jdbcTemplate.query(sql,
					new Object[] { DynamicState.CHECKED.ordinal(), user_id, (page - 1) * count, count }, dynamicMapper);
		}
		
		String sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state,u.nick_name,u.avatar,u.sex,u.type,u.birthday,u.isvip  from "
				+ getTableName()
				+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id "
				+ " left join t_user u on dy.user_id=u.user_id  where  dy.type=0  and dy.state=? and dy.user_id=?  order by dy.id desc limit ?,?";
		return jdbcTemplate.query(sql,
				new Object[] { DynamicState.CHECKED.ordinal(), user_id, (page - 1) * count, count }, dynamicMapper);
	}

	// 获取自身的动态
	public List<UserDynamic> getMyDynamic(long user_id, int page, int count,boolean canLoadVideoData) {
		
		if(canLoadVideoData) {
			String sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state,u.nick_name,u.avatar,u.sex,u.type,u.birthday,u.isvip  from "
					+ getTableName()
					+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id"
					+ "  left join t_user u on dy.user_id=u.user_id  where dy.state<>? and  dy.user_id=? order by dy.id desc limit ?,?";

			return jdbcTemplate.query(sql,
					new Object[] { DynamicState.ILLEGAL.ordinal(), user_id, (page - 1) * count, count }, dynamicMapper);
		} 
		
			String sql = "select dy.*,coalesce(t_like.relationship, '0') as like_state,u.nick_name,u.avatar,u.sex,u.type,u.birthday,u.isvip  from "
					+ getTableName()
					+ " dy left join t_like_dynamic t_like on dy.id=t_like.dynamic_id and dy.user_id=t_like.user_id"
					+ "  left join t_user u on dy.user_id=u.user_id  where dy.type=0 and dy.state<>? and  dy.user_id=? order by dy.id desc limit ?,?";

			return jdbcTemplate.query(sql,
					new Object[] { DynamicState.ILLEGAL.ordinal(), user_id, (page - 1) * count, count }, dynamicMapper);
		
	}

	public List<UserDynamic> getAllDynamic() {
		String sql = "select * from " + getTableName();
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
	}

	public DynamicComment loadComment(long dynamic_id, long comment_id) {

		String atSql = "select cc.id as at_commtent_id,cc.content as at_content, cc.comment_time as at_comment_time,cc.user_id as at_u_id ,au.nick_name as at_nick_name ,au.isvip ,au.avatar as at_avatar,au.sex as at_sex ,cc.comment_time as at_create_time  "
				+ "from t_dynamic_comment cc left join t_user au on cc.user_id=au.user_id ";
		String sql = "select c.*,u.nick_name,u.avatar,u.sex,u.isvip, "
				+ "at_c.* from t_dynamic_comment c  left join t_user u on c.user_id=u.user_id " + "left join ( " + atSql
				+ " ) as at_c on c.at_comment_id=at_c.at_commtent_id    where c.dynamic_id=? and c.id=?";
		List<DynamicComment> comments = jdbcTemplate.query(sql, new Object[] { dynamic_id, comment_id },
				new DynamicCommentMapper());
		return comments.get(0);
	}

	public List<DynamicComment> commentList(long user_id,long dynamic_id, int count, long last_comment_id) {
		String sql = "select c.*,u.nick_name,u.avatar,u.sex,u.isvip  from t_dynamic_comment c "
				+ "left join t_user u on c.user_id=u.user_id  "
				+ " where (c.user_id=? or c.status=?) and  c.dynamic_id=? and c.id<? and  c.pid=0 order by c.id desc limit ?";
		return jdbcTemplate
				.query(sql,
						new Object[] {user_id, DynamicCommentStatus.CHECKED.ordinal(), dynamic_id,
								last_comment_id <= 0 ? Long.MAX_VALUE : last_comment_id, count },
						new DynamicCommentMapper());

	}

	public UserDynamic detail(long dynamic_id) {
			String sql = "select dy.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type ,user.isvip   from "
					+ getTableName() + " dy left join t_user user on dy.user_id=user.user_id " + " where dy.id=?";
			List<UserDynamic> dys= jdbcTemplate.query(sql, new Object[] { dynamic_id }, dynamicMapper);
			if(dys.isEmpty()) {
				return null;
			}
			return dys.get(0);
	}

	public UserDynamic basic(long dynamic_id) {
			String sql = "select  *   from " + getTableName() + "   where id=?";
			List<UserDynamic> dys= jdbcTemplate.query(sql, new Object[] { dynamic_id },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
			if(dys.isEmpty()) {
				return null;
			}
			return dys.get(0);
	}

	public void updateAddress(UserDynamic dynamic) {
		jdbcTemplate.update("update " + getTableName()
				+ " set addr=? ,street=?,city=?,region=? ,province_id=?,city_id=?,district_id=?, ip=? where id=?",
				new Object[] { dynamic.getAddr(), dynamic.getStreet(), dynamic.getCity(), dynamic.getRegion(),
						dynamic.getProvince_id(), dynamic.getCity_id(), dynamic.getDistrict_id(), dynamic.getIp(),
						dynamic.getId() });
	}

	public void updateBrowserCount(long dynamic_id, int browser_count) {
		jdbcTemplate.update("update " + getTableName() + " set browser_count=? where id=?",
				new Object[] { browser_count, dynamic_id });
	}

	public long getUserIdByDynamicId(long dynamic_id) {
			String sql = "select user_id  from " + getTableName() + " where id=?";
			List<Long> ids= jdbcTemplate.queryForList(sql, new Object[] { dynamic_id }, Long.class);
			return ids.get(0);
	}

	public long updateLikeState(UserDynamicRelationShip dynamicRelationShip) {
		String sql = "select count(*) from " + TABLE_LIKE_DYNAMIC_STATE + " where user_id=? and dynamic_id=?";
		int count = jdbcTemplate.queryForObject(sql,
				new Object[] { dynamicRelationShip.getUser_id(), dynamicRelationShip.getDynamic_id() }, Integer.class);
		if (count == 0) {
			insertObject(dynamicRelationShip);
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
			List<Integer> i= jdbcTemplate.queryForList(sql, new Object[] { user_id, dynamic_id }, Integer.class);
			if(!i.isEmpty()) {
				return i.get(0);
			}
		return LikeDynamicState.UNLIKE.ordinal();
	}

	public int delete(Long user_id, long dynamic_id) {
		// 删除本体
		String sql = "delete from " + getTableName() + " where user_id=? and id=?";
		jdbcTemplate.update(sql, new Object[] { user_id, dynamic_id });
		// 删除喜欢记录
		sql = "delete from t_like_dynamic where user_id=? and dynamic_id=?";
		jdbcTemplate.update(sql, new Object[] { user_id, dynamic_id });

		return 0;
	}

	public int getMostCityID() {
			String sql = "select c.city_id from (select count(*) as count, gb.* from t_user_dynamic gb group by gb.city_id) as c order by c.count desc limit 1";
			List<Integer> is= jdbcTemplate.queryForList(sql, Integer.class);
			return is.isEmpty()?0:is.get(0);
	}

	// 获取用户发布的动态里面的图片
	@SuppressWarnings("unchecked")
	public List<SimpleDynamic> getUserImages(long user_id, long last_image_id, int count,boolean canLoadVideoData) {
		if(canLoadVideoData) {
			return jdbcTemplate.query(
					"select * where  state=? and  user_id=? and local_image_name<>? and id<? order by id desc limit ?",
					new Object[] { DynamicState.CHECKED.ordinal(), user_id, "", last_image_id, count },
					getEntityMapper(SimpleDynamic.class));
		}
		return jdbcTemplate.query(
				"select * where type=0 and  state=? and  user_id=? and local_image_name<>? and id<? order by id desc limit ?",
				new Object[] { DynamicState.CHECKED.ordinal(), user_id, "", last_image_id, count },
				getEntityMapper(SimpleDynamic.class));
	}

	public int updateCityId(long dy_id, int province_id, int city_id, int district_id) {
		return jdbcTemplate.update("update t_user_dynamic  set province_id=?,city_id=?,district_id=? where id=?",
				new Object[] { province_id, city_id, district_id, dy_id });
	}

	public void updateCommentStatus(long user_id, DynamicCommentStatus ship) {
		jdbcTemplate.update("update t_dynamic_comment set status=? where user_id=?",
				new Object[] { ship.ordinal(), user_id });
	}

	public List<UserDynamic> loadFollow(long user_id, long last_id, int count) {
		
		String sql = "select d.*,"
				+ "coalesce((select relationship from t_like_dynamic t_like where t_like.dynamic_id=d.id and t_like.user_id=?), '0') as like_state ,"
				+ "u.user_id ,u.nick_name ,u.avatar,u.sex,u.video_cert_status ,u.birthday ,u.type ,u.isvip  "
				+ "from t_user_follow f left join  t_user_dynamic d on f.target_id=d.user_id "
				+ "left join t_user u on  d.user_id=u.user_id "
				+ "where  d.state=? and  f.uid=? and  d.id<? and f.target_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) "
				+ "  order by d.id desc limit ?";
		Object[] param = new Object[] { user_id, DynamicState.CHECKED.ordinal(), user_id, last_id, user_id,
				Relationship.BLACK.ordinal(), count };
		return jdbcTemplate.query(sql, param, dynamicMapper);
	}

	public List<UserDynamic> getDyanmicByState(int pageIndex, int pageSize, DynamicState state) {
		String sql = "select dynamic.*,(select count(*) from t_dynamic_comment where dynamic_id=dynamic.id) as commentCount ,user.user_id ,user.isvip ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type from "
				+ getTableName()
				+ " dynamic  left join t_user user on  dynamic.user_id=user.user_id  where dynamic.state=?   order by dynamic.id desc limit ?,?";
		return jdbcTemplate.query(sql, new Object[] { state.ordinal(), (pageIndex - 1) * pageSize, pageSize },
				dynamicMapper);

	}

	// 修改动态的状态
	public int updateDynamicState(long id, DynamicState state) {
		String sql = "update  t_user_dynamic set state=? where id=? ";
		return jdbcTemplate.update(sql, new Object[] { state.ordinal(), id });
	}

	public List<UserDynamic> getIllegalDyanmic() {
		String sql = "select  id,local_image_name from " + getTableName()
				+ "   where state=?  and local_image_name<>'illegal.jpg'";
		return jdbcTemplate.query(sql, new Object[] { DynamicState.ILLEGAL.ordinal() },
				new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
	}

	public int updateDynamicImgToIllegal(long id) {
		String sql = "update    " + getTableName() + "  set local_image_name=?  where state=?  and id=?";
		return jdbcTemplate.update(sql, new Object[] { "illegal.jpg", DynamicState.ILLEGAL.ordinal(), id });
	}


	public List<DynamicComment> loadSubComm(long pid, long did, int count, long last_id) {
		String sql = "select c.*,u.nick_name,u.avatar,u.sex,u.isvip " + "from t_dynamic_comment c  "
				+ "left join t_user u on c.user_id=u.user_id     where c.status=? and  c.dynamic_id=? and c.pid=? and c.id>? order by c.id limit ?";

		return jdbcTemplate.query(sql, new Object[] { DynamicCommentStatus.CHECKED.ordinal(), did, pid, last_id, count },
				new BeanPropertyRowMapper<DynamicComment>(DynamicComment.class) {
					@Override
					public DynamicComment mapRow(ResultSet rs, int rowNumber) throws SQLException {
						DynamicComment dc = super.mapRow(rs, rowNumber);
						BaseUser user = new BaseUser();
						user.setUser_id(rs.getLong("user_id"));
						user.setNick_name(rs.getString("nick_name"));
						user.setAvatar(rs.getString("avatar"));
						user.setSex(rs.getString("sex"));
						int isVip = rs.getInt("isvip");
						user.setIsvip(isVip);
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

			BaseUser user = new BaseUser();
			user.setUser_id(rs.getLong("user_id"));
			user.setNick_name(rs.getString("nick_name"));
			user.setAvatar(rs.getString("avatar"));
			user.setSex(rs.getString("sex"));
			int vipObj = rs.getInt("isvip");
			user.setIsvip(vipObj);
			comment.setUser(user);
			comment.setSub_comm(loadSubComm(comment.getId(), comment.getDynamic_id(), 10, 0));
			return comment;
		}

	}

	public int getDynamicCount(long id) {
		String sql = "select count(*) from " + getTableName() + " where id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
	}

	public void sendFlower(long user_id, long dynamic_id, int gif_id, int count) {
		String sql = "insert into t_send_flower (uid,dy_id,create_time,gift_id,count) values (?, ?,?,?,?)";
		jdbcTemplate.update(sql, new Object[] { user_id, dynamic_id, new Date(), gif_id, count });
	}

	public int getDynamicCommentCount(Long user_id) {
		if(user_id==null) {
			return jdbcTemplate.queryForObject("select count(*) from "+getTableName(DynamicComment.class) +" where status<>?",new Object[] {DynamicCommentStatus.ILLEGAL.ordinal()}, Integer.class);
		}else {
			return jdbcTemplate.queryForObject("select count(*) from "+getTableName(DynamicComment.class) +" where  user_id=? ",new Object[] {user_id}, Integer.class);
		}
	}

	public List<DynamicComment> loadDynamicCommentToCheck(Long user_id,int page,int count) {
		
		if(user_id==null) {
			String sql = "select c.*,u.nick_name,u.avatar,u.sex,u.isvip  from t_dynamic_comment c "
					+ "left join t_user u on c.user_id=u.user_id  "
					+ " where c.status<>?  order by c.id desc limit ?,?";
			return jdbcTemplate
					.query(sql,
							new Object[] { DynamicCommentStatus.ILLEGAL.ordinal(),(page-1)*count, count},
							new DynamicCommentMapper());
		}else {
			String sql = "select c.*,u.nick_name,u.avatar,u.sex,u.isvip  from t_dynamic_comment c "
					+ "left join t_user u on c.user_id=u.user_id  "
					+ " where  c.user_id=?  order by c.id desc limit ?,?";
			return jdbcTemplate
					.query(sql,
							new Object[] {user_id,(page-1)*count, count},
							new DynamicCommentMapper());
		}
		
		

	}

	public void changeCommentStatus(int id, int status) {
		 jdbcTemplate.update("update "+getTableName(DynamicComment.class)+" set status=? where id=?",status,id);
	}
}
