package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.mapper.DynamicMsgMapper;
import com.zhan.app.nearby.comm.DynamicMsgType;
import com.zhan.app.nearby.comm.MsgState;
import com.zhan.app.nearby.comm.Relationship;

@Repository("dynamicMsgDao")
public class DynamicMsgDao extends BaseDao {
	public static final String TABLE_DYNAMIC_MSG = "t_dynamic_msg";
	@Resource
	private JdbcTemplate jdbcTemplate;

	public long insert(DynamicMessage msg) {
		return saveObj(jdbcTemplate, TABLE_DYNAMIC_MSG, msg);
	}

	public List<DynamicMessage> loadMsg(Long user_id, long last_id, int type) {
		if(type==0){
			String sql = "select msg.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type  from "
					+ TABLE_DYNAMIC_MSG
					+ " msg left join t_user user on msg.by_user_id=user.user_id where msg.user_id=? and msg.id>? and msg.type<? "
					+ fiflterBlock() + " order by msg.id desc";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, 2,user_id, Relationship.BLACK.ordinal() },
					new DynamicMsgMapper());
		}else{
			String sql = "select msg.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday,user.type  from "
					+ TABLE_DYNAMIC_MSG
					+ " msg left join t_user user on msg.by_user_id=user.user_id where msg.user_id=? and msg.id>? and msg.type>? order by msg.id desc";
			return jdbcTemplate.query(sql, new Object[] { user_id, last_id, 1},
					new DynamicMsgMapper());
		}
		

	}

	public int delete(long msg_id) {
		String sql = "delete from " + TABLE_DYNAMIC_MSG + " where id=?";
		return jdbcTemplate.update(sql, new Object[] { msg_id });
	}

	public int updateState(long id) {
		String sql = "update " + TABLE_DYNAMIC_MSG + " set isReadNum=? where id=?";
		return jdbcTemplate.update(sql, new Object[] { MsgState.READED.ordinal(), id });
	}

	private String fiflterBlock() {
		return " and msg.by_user_id not in (select with_user_id from t_user_relationship where user_id=? and relationship=?) ";
	}
}
