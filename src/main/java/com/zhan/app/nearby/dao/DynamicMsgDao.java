package com.zhan.app.nearby.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.zhan.app.nearby.bean.DynamicMessage;
import com.zhan.app.nearby.bean.mapper.DynamicMsgMapper;

@Repository("dynamicMsgDao")
public class DynamicMsgDao extends BaseDao {
	public static final String TABLE_DYNAMIC_MSG = "t_dynamic_msg";
	@Resource
	private JdbcTemplate jdbcTemplate;
	
	public long insert(DynamicMessage msg){
		return saveObj(jdbcTemplate, TABLE_DYNAMIC_MSG, msg);
	}

	public List<DynamicMessage> loadMsg(Long user_id) {
		try{
		   String sql = "select msg.*,user.user_id  ,user.nick_name ,user.avatar,user.sex ,user.birthday  from " + TABLE_DYNAMIC_MSG + " msg left join t_user user on msg.user_id=user.user_id where msg.user_id=?";
		  return jdbcTemplate.query(sql, new Object[]{user_id}, new DynamicMsgMapper());
		}catch(Exception e){
		   return null;
		}
	}

}