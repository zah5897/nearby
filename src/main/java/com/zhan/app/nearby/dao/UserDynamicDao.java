package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.DynamicComment;
import com.zhan.app.nearby.bean.UserDynamic;
import com.zhan.app.nearby.bean.mapper.DynamicCommentMapper;
import com.zhan.app.nearby.bean.mapper.DynamicMapper;
import com.zhan.app.nearby.comm.ImageStatus;

@Repository("userDynamicDao")
public class UserDynamicDao extends BaseDao {
	public static final String TABLE_USER_DYNAMIC = "t_user_dynamic";
	public static final String TABLE_HOME_FOUND_SELECTED = "t_home_found_selected";
	public static final String TABLE_DYNAMIC_COMMENT = "t_dynamic_comment";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public long insertDynamic(UserDynamic dyanmic) {
		return saveObj(jdbcTemplate, TABLE_USER_DYNAMIC, dyanmic);
	}

	public List<UserDynamic> getHomeFoundSelected(ImageStatus status, long last_id, int page_size) {
			String sql = "select *from " + TABLE_USER_DYNAMIC + " dyanmic left join " + TABLE_HOME_FOUND_SELECTED
					+ " selected on dyanmic.id=selected.dynamic_id where selected.selected_state=? and selected.dynamic_id>? order by selected.dynamic_id desc limit ?";

			return jdbcTemplate.query(sql, new Object[] { status.ordinal(), last_id, page_size },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
	}

	public void addHomeFoundSelected(long dynamic_id) {
		String sql = "insert into " + TABLE_HOME_FOUND_SELECTED + " values (?, ?)";
		jdbcTemplate.update(sql, new Object[] { dynamic_id, ImageStatus.SELECTED.ordinal() });
	}

	public List<Integer> getPraiseCount(long dynamic_id) {
		String sql = "select praise_count from " + TABLE_USER_DYNAMIC + " where id=?";
		return jdbcTemplate.queryForList(sql, new Object[] { dynamic_id }, Integer.class);
	}

	public int praiseDynamic(long dynamic_id) {
		List<Integer> result = getPraiseCount(dynamic_id);

		if (result == null || result.size() == 0) {
			return -1;
		}
		Integer hasPraiseCount = result.get(0);

		if (hasPraiseCount < 0) {
			hasPraiseCount = 0;
		}
		jdbcTemplate.update("update " + TABLE_USER_DYNAMIC + " set praise_count=? where id=?",
				new Object[] { hasPraiseCount += 1, dynamic_id });
		return hasPraiseCount;
	}

	public List<UserDynamic> getUserDynamic(long user_id, long last_id, int count) {
		if (last_id < 1) {
			String sql = "select * from " + TABLE_USER_DYNAMIC + " where user_id=? order by id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, count },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		} else {
			String sql = "select * from " + TABLE_USER_DYNAMIC + " where user_id=? and id<? order by id desc limit ?";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, count },
					new BeanPropertyRowMapper<UserDynamic>(UserDynamic.class));
		}
	}
	
	public long comment(DynamicComment comment){
		return saveObj(jdbcTemplate, TABLE_DYNAMIC_COMMENT, comment);
	}
	public DynamicComment loadComment(long dynamic_id,long  comment_id) {
		
		String sql="select comment.*,user.user_id  ,user.nick_name ,user.avatar,user.sex from "+TABLE_DYNAMIC_COMMENT+" comment left join t_user user on comment.user_id=user.user_id where comment.dynamic_id=? and comment.id=?";
		
		return jdbcTemplate.queryForObject(sql, new Object[] { dynamic_id,comment_id },
				new DynamicCommentMapper());
	}
	public List<DynamicComment> commentList(long dynamic_id,int count,long last_comment_id) {
		
		String sql="select comment.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday from "+TABLE_DYNAMIC_COMMENT+" comment left join t_user user on comment.user_id=user.user_id and comment.dynamic_id=? and comment.id>? order by comment.id desc limit ?";
		
		return jdbcTemplate.query(sql, new Object[] { dynamic_id,last_comment_id, count },
				new DynamicCommentMapper());
	}

	public UserDynamic detail(long dynamic_id) {
		try{
		String sql = "select dy.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday  from " + TABLE_USER_DYNAMIC + " dy left join t_user user on dy.user_id=user.user_id where dy.id=?";
		return jdbcTemplate.queryForObject(sql, new Object[] {dynamic_id },new DynamicMapper());
		}catch(Exception e){
			return null;
		}
		}
	
	
	
}
